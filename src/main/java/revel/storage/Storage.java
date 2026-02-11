package revel.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import revel.RevelException;
import revel.task.Task;
import revel.task.TaskList;

/**
 * Manages loading and saving tasks to disk.
 */
public class Storage {
    private final Path filePath;

    public Storage(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

    /**
     * Loads tasks from the configured storage file.
     *
     * @return List of tasks (empty if file does not exist).
     * @throws RevelException If the file cannot be read.
     */
    public List<Task> load() throws RevelException {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            return lines.stream()
                    .filter(line -> !line.isBlank())
                    .map(Task::fromFileString)
                    .toList();
        } catch (IOException e) {
            throw new RevelException("Unable to load tasks from file: " + filePath);
        }
    }

    /**
     * Saves tasks to the configured storage file.
     *
     * @param tasks Task list to persist.
     * @throws RevelException If the tasks cannot be saved.
     */
    public void save(TaskList tasks) throws RevelException {
        assert tasks != null : "tasks cannot be null";
        try {
            Files.createDirectories(filePath.getParent());
            // List<String> lines = new ArrayList<>();

            List<String> lines = tasks.getTaskList()
                    .stream()
                    .map(Task::toFileString)
                    .toList();


            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RevelException("Unable to save tasks to disk.");
        }
    }

}
