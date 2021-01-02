package com.niklasarndt.healthchecksio;

import okhttp3.Interceptor;
import okhttp3.Response;
import java.io.IOException;

/**
 * <p>Internal class for changing the user agent of each request.</p>
 * <br>
 * <p>The user agent will look like this: {@code healthchecksio-java:<version>}</p>
 */
public class UserAgentInterceptor implements Interceptor {

    private static final String AGENT = "healthchecksio-java:" + HealthchecksInfo.VERSION();

    protected UserAgentInterceptor() {

    }

    /**
     * Intercepts a request.
     *
     * @param chain The {@link Interceptor} chain, which is processed recursively.
     *
     * @return The object which is ready to be sent.
     *
     * @throws IOException If an error occurs while processing the {@link Interceptor} chain.
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .header("User-Agent", AGENT).build());
    }
}