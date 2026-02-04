package revel.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import revel.task.ToDo;
import revel.task.Deadline;
import revel.task.TaskList;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_roundTrip_ok() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));
        list.addTask(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));

        storage.save(list);
        TaskList loaded = new TaskList(storage.load());

        assertEquals(2, loaded.getSize());
        assertEquals(list.get(0).toFileString(), loaded.get(0).toFileString());
        assertEquals(list.get(1).toFileString(), loaded.get(1).toFileString());
    }

    @Test
    void load_missingFile_returnsEmptyList() throws Exception {
        Path file = tempDir.resolve("missing.txt");
        Storage storage = new Storage(file.toString());

        List<?> loaded = storage.load();
        assertEquals(0, loaded.size());
    }

    @Test
    void load_ignoresBlankLines() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file,
                "\n"
                + "TD | 0 | read book\n"
                + "\n",
                java.nio.charset.StandardCharsets.UTF_8);
        Storage storage = new Storage(file.toString());

        TaskList loaded = new TaskList(storage.load());
        assertEquals(1, loaded.getSize());
        assertEquals("TD | 0 | read book", loaded.get(0).toFileString());
    }

    @Test
    void save_createsParentDirectories() throws Exception {
        Path file = tempDir.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));

        storage.save(list);
        assertEquals(true, Files.exists(file));
    }
}
