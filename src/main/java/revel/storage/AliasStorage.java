package revel.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

import revel.RevelException;
import revel.command.CommandWord;

/**
 * Handles loading and saving user-defined command aliases.
 */
public class AliasStorage {
    private final Path filePath;

    public AliasStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads aliases from the configured JSON file.
     *
     * @return Alias map (empty if file does not exist or is empty).
     * @throws RevelException If the file cannot be read or parsed.
     */
    public Map<String, CommandWord> load() throws RevelException {
        try {
            if (Files.notExists(filePath)) {
                return new LinkedHashMap<>();
            }

            String raw = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (raw.isEmpty()) {
                return new LinkedHashMap<>();
            }

            return parseJsonObject(raw);
        } catch (IOException e) {
            throw new RevelException("Unable to load aliases from file: " + filePath);
        }
    }

    /**
     * Saves aliases to the configured JSON file.
     *
     * @param aliases Alias map to persist.
     * @throws RevelException If the aliases cannot be saved.
     */
    public void save(Map<String, CommandWord> aliases) throws RevelException {
        try {
            Files.createDirectories(filePath.getParent());
            String json = toJsonObject(aliases);
            Files.writeString(filePath, json, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RevelException("Unable to save aliases to disk.");
        }
    }

    private Map<String, CommandWord> parseJsonObject(String raw) throws RevelException {
        String s = raw.trim();
        if (!s.startsWith("{") || !s.endsWith("}")) {
            throw new RevelException("Invalid aliases.json format: expected JSON object.");
        }

        String body = s.substring(1, s.length() - 1).trim();
        Map<String, CommandWord> result = new LinkedHashMap<>();
        if (body.isEmpty()) {
            return result;
        }

        int i = 0;
        while (i < body.length()) {
            i = skipWhitespace(body, i);
            if (i >= body.length() || body.charAt(i) != '"') {
                throw new RevelException("Invalid aliases.json format: expected string key.");
            }
            ParseResult keyResult = readJsonString(body, i);
            String alias = normalizeAlias(keyResult.value());
            i = skipWhitespace(body, keyResult.nextIndex());

            if (i >= body.length() || body.charAt(i) != ':') {
                throw new RevelException("Invalid aliases.json format: expected ':' after key.");
            }
            i++;
            i = skipWhitespace(body, i);

            if (i >= body.length() || body.charAt(i) != '"') {
                throw new RevelException("Invalid aliases.json format: expected string value.");
            }
            ParseResult valueResult = readJsonString(body, i);
            String commandRaw = valueResult.value().trim();
            CommandWord word = parseCommandWord(commandRaw);
            i = skipWhitespace(body, valueResult.nextIndex());

            if (result.containsKey(alias)) {
                throw new RevelException("Duplicate alias in aliases.json: " + alias);
            }
            result.put(alias, word);

            if (i >= body.length()) {
                break;
            }
            if (body.charAt(i) == ',') {
                i++;
                continue;
            }
            throw new RevelException("Invalid aliases.json format: expected ',' or end of object.");
        }

        return result;
    }

    private CommandWord parseCommandWord(String commandRaw) throws RevelException {
        try {
            return CommandWord.valueOf(commandRaw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RevelException("Unknown command in aliases.json: " + commandRaw);
        }
    }

    private String normalizeAlias(String aliasRaw) throws RevelException {
        String alias = aliasRaw.trim().toLowerCase();
        if (alias.isEmpty()) {
            throw new RevelException("Alias cannot be empty in aliases.json.");
        }
        for (int i = 0; i < alias.length(); i++) {
            if (Character.isWhitespace(alias.charAt(i))) {
                throw new RevelException("Alias cannot contain whitespace: " + alias);
            }
        }
        return alias;
    }

    private String toJsonObject(Map<String, CommandWord> aliases) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        int index = 0;
        for (Map.Entry<String, CommandWord> entry : aliases.entrySet()) {
            sb.append("  \"")
                    .append(escapeJson(entry.getKey()))
                    .append("\": \"")
                    .append(escapeJson(entry.getValue().name().toLowerCase()))
                    .append("\"");
            if (index < aliases.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
            index++;
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private int skipWhitespace(String s, int i) {
        int idx = i;
        while (idx < s.length() && Character.isWhitespace(s.charAt(idx))) {
            idx++;
        }
        return idx;
    }

    private ParseResult readJsonString(String s, int startIndex) throws RevelException {
        StringBuilder sb = new StringBuilder();
        int i = startIndex + 1; // skip opening quote
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '"') {
                return new ParseResult(sb.toString(), i + 1);
            }
            if (c == '\\') {
                if (i + 1 >= s.length()) {
                    throw new RevelException("Invalid aliases.json format: unterminated escape.");
                }
                char next = s.charAt(i + 1);
                if (next == '"' || next == '\\' || next == '/') {
                    sb.append(next);
                } else if (next == 'n') {
                    sb.append('\n');
                } else if (next == 't') {
                    sb.append('\t');
                } else if (next == 'r') {
                    sb.append('\r');
                } else {
                    throw new RevelException("Invalid aliases.json format: unsupported escape.");
                }
                i += 2;
                continue;
            }
            sb.append(c);
            i++;
        }
        throw new RevelException("Invalid aliases.json format: unterminated string.");
    }

    private record ParseResult(String value, int nextIndex) {}
}
