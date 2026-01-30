package revel;

import revel.task.Task;

public class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return "TD | " + (isDone ? 1 : 0) + " | " + description;
    }
}
