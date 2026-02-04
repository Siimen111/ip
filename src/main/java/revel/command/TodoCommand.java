package revel.command;

import revel.RevelException;
import revel.ToDo;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Adds a {@link ToDo} task to the task list.
 * <p>
 * After adding the task, this command attempts to persist the updated task list using {@link Storage}.
 * If saving fails, a warning is shown to the user.
 * </p>
 */
public class TodoCommand extends Command {
    private final String description;

    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        Task selectedTask = new ToDo(this.description);
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
