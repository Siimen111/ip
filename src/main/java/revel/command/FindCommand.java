package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        TaskList foundTasks = tasks.findTasks(keyword);
        return ui.showFoundTaskList(foundTasks);
    }
}
