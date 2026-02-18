package revel.command;

import revel.RevelException;
import revel.parser.TaskArgumentParser;
import revel.storage.Storage;
import revel.task.Event;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Adds a {@link Event} task to the task list.
 * <p>
 * The deadline details (description and due date/time) are provided via {@link TaskArgumentParser.EventArgs}.
 * After adding the task, this command attempts to persist the updated task list using {@link Storage}.
 * If saving fails, a warning is shown to the user.
 * </p>
 */
public class EventCommand extends Command {
    private final TaskArgumentParser.EventArgs eventArgs;

    public EventCommand(TaskArgumentParser.EventArgs eventArgs) {
        this.eventArgs = eventArgs;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        Task selectedTask = new Event(this.eventArgs.description(),
                this.eventArgs.fromDate(), this.eventArgs.toDate());
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        String response = ui.showTaskAdded(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            response += "\n" + ui.showSaveWarning(e.getMessage());
        }
        return response;
    }
}
