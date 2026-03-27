package meditrack.model;

import java.util.List;

import javafx.collections.ObservableList;

import meditrack.commons.core.Index;
import meditrack.logic.commands.exceptions.CommandException;

/**
 * API of the Model component.
 */
public interface Model {

    /**
     * Retrieves the current Session object.
     */
    Session getSession();

    /**
     * Sets the active role in the Session after a successful login.
     */
    void setRole(Role role);

    /**
     * Adds a new supply to the inventory.
     * The supply name must not already exist (case-insensitive).
     */
    void addSupply(Supply supply);

    /**
     * Replaces the supply at {@code targetIndex} with {@code editedSupply}.
     */
    void editSupply(Index targetIndex, Supply editedSupply);

    /**
     * Deletes and returns the supply at {@code targetIndex}.
     */
    Supply deleteSupply(Index targetIndex);

    /**
     * Returns an observable list of all supplies.
     */
    ObservableList<Supply> getFilteredSupplyList();

    /**
     * Returns supplies expiring within {@code daysThreshold} days, sorted by expiry date ascending.
     */
    List<Supply> getExpiringSupplies(int daysThreshold);

    /**
     * Returns supplies with quantity below {@code quantityThreshold}, sorted by quantity ascending.
     */
    List<Supply> getLowStockSupplies(int quantityThreshold);

    /**
     * Returns a read-only view of the underlying MediTrack data (for Storage serialisation).
     */
    ReadOnlyMediTrack getMediTrack();

    /**
     * Adds a new personnel member to the roster.
     */
    void addPersonnel(String name, Status status) throws CommandException;

    /**
     * Removes a personnel member by 1-based index.
     */
    Personnel deletePersonnel(int oneBasedIndex) throws CommandException;

    /**
     * Updates the status of a personnel member by 1-based index.
     */
    void setPersonnelStatus(int oneBasedIndex, Status newStatus) throws CommandException;

    /**
     * Returns a snapshot list of personnel filtered by status (null for all).
     */
    List<Personnel> getFilteredPersonnelList(Status statusFilter);

    /**
     * Returns the live observable personnel list for UI binding.
     */
    ObservableList<Personnel> getPersonnelList();

    /**
     * Generates a randomised duty roster from FIT personnel.
     */
    List<Personnel> generateRoster() throws CommandException;

    /**
     * Returns the total number of personnel in the roster.
     */
    int getPersonnelCount();

    // ── Duty slot management ──────────────────────────────────────────────────

    /**
     * Returns an unmodifiable snapshot of scheduled duty slots.
     */
    List<DutySlot> getDutySlots();

    /**
     * Appends a duty slot to the schedule and persists the change.
     */
    void addDutySlot(DutySlot slot);

    /**
     * Removes the duty slot at the given zero-based index.
     *
     * @throws CommandException if the index is out of bounds
     */
    void removeDutySlot(int zeroBasedIndex) throws CommandException;

    /**
     * Removes all scheduled duty slots.
     */
    void clearDutySlots();

    /**
     * Replaces the duty slot at {@code zeroBasedIndex} with {@code newSlot}.
     *
     * @throws CommandException if the index is out of bounds
     */
    void replaceDutySlot(int zeroBasedIndex, DutySlot newSlot) throws CommandException;
}