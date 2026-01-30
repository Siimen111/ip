public class EventCommand extends Command {
    private final Parser.EventArgs eventArgs;

    public EventCommand(Parser.EventArgs eventArgs) {
        this.eventArgs = eventArgs;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = new Event(this.eventArgs.description(),
                this.eventArgs.fromDate(), this.eventArgs.toDate());
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        storage.save(tasks);
    }
}
