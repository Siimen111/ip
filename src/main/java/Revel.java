import java.io.IOException;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.ArrayList;

public class Revel {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> storedTasks = new ArrayList<>();
        String intro = """
                ____________________________________________________________
                 Hello! I'm Revel
                 What can I do for you?
                ____________________________________________________________
                """;
        String indent = "____________________________________________________________";
        System.out.println(intro);
        Storage storage = new Storage("data/Revel.txt");
        try {
            storedTasks.addAll(storage.load()); // <-- actually populate your in-memory list
        } catch (IOException e) {
            // file missing / can't read: start empty
        }

        boolean exitLoop = false;
        while (true) {
            String input = sc.nextLine().trim();
            try {
                if (input.isEmpty()) {
                    throw new RevelException(("Please enter a command. Type help for a list of commands available to you."));
                }

                String[] parts = input.split("\\s+", 2);
                String commandStr = parts[0];
                String argsLine = (parts.length == 2) ? parts[1].trim() : "";

                Command cmd = Command.parse(commandStr);
                switch (cmd) {
                    case HELLO -> {
                        System.out.println(intro);
                        continue;
                    }
                    case BYE -> {
                        System.out.println(indent + "\n Bye. Hope to see you again soon!\n" + indent);
                        exitLoop = true;
                        saveSafely(storage, storedTasks);
                    }

                    case LIST -> {
                        System.out.println(indent);
                        System.out.println("Here are the tasks in your list:");
                        IntStream.range(0, storedTasks.size()).mapToObj(i -> (i + 1) + "." + storedTasks.get(i).toString()).forEach(System.out::println);
                        System.out.println(indent);
                        continue;
                    }

                    case TODO -> {
                        if (argsLine.isEmpty()) {
                            throw new RevelException("Sorry, but the description of todo cannot be empty.\n" +
                                    "Usage: todo <description>");
                        }
                        Task selectedTask = new ToDo(argsLine);
                        storedTasks.add(selectedTask);
                        printTask(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks);
                        continue;
                    }

                    case DEADLINE -> {
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
                        printTask(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks);
                        continue;
                    }

                    case EVENT -> {
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
                        printTask(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks);
                        continue;
                    }

                    case MARK -> {
                        Task selectedTask = markTask(argsLine, storedTasks);
                        System.out.println(indent + "\n" + " Nice! I've marked this task as done:\n  "
                                + selectedTask + "\n" + indent);
                        saveSafely(storage, storedTasks);
                        continue;
                    }

                    case UNMARK -> {
                        Task selectedTask = unmarkTask(argsLine, storedTasks);
                        System.out.println(indent + "\n" + " OK, I've marked this task as not done yet:\n  "
                                + selectedTask + "\n" + indent);
                        saveSafely(storage, storedTasks);
                        continue;
                    }

                    case DELETE -> {
                        Task selectedTask = deleteTask(argsLine, storedTasks);
                        System.out.println("____________________________________________________________");
                        System.out.println(" Got it. I've removed this task:");
                        System.out.println(selectedTask.toString());
                        System.out.println("Now you have " + storedTasks.size() + " tasks in the list.");
                        System.out.println("____________________________________________________________");
                        saveSafely(storage, storedTasks);
                    }

                    case HELP -> {
                        System.out.println("Available Commands: " + Command.helpText());
                        continue;
                    }
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



    private static int extractNumber(String argsLine) throws RevelException{
        try {
            return Integer.parseInt(argsLine.trim());
        } catch (NumberFormatException e) {
            throw new RevelException("Sorry, but the task number must be an integer.");
        }
    }

    private static int parseTaskNumber(int taskNumber, int itemCount) throws RevelException {
        if (taskNumber > itemCount || taskNumber <= 0) {
            throw new RevelException("Sorry, but the number you selected is not in the list.\n" +
                    "Please try another number.");
        }
        return taskNumber;
    }

    private static Task getTask(String argsLine, ArrayList<Task> storedTasks) throws RevelException {
        int itemCount = storedTasks.size();
        int selectedNumber = parseTaskNumber(extractNumber(argsLine), itemCount);
        return storedTasks.get(selectedNumber - 1);
    }

    private static Task markTask(String argsLine, ArrayList<Task> storedTasks) throws RevelException {
        int itemCount = storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be marked.\n" +
                    "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n" +
                    "Usage: mark <number>");
        }
        Task selectedTask = getTask(argsLine, storedTasks);
        selectedTask.markAsDone();
        return selectedTask;
    }

    private static Task unmarkTask(String argsLine, ArrayList<Task> storedTasks) throws RevelException {
        int itemCount = storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be unmarked.\n" +
                    "Add a task and try again.");
        }
        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n" +
                    "Usage: unmark <number>");
        }
        Task selectedTask = getTask(argsLine, storedTasks);
        selectedTask.markAsUndone();
        return selectedTask;
    }

    private static Task deleteTask(String argsLine, ArrayList<Task> storedTasks) throws RevelException {
        int itemCount = storedTasks.size();
        if (itemCount == 0) {
            throw new RevelException("Sorry, but there are no tasks to be deleted.\n" +
                    "Add a task and try again.");
        }

        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the task number cannot be empty.\n" +
                    "Usage: delete <number>");
        }

        int selectedNumber = parseTaskNumber(extractNumber(argsLine), itemCount);
        Task selectedTask = getTask(argsLine, storedTasks);
        storedTasks.remove(selectedNumber - 1);
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

    private static void saveSafely(Storage storage, ArrayList<Task> storedTasks) {
        try {
            storage.save(storedTasks);
        } catch (IOException e) {
            String indent = "____________________________________________________________";
            System.out.println(indent);
            System.out.println(" Warning: could not save tasks to disk: " + e.getMessage());
            System.out.println(indent);
        }
    }

}
