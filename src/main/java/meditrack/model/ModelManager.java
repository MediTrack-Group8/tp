package meditrack.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;

import meditrack.commons.core.Index;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.exceptions.InvalidIndexException;

/** In-memory model: supplies + personnel + current session. */
public class ModelManager implements Model {

    private static final String MSG_DUPLICATE =
            "A personnel member named \"%s\" already exists.";
    private static final String MSG_OUT_OF_BOUNDS =
            "Index %d is out of bounds. The list currently has %d member(s).";
    private static final String MSG_NO_FIT =
            "Cannot generate roster: no personnel with status FIT currently exist.";

    private final MediTrack mediTrack;
    private final Session session;

    /**
     * Creates a manager backed by the given {@link MediTrack}.
     *
     * @param mediTrack data loaded from storage or empty for a new session
     */
    public ModelManager(MediTrack mediTrack) {
        this.mediTrack = mediTrack;
        this.session = Session.getInstance();
    }

    /** Creates a manager with an empty {@link MediTrack}. */
    public ModelManager() {
        this(new MediTrack());
    }

    /** Returns the current session. */
    @Override
    public Session getSession() {
        return session;
    }

    /** Sets the active role after login. */
    @Override
    public void setRole(Role role) {
        session.setRole(role);
    }

    /** Adds a supply; duplicate names are rejected in the backing list. */
    @Override
    public void addSupply(Supply supply) {
        mediTrack.addSupply(supply);
    }

    /** Replaces the supply at {@code targetIndex}. */
    @Override
    public void editSupply(Index targetIndex, Supply editedSupply) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        mediTrack.setSupply(zeroIndex, editedSupply);
    }

    /** Deletes and returns the supply at {@code targetIndex}. */
    @Override
    public Supply deleteSupply(Index targetIndex) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        return mediTrack.removeSupply(zeroIndex);
    }

    /** Returns all supplies as an observable list. */
    @Override
    public ObservableList<Supply> getFilteredSupplyList() {
        return mediTrack.getSupplyList();
    }

    /** Supplies expiring within {@code daysThreshold} days, sorted by expiry. */
    @Override
    public List<Supply> getExpiringSupplies(int daysThreshold) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(daysThreshold);
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> !s.getExpiryDate().isBefore(today) && !s.getExpiryDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Supply::getExpiryDate))
                .collect(Collectors.toList());
    }

    /** Supplies below {@code quantityThreshold} quantity, sorted ascending. */
    @Override
    public List<Supply> getLowStockSupplies(int quantityThreshold) {
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> s.getQuantity() < quantityThreshold)
                .sorted(Comparator.comparingInt(Supply::getQuantity))
                .collect(Collectors.toList());
    }

    /** Read-only view of data for saving to disk. */
    @Override
    public ReadOnlyMediTrack getMediTrack() {
        return mediTrack;
    }

    /** Observable personnel list for the UI. */
    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return mediTrack.getPersonnelList();
    }

    /** Adds a person to the roster. */
    @Override
    public void addPersonnel(String name, Status status) throws CommandException {
        Personnel candidate = new Personnel(name, status);
        for (Personnel existing : getInternalPersonnelList()) {
            if (existing.equals(candidate)) {
                throw new CommandException(String.format(MSG_DUPLICATE, name));
            }
        }
        getInternalPersonnelList().add(candidate);
    }

    /** Removes the person at the given 1-based index. */
    @Override
    public Personnel deletePersonnel(int oneBasedIndex) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        return list.remove(oneBasedIndex - 1);
    }

    /** Updates status for the person at the 1-based index. */
    @Override
    public void setPersonnelStatus(int oneBasedIndex, Status newStatus) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        list.get(oneBasedIndex - 1).setStatus(newStatus);
    }

    /** Personnel filtered by status; {@code null} means everyone. */
    @Override
    public List<Personnel> getFilteredPersonnelList(Status statusFilter) {
        if (statusFilter == null) {
            return Collections.unmodifiableList(new ArrayList<>(getInternalPersonnelList()));
        }
        return getInternalPersonnelList().stream()
                .filter(p -> p.getStatus() == statusFilter)
                .collect(Collectors.toUnmodifiableList());
    }

    /** Shuffled list of FIT personnel for duty roster. */
    @Override
    public List<Personnel> generateRoster() throws CommandException {
        List<Personnel> fit = new ArrayList<>(getFilteredPersonnelList(Status.FIT));
        if (fit.isEmpty()) {
            throw new CommandException(MSG_NO_FIT);
        }
        Collections.shuffle(fit);
        return fit;
    }

    /** Number of people in the roster. */
    @Override
    public int getPersonnelCount() {
        return getInternalPersonnelList().size();
    }

    private ObservableList<Personnel> getInternalPersonnelList() {
        return mediTrack.getPersonnelObservable();
    }

    // ── Duty slot management ──────────────────────────────────────────────────

    @Override
    public List<DutySlot> getDutySlots() {
        return mediTrack.getDutySlots();
    }

    @Override
    public void addDutySlot(DutySlot slot) {
        mediTrack.getDutySlotsInternal().add(slot);
    }

    @Override
    public void removeDutySlot(int zeroBasedIndex) throws CommandException {
        List<DutySlot> slots = mediTrack.getDutySlotsInternal();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= slots.size()) {
            throw new CommandException(
                    String.format("Index %d is out of bounds. The roster has %d slot(s).",
                            zeroBasedIndex, slots.size()));
        }
        slots.remove(zeroBasedIndex);
    }

    @Override
    public void clearDutySlots() {
        mediTrack.getDutySlotsInternal().clear();
    }

    @Override
    public void replaceDutySlot(int zeroBasedIndex, DutySlot newSlot) throws CommandException {
        List<DutySlot> slots = mediTrack.getDutySlotsInternal();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= slots.size()) {
            throw new CommandException(
                    String.format("Index %d is out of bounds. The roster has %d slot(s).",
                            zeroBasedIndex, slots.size()));
        }
        slots.set(zeroBasedIndex, newSlot);
    }
}
