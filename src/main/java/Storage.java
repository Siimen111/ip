import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Storage {
    private final Path filePath;

    public Storage(String relativePath) {
        this.filePath = Paths.get(relativePath);
    }

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
