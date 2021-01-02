package com.niklasarndt.healthchecksio.exception;

/**
 * <p>An {@link UnauthorizedException} is thrown whenever you attempt to do something which
 * healthchecks.io doesn't allow. There are 2 main cases when this happens:</p>
 *
 * <ol>
 *     <li>You are using a read-only key to access a feature which requires write-access.</li>
 *     <li>The API responds with a status code of
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/401">401</a> or
 *     <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/403">403</a>.
 *     You're either using a read-only API key or try to access resource you simply don't
 *     have access to.</li>
 * </ol>
 *
 * @since 1.0.1
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Healthchecks.io denied the request. Are you using an invalid or read-only API key?");
    }
}
