import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    protected LocalDateTime byDate;
    public Deadline(String description, LocalDateTime byDate) {
        super(description);
        this.byDate = byDate;
    }

    public LocalDateTime getByDate() {
        return this.byDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + Parser.formatForUser(this.byDate) + ")";
    }

    @Override
    public String toFileString() {
        return "DL | " + (isDone ? 1 : 0) + " | " + description + " | " + byDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
