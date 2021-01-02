package com.niklasarndt.healthchecksio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niklasarndt.healthchecksio.json.TimestampDeserializer;
import lombok.Data;
import java.net.URL;
import java.util.Date;

/**
 * <p>The model class {@link Check} represents a status check, identified by a UUID (write access)
 * or unique key (read-only).</p>
 *
 * <p>For more information on the models, visit the
 * <a href="https://github.com/niklas2810/healthchecksio-java/wiki/Model-Overview">wiki</a>.</p>
 *
 * @since 1.0.1
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Check {

    private String name;

    private String tags;

    @JsonProperty("desc")
    private String description;

    @JsonProperty("grace")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int graceTime;

    @JsonProperty(value = "last_duration", access = WRITE_ONLY)
    private int lastDuration;

    @JsonProperty(value = "n_pings", access = WRITE_ONLY)
    private int pings;

    @JsonProperty(value = "status", access = WRITE_ONLY)
    private String status;

    @JsonProperty(value = "last_ping", access = WRITE_ONLY)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Date lastPing;

    @JsonProperty(value = "next_ping", access = WRITE_ONLY)
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Date nextPing;

    @JsonProperty("manual_resume")
    private boolean manualResume = false;

    private String methods = "";

    @JsonProperty(value = "ping_url", access = WRITE_ONLY)
    private URL pingUrl;

    @JsonProperty(value = "update_url", access = WRITE_ONLY)
    private URL updateUrl;

    @JsonProperty(value = "pause_url", access = WRITE_ONLY)
    private URL pauseUrl;

    @JsonProperty("channels")
    private String integrations;

    private String schedule;

    @JsonProperty("tz")
    private String timeZone;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int timeout;

    @JsonProperty(value = "unique_key", access = WRITE_ONLY)
    private String readOnlyKey;

    private String[] unique;

    /**
     * <p>A healthchecks.io check can either use a
     * schedule in cron-format ({@link #schedule})
     * or a fixed interval of seconds ({@link #timeout}).</p>
     *
     * @return Whether this check uses the cron schedule or a fixed interval.
     */
    @JsonIgnore
    public boolean usesCronSchedule() {
        return schedule != null;
    }

    /**
     * <p>If you retrieve a check from healthchecks.io
     * with a read-only key, you will receive an {@code unique_key}
     * as well, which you can use to identify this check without
     * actually knowing it's UUID.</p>
     *
     * @return Whether this check was retrieved from with
     *         a read-only API key.
     */
    @JsonIgnore
    public boolean isReadOnly() {
        return readOnlyKey != null;
    }

    /**
     * <p>Uses the {@link #pingUrl} of the check (only available with write access!)
     * to obtain the UUID.</p>
     *
     * @return The UUID of the check (or null if read-only).
     */
    @JsonIgnore
    public String getUuid() {
        if (pingUrl == null)
            return null;

        return pingUrl.toString().substring(pingUrl.toString().lastIndexOf("/") + 1);
    }

    /**
     * <p>Checks whether this check has the specified {@link Integration}
     * included as activated.</p>
     *
     * @param integration The integration to test check
     *
     * @return Whether this check contains this notification channel.
     */
    public boolean hasIntegration(Integration integration) {
        return hasIntegration(integration.getId());
    }

    /**
     * <p>Checks whether this check has the specified {@link Integration}
     * id included as activated.</p>
     *
     * @param id The id of the integration to check
     *
     * @return Whether this check contains this notification channels' od.
     */
    public boolean hasIntegration(String id) {
        if (integrations == null || integrations.length() == 0)
            return false;
        String[] opts = integrations.split(",");
        for (String opt : opts) {
            if (opt.equals(id))
                return true;
        }

        return false;
    }

    /**
     * <p>The result of this method depends on whether the
     * check was fetched with a read-only api key.</p>
     *
     * @return If Read-only: Unique Key, otherwise UUID.
     */
    @JsonIgnore
    public String getUuidOrUniqueKey() {
        return isReadOnly() ? readOnlyKey : getUuid();
    }
}
