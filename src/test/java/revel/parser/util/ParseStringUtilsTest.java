package revel.parser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParseStringUtilsTest {
    @Test
    void trimSubstringLeft_validInput_returnsLeftSegment() {
        String result = ParseStringUtils.trimSubstringLeft("read book /by 2026-02-20", "/by");
        assertEquals("read book", result);
    }

    @Test
    void trimSubstringRight_validInput_returnsRightSegment() {
        String result = ParseStringUtils.trimSubstringRight("read book /by 2026-02-20", "/by");
        assertEquals("2026-02-20", result);
    }

    @Test
    void trimSubstring_validInput_returnsMiddleSegment() {
        String result = ParseStringUtils.trimSubstring("party /from 2026-02-20 /to 2026-02-21", "/from", "/to");
        assertEquals("2026-02-20", result);
    }

    @Test
    void trimSubstringLeft_missingDelimiter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                ParseStringUtils.trimSubstringLeft("read book 2026-02-20", "/by"));
    }

    @Test
    void trimSubstringRight_missingDelimiter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                ParseStringUtils.trimSubstringRight("read book 2026-02-20", "/by"));
    }

    @Test
    void trimSubstring_missingDelimiters_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                ParseStringUtils.trimSubstring("party 2026-02-20 2026-02-21", "/from", "/to"));
    }
}
