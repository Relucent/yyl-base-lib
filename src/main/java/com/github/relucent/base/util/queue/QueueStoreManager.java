package com.github.relucent.base.util.queue;

import java.util.Collection;

/**
 * 队列管理器
 */
public interface QueueStoreManager<T> {

    /**
     * 返回队列存储器
     * @param name 队列名(唯一标识)
     * @return 队列存储器
     */
    QueueStore<T> getQueue(String name);

    /**
     * 返回队列管理器中可用队列名称列表
     * @return 队列管理器中可用队列名称列表
     */
    Collection<String> getQueueStoreNames();
}
