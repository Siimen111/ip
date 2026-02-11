package revel.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import revel.RevelException;
import revel.command.Command;
import revel.parser.Parser;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Main application entry point and runtime loop for Revel.
 */
public class Revel {

    private final Ui ui;
    private static final String TASKS_FILE_NAME = "tasks.txt";

    private final Storage storage;
    private TaskList storedTasks;
    private final Path dataDir;

    /**
     * Creates a Revel instance using the given data directory.
     *
     * @param dataDir Directory containing the storage files.
     */
    public Revel(String dataDir) {
        this.dataDir = Paths.get(dataDir);
        ui = new Ui();
        storage = new Storage(this.dataDir.resolve(TASKS_FILE_NAME));

        try {
            storedTasks = new TaskList(storage.load()); // <-- actually populate your in-memory list
        } catch (RevelException e) {
            System.out.println(ui.showLoadingError());
            storedTasks = new TaskList();
        }
    }

    /**
     * Runs the main input-processing loop.
     */
    public void run() {

        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parse(fullCommand);
                String response = c.execute(storedTasks, ui, storage);
                System.out.println(response);
                isExit = c.isExit();
            } catch (RevelException e) {
                System.out.println(ui.showError(e.getMessage()));
            }
            ui.close();
        }
    }

    /**
     * Starts the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Revel("data").run();
    }

    /**
     * Generates a response for the user's chat message
     */
    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            return c.execute(storedTasks, ui, storage);
        } catch (RevelException e) {
            return ui.showError(e.getMessage());
        }
    }
}
