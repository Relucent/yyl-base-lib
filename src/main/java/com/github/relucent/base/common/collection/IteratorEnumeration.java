package com.github.relucent.base.common.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * 迭代器{@link Iterator}枚举对象{@link Enumeration}适配类
 * @param <E> 元素类型
 */
@SuppressWarnings("serial")
public class IteratorEnumeration<E> implements Enumeration<E>, Serializable {

    private final Iterator<E> iterator;

    /**
     * 构造函数
     * @param iterator {@link Iterator}对象
     */
    public IteratorEnumeration(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public E nextElement() {
        return iterator.next();
    }
}
