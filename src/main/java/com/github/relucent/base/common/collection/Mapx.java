package com.github.relucent.base.common.collection;

import java.util.Date;
import java.util.LinkedHashMap;

import com.github.relucent.base.common.convert.ConvertUtil;

/**
 * 增强版Map接口的实现类。<br>
 * 键为字符串类型，如果输入的键为{@code null}，将作为字符串"null"处理。
 * @author YYL
 */
@SuppressWarnings("serial")
public class Mapx extends MapWrapper<String, Object> {

    // ==============================Fields==============================================
    // ...

    // ==============================Constructors========================================
    public Mapx() {
        super(new LinkedHashMap<>());
    }

    // ==============================Methods=============================================
    @Override
    public Object get(Object key) {
        return super.get(convertKey(key));
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(convertKey(key), value);
    }

    @Override
    public Object remove(Object key) {
        return super.remove(convertKey(key));
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public Date getDate(String key) {
        return getDate(key, null);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        return getEnum(key, enumType, null);
    }

    public Mapx getMap(String key) {
        return getMap(key, null);
    }

    public Listx getList(String key) {
        return getList(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return ConvertUtil.toBoolean(get(key), defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return ConvertUtil.toInteger(get(key), defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return ConvertUtil.toLong(get(key), defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return ConvertUtil.toFloat(get(key), defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        return ConvertUtil.toDouble(get(key), defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return ConvertUtil.toString(get(key), defaultValue);
    }

    public Date getDate(String key, Date defaultValue) {
        return ConvertUtil.toDate(get(key), defaultValue);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumType, T defaultValue) {
        return ConvertUtil.toEnum(get(key), enumType, defaultValue);
    }

    public Mapx getMap(String key, Mapx defaultValue) {
        return ConvertUtil.toMap(get(key), defaultValue);
    }

    public Listx getList(String key, Listx defaultValue) {
        return ConvertUtil.toList(get(key), defaultValue);
    }

    /**
     * 将输入键转换为另一个对象以存储在Map中
     * @param key 键
     * @return 转换后的键
     */
    private String convertKey(final Object key) {
        return String.valueOf(key);
    }
}
