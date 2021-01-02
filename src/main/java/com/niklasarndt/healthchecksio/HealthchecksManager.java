package com.niklasarndt.healthchecksio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklasarndt.healthchecksio.exception.ParseException;
import com.niklasarndt.healthchecksio.exception.UnauthorizedException;
import com.niklasarndt.healthchecksio.model.Check;
import com.niklasarndt.healthchecksio.model.Integration;
import com.niklasarndt.healthchecksio.model.Ping;
import com.niklasarndt.healthchecksio.model.StatusFlip;
import com.niklasarndt.healthchecksio.util.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HealthchecksManager {

    private static final String HEALTHCHECKS_HOST = "https://healthchecks.io";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(Healthchecks.USER_AGENT).build();
    private final String host;
    private final String token;
    private final String baseUrl;

    protected HealthchecksManager(String token) {
        this(HEALTHCHECKS_HOST, token);
    }

    protected HealthchecksManager(String host, String token) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(token);
        host = host.trim();

        //URL validation
        if (!host.equals(HEALTHCHECKS_HOST))
            this.host = Healthchecks.validateUrl(host);
        else //Skip URL validation for default host (already validated)
            this.host = host;

        this.token = token;
        this.baseUrl = host + (host.endsWith("/") ? "api/v1" : "/api/v1");
    }

    public CompletableFuture<Check> getCheck(Check check) {
        return getCheck(check.getUuidOrUniqueKey());
    }

    public CompletableFuture<Check> getCheck(String uuid) {
        return parseJsonResponse(request("/checks/" + uuid), Check.class);
    }

    public CompletableFuture<Check> createCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();

        return request("/checks/", writeJson(check)).thenApply(response -> {
            verifyResponse(response);

            if (response.code() != 201)
                throw new IllegalArgumentException("The check has an invalid format, " +
                        "healthchecks.io did not create a new check! "
                        + response.code() + " (" + response.message() + ")");

            String body;
            try {
                body = response.body().string();
            } catch (IOException e) {
                throw new IllegalStateException("Could not read response from healthchecks.io!", e);
            }
            return readJson(body, Check.class);
        });
    }

    public CompletableFuture<Check> updateCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();

        return parseJsonResponse(request("/checks/" + check.getUuid(),
                writeJson(check)),
                Check.class);
    }

    public CompletableFuture<Check> pauseCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return pauseCheck(check.getUuid());
    }

    public CompletableFuture<Check> pauseCheck(String uuid) {
        return parseJsonResponse(request("/checks/" + uuid + "/pause", ""), Check.class);
    }

    public CompletableFuture<Check> deleteCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return deleteCheck(check.getUuid());
    }

    public CompletableFuture<Check> deleteCheck(String uuid) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + "/checks/" + uuid)
                .addHeader("X-Api-Key", token)
                .delete();
        return parseJsonResponse(request(builder.build()), Check.class);
    }

    public CompletableFuture<Check[]> getExistingChecks(String... tags) {
        return parseFirstNode(request("/checks", null,
                Arrays.stream(tags).map(tag -> new Pair<>("tag", tag)).toArray(Pair[]::new)),
                Check[].class);
    }

    public CompletableFuture<Check> getCheckByName(String name) {
        return getExistingChecks().thenApply(checks -> {
            for (Check check : checks) {
                if (check.getName().equals(name))
                    return check;
            }
            return null;
        });
    }

    public CompletableFuture<Ping[]> getPings(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return getPings(check.getUuid());
    }

    public CompletableFuture<Ping[]> getPings(String uuid) {
        return parseFirstNode(request("/checks/" + uuid + "/pings"), Ping[].class);
    }

    public CompletableFuture<StatusFlip[]> getFlips(Check check) {
        return getFlips(check.getUuidOrUniqueKey());
    }

    public CompletableFuture<StatusFlip[]> getFlips(Check check, long seconds) {
        return getFlips(check.getUuidOrUniqueKey(), seconds);
    }

    public CompletableFuture<StatusFlip[]> getFlips(Check check, long start, long end) {
        return getFlips(check.getUuidOrUniqueKey(), start, end);
    }

    public CompletableFuture<StatusFlip[]> getFlips(String uuid) {
        return getFlips(uuid, Long.MAX_VALUE);
    }

    public CompletableFuture<StatusFlip[]> getFlips(String uuid, long seconds) {
        return parseFirstNode(request("/checks/" + uuid + "/flips", null,
                new Pair<>("seconds", seconds + "")), StatusFlip[].class);
    }

    public CompletableFuture<StatusFlip[]> getFlips(String uuid, long start, long end) {
        return parseFirstNode(request("/checks/" + uuid + "/flips", null,
                new Pair<>("start", start + ""), new Pair<>("end", end + "")),
                StatusFlip[].class);
    }

    public CompletableFuture<Integration[]> getChannels() {
        return parseFirstNode(request("/channels"), Integration[].class);
    }

    private <T> CompletableFuture<T> parseFirstNode(CompletableFuture<Response> response, Class<T> clazz) {
        return parseJsonResponse(response, JsonNode.class)
                .thenApply(json -> readJson(json.iterator().next().toString(), clazz));
    }

    private <T> CompletableFuture<T> parseJsonResponse(CompletableFuture<Response> future, Class<T> clazz) {
        return future.thenApply(response -> {
            verifyResponse(response);

            String body;
            try {
                body = response.body().string();
            } catch (IOException e) {
                throw new IllegalStateException("Could not read response from healthchecks.io!", e);
            }
            return readJson(body, clazz);
        });
    }

    private CompletableFuture<Response> request(String path) {
        return request(path, null);
    }

    private CompletableFuture<Response> request(String path, String body, Pair<String, String>... params) {
        String url = baseUrl + path;

        if (params != null) {
            if (!url.endsWith("?"))
                url = url + "?";
            StringBuilder builder = new StringBuilder(url);

            for (Pair<String, String> param : params)
                builder.append(param.getLeft()).append("=").append(param.getRight()).append("&");

            url = builder.substring(0, builder.length() - 1);
        }

        Request.Builder builder = new Request.Builder()
                .addHeader("X-Api-Key", token)
                .url(url);

        if (body != null)
            builder.post(RequestBody.create(body, Healthchecks.PLAIN_TEXT));
        else
            builder.get();

        return request(builder.build());
    }

    private void verifyResponse(Response response) {
        if (response.code() == 401 || response.code() == 403)
            throw new UnauthorizedException();
        if (!response.isSuccessful())
            throw new IllegalStateException("Request to healthchecks.io was not successful: "
                    + response.code() + " (" + response.message() + ")");
    }

    private CompletableFuture<Response> request(Request request) {
        OkHttpResponseFuture callback = new OkHttpResponseFuture();

        Healthchecks.LOG.debug("Sending request to  {} via {} (has body: {})",
                request.url().toString(), request.method(), request.body() != null);

        client.newCall(request).enqueue(callback);
        return callback.future;
    }

    private <T> T readJson(String input, Class<T> clazz) {
        try {
            return MAPPER.readValue(input, clazz);
        } catch (JsonProcessingException e) {
            throw new ParseException("The JSON data could not be parsed", e);
        }
    }

    private String writeJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ParseException("The object could not be translated to JSON", e);
        }
    }
}
