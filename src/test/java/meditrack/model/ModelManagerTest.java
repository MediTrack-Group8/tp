package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import meditrack.logic.commands.exceptions.CommandException;

/**
 * JUnit tests for the ModelManager, focusing on time-dependent logic and dependency injection.
 */
public class ModelManagerTest {

    private ModelManager modelManager;

    @BeforeEach
    public void setUp() {
        modelManager = new ModelManager();
    }

    @Test
    public void cleanExpiredStatuses_mcNotExpired_statusRemainsMc() throws CommandException {
        // Arrange: Add a personnel and set them to MC expiring in 2 days
        modelManager.addPersonnel("John Doe", Status.FIT);
        Personnel john = modelManager.getFilteredPersonnelList(null).get(0);

        john.setStatus(Status.MC);
        john.setStatusExpiryDate(LocalDate.now(modelManager.getClock()).plusDays(2));

        // Act: Run the cleaner logic
        modelManager.cleanExpiredStatuses();

        // Assert: John should still be on MC because time hasn't passed
        assertEquals(Status.MC, john.getStatus());
    }

    @Test
    public void cleanExpiredStatuses_mcExpiredViaTimeTravel_statusRevertsToFit() throws CommandException {
        // Arrange: Add a personnel and set them to MC expiring in 2 days
        modelManager.addPersonnel("Jane Smith", Status.FIT);
        Personnel jane = modelManager.getFilteredPersonnelList(null).get(0);

        jane.setStatus(Status.MC);
        jane.setStatusExpiryDate(LocalDate.now(modelManager.getClock()).plusDays(2));

        // Fast-Forward the clock 3 days into the future! (Dependency Injection Test)
        Clock futureClock = Clock.offset(modelManager.getClock(), Duration.ofDays(3));
        modelManager.setClock(futureClock);

        // Act: Run the cleaner logic using the future clock
        modelManager.cleanExpiredStatuses();

        // Assert: Jane should have automatically reverted to FIT, and expiry date cleared
        assertEquals(Status.FIT, jane.getStatus());
        assertNull(jane.getStatusExpiryDate());
    }
}