package com.niklasarndt.healthchecksio;

import com.niklasarndt.healthchecksio.exception.UnauthorizedException;
import com.niklasarndt.healthchecksio.model.Check;
import com.niklasarndt.healthchecksio.util.UserAgentInterceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * <p>This is the main class for all library users.</p>
 *
 * <p>Using this class, you can create new {@link HealthchecksClient} and
 * {@link HealthchecksManager} instances.</p>
 *
 * @since 1.0.0
 */
public class Healthchecks {

    protected static final UserAgentInterceptor USER_AGENT = new UserAgentInterceptor();
    protected static final MediaType PLAIN_TEXT = MediaType.parse("text/plain");

    /**
     * <p>Create a new healthchecks.io Manager.</p>
     *
     * <p>This object supports listing, creating, updating, pausing and deleting checks.</p>
     *
     * <p>It can list status flips, individual pings and notification channels (integrations)
     * as well.</p>
     *
     * <p>Please note that if you use a <b>read-only api key</b>, some features might not
     * be available to you.</p>
     *
     * @param apiKey Your read-only or normal API key. Please note that if you use a <b>read-only api key</b>, some features might not
     *               * be available to you.
     *
     * @return A new {@link HealthchecksManager}, which you can use to utilize the Management API.
     */
    public static HealthchecksManager manager(String apiKey) {
        return new HealthchecksManagerImpl(apiKey);
    }

    /**
     * <p>Create a new healthchecks.io Manager for a custom host URL.</p>
     *
     * <p>This object supports listing, creating, updating, pausing and deleting checks.</p>
     *
     * <p>It can list status flips, individual pings and notification channels (integrations)
     * as well.</p>
     *
     * <p>Please note that if you use a <b>read-only api key</b>, some features might not
     * be available to you.</p>
     *
     * @param hostUrl The URL of your custom healthchecks instance. Normally, that's healthchecks.io,
     *                then you can use the constructor with only one parameter: {@link #manager(String)}
     *                If you have a self-hosted instance, you can use this one as well. <b>Must not be null.</b>
     * @param apiKey  Your read-only or normal API key. Please note that if you use a <b>read-only api key</b>, some features might not
     *                * be available to you.
     *
     * @return A new {@link HealthchecksManager}, which you can use to utilize the Management API.
     */
    public static HealthchecksManager manager(String hostUrl, String apiKey) {
        return new HealthchecksManagerImpl(hostUrl, apiKey);
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
     * @return A new {@link HealthchecksClient} client, which you can use
     */
    public static HealthchecksClient forUuid(String uuid) {
        return new HealthchecksClientImpl(uuid);
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
     * @return A new {@link HealthchecksClient} client, which you can use
     *         to utilize the Pinging API.
     *
     * @throws IllegalArgumentException If the {@code hostUrl} is invalid.
     *                                  The bare minimum is something like {@code http://localhost},
     *                                  but the Protocol (http/https), Host (e.g. hc-ping.com),
     *                                  (optionally) custom Port (e.g. 8080) and sub path (e.g. /healthchecks)
     *                                  will be taken into account as well!
     */
    public static HealthchecksClient forUuid(String hostUrl, String uuid) {
        return new HealthchecksClientImpl(hostUrl, uuid);
    }

    /**
     * <p>Creates a new healthchecks.io Client <b>for an already retrieved {@link Check}</b>.</p>
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
     * @param check The check you already retrieved (probably with a
     *              {@link HealthchecksManager} with a valid {@code pingUrl}.
     *
     * @return A new {@link HealthchecksClient} client, which you can use
     *         to utilize the Pinging API.
     *
     * @throws IllegalArgumentException If the {@code pingUrl} of your Check is invalid.
     */
    public static HealthchecksClient forCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();

        return new HealthchecksClientImpl(check.getPingUrl());
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
}
