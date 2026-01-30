package revel.command;

import revel.RevelException;
import revel.ToDo;
import revel.storage.Storage;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class TodoCommand extends Command {
    private final String description;

    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
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
