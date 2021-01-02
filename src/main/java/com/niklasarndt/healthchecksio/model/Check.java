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

    @JsonIgnore
    public boolean usesCronSchedule() {
        return schedule != null;
    }

    @JsonIgnore
    public boolean isReadOnly() {
        return readOnlyKey != null;
    }

    @JsonIgnore
    public String getUuid() {
        if (pingUrl == null)
            return null;

        return pingUrl.toString().substring(pingUrl.toString().lastIndexOf("/") + 1);
    }

    public boolean hasIntegration(Integration integration) {
        return hasIntegration(integration.getId());
    }

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

    @JsonIgnore
    public String getUuidOrUniqueKey() {
        return isReadOnly() ? readOnlyKey : getUuid();
    }
}
