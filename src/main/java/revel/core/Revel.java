package revel.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import revel.RevelException;
import revel.command.Command;
import revel.parser.Parser;
import revel.storage.AliasStorage;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Main application entry point and runtime loop for Revel.
 */
public class Revel {

    private static final String TASKS_FILE_NAME = "tasks.txt";
    private static final String ALIASES_FILE_NAME = "aliases.json";
    private final Ui ui;
    private final Storage storage;
    private TaskList storedTasks;

    /**
     * Creates a Revel instance using the given data directory.
     *
     * @param dataDir Directory containing the storage files.
     */
    public Revel(String dataDir) {
        Path dataDirPath = Paths.get(dataDir);
        ui = new Ui();
        storage = new Storage(dataDirPath.resolve(TASKS_FILE_NAME));
        AliasStorage aliasStorage = new AliasStorage(dataDirPath.resolve(ALIASES_FILE_NAME));
        Parser.setAliasStorage(aliasStorage);
        try {
            storedTasks = new TaskList(storage.load());
        } catch (RevelException e) {
            System.out.println(ui.showLoadingError());
            storedTasks = new TaskList();
        }
        try {
            Parser.replaceUserAliases(aliasStorage.load());
        } catch (RevelException e) {
            System.out.println(ui.showError(e.getMessage()));
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
        }
        ui.close();
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
