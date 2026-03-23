package meditrack.ui.screen;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;
import meditrack.model.Status;

import java.util.List;

/**
 * FIT Personnel screen — displays only personnel with {@link Status#FIT}.
 *
 * <p>Read-only for all roles. Serves as a quick deployable-headcount reference.
 * The "Deployable: N" badge updates automatically on each {@link #refresh()}.
 */
public class FitPersonnelScreen extends VBox {

    private final ModelManager model;
    private final ObservableList<Personnel> tableData = FXCollections.observableArrayList();
    private final TableView<Personnel> table = new TableView<>(tableData);
    private final Label countBadge = new Label();

    public FitPersonnelScreen(ModelManager model) {
        this.model = model;
        buildUi();
        refresh();
    }

    @SuppressWarnings("unchecked")
    private void buildUi() {
        setSpacing(12);
        setPadding(new Insets(20));

        Label title = new Label("FIT Personnel");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        countBadge.setStyle("-fx-background-color: #198754; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 10;");

        HBox header = new HBox(12, title, countBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        // # column
        TableColumn<Personnel, String> indexCol = new TableColumn<>("#");
        indexCol.setPrefWidth(45);
        indexCol.setCellValueFactory(cd ->
                new SimpleStringProperty(String.valueOf(tableData.indexOf(cd.getValue()) + 1)));

        // Name column
        TableColumn<Personnel, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(280);
        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        // Status column (always FIT by filter, shown for clarity)
        TableColumn<Personnel, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStatus().toString()));

        table.getColumns().addAll(indexCol, nameCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No FIT personnel available."));

        getChildren().addAll(header, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    /** Re-queries the model and refreshes the table and headcount badge. */
    public void refresh() {
        List<Personnel> fit = model.getFilteredPersonnelList(Status.FIT);
        tableData.setAll(fit);
        countBadge.setText("Deployable: " + fit.size());
    }
}