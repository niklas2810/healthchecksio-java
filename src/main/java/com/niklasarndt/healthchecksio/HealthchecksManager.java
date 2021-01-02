package com.niklasarndt.healthchecksio;

import com.niklasarndt.healthchecksio.exception.UnauthorizedException;
import com.niklasarndt.healthchecksio.model.Check;
import com.niklasarndt.healthchecksio.model.Integration;
import com.niklasarndt.healthchecksio.model.Ping;
import com.niklasarndt.healthchecksio.model.StatusFlip;
import java.util.concurrent.CompletableFuture;

public interface HealthchecksManager {

    default CompletableFuture<Check> getCheck(Check check) {
        return getCheck(check.getUuidOrUniqueKey());
    }

    CompletableFuture<Check> getCheck(String uuid);

    CompletableFuture<Check> getCheckByName(String name);

    CompletableFuture<Check[]> getExistingChecks(String... tags);

    CompletableFuture<Check> createCheck(Check check);

    CompletableFuture<Check> updateCheck(Check check);

    default CompletableFuture<Check> pauseCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return pauseCheck(check.getUuid());
    }

    CompletableFuture<Check> pauseCheck(String uuid);

    default CompletableFuture<Check> deleteCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return deleteCheck(check.getUuid());
    }

    CompletableFuture<Check> deleteCheck(String uuid);

    default CompletableFuture<Ping[]> getPings(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return getPings(check.getUuid());
    }

    CompletableFuture<Ping[]> getPings(String uuid);

    default CompletableFuture<StatusFlip[]> getFlips(Check check) {
        return getFlips(check.getUuidOrUniqueKey());
    }

    default CompletableFuture<StatusFlip[]> getFlips(Check check, long seconds) {
        return getFlips(check.getUuidOrUniqueKey(), seconds);
    }

    default CompletableFuture<StatusFlip[]> getFlips(Check check, long start, long end) {
        return getFlips(check.getUuidOrUniqueKey(), start, end);
    }

    default CompletableFuture<StatusFlip[]> getFlips(String uuid) {
        return getFlips(uuid, Long.MAX_VALUE);
    }

    CompletableFuture<StatusFlip[]> getFlips(String uuid, long seconds);

    CompletableFuture<StatusFlip[]> getFlips(String uuid, long start, long end);

    CompletableFuture<Integration[]> getChannels();

}
