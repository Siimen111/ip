package revel.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import revel.RevelException;
import revel.command.ByeCommand;
import revel.command.Command;
import revel.command.CommandWord;
import revel.command.DeadlineCommand;
import revel.command.DeleteCommand;
import revel.command.EventCommand;
import revel.command.FindCommand;
import revel.command.HelloCommand;
import revel.command.HelpCommand;
import revel.command.ListCommand;
import revel.command.MarkCommand;
import revel.command.TodoCommand;
import revel.command.UnmarkCommand;

/**
 * Parses user input into commands and command arguments.
 */
public class Parser {
    // alias -> command words
    private static final Map<String, CommandWord> ALIASES = new LinkedHashMap<>();
    // DateTime Constants
    private static final String MESSAGE_UNKNOWN_COMMAND =
            " Sorry! I am unable to assist you with that.\n"
                    + "Type 'help' for a list of commands available to you.";
    private static final String MESSAGE_EMPTY_TODO =
            " Sorry, but the description of todo cannot be empty.\n"
                    + "Usage: todo <description>";
    private static final String MESSAGE_EMPTY_DEADLINE =
            " Sorry, but the description of deadline cannot be empty.\n"
                    + "Usage: deadline <description> /by <date/time>";
    private static final String MESSAGE_MISSING_BY =
            " Missing /by.\n"
                    + "Usage: deadline <description> /by <date/time>";
    private static final String MESSAGE_EMPTY_EVENT =
            " Sorry, but the description of event cannot be empty.\n"
                    + "Usage: event <description> /from <start date> /to <end date>";
    private static final DateTimeFormatter IN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter IN_YMD_HHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter IN_YMD_HH_COLON_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter IN_DMY_HHMM = DateTimeFormatter
            .ofPattern("d/M/yyyy HHmm"); // example: 2/12/2019 1800

    // For printing
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Record classes for storing parsed task commands
    /**
     * Represents the split input of a command word and its argument line.
     */
    public record ParsedInput(String command, String argsLine) {}

    /**
     * Represents parsed deadline arguments.
     */
    public record DeadlineArgs(String description, LocalDateTime byDate) {}

    /**
     * Represents parsed event arguments.
     */
    public record EventArgs(String description, LocalDateTime fromDate, LocalDateTime toDate) {}

    static {
        // register aliases here
        register(CommandWord.HELLO, "hello", "hi");
        register(CommandWord.BYE, "bye", "exit", "bb");
        register(CommandWord.LIST, "list", "tasks", "ls");
        register(CommandWord.TODO, "todo", "t");
        register(CommandWord.DEADLINE, "deadline", "dl");
        register(CommandWord.EVENT, "event", "evt");
        register(CommandWord.MARK, "mark", "tick");
        register(CommandWord.UNMARK, "unmark", "untick");
        register(CommandWord.DELETE, "delete", "del");
        register(CommandWord.HELP, "help", "h");
        register(CommandWord.FIND, "find");
    }

    private static void register(CommandWord word, String... aliases) {
        for (String a : aliases) {
            String key = a.toLowerCase();
            ALIASES.put(key, word);
        }
    }

    /**
     * Parses a command word token into a supported {@link CommandWord}.
     *
     * @param token Raw token representing a command word.
     * @return Parsed command word.
     * @throws RevelException If the command word is not supported.
     */
    public static CommandWord parseWord(String token) throws RevelException {
        String key = token.trim().toLowerCase();
        CommandWord word = ALIASES.get(key);
        if (word == null) {
            throw new RevelException(MESSAGE_UNKNOWN_COMMAND);
        }
        return word;
    }

    /**
     * Returns a comma-separated list of supported command words and aliases.
     *
     * @return Help text for the supported commands.
     */
    public static String helpText() {
        // unique + stable order (LinkedHashMap preserves insertion order)
        return ALIASES.keySet().stream()
                .distinct()
                .collect(Collectors.joining(", "));
    }

    /**
     * Parses a raw command line into a concrete {@link Command}.
     *
     * @param rawCommand Full command line from user input.
     * @return Parsed command instance.
     * @throws RevelException If parsing fails or arguments are invalid.
     */
    public static Command parse(String rawCommand) throws RevelException {
        ParsedInput parsedInput = parseInput(rawCommand);
        CommandWord commandWord = parseWord(parsedInput.command());
        String argsLine = parsedInput.argsLine();

        switch (commandWord) {
        case HELLO -> {
            return new HelloCommand();
        }

        case BYE -> {
            return new ByeCommand();
        }

        case LIST -> {
            return new ListCommand();
        }

        case TODO -> {
            return new TodoCommand(parseTodo(argsLine));
        }

        case DEADLINE -> {
            DeadlineArgs deadlineArgs = parseDeadline(argsLine);
            return new DeadlineCommand(deadlineArgs);
        }

        case EVENT -> {
            EventArgs eventArgs = parseEvent(argsLine);
            return new EventCommand(eventArgs);
        }

        case MARK -> {
            return new MarkCommand(argsLine);
        }

        case UNMARK -> {
            return new UnmarkCommand(argsLine);
        }

        case DELETE -> {
            return new DeleteCommand(argsLine);
        }

        case HELP -> {
            return new HelpCommand();
        }

        case FIND -> {
            return new FindCommand(argsLine);
        }
        default -> throw new RevelException(MESSAGE_UNKNOWN_COMMAND);
        }
    }


    /**
     * Splits input into a command word and the argument string.
     *
     * @param input Raw user input.
     * @return Parsed input with command and argument line.
     * @throws RevelException If the input is empty.
     */
    public static ParsedInput parseInput(String input) throws RevelException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new RevelException(" Please enter a command. Type help for a list of commands available to you.");
        }

        String[] parts = input.split("\\s+", 2);
        String commandStr = parts[0];
        String argsLine = (parts.length == 2) ? parts[1].trim() : "";

        return new ParsedInput(commandStr, argsLine);
    }

    /**
     * Parses a todo task description.
     *
     * @param argsLine Argument string for a todo command.
     * @return Description for the todo task.
     * @throws RevelException If the description is empty.
     */
    public static String parseTodo(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException(MESSAGE_EMPTY_TODO);
        }
        return argsLine;
    }

    /**
     * Parses deadline task arguments.
     *
     * @param argsLine Argument string for a deadline command.
     * @return Parsed deadline arguments.
     * @throws RevelException If the arguments are invalid.
     */
    public static DeadlineArgs parseDeadline(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException(MESSAGE_EMPTY_DEADLINE);
        }

        if (!argsLine.contains("/by")) {
            throw new RevelException(MESSAGE_MISSING_BY);
        }

        String taskDesc = trimSubstringLeft(argsLine, "/by");
        String rawDateTime = trimSubstringRight(argsLine, "/by");

        if (taskDesc.isEmpty() || rawDateTime.isEmpty()) {
            throw new RevelException(" Sorry, but the format used is invalid.\n"
                    + "Usage: deadline <description> /by <date/time>");
        }
        LocalDateTime byDate = parseToLocalDateTime(rawDateTime);

        return new DeadlineArgs(taskDesc, byDate);
    }

    /**
     * Parses event task arguments.
     *
     * @param argsLine Argument string for an event command.
     * @return Parsed event arguments.
     * @throws RevelException If the arguments are invalid.
     */
    public static EventArgs parseEvent(String argsLine) throws RevelException {
        if (argsLine.isEmpty()) {
            throw new RevelException(MESSAGE_EMPTY_EVENT);
        }

        if (!argsLine.contains("/from") || !argsLine.contains("/to")) {
            throw new RevelException(" Sorry, but the format used is invalid: Missing /from or /to.\n"
                    + "Usage: event <description> /from <start date> /to <end date>");
        }

        int fromPos = argsLine.indexOf("/from");
        int toPos = argsLine.indexOf("/to");

        if (toPos < fromPos) {
            throw new RevelException(" Sorry, but the format used is invalid: '/from' must come before '/to'.\n"
                    + "Usage: event <description> /from <start date> /to <end date>");
        }

        String taskDesc = trimSubstringLeft(argsLine, "/from");
        String startDate = trimSubstring(argsLine, "/from", "/to");
        String endDate = trimSubstringRight(argsLine, "/to");
        if (taskDesc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            throw new RevelException(" Sorry, but the format used is invalid: one or more arguments are missing\n"
                    + "Usage: event <description> /from <start date> /to <end date>");
        }

        LocalDateTime fromDate = parseToLocalDateTime(startDate);
        LocalDateTime toDate = parseToLocalDateTime(endDate);

        return new EventArgs(taskDesc, fromDate, toDate);
    }

    /**
     * Parses a number from an argument string.
     *
     * @param argsLine Argument string containing a number.
     * @return Parsed integer.
     * @throws RevelException If the input is not a number.
     */
    public static int parseNumber(String argsLine) throws RevelException {
        try {
            return Integer.parseInt(argsLine.trim());
        } catch (NumberFormatException e) {
            throw new RevelException(" Sorry, but the task number must be an integer.");
        }
    }

    /**
     * Validates that a task number is within the list bounds.
     *
     * @param taskNumber Task number provided by the user.
     * @param itemCount Total number of tasks.
     * @return Validated task number.
     * @throws RevelException If the number is out of range.
     */
    public static int parseTaskNumber(int taskNumber, int itemCount) throws RevelException {

        if (taskNumber > itemCount || taskNumber <= 0) {
            throw new RevelException(" Sorry, but the number you selected is not in the list.\n"
                    + "Please try another number.");
        }
        return taskNumber;
    }



    private static LocalDateTime parseToLocalDateTime(String raw) throws RevelException {
        String s = raw.trim();

        DateTimeFormatter[] dateTimeFormats = {IN_YMD_HHMM, IN_YMD_HH_COLON_MM, IN_DMY_HHMM};

        // Try date-time formats first
        DateTimeParseException last = null;
        for (DateTimeFormatter f : dateTimeFormats) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException e) {
                last = e; // record the failure (catch is no longer empty)
            }
        }

        // Then try date-only
        try {
            LocalDate d = LocalDate.parse(s, IN_DATE);
            return d.atStartOfDay(); // default time 00:00 if none given
        } catch (DateTimeParseException e) {

            throw new RevelException(
                    """
                             Sorry, but your date/time is invalid.
                            Accepted formats:
                              yyyy-MM-dd
                              yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
                              d/M/yyyy HHmm (e.g., 2/12/2019 1800)"""
            );
        }
    }

    /**
     * Formats a date-time for user display.
     *
     * @param dt Date-time to format.
     * @return Formatted string for output.
     */
    public static String formatForUser(LocalDateTime dt) {
        // If you want date-only display when time is 00:00:
        if (dt.getHour() == 0 && dt.getMinute() == 0) {
            return dt.format(OUT_DATE);
        }
        return dt.format(OUT_DATE_TIME);
    }

    private static String trimSubstringLeft(String str, String delimiter) {
        assert str != null : "str cannot be null";
        assert delimiter != null : "delimiter cannot be null";
        assert str.contains(delimiter) : "delimiter cannot be found";
        return str.substring(0, str.indexOf(delimiter)).trim();
    }

    private static String trimSubstringRight(String str, String delimiter) {
        assert str != null : "str cannot be null";
        assert delimiter != null : "delimiter cannot be null";
        assert str.contains(delimiter) : "delimiter cannot be found";
        return str.substring(str.indexOf(delimiter) + delimiter.length()).trim();
    }

    /**
     * Trims the substring between the given delimiters.
     *
     * @param str Input string.
     * @param startDelimiter Start delimiter.
     * @param endDelimiter End delimiter.
     * @return Trimmed substring between the delimiters.
     */
    public static String trimSubstring(String str, String startDelimiter, String endDelimiter) {
        assert str != null : "str cannot be null";
        assert startDelimiter != null : "delimiter cannot be null";
        assert endDelimiter != null : "delimiter cannot be null";


        int startIndex = str.indexOf(startDelimiter);
        int endIndex = str.indexOf(endDelimiter, startIndex);

        assert startIndex >= 0 : "startDelimiter cannot be found";
        assert endIndex >= 0 : "endDelimiter cannot be found";
        assert endIndex >= startIndex : "endDelimiter index is before startLimiter index";
        int start = startIndex + startDelimiter.length();

        return str.substring(start, endIndex).trim();
    }
}


