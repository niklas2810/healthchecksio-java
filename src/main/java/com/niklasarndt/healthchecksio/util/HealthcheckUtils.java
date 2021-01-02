package com.niklasarndt.healthchecksio.util;


public class HealthcheckUtils {

    public static String removeColonFromTimestamp(String input) {
        if (input == null || !input.contains(":"))
            return input;
        return input.substring(0, input.lastIndexOf(':'))
                + input.substring(input.lastIndexOf(':') + 1);
    }
}
