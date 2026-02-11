package revel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import revel.RevelException;


/**
 * Represents a task in the task list.
 */
public abstract class Task {
    private enum TaskType {
        TODO("TD"),
        DEADLINE("DL"),
        EVENT("E");

        final String code;
        TaskType(String code) {
            this.code = code;
        }

        static TaskType fromCode(String code) throws RevelException {
            for (TaskType t : TaskType.values()) {
                if (t.code.equals(code)) {
                    return t;
                }
            }
            throw new RevelException("Unknown task type: " + code);
        }
    }

    private static final String DONE_FLAG = "1";
    private static final String DONE_ICON = "X";
    private static final String NOT_DONE_ICON = " ";
    protected final String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Task description.
     */
    public Task(String description) {
        assert description != null : "description cannot be null";
        assert !description.isBlank() : "description cannot be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Parses a task from its file storage representation.
     *
     * @param line Serialized task string.
     * @return Parsed task instance.
     */
    public static Task fromFileString(String line) throws RevelException {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        String type = parts[0];
        TaskType taskType = TaskType.fromCode(type);
        boolean isDone = parts[1].equals(DONE_FLAG);
        String desc = parts[2];

        Task task;
        switch (taskType) {
        case TODO:
            task = new ToDo(desc);
            break;
        case DEADLINE:
            LocalDateTime byDate = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            task = new Deadline(desc, byDate);
            break;
        case EVENT:
            LocalDateTime fromDate = LocalDateTime.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime toDate = LocalDateTime.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            task = new Event(desc, fromDate, toDate);
            break;
        default:
            throw new RevelException("Unknown task type: " + type);
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
        return (this.isDone ? DONE_ICON : NOT_DONE_ICON);
    }

    /**
     * Returns whether the task is completed.
     *
     * @return True if done.
     */
    public boolean isDone() {
        return this.isDone;
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
