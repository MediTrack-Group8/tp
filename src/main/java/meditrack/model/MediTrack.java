package meditrack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import meditrack.model.exceptions.DuplicateSupplyException;

/**
 * Root data container that holds both the supply and personnel lists.
 * The public getters return unmodifiable lists so nothing outside can
 * accidentally mess with the data directly.
 */
public class MediTrack implements ReadOnlyMediTrack {

    private final ObservableList<Supply> supplies = FXCollections.observableArrayList();
    final ObservableList<Personnel> personnel = FXCollections.observableArrayList();
    private final List<DutySlot> dutySlots = new ArrayList<>();

    /**
     * Appends a {@link Personnel} record directly to the internal list.
     * Used by {@code StorageManager} when loading data from disk.
     * No duplicate check is performed here — callers are responsible for validation.
     */
    public void addPersonnelRecord(Personnel p) {
        personnel.add(p);
    }

    /**
     * Returns the live mutable personnel list (same package only; used by {@link ModelManager}).
     */
    ObservableList<Personnel> getPersonnelObservable() {
        return personnel;
    }

    /** Unmodifiable view of supplies. */
    @Override
    public ObservableList<Supply> getSupplyList() {
        return FXCollections.unmodifiableObservableList(supplies);
    }

    /** Unmodifiable view of personnel. */
    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return FXCollections.unmodifiableObservableList(personnel);
    }

    /** Checks for duplicate supply name (case-insensitive). */
    public boolean hasSupply(Supply supply) {
        return supplies.stream().anyMatch(s -> s.equals(supply));
    }

    /** Adds a supply; throws if the name already exists (case-insensitive). */
    public void addSupply(Supply supply) {
        if (hasSupply(supply)) {
            throw new DuplicateSupplyException();
        }
        supplies.add(supply);
    }

    /** Replaces the supply at {@code index}. */
    public void setSupply(int index, Supply editedSupply) {
        supplies.set(index, editedSupply);
    }

    /** Removes and returns the supply at {@code index}. */
    public Supply removeSupply(int index) {
        return supplies.remove(index);
    }

    /** Returns the internal modifiable list — only ModelManager should call this. */
    public ObservableList<Supply> getInternalSupplyList() {
        return supplies;
    }

    /** Appends a supply without duplicate check — used by StorageManager when loading from disk. */
    public void addSupplyRecord(Supply s) {
        supplies.add(s);
    }

    // ── Duty slots ────────────────────────────────────────────────────────────

    /** Unmodifiable view of scheduled duty slots. */
    @Override
    public List<DutySlot> getDutySlots() {
        return Collections.unmodifiableList(dutySlots);
    }

    /** Internal mutable list — only ModelManager should call this. */
    List<DutySlot> getDutySlotsInternal() {
        return dutySlots;
    }

    /** Appends a duty slot record — used by StorageManager when loading from disk. */
    public void addDutySlotRecord(DutySlot slot) {
        dutySlots.add(slot);
    }
}