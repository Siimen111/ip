package revel.ui;

import java.io.InputStream;

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
    private static final String REVEL_IMAGE_FILEPATH = "images/revel.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Revel revel;
    private final Image revelImage = loadRevelImage();

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Loads Revel's avatar image from the classpath.
     * Returns the image if it can be read, or {@code null} if the resource is missing
     * or cannot be loaded so the UI can continue without crashing.
     *
     * @return The loaded avatar image, or {@code null} if unavailable.
     */
    private Image loadRevelImage() {
        return loadImageFromClasspath(this.getClass().getClassLoader(), REVEL_IMAGE_FILEPATH);
    }

    static Image loadImageFromClasspath(ClassLoader classLoader, String resourcePath) {
        if (classLoader == null || resourcePath == null || resourcePath.isBlank()) {
            return null;
        }
        try (InputStream imageStream = classLoader.getResourceAsStream(resourcePath)) {
            if (imageStream == null) {
                return null;
            }
            return new Image(imageStream);
        } catch (Exception e) {
            return null;
        }
    }

    /** Injects the Revel instance */
    public void setRevel(Revel r) {
        revel = r;
        dialogContainer.getChildren().add(
                DialogBox.getRevelDialog(revel.getIntroMessage(), revelImage, "HelloCommand")
        );
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
        String commandType = revel.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getRevelDialog(response, revelImage, commandType)
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
