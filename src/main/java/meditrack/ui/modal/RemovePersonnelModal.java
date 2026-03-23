package meditrack.ui.modal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import meditrack.logic.commands.personnel.RemovePersonnelCommand;

/**
 * Confirmation modal shown before removing a personnel member.
 *
 * <p>Returns a {@link RemovePersonnelCommand} on OK,
 * or {@link java.util.Optional#empty()} on Cancel.
 */
public class RemovePersonnelModal extends Dialog<RemovePersonnelCommand> {

    private final String personnelName;
    private final int oneBasedIndex;

    public RemovePersonnelModal(String personnelName, int oneBasedIndex) {
        this.personnelName = personnelName;
        this.oneBasedIndex = oneBasedIndex;
        setTitle("Remove Personnel");
        setHeaderText("Confirm Removal");
        buildContent();
        wireResultConverter();
    }

    private void buildContent() {
        Label msg = new Label(
                "Are you sure you want to remove:\n\n"
                        + "  #" + oneBasedIndex + "  —  " + personnelName + "\n\n"
                        + "This action cannot be undone.");
        msg.setWrapText(true);

        VBox content = new VBox(msg);
        content.setPadding(new Insets(16, 24, 8, 24));

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText("Remove");
        okBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; "
                + "-fx-font-weight: bold;");
    }

    private void wireResultConverter() {
        setResultConverter(buttonType ->
                buttonType == ButtonType.OK
                        ? new RemovePersonnelCommand(oneBasedIndex)
                        : null);
    }
}