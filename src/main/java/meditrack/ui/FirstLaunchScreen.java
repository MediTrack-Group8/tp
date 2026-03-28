package meditrack.ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import meditrack.security.PasswordManager;
import meditrack.storage.JsonMediTrackStorage;
import meditrack.storage.JsonSerializableMediTrack;

/**
 * Represents the UI screen displayed when the application is launched for the very first time.
 * Handles the creation and secure storage of the master application password.
 * Uses the "Field Ops Command" tactical design system.
 */
public class FirstLaunchScreen extends BorderPane {

    // Design tokens — Field Ops Command design system
    private static final String BG          = "#0a0a0a";
    private static final String SURFACE     = "#121410";
    private static final String OLIVE       = "#556b2f";
    private static final String OLIVE_LIGHT = "#8aa65c";
    private static final String TEXT_DIM    = "#8f9284";
    private static final String TEXT_MUTED  = "#45483c";
    private static final String BORDER      = "#2a2d24";
    private static final String WARNING     = "#fbbc00";

    private final Runnable onSetupComplete;
    private final JsonMediTrackStorage storageEngine;

    /**
     * Constructs the First Launch setup screen.
     *
     * @param onSetupComplete A callback function to execute once the password is successfully
     *                        saved (usually transitions to Login).
     */
    public FirstLaunchScreen(Runnable onSetupComplete) {
        this.onSetupComplete = onSetupComplete;
        this.storageEngine = new JsonMediTrackStorage();
        initializeUI();
    }

    private void initializeUI() {
        setStyle("-fx-background-color: " + BG + ";");
        setTop(buildTitleBar());
        setCenter(buildMainContent());
        setBottom(buildStatusBar());
    }

    private HBox buildTitleBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 12, 0, 12));
        bar.setPrefHeight(36);
        bar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 0 0 1 0;");

        Rectangle icon = new Rectangle(12, 12);
        icon.setFill(Color.web(OLIVE));
        icon.setStroke(Color.web(OLIVE_LIGHT));
        icon.setStrokeWidth(1);

        Label title = new Label("  MEDITRACK TERMINAL  //  INITIAL SYSTEM SETUP");
        title.setStyle("-fx-text-fill: " + OLIVE_LIGHT + "; -fx-font-size: 10px; -fx-font-weight: bold;"
                + " -fx-font-family: 'Consolas', 'Courier New', monospace;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label close = new Label("✕");
        close.setStyle("-fx-text-fill: #e07070; -fx-font-size: 11px;"
                + " -fx-font-family: 'Consolas', monospace;");
        close.setOpacity(0.6);
        close.setOnMouseEntered(e -> close.setOpacity(1.0));
        close.setOnMouseExited(e -> close.setOpacity(0.6));

        bar.getChildren().addAll(icon, title, spacer, close);
        return bar;
    }

    private StackPane buildMainContent() {
        StackPane stack = new StackPane();
        stack.setStyle("-fx-background-color: " + BG + ";");

        Canvas gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(stack.widthProperty());
        gridCanvas.heightProperty().bind(stack.heightProperty());
        gridCanvas.widthProperty().addListener((o, ov, nv) -> drawDotGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((o, ov, nv) -> drawDotGrid(gridCanvas));

        Rectangle outerFrame = new Rectangle();
        outerFrame.setFill(Color.TRANSPARENT);
        outerFrame.setStroke(Color.web(OLIVE, 0.18));
        outerFrame.setStrokeWidth(1);
        outerFrame.widthProperty().bind(stack.widthProperty().multiply(0.48));
        outerFrame.setHeight(420);

        Rectangle innerFrame = new Rectangle();
        innerFrame.setFill(Color.TRANSPARENT);
        innerFrame.setStroke(Color.web(OLIVE, 0.08));
        innerFrame.setStrokeWidth(1);
        innerFrame.widthProperty().bind(stack.widthProperty().multiply(0.52));
        innerFrame.setHeight(460);

        VBox form = buildSetupForm();

        stack.getChildren().addAll(gridCanvas, innerFrame, outerFrame, form);
        return stack;
    }

    private void drawDotGrid(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web(OLIVE, 0.07));
        int spacing = 28;
        for (int x = spacing; x < canvas.getWidth(); x += spacing) {
            for (int y = spacing; y < canvas.getHeight(); y += spacing) {
                gc.fillOval(x - 1.5, y - 1.5, 3, 3);
            }
        }
    }

    private VBox buildSetupForm() {
        VBox form = new VBox(18);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(340);
        form.setPadding(new Insets(48, 40, 48, 40));

        // --- Tag ---
        Label tag = new Label("[ SYSTEM INITIALIZATION ]");
        tag.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-font-size: 9px;"
                + " -fx-font-family: 'Consolas', monospace;");

        // --- Heading ---
        Label heading = new Label("ESTABLISH\nMASTER KEY");
        heading.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;"
                + " -fx-font-family: 'Consolas', monospace; -fx-text-alignment: center;");
        heading.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // --- Divider ---
        HBox divRow = new HBox(8);
        divRow.setAlignment(Pos.CENTER);
        Region l1 = new Region();
        l1.setPrefWidth(36);
        l1.setPrefHeight(1);
        l1.setStyle("-fx-background-color: " + OLIVE + ";");
        Label divText = new Label("FIRST LAUNCH  //  ONE-TIME SETUP");
        divText.setStyle("-fx-text-fill: " + OLIVE_LIGHT + "; -fx-font-size: 9px;"
                + " -fx-font-family: 'Consolas', monospace;");
        Region l2 = new Region();
        l2.setPrefWidth(36);
        l2.setPrefHeight(1);
        l2.setStyle("-fx-background-color: " + OLIVE + ";");
        divRow.getChildren().addAll(l1, divText, l2);

        // --- Password field ---
        VBox passwordSection = new VBox(5);
        Label pwLabel = new Label("NEW MASTER ACCESS KEY");
        pwLabel.setStyle("-fx-text-fill: " + OLIVE_LIGHT + "; -fx-font-size: 9px; -fx-font-weight: bold;"
                + " -fx-font-family: 'Consolas', monospace;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter new master password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle(inputStyle());
        passwordField.focusedProperty().addListener((o, ov, focused) ->
                passwordField.setStyle(focused ? inputFocusStyle() : inputStyle()));
        passwordSection.getChildren().addAll(pwLabel, passwordField);

        // --- Error label ---
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #e07070; -fx-font-size: 11px;"
                + " -fx-font-family: 'Consolas', monospace;");

        // --- Confirm button ---
        Button confirmBtn = new Button("INITIALIZE SYSTEM");
        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle(buttonStyle());
        confirmBtn.setOnMouseEntered(e -> confirmBtn.setStyle(buttonHoverStyle()));
        confirmBtn.setOnMouseExited(e -> confirmBtn.setStyle(buttonStyle()));
        confirmBtn.setOnAction(e -> handlePasswordSetup(passwordField.getText(), errorLabel));

        form.getChildren().addAll(tag, heading, divRow, passwordSection, errorLabel, confirmBtn);
        return form;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 12, 0, 12));
        bar.setPrefHeight(28);
        bar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER
                + "; -fx-border-width: 1 0 0 0;");

        Label warn = new Label("⚠  FIRST LAUNCH — NO CREDENTIALS ESTABLISHED");
        warn.setStyle("-fx-text-fill: " + WARNING + "; -fx-font-size: 9px;"
                + " -fx-font-family: 'Consolas', monospace;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label clock = new Label();
        clock.setStyle("-fx-text-fill: " + OLIVE_LIGHT + "; -fx-font-size: 9px;"
                + " -fx-font-family: 'Consolas', monospace;");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        clock.setText(LocalDateTime.now().format(fmt));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> clock.setText(LocalDateTime.now().format(fmt))));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        bar.getChildren().addAll(warn, spacer, clock);
        return bar;
    }

    private String inputStyle() {
        return "-fx-background-color: #0d0f0b; -fx-text-fill: " + OLIVE_LIGHT + ";"
                + " -fx-border-color: " + BORDER + "; -fx-border-width: 1;"
                + " -fx-padding: 10 12 10 12; -fx-font-size: 13px;"
                + " -fx-background-radius: 0; -fx-border-radius: 0;"
                + " -fx-font-family: 'Consolas', 'Courier New', monospace;"
                + " -fx-prompt-text-fill: " + TEXT_MUTED + ";";
    }

    private String inputFocusStyle() {
        return "-fx-background-color: #0d0f0b; -fx-text-fill: " + OLIVE_LIGHT + ";"
                + " -fx-border-color: " + OLIVE + "; -fx-border-width: 0 0 2 0;"
                + " -fx-padding: 10 12 10 12; -fx-font-size: 13px;"
                + " -fx-background-radius: 0; -fx-border-radius: 0;"
                + " -fx-font-family: 'Consolas', 'Courier New', monospace;"
                + " -fx-prompt-text-fill: " + TEXT_MUTED + ";";
    }

    private String buttonStyle() {
        return "-fx-background-color: " + OLIVE + "; -fx-text-fill: white;"
                + " -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 13 24 13 24;"
                + " -fx-background-radius: 0; -fx-cursor: hand;"
                + " -fx-font-family: 'Consolas', monospace;";
    }

    private String buttonHoverStyle() {
        return "-fx-background-color: " + OLIVE_LIGHT + "; -fx-text-fill: white;"
                + " -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 13 24 13 24;"
                + " -fx-background-radius: 0; -fx-cursor: hand;"
                + " -fx-font-family: 'Consolas', monospace;";
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
            errorLabel.setText("! ACCESS KEY CANNOT BE EMPTY.");
            return;
        }

        try {
            String hash = PasswordManager.hashPassword(plainTextPassword);
            JsonSerializableMediTrack initialData = new JsonSerializableMediTrack(hash, null, null, null);
            storageEngine.saveData(initialData);
            onSetupComplete.run();
        } catch (IOException ex) {
            errorLabel.setText("! SYSTEM ERROR: " + ex.getMessage().toUpperCase());
        }
    }
}
