package com.niklasarndt.healthchecks;

import com.niklasarndt.healthchecksio.Healthchecks;
import com.niklasarndt.healthchecksio.HealthchecksInfo;
import okhttp3.Response;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {

    private Healthchecks healthchecks;

    @BeforeAll
    public void setUp() {
        String uuid = System.getenv("HEALTHCHECKS_UUID");

        if (uuid != null)
            healthchecks = Healthchecks.forUuid(uuid);
        else
            System.err.println("WARNING: The environment variable HEALTHCHECKS_UUID has not been " +
                    "set. To run API-related tests, please change this!");
    }

    @Test
    public void testApi() throws ExecutionException, InterruptedException {
        if (healthchecks == null)
            return;

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
        assertThrows(IllegalArgumentException.class, () ->
                Healthchecks.forUuid("", null));
        assertThrows(IllegalArgumentException.class, () ->
                Healthchecks.forUuid("http://", null));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost", null));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost:8080", null));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost/healthchecks", null));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("http://localhost:8080/healthchecks", null));
        assertDoesNotThrow(() ->
                Healthchecks.forUuid("https://some.domain.com:8080/healthchecks", null));

    }
}
