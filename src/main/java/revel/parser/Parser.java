package revel.parser;

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
public class Parser extends TaskArgumentParser {
    // DateTime Constants
    private static final String MESSAGE_UNKNOWN_COMMAND =
            " Sorry! I am unable to assist you with that.\n"
                    + "Type 'help' for a list of commands available to you.";

    // Record classes for storing parsed task commands
    /**
     * Represents the split input of a command word and its argument line.
     */
    public record ParsedInput(String command, String argsLine) {}

    /**
     * Parses a command word token into a supported {@link CommandWord}.
     *
     * @param token Raw token representing a command word.
     * @return Parsed command word.
     * @throws RevelException If the command word is not supported.
     */
    public static CommandWord parseWord(String token) throws RevelException {
        String key = token.trim().toLowerCase();
        CommandWord word = AliasParser.ALIASES.get(key);
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
        return AliasParser.ALIASES.keySet().stream()
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
            return AliasParser.parseAliasCommand(argsLine);
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

}


