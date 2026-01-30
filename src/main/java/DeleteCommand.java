public class DeleteCommand extends Command {
    private final String argsLine;

    public DeleteCommand(String argsLine) {
        this.argsLine = argsLine;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Task selectedTask = tasks.deleteTask(this.argsLine);
        ui.showTaskDeleted(selectedTask, tasks.getSize());
        storage.save(tasks);
    }
}
