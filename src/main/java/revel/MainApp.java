package revel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import revel.ui.MainWindow;

/**
 * Entry point for the Revel JavaFX application.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = loader.load();

        MainWindow controller = loader.getController();
        controller.setRevel(new Revel("data/tasks.txt"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Revel");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
