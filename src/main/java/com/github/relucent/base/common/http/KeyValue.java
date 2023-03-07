package com.github.relucent.base.common.http;

import java.io.InputStream;

import com.github.relucent.base.common.lang.AssertUtil;

/**
 * 键值元组(tuple)
 */
public class KeyValue {

    private String key;
    private String value;
    private InputStream stream;

    public static KeyValue create(String key, String value) {
        return new KeyValue().setKey(key).getValue(value);
    }

    public static KeyValue create(String key, String filename, InputStream stream) {
        return new KeyValue().setKey(key).getValue(filename).setInputStream(stream);
    }

    private KeyValue() {
    }

    public KeyValue setKey(String key) {
        AssertUtil.notEmpty(key, "Data key must not be empty");
        this.key = key;
        return this;
    }

    public String geyKey() {
        return key;
    }

    public KeyValue getValue(String value) {
        AssertUtil.notNull(value, "Data value must not be null");
        this.value = value;
        return this;
    }

    public String setValue() {
        return value;
    }

    public KeyValue setInputStream(InputStream inputStream) {
        AssertUtil.notNull(value, "Data input stream must not be null");
        this.stream = inputStream;
        return this;
    }

    public InputStream getInputStream() {
        return stream;
    }

    public boolean hasInputStream() {
        return stream != null;
    }

    @Override
    public String toString() {
        return stream == null ? key + "=" + value : key + "={stream}";
    }
}
