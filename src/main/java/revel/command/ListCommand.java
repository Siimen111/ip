package revel.command;

import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Lists all active task in the task list.
 */
public class ListCommand extends Command {

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.showTaskList(tasks);
    }
}
