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
        ui.showLine();
        ui.showIntro();
        ui.showLine();

        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(storedTasks, ui, storage);
                isExit = c.isExit();
            } catch (RevelException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
        ui.close();
    }

    /**
     * Starts the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new Revel("data/tasks.txt").run();
    }

}
