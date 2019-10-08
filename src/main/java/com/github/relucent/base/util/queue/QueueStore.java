package com.github.relucent.base.util.queue;

/**
 * 数据队列(存储)接口
 */
public interface QueueStore<T> {

    /**
     * 添加数据到队列中
     * @param element 需要添加的数据
     */
    void push(T element);

    /**
     * 添加数据到队列中
     * @param element 需要添加的数据
     * @param priority 优先级别
     */
    void push(T element, int priority);

    /**
     * 从队列中获取数据(非阻塞)
     * @return 获取数据，如果队列为空则返回NULL
     */
    T poll();

    /**
     * 清空队列
     */
    void clear();

    /**
     * 队列当前长度
     * @return 获取队列当前长度
     */
    int size();
}
