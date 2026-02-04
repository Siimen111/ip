package revel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import revel.RevelException;

public class TaskListTest {

    @Test
    void addTask_increasesSizeAndRetrievable() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));

        assertEquals(1, list.getSize());
        assertEquals("[T][ ] read book", list.get(0).toString());
    }

    @Test
    void getTask_invalidNumber_throws() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));

        assertThrows(RevelException.class, () -> list.getTask("2"));
    }

    @Test
    void markTask_marksSelectedTask() throws Exception {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));

        Task marked = list.markTask("1");
        assertTrue(marked.toString().contains("[X]"));
    }

    @Test
    void markTask_emptyList_throws() {
        TaskList list = new TaskList();
        assertThrows(RevelException.class, () -> list.markTask("1"));
    }

    @Test
    void markTask_emptyArgs_throws() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));
        assertThrows(RevelException.class, () -> list.markTask(""));
    }

    @Test
    void unmarkTask_unmarksSelectedTask() throws Exception {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));
        list.markTask("1");

        Task unmarked = list.unmarkTask("1");
        assertTrue(unmarked.toString().contains("[ ]"));
    }

    @Test
    void deleteTask_removesTask() throws Exception {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));
        list.addTask(new ToDo("write report"));

        Task deleted = list.deleteTask("1");
        assertEquals("[T][ ] read book", deleted.toString());
        assertEquals(1, list.getSize());
        assertEquals("[T][ ] write report", list.get(0).toString());
    }

    @Test
    void deleteTask_emptyList_throws() {
        TaskList list = new TaskList();
        assertThrows(RevelException.class, () -> list.deleteTask("1"));
    }

    @Test
    void findTasks_caseInsensitive_matchesDescriptions() throws Exception {
        TaskList list = new TaskList();
        list.addTask(new ToDo("Read Book"));
        list.addTask(new ToDo("write report"));

        TaskList results = list.findTasks("read");
        assertEquals(1, results.getSize());
        assertEquals("[T][ ] Read Book", results.get(0).toString());
    }

    @Test
    void findTasks_emptyKeyword_throws() {
        TaskList list = new TaskList();
        list.addTask(new ToDo("read book"));

        assertThrows(RevelException.class, () -> list.findTasks("  "));
    }
}
