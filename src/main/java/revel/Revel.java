package revel;

import revel.command.Command;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

public class Revel {

    private final Ui ui;
    private final Storage storage;
    TaskList storedTasks;

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

    public static void main(String[] args) {
        new Revel("data/tasks.txt").run();
    }

}
