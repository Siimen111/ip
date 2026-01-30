package revel.ui;

import java.util.Scanner;
import java.util.stream.IntStream;

import revel.Parser;
import revel.task.Task;
import revel.task.TaskList;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INTRO_LINE = " Hello! I'm Revel\n"
                  + " What can I do for you?";
    final Scanner sc;

    public Ui() {
        sc = new Scanner(System.in);
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public void showIntro() {
        System.out.println(INTRO_LINE);
    }

    public String readCommand() {
        return sc.nextLine().trim();
    }

    public void showBye() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showLoadingError() {
        System.out.println(" Loading error occurred! Task List not found!\n"
                + "Creating new empty task list...");
    }

    public void showHelp() {
        System.out.println(" Available Commands: " + Parser.helpText());
    }

    public void showFoundTaskList(TaskList tasks) {
        System.out.println(" Here are the matching tasks in your list:");
        IntStream.range(0, tasks.getSize()).mapToObj(i -> (i + 1) + "." + tasks.get(i).toString()).forEach(System.out::println);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        IntStream.range(0, tasks.getSize())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i).toString())
                .forEach(System.out::println);
    }

    public void showTaskAdded(Task task, int itemCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + itemCount + " tasks in the list.");
    }

    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:\n  "
                + task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:\n  "
                + task);
    }

    public void showTaskDeleted(Task task, int remainingItemCount) {
        System.out.println(" Got it. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + remainingItemCount + " tasks in the list.");
    }

    public void showSaveWarning(String message) {
        System.out.println(" Warning: could not save tasks to disk: " + message);
    }

    public void close() {
        sc.close();
    }
}
