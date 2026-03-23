package meditrack.model;

/**
 * Represents the current user session in the application.
 */
public class Session {
    private static Session instance;
    private Role currentRole;

    private Session() {
    }

    /**
     * Retrieves the single instance of the Session.
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setRole(Role role) {
        this.currentRole = role;
    }

    public Role getRole() {
        return currentRole;
    }

    public void clear() {
        this.currentRole = null;
    }
}