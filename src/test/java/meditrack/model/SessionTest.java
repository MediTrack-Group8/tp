package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Session manager, ensuring Singleton integrity and state clearing.
 */
public class SessionTest {

    @BeforeEach
    public void setUp() {
        // Ensure a clean slate before each test
        Session.getInstance().clear();
    }

    @Test
    public void getInstance_returnsSameSingletonInstance() {
        Session firstCall = Session.getInstance();
        Session secondCall = Session.getInstance();

        // assertSame checks if they point to the exact same object in memory
        assertSame(firstCall, secondCall);
    }

    @Test
    public void setRoleAndGetRole_storesAndRetrievesCorrectly() {
        Session session = Session.getInstance();

        session.setRole(Role.MEDICAL_OFFICER);
        assertEquals(Role.MEDICAL_OFFICER, session.getRole());

        // Update the role
        session.setRole(Role.LOGISTICS_OFFICER);
        assertEquals(Role.LOGISTICS_OFFICER, session.getRole());
    }

    @Test
    public void clear_resetsRoleToNull() {
        Session session = Session.getInstance();
        session.setRole(Role.FIELD_MEDIC);

        // Act
        session.clear();

        // Assert
        assertNull(session.getRole(), "Session role should be null after clearing.");
    }
}