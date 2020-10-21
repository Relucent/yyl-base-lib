package com.github.relucent.base.common.mq;

/**
 * 消息队列工厂接口类
 * @param <T> 消息元素类型
 */
public interface MessageQueueFactory<T> {
    /**
     * 创建一个消息队列
     * @return 消息队列
     */
    MessageQueue<T> create();
}
