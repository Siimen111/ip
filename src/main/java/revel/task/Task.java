package revel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task in the task list.
 */
public abstract class Task {
    protected final String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Parses a task from its file storage representation.
     *
     * @param line Serialized task string.
     * @return Parsed task instance.
     */
    public static Task fromFileString(String line) {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        String type = parts[0];
        boolean isDone = parts[1].equals("1");
        String desc = parts[2];

        Task task;
        switch (type) {
        case "TD":
            task = new ToDo(desc);
            break;
        case "DL":
            LocalDateTime byDate = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            task = new Deadline(desc, byDate);
            break;
        case "E":
            LocalDateTime fromDate = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime toDate = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            task = new Event(desc, fromDate, toDate);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type:" + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns the task description.
     *
     * @return Description text.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the completion status icon.
     *
     * @return "X" if done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not completed.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the user-facing representation of the task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Returns the file storage representation of this task.
     *
     * @return Serialized task string.
     */
    public abstract String toFileString();
}
