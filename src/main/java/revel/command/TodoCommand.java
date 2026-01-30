package revel.command;

import revel.Parser;
import revel.RevelException;
import revel.ToDo;
import revel.storage.Storage;
import revel.task.Event;
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
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task selectedTask = new ToDo(this.description);
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }
}
