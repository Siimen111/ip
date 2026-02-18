package revel.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import revel.RevelException;

/**
 * Parses and formats date/time values used by command parsing.
 * <p>
 * This class centralizes accepted user input date formats and conversion
 * logic to and from {@code LocalDateTime}, so {@link Parser} stays focused
 * on command dispatch and argument flow.
 */
public class DateTimeParser {
    private static final DateTimeFormatter IN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter IN_YMD_HHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter IN_YMD_HH_COLON_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter IN_DMY_HHMM = DateTimeFormatter
            .ofPattern("d/M/yyyy HHmm"); // example: 2/12/2019 1800
    // For printing
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter OUT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    protected static LocalDateTime parseToLocalDateTime(String raw) throws RevelException {
        String s = raw.trim();

        DateTimeFormatter[] dateTimeFormats = {IN_YMD_HHMM, IN_YMD_HH_COLON_MM, IN_DMY_HHMM};

        // Try date-time formats first
        DateTimeParseException last = null;
        for (DateTimeFormatter f : dateTimeFormats) {
            try {
                return LocalDateTime.parse(s, f);
            } catch (DateTimeParseException e) {
                last = e; // record the failure (catch is no longer empty)
            }
        }

        // Then try date-only
        try {
            LocalDate d = LocalDate.parse(s, IN_DATE);
            return d.atStartOfDay(); // default time 00:00 if none given
        } catch (DateTimeParseException e) {

            throw new RevelException(
                    """
                             Sorry, but your date/time is invalid.
                            Accepted formats:
                              yyyy-MM-dd
                              yyyy-MM-dd HHmm (e.g., 2019-12-02 1800)
                              d/M/yyyy HHmm (e.g., 2/12/2019 1800)"""
            );
        }
    }

    /**
     * Formats a date-time for user display.
     *
     * @param dt Date-time to format.
     * @return Formatted string for output.
     */
    public static String formatForUser(LocalDateTime dt) {
        // If you want date-only display when time is 00:00:
        if (dt.getHour() == 0 && dt.getMinute() == 0) {
            return dt.format(OUT_DATE);
        }
        return dt.format(OUT_DATE_TIME);
    }
}
