package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Removes a {@link Task} task to the task list.
 * <p>
 * After removing the task, this command attempts to persist the updated task list using {@link Storage}.
 * If saving fails, a warning is shown to the user.
 * </p>
 */
public class DeleteCommand extends Command {
    private final String argsLine;

    public DeleteCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.deleteTask(this.argsLine);
        String response = ui.showTaskDeleted(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            response += "\n" + ui.showSaveWarning(e.getMessage());
        }
        return response;
    }
}
