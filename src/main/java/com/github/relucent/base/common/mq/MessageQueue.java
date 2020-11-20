package com.github.relucent.base.common.mq;

import java.util.function.Consumer;

/**
 * 消息队列接口类
 * @param <T> 消息元素类型
 */
public interface MessageQueue<T> {

    /**
     * 获取消息队列名称
     * @return 消息队列名称
     */
    String getName();

    /**
     * 发布消息
     * @param element 消息元素
     */
    void publish(T element);

    /**
     * 订阅消息
     * @param subscriber 消息订阅者
     */
    void subscribe(Subscriber<T> subscriber);

    /**
     * 消息订阅者
     * @param <T> 消息元素类型
     */
    interface Subscriber<T> extends Consumer<T> {
    }
}
