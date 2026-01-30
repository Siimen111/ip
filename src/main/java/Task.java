public abstract class  Task {
    protected String description;
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
            task = new Deadline(desc, parts[3]);
            break;
        case "E":
            task = new Event(desc, parts[3], parts[4]);
            break;
        default:
            throw new IllegalArgumentException("Unknown task type:" + type);
        }

        if (isDone) {
            task.markAsDone();
        }
        return task;
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
