package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class UnmarkCommand extends Command {
    private final String argsLine;

    public UnmarkCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.unmarkTask(this.argsLine);
        ui.showTaskUnmarked(selectedTask);
        storage.save(tasks);
    }
}
