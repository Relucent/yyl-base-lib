package com.github.relucent.base.plugin.jackson.databind;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * BigDecimal 序列化
 */
public class BigDecimalPowerSerializer extends JsonSerializer<BigDecimal> {

    /**
     * Singleton instance to use.
     */
    public final static BigDecimalPowerSerializer INSTANCE = new BigDecimalPowerSerializer();

    @Override
    public void serialize(BigDecimal decimal, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (decimal != null) {
            gen.writeString(decimal.toPlainString());
        } else {
            gen.writeNull();
        }
    }

}
