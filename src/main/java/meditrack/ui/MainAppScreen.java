package meditrack.ui;

import javafx.scene.layout.*;
import meditrack.model.MediTrack;
import meditrack.model.ModelManager;
import meditrack.model.Session;
import meditrack.storage.StorageManager;
import meditrack.ui.screen.DutyRosterScreen;
import meditrack.ui.screen.FitPersonnelScreen;
import meditrack.ui.screen.PersonnelScreen;
import meditrack.ui.sidebar.Sidebar;
import meditrack.ui.sidebar.Sidebar.Screen;

/**
 * Root application layout wiring the {@link Sidebar} and the content area together.
 *
 * <p>Drop this into {@code Main.showMainAppScreen()} by replacing the placeholder:
 * <pre>
 *     private void showMainAppScreen() {
 *     MainAppScreen mainApp = new MainAppScreen(mediTrack, storageManager, this::showLoginScreen);
 *         primaryStage.setScene(new Scene(mainApp, 900, 620));
 *     }
 * </pre>
 *
 * <p>Role-based home screen: both roles land on the Personnel screen.
 * {@link PersonnelScreen} reads {@link Session} internally to decide editability.
 */
public class MainAppScreen extends HBox {

    private final ModelManager model;
    private final StorageManager storage;
    private final StackPane contentArea = new StackPane();

    // Lazily created — built once, refreshed on each revisit
    private PersonnelScreen personnelScreen;
    private FitPersonnelScreen fitPersonnelScreen;
    private DutyRosterScreen dutyRosterScreen;

    /**
     * @param mediTrack     the shared data store (pass the one loaded from storage)
     * @param logoutCallback called when logout is requested; typically navigates to LoginScreen
     */
    public MainAppScreen(MediTrack mediTrack, StorageManager storage, Runnable logoutCallback) {
        this.model = new ModelManager(mediTrack);
        this.storage = storage;
        setFillHeight(true);

        Sidebar sidebar = new Sidebar(this::showScreen, () -> {
            Session.getInstance().clear();
            logoutCallback.run();
        });

        HBox.setHgrow(contentArea, Priority.ALWAYS);
        contentArea.setStyle("-fx-background-color: #f0f2f5; -fx-padding: 16;");

        getChildren().addAll(sidebar, contentArea);
        showScreen(Screen.PERSONNEL);
    }

    /**
     * Switches the content area to the given screen.
     * Screens are lazily instantiated and refreshed on each switch.
     */
    public void showScreen(Screen screen) {
        contentArea.getChildren().clear();
        switch (screen) {
            case PERSONNEL:
                if (personnelScreen == null) {
                    personnelScreen = new PersonnelScreen(model, storage);
                }
                personnelScreen.refresh();
                contentArea.getChildren().add(personnelScreen);
                break;
            case FIT_PERSONNEL:
                if (fitPersonnelScreen == null) {
                    fitPersonnelScreen = new FitPersonnelScreen(model);
                }
                fitPersonnelScreen.refresh();
                contentArea.getChildren().add(fitPersonnelScreen);
                break;
            case DUTY_ROSTER:
                if (dutyRosterScreen == null) {
                    dutyRosterScreen = new DutyRosterScreen(model);
                }
                contentArea.getChildren().add(dutyRosterScreen);
                break;
        }
    }
}