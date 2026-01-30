package revel.command;

import revel.Parser;
import revel.RevelException;
import revel.storage.Storage;
import revel.task.Deadline;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class DeadlineCommand extends Command {
    private final Parser.DeadlineArgs deadlineArgs;

    public DeadlineCommand(Parser.DeadlineArgs deadlineArgs) {
        this.deadlineArgs = deadlineArgs;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = new Deadline(this.deadlineArgs.description(),
                this.deadlineArgs.byDate());
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }
}
