package com.niklasarndt.healthchecksio.util;

/**
 * Contains utility functions for this library.
 *
 * @since 1.0.1
 */
public class HealthcheckUtils {

    /**
     * <p>The problem is that {@link java.text.SimpleDateFormat} is
     * a bit inconsistent in it's parsing algorithm.</p>
     *
     * <p>Therefore, timezone specifiers like {@code +01:00} are not accepted,
     * but {@code +0100} are, even though they should mean the same. This is
     * why I decided to remove this last colon from the timestamp string.</p>
     *
     * @param input The input timestamp (e.g. 2020-12-24T15:12:24+02:00)
     *
     * @return The input timestamp without the last colon.
     */
    public static String removeColonFromTimestamp(String input) {
        if (input == null || !input.contains(":"))
            return input;
        return input.substring(0, input.lastIndexOf(':'))
                + input.substring(input.lastIndexOf(':') + 1);
    }
}
