package com.github.relucent.base.util.convert.impl;

import java.util.Date;

import com.github.relucent.base.util.convert.Converter;
import com.github.relucent.base.util.time.DateUtil;

/**
 * 字符串类型转换器
 * @author YYL
 */
public class StringConverter implements Converter<String> {

    public static final StringConverter INSTANCE = new StringConverter();

    @Override
    public String convert(Object source, Class<? extends String> toType, String vDefault) {
        String result = vDefault;
        try {
            if (source == null) {
                /* do not handle */
            } else if (source instanceof Date) {
                result = DateUtil.format((Date) source);
            } else {
                return String.valueOf(source);
            }
        } catch (Exception e) {
            // Ignore//
        }
        return result;
    }

    @Override
    public boolean support(Class<? extends String> type) {
        return String.class.isAssignableFrom(type);
    }

    public String convertString(String string) {
        return string != null ? string : "";
    }
}
