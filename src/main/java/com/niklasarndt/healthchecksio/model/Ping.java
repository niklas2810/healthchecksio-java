package com.niklasarndt.healthchecksio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niklasarndt.healthchecksio.json.TimestampMsDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.util.Date;


/**
 * <p>The model class {@link Ping} represents a single message to the api
 * (only available with full api access).</p>
 *
 * <p>For more information on the models, visit the
 * <a href="https://github.com/niklas2810/healthchecksio-java/wiki/Model-Overview">wiki</a>.</p>
 *
 * @since 1.0.1
 */
@Getter
@ToString
@EqualsAndHashCode
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
