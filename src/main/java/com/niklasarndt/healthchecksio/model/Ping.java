package com.niklasarndt.healthchecksio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niklasarndt.healthchecksio.json.TimestampMsDeserializer;
import lombok.Getter;
import lombok.ToString;
import java.util.Date;

@Getter
@ToString
public class Ping {

    private String type;

    @JsonProperty("date")
    @JsonDeserialize(using = TimestampMsDeserializer.class)
    private Date timestamp;

    @JsonProperty("n")
    private int pingIndex;

    private String scheme;

    @JsonProperty("remote_addr")
    private String remoteAddress;

    private String method;

    @JsonProperty("ua")
    private String userAgent;

    private double duration;
}
