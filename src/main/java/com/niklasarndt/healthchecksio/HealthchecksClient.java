package com.niklasarndt.healthchecksio;

import okhttp3.Response;
import java.util.concurrent.CompletableFuture;

public interface HealthchecksClient {

    /**
     * <p>Notifies healthchecks.io about the start of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    default CompletableFuture<Response> start() {
        return start(null);
    }

    /**
     * <p>Notifies healthchecks.io about the start of an event.</p>
     *
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> start(String body);

    /**
     * <p>Notifies healthchecks.io about the (successful) completion of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    default CompletableFuture<Response> success() {
        return success(null);
    }

    /**
     * <p>Notifies healthchecks.io about the (successful) completion of an event.</p>
     *
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> success(String body);

    /**
     * <p>Notifies healthchecks.io about the failure of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    default CompletableFuture<Response> fail() {
        return fail(null);
    }

    /**
     * <p>Notifies healthchecks.io about the failure of an event.</p>
     *
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> fail(String body);

    /**
     * <p>Notifies healthchecks.io about the completion of an event with
     * the exit code {@code code}.</p>
     *
     * @param code The exit code of the event (0-255). If the code is equal to 0, healthchecks.io
     *             will interpret this as a success, all other exit codes will raise an alert!
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    default CompletableFuture<Response> exitCode(int code) {
        return exitCode(code, null);
    }

    /**
     * <p>Notifies healthchecks.io about the completion of an event with
     * the exit code {@code code}.</p>
     *
     * @param code The exit code of the event (0-255). If the code is equal to 0, healthchecks.io
     *             will interpret this as a success, all other exit codes will raise an alert!
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> exitCode(int code, String body);
}
