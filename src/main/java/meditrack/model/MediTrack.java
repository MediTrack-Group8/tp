package meditrack.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Wraps all data at the MediTrack level.
 * Serves as the root container for the application's data.
 */
public class MediTrack implements ReadOnlyMediTrack {
    // Person B and C will build out the actual list operations!
    private final ObservableList<Supply> supplies = FXCollections.observableArrayList();
    final ObservableList<Personnel> personnel = FXCollections.observableArrayList();

    /**
     * Appends a {@link Personnel} record directly to the internal list.
     * Used by {@code StorageManager} when loading data from disk.
     * No duplicate check is performed here — callers are responsible for validation.
     */
    public void addPersonnelRecord(Personnel p) {
        personnel.add(p);
    }

    // -------------------------------------------------------------------------
    // Package-private accessor for ModelManager (same package)
    // -------------------------------------------------------------------------

    /**
     * Returns the live mutable personnel list.
     * Only accessible within {@code meditrack.model} — used by {@link ModelManager}.
     */
    ObservableList<Personnel> getPersonnelObservable() {
        return personnel;
    }

    @Override
    public ObservableList<Supply> getSupplyList() {
        return FXCollections.unmodifiableObservableList(supplies);
    }

    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return FXCollections.unmodifiableObservableList(personnel);
    }
}