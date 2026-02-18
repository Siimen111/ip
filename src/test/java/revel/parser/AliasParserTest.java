package revel.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import revel.RevelException;
import revel.command.AliasCommand;
import revel.command.Command;
import revel.command.TodoCommand;
import revel.storage.AliasStorage;
import revel.task.TaskList;

public class AliasParserTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        AliasStorage aliasStorage = new AliasStorage(tempDir.resolve("aliases.json"));
        AliasParser.setAliasStorage(aliasStorage);
        AliasParser.replaceUserAliases(Map.of());
    }

    @Test
    void parse_aliasAddBuiltIn_throws() {
        assertThrows(RevelException.class, () -> Parser.parse("alias add help help"));
    }

    @Test
    void parse_aliasAdd_thenAliasWorks() throws Exception {
        Command c = Parser.parse("alias add x todo");
        assertInstanceOf(AliasCommand.class, c);

        c.execute(new TaskList(), null, null);

        Command aliasCommand = Parser.parse("x read book");
        assertInstanceOf(TodoCommand.class, aliasCommand);
    }
}
