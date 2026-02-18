package revel.parser;

import java.time.LocalDateTime;

import revel.RevelException;
import revel.parser.util.ParseStringUtils;

/**
 * Parses and validates task-related command arguments.
 * <p>
 * This class handles argument extraction for task commands such as
 * {@code todo}, {@code deadline}, and {@code event}, including
 * delimiter-based splitting, date/time conversion, and task-number
 * validation. It produces structured argument records used by command
 * constructors.
 */
public class TaskArgumentParser {
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
     * @param itemCount  Total number of tasks.
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

    /**
     * Represents parsed deadline arguments.
     */
    public record DeadlineArgs(String description, LocalDateTime byDate) {
    }

    /**
     * Represents parsed event arguments.
     */
    public record EventArgs(String description, LocalDateTime fromDate, LocalDateTime toDate) {
    }
}
