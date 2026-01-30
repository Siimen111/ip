package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class DeleteCommand extends Command {
    private final String argsLine;

    public DeleteCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.deleteTask(this.argsLine);
        ui.showTaskDeleted(selectedTask, tasks.getSize());
        storage.save(tasks);
    }
}
