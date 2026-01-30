package revel.command;

import revel.Parser;
import revel.RevelException;
import revel.storage.Storage;
import revel.task.Event;
import revel.task.Task;
import revel.task.TaskList;
import revel.ui.Ui;

public class EventCommand extends Command {
    private final Parser.EventArgs eventArgs;

    public EventCommand(Parser.EventArgs eventArgs) {
        this.eventArgs = eventArgs;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task selectedTask = new Event(this.eventArgs.description(),
                this.eventArgs.fromDate(), this.eventArgs.toDate());
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        try {
            storage.save(tasks);
        } catch (RevelException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }
}
