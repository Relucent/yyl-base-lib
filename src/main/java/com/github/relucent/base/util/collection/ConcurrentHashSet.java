package com.github.relucent.base.util.collection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 集合对象，通过{@link ConcurrentHashMap}实现的线程安全Set，性能高于{@link ConcurrentSkipListSet}。<br>
 * @see Collections#newSetFromMap
 * @see ConcurrentSkipListSet
 * @param <E> 元素类型
 */
@SuppressWarnings("serial")
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {

    // ==============================Fields==============================================
    /** 映射中的值对象，如果值为此对象表示有数据，否则无数据 */
    private static final Object PRESENT = new Object();
    /** 线程安全的Map对象 */
    private final ConcurrentMap<E, Object> map;

    // ==============================Constructors========================================
    /**
     * 构造函数
     */
    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<>();
    }

    /**
     * 构造函数
     * @param initialCapacity 初始大小
     */
    public ConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * 构造函数
     * @param initialCapacity 初始大小
     * @param loadFactor 负载因子
     */
    public ConcurrentHashSet(int initialCapacity, float loadFactor) {
        map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 构造函数
     * @param initialCapacity 初始大小
     * @param loadFactor 负载因子
     * @param concurrencyLevel 线程并发度，预估同时操作数据的线程
     */
    public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
        map = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * 构造函数，根据已有集合构造
     * @param iterable 集合对象{@link Iterable}
     */
    public ConcurrentHashSet(Iterable<E> iterable) {
        map = new ConcurrentHashMap<>();
        for (E e : iterable) {
            add(e);
        }
    }

    // ==============================Methods=============================================
    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }
}
