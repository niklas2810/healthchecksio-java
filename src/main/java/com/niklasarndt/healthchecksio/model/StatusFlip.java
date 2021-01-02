package com.niklasarndt.healthchecksio.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niklasarndt.healthchecksio.json.TimestampDeserializer;
import lombok.Getter;
import lombok.ToString;
import java.util.Date;

@ToString
public class StatusFlip {

    @JsonDeserialize(using = TimestampDeserializer.class)
    @Getter
    private Date timestamp;

    private int up;

    public boolean isUp() {
        return up == 1;
    }
}
