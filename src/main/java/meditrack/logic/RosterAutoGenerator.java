package meditrack.logic;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import meditrack.model.DutySlot;
import meditrack.model.DutyType;

/**
 * Generates a duty roster automatically from a list of personnel names.
 *
 * <p>Rules applied:
 * <ul>
 *   <li>Guard Duty and Patrol are scheduled across a 24-hour window (00:00–00:00).</li>
 *   <li>All other duty types are scheduled 08:00–20:00.</li>
 *   <li>Slot duration is derived from the coverage window divided by the number of
 *       personnel, rounded to the nearest 30 minutes, and clamped to [30, 240] min.</li>
 *   <li>One person may hold multiple slots (different duty types) provided they do not
 *       overlap and the person retains at least one 8-hour contiguous break in the day.</li>
 * </ul>
 */
public class RosterAutoGenerator {

    private static final int DAY_MINUTES = 1440;
    private static final int MIN_BREAK_MINUTES = 480; // 8 hours
    private static final int MIN_SLOT_MINUTES = 30;
    private static final int MAX_SLOT_MINUTES = 240; // 4 hours

    /** Duty types that require round-the-clock (24 h) coverage. */
    private static final Set<DutyType> ROUND_CLOCK =
            EnumSet.of(DutyType.GUARD_DUTY, DutyType.PATROL);

    private RosterAutoGenerator() {}

    /**
     * Generates duty slots for the given personnel names and duty types.
     *
     * @param personnelNames  names of all FIT personnel available for assignment
     * @param selectedTypes   duty types to schedule
     * @return list of generated {@link DutySlot}s, sorted by start time then duty type
     */
    public static List<DutySlot> generate(List<String> personnelNames,
                                          List<DutyType> selectedTypes) {
        if (personnelNames.isEmpty() || selectedTypes.isEmpty()) {
            return List.of();
        }

        List<String> shuffled = new ArrayList<>(personnelNames);
        Collections.shuffle(shuffled);

        // Track each person's intervals as [startMin, endMin] pairs
        Map<String, List<int[]>> personIntervals = new HashMap<>();
        for (String name : shuffled) {
            personIntervals.put(name, new ArrayList<>());
        }

        List<DutySlot> result = new ArrayList<>();

        for (DutyType type : selectedTypes) {
            int windowStart = ROUND_CLOCK.contains(type) ? 0 : 480;        // 00:00 or 08:00
            int windowEnd   = ROUND_CLOCK.contains(type) ? DAY_MINUTES : 1200; // 00:00 or 20:00
            int windowMinutes = windowEnd - windowStart;

            int rawSlot = windowMinutes / shuffled.size();
            int slotMinutes = roundToHalfHour(rawSlot);
            slotMinutes = Math.max(MIN_SLOT_MINUTES, Math.min(MAX_SLOT_MINUTES, slotMinutes));

            int current = windowStart;
            int personIdx = 0;

            while (current < windowEnd) {
                int slotEnd = Math.min(current + slotMinutes, windowEnd);

                // Find available person starting from personIdx, cycling through all
                String assigned = null;
                for (int i = 0; i < shuffled.size(); i++) {
                    int idx = (personIdx + i) % shuffled.size();
                    String name = shuffled.get(idx);
                    List<int[]> intervals = personIntervals.get(name);
                    if (hasNoOverlap(intervals, current, slotEnd)
                            && hasEightHourBreak(intervals, current, slotEnd)) {
                        assigned = name;
                        personIdx = (idx + 1) % shuffled.size();
                        break;
                    }
                }

                if (assigned != null) {
                    result.add(new DutySlot(
                            minutesToTime(current),
                            minutesToTime(slotEnd),
                            type,
                            assigned));
                    personIntervals.get(assigned).add(new int[]{current, slotEnd});
                }

                current = slotEnd;
            }
        }

        // Sort by start time
        result.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        return result;
    }

    // ── Constraint helpers ────────────────────────────────────────────────────

    /**
     * Returns true if [newStart, newEnd) does not overlap any existing interval.
     */
    private static boolean hasNoOverlap(List<int[]> intervals, int newStart, int newEnd) {
        for (int[] iv : intervals) {
            if (newStart < iv[1] && newEnd > iv[0]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if, after adding [newStart, newEnd), the person still has at
     * least one 8-hour contiguous free block within the 24-hour day.
     */
    private static boolean hasEightHourBreak(List<int[]> existing, int newStart, int newEnd) {
        List<int[]> all = new ArrayList<>(existing);
        all.add(new int[]{newStart, newEnd});
        all.sort((a, b) -> Integer.compare(a[0], b[0]));

        // Gap before the first duty
        if (all.get(0)[0] >= MIN_BREAK_MINUTES) {
            return true;
        }
        // Gaps between consecutive duties
        for (int i = 0; i < all.size() - 1; i++) {
            int gap = all.get(i + 1)[0] - all.get(i)[1];
            if (gap >= MIN_BREAK_MINUTES) {
                return true;
            }
        }
        // Gap after the last duty
        int afterLast = DAY_MINUTES - all.get(all.size() - 1)[1];
        return afterLast >= MIN_BREAK_MINUTES;
    }

    // ── Time utilities ────────────────────────────────────────────────────────

    /** Rounds minutes to the nearest 30-minute boundary. */
    private static int roundToHalfHour(int minutes) {
        return ((minutes + 15) / 30) * 30;
    }

    /**
     * Converts minutes-from-midnight to a {@link LocalTime}.
     * 1440 (24:00) maps to {@code LocalTime.MIDNIGHT} (00:00).
     */
    private static LocalTime minutesToTime(int minutes) {
        int clamped = minutes % DAY_MINUTES;
        return LocalTime.of(clamped / 60, clamped % 60);
    }
}