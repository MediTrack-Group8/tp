package meditrack.storage;

import java.io.IOException;
import java.util.Optional;

import meditrack.model.ReadOnlyMediTrack;

/**
 * API of the Storage component.
 * Defines the contract for reading from and writing to the local hard drive.
 */
public interface Storage {

    /**
     * Checks whether the local data file exists on the hard drive.
     * * @return true if data.json does not exist (indicating a first-time launch), false otherwise.
     */
    boolean isFirstLaunch();

    /**
     * Reads the local JSON file during application startup.
     * * @return An Optional containing the parsed ReadOnlyMediTrack data, or an empty Optional if no file is found.
     */
    Optional<ReadOnlyMediTrack> readMediTrackData();

    /**
     * Saves the current state of the application to the local JSON file.
     * * @param data A read-only snapshot of the current Model data to be saved.
     * @throws IOException If there is an error writing the file to the hard drive.
     */
    void saveMediTrackData(ReadOnlyMediTrack data) throws IOException;
}