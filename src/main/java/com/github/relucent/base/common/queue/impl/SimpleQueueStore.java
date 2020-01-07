package com.github.relucent.base.common.queue.impl;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.github.relucent.base.common.queue.Distinct;
import com.github.relucent.base.common.queue.QueueStore;

/***
 * 数据队列(存储)实现类
 */
public class SimpleQueueStore<T> implements QueueStore<T> {

    // ==============================Fields===========================================
    private static final int DEFAULT_PRIORITY = 5;
    private final Queue<ElementEntry<T>> store;
    private final Distinct<T> distinct;

    // ==============================Constructors=====================================
    public SimpleQueueStore() {
        this(NoneDistinct.<T>instance());
    }

    public SimpleQueueStore(Distinct<T> distinct) {
        this.store = new PriorityBlockingQueue<ElementEntry<T>>(31, new Comparator<ElementEntry<T>>() {
            @Override
            public int compare(ElementEntry<T> left, ElementEntry<T> right) {
                return -(Integer.compare(left.priority, right.priority));
            }
        });
        this.distinct = distinct;
    }

    // ==============================Methods==========================================
    @Override
    public void push(T element) {
        if (distinct.add(element)) {
            push(element, DEFAULT_PRIORITY);
        }
    }

    @Override
    public void push(T element, int priority) {
        ElementEntry<T> elem = toWrapper(element, priority);
        distinct.reomve(element);
        store.offer(elem);
    }

    @Override
    public T poll() {
        ElementEntry<T> elem = store.poll();
        return elem != null ? elem.value : null;
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    public int size() {
        return store.size();
    }

    private ElementEntry<T> toWrapper(T element, int priority) {
        return new ElementEntry<>(element, priority);
    }

    // ==============================InnerClass=======================================
    private static class ElementEntry<T> {
        private T value;
        private int priority;

        public ElementEntry(T value, int priority) {
            this.value = value;
            this.priority = priority;
        }
    }
}
