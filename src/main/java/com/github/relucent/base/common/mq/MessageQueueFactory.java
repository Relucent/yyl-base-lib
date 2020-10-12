package com.github.relucent.base.common.mq;

/**
 * 消息队列工厂接口类
 * @param <T>
 */
public interface MessageQueueFactory<T> {
    /**
     * 创建一个消息队列
     */
    MessageQueue<T> create();
}
