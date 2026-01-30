package revel.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import revel.ToDo;
import revel.task.Deadline;
import revel.task.TaskList;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
