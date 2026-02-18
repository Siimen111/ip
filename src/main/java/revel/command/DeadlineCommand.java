package revel.command;

import revel.RevelException;
import revel.parser.TaskArgumentParser;
import revel.storage.Storage;
import revel.task.Deadline;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Adds a {@link Deadline} task to the task list.
 * <p>
 * The deadline details (description and due date/time) are provided via {@link TaskArgumentParser.DeadlineArgs}.
 * After adding the task, this command attempts to persist the updated task list using {@link Storage}.
 * If saving fails, a warning is shown to the user.
 * </p>
 */
public class DeadlineCommand extends Command {
    private final TaskArgumentParser.DeadlineArgs deadlineArgs;

    public DeadlineCommand(TaskArgumentParser.DeadlineArgs deadlineArgs) {
        this.deadlineArgs = deadlineArgs;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        Task selectedTask = new Deadline(this.deadlineArgs.description(),
                this.deadlineArgs.byDate());
        tasks.addTask(selectedTask);
        String response = ui.showTaskAdded(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            response += "\n" + ui.showSaveWarning(e.getMessage());
        }
        return response;
    }
}
