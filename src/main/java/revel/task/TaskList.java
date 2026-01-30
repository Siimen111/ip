package revel.task;

import java.util.ArrayList;
import java.util.List;

import revel.Parser;
import revel.RevelException;

public class TaskList {
    final ArrayList<Task> storedTasks;

    public TaskList() {
        this.storedTasks = new ArrayList<>();
    }

    public TaskList(List<Task> storedTasks) {
        this.storedTasks = new ArrayList<>();
        this.storedTasks.addAll(storedTasks);
    }

    public List<Task> getTaskList() {
        return this.storedTasks;
    }

    public int getSize() {
        return this.storedTasks.size();
    }

    public Task get(int i) {
        return this.storedTasks.get(i);
    }

    public Task getTask(String argsLine) throws RevelException {
        int itemCount = this.storedTasks.size();
        int selectedNumber = Parser.parseTaskNumber(Parser.parseNumber(argsLine), itemCount);
        return this.storedTasks.get(selectedNumber - 1);
    }

    public void addTask(Task task) {
        this.storedTasks.add(task);
    }

    public Task markTask(String argsLine) throws RevelException {
        int itemCount = this.storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be marked.\n"
                    + "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n"
                    + "Usage: mark <number>");
        }
        Task selectedTask = getTask(argsLine);
        selectedTask.markAsDone();
        return selectedTask;
    }

    public Task unmarkTask(String argsLine) throws RevelException {
        int itemCount = this.storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be unmarked.\n"
                    + "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n"
                    + "Usage: unmark <number>");
        }
        Task selectedTask = getTask(argsLine);
        selectedTask.markAsUndone();
        return selectedTask;
    }

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

}
