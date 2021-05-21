package com.github.relucent.base.common.mq.impl;

import java.util.Set;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.collection.ConcurrentHashSet;
import com.github.relucent.base.common.mq.MessageQueue;

/**
 * 本地消息队列
 * @param <T> 消息元素类型
 */
public class LocalMessageQueue<T> implements MessageQueue<T> {

    private final Set<Subscriber<T>> subscribers;
    private final String name;

    /**
     * 构造本地消息队列
     * @param 消息队列名称
     */
    public LocalMessageQueue(String name) {
        this.name = name;
        this.subscribers = new ConcurrentHashSet<>();
    }

    /**
     * 获取消息队列名称
     * @return 消息队列名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 发布消息
     * @param element 消息元素
     */
    @Override
    public void publish(T element) {
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.accept(element);
        }
    }

    /**
     * 订阅消息
     * @param subscriber 消息订阅者
     */
    @Override
    public void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * 退订消息
     * @param subscriber 消息订阅者
     */
    public void unsubscribe(Subscriber<T> subscriber) {
        if (!CollectionUtil.isEmpty(subscribers)) {
            subscribers.remove(subscriber);
        }
    }
}
