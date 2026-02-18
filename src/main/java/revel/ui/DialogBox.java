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

    public static DialogBox getUserDialog(String text, Image img) {
        var db = new DialogBox(text, img);
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
