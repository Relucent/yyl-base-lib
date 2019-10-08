package com.github.relucent.base.util.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * List 包装类，通过包装一个已有List实现特定功能。
 * @param <E> 元素类型
 */
@SuppressWarnings("serial")
public class ListWrapper<E> implements List<E>, Serializable, Cloneable {

    // ==============================Fields==============================================
    /** 默认初始大小 */
    protected static final int DEFAULT_CAPACITY = 16;
    /** 原始的List */
    protected final List<E> raw;

    // ==============================Constructors========================================
    /**
     * 构造函数
     * @param raw 被包装的List
     */
    public ListWrapper(List<E> raw) {
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
    public boolean contains(Object o) {
        return raw.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return raw.iterator();
    }

    @Override
    public Object[] toArray() {
        return raw.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return raw.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return raw.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return raw.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return raw.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return raw.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return raw.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return raw.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return raw.retainAll(c);
    }

    @Override
    public void clear() {
        raw.clear();
    }

    @Override
    public E get(int index) {
        return raw.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (index < raw.size()) {
            return raw.set(index, element);
        } else {
            while (index != raw.size()) {
                raw.add((E) null);
            }
            raw.add(element);
            return null;
        }
    }

    @Override
    public void add(int index, E element) {
        if (index <= raw.size()) {
            raw.add(index, element);
        } else {
            while (index != raw.size()) {
                raw.add((E) null);
            }
            raw.add(element);
        }
    }

    @Override
    public E remove(int index) {
        if (index >= raw.size()) {
            return null;
        }
        return raw.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return raw.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return raw.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return raw.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return raw.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return raw.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        Iterator<E> it = iterator();
        if (!it.hasNext()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (;;) {
            E e = it.next();
            if (e == null) {
                builder.append("null");
            } else if (e == this || e == raw) {
                builder.append("(this Collection)");
            } else if (e instanceof ListWrapper) {
                builder.append("(List:").append(((ListWrapper<?>) e).size()).append(")");
            } else {
                builder.append(e);
            }
            if (!it.hasNext()) {
                return builder.append(']').toString();
            }
            builder.append(',').append(' ');
        }
    }
}
