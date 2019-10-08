package com.github.relucent.base.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Map包装类，通过包装一个已有Map实现特定功能。例如自定义Key的规则或Value规则
 * @param <K> 键类型
 * @param <V> 值类型
 */
@SuppressWarnings("serial")
public class MapWrapper<K, V> implements Map<K, V>, Serializable, Cloneable {

    // ==============================Fields==============================================
    /** 默认负载因子 */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /** 默认初始大小 */
    protected static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** 原始的MAP */
    protected final Map<K, V> raw;

    // ==============================Constructors========================================
    /**
     * 构造函数
     * @param raw 被包装的Map
     */
    public MapWrapper(Map<K, V> raw) {
        this.raw = raw;
    }

    // ==============================Methods=============================================
    @Override
    public int size() {
        return raw.size();
    }

    @Override
    public boolean isEmpty() {
        return raw.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return raw.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return raw.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return raw.get(key);
    }

    @Override
    public V put(K key, V value) {
        return raw.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return raw.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        raw.putAll(m);
    }

    @Override
    public void clear() {
        raw.clear();
    }

    @Override
    public Set<K> keySet() {
        return raw.keySet();
    }

    @Override
    public Collection<V> values() {
        return raw.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return raw.entrySet();
    }

    @Override
    public String toString() {
        Iterator<Entry<K, V>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (;;) {
            Entry<K, V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            if (key == this || key == raw) {
                builder.append("(this Map)");
            } else if (key instanceof MapWrapper) {
                builder.append("(Map:").append(((Map<?, ?>) key).size()).append(")");
            } else {
                builder.append(key);
            }
            builder.append('=');
            if (value == this || value == raw) {
                builder.append("(this Map)");
            } else if (value instanceof MapWrapper) {
                builder.append("(Map:").append(((Map<?, ?>) value).size()).append(")");
            } else {
                builder.append(value);
            }
            if (!i.hasNext()) {
                return builder.append('}').toString();
            }
            builder.append(',').append(' ');
        }
    }
}
