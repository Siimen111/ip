public class DeadlineCommand extends Command {
    private final Parser.DeadlineArgs deadlineArgs;

    public DeadlineCommand(Parser.DeadlineArgs deadlineArgs) {
        this.deadlineArgs = deadlineArgs;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = new Deadline(this.deadlineArgs.description(),
                this.deadlineArgs.byDate());
        tasks.addTask(selectedTask);
        ui.showTaskAdded(selectedTask, tasks.getSize());
        storage.save(tasks);
    }
}
