package meditrack;

import java.util.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import meditrack.logic.Logic;
import meditrack.logic.LogicManager;
import meditrack.model.MediTrack;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.ReadOnlyMediTrack;
import meditrack.storage.StorageManager;
import meditrack.ui.LoginScreen;
import meditrack.ui.MainAppScreen;

/**
 * The main entry point for the MediTrack JavaFX application.
 * Handles the application lifecycle, including initializing the storage layer,
 * loading data, and constructing the decoupled Model-View-Controller architecture.
 */
public class Main extends Application {

    private Stage primaryStage;
    private final StorageManager storageManager = new StorageManager();

    private Model model;
    private Logic logic;

    /**
     * Initializes the primary JavaFX stage, loads necessary data from disk,
     * wires the Logic and Model components together, and displays the Login Screen.
     *
     * @param primaryStage The primary window provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MediTrack");
        primaryStage.setWidth(900);
        primaryStage.setHeight(620);

        // 1. Initialize Storage Data
        Optional<ReadOnlyMediTrack> loaded = storageManager.readMediTrackData();
        MediTrack mediTrack = loaded.isPresent() ? (MediTrack) loaded.get() : new MediTrack();

        // 2. Initialize Core Architecture (Ensures UI is completely decoupled from Storage)
        model = new ModelManager(mediTrack);
        logic = new LogicManager(model, storageManager);

        showLoginScreen();
        primaryStage.show();
    }

    /**
     * Transitions the application view to the Role-Based Access Control Login Screen.
     */
    private void showLoginScreen() {
        // We will fix LoginScreen to accept 'model' in Batch 2
        LoginScreen loginScreen = new LoginScreen(model, this::showMainAppScreen);
        primaryStage.setScene(new Scene(loginScreen));
    }

    /**
     * Transitions the application view to the Main Dashboard after successful authentication.
     */
    private void showMainAppScreen() {
        MainAppScreen mainApp = new MainAppScreen(model, logic, this::showLoginScreen);
        primaryStage.setScene(new Scene(mainApp, 900, 620));
    }

    /**
     * The standard Java application entry point.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        launch(args);
    }
}