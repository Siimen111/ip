package revel.command;


import revel.RevelException;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        TaskList foundTasks = tasks.findTasks(keyword);
        ui.showFoundTaskList(foundTasks);
    }
}
