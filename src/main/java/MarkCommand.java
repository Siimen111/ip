public class MarkCommand extends Command {
    private final String argsLine;

    public MarkCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.markTask(this.argsLine);
        ui.showTaskMarked(selectedTask);
        storage.save(tasks);
    }
}
