package revel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import revel.parser.Parser;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    protected final LocalDateTime byDate;

    /**
     * Creates a deadline task.
     *
     * @param description Task description.
     * @param byDate Deadline date-time.
     */
    public Deadline(String description, LocalDateTime byDate) {
        super(description);
        this.byDate = byDate;
    }

    /**
     * Returns a user-friendly representation of the deadline task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + Parser.formatForUser(this.byDate) + ")";
    }

    /**
     * Returns the file storage representation of this task.
     *
     * @return Serialized task string.
     */
    @Override
    public String toFileString() {
        return "DL | " + (isDone ? 1 : 0) + " | " + description + " | "
                + byDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
