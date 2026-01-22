import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.ArrayList;

public class Revel {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storedTasks = new ArrayList<>();
        int itemCount = 0;
        boolean exitLoop = false;
        String allCommands = "help, list, todo, event, deadline, mark, unmark, exit, bye";
        String intro = """
                ____________________________________________________________
                 Hello! I'm Revel
                 What can I do for you?
                ____________________________________________________________
                """;
        String indent = "____________________________________________________________";
        System.out.println(intro);

        while (true) {
            String input = sc.nextLine().trim();
            try {
                if (input.isEmpty()) {
                    throw new RevelException(("Please enter a command. Type help for a list of commands available to you."));
                }

                String[] parts = input.split("\\s+", 2);
                String command = parts[0];
                String argsLine = (parts.length == 2) ? parts[1].trim() : "";

                switch (command) {
                    case "bye", "exit" -> {
                        System.out.println(indent + "\n Bye. Hope to see you again soon!\n" + indent);
                        exitLoop = true;
                    }
                    case "list" -> {
                        System.out.println(indent);
                        System.out.println("Here are the tasks in your list:");
                        IntStream.range(0, itemCount).mapToObj(i -> (i + 1) + "." + storedTasks.get(i).toString()).forEach(System.out::println);
                        System.out.println(indent);
                        continue;
                    }
                    case "todo" -> {
                        if (argsLine.isEmpty()) {
                            throw new RevelException("Sorry, but the description of todo cannot be empty.\n" +
                                    "Usage: todo <description>");
                        }
                        Task selectedTask = new ToDo(argsLine);
                        storedTasks.add(selectedTask);
                        itemCount++;
                        printTask(selectedTask, itemCount);
                        continue;
                    }
                    case "deadline" -> {
                        if (argsLine.isEmpty()) {
                            throw new RevelException("Sorry, but the description of deadline cannot be empty.\n" +
                                    "Usage: deadline <description> /by <date/time>");
                        }
                        if (!argsLine.contains("/by")) {
                            throw new RevelException("Missing /by.\n" +
                                    "Usage: deadline <description> /by <date/time>");
                        }
                        String taskDesc = trimSubstringLeft(argsLine, "/by");
                        String dateTime = trimSubstringRight(argsLine, "/by");
                        if (taskDesc.isEmpty() || dateTime.isEmpty()) {
                            throw new RevelException("Sorry, but the format used is invalid.\n" +
                                    "Usage: deadline <description> /by <date/time>");
                        }
                        Task selectedTask = new Deadline(taskDesc, dateTime);
                        storedTasks.add(selectedTask);
                        itemCount++;
                        printTask(selectedTask, itemCount);
                        continue;
                    }
                    case "event" -> {
                        if (argsLine.isEmpty()) {
                            throw new RevelException("Sorry, but the description of event cannot be empty.\n" +
                                    "Usage: event <description> /from <start date> /to <end date>");
                        }
                        if (!argsLine.contains("/from") || !argsLine.contains("/to")) {
                            throw new RevelException("Sorry, but the format used is invalid: Missing /from or /to.\n" +
                                    "Usage: event <description> /from <start date> /to <end date>");
                        }

                        int fromPos = argsLine.indexOf("/from");
                        int toPos = argsLine.indexOf("/to");

                        if (toPos < fromPos) {
                            throw new RevelException("Sorry, but the format used is invalid: '/from' must come before '/to'.\n" +
                                    "Usage: event <description> /from <start date> /to <end date>");
                        }
                        String taskDesc = trimSubstringLeft(argsLine, "/from");
                        String startDate = trimSubstring(argsLine, "/from", "/to");
                        String endDate = trimSubstringRight(argsLine, "/to");
                        if (taskDesc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                            throw new RevelException("Sorry, but the format used is invalid: one or more arguments are missing\n" +
                                    "Usage: event <description> /from <start date> /to <end date>");
                        }
                        Task selectedTask = new Event(taskDesc, startDate, endDate);
                        storedTasks.add(selectedTask);
                        itemCount++;
                        printTask(selectedTask, itemCount);
                        continue;
                    }
                    case "mark" -> {
                        Task selectedTask = markTask(itemCount, input, storedTasks);
                        System.out.println(indent + "\n" + " Nice! I've marked this task as done:\n  "
                                + selectedTask + "\n" + indent);
                        continue;
                    }
                    case "unmark" -> {
                        Task selectedTask = unmarkTask(itemCount, input, storedTasks);
                        System.out.println(indent + "\n" + " OK, I've marked this task as not done yet:\n  "
                                + selectedTask + "\n" + indent);
                        continue;
                    }
                    case "delete" -> {

                    }
                    case "help" -> {
                        System.out.println("Available Commands: " + allCommands);
                        continue;
                    }
                    default -> throw new RevelException("Sorry! I am unable to assist you with that.\n" +
                            "Type 'help' for a list of commands available to you.");
                }
            } catch (RevelException e) {
                System.out.println(indent + "\n " + e.getMessage() + "\n" + indent);
            }
            if (exitLoop) {
                    break;
                }

        }
        sc.close();
    }

    private static Task markTask(int itemCount, String input, ArrayList<Task> storedTasks) throws RevelException {
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be marked.\n" +
                    "Add a task and try again.");
        }

        if (input.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n" +
                    "Usage: mark <number>");
        }
        int selectedNumber = Integer.parseInt(input.split(" ")[1]);
        if (selectedNumber > itemCount) {
            throw new RevelException("Sorry, but the number you selected is not in the list.\n" +
                    "Please try another number.");
        }
        Task selectedTask = storedTasks.get(selectedNumber - 1);
        selectedTask.markAsDone();
        return selectedTask;
    }

    private static Task unmarkTask(int itemCount, String input, ArrayList<Task> storedTasks) throws RevelException {
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be unmarked.\n" +
                    "Add a task and try again.");
        }
        if (input.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n" +
                    "Usage: unmark <number>");
        }
        int selectedNumber = Integer.parseInt(input.split(" ")[1]);
        if (selectedNumber > itemCount) {
            throw new RevelException("Sorry, but the number you selected is not in the list.\n" +
                    "Please try another number.");
        }
        Task selectedTask = storedTasks.get(selectedNumber - 1);
        selectedTask.markAsUndone();
        return selectedTask;
    }

    private static void printTask(Task task, int itemCount) {
        String indent = "____________________________________________________________";
        System.out.println(indent);
        System.out.println("Got it. I've added this task: ");
        System.out.println(task);
        System.out.println("Now you have " + (itemCount) + " tasks in the list.");
        System.out.println(indent);
    }

    private static String trimSubstringLeft(String str, String delimiter) {
        return str.substring(0, str.indexOf(delimiter)).trim();
    }

    private static String trimSubstringRight(String str, String delimiter) {
        return str.substring(str.indexOf(delimiter) + delimiter.length()).trim();
    }

    public static String trimSubstring(String str, String startDelimiter, String endDelimiter) {
        int start = str.indexOf(startDelimiter) + startDelimiter.length();
        int end = str.indexOf(endDelimiter, start);
        return str.substring(start, end).trim();
    }
}
