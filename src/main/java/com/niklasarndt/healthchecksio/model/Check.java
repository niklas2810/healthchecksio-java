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
    private int pingAmount;

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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int timeout;

    @JsonProperty("tz")
    private String timeZone;

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

    @JsonIgnore
    public String getUuidOrUniqueKey() {
        return isReadOnly() ? readOnlyKey : getUuid();
    }
}
