package com.github.relucent.base.common.convert.impl;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Date;
import java.util.TimeZone;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.time.DateUtil;

/**
 * 字符串类型转换器<br>
 * 宽容语义：无法转换时返回 {@code null}（含 Clob、Blob 读取失败的情况），不抛出异常
 * @author YYL
 */
public class StringConverter implements BasicConverter<String> {

    public static final StringConverter INSTANCE = new StringConverter();

    @Override
    public String convertInternal(Object value, Class<? extends String> toType) {
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
     * @return 字符串，读取失败时返回 {@code null}
     */
    private static String clobToString(Clob clob) {
        try (Reader reader = clob.getCharacterStream()) {
            return IoUtil.toString(reader);
        } catch (Exception ignore) {
            // 读取失败按转换失败处理，不抛出异常
            return null;
        }
    }

    /**
     * Blob字段值转字符串
     * @param blob {@link Blob}
     * @return 字符串，读取失败时返回 {@code null}
     */
    private static String blobToString(Blob blob) {
        try (InputStream input = blob.getBinaryStream()) {
            return IoUtil.toString(input);
        } catch (Exception ignore) {
            // 读取失败按转换失败处理，不抛出异常
            return null;
        }
    }
}
