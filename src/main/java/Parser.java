import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {
    // DateTime Constants
    private static final DateTimeFormatter IN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter IN_YMD_HHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter IN_YMD_HH_COLON_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter IN_DMY_HHMM = DateTimeFormatter.ofPattern("d/M/yyyy HHmm"); // example: 2/12/2019 1800

    // For printing
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Record classes for storing parsed task commands
    public record ParsedInput(Command command, String argsLine) {}
    public record DeadlineArgs(String description, LocalDateTime byDate) {}
    public record EventArgs(String description, LocalDateTime toDate, LocalDateTime fromDate) {}

    static Command parseCommand(String token) throws RevelException {
        String t = token.toLowerCase();

        for (Command c : Command.values()) {
            for (String a : c.getAliases()) {
                if (a.equals(t)) {
                    return c;
                }
            }
        }

        throw new RevelException("Sorry! I am unable to assist you with that.\n" +
                "Type 'help' for a list of commands available to you.");
    }


    public static ParsedInput parseInput(String input) throws RevelException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new RevelException("Please enter a command. Type help for a list of commands available to you.");
        }

        String[] parts = input.split("\\s+", 2);
        String commandStr = parts[0];
        String argsLine = (parts.length == 2) ? parts[1].trim() : "";

        Command cmd = parseCommand(commandStr);
        return new ParsedInput(cmd, argsLine);
    }

    public static String parseTodo(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the description of todo cannot be empty.\n" +
                    "Usage: todo <description>");
        }
        return argsLine;
    }

    public static DeadlineArgs parseDeadline(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the description of deadline cannot be empty.\n" +
                    "Usage: deadline <description> /by <date/time>");
        }

        if (!argsLine.contains("/by")) {
            throw new RevelException("Missing /by.\n" +
                    "Usage: deadline <description> /by <date/time>");
        }

        String taskDesc = trimSubstringLeft(argsLine, "/by");
        String rawDateTime = trimSubstringRight(argsLine, "/by");

        if (taskDesc.isEmpty() || rawDateTime.isEmpty()) {
            throw new RevelException("Sorry, but the format used is invalid.\n" +
                    "Usage: deadline <description> /by <date/time>");
        }
        LocalDateTime byDate = parseToLocalDateTime(rawDateTime);

        return new DeadlineArgs(taskDesc, byDate);
    }

    public static EventArgs parseEvent(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException("Sorry, but the description of event cannot be empty.\n" +
                    "Usage: event <description> /from <start date> /to <end date>");
        }

        if (!argsLine.contains("/from") || !argsLine.contains("/to")) {
            throw new RevelException("Sorry, but the format used is invalid: Missing /from or /to.\n" +
                    "Usage: event <description> /from <start date> /to <end date>");
        }

        int fromPos = argsLine.indexOf("/from");
        int toPos = argsLine.indexOf("/to");

        if (toPos < fromPos) {
            throw new RevelException("Sorry, but the format used is invalid: '/from' must come before '/to'.\n" +
                    "Usage: event <description> /from <start date> /to <end date>");
        }

        String taskDesc = trimSubstringLeft(argsLine, "/from");
        String startDate = trimSubstring(argsLine, "/from", "/to");
        String endDate = trimSubstringRight(argsLine, "/to");
        if (taskDesc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            throw new RevelException("Sorry, but the format used is invalid: one or more arguments are missing\n" +
                    "Usage: event <description> /from <start date> /to <end date>");
        }

        LocalDateTime toDate = parseToLocalDateTime(startDate);
        LocalDateTime fromDate = parseToLocalDateTime(endDate);

        return new EventArgs(taskDesc, fromDate, toDate);
    }

    public static int parseNumber(String argsLine) throws RevelException {
        try {
            return Integer.parseInt(argsLine.trim());
        } catch (NumberFormatException e) {
            throw new RevelException("Sorry, but the task number must be an integer.");
        }
    }

    public static int parseTaskNumber(int taskNumber, int itemCount) throws RevelException {
        if (taskNumber > itemCount || taskNumber <= 0) {
            throw new RevelException("Sorry, but the number you selected is not in the list.\n" +
                    "Please try another number.");
        }
        return taskNumber;
    }



    private static LocalDateTime parseToLocalDateTime(String raw) throws RevelException {
        String s = raw.trim();

        // Try date-time formats first
        try { return LocalDateTime.parse(s, IN_YMD_HHMM); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(s, IN_YMD_HH_COLON_MM); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(s, IN_DMY_HHMM); } catch (DateTimeParseException ignored) {}

        // Then try date-only
        try {
            LocalDate d = LocalDate.parse(s, IN_DATE);
            return d.atStartOfDay(); // default time 00:00 if none given
        } catch (DateTimeParseException ignored) {}

        throw new RevelException(
                """
                        Sorry, but your date/time is invalid.
                        Accepted formats:
                          yyyy-MM-dd
                          yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
                          d/M/yyyy HHmm (e.g., 2/12/2019 1800)"""
        );
    }

    public static String formatForUser(LocalDateTime dt) {
        // If you want date-only display when time is 00:00:
        if (dt.getHour() == 0 && dt.getMinute() == 0) {
            return dt.format(OUT_DATE);
        }
        return dt.format(OUT_DATE_TIME);
    }

    private static String trimSubstringLeft(String str, String delimiter) {
        return str.substring(0, str.indexOf(delimiter)).trim();
    }

    private static String trimSubstringRight(String str, String delimiter) {
        return str.substring(str.indexOf(delimiter) + delimiter.length()).trim();
    }

    public static String trimSubstring(String str, String startDelimiter, String endDelimiter) {
        int start = str.indexOf(startDelimiter) + startDelimiter.length();
        int end = str.indexOf(endDelimiter, start);
        return str.substring(start, end).trim();
    }
}


