package meditrack;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import meditrack.model.MediTrack;
import meditrack.model.ReadOnlyMediTrack;
import meditrack.storage.StorageManager;
import meditrack.ui.LoginScreen;
import meditrack.ui.MainAppScreen;

/**
 * The main entry point for the MediTrack JavaFX application.
 * Handles the application lifecycle, including booting the storage layer,
 * loading data, and transitioning between the login and main application screens.
 */
public class Main extends Application {

    private Stage primaryStage;
    private final StorageManager storageManager = new StorageManager();

    /** * The root data container loaded once per session from the disk.
     * If no save file exists, an empty MediTrack instance is created.
     */
    private MediTrack mediTrack;

    /**
     * Initializes the primary JavaFX stage, loads necessary data via the StorageManager,
     * and displays the Login Screen.
     *
     * @param primaryStage The primary window provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MediTrack");
        primaryStage.setWidth(900);
        primaryStage.setHeight(620);

        Optional<ReadOnlyMediTrack> loaded = storageManager.readMediTrackData();
        mediTrack = loaded.isPresent()
                ? (MediTrack) loaded.get()
                : new MediTrack();

        // Bypass any setup wizard and boot straight into the Login Screen
        showLoginScreen();

        primaryStage.show();
    }

    /**
     * Transitions the application view to the Role-Based Access Control Login Screen.
     */
    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this::showMainAppScreen);
        primaryStage.setScene(new Scene(loginScreen));
    }

    /**
     * Transitions the application view to the Main Dashboard after successful authentication.
     */
    private void showMainAppScreen() {
        MainAppScreen mainApp = new MainAppScreen(mediTrack, storageManager, this::showLoginScreen);
        primaryStage.setScene(new Scene(mainApp, 900, 620));
    }

    /**
     * The standard Java entry point.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        launch(args);
    }
}