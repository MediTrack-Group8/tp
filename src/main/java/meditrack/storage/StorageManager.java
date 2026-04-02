package meditrack.storage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.MediTrack;
import meditrack.model.Personnel;
import meditrack.model.ReadOnlyMediTrack;

/**
 * Coordinates the saving and loading of MediTrack data by managing the lower-level JSON storage component.
 */
public class StorageManager implements Storage {

    private final JsonMediTrackStorage jsonStorage;

    /**
     * Creates a StorageManager using the default JSON storage file path.
     */
    public StorageManager() {
        this.jsonStorage = new JsonMediTrackStorage();
    }

    /**
     * Creates a StorageManager with an injected JSON storage component.
     * Primarily used for automated testing.
     *
     * @param jsonStorage The configured storage component to use.
     */
    public StorageManager(JsonMediTrackStorage jsonStorage) {
        this.jsonStorage = jsonStorage;
    }

    /**
     * Loads the entire application state from disk.
     * Automatically ignores corrupted individual records to prevent total application failure.
     *
     * @return An Optional containing the loaded MediTrack model, or empty if no save exists.
     */
    @Override
    public Optional<ReadOnlyMediTrack> readMediTrackData() {
        Optional<JsonSerializableMediTrack> rawData = jsonStorage.readData();
        if (rawData.isEmpty()) {
            return Optional.empty();
        }

        JsonSerializableMediTrack serializable = rawData.get();
        MediTrack mediTrack = new MediTrack();

        for (JsonAdaptedPersonnel adapted : serializable.personnel) {
            try {
                Personnel p = adapted.toModelType();
                mediTrack.addPersonnelRecord(p);
            } catch (CommandException e) {
                System.err.println("[StorageManager] Skipping corrupt personnel record: " + e.getMessage());
            }
        }

        for (JsonAdaptedSupply adapted : serializable.supplies) {
            try {
                mediTrack.addSupplyRecord(adapted.toModelType());
            } catch (CommandException e) {
                System.err.println("[StorageManager] Skipping corrupt supply record: " + e.getMessage());
            }
        }

        for (JsonAdaptedDutySlot adapted : serializable.dutySlots) {
            try {
                mediTrack.addDutySlotRecord(adapted.toModelType());
            } catch (CommandException e) {
                System.err.println("[StorageManager] Skipping corrupt duty slot record: " + e.getMessage());
            }
        }

        return Optional.of(mediTrack);
    }

    /**
     * Serializes the current application state and writes it to disk.
     *
     * @param data The read-only model data to persist.
     * @throws IOException If disk writing fails.
     */
    @Override
    public void saveMediTrackData(ReadOnlyMediTrack data) throws IOException {
        List<JsonAdaptedPersonnel> adaptedPersonnel = data.getPersonnelList()
                .stream()
                .map(JsonAdaptedPersonnel::fromModelType)
                .toList();

        List<JsonAdaptedSupply> adaptedSupplies = data.getSupplyList()
                .stream()
                .map(JsonAdaptedSupply::fromModelType)
                .toList();

        List<JsonAdaptedDutySlot> adaptedDutySlots = data.getDutySlots()
                .stream()
                .map(JsonAdaptedDutySlot::fromModelType)
                .toList();

        JsonSerializableMediTrack serializableData =
                new JsonSerializableMediTrack(adaptedSupplies, adaptedPersonnel, adaptedDutySlots);

        jsonStorage.saveData(serializableData);
    }
}