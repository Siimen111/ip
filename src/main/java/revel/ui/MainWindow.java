package revel.ui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import revel.RevelException;
import revel.command.Command;
import revel.core.Revel;
import revel.parser.Parser;

/**
 * Controller for the main window layout and user interactions.
 */
public class MainWindow extends AnchorPane {
    private static final double EXIT_DELAY_SECONDS = 2.0;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Revel revel;

    private Image userImage = new Image(this.getClass().getClassLoader().getResourceAsStream("images/user.png"));
    private Image revelImage = new Image(this.getClass().getClassLoader().getResourceAsStream("images/revel.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Revel instance */
    public void setRevel(Revel r) {
        revel = r;
    }

    /**
     * Creates two dialog boxes, one echoing user input, and other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        boolean isExit = false;
        try {
            Command c = Parser.parse(input);
            isExit = c.isExit();
        } catch (RevelException e) {
            // Let getResponse handle error messaging.
        }
        String response = revel.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getRevelDialog(response, revelImage)
        );
        userInput.clear();
        if (isExit) {
            userInput.setEditable(false);
            userInput.setDisable(true);
            if (sendButton != null) {
                sendButton.setDisable(true);
            }
            PauseTransition delay = new PauseTransition(Duration.seconds(EXIT_DELAY_SECONDS));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

}
