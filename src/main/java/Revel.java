import java.io.IOException;
import java.util.ArrayList;


public class Revel {

    private final Ui ui;
    private final Storage storage;
    ArrayList<Task> storedTasks;

    public Revel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        storedTasks = new ArrayList<>();

        try {
            // storage.clearForTesting(); // TODO: Remove after testing
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

                Parser.ParsedInput parsed = Parser.parseInput(input);
                Command cmd = parsed.command();
                String argsLine = parsed.argsLine();

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
                        String description = Parser.parseTodo(argsLine);
                        Task selectedTask = new ToDo(description);
                        storedTasks.add(selectedTask);
                        ui.showTaskAdded(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
                        continue;
                    }

                    case DEADLINE -> {
                        Parser.DeadlineArgs d = Parser.parseDeadline(argsLine);
                        Task selectedTask = new Deadline(d.description(), d.byDate());
                        storedTasks.add(selectedTask);
                        ui.showTaskAdded(selectedTask, storedTasks.size());
                        saveSafely(storage, storedTasks, ui);
                        continue;
                    }

                    case EVENT -> {
                        Parser.EventArgs e = Parser.parseEvent(argsLine);
                        Task selectedTask = new Event(e.description(), e.fromDate(), e.toDate());
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


    private static void saveSafely(Storage storage, ArrayList<Task> storedTasks, Ui ui) {
        try {
            storage.save(storedTasks);
        } catch (IOException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }


}
