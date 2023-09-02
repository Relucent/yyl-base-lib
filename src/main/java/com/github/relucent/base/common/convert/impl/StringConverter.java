package com.github.relucent.base.common.convert.impl;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.TimeZone;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.time.DateUtil;

/**
 * 字符串类型转换器
 * @author YYL
 */
public class StringConverter implements Converter<String> {

    public static final StringConverter INSTANCE = new StringConverter();

    @Override
    public String convert(Object source, Class<? extends String> toType) {
        try {
            return convertInternal(source);
        } catch (Exception ignore) {
            return null;
        }
    }

    protected String convertInternal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof CharSequence) {
            return value.toString();
        }
        if (value instanceof Date) {
            return DateUtil.format((Date) value);
        }
        if (value instanceof TimeZone) {
            return ((TimeZone) value).getID();
        }
        if (value instanceof Clob) {
            return clobToString((Clob) value);
        }
        if (value instanceof Blob) {
            return blobToString((Blob) value);
        }
        if (value instanceof Type) {
            return ((Type) value).getTypeName();
        }
        return value.toString();
    }

    /**
     * Clob字段值转字符串
     * @param clob {@link Clob}
     * @return 字符串
     */
    private static String clobToString(Clob clob) {
        try {
            try (Reader reader = clob.getCharacterStream()) {
                return IoUtil.toString(reader);
            }
        } catch (Exception e) {
            throw ExceptionHelper.propagate(e);
        }
    }

    /**
     * Blob字段值转字符串
     * @param blob {@link Blob}
     * @return 字符串
     */
    private static String blobToString(Blob blob) {
        try {
            try (InputStream input = blob.getBinaryStream()) {
                return IoUtil.toString(input);
            }
        } catch (Exception e) {
            throw ExceptionHelper.propagate(e);
        }
    }
}
