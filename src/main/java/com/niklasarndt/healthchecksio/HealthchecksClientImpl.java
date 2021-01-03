package com.niklasarndt.healthchecksio;

import com.niklasarndt.healthchecksio.util.OkHttpResponseFuture;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Please check out {@link HealthchecksClient} for documentation (this is just the
 * implementation).</p>
 *
 * @since 1.0.0
 */
public class HealthchecksClientImpl implements HealthchecksClient {

    private static final Logger LOG = LoggerFactory.getLogger(HealthchecksClient.class);
    private static final String HEALTHCHECKS_HOST = "https://hc-ping.com/";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(Healthchecks.USER_AGENT)
            .build();
    private final String host;
    private final String baseUrl;

    protected HealthchecksClientImpl(URL base) {
        this(base.toString().substring(0, base.toString().lastIndexOf("/")),
                base.toString().substring(base.toString().lastIndexOf("/") + 1));
    }

    protected HealthchecksClientImpl(String uuid) {
        this(HEALTHCHECKS_HOST, uuid);
    }

    protected HealthchecksClientImpl(String host, String uuid) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(uuid);
        host = host.trim();

        //URL validation
        if (!host.equals(HEALTHCHECKS_HOST))
            this.host = Healthchecks.validateUrl(host);
        else //Skip URL validation for default host (already validated)
            this.host = host;

        this.baseUrl = this.host + uuid;
        LOG.debug("Host url has been set to {}", this.host);
    }

    @Override
    public CompletableFuture<Response> start(String body) {
        return sendHeartbeat(EventType.START, body);
    }

    @Override
    public CompletableFuture<Response> success(String body) {
        return sendHeartbeat(EventType.SUCCESS, body);
    }

    @Override
    public CompletableFuture<Response> fail(String body) {
        return sendHeartbeat(EventType.FAIL, body);
    }

    @Override
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
     * @param path The sub path in the URL, e.g. {@code /fail} or {@code /1} (for exit code one).
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
            builder.post(RequestBody.create(body, Healthchecks.PLAIN_TEXT));
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
