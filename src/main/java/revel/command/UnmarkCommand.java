package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;


/**
 * Unmarks a task in the task list as completed.
 * <p>
 * The task to i,mark is identified by a task number provided in {@code argsLine}
 * (e.g., {@code "2"} for the second task). After unmarking the task, this command
 * attempts to save the updated task list to disk.
 * </p>
 */
public class UnmarkCommand extends Command {
    private final String argsLine;

    public UnmarkCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.unmarkTask(this.argsLine);
        String response = ui.showTaskUnmarked(selectedTask);
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            response += "\n" + ui.showSaveWarning(e.getMessage());
        }
        return response;
    }
}
