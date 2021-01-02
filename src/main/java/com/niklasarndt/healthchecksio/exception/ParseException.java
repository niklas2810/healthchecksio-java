package com.niklasarndt.healthchecksio.exception;

/**
 * <p></p>A {@link ParseException} is thrown whenever the Jackson API
 * stops working (API read / write). There are two common cases why this happens:</p>
 *
 * <ol>
 *     <li>Poor implementation of the healthchecks.io API. Let's hope that this never happens.</li>
 *     <li>You inserted incorrect data into an object and somehow managed to break the Jackson library.
 *     Congrats!</li>
 * </ol>
 *
 * @since 1.0.1
 */
public class ParseException extends RuntimeException {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
