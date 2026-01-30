public class ByeCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        ui.showBye();
    }


    @Override
    public boolean isExit() {
        return true;
    }

}
