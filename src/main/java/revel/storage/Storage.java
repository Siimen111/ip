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
 * Handles persistence of tasks to and from the local filesystem.
 * <p>
 * Tasks are stored as one task per line in a text file. The encoding/decoding format
 * is defined by {@link Task#toFileString()} and {@link Task#fromFileString(String)}.
 * </p>
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage handler that reads from and writes to the given relative file path.
     *
     * @param relativePath Relative path to the save file (e.g., {@code "data/tasks.txt"}).
     */
    public Storage(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

    /**
     * Loads tasks from disk.
     * <p>
     * If the save file does not exist, an empty list is returned.
     * </p>
     *
     * @return A list of tasks loaded from the save file.
     * @throws RevelException If an I/O error occurs while reading the save file, or if task lines
     *                        cannot be parsed into valid {@link Task} objects.
     */
    public List<Task> load() throws RevelException {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                tasks.add(Task.fromFileString(line));
            }
            return tasks;
        } catch (IOException e) {
            throw new RevelException("Unable to load tasks from file: " + filePath);
        }
    }

    /**
     * Loads tasks from disk.
     * <p>
     * If the save file does not exist, an empty list is returned.
     * </p>
     *
     * @return A list of tasks loaded from the save file.
     * @throws RevelException If an I/O error occurs while reading the save file, or if task lines
     *                        cannot be parsed into valid {@link Task} objects.
     */
    public void save(TaskList tasks) throws RevelException {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();

            for (Task t : tasks.getTaskList()) {
                lines.add(t.toFileString());
            }

            Files.write(filePath, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RevelException("Unable to save tasks to disk.");
        }
    }

}
