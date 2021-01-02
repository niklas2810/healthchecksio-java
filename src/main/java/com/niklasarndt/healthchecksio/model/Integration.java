package com.niklasarndt.healthchecksio.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * <p>The model class {@link Integer} represents a notification channel (only available with
 * full api key).</p>
 *
 * <p>For more information on the models, visit the
 * <a href="https://github.com/niklas2810/healthchecksio-java/wiki/Model-Overview">wiki</a>.</p>
 *
 * @since 1.0.1
 */
@Getter
@ToString
@EqualsAndHashCode
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
