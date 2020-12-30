package com.niklasarndt.healthchecksio;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * <p>This class wraps a {@link CompletableFuture} object inside a OkHttp callback.
 * For internal use only, you don't need to think too much about this one.
 * </p>
 * <br>
 * <p>With much inspiration from <a href="https://stackoverflow.com/questions/42308439/java-retrieving-result-from-okhttp-asynchronous-get">stackoverflow</a>.</p>
 */
public class OkHttpResponseFuture implements Callback {
    /**
     * <p>For logging during tests.</p>
     * <p>If you set the Log Level for this package to {@code DEBUG}, these
     * messages will appear in normal mode as well!</p>
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Healthchecks.class);

    /**
     * <p>This is the {@link CompletableFuture} object where the response will be stored.</p>
     */
    public final CompletableFuture<Response> future = new CompletableFuture<>();

    protected OkHttpResponseFuture() {
    }

    /**
     * <p>Reports the request error to the {@link #future} object.</p>
     *
     * <br>
     * {@inheritDoc}
     */
    @Override
    public void onFailure(Call call, IOException e) {
        LOGGER.debug("Failed to contact healthchecks.io!", e);

        future.completeExceptionally(e);
    }

    /**
     * <p>Fills the {@link #future} object with the reponse from healthchecks.io.</p>
     * <br>
     * {@inheritDoc}
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LOGGER.debug("Completed call to {}, response is {}", call.request().url(),
                response.code());

        future.complete(response);
    }
}
