package com.niklasarndt.healthchecksio;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.CompletableFuture;

public class Healthchecks {

    private static final UserAgentInterceptor userAgent = new UserAgentInterceptor();
    private static final MediaType plainTextType = MediaType.parse("text/plain");

    /**
     * <p>Creates a new healthchecks.io Client.</p>
     * <br>
     * <p>Currently supported messages:</p>
     * <ul>
     *     <li>{@code start()}: The process which the check tracks just started.</li>
     *     <li>{@code success()}: The process which the check tracks completed successfully.</li>
     *     <li>{@code fail()}: The process which the check tracks failed (this will cause an alert!).</li>
     * </ul>
     * <p>Optionally, you can also pass a parameter {@link String} {@code body} which will be stored
     * together with your status message on healthchecks.io.</p>
     * <p>All requests will be sent asynchronously and return a {@link CompletableFuture} with a
     * {@link Response} object.</p>
     * <p>Use {@link CompletableFuture#get()} to retrieve your response!</p>
     *
     * @param uuid The universal unique identifier (UUID) of your check.
     *             You will find this one your check dashboard.
     *
     * @return A new {@link Healthchecks} client, which you can use
     */
    public static Healthchecks forUuid(String uuid) {
        return new Healthchecks(uuid);
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(userAgent).build();
    private final String uuid;

    private Healthchecks(String uuid) {
        this.uuid = uuid;
    }

    /**
     * <p>Notifies healthchecks.io about the start of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> start() {
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
    public CompletableFuture<Response> start(String body) {
        return sendHeartbeat(EventType.START, body);
    }

    /**
     * <p>Notifies healthchecks.io about the (successful) completion of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> success() {
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
    public CompletableFuture<Response> success(String body) {
        return sendHeartbeat(EventType.SUCCESS, body);
    }

    /**
     * <p>Notifies healthchecks.io about the failure of an event.</p>
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> fail() {
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
    public CompletableFuture<Response> fail(String body) {
        return sendHeartbeat(EventType.FAIL, body);
    }

    /**
     * <p>Private executor for status check messages.</p>
     * <br>
     * <p>First off, the request will be built. If a {@code body} has
     * been specified, this one will be put in the response body as well.</p>
     * <p>Afterwards a new call in enqueued, and the {@link CompletableFuture}
     * object is returned.</p>
     *
     * @param type The {@link EventType} of the ping.
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    private CompletableFuture<Response> sendHeartbeat(EventType type, String body) {
        Request.Builder builder = new Request.Builder()
                .url("https://hc-ping.com/" + uuid + type);

        if (body != null)
            builder.post(RequestBody.create(plainTextType, body));

        OkHttpResponseFuture callback = new OkHttpResponseFuture();

        client.newCall(builder.build()).enqueue(callback);
        return callback.future;
    }

    private enum EventType {
        /**
         * The process which the check tracks completed successfully.
         */
        SUCCESS(""),
        /**
         * The process which the check tracks failed (this will cause an alert!)
         */
        FAIL("/fail"),
        /**
         * The process which the check tracks just started.
         */
        START("/start");

        private final String path;

        EventType(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }
    }
}
