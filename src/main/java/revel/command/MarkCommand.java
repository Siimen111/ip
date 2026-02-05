package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Marks a task in the task list as completed.
 * <p>
 * The task to mark is identified by a task number provided in {@code argsLine}
 * (e.g., {@code "2"} for the second task). After marking the task, this command
 * attempts to save the updated task list to disk.
 * </p>
 */
public class MarkCommand extends Command {
    private final String argsLine;

    public MarkCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.getTaskForMarking(this.argsLine);
        if (selectedTask.isDone()) {
            return ui.showTaskAlreadyMarked(selectedTask);
        }

        selectedTask.markAsDone();
        String response = ui.showTaskMarked(selectedTask);
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            response += "\n" + ui.showSaveWarning(e.getMessage());
        }
        return response;
    }
}
