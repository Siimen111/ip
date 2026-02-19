package revel.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class RevelTest {
    @TempDir
    Path tempDir;

    @Test
    void getResponse_todoCommand_setsCommandTypeAndReturnsSuccessMessage() {
        Revel revel = new Revel(tempDir.toString());

        String response = revel.getResponse("todo write tests");

        assertEquals("TodoCommand", revel.getCommandType());
        assertTrue(response.contains("added this task"));
        assertTrue(response.contains("write tests"));
    }

    @Test
    void getResponse_invalidCommand_setsErrorCommand() {
        Revel revel = new Revel(tempDir.toString());

        String response = revel.getResponse("this-is-not-a-command");

        assertEquals("ErrorCommand", revel.getCommandType());
        assertTrue(response.contains("Type 'help'"));
    }
}
