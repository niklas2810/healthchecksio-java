package com.niklasarndt.healthchecksio.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Integration {

    /**
     * The UUID of the integration.
     */
    private String id;
    /**
     * The name of the integration (e.g. Private Email).
     */
    private String name;
    /**
     * The integration type (e.g. sms, email, telegram)
     */
    private String kind;
}
