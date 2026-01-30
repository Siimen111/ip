package revel;
import org.junit.jupiter.api.Test;
import revel.command.ByeCommand;
import revel.command.Command;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    @Test
    void parse_exitAlias_returnsByeCommand() throws Exception {
        Command c = Parser.parse("exit");
        assertTrue(c instanceof ByeCommand);
        assertTrue(c.isExit());
    }

    @Test
    void parse_deadlineWithDmyTime_parsesToLocalDateTime() throws Exception {
        Parser.DeadlineArgs d = Parser.parseDeadline("return book /by 2/12/2019 1800");
        assertEquals("return book", d.description());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), d.byDate());
    }
}
