package revel;

import revel.command.Command;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Main application entry point and runtime loop for Revel.
 */
public class Revel {

    private final Ui ui;
    private final Storage storage;
    private TaskList storedTasks;

    /**
     * Creates a Revel instance using the given storage file path.
     *
     * @param filePath Path to the task storage file.
     */
    public Revel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            storedTasks = new TaskList(storage.load()); // <-- actually populate your in-memory list
        } catch (RevelException e) {
            ui.showLoadingError();
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
        new Revel("data/tasks.txt").run();
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
