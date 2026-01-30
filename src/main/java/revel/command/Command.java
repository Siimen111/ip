package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

public abstract class Command {

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException;

    public boolean isExit() {
        return false;
    }
}
