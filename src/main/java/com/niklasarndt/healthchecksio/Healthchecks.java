package com.niklasarndt.healthchecksio;

import com.niklasarndt.healthchecksio.exception.UnauthorizedException;
import com.niklasarndt.healthchecksio.model.Check;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Healthchecks {

    protected static final UserAgentInterceptor USER_AGENT = new UserAgentInterceptor();
    protected static final Logger LOG = LoggerFactory.getLogger(Healthchecks.class);
    protected static final MediaType PLAIN_TEXT = MediaType.parse("text/plain");
    private static final String HEALTHCHECKS_HOST = "https://hc-ping.com/";

    public static HealthchecksManager manager(String host, String apiKey) {
        return new HealthchecksManager(host, apiKey);
    }

    public static HealthchecksManager manager(String apiKey) {
        return new HealthchecksManager(apiKey);
    }

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
     *             You will find this one your check dashboard. <b>Must not be null.</b>
     *
     * @return A new {@link Healthchecks} client, which you can use
     */
    public static Healthchecks forUuid(String uuid) {
        return new Healthchecks(uuid);
    }

    /**
     * <p>Creates a new healthchecks.io Client <b>for a custom host</b>.</p>
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
     * @param hostUrl The URL of your custom healthchecks instance. Normally, that's hc-ping.com,
     *                then you can use the constructor with only one parameter: {@link #forUuid(String)}
     *                If you have a self-hosted instance, you can use this one as well. <b>Must not be null.</b>
     * @param uuid    The universal unique identifier (UUID) of your check.
     *                You will find this one your check dashboard. <b>Must not be null.</b>
     *
     * @return A new {@link Healthchecks} client, which you can use
     *
     * @throws IllegalArgumentException If the {@code hostUrl} is invalid.
     *                                  The bare minimum is something like {@code http://localhost},
     *                                  but the Protocol (http/https), Host (e.g. hc-ping.com),
     *                                  (optionally) custom Port (e.g. 8080) and subpath (e.g. /healthchecks)
     *                                  will be taken into account as well!
     */
    public static Healthchecks forUuid(String hostUrl, String uuid) {
        return new Healthchecks(hostUrl, uuid);
    }

    protected static String validateUrl(String host) {
        try {
            URL url = new URL(host);
            if (url.getHost() == null || url.getHost().length() == 0)
                throw new IllegalArgumentException("No host specified in " + url.toString());

            return url.getProtocol() + "://" + url.getHost()
                    + (url.getPort() != -1 ? ":" + url.getPort() : "")
                    + url.getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The host URL " + host + " is invalid!", e);
        }
    }

    public static Healthchecks forCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();

        return new Healthchecks(check.getPingUrl());
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(USER_AGENT).build();
    private final String host;
    private final String uuid;
    private final String baseUrl;

    private Healthchecks(URL base) {
        this(base.toString().substring(0, base.toString().lastIndexOf("/")),
                base.toString().substring(base.toString().lastIndexOf("/") + 1));
    }

    private Healthchecks(String uuid) {
        this(HEALTHCHECKS_HOST, uuid);
    }

    private Healthchecks(String host, String uuid) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(uuid);
        host = host.trim();

        //URL validation
        if (!host.equals(HEALTHCHECKS_HOST))
            this.host = validateUrl(host);
        else //Skip URL validation for default host (already validated)
            this.host = host;

        this.uuid = uuid;

        this.baseUrl = this.host + uuid;
        LOG.debug("Host url has been set to {}", this.host);
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
     * <p>Notifies healthchecks.io about the completion of an event with
     * the exit code {@code code}.</p>
     *
     * @param code The exit code of the event (0-255). If the code is equal to 0, healthchecks.io
     *             will interpret this as a success, all other exit codes will raise an alert!
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    public CompletableFuture<Response> exitCode(int code) {
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
    public CompletableFuture<Response> exitCode(int code, String body) {
        if (code < 0 || code > 255)
            throw new IllegalArgumentException("Only values from 0 to 255 are valid exit codes!");

        return sendHeartbeat("/" + code, body);
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
        return sendHeartbeat(type.path, body);
    }


    /**
     * <p>Private executor for status check messages.</p>
     * <br>
     * <p>First off, the request will be built. If a {@code body} has
     * been specified, this one will be put in the response body as well.</p>
     * <p>Afterwards a new call in enqueued, and the {@link CompletableFuture}
     * object is returned.</p>
     *
     * @param path The subpath in the URL, e.g. {@code /fail} or {@code /1} (for exit code one).
     * @param body A message (plain text) which will be stored on healthchecks.io, together with this status message.
     *
     * @return A {@link CompletableFuture} with a
     *         * {@link Response} object. Use {@link CompletableFuture#get()} to retrieve your response!
     */
    private CompletableFuture<Response> sendHeartbeat(String path, String body) {
        LOG.debug("Sending signal to path {} (host: {}, has body: {})",
                path, host, body != null);

        Request.Builder builder = new Request.Builder()
                .url(baseUrl + path);
        if (body != null)
            builder.post(RequestBody.create(body, PLAIN_TEXT));
        else
            builder.get();

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
