package meditrack.storage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.MediTrack;
import meditrack.model.Personnel;
import meditrack.model.ReadOnlyMediTrack;

/**
 * Manages storage of MediTrack data in local storage.
 *
 * <p>Fills in the two TODOs left by the team's stub:
 * <ol>
 *   <li>{@link #readMediTrackData()} — maps {@link JsonSerializableMediTrack}
 *       → {@link ReadOnlyMediTrack} using {@link JsonAdaptedPersonnel#toModelType()}</li>
 *   <li>{@link #saveMediTrackData(ReadOnlyMediTrack)} — maps
 *       {@link ReadOnlyMediTrack} → {@link JsonSerializableMediTrack} and writes to disk</li>
 * </ol>
 */
public class StorageManager implements Storage {

    private final JsonMediTrackStorage jsonStorage;

    /** Constructs a {@code StorageManager} with the default JSON storage engine. */
    public StorageManager() {
        this.jsonStorage = new JsonMediTrackStorage();
    }

    // -------------------------------------------------------------------------
    // isFirstLaunch — unchanged from team stub
    // -------------------------------------------------------------------------

    @Override
    public boolean isFirstLaunch() {
        return !jsonStorage.getFilePath().toFile().exists();
    }

    // -------------------------------------------------------------------------
    // readMediTrackData — fills in team's TODO
    // -------------------------------------------------------------------------

    /**
     * Reads the local JSON file and converts it to a {@link ReadOnlyMediTrack}.
     *
     * <p>If the file is missing or a personnel record cannot be parsed,
     * an empty Optional is returned so the app falls back to defaults.
     *
     * @return Optional containing populated {@link MediTrack}, or empty on failure
     */
    @Override
    public Optional<ReadOnlyMediTrack> readMediTrackData() {
        Optional<JsonSerializableMediTrack> rawData = jsonStorage.readData();
        if (rawData.isEmpty()) {
            return Optional.empty();
        }

        JsonSerializableMediTrack serializable = rawData.get();
        MediTrack mediTrack = new MediTrack();

        // Convert each JsonAdaptedPersonnel → Personnel and add to MediTrack
        for (JsonAdaptedPersonnel adapted : serializable.personnel) {
            try {
                Personnel p = adapted.toModelType();
                mediTrack.addPersonnelRecord(p);
            } catch (CommandException e) {
                // Corrupt record: skip and log; do not crash the whole app
                System.err.println("[StorageManager] Skipping corrupt personnel record: "
                        + e.getMessage());
            }
        }

        // Person B will add supply loading here in the same pattern
        // for (JsonAdaptedSupply adapted : serializable.supplies) { ... }

        return Optional.of(mediTrack);
    }

    // -------------------------------------------------------------------------
    // saveMediTrackData — fills in team's TODO
    // -------------------------------------------------------------------------

    /**
     * Serialises the current model data to JSON and writes it to disk.
     *
     * @param data a read-only snapshot of the current model state
     * @throws IOException if the file cannot be written
     */
    @Override
    public void saveMediTrackData(ReadOnlyMediTrack data) throws IOException {
        // Convert Personnel domain objects → JsonAdaptedPersonnel DTOs
        List<JsonAdaptedPersonnel> adaptedPersonnel = data.getPersonnelList()
                .stream()
                .map(JsonAdaptedPersonnel::fromModelType)
                .toList();

        // Person B will provide adaptedSupplies in the same pattern
        List<JsonAdaptedSupply> adaptedSupplies = data.getSupplyList()
                .stream()
                .map(s -> new JsonAdaptedSupply(null, 0, null)) // placeholder until Person B fills in
                .toList();

        // Preserve the existing password hash — read it back from the file
        String passwordHash = jsonStorage.readData()
                .map(d -> d.passwordHash)
                .orElse(null);

        JsonSerializableMediTrack serializableData =
                new JsonSerializableMediTrack(passwordHash, adaptedSupplies, adaptedPersonnel);

        jsonStorage.saveData(serializableData);
    }
}