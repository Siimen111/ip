package revel.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import revel.command.ByeCommand;
import revel.command.Command;
import revel.RevelException;

public class ParserTest {
    @Test
    void parse_exitAlias_returnsByeCommand() throws Exception {
        Command c = Parser.parse("exit");
        assertInstanceOf(ByeCommand.class, c);
        assertTrue(c.isExit());
    }

    @Test
    void parse_deadlineWithDmyTime_parsesToLocalDateTime() throws Exception {
        Parser.DeadlineArgs d = Parser.parseDeadline("return book /by 2/12/2019 1800");
        assertEquals("return book", d.description());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), d.byDate());
    }

    @Test
    void parseInput_empty_throws() {
        assertThrows(RevelException.class, () -> Parser.parseInput("   "));
    }

    @Test
    void parseTodo_empty_throws() {
        assertThrows(RevelException.class, () -> Parser.parseTodo(""));
    }

    @Test
    void parseDeadline_missingBy_throws() {
        assertThrows(RevelException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    void parseDeadline_invalidDate_throws() {
        assertThrows(RevelException.class, () -> Parser.parseDeadline("return book /by 2019-13-40"));
    }

    @Test
    void parseEvent_missingFromOrTo_throws() {
        assertThrows(RevelException.class, () -> Parser.parseEvent("party /from 2024-10-02"));
    }

    @Test
    void parseEvent_fromAfterTo_throws() {
        assertThrows(RevelException.class, () -> Parser.parseEvent("party /to 2024-10-02 /from 2024-10-01"));
    }

    @Test
    void parseNumber_nonNumeric_throws() {
        assertThrows(RevelException.class, () -> Parser.parseNumber("abc"));
    }

    @Test
    void parseTaskNumber_outOfRange_throws() {
        assertThrows(RevelException.class, () -> Parser.parseTaskNumber(0, 3));
    }

    @Test
    void formatForUser_dateOnly_formatsWithoutTime() {
        String formatted = Parser.formatForUser(LocalDateTime.of(2026, 2, 4, 0, 0));
        assertEquals("04/02/2026", formatted);
    }
}
