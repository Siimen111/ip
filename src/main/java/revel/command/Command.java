package revel.command;

import revel.RevelException;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Represents an executable user command.
 * <p>
 *     Concrete subclasses implement {@link #execute(TaskList, Ui, Storage)} to perform an action,
 *     such as modifying the task list, displaying information to the user, or exiting the program.
 * </p>
 */
public abstract class Command {
    /**
     * Executes this command.
     * <p>
     * Implementations may read and/or modify the given {@code tasks}, print messages via {@code ui},
     * and persist changes using {@code storage} when appropriate.
     * </p>
     *
     * @param tasks   The application's task list.
     * @param ui      The UI used to display messages to the user.
     * @param storage The storage handler used to load/save tasks.
     * @throws RevelException If the command cannot be executed due to invalid input or storage errors.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws RevelException;

    public boolean isExit() {
        return false;
    }
}
