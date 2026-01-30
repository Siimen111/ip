import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Revel {

    private final Ui ui;
    private final Storage storage;
    ArrayList<Task> storedTasks;

    public Revel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        storedTasks = new ArrayList<>();

        try {
            storedTasks.addAll(storage.load()); // <-- actually populate your in-memory list
        } catch (IOException e) {
            // file missing / can't read: start empty
        }
    }

    public void run() {
        ui.showIntro();
        boolean exitLoop = false;

        while (true) {
            String input = ui.readCommand();
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
                        ui.showIntro();
                        continue;
                    }
                    case BYE -> {
                        ui.showBye();
                        exitLoop = true;
                        saveSafely(storage, storedTasks, ui);
                    }

                    case LIST -> {
                        ui.showTaskList(storedTasks);
                        continue;
                    }

                    case TODO -> {
                        if (argsLine.isEmpty()) {
                            throw new RevelException("Sorry, but the description of todo cannot be empty.\n" +
                                    "Usage: todo <description>");
                        }

                        Task selectedTask = new ToDo(argsLine);
                        storedTasks.add(selectedTask);
                        ui.showTaskAdded(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
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
                        LocalDateTime byDate = parseToLocalDateTime(dateTime);

                        Task selectedTask = new Deadline(taskDesc, byDate);
                        storedTasks.add(selectedTask);
                        ui.showTaskAdded(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
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

                        LocalDateTime toDate = parseToLocalDateTime(startDate);
                        LocalDateTime fromDate = parseToLocalDateTime(endDate);
                        Task selectedTask = new Event(taskDesc, toDate, fromDate);
                        storedTasks.add(selectedTask);
                        ui.showTaskAdded(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
                        continue;
                    }

                    case MARK -> {
                        Task selectedTask = markTask(argsLine, storedTasks);
                        ui.showTaskMarked(selectedTask);
                        saveSafely(storage, storedTasks, ui);
                        continue;
                    }

                    case UNMARK -> {
                        Task selectedTask = unmarkTask(argsLine, storedTasks);
                        ui.showTaskUnMarked(selectedTask);
                        saveSafely(storage, storedTasks, ui);
                        continue;
                    }

                    case DELETE -> {
                        Task selectedTask = deleteTask(argsLine, storedTasks);
                        ui.showTaskDeleted(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
                    }

                    case HELP -> {
                        ui.showHelp(Command.helpText());
                        continue;
                    }
                }
            } catch (RevelException e) {
                ui.showError(e.getMessage());
            }
            if (exitLoop) {
                break;
            }

        }
        ui.close();
    }

    public static void main(String[] args) {
        new Revel("data/tasks.txt").run();
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

    private static void saveSafely(Storage storage, ArrayList<Task> storedTasks, Ui ui) {
        try {
            storage.save(storedTasks);
        } catch (IOException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }

    private static final DateTimeFormatter IN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter IN_YMD_HHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter IN_YMD_HH_COLON_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter IN_DMY_HHMM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm"); // example: 2/12/2019 1800

    // For printing
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static LocalDateTime parseToLocalDateTime(String raw) throws RevelException {
        String s = raw.trim();

        // Try date-time formats first
        try { return LocalDateTime.parse(s, IN_YMD_HHMM); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(s, IN_YMD_HH_COLON_MM); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(s, IN_DMY_HHMM); } catch (DateTimeParseException ignored) {}

        // Then try date-only
        try {
            LocalDate d = LocalDate.parse(s, IN_DATE);
            return d.atStartOfDay(); // default time 00:00 if none given
        } catch (DateTimeParseException ignored) {}

        throw new RevelException(
                """
                        Sorry, but your date/time is invalid.
                        Accepted formats:
                          yyyy-MM-dd
                          yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
                          d/M/yyyy HHmm (e.g., 2/12/2019 1800)"""
        );
    }

    public static String formatForUser(LocalDateTime dt) {
        // If you want date-only display when time is 00:00:
        if (dt.getHour() == 0 && dt.getMinute() == 0) {
            return dt.format(OUT_DATE);
        }
        return dt.format(OUT_DATE_TIME);
    }


}
