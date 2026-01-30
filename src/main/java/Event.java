import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends Task {
    protected LocalDateTime fromDate;
    protected LocalDateTime toDate;

    public Event(String description, LocalDateTime fromDate, LocalDateTime toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDateTime getFromDate() {
        return this.fromDate;
    }

    public LocalDateTime getToDate() {
        return this.toDate;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + Revel.formatForUser(this.fromDate)
                + " to: " + Revel.formatForUser(this.toDate) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + (isDone ? 1 : 0) + " | " + description + " | "
                + fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " | "
                + toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
