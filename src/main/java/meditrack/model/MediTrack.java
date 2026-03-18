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
    private final ObservableList<Personnel> personnel = FXCollections.observableArrayList();

    @Override
    public ObservableList<Supply> getSupplyList() {
        return FXCollections.unmodifiableObservableList(supplies);
    }

    @Override
    public ObservableList<Personnel> getPersonnelList() {
        return FXCollections.unmodifiableObservableList(personnel);
    }
}