package meditrack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import meditrack.logic.commands.exceptions.CommandException;

/**
 * Concrete implementation of {@link Model}.
 *
 * <p>Wraps a {@link MediTrack} data object and a {@link Session}, and exposes
 * all Personnel CRUD operations required by Person C's commands.
 *
 * <p>The underlying personnel list is an {@link ObservableList} so JavaFX
 * UI components can bind to it directly.
 */
public class ModelManager implements Model {

    private static final String MSG_DUPLICATE =
            "A personnel member named \"%s\" already exists.";
    private static final String MSG_OUT_OF_BOUNDS =
            "Index %d is out of bounds. The list currently has %d member(s).";
    private static final String MSG_NO_FIT =
            "Cannot generate roster: no personnel with status FIT currently exist.";

    private final MediTrack mediTrack;
    private final Session session;

    /**
     * Constructs a ModelManager with the given data store.
     * Uses the shared {@link Session} singleton.
     */
    public ModelManager(MediTrack mediTrack) {
        this.mediTrack = mediTrack;
        this.session = Session.getInstance();
    }

    // -------------------------------------------------------------------------
    // Model interface — session
    // -------------------------------------------------------------------------

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setRole(Role role) {
        session.setRole(role);
    }

    // -------------------------------------------------------------------------
    // Personnel — internal mutable list (MediTrack stores ObservableList)
    // -------------------------------------------------------------------------

    /**
     * Returns a live, unmodifiable view of the full personnel list.
     * Suitable for binding to JavaFX TableViews.
     */
    public ObservableList<Personnel> getPersonnelList() {
        return mediTrack.getPersonnelList();
    }

    // -------------------------------------------------------------------------
    // addPersonnel
    // -------------------------------------------------------------------------

    /**
     * Adds a new personnel member to the roster.
     *
     * @param name   non-blank display name
     * @param status initial medical readiness status
     * @throws CommandException if a member with the same name (case-insensitive) already exists
     */
    public void addPersonnel(String name, Status status) throws CommandException {
        Personnel candidate = new Personnel(name, status);
        for (Personnel existing : getInternalList()) {
            if (existing.equals(candidate)) {
                throw new CommandException(String.format(MSG_DUPLICATE, name));
            }
        }
        getInternalList().add(candidate);
    }

    // -------------------------------------------------------------------------
    // deletePersonnel
    // -------------------------------------------------------------------------

    /**
     * Removes a personnel member by 1-based index (as shown in the UI).
     *
     * @param oneBasedIndex index shown to the user, starting at 1
     * @return the removed {@link Personnel}
     * @throws CommandException if the index is out of range
     */
    public Personnel deletePersonnel(int oneBasedIndex) throws CommandException {
        List<Personnel> list = getInternalList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        return list.remove(oneBasedIndex - 1);
    }

    // -------------------------------------------------------------------------
    // setPersonnelStatus
    // -------------------------------------------------------------------------

    /**
     * Updates the status of a personnel member by 1-based index.
     *
     * @param oneBasedIndex index shown to the user, starting at 1
     * @param newStatus     the new medical readiness status
     * @throws CommandException if the index is out of range
     */
    public void setPersonnelStatus(int oneBasedIndex, Status newStatus) throws CommandException {
        List<Personnel> list = getInternalList();
        if (oneBasedIndex < 1 || oneBasedIndex > list.size()) {
            throw new CommandException(
                    String.format(MSG_OUT_OF_BOUNDS, oneBasedIndex, list.size()));
        }
        list.get(oneBasedIndex - 1).setStatus(newStatus);
    }

    // -------------------------------------------------------------------------
    // getFilteredPersonnelList
    // -------------------------------------------------------------------------

    /**
     * Returns a snapshot list of personnel filtered by status.
     * Pass {@code null} to return all personnel with no filter applied.
     *
     * @param statusFilter status to filter by, or {@code null} for all
     * @return immutable filtered list
     */
    public List<Personnel> getFilteredPersonnelList(Status statusFilter) {
        if (statusFilter == null) {
            return Collections.unmodifiableList(new ArrayList<>(getInternalList()));
        }
        return getInternalList().stream()
                .filter(p -> p.getStatus() == statusFilter)
                .collect(Collectors.toUnmodifiableList());
    }

    // -------------------------------------------------------------------------
    // generateRoster (stateless)
    // -------------------------------------------------------------------------

    /**
     * Generates a randomised duty roster from all currently FIT personnel.
     *
     * <p>This operation is <b>stateless</b> — it does not persist the roster
     * to the model. Each call produces a fresh shuffle.
     *
     * @return a mutable, randomly-ordered copy of the FIT personnel list
     * @throws CommandException if there are no FIT personnel
     */
    public List<Personnel> generateRoster() throws CommandException {
        List<Personnel> fit = new ArrayList<>(getFilteredPersonnelList(Status.FIT));
        if (fit.isEmpty()) {
            throw new CommandException(MSG_NO_FIT);
        }
        Collections.shuffle(fit);
        return fit;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the mutable backing list from MediTrack.
     * MediTrack exposes an unmodifiable view via getPersonnelList(), so we
     * cast through to the internal ObservableList for mutation.
     */
    private ObservableList<Personnel> getInternalList() {
        // Both ModelManager and MediTrack are in meditrack.model so getPersonnelObservable() is accessible.
        return mediTrack.getPersonnelObservable();
    }

    /** Returns the underlying MediTrack data store. Used by PersonnelScreen to get a snapshot for saving. */
    public MediTrack getMediTrack() {
        return mediTrack;
    }

    /** Total number of personnel currently in the roster. */
    public int getPersonnelCount() {
        return getInternalList().size();
    }
}