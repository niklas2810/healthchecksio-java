package com.niklasarndt.healthchecksio.util;

/**
 * Specialization of {@link Pair}, accepting two {@link String}s.
 *
 * @since 1.0.1
 */
public class QueryParam extends Pair<String, String> {

    public QueryParam(String key, String value) {
        super(key, value);
    }
}
