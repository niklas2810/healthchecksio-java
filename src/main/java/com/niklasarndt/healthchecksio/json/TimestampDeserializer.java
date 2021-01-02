package com.niklasarndt.healthchecksio.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.niklasarndt.healthchecksio.util.HealthcheckUtils;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Internal class to parse timestamps from the API.
 *
 * @since 1.0.1
 */
public class TimestampDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {

        try {
            String raw = parser.getText();
            return FORMAT.parse(HealthcheckUtils.removeColonFromTimestamp(raw));
        } catch (ParseException ignore) {
            return null;
        }

    }
}
