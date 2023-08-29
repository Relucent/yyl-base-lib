package com.github.relucent.base.common.json.impl;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.relucent.base.common.bean.BeanUtil;
import com.github.relucent.base.common.bean.info.BeanPropDesc;
import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.constant.CharConstant;
import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.io.IoRuntimeException;
import com.github.relucent.base.common.json.JsonConfig;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.time.DateUtil;
import com.github.relucent.base.common.time.TemporalAccessorUtil;

/**
 * JSON 字符输出流
 */
public class JsonWriter {

    // ==============================Fields==============================================
    /** 字符流 */
    private final Writer writer;
    /** 本级别缩进量 */
    private final Indenter indenter;
    /** 配置信息 */
    private final JsonConfig config;

    // ==============================Methods=============================================
    /**
     * 创建JSONWriter
     * @param writer {@link Writer}
     * @param config 配置项
     * @return JSONWriter
     */
    public static JsonWriter of(Writer writer, JsonConfig config) {
        return new JsonWriter(writer, config);
    }

    // ==============================Constructors========================================
    /**
     * 构造
     * @param writer {@link Writer}
     * @param config 配置项
     */
    public JsonWriter(Writer writer, JsonConfig config) {
        this.writer = writer;
        this.indenter = new Indenter(config.getindentFactor());
        this.config = config;
    }

    // ==============================Methods=============================================
    public void flush() {
        try {
            this.writer.flush();
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    // ==============================WriteMethods========================================
    /**
     * 写入JSON的值，根据值类型不同，输出不同内容
     * @param value 值
     */
    @SuppressWarnings("rawtypes")
    public void writeObject(Object value) {

        if (JsonNull.isNull(value)) {
            writeNull();
            return;
        }
        if (value instanceof CharSequence) {
            writeString((CharSequence) value);
            return;
        }
        if (value instanceof Boolean) {
            writeBoolean((Boolean) value);
            return;
        }
        if (value instanceof Number) {
            writeNumber((Number) value);
            return;
        }
        if (value instanceof Enum) {
            writeEnum((Enum) value);
            return;
        }
        if (value instanceof Object[]) {
            writeArray((Object[]) value);
            return;
        }

        if (value instanceof Map) {
            writeMap((Map) value);
            return;
        }
        if (value instanceof Iterable) {
            writeIterable((Iterable) value);
            return;
        }
        if (value instanceof Iterator) {
            writeIterator((Iterator<?>) value);
            return;
        }

        if (value instanceof Date) {
            writeDate((Date) value);
            return;
        }
        if (value instanceof Calendar) {
            writeCalendar((Calendar) value);
            return;
        }
        if (value instanceof TemporalAccessor) {
            writeTemporalAccessor((TemporalAccessor) value);
            return;
        }

        Class<?> clazz = value.getClass();
        if (clazz.isInterface()) {
            writeEmpty();
            return;
        }

        writeBean(value);
    }

    /**
     * 写入NULL
     */
    public void writeNull() {
        writeRaw(StringConstant.NULL);
    }

    /**
     * 写入字符串值，并包装引号并转义字符<br>
     * @param value 字符串对象
     */
    private void writeString(CharSequence value) {
        try {
            if (StringUtil.isEmpty(value)) {
                writer.write("\"\"");
                return;
            }
            String string = value.toString();
            writer.write('"');
            for (int i = 0, length = string.length(); i < length; i++) {
                char ch = string.charAt(i);
                switch (ch) {
                case '"':
                case '\\':
                    writer.append('\\');
                    writer.append(ch);
                    break;
                case '\b':
                    writer.append('\\');
                    writer.append('b');
                    break;
                case '\n':
                    writer.append('\\');
                    writer.append('n');
                    break;
                case '\t':
                    writer.append('\\');
                    writer.append('t');
                    break;
                case '\f':
                    writer.append('\\');
                    writer.append('f');
                    break;
                case '\r':
                    writer.append('\\');
                    writer.append('r');
                    break;
                default:
                    if (ch < '\u0020' || //
                            (ch >= '\u0080' && ch <= '\u00a0') || //
                            (ch >= '\u2000' && ch <= '\u2010') || //
                            (ch >= '\u2028' && ch <= '\u202F') || //
                            (ch >= '\u2066' && ch <= '\u206F')//
                    ) {
                        writer.append(Hex.toUnicodeHex(ch));
                    } else {
                        writer.append(Character.toString(ch));
                    }
                }
            }
            writer.write('"');
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 写入布尔值
     * @param bool 布尔值
     */
    private void writeBoolean(Boolean bool) {
        writeRaw(bool.toString());
    }

    /**
     * 写入数值
     * @param number 数值
     */
    private void writeNumber(Number number) {

        boolean isStripTrailingZeros = config.isStripTrailingZeros();

        // BigDecimal单独处理，使用非科学计数法
        if (number instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) number;
            if (isStripTrailingZeros) {
                decimal = decimal.stripTrailingZeros();
            }
            writeRaw(decimal.toPlainString());
            return;
        }

        String csq = number.toString();
        if (isStripTrailingZeros) {
            if (csq.indexOf('.') > 0 && csq.indexOf('e') < 0 && csq.indexOf('E') < 0) {
                while (csq.endsWith("0")) {
                    csq = csq.substring(0, csq.length() - 1);
                }
                if (csq.endsWith(".")) {
                    csq = csq.substring(0, csq.length() - 1);
                }
            }
        }
        writeRaw(csq);
    }

    /**
     * 写入枚举对象
     * @param enumValue 枚举对象
     */
    private void writeEnum(Enum<?> enumValue) {
        writeString(enumValue.name());
    }

    /**
     * 写入Map对象
     * @param map Map对象
     */
    private void writeMap(Map<?, ?> map) {
        final boolean isIgnoreNullValue = config.isIgnoreNullValue();
        try {
            writeRaw(CharConstant.DELIM_START);
            indenter.increment();
            int index = 0;
            for (java.util.Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (JsonNull.isNull(value) && isIgnoreNullValue) {
                    continue;
                }
                if (index != 0) {
                    writeRaw(CharConstant.COMMA);
                }
                writePretty();
                writeString(String.valueOf(key));
                writeRaw(CharConstant.COLON);
                writePrettySpace();
                writeObject(value);
                index++;
            }
            writePretty();
            writeRaw(CharConstant.DELIM_END);
            flush();

            indenter.decrement();
        } catch (Exception e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 写数组
     * @param array 数组
     */
    private void writeArray(Object[] array) {
        writeRaw(CharConstant.BRACKET_START);
        indenter.increment();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writeRaw(CharConstant.COMMA);
            }
            // 换行缩进
            writePretty();
            writeObject(array[i]);
        }
        writePretty();
        writeRaw(CharConstant.BRACKET_END);
        flush();
        indenter.decrement();
    }

    /**
     * 写可迭代的对象
     * @param iterable 可迭代的对象
     */
    private void writeIterable(Iterable<?> iterable) {
        writeRaw(CharConstant.BRACKET_START);
        indenter.increment();
        int i = 0;
        for (Object element : iterable) {
            if ((i++) != 0) {
                writeRaw(CharConstant.COMMA);
            }
            writePretty();
            writeObject(element);
        }
        writePretty();
        writeRaw(CharConstant.BRACKET_END);
        flush();
        indenter.decrement();
    }

    /**
     * 写迭代器
     * @param iterator 迭代器
     */
    private void writeIterator(Iterator<?> iterator) {
        writeRaw(CharConstant.BRACKET_START);
        indenter.increment();
        int i = 0;
        while (iterator.hasNext()) {
            Object element = iterator.next();
            if ((i++) != 0) {
                writeRaw(CharConstant.COMMA);
            }
            // 换行缩进
            writePretty();
            writeObject(element);
        }
        writePretty();
        writeRaw(CharConstant.BRACKET_END);
        flush();
        indenter.decrement();
    }

    /**
     * 写入日期
     * @param date 日期
     */
    private void writeDate(Date date) {
        if (config != null && config.isWriteDateAsTimestamps()) {
            writeRaw(Long.toString(date.getTime()));
            return;
        }
        writeString(DateUtil.formatDateTime(date));
    }

    /**
     * 写入时间（日历）对象
     * @param calendar 时间（日历）对象
     */
    private void writeCalendar(Calendar calendar) {
        if (config != null && config.isWriteDateAsTimestamps()) {
            writeRaw(Long.toString(calendar.getTimeInMillis()));
            return;
        }
        writeDate(calendar.getTime());
    }

    /**
     * 写入时间对象
     * @param time 时间对象 {@link TemporalAccessor}
     */
    private void writeTemporalAccessor(TemporalAccessor time) {
        if (time instanceof MonthDay) {
            writeString(time.toString());
            return;
        }

        if (config != null && config.isWriteDateAsTimestamps()) {
            long timeMillis = TemporalAccessorUtil.toEpochMilli(time);
            writeRaw(Long.toString(timeMillis));
            return;
        }
        writeString(TemporalAccessorUtil.format(time, null));
    }

    /**
     * 写入Bean对象
     * @param bean Bean对象
     */
    private void writeBean(Object bean) {
        Map<String, Object> proxy = new HashMap<>();
        try {
            Class<?> clazz = bean.getClass();
            boolean isTransientSupport = config.isTransientSupport();
            boolean ignoreNullValue = config.isIgnoreNullValue();
            Map<String, BeanPropDesc> pdMap = BeanUtil.getBeanDesc(clazz).getPropMap();
            for (Map.Entry<String, BeanPropDesc> pdEntry : pdMap.entrySet()) {
                String name = pdEntry.getKey();
                BeanPropDesc pd = pdEntry.getValue();

                if (!pd.isReadable(isTransientSupport)) {
                    continue;
                }

                Object value = pd.getValue(bean);

                if (value == null && ignoreNullValue) {
                    continue;
                }
                proxy.put(name, value);
            }
        } catch (Exception e) {
            writeEmpty();
            return;
        }
        writeObject(proxy);
    }

    /**
     * 写入一个对象
     */
    private void writeEmpty() {
        writeRaw("{}");
    }

    /**
     * 写入换行符和缩进
     * @return this
     */
    private JsonWriter writePretty() {
        if (indenter.isPretty()) {
            writeRaw(CharConstant.LF);
            for (int i = indenter.getIndent(); i >= 0; i--) {
                writeRaw(CharConstant.SPACE);
            }
        }
        return this;
    }

    /**
     * 写入空格
     */
    private void writePrettySpace() {
        if (indenter.isPretty()) {
            writeRaw(CharConstant.SPACE);
        }
    }

    /**
     * 写入原始字符串值，不做任何处理
     * @param csq 字符串
     * @return this
     */
    private JsonWriter writeRaw(String csq) {
        try {
            writer.append(csq);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
        return this;
    }

    /**
     * 写入原始字符值，不做任何处理
     * @param c 字符串
     * @return this
     */
    private JsonWriter writeRaw(char c) {
        try {
            writer.write(c);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
        return this;
    }
}
