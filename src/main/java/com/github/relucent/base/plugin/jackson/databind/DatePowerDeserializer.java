package com.github.relucent.base.plugin.jackson.databind;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.relucent.base.common.time.DateUtil;

/**
 * 日期反序列化
 */
public class DatePowerDeserializer extends JsonDeserializer<Date> {

    /** Singleton instance to use. */
    public final static DatePowerDeserializer INSTANCE = new DatePowerDeserializer();

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        String text = parser.getText();
        return DateUtil.parseDate(text);
    }
}
