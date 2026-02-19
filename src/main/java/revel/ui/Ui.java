package revel.ui;

import java.util.Scanner;
import java.util.stream.IntStream;

import revel.parser.Parser;
import revel.task.Task;
import revel.task.TaskList;

/**
 * Handles user interaction and console output.
 */
public class Ui {
    final Scanner sc;

    /**
     * Creates a UI instance backed by standard input.
     */
    public Ui() {
        sc = new Scanner(System.in);
    }


    /**
     * Prints the introduction message.
     */
    public String showIntro() {
        return joinLines(" Hello! I'm Revel, a friendly assistant.",
                " What can I do for you?");
    }

    /**
     * Reads the next command from the user.
     *
     * @return Trimmed command line.
     */
    public String readCommand() {
        return sc.nextLine().trim();
    }

    /**
     * Prints the farewell message.
     */
    public String showBye() {
        return " Bye. Hope to see you again soon!";
    }

    /**
     * Prints an error message.
     *
     * @param message Error text.
     */
    public String showError(String message) {
        return message;
    }

    private String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Prints a loading error message.
     */
    public String showLoadingError() {
        return joinLines(
                " Loading error occurred! Task List not found!",
                "Creating new empty task list..."
        );
    }

    /**
     * Prints a help message with the supported commands.
     */
    public String showHelp() {
        return " Available Commands: " + Parser.helpText();
    }

    /**
     * Prints a list of tasks that match a search keyword.
     *
     * @param tasks Task list to display.
     */
    public String showFoundTaskList(TaskList tasks) {
        StringBuilder sb = new StringBuilder(" Here are the matching tasks in your list:");
        IntStream.range(0, tasks.getSize())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .forEach(line -> sb.append("\n").append(line));
        return sb.toString();
    }

    /**
     * Prints all tasks in the list.
     *
     * @param tasks Task list to display.
     */
    public String showTaskList(TaskList tasks) {
        if (tasks.getSize() == 0) {
            return " You have no tasks in your list!";
        }
        StringBuilder sb = new StringBuilder(" Here are the tasks in your list:");
        IntStream.range(0, tasks.getSize())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .forEach(line -> sb.append("\n").append(line));
        return sb.toString();
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task Added task.
     * @param itemCount Current task count.
     */
    public String showTaskAdded(Task task, int itemCount) {
        return joinLines(
                " Got it. I've added this task:",
                task.toString(),
                "Now you have " + itemCount + " tasks in the list."
        );
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task Marked task.
     */
    public String showTaskMarked(Task task) {
        return joinLines(
                " Good Job!",
                " I've marked this task as done: ",
                task.toString());
    }

    /**
     * Prints confirmation that a task was already marked as done.
     *
     * @param task Marked task.
     */
    public String showTaskAlreadyMarked(Task task) {
        return joinLines(
                " This task is already marked as done: ",
                task.toString());
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task Unmarked task.
     */
    public String showTaskUnmarked(Task task) {
        return joinLines(
                " OK, I've marked this task as not done yet: ",
                task.toString());
    }

    /**
     * Prints confirmation that a task was already marked as not done.
     *
     * @param task Unmarked task.
     */
    public String showTaskAlreadyUnmarked(Task task) {
        return joinLines(
                " This task is already marked as not done: ",
                task.toString());
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param remainingItemCount Remaining task count.
     */
    public String showTaskDeleted(Task task, int remainingItemCount) {
        return joinLines(
                " Got it. I've removed this task:",
                task.toString(),
                "Now you have " + remainingItemCount + " tasks in the list.");
    }

    /**
     * Prints a warning that tasks could not be saved.
     *
     * @param message Error detail.
     */
    public String showSaveWarning(String message) {
        return " Warning: could not save tasks to disk: " + message;
    }

    /**
     * Closes the UI and its input stream.
     */
    public void close() {
        sc.close();
    }
}
