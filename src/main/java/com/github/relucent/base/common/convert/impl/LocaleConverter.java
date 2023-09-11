package com.github.relucent.base.common.convert.impl;

import java.util.Locale;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@code Locale}转换器<br>
 */
public class LocaleConverter implements BasicConverter<Locale> {

    public static final LocaleConverter INSTANCE = new LocaleConverter();

    public Locale convertInternal(Object source, Class<? extends Locale> toType) {
        if (source instanceof Locale) {
            return (Locale) source;
        }
        try {
            String language = StringUtil.string(source);
            if (StringUtil.isNotBlank(language)) {
                String[] items = StringUtil.split(language, "_");
                if (items.length == 1) {
                    return new Locale(items[0]);
                }
                if (items.length == 2) {
                    return new Locale(items[0], items[1]);
                }
                return new Locale(items[0], items[1], items[2]);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
