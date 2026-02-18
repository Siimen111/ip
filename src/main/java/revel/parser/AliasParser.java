package revel.parser;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import revel.RevelException;
import revel.command.AliasCommand;
import revel.command.CommandWord;
import revel.storage.AliasStorage;

/**
 * Handles alias-related parsing and alias registry management.
 * <p>
 * This class maintains built-in and user-defined aliases, validates alias updates,
 * and parses {@code alias} command arguments into {@link AliasCommand} instances.
 * It also enforces alias rules such as preventing overrides/removals of reserved
 * built-in aliases.
 */
public class AliasParser {
    // alias -> command words
    protected static final Map<String, CommandWord> ALIASES = new LinkedHashMap<>();
    // Built-in aliases (reserved)
    private static final Set<String> BUILTIN_ALIASES;
    private static final Map<String, CommandWord> USER_ALIASES = new LinkedHashMap<>();
    private static final String MESSAGE_WRONG_ALIAS = " Sorry, but this alias cannot be used.\n";
    private static final String MESSAGE_ALIAS_USAGE =
            """
                     Usage:
                      alias add <alias> <command>
                      alias remove <alias>
                      alias list
                    """;
    private static AliasStorage aliasStorage;

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
        register(CommandWord.ALIAS, "alias");
    }

    static {
        BUILTIN_ALIASES = new HashSet<>(ALIASES.keySet());
    }

    private static void register(CommandWord word, String... aliases) {
        for (String a : aliases) {
            String key = a.toLowerCase();
            ALIASES.put(key, word);
        }
    }

    /**
     * Sets the alias storage used by alias commands.
     *
     * @param storage Alias storage instance.
     */
    public static void setAliasStorage(AliasStorage storage) {
        aliasStorage = storage;
    }

    /**
     * Registers user-defined aliases.
     *
     * @param userAliases Map of alias to command word.
     */
    public static void registerUserAliases(Map<String, CommandWord> userAliases) throws RevelException {
        if (userAliases == null || userAliases.isEmpty()) {
            return;
        }
        for (Map.Entry<String, CommandWord> entry : userAliases.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (BUILTIN_ALIASES.contains(key)) {
                throw new RevelException("Alias cannot override a built-in alias: " + key);
            }
            USER_ALIASES.put(key, entry.getValue());
            ALIASES.put(key, entry.getValue());
        }
    }

    /**
     * Replaces all user-defined aliases with the given set.
     *
     * @param userAliases Map of alias to command word.
     */
    public static void replaceUserAliases(Map<String, CommandWord> userAliases) throws RevelException {
        for (String key : USER_ALIASES.keySet()) {
            ALIASES.remove(key);
        }
        USER_ALIASES.clear();
        AliasParser.registerUserAliases(userAliases);
    }

    protected static AliasCommand parseAliasCommand(String argsLine) throws RevelException {
        if (aliasStorage == null) {
            throw new RevelException("Alias storage is not configured.");
        }
        if (argsLine == null || argsLine.trim().isEmpty()) {
            throw new RevelException(MESSAGE_WRONG_ALIAS + MESSAGE_ALIAS_USAGE);
        }
        String[] parts = argsLine.trim().split("\\s+");
        String action = parts[0].toLowerCase();

        return switch (action) {
        case "add" -> {
            if (parts.length != 3) {
                throw new RevelException(MESSAGE_WRONG_ALIAS + MESSAGE_ALIAS_USAGE);
            }
            String aliasKey = parts[1].toLowerCase();
            if (BUILTIN_ALIASES.contains(aliasKey)) {
                throw new RevelException("Alias cannot override a built-in alias: " + aliasKey);
            }
            yield new AliasCommand(AliasCommand.Action.ADD, parts[1], parts[2], aliasStorage);
        }
        case "remove" -> {
            if (parts.length != 2) {
                throw new RevelException(MESSAGE_WRONG_ALIAS + MESSAGE_ALIAS_USAGE);
            }
            String aliasKey = parts[1].toLowerCase();
            if (BUILTIN_ALIASES.contains(aliasKey)) {
                throw new RevelException("Cannot remove built-in alias: " + aliasKey);
            }
            yield new AliasCommand(AliasCommand.Action.REMOVE, parts[1], null, aliasStorage);
        }
        case "list" -> {
            if (parts.length != 1) {
                throw new RevelException(MESSAGE_ALIAS_USAGE);
            }
            yield new AliasCommand(AliasCommand.Action.LIST, null, null, aliasStorage);
        }
        default -> throw new RevelException(MESSAGE_ALIAS_USAGE);
        };
    }
}
