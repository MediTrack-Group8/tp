package meditrack.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import meditrack.logic.Logic;
import meditrack.storage.CsvExportUtility;
import meditrack.logic.LogicManager;
import meditrack.model.MediTrack;
import meditrack.model.ModelManager;
import meditrack.model.Role;
import meditrack.model.Session;
import meditrack.storage.StorageManager;
import meditrack.ui.screen.DashboardScreen;
import meditrack.ui.screen.DutyRosterScreen;
import meditrack.ui.screen.LowSupplyScreen;
import meditrack.ui.screen.ExpiringSoonScreen;
import meditrack.ui.screen.MedicalAttentionScreen;
import meditrack.ui.screen.InventoryScreen;
import meditrack.ui.screen.PersonnelScreen;
import meditrack.ui.screen.ResupplyReportScreen;
import meditrack.ui.screen.SupplyLevelsScreen;
import meditrack.ui.sidebar.Sidebar;
import meditrack.ui.sidebar.Sidebar.Screen;

/**
 * Root application layout wiring the {@link Sidebar} and the content area together.
 *
 * <p>Role-based home screen:
 * <ul>
 *   <li>FIELD_MEDIC — Inventory</li>
 *   <li>MEDICAL_OFFICER — Personnel</li>
 *   <li>LOGISTICS_OFFICER — Supply Levels</li>
 * </ul>
 */
public class MainAppScreen extends HBox {

    private final ModelManager model;
    private final StorageManager storage;
    private final Logic logic;
    private final StackPane contentArea = new StackPane();

    private DashboardScreen dashboardScreen;
    private PersonnelScreen personnelScreen;
    private DutyRosterScreen dutyRosterScreen;
    private MedicalAttentionScreen medicalAttentionScreen;
    private InventoryScreen inventoryScreen;
    private LowSupplyScreen lowSupplyScreen;
    private ExpiringSoonScreen expiringSoonScreen;
    private SupplyLevelsScreen supplyLevelsScreen;
    private ResupplyReportScreen resupplyReportScreen;

    // --- DEV MODE VARIABLES ---
    private VBox devPanel;
    private boolean isDevMode = false;
    private Screen currentScreen;

    /**
     * @param mediTrack data loaded at startup
     * @param storage for persistence
     * @param logoutCallback run when user logs out (returns to login)
     */
    public MainAppScreen(MediTrack mediTrack, StorageManager storage, Runnable logoutCallback) {
        this.model = new ModelManager(mediTrack);
        this.storage = storage;
        this.logic = new LogicManager(model, storage);
        setFillHeight(true);

        Sidebar sidebar = new Sidebar(this::showScreen, () -> {
            Session.getInstance().clear();
            logoutCallback.run();
        }, this::handleExport);

        HBox.setHgrow(contentArea, Priority.ALWAYS);
        contentArea.setStyle("-fx-background-color: #0d0f0b;");

        getChildren().addAll(sidebar, contentArea);
        showScreen(Screen.DASHBOARD);

        // --- DEV MODE SHORTCUT ACTIVATOR ---
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN),
                        () -> {
                            isDevMode = !isDevMode;
                            showScreen(currentScreen);
                        }
                );
            }
        });
    }

    /**
     * Switches the content area to the given screen.
     * Screens are lazily instantiated and refreshed on each switch.
     */
    public void showScreen(Screen screen) {
        this.currentScreen = screen;
        contentArea.getChildren().clear();
        switch (screen) {
            case DASHBOARD:
                if (dashboardScreen == null) {
                    dashboardScreen = new DashboardScreen(model);
                }
                dashboardScreen.refresh();
                contentArea.getChildren().add(dashboardScreen);
                break;
            case PERSONNEL:
                if (personnelScreen == null) {
                    personnelScreen = new PersonnelScreen(model, storage);
                }
                personnelScreen.refresh();
                contentArea.getChildren().add(personnelScreen);
                break;
            case DUTY_ROSTER:
                if (dutyRosterScreen == null) {
                    dutyRosterScreen = new DutyRosterScreen(model, storage);
                }
                dutyRosterScreen.refresh();
                contentArea.getChildren().add(dutyRosterScreen);
                break;
            case MEDICAL_ATTENTION:
                if (medicalAttentionScreen == null) {
                    medicalAttentionScreen = new MedicalAttentionScreen(model);
                }
                medicalAttentionScreen.refresh();
                contentArea.getChildren().add(medicalAttentionScreen);
                break;
            case INVENTORY:
                if (inventoryScreen == null) {
                    inventoryScreen = new InventoryScreen(model, logic);
                }
                contentArea.getChildren().add(inventoryScreen);
                break;
            case LOW_SUPPLY:
                if (lowSupplyScreen == null) {
                    lowSupplyScreen = new LowSupplyScreen(model);
                }
                lowSupplyScreen.refresh();
                contentArea.getChildren().add(lowSupplyScreen);
                break;
            case EXPIRING_SOON:
                if (expiringSoonScreen == null) {
                    expiringSoonScreen = new ExpiringSoonScreen(model);
                }
                expiringSoonScreen.refresh();
                contentArea.getChildren().add(expiringSoonScreen);
                break;
            case SUPPLY_LEVELS:
                if (supplyLevelsScreen == null) {
                    supplyLevelsScreen = new SupplyLevelsScreen(model);
                }
                contentArea.getChildren().add(supplyLevelsScreen);
                break;
            case RESUPPLY_REPORT:
                if (resupplyReportScreen == null) {
                    resupplyReportScreen = new ResupplyReportScreen(model, logic);
                }
                contentArea.getChildren().add(resupplyReportScreen);
                break;
        }

        if (isDevMode) {
            if (devPanel == null) devPanel = buildDevPanel();
            contentArea.getChildren().add(devPanel);
            StackPane.setAlignment(devPanel, Pos.TOP_RIGHT);
            StackPane.setMargin(devPanel, new Insets(20));
        }
    }

    /**
     * Handles the CSV export process, enforcing role-based access control,
     * and shows a popup alert with the result.
     */
    private void handleExport() {
        try {
            Role currentRole = Session.getInstance().getRole();

            Path savedPath = CsvExportUtility.exportData(model.getMediTrack(), currentRole);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Security Clearance: " + currentRole.toString()
                    + "\nData successfully exported to:\n" + savedPath.toAbsolutePath());
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText(null);
            alert.setContentText("Could not export data: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /** Builds the secret developer tools panel. */
    private VBox buildDevPanel() {
        VBox panel = new VBox(10);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: rgba(20, 20, 20, 0.95); -fx-border-color: #00ff00;"
                + " -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(0,255,0,0.5), 10, 0, 0, 0);");

        Label title = new Label("⚠ DEV MODE ACTIVE");
        title.setStyle("-fx-text-fill: #00ff00; -fx-font-size: 14px; -fx-font-weight: bold; -fx-font-family: 'Consolas', monospace;");

        Button fastForwardBtn = new Button("⏩ TIME TRAVEL (DAYS)");
        fastForwardBtn.setStyle("-fx-background-color: #004400; -fx-text-fill: #00ff00; -fx-font-family: 'Consolas', monospace; -fx-cursor: hand; -fx-border-color: #00ff00;");

        fastForwardBtn.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog("3");
            dialog.setTitle("Dev Tools: Time Travel");
            dialog.setHeaderText("Fast Forward Time");
            dialog.setContentText("Enter number of days to skip:");

            dialog.getDialogPane().setStyle("-fx-background-color: #1e201c; -fx-border-color: #00ff00; -fx-border-width: 1;");
            dialog.getEditor().setStyle("-fx-background-color: #292b26; -fx-text-fill: #00ff00; -fx-font-family: 'Consolas', monospace;");
            dialog.getDialogPane().lookupAll(".label").forEach(n -> n.setStyle("-fx-text-fill: #00ff00; -fx-font-family: 'Consolas', monospace;"));

            java.util.Optional<String> result = dialog.showAndWait();

            result.ifPresent(daysStr -> {
                try {
                    int days = Integer.parseInt(daysStr.trim());

                    Clock futureClock = Clock.offset(model.getClock(), Duration.ofDays(days));
                    model.setClock(futureClock);

                    model.cleanExpiredStatuses();

                    showScreen(currentScreen);

                    System.out.println("DEV: Clock shifted " + days + " days forward.");
                } catch (NumberFormatException ex) {
                    System.out.println("DEV: Invalid integer inputted.");
                }
            });
        });

        panel.getChildren().addAll(title, fastForwardBtn);
        return panel;
    }
}
