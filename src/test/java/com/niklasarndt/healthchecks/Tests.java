package com.niklasarndt.healthchecks;

import com.niklasarndt.healthchecksio.Healthchecks;
import com.niklasarndt.healthchecksio.HealthchecksInfo;
import okhttp3.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {

    @Test
    public void testApi() throws ExecutionException, InterruptedException {
        String uuid = System.getenv("HEALTHCHECKS_UUID");

        if (uuid == null) {
            System.err.println("WARNING: The environment variable HEALTHCHECKS_UUID has not been " +
                    "set. To run API-related tests, please change this!");
            return;
        }

        Healthchecks healthchecks = Healthchecks.forUuid(uuid);

        final CompletableFuture<Response> then = healthchecks.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(healthchecks.success("THIS WAS AN API TEST RUN")
                .get().isSuccessful());
        assertTrue(then.isDone());
        assertTrue(then.get().isSuccessful());
    }

    @Test
    public void testBuildInfo() {
        assertNotNull(HealthchecksInfo.NAME());
        assertNotNull(HealthchecksInfo.DESCRIPTION());
        assertNotNull(HealthchecksInfo.VERSION());
        assertNotNull(HealthchecksInfo.TARGETJDK());
        assertNotNull(HealthchecksInfo.URL());

        assertNotEquals("UNKNOWN", HealthchecksInfo.NAME());
        assertNotEquals("UNKNOWN", HealthchecksInfo.DESCRIPTION());
        assertNotEquals("UNKNOWN", HealthchecksInfo.VERSION());
        assertNotEquals("UNKNOWN", HealthchecksInfo.TARGETJDK());
        assertNotEquals("UNKNOWN", HealthchecksInfo.URL());
    }

    @Test
    public void testUrlValidation() {
        //This UUID is obviously not valid and just here to satisfy the library.
        String uuid = "abc";

        assertThrows(IllegalArgumentException.class, () ->
                Healthchecks.forUuid("", uuid));
        assertThrows(IllegalArgumentException.class, () ->
                Healthchecks.forUuid("http://", uuid));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost", uuid));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost:8080", uuid));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost/healthchecks", uuid));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost:8080/healthchecks", uuid));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("https://some.domain.com:8080/healthchecks", uuid));

    }
}
