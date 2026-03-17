package meditrack.storage;

import java.io.IOException;
import java.util.Optional;

import meditrack.model.ReadOnlyMediTrack;

/**
 * Manages storage of MediTrack data in local storage.
 * Acts as the concrete implementation of the Storage interface, delegating file I/O to JSON-specific classes.
 */
public class StorageManager implements Storage {

    private final JsonMediTrackStorage jsonStorage;

    /**
     * Constructs a {@code StorageManager} with the default JSON storage engine.
     */
    public StorageManager() {
        this.jsonStorage = new JsonMediTrackStorage();
    }

    /**
     * Checks if the application is being launched for the first time by verifying the existence of the data file.
     *
     * @return true if the JSON data file does not exist, false otherwise.
     */
    @Override
    public boolean isFirstLaunch() {
        // If data.json does not exist, it is the first launch.
        return !jsonStorage.getFilePath().toFile().exists();
    }

    /**
     * Reads the application data from the local JSON file.
     *
     * @return An Optional containing the deserialized model data, or an empty Optional if the file is missing.
     */
    @Override
    public Optional<ReadOnlyMediTrack> readMediTrackData() {
        // TODO: Person B & C will map JsonSerializableMediTrack to ReadOnlyMediTrack here
        // For now, we return empty so it compiles.
        return Optional.empty();
    }

    /**
     * Saves the current application state to the local JSON file.
     *
     * @param data A read-only snapshot of the data to save.
     * @throws IOException If the file cannot be written to the disk.
     */
    @Override
    public void saveMediTrackData(ReadOnlyMediTrack data) throws IOException {
        // TODO: Map ReadOnlyMediTrack to JsonSerializableMediTrack, then save
        // jsonStorage.saveData(serializableData);
    }
}