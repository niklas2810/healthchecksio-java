package com.niklasarndt.healthchecksio.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * <p>Internal class to parse zeroes/ones into booleans.</p>
 *
 * <p>0 will be treated as false, everything else as true.</p>
 *
 * @since 1.0.1
 */
public class BinaryBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return parser.getIntValue() != 0;
    }
}
