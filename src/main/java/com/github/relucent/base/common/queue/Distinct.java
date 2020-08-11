package com.github.relucent.base.common.queue;

/**
 * 去除重复的元素
 */
public interface Distinct<T> {

    /**
     * 添加元素
     * @param element 元素
     * @return 如果元素已经存在则返回false,否则返回true.
     */
    boolean add(T element);

    /**
     * 移除元素
     * @param element 元素
     */
    void reomve(T element);

    /**
     * 清空元素
     */
    void clear();

    /** 元素摘要类 */
    public interface DistinctDigester<T> {
        String apply(Object element);
    }
}
