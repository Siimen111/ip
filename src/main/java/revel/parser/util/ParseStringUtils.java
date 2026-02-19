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
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter cannot be null");
        }
        int delimiterIndex = str.indexOf(delimiter);
        if (delimiterIndex < 0) {
            throw new IllegalArgumentException("delimiter cannot be found");
        }
        return str.substring(0, delimiterIndex).trim();
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
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter cannot be null");
        }
        int delimiterIndex = str.indexOf(delimiter);
        if (delimiterIndex < 0) {
            throw new IllegalArgumentException("delimiter cannot be found");
        }
        return str.substring(delimiterIndex + delimiter.length()).trim();
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
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        if (startDelimiter == null) {
            throw new IllegalArgumentException("startDelimiter cannot be null");
        }
        if (endDelimiter == null) {
            throw new IllegalArgumentException("endDelimiter cannot be null");
        }
        int startIndex = str.indexOf(startDelimiter);
        if (startIndex < 0) {
            throw new IllegalArgumentException("startDelimiter cannot be found");
        }
        int endIndex = str.indexOf(endDelimiter, startIndex + startDelimiter.length());
        if (endIndex < 0) {
            throw new IllegalArgumentException("endDelimiter cannot be found");
        }
        int start = startIndex + startDelimiter.length();

        return str.substring(start, endIndex).trim();
    }
}
