package com.github.relucent.base.common.mq;

/**
 * 消息队列管理器接口类
 * @param <T> 消息元素类型
 */
public interface MessageQueueManager {
    /**
     * 获取与定义的消息队列
     * @param <T> 消息元素类型
     * @param definition 消息队列定义
     * @return 消息队列实例
     */
    <T> MessageQueue<T> getMessageQueue(MessageQueueDefinition<T> definition);
}
