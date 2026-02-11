package revel;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import revel.core.Revel;
import revel.ui.MainWindow;

/**
 * Entry point for the Revel JavaFX application.
 */
public class MainApp extends Application {

    private static final int MIN_WINDOW_HEIGHT = 220;
    private static final int MIN_WINDOW_WIDTH = 417;
    private static final String DATA_DIR = "data/";
    private final Revel revel = new Revel(DATA_DIR);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Revel Chatbot");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setRevel(revel);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Startup Error");
            alert.setHeaderText("Unable to start Revel");
            alert.setContentText("Failed to load MainWindow.fxml.\n" + e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }


    }

    public static void main(String[] args) {
        launch(args);
    }
}
