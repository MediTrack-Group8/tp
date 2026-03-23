package meditrack.ui.sidebar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import meditrack.model.Role;
import meditrack.model.Session;

import java.util.function.Consumer;

/**
 * Sidebar navigation component.
 *
 * <p>Navigation items are the same for all roles, but labels are adjusted:
 * <ul>
 *   <li><b>MEDICAL_OFFICER</b> — "Personnel" (full edit access)</li>
 *   <li><b>FIELD_MEDIC / LOGISTICS_OFFICER</b> — "Personnel (Read-Only)"</li>
 * </ul>
 * All roles can view FIT Personnel and generate the Duty Roster.
 *
 * <p>The {@code navigationHandler} callback receives a {@link Screen} enum value
 * on each nav click. The {@code logoutHandler} is called on logout.
 */
public class Sidebar extends VBox {

    /** Identifies which screen to display in the content area. */
    public enum Screen {
        PERSONNEL,
        FIT_PERSONNEL,
        DUTY_ROSTER
    }

    private final Consumer<Screen> navigationHandler;
    private final Runnable logoutHandler;
    private Button activeButton;

    public Sidebar(Consumer<Screen> navigationHandler, Runnable logoutHandler) {
        this.navigationHandler = navigationHandler;
        this.logoutHandler = logoutHandler;
        buildUi();
    }

    private void buildUi() {
        setPrefWidth(200);
        setMinWidth(180);
        setSpacing(4);
        setPadding(new Insets(16, 8, 16, 8));
        setStyle("-fx-background-color: #1c2b3a;");

        // Brand label
        Label brand = new Label("MediTrack");
        brand.setStyle("-fx-text-fill: white; -fx-font-size: 17px; "
                + "-fx-font-weight: bold; -fx-padding: 0 0 10 8;");

        // Role badge — maps team's Role enum to display text
        Role role = Session.getInstance().getRole();
        String roleText = switch (role) {
            case MEDICAL_OFFICER  -> "Medical Officer";
            case FIELD_MEDIC      -> "Field Medic";
            case LOGISTICS_OFFICER -> "Logistics Officer";
        };
        Label roleBadge = new Label(roleText);
        roleBadge.setMaxWidth(Double.MAX_VALUE);
        roleBadge.setStyle("-fx-background-color: #2e4057; -fx-text-fill: #a0b8cc; "
                + "-fx-font-size: 11px; -fx-padding: 4 8 4 8; -fx-background-radius: 4;");

        // Nav buttons
        String personnelLabel = (role == Role.MEDICAL_OFFICER)
                ? "Personnel" : "Personnel (Read-Only)";
        Button personnelBtn  = navButton(personnelLabel,    Screen.PERSONNEL);
        Button fitBtn        = navButton("FIT Personnel",   Screen.FIT_PERSONNEL);
        Button rosterBtn     = navButton("Duty Roster",     Screen.DUTY_ROSTER);

        // Spacer pushes logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Logout
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #e07070; "
                + "-fx-font-size: 13px; -fx-padding: 9 12 9 12; -fx-cursor: hand; "
                + "-fx-background-radius: 6; -fx-alignment: CENTER_LEFT;");
        logoutBtn.setOnAction(e -> logoutHandler.run());

        getChildren().addAll(
                brand, roleBadge, new Separator(),
                personnelBtn, fitBtn, rosterBtn,
                spacer, new Separator(), logoutBtn);

        activateButton(personnelBtn); // default active
    }

    private Button navButton(String label, Screen target) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(inactiveStyle());
        btn.setOnAction(e -> {
            activateButton(btn);
            navigationHandler.accept(target);
        });
        return btn;
    }

    private void activateButton(Button btn) {
        if (activeButton != null) {
            activeButton.setStyle(inactiveStyle());
        }
        btn.setStyle(activeStyle());
        activeButton = btn;
    }

    private String inactiveStyle() {
        return "-fx-background-color: transparent; -fx-text-fill: #c5d5e4; "
                + "-fx-font-size: 13px; -fx-padding: 9 12 9 12; -fx-cursor: hand; "
                + "-fx-background-radius: 6; -fx-alignment: CENTER_LEFT;";
    }

    private String activeStyle() {
        return "-fx-background-color: #0d6efd; -fx-text-fill: white; "
                + "-fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 9 12 9 12; "
                + "-fx-cursor: hand; -fx-background-radius: 6; -fx-alignment: CENTER_LEFT;";
    }
}