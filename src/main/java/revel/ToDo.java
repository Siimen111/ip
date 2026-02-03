package revel;

import revel.task.Task;

/**
 * Represents a todo task without date constraints.
 */
public class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns a user-friendly representation of the todo task.
     *
     * @return Formatted task string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the file storage representation of this task.
     *
     * @return Serialized task string.
     */
    @Override
    public String toFileString() {
        return "TD | " + (isDone ? 1 : 0) + " | " + description;
    }
}
