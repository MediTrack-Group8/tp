package meditrack.model;

import java.time.Clock;
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

/**
 * Represents the in-memory model of the MediTrack data.
 * Handles operations related to personnel, supplies, duty slots, and system time.
 */
public class ModelManager implements Model {

    private static final String MSG_DUPLICATE =
            "A personnel member named \"%s\" already exists.";
    private static final String MSG_OUT_OF_BOUNDS =
            "Index %d is out of bounds. The list currently has %d member(s).";
    private static final String MSG_SLOT_OUT_OF_BOUNDS =
            "Slot index %d is out of bounds. The roster currently has %d slot(s).";

    private final MediTrack mediTrack;
    private final Session session;
    private Clock clock = Clock.systemDefaultZone();

    /**
     * Creates a ModelManager backed by the given MediTrack.
     *
     * @param mediTrack The MediTrack data instance to manage.
     */
    public ModelManager(MediTrack mediTrack) {
        this.mediTrack = mediTrack;
        this.session = Session.getInstance();
        cleanExpiredStatuses();
    }

    /** * Creates a ModelManager with an empty MediTrack instance.
     */
    public ModelManager() {
        this(new MediTrack());
    }

    /**
     * Gets the current system clock, used for time-based calculations.
     * * @return The current Clock instance.
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Injects a specific clock instance, useful for time-travel simulations and testing.
     * * @param clock The Clock instance to set.
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }

    /** * Retrieves the current user session.
     * * @return The current Session instance.
     */
    @Override
    public Session getSession() {
        return session;
    }

    /** * Sets the active operational role for the current session.
     * * @param role The role to assign to the session.
     */
    @Override
    public void setRole(Role role) {
        session.setRole(role);
    }

    /** * Adds a new supply to the inventory.
     * * @param supply The supply item to add.
     */
    @Override
    public void addSupply(Supply supply) {
        mediTrack.addSupply(supply);
    }

    /** * Replaces the supply at the specified index with a new supply object.
     * * @param targetIndex The index of the supply to edit.
     * @param editedSupply The updated supply object.
     * @throws InvalidIndexException If the index is out of bounds.
     */
    @Override
    public void editSupply(Index targetIndex, Supply editedSupply) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        mediTrack.setSupply(zeroIndex, editedSupply);
    }

    /** * Deletes and returns the supply at the specified index.
     * * @param targetIndex The index of the supply to delete.
     * @return The deleted supply item.
     * @throws InvalidIndexException If the index is out of bounds.
     */
    @Override
    public Supply deleteSupply(Index targetIndex) {
        int zeroIndex = targetIndex.getZeroBased();
        ObservableList<Supply> internalList = mediTrack.getInternalSupplyList();
        if (zeroIndex < 0 || zeroIndex >= internalList.size()) {
            throw new InvalidIndexException();
        }
        return mediTrack.removeSupply(zeroIndex);
    }

    /** * Retrieves an observable list of all filtered supplies.
     * * @return An ObservableList of Supply objects.
     */
    @Override
    public ObservableList<Supply> getFilteredSupplyList() {
        return mediTrack.getSupplyList();
    }

    /** * Retrieves supplies expiring within a given threshold of days, relative to the injected clock.
     * * @param daysThreshold The number of days from today to check for expiration.
     * @return A list of supplies sorted chronologically by expiry date.
     */
    @Override
    public List<Supply> getExpiringSupplies(int daysThreshold) {
        LocalDate today = LocalDate.now(clock);
        LocalDate cutoff = today.plusDays(daysThreshold);
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> !s.getExpiryDate().isBefore(today) && !s.getExpiryDate().isAfter(cutoff))
                .sorted(Comparator.comparing(Supply::getExpiryDate))
                .collect(Collectors.toList());
    }

    /** * Retrieves supplies where the quantity is below the specified threshold.
     * * @param quantityThreshold The threshold below which a supply is considered low stock.
     * @return A list of supplies sorted in ascending order by quantity.
     */
    @Override
    public List<Supply> getLowStockSupplies(int quantityThreshold) {
        return mediTrack.getInternalSupplyList().stream()
                .filter(s -> s.getQuantity() < quantityThreshold)
                .sorted(Comparator.comparingInt(Supply::getQuantity))
                .collect(Collectors.toList());
    }

    /** * Retrieves a read-only view of the MediTrack data for disk persistence.
     * * @return A ReadOnlyMediTrack instance.
     */
    @Override
    public ReadOnlyMediTrack getMediTrack() {
        return mediTrack;
    }

    /** * Retrieves an observable list of all personnel.
     * * @return An ObservableList of Personnel objects.
     */
    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return mediTrack.getPersonnelList();
    }

    /** * Adds a person to the roster with basic information.
     * * @param name The name of the personnel.
     * @param status The medical status of the personnel.
     * @throws CommandException If a person with the same name already exists.
     */
    @Override
    public void addPersonnel(String name, Status status) throws CommandException {
        addPersonnel(name, status, null, "");
    }

    /** * Adds a person to the roster with extended medical information.
     * * @param name The name of the personnel.
     * @param status The medical status of the personnel.
     * @param bloodGroup The blood group of the personnel.
     * @param allergies Known allergies of the personnel.
     * @throws CommandException If a person with the same name already exists.
     */
    @Override
    public void addPersonnel(String name, Status status, BloodGroup bloodGroup, String allergies)
            throws CommandException {
        Personnel candidate = new Personnel(name, status, bloodGroup, allergies);
        for (Personnel existing : getInternalPersonnelList()) {
            if (existing.equals(candidate)) {
                throw new CommandException(String.format(MSG_DUPLICATE, name));
            }
        }
        getInternalPersonnelList().add(candidate);
    }

    /** * Removes the personnel at the given 1-based index.
     * * @param oneBasedIndex The 1-based index of the personnel to remove.
     * @return The removed Personnel object.
     * @throws CommandException If the index is out of bounds.
     */
    @Override
    public Personnel deletePersonnel(int oneBasedIndex) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        return list.remove(oneBasedIndex - 1);
    }

    /** * Updates the status for the personnel at the specified 1-based index.
     * * @param oneBasedIndex The 1-based index of the personnel.
     * @param newStatus The new medical status to apply.
     * @throws CommandException If the index is out of bounds.
     */
    @Override
    public void setPersonnelStatus(int oneBasedIndex, Status newStatus) throws CommandException {
        List<Personnel> list = getInternalPersonnelList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        list.get(oneBasedIndex - 1).setStatus(newStatus);
    }

    /** * Retrieves personnel filtered by a specific status.
     * * @param statusFilter The status to filter by (null returns all personnel).
     * @return An unmodifiable list of matching personnel.
     */
    @Override
    public List<Personnel> getFilteredPersonnelList(Status statusFilter) {
        if (statusFilter == null) {
            return Collections.unmodifiableList(new ArrayList<>(getInternalPersonnelList()));
        }
        return getInternalPersonnelList().stream()
                .filter(p -> p.getStatus() == statusFilter)
                .collect(Collectors.toUnmodifiableList());
    }

    /** * Retrieves the total number of personnel in the roster.
     * * @return The integer count of personnel.
     */
    @Override
    public int getPersonnelCount() {
        return getInternalPersonnelList().size();
    }

    private ObservableList<Personnel> getInternalPersonnelList() {
        return mediTrack.getPersonnelObservable();
    }

    // --- Duty Slot Management ---

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
                    String.format(MSG_SLOT_OUT_OF_BOUNDS, zeroBasedIndex, slots.size()));
        }
        slots.remove(zeroBasedIndex);
    }

    @Override
    public void clearDutySlots() {
        mediTrack.getDutySlotsInternal().clear();
    }

    @Override
    public void clearDutySlotsForDate(LocalDate date) {
        mediTrack.getDutySlotsInternal().removeIf(slot -> slot.getDate().equals(date));
    }

    @Override
    public void replaceDutySlot(int zeroBasedIndex, DutySlot newSlot) throws CommandException {
        List<DutySlot> slots = mediTrack.getDutySlotsInternal();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= slots.size()) {
            throw new CommandException(
                    String.format(MSG_SLOT_OUT_OF_BOUNDS, zeroBasedIndex, slots.size()));
        }
        slots.set(zeroBasedIndex, newSlot);
    }

    /**
     * Scans the roster for expired medical statuses (MC/Light Duty) and
     * automatically reverts those personnel to FIT status using the system/injected clock.
     */
    public void cleanExpiredStatuses() {
        LocalDate today = LocalDate.now(clock);
        List<Personnel> roster = getInternalPersonnelList();

        for (Personnel p : roster) {
            if (p.getStatusExpiryDate() != null && !today.isBefore(p.getStatusExpiryDate())) {
                p.setStatus(Status.FIT);
                p.setStatusExpiryDate(null);
            }
        }
    }
}