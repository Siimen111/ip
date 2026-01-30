import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String INTRO_LINE = """
                ____________________________________________________________
                 Hello! I'm Revel
                 What can I do for you?
                ____________________________________________________________
                """;
    Scanner sc;

    public Ui() {
        sc = new Scanner(System.in);
    }

    public void showIntro() {
        System.out.println(INTRO_LINE);
    }

    public String readCommand() {
        return sc.nextLine().trim();
    }

    public void showBye() {
        System.out.println(LINE + "\n Bye. Hope to see you again soon!\n" + LINE);
    }

    public void showError(String message) {
        System.out.println(LINE + "\n " + message + "\n " + LINE);
    }

    public void showHelp(String helpText) {
        System.out.println("Available Commands: " + helpText);
    }

    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        IntStream.range(0, tasks.size()).mapToObj(i -> (i + 1) + "." + tasks.get(i).toString()).forEach(System.out::println);
        System.out.println(LINE);
    }

    public void showTaskAdded(Task task, int itemCount) {
        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + itemCount + " tasks in the list.");
        System.out.println(LINE);
    }

    public void showTaskMarked(Task task) {
        System.out.println(LINE + "\n" + " Nice! I've marked this task as done:\n  "
                + task + "\n" + LINE);
    }

    public void showTaskUnMarked(Task task) {
        System.out.println(LINE + "\n" + " OK, I've marked this task as not done yet:\n  "
                + task + "\n" + LINE);
    }

    public void showTaskDeleted(Task task, int remainingItemCount) {
        System.out.println("____________________________________________________________");
        System.out.println(" Got it. I've removed this task:");
        System.out.println(task.toString());
        System.out.println("Now you have " + remainingItemCount + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    public void showSaveWarning(String message) {
        String indent = "____________________________________________________________";
        System.out.println(indent);
        System.out.println(" Warning: could not save tasks to disk: " + message);
        System.out.println(indent);
    }

    public void close() {
        sc.close();
    }
}
