package com.niklasarndt.healthchecksio;

import com.niklasarndt.healthchecksio.exception.UnauthorizedException;
import com.niklasarndt.healthchecksio.model.Check;
import com.niklasarndt.healthchecksio.model.Integration;
import com.niklasarndt.healthchecksio.model.Ping;
import com.niklasarndt.healthchecksio.model.StatusFlip;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Utilizes the <a href="https://healthchecks.io/docs/api/">Management API</a>
 * to manage or read checks/pings on a healthchecks.io host.</p>
 *
 * @since 1.0.1
 */
public interface HealthchecksManager {

    /**
     * <p>Requests a {@link Check} from the API <i>again</i>,
     * based on the UUID or unique key.</p>
     *
     * @param check The already existing check with a valid UUID or unique key.
     *
     * @return A {@link Check}, wrapped in a {@link CompletableFuture} object
     *         for async execution.
     */
    default CompletableFuture<Check> getCheck(Check check) {
        return getCheck(check.getUuidOrUniqueKey());
    }

    /**
     * <p>Requests a {@link Check} from the API,
     * based on the UUID or unique key {@code key}.</p>
     *
     * @param key A valid UUID or unique key for a check.
     *
     * @return A {@link Check}, wrapped in a {@link CompletableFuture} object
     *         for async execution.
     */
    CompletableFuture<Check> getCheck(String key);

    /**
     * <p>Requests a {@link Check} from the API,
     * based on the name provided.</p>
     *
     * <p>Please note that this method requests <b>all</b> checks
     * from the API, since no query method by name exists natively.</p>
     *
     * @param name The name of the check.
     *
     * @return A {@link Check}, wrapped in a {@link CompletableFuture} object
     *         for async execution, or {@code null} if no check with that name exists.
     */
    CompletableFuture<Check> getCheckByName(String name);

    /**
     * <p>Requests multiple {@link Check}s from the API.</p>
     *
     * <p>If you want to do this (<i>optional</i>), you can specify multiple tags. If specified,
     * the healthchecks.io instance will only return checks which have <b>all</b>
     * the tags provided.</p>
     *
     * @param tags <i>(optional)</i> The tags which all returned checks should have
     *
     * @return An array of {@link Check}s, wrapped in a {@link CompletableFuture} object
     *         for async execution.
     */
    CompletableFuture<Check[]> getExistingChecks(String... tags);

    /**
     * <p>Creates a new {@link Check} based on the input data provided.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>To use this method, create a new Check via {@code new Check()} and
     * fill in the values you want to change (e.g. name, schedule). The library/
     * healthchecks.io will fill in the default values for all the other data
     * automatically.</p>
     *
     * <p><i>Invalid data will lead to a failing request.</i></p>
     *
     * @param check The raw check data (doesn't need to contain any data at all).
     *
     * @return A {@link Check}, wrapped in a {@link CompletableFuture} object
     *         for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (healthchecks.io rejects
     *                               the request).
     */
    CompletableFuture<Check> createCheck(Check check);

    /**
     * <p>Updates an already {@link Check}.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getCheck(String)} and
     * edit in the values you want to change (e.g. description, schedule). The library/
     * healthchecks.io will fill in the default values for all the other data
     * automatically.</p>
     *
     * <p>Some data (like the ping url) will obviously not be sent back to the healthchecks.io
     * instance, since these values are static.</p>
     *
     * <p><i>Invalid data will lead to a failing request.</i></p>
     *
     * @param check The altered {@link Check} data.
     *
     * @return The updated {@link Check}, wrapped in a {@link CompletableFuture} object
     *         for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you won't have
     *                               the UUID or the permission to update checks).
     */
    CompletableFuture<Check> updateCheck(Check check);

    /**
     * <p>Pauses a {@link Check}.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getCheck(String)} and
     * then invoke this method.</p>
     *
     * @param check The raw check data (doesn't need to contain any data at all).
     *
     * @return The {@link Check} which has just been paused,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    default CompletableFuture<Check> pauseCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return pauseCheck(check.getUuid());
    }

    /**
     * <p>Pauses a {@link Check} with the UUID provided.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>An invalid UUID will lead to a failing request.</p>
     *
     * @param uuid A valid UUID for a {@link Check} you have control over.
     *
     * @return The {@link Check} which has just been paused,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    CompletableFuture<Check> pauseCheck(String uuid);

    /**
     * <p>Deletes a {@link Check}. The check data will
     * be <b>permanently gone</b>, so make sure you know what you are doing.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getCheck(String)} and
     * then invoke this method.</p>
     *
     * @param check The obtained check (it's UUID is the important data).
     *
     * @return The {@link Check} which has just been deleted,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    default CompletableFuture<Check> deleteCheck(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return deleteCheck(check.getUuid());
    }

    /**
     * <p>Deletes a {@link Check} with the specified UUID. The check data will
     * be <b>permanently gone</b>, so make sure you know what you are doing.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * @param uuid A valid UUID for a {@link Check} you have control over.
     *
     * @return The {@link Check} which has just been deleted,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    CompletableFuture<Check> deleteCheck(String uuid);

    /**
     * <p>Lists all {@link Ping}s of a {@link Check}. If you use
     * a free account, this number is limited to 100, otherwise to 1000.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getCheck(String)} and
     * then invoke this method.</p>
     *
     * @param check The obtained check (it's UUID is the important data).
     *
     * @return An array of {@link Ping}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    default CompletableFuture<Ping[]> getPings(Check check) {
        if (check.isReadOnly())
            throw new UnauthorizedException();
        return getPings(check.getUuid());
    }

    /**
     * <p>Lists all {@link Ping}s of a {@link Check} with the specified UUID. If you use
     * a free account, this number is limited to 100, otherwise to 1000.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * @param uuid A valid UUId for a {@link Check} you have control over.
     *
     * @return An array of {@link Ping}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (you don't have an UUID).
     */
    CompletableFuture<Ping[]> getPings(String uuid);

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} (in the last 60 years).</p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getExistingChecks(String...)}
     * and then invoke this method.</p>
     *
     * @param check An already existing {@link Check}.
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    default CompletableFuture<StatusFlip[]> getFlips(Check check) {
        return getFlips(check.getUuidOrUniqueKey());
    }

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} (in the last {@code seconds} seconds).</p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getExistingChecks(String...)}
     * and then invoke this method.</p>
     *
     * @param check   An already existing {@link Check}.
     * @param seconds The maximum amount of seconds which have passed since the status flip.
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    default CompletableFuture<StatusFlip[]> getFlips(Check check, long seconds) {
        return getFlips(check.getUuidOrUniqueKey(), seconds);
    }

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} (in the time frame).</p>
     *
     * <p>To use this method, obtain a Check, e.g. via {@link #getExistingChecks(String...)}
     * and then invoke this method.</p>
     *
     * <p>The parameters are UNIX timestamps (e.g. System.currentTimeMillis()).</p>
     *
     * @param check An already existing {@link Check}.
     * @param start The start date of the time frame (UNIX timestamp).
     * @param end   The end date of the time frame (UNIX timestamp).
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    default CompletableFuture<StatusFlip[]> getFlips(Check check, long start, long end) {
        return getFlips(check.getUuidOrUniqueKey(), start, end);
    }

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} with
     * the UUID or unique key (in the last 60 years).</p>
     *
     * @param key A valid UUID or unique key (read-only mode).
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    default CompletableFuture<StatusFlip[]> getFlips(String key) {
        //Seconds value: https://github.com/healthchecks/healthchecks/blob/63beeb05a177f4c47d4bad46b511d37973b38416/hc/api/forms.py#L19
        return getFlips(key, 31536000);
    }

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} with
     * the UUID (in the last {@code seconds} seconds).</p>
     *
     * @param key     A valid UUID or unique key (read-only mode).
     * @param seconds The maximum amount of seconds which have passed since the status flip.
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    CompletableFuture<StatusFlip[]> getFlips(String key, long seconds);

    /**
     * <p>Lists all {@link StatusFlip}s of a {@link Check} with
     * the UUID or unique key (in the time frame).</p>
     *
     * <p>The parameters are UNIX timestamps (e.g. System.currentTimeMillis()).</p>
     *
     * @param key   A valid UUID or unique key (read-only mode).
     * @param start The start date of the time frame (UNIX timestamp).
     * @param end   The end date of the time frame (UNIX timestamp).
     *
     * @return An array of {@link StatusFlip}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     */
    CompletableFuture<StatusFlip[]> getFlips(String key, long start, long end);

    /**
     * <p>Lists all {@link Integration}s registered for this account.</p>
     *
     * <p><b>Not available in read-only mode!</b></p>
     *
     * @return An array of {@link Integration}s,
     *         wrapped in a {@link CompletableFuture} object for async execution.
     *
     * @throws UnauthorizedException If you are in read-only mode (healthchecks.io
     *                               rejects your request).
     */
    CompletableFuture<Integration[]> getChannels();

}
