package meditrack.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import meditrack.model.MediTrack;
import meditrack.model.ReadOnlyMediTrack;
import meditrack.model.Status;

/**
 * End-to-end integration tests for the StorageManager using temporary file injection.
 */
public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;
    private MediTrack testModel;

    @BeforeEach
    public void setUp() {
        // Inject a temporary file path so we don't overwrite the real data.json
        Path tempFilePath = testFolder.resolve("test_data.json");
        JsonMediTrackStorage jsonStorage = new JsonMediTrackStorage(tempFilePath);
        storageManager = new StorageManager(jsonStorage);

        testModel = new MediTrack();
    }

    @Test
    public void readMediTrackData_fileDoesNotExist_returnsEmptyOptional() {
        // Act: Try reading from a fresh temp path that hasn't been written to
        Optional<ReadOnlyMediTrack> result = storageManager.readMediTrackData();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void saveAndReadMediTrackData_validModel_readsCorrectly() throws Exception {
        // Arrange: Populate the model with data
        testModel.addPersonnelRecord(new meditrack.model.Personnel("Test Soldier", Status.FIT));

        // Act: Save it, then read it back
        storageManager.saveMediTrackData(testModel);
        Optional<ReadOnlyMediTrack> loadedResult = storageManager.readMediTrackData();

        // Assert: Ensure the data survived the round trip to the disk and back
        assertTrue(loadedResult.isPresent());
        ReadOnlyMediTrack loadedData = loadedResult.get();
        assertEquals(1, loadedData.getPersonnelList().size());
        assertEquals("Test Soldier", loadedData.getPersonnelList().get(0).getName());
    }
}