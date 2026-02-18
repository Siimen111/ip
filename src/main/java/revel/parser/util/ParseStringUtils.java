package revel.parser.util;

/**
 * Utility methods for extracting trimmed substrings around parser delimiters.
 * <p>
 * These helpers are intended for command parsing logic where arguments are split
 * using markers such as {@code /by}, {@code /from}, and {@code /to}.
 */
public final class ParseStringUtils {
    /**
     * Returns the trimmed substring to the left of the first occurrence of {@code delimiter}.
     *
     * @param str input string containing the delimiter
     * @param delimiter delimiter to search for
     * @return trimmed substring before the delimiter
     * @throws IllegalArgumentException if {@code str} or {@code delimiter} is null,
     *                                  or if {@code delimiter} is not present in {@code str}
     */
    public static String trimSubstringLeft(String str, String delimiter) {
        assert str != null : "str cannot be null";
        assert delimiter != null : "delimiter cannot be null";
        assert str.contains(delimiter) : "delimiter cannot be found";
        return str.substring(0, str.indexOf(delimiter)).trim();
    }
    /**
     * Returns the trimmed substring to the right of the first occurrence of {@code delimiter}.
     *
     * @param str input string containing the delimiter
     * @param delimiter delimiter to search for
     * @return trimmed substring after the delimiter
     * @throws IllegalArgumentException if {@code str} or {@code delimiter} is null,
     *                                  or if {@code delimiter} is not present in {@code str}
     */
    public static String trimSubstringRight(String str, String delimiter) {
        assert str != null : "str cannot be null";
        assert delimiter != null : "delimiter cannot be null";
        assert str.contains(delimiter) : "delimiter cannot be found";
        return str.substring(str.indexOf(delimiter) + delimiter.length()).trim();
    }
    /**
     * Trims the substring between the given delimiters.
     *
     * @param str Input string.
     * @param startDelimiter Start delimiter.
     * @param endDelimiter End delimiter.
     * @return Trimmed substring between the delimiters.
     */
    public static String trimSubstring(String str, String startDelimiter, String endDelimiter) {
        assert str != null : "str cannot be null";
        assert startDelimiter != null : "delimiter cannot be null";
        assert endDelimiter != null : "delimiter cannot be null";


        int startIndex = str.indexOf(startDelimiter);
        int endIndex = str.indexOf(endDelimiter, startIndex);

        assert startIndex >= 0 : "startDelimiter cannot be found";
        assert endIndex >= 0 : "endDelimiter cannot be found";
        assert endIndex >= startIndex : "endDelimiter index is before startLimiter index";
        int start = startIndex + startDelimiter.length();

        return str.substring(start, endIndex).trim();
    }
}
