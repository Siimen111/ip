package revel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import revel.RevelException;

public class TaskTest {
    @Test
    void fromFileString_todoParsesCorrectly() throws Exception {
        Task task = Task.fromFileString("TD | 1 | read book");

        assertInstanceOf(ToDo.class, task);
        assertTrue(task.isDone());
        assertEquals("TD | 1 | read book", task.toFileString());
    }

    @Test
    void fromFileString_deadlineParsesCorrectly() throws Exception {
        Task task = Task.fromFileString("DL | 0 | return book | 2019-12-02T18:00:00");

        assertInstanceOf(Deadline.class, task);
        assertEquals("DL | 0 | return book | 2019-12-02T18:00:00", task.toFileString());
    }

    @Test
    void fromFileString_eventParsesCorrectly() throws Exception {
        Task task = Task.fromFileString("E | 0 | project meeting | 2026-02-20T10:00:00 | 2026-02-20T11:00:00");

        assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | project meeting | 2026-02-20T10:00:00 | 2026-02-20T11:00:00", task.toFileString());
    }

    @Test
    void fromFileString_unknownType_throws() {
        assertThrows(RevelException.class, () -> Task.fromFileString("X | 0 | read book"));
    }

    @Test
    void fromFileString_corruptData_throws() {
        assertThrows(RevelException.class, () -> Task.fromFileString("DL | 0 | return book"));
    }

    @Test
    void fromFileString_invalidDate_throws() {
        assertThrows(RevelException.class, () ->
                Task.fromFileString("DL | 0 | return book | not-a-date"));
    }

    @Test
    void toFileString_roundTrip_preservesEvent() throws Exception {
        Event original = new Event(
                "team sync",
                LocalDateTime.of(2026, 2, 21, 9, 30),
                LocalDateTime.of(2026, 2, 21, 10, 0));
        original.markAsDone();

        Task parsed = Task.fromFileString(original.toFileString());

        assertEquals(original.toFileString(), parsed.toFileString());
        assertTrue(parsed.isDone());
    }
}
