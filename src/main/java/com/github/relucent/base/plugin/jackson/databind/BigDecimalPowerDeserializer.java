package com.github.relucent.base.plugin.jackson.databind;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * BigDecimal反序列化
 */
public class BigDecimalPowerDeserializer extends JsonDeserializer<BigDecimal> {

    /** Singleton instance to use. */
    public final static BigDecimalPowerDeserializer INSTANCE = new BigDecimalPowerDeserializer();

    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String text = parser.getText();
        if (text != null) {
            try {
                return new BigDecimal(text);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }
}
