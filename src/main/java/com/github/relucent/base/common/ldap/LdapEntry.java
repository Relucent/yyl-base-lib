package com.github.relucent.base.common.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * LDAP条目
 * @author YYL
 * @version 2012-10-13
 */
public class LdapEntry {

    // ==============================Fields===========================================
    /** 区分名(DN，Distinguished Name) */
    private final String dn;
    /** 条目属性 */
    private final Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param dn 区分名
     */
    public LdapEntry(String dn) {
        this.dn = dn;
    }
    // ==============================Methods==========================================

    /**
     * 获得条目的区分名
     * @return 区分名
     */
    public String getDn() {
        return dn;
    }

    /**
     * 设置条目属性
     * @param key 属性键
     * @param value 属性值
     */
    public void put(String key, Object value) {
        if (value != null) {
            List<Object> values = new ArrayList<Object>();
            values.add(value);
            attributes.put(convertKey(key), values);
        }
    }

    /**
     * 设置条目属性
     * @param key 属性键
     * @param value 属性值
     */
    public void put(String key, Number value) {
        if (value != null) {
            put(key, value.toString());
        }
    }

    /**
     * 设置条目属性
     * @param key 属性键
     * @param values 属性值
     */
    public void putAll(String key, List<Object> values) {
        attributes.put(convertKey(key), values);
    }

    /**
     * 获得单条属性
     * @param key 属性键
     * @return 属性值
     */
    public Object get(String key) {
        List<Object> values = getAll(key);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /**
     * 获得属性
     * @param key 属性键
     * @return 属性值
     */
    public List<Object> getAll(String key) {
        return attributes.get(convertKey(key));
    }

    /**
     * 删除属性
     * @param key 属性键
     */
    public void remove(String key) {
        attributes.remove(convertKey(key));
    }

    /**
     * 判断属性是否为空
     * @return 属性是否为空
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * 获得属性键的集合
     * @return 属性键的集合
     */
    public Set<String> keySet() {
        return attributes.keySet();
    }

    /**
     * 转换属性键，将属性键转换为小写（LDAP默认不区分大小写）
     * @param key 属性键
     * @return 转换后的属性键
     */
    protected String convertKey(Object key) {
        if (key != null) {
            return key.toString().toLowerCase();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "dn:" + dn;
    }
}
