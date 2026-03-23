package meditrack.ui;

import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import meditrack.model.Role;
import meditrack.model.Session;
import meditrack.security.PasswordManager;
import meditrack.storage.JsonMediTrackStorage;
import meditrack.storage.JsonSerializableMediTrack;

/**
 * Represents the Login UI screen.
 * Handles user authentication and role selection before granting access to the main application.
 */
public class LoginScreen extends VBox {

    private final Runnable onLoginSuccess;
    private final JsonMediTrackStorage storageEngine;

    /**
     * Constructs the Login screen.
     *
     * @param onLoginSuccess A callback function to execute once authentication is successful.
     */
    public LoginScreen(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        this.storageEngine = new JsonMediTrackStorage();
        initializeUI();
    }

    /**
     * Initializes and arranges the JavaFX components for the login screen.
     */
    private void initializeUI() {
        // App background and layout
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #f0f2f5;"); // Matched to MainAppScreen

        // Title
        Label titleLabel = new Label("MediTrack Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Inputs
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Master Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 8; -fx-background-radius: 4;");

        ComboBox<Role> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(Role.values());
        roleDropdown.setPromptText("Select Role");
        roleDropdown.setMaxWidth(250);
        roleDropdown.setStyle("-fx-padding: 4; -fx-background-radius: 4;");

        // Error Feedback
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;"); // Matched to PersonnelScreen

        // Action Button
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(250);
        loginButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 5; -fx-cursor: hand;");
        loginButton.setOnAction(e -> handleLogin(passwordField.getText(), roleDropdown.getValue(), errorLabel));

        this.getChildren().addAll(titleLabel, passwordField, roleDropdown, loginButton, errorLabel);
    }

    /**
     * Handles the authentication process when the login button is clicked.
     *
     * @param plainTextPassword The password entered by the user.
     * @param selectedRole The role selected from the dropdown.
     * @param errorLabel The label used to display authentication errors.
     */
    private void handleLogin(String plainTextPassword, Role selectedRole, Label errorLabel) {
        if (selectedRole == null) {
            errorLabel.setText("Please select a role.");
            return;
        }

        Optional<JsonSerializableMediTrack> dataOpt = storageEngine.readData();
        if (dataOpt.isEmpty()) {
            errorLabel.setText("Database missing! Please restart app.");
            return;
        }

        String storedHash = dataOpt.get().passwordHash;

        if (PasswordManager.checkPassword(plainTextPassword, storedHash)) {
            // Authentication successful: Set the active session role
            Session.getInstance().setRole(selectedRole);
            onLoginSuccess.run();
        } else {
            errorLabel.setText("Incorrect password.");
        }
    }
}