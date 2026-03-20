package meditrack.ui.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.logic.commands.personnel.GenerateRosterCommand;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;

import java.util.List;

/**
 * Duty Roster screen — generates and displays a randomised roster of FIT personnel.
 *
 * <p>Available to all roles. "Generate Roster" swaps to "Regenerate" after
 * the first successful generation. Each click produces a new shuffle.
 */
public class DutyRosterScreen extends VBox {

    private final ModelManager model;

    private final ListView<String> rosterList = new ListView<>();
    private final Label headlineLabel = new Label("Press \"Generate Roster\" to begin.");
    private final Label errorLabel = new Label();
    private final Button generateBtn = new Button("Generate Roster");
    private final Button regenerateBtn = new Button("Regenerate");

    private boolean hasRoster = false;

    public DutyRosterScreen(ModelManager model) {
        this.model = model;
        buildUi();
    }

    private void buildUi() {
        setSpacing(14);
        setPadding(new Insets(20));

        Label title = new Label("Duty Roster");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        headlineLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        rosterList.setPlaceholder(new Label("No roster generated yet."));

        // Style generate button
        String btnStyle = "-fx-background-color: #0d6efd; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-background-radius: 5;";
        generateBtn.setStyle(btnStyle);
        generateBtn.setOnAction(e -> generateRoster());

        regenerateBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-background-radius: 5;");
        regenerateBtn.setOnAction(e -> generateRoster());
        regenerateBtn.setVisible(false);
        regenerateBtn.setManaged(false);

        HBox btnRow = new HBox(10, generateBtn, regenerateBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(title, headlineLabel, errorLabel, btnRow, rosterList);
        VBox.setVgrow(rosterList, Priority.ALWAYS);
    }

    private void generateRoster() {
        clearError();
        GenerateRosterCommand cmd = new GenerateRosterCommand();
        try {
            CommandResult result = cmd.execute(model);
            List<Personnel> roster = cmd.getLastRoster();

            rosterList.getItems().clear();
            for (int i = 0; i < roster.size(); i++) {
                rosterList.getItems().add((i + 1) + ".  " + roster.get(i).getName());
            }
            headlineLabel.setText("Duty Roster — " + roster.size() + " FIT personnel assigned");

            if (!hasRoster) {
                hasRoster = true;
                generateBtn.setVisible(false);
                generateBtn.setManaged(false);
                regenerateBtn.setVisible(true);
                regenerateBtn.setManaged(true);
            }
        } catch (CommandException ex) {
            showError(ex.getMessage());
            rosterList.getItems().clear();
            headlineLabel.setText("Roster generation failed.");
        }
    }

    private void showError(String msg) {
        errorLabel.setText("⚠  " + msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}