package revel.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import javafx.scene.image.Image;

public class MainWindowTest {
    @Test
    void loadImageFromClasspath_existingImage_returnsImage() {
        Image image = MainWindow.loadImageFromClasspath(
                MainWindow.class.getClassLoader(), "images/revel.png");

        assertNotNull(image);
        assertFalse(image.isError());
    }

    @Test
    void loadImageFromClasspath_missingImage_returnsNull() {
        Image image = MainWindow.loadImageFromClasspath(
                MainWindow.class.getClassLoader(), "images/does-not-exist.png");

        assertNull(image);
    }

    @Test
    void loadImageFromClasspath_invalidInputs_returnsNull() {
        assertNull(MainWindow.loadImageFromClasspath(null, "images/revel.png"));
        assertNull(MainWindow.loadImageFromClasspath(MainWindow.class.getClassLoader(), null));
        assertNull(MainWindow.loadImageFromClasspath(MainWindow.class.getClassLoader(), " "));
    }
}
