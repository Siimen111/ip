package revel.command;

import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Prints a goodbye statement and exits the program.
 */
public class ByeCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }


    @Override
    public boolean isExit() {
        return true;
    }

}
