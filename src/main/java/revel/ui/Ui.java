package revel.ui;

import java.util.Scanner;
import java.util.stream.IntStream;

import revel.Parser;
import revel.task.Task;
import revel.task.TaskList;

/**
 * Handles user interaction and console output.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INTRO_LINE = " Hello! I'm Revel\n"
                  + " What can I do for you?";
    final Scanner sc;

    /**
     * Creates a UI instance backed by standard input.
     */
    public Ui() {
        sc = new Scanner(System.in);
    }

    /**
     * Prints a separator line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the introduction message.
     */
    public void showIntro() {
        System.out.println(INTRO_LINE);
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
    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Prints an error message.
     *
     * @param message Error text.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints a loading error message.
     */
    public void showLoadingError() {
        System.out.println(" Loading error occurred! Task List not found!\n"
                + "Creating new empty task list...");
    }

    /**
     * Prints a help message with the supported commands.
     */
    public void showHelp() {
        System.out.println(" Available Commands: " + Parser.helpText());
    }

    /**
     * Prints a list of tasks that match a search keyword.
     *
     * @param tasks Task list to display.
     */
    public void showFoundTaskList(TaskList tasks) {
        System.out.println(" Here are the matching tasks in your list:");
        IntStream.range(0, tasks.getSize())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .forEach(System.out::println);
    }

    /**
     * Prints all tasks in the list.
     *
     * @param tasks Task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        IntStream.range(0, tasks.getSize())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .forEach(System.out::println);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task Added task.
     * @param itemCount Current task count.
     */
    public void showTaskAdded(Task task, int itemCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + itemCount + " tasks in the list.");
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task Marked task.
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:\n  "
                + task);
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task Unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:\n  "
                + task);
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param remainingItemCount Remaining task count.
     */
    public void showTaskDeleted(Task task, int remainingItemCount) {
        System.out.println(" Got it. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + remainingItemCount + " tasks in the list.");
    }

    /**
     * Prints a warning that tasks could not be saved.
     *
     * @param message Error detail.
     */
    public void showSaveWarning(String message) {
        System.out.println(" Warning: could not save tasks to disk: " + message);
    }

    /**
     * Closes the UI and its input stream.
     */
    public void close() {
        sc.close();
    }
}
