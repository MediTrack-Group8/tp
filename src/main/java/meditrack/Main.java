package meditrack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import meditrack.storage.StorageManager;
import meditrack.ui.FirstLaunchScreen;
import meditrack.ui.LoginScreen;

/**
 * The main entry point for the MediTrack JavaFX application.
 * Manages the primary stage and controls the flow between setup, login, and the main app.
 */
public class Main extends Application {

    private Stage primaryStage;
    private final StorageManager storageManager = new StorageManager();

    /**
     * The main entry point for all JavaFX applications.
     *
     * @param primaryStage The primary window for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("MediTrack");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        if (storageManager.isFirstLaunch()) {
            showFirstLaunchScreen();
        } else {
            showLoginScreen();
        }

        primaryStage.show();
    }

    /**
     * Displays the First Launch Setup screen.
     */
    private void showFirstLaunchScreen() {
        FirstLaunchScreen setupScreen = new FirstLaunchScreen(this::showLoginScreen);
        primaryStage.setScene(new Scene(setupScreen));
    }

    /**
     * Displays the Login screen.
     */
    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this::showMainAppScreen);
        primaryStage.setScene(new Scene(loginScreen));
    }

    /**
     * Displays the Main Application screen after a successful login.
     * Acts as a placeholder until the full UI is built by the team.
     */
    private void showMainAppScreen() {
        // Placeholder for the main application view that your teammates will help build
        StackPane mainAppRoot = new StackPane(new Label("Welcome to MediTrack! Role: " +
                meditrack.model.Session.getInstance().getRole()));
        primaryStage.setScene(new Scene(mainAppRoot));
    }

    /**
     * The standard Java main method.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}