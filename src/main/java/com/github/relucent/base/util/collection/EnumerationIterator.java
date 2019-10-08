package com.github.relucent.base.util.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * 枚举对象{@link Enumeration}迭代器{@link Iterator}适配类
 * @param <E> 元素类型
 */
@SuppressWarnings("serial")
public class EnumerationIterator<E> implements Iterator<E>, Iterable<E>, Serializable {

    private final Enumeration<E> enumeration;

    /**
     * 构造
     * @param enumeration {@link Enumeration}对象
     */
    public EnumerationIterator(Enumeration<E> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public E next() {
        return enumeration.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return this;
    }
}
