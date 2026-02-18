package revel.parser;

import java.time.LocalDateTime;
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
import revel.parser.util.ParseStringUtils;

/**
 * Parses user input into commands and command arguments.
 */
public class Parser extends AliasParser {
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
        case ALIAS -> {
            return parseAliasCommand(argsLine);
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

        String taskDesc = ParseStringUtils.trimSubstringLeft(argsLine, "/by");
        String rawDateTime = ParseStringUtils.trimSubstringRight(argsLine, "/by");

        if (taskDesc.isEmpty() || rawDateTime.isEmpty()) {
            throw new RevelException(" Sorry, but the format used is invalid.\n"
                    + "Usage: deadline <description> /by <date/time>");
        }
        LocalDateTime byDate = DateTimeParser.parseToLocalDateTime(rawDateTime);

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

        String taskDesc = ParseStringUtils.trimSubstringLeft(argsLine, "/from");
        String startDate = ParseStringUtils.trimSubstring(argsLine, "/from", "/to");
        String endDate = ParseStringUtils.trimSubstringRight(argsLine, "/to");
        if (taskDesc.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            throw new RevelException(" Sorry, but the format used is invalid: one or more arguments are missing\n"
                    + "Usage: event <description> /from <start date> /to <end date>");
        }

        LocalDateTime fromDate = DateTimeParser.parseToLocalDateTime(startDate);
        LocalDateTime toDate = DateTimeParser.parseToLocalDateTime(endDate);

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


}


