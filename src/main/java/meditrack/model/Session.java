package meditrack.model;

/**
 * Manages the active user session within the application.
 * Implements the Singleton pattern to ensure a single, globally accessible
 * session state representing the currently logged-in user's role.
 */
public class Session {

    private static Session instance;
    private Role currentRole;

    /**
     * Private constructor to prevent external instantiation.
     */
    private Session() {
    }

    /**
     * Retrieves the singleton instance of the Session.
     * Lazily initializes the instance on the first call.
     *
     * @return The active, global Session instance.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Assigns the active operational role to the current session upon successful login.
     *
     * @param role The Role to assign to the user.
     */
    public void setRole(Role role) {
        this.currentRole = role;
    }

    /**
     * Retrieves the role associated with the current session.
     *
     * @return The active Role, or null if no user is logged in.
     */
    public Role getRole() {
        return currentRole;
    }

    /**
     * Clears the current session data, effectively logging the user out and removing permissions.
     */
    public void clear() {
        this.currentRole = null;
    }
}