package com.github.relucent.base.common.collection;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 数组迭代器{@link Iterator}适配类
 */
public class ArrayIterator<E> implements Iterator<E> {

    // =================================Fields========================================
    /** 数组 */
    private final Object array;
    /** 数组长度 */
    private final int length;
    /** 当前位置 */
    private int cursor = 0;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param array 数组
     * @throws IllegalArgumentException array对象不为数组抛出此异常
     */
    public ArrayIterator(final Object array) {
        this.array = Objects.requireNonNull(array);
        this.length = Array.getLength(array);
    }

    // ==============================Methods==========================================
    @Override
    public boolean hasNext() {
        return (cursor < length);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return (E) Array.get(array, cursor++);
    }
}
