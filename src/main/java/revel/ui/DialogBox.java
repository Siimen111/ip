package revel.ui;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * A custom JavaFX component that displays a dialog text with an avatar image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image i) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        displayPicture.setImage(i);
        Circle clip = new Circle();
        clip.centerXProperty().bind(displayPicture.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(displayPicture.fitHeightProperty().divide(2));
        clip.radiusProperty().bind(
                javafx.beans.binding.Bindings.min(displayPicture.fitWidthProperty(), displayPicture.fitHeightProperty())
                        .divide(2)
        );
        displayPicture.setClip(clip);
        dialog.getStyleClass().add("dialog-text");
    }

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dialog.setText(text);
        dialog.getStyleClass().add("dialog-text");
    }

    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("app-row");
        dialog.getStyleClass().add("app-msg");
    }

    public static DialogBox getUserDialog(String text) {
        var db = new DialogBox(text);
        db.getStyleClass().add("user-row");
        db.dialog.getStyleClass().add("user-msg");
        db.displayPicture.setManaged(false);
        db.displayPicture.setVisible(false);
        return db;
    }
    private void changeDialogStyle(String commandType) {
        switch (commandType) {
        case "TodoCommand", "DeadlineCommand", "EventCommand":
            dialog.getStyleClass().add("add-label");
            break;
        case "MarkCommand":
            dialog.getStyleClass().add("marked-label");
            break;
        case "UnmarkCommand":
            dialog.getStyleClass().add("unmarked-label");
            break;
        case "DeleteCommand":
            dialog.getStyleClass().add("delete-label");
            break;
        case "ErrorCommand":
            dialog.getStyleClass().add("error-label");
            break;
        default:
            break;
        }
    }
    public static DialogBox getRevelDialog(String text, Image img, String commandType) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType);
        return db;
    }
}
