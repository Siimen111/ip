package revel.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import revel.ToDo;

public abstract class Task {
    protected final String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

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

    public String getDescription() {
        return this.description;
    }

    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsUndone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    public abstract String toFileString();
}
