package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class MarkCommand extends Command {
    private final String argsLine;

    public MarkCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.markTask(this.argsLine);
        ui.showTaskMarked(selectedTask);
        storage.save(tasks);
    }
}
