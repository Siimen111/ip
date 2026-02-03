package revel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import revel.Parser;

/**
 * Represents a task with a start and end date.
 */
public class Event extends Task {
    protected final LocalDateTime fromDate;
    protected final LocalDateTime toDate;

    /**
     * Creates an event task.
     *
     * @param description Task description.
     * @param fromDate Start date-time.
     * @param toDate End date-time.
     */
    public Event(String description, LocalDateTime fromDate, LocalDateTime toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Returns a user-friendly representation of the event task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + Parser.formatForUser(this.fromDate)
                + " to: " + Parser.formatForUser(this.toDate) + ")";
    }

    /**
     * Returns the file storage representation of this task.
     *
     * @return Serialized task string.
     */
    @Override
    public String toFileString() {
        return "E | " + (isDone ? 1 : 0) + " | " + description + " | "
                + fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " | "
                + toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
