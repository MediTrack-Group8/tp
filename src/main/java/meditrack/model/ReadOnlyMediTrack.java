package meditrack.model;

import javafx.collections.ObservableList;

/**
 * Unmodifiable view of the MediTrack data.
 */
public interface ReadOnlyMediTrack {
    ObservableList<Supply> getSupplyList();
    ObservableList<Personnel> getPersonnelList();
}