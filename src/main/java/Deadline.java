public class Deadline extends Task {
    protected String byDate;
    public Deadline(String description, String byDate) {
        super(description);
        this.byDate = byDate;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.byDate + ")";
    }
}
