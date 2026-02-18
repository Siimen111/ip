package revel.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import revel.RevelException;
import revel.parser.AliasParser;
import revel.storage.AliasStorage;
import revel.storage.Storage;
import revel.task.TaskList;
import revel.ui.Ui;

/**
 * Adds, removes, or lists user-defined command aliases.
 */
public class AliasCommand extends Command {
    /**
     * Supported alias operations.
     */
    public enum Action {
        ADD,
        REMOVE,
        LIST
    }

    private final Action action;
    private final String alias;
    private final String commandName;
    private final AliasStorage aliasStorage;

    /**
     * Creates an alias command with the given action and parameters.
     *
     * @param action Alias operation to perform.
     * @param alias Alias to add or remove (null for list).
     * @param commandName Command keyword to map to (required for add).
     * @param aliasStorage Storage used to persist aliases.
     */
    public AliasCommand(Action action, String alias, String commandName, AliasStorage aliasStorage) {
        this.action = action;
        this.alias = alias;
        this.commandName = commandName;
        this.aliasStorage = aliasStorage;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws RevelException {
        Map<String, CommandWord> aliases = new LinkedHashMap<>(aliasStorage.load());

        return switch (action) {
        case ADD -> {
            String aliasKey = normalizeAlias(alias);
            CommandWord commandWord = parseCommandWord(commandName);
            aliases.put(aliasKey, commandWord);
            aliasStorage.save(aliases);
            AliasParser.replaceUserAliases(aliases);
            yield " Added alias: " + aliasKey + " -> " + commandWord.name().toLowerCase();
        }
        case REMOVE -> {
            String aliasKey = normalizeAlias(alias);
            if (aliases.remove(aliasKey) == null) {
                throw new RevelException("Alias does not exist: " + aliasKey);
            }
            aliasStorage.save(aliases);
            AliasParser.replaceUserAliases(aliases);
            yield " Removed alias: " + aliasKey;
        }
        case LIST -> {
            if (aliases.isEmpty()) {
                yield " No aliases defined.";
            }
            String list = aliases.entrySet().stream()
                    .map(entry -> entry.getKey() + " -> " + entry.getValue().name().toLowerCase())
                    .collect(Collectors.joining("\n"));
            yield " Aliases:\n" + list;
        }
        };
    }

    private String normalizeAlias(String raw) throws RevelException {
        if (raw == null) {
            throw new RevelException("Alias cannot be empty.");
        }
        String aliasKey = raw.trim().toLowerCase();
        if (aliasKey.isEmpty()) {
            throw new RevelException("Alias cannot be empty.");
        }
        for (int i = 0; i < aliasKey.length(); i++) {
            if (Character.isWhitespace(aliasKey.charAt(i))) {
                throw new RevelException("Alias cannot contain whitespace: " + aliasKey);
            }
        }
        return aliasKey;
    }

    private CommandWord parseCommandWord(String raw) throws RevelException {
        if (raw == null || raw.trim().isEmpty()) {
            throw new RevelException("Command for alias cannot be empty.");
        }
        try {
            return CommandWord.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RevelException("Unknown command for alias: " + raw);
        }
    }
}
