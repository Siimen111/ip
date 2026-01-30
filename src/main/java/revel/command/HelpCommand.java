package revel.command;

import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Prints a help statement displaying all available commands (including macros)
 * in the console.
 */
public class HelpCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
