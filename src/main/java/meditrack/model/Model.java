package meditrack.model;
/**
 * API of the Model component.
 */
public interface Model {
    /**
     * Retrieves the current Session object.
     */
    Session getSession();
    /**
     * Sets the active role in the Session after a successful login.
     */
    void setRole(Role role);
    // Teammates will add CRUD methods here (e.g., addSupply, deletePersonnel)
}