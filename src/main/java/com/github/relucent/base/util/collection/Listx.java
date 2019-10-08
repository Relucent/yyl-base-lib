package com.github.relucent.base.util.collection;

import java.util.ArrayList;
import java.util.Date;

import com.github.relucent.base.util.convert.ConvertUtil;

/**
 * 增强版List接口的实现类。<br>
 */
@SuppressWarnings("serial")
public class Listx extends ListWrapper<Object> {

    // ==============================Fields==============================================
    // ...

    // ==============================Constructors========================================
    public Listx() {
        super(new ArrayList<>());
    }

    // ==============================Methods=============================================
    public Boolean getBoolean(int index) {
        return getBoolean(index, null);
    }

    public Integer getInteger(int index) {
        return getInteger(index, null);
    }

    public Long getLong(int index) {
        return getLong(index, null);
    }

    public Float getFloat(int index) {
        return getFloat(index, null);
    }

    public Double getDouble(int index) {
        return getDouble(index, null);
    }

    public Date getDate(int index) {
        return getDate(index, null);
    }

    public String getString(int index) {
        return getString(index, null);
    }

    public <T extends Enum<T>> T getEnum(int index, Class<T> enumType) {
        return getEnum(index, enumType, null);
    }

    public Mapx getMap(int index) {
        return getMap(index, null);
    }

    public Listx getList(int index) {
        return getList(index, null);
    }

    public Boolean getBoolean(int index, Boolean defaultValue) {
        return ConvertUtil.toBoolean(get(index), defaultValue);
    }

    public Integer getInteger(int index, Integer defaultValue) {
        return ConvertUtil.toInteger(get(index), defaultValue);
    }

    public Long getLong(int index, Long defaultValue) {
        return ConvertUtil.toLong(get(index), defaultValue);
    }

    public Float getFloat(int index, Float defaultValue) {
        return ConvertUtil.toFloat(get(index), defaultValue);
    }

    public Double getDouble(int index, Double defaultValue) {
        return ConvertUtil.toDouble(get(index), defaultValue);
    }

    public String getString(int index, String defaultValue) {
        return ConvertUtil.toString(get(index), defaultValue);
    }

    public Date getDate(int index, Date defaultValue) {
        return ConvertUtil.toDate(get(index), defaultValue);
    }

    public <T extends Enum<T>> T getEnum(int index, Class<T> enumType, T defaultEnum) {
        return ConvertUtil.toEnum(get(index), enumType, defaultEnum);
    }

    public Mapx getMap(int index, Mapx defaultValue) {
        return ConvertUtil.toMap(get(index), defaultValue);
    }

    public Listx getList(int index, Listx defaultValue) {
        return ConvertUtil.toList(get(index), defaultValue);
    }
}
