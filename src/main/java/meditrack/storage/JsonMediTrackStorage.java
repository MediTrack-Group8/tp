package meditrack.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles the low-level File I/O operations to persist MediTrack data to the hard drive using Jackson.
 */
public class JsonMediTrackStorage {

    private final Path filePath;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a storage instance pointing to the default data.json path.
     */
    public JsonMediTrackStorage() {
        this.filePath = Paths.get("data.json");
    }

    /**
     * Creates a storage instance pointing to a specified file path.
     * Useful for dependency injection during testing to prevent overwriting production data.
     *
     * @param filePath The custom Path to read from and write to.
     */
    public JsonMediTrackStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Retrieves the configured file path where the data is stored.
     *
     * @return The Path object.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Reads the serialized application data from the JSON file.
     *
     * @return An Optional containing the data if successful, or empty if the file is missing or unreadable.
     */
    public Optional<JsonSerializableMediTrack> readData() {
        File file = filePath.toFile();
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            JsonSerializableMediTrack data = objectMapper.readValue(file, JsonSerializableMediTrack.class);
            return Optional.of(data);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Saves the serialized data directly to the configured JSON file.
     *
     * @param data The root data wrapper to serialize.
     * @throws IOException If there is an issue writing to the disk.
     */
    public void saveData(JsonSerializableMediTrack data) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), data);
    }
}