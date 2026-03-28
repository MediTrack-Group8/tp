package meditrack.ui;

import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import meditrack.security.PasswordManager;
import meditrack.storage.JsonMediTrackStorage;
import meditrack.storage.JsonSerializableMediTrack;

/**
 * Represents the UI screen displayed when the application is launched for the very first time.
 * Handles the creation and secure storage of the master application password.
 */
public class FirstLaunchScreen extends VBox {

    private final Runnable onSetupComplete;
    private final JsonMediTrackStorage storageEngine;

    /**
     * Constructs the First Launch setup screen.
     *
     * @param onSetupComplete A callback function to execute once the password is successfully saved (usually transitions to Login).
     */
    public FirstLaunchScreen(Runnable onSetupComplete) {
        this.onSetupComplete = onSetupComplete;
        this.storageEngine = new JsonMediTrackStorage();
        initializeUI();
    }

    /**
     * Initializes and arranges the JavaFX components for this screen.
     */
    private void initializeUI() {
        // App background and layout
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #f0f2f5;"); // Matched to MainAppScreen

        // Title
        Label titleLabel = new Label("Welcome to MediTrack\nPlease set a master password:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");

        // Input
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new master password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 8; -fx-background-radius: 4;");

        // Error Feedback
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;"); // Matched to PersonnelScreen

        // Action Button
        Button confirmButton = new Button("Set Password");
        confirmButton.setMaxWidth(250);
        confirmButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 5; -fx-cursor: hand;");
        confirmButton.setOnAction(e -> handlePasswordSetup(passwordField.getText(), errorLabel));

        this.getChildren().addAll(titleLabel, passwordField, confirmButton, errorLabel);
    }

    /**
     * Handles the logic when the user clicks the Confirm button.
     * Hashes the password and initializes the data.json file.
     *
     * @param plainTextPassword The password entered by the user.
     * @param errorLabel The label used to display validation errors.
     */
    private void handlePasswordSetup(String plainTextPassword, Label errorLabel) {
        if (plainTextPassword == null || plainTextPassword.trim().isEmpty()) {
            errorLabel.setText("Password cannot be empty.");
            return;
        }

        try {
            // Hash the password
            String hash = PasswordManager.hashPassword(plainTextPassword);

            // Create the initial empty database with the new password hash
            JsonSerializableMediTrack initialData = new JsonSerializableMediTrack(hash, null, null, null);
            storageEngine.saveData(initialData);

            // Trigger the callback to switch to the Login screen
            onSetupComplete.run();
        } catch (IOException ex) {
            errorLabel.setText("Failed to save data: " + ex.getMessage());
        }
    }
}