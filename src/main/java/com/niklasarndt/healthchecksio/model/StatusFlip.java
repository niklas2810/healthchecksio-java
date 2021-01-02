package com.niklasarndt.healthchecksio.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niklasarndt.healthchecksio.json.BinaryBooleanDeserializer;
import com.niklasarndt.healthchecksio.json.TimestampDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.util.Date;

/**
 * <p>The model class {@link StatusFlip} represents a status change (check went up or down).</p>
 *
 * <p>For more information on the models, visit the
 * <a href="https://github.com/niklas2810/healthchecksio-java/wiki/Model-Overview">wiki</a>.</p>
 *
 * @since 1.0.1
 */
@Getter
@ToString
@EqualsAndHashCode
public class StatusFlip {

    @JsonDeserialize(using = TimestampDeserializer.class)
    private Date timestamp;

    @JsonDeserialize(using = BinaryBooleanDeserializer.class)
    private boolean up;
}
