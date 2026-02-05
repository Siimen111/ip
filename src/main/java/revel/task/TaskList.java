package revel.task;

import java.util.ArrayList;
import java.util.List;

import revel.RevelException;
import revel.parser.Parser;

/**
 * Stores and manages a list of tasks.
 */
public class TaskList {
    final ArrayList<Task> storedTasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.storedTasks = new ArrayList<>();
    }

    /**
     * Creates a task list from an existing collection.
     *
     * @param storedTasks Tasks to initialize with.
     */
    public TaskList(List<Task> storedTasks) {
        this.storedTasks = new ArrayList<>();
        this.storedTasks.addAll(storedTasks);
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return Task list.
     */
    public List<Task> getTaskList() {
        return this.storedTasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int getSize() {
        return this.storedTasks.size();
    }

    /**
     * Returns the task at the given index.
     *
     * @param i Zero-based index.
     * @return Task at the index.
     */
    public Task get(int i) {
        return this.storedTasks.get(i);
    }

    /**
     * Returns the task referenced by the argument string.
     *
     * @param argsLine Argument string containing a task number.
     * @return Selected task.
     * @throws RevelException If the task number is invalid.
     */
    public Task getTask(String argsLine) throws RevelException {
        int itemCount = this.storedTasks.size();
        int selectedNumber = Parser.parseTaskNumber(Parser.parseNumber(argsLine), itemCount);
        return this.storedTasks.get(selectedNumber - 1);
    }

    private Task getTaskForUpdate(String argsLine, String actionPast, String actionCommand) throws RevelException {
        int itemCount = this.storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be " + actionPast + ".\n"
                    + "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n"
                    + "Usage: " + actionCommand + " <number>");
        }
        return getTask(argsLine);
    }

    /**
     * Returns the task to be marked.
     *
     * @param argsLine Argument string containing a task number.
     * @return Selected task.
     * @throws RevelException If the task number is invalid.
     */
    public Task getTaskForMarking(String argsLine) throws RevelException {
        return getTaskForUpdate(argsLine, "marked", "mark");
    }

    /**
     * Returns the task to be unmarked.
     *
     * @param argsLine Argument string containing a task number.
     * @return Selected task.
     * @throws RevelException If the task number is invalid.
     */
    public Task getTaskForUnmarking(String argsLine) throws RevelException {
        return getTaskForUpdate(argsLine, "unmarked", "unmark");
    }

    /**
     * Adds a task to the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        this.storedTasks.add(task);
    }

    /**
     * Marks a task as completed.
     *
     * @param argsLine Argument string containing a task number.
     * @return Marked task.
     * @throws RevelException If the task number is invalid.
     */
    public Task markTask(String argsLine) throws RevelException {
        Task selectedTask = getTaskForMarking(argsLine);
        selectedTask.markAsDone();
        return selectedTask;
    }

    /**
     * Marks a task as not completed.
     *
     * @param argsLine Argument string containing a task number.
     * @return Unmarked task.
     * @throws RevelException If the task number is invalid.
     */
    public Task unmarkTask(String argsLine) throws RevelException {
        Task selectedTask = getTaskForUnmarking(argsLine);
        selectedTask.markAsUndone();
        return selectedTask;
    }

    /**
     * Deletes a task from the list.
     *
     * @param argsLine Argument string containing a task number.
     * @return Deleted task.
     * @throws RevelException If the task number is invalid.
     */
    public Task deleteTask(String argsLine) throws RevelException {
        int itemCount = this.storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be deleted.\n"
                    + "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n"
                    + "Usage: delete <number>");
        }

        int selectedNumber = Parser.parseTaskNumber(Parser.parseNumber(argsLine), itemCount);
        Task selectedTask = getTask(argsLine);
        this.storedTasks.remove(selectedNumber - 1);
        return selectedTask;
    }

    /**
     * Returns tasks whose string representation contains the given keyword (case-insensitive).
     *
     * @param keyword The search keyword.
     * @return A list of matching tasks (may be empty).
     * @throws RevelException If the keyword is empty.
     */
    public TaskList findTasks(String keyword) throws RevelException {
        String k = keyword.trim();
        if (k.isEmpty()) {
            throw new RevelException("Sorry, but the keyword to find cannot be empty.\n"
                    + "Usage: find <keyword>");
        }

        String key = k.toLowerCase();
        List<Task> matches = new ArrayList<>();

        for (Task t : storedTasks) {
            String lock = t.getDescription().toLowerCase();
            if (lock.contains(key)) {
                matches.add(t);
            }
        }
        return new TaskList(matches);
    }

}
