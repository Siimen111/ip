import java.util.ArrayList;


public class Revel {

    private final Ui ui;
    private final Storage storage;
    TaskList storedTasks;

    public Revel(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            storedTasks = new TaskList(storage.load()); // <-- actually populate your in-memory list
        } catch (RevelException e) {
           ui.showLoadingError();
            storedTasks = new TaskList();
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
                    storedTasks.addTask(selectedTask);
                    ui.showTaskAdded(selectedTask, storedTasks.getSize());
                    saveSafely(storage, storedTasks, ui);
                    continue;
                    }

                case DEADLINE -> {
                    Parser.DeadlineArgs d = Parser.parseDeadline(argsLine);
                    Task selectedTask = new Deadline(d.description(), d.byDate());
                    storedTasks.addTask(selectedTask);
                    ui.showTaskAdded(selectedTask, storedTasks.getSize());
                    saveSafely(storage, storedTasks, ui);
                    continue;
                    }

                case EVENT -> {
                    Parser.EventArgs e = Parser.parseEvent(argsLine);
                    Task selectedTask = new Event(e.description(), e.fromDate(), e.toDate());
                    storedTasks.addTask(selectedTask);
                    ui.showTaskAdded(selectedTask, storedTasks.getSize());
                    saveSafely(storage, storedTasks, ui);
                    continue;
                    }

                case MARK -> {
                    Task selectedTask = storedTasks.markTask(argsLine);
                    ui.showTaskMarked(selectedTask);
                    saveSafely(storage, storedTasks, ui);
                    continue;
                    }

                case UNMARK -> {
                    Task selectedTask = storedTasks.unmarkTask(argsLine);
                    ui.showTaskUnMarked(selectedTask);
                    saveSafely(storage, storedTasks, ui);
                    continue;
                    }

                case DELETE -> {
                    Task selectedTask = storedTasks.deleteTask(argsLine);
                    ui.showTaskDeleted(selectedTask, storedTasks.getSize());
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

    private static void saveSafely(Storage storage, TaskList storedTasks, Ui ui) {
        try {
            storage.save(storedTasks);
        } catch (RevelException e) {
            ui.showSaveWarning(e.getMessage());
        }
    }


}
