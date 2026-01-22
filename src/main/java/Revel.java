import java.util.Scanner;

public class Revel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Task[] storedTasks = new Task[100];
        int itemCount = 0;
        int selectedNumber;
        Task selectedTask;
        boolean exitLoop = false;
        String intro = """
                ____________________________________________________________
                 Hello! I'm Revel
                 What can I do for you?
                ____________________________________________________________
                """;
        String indent = "____________________________________________________________";
        System.out.println(intro);

        while (true) {
            String input = sc.nextLine();

            switch (input.split(" ")[0]) {
                case "bye":
                    System.out.println(indent + "\n" + " Bye. Hope to see you again soon!\n" + indent);
                    exitLoop = true;
                    break;
                case "list":
                    System.out.println(indent);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < itemCount; i++) {
                        System.out.println((i + 1) + "." + storedTasks[i].toString());
                    }
                    System.out.println(indent);
                    continue;
                case "mark":
                    selectedNumber = Integer.parseInt(input.split(" ")[1]);
                    selectedTask = storedTasks[selectedNumber - 1];
                    selectedTask.markAsDone();
                    System.out.println(indent + "\n" + " Nice! I've marked this task as done:\n  "
                    + selectedTask + "\n" + indent);
                    continue;
                case "unmark":
                    selectedNumber = Integer.parseInt(input.split(" ")[1]);
                    selectedTask = storedTasks[selectedNumber - 1];
                    selectedTask.markAsUndone();
                    System.out.println(indent + "\n" + " OK, I've marked this task as not done yet:\n  "
                                       + selectedTask + "\n" + indent);
                    continue;

                default:
                    Task newTask = new Task(input);
                    storedTasks[itemCount++] = newTask;
                    System.out.println(indent + "\n" + "added: " + input + "\n" + indent);
            }

            if (exitLoop) {
                break;
            }

        }
    }
}
