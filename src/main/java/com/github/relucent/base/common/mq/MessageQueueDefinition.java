package com.github.relucent.base.common.mq;

import java.util.Objects;

import com.github.relucent.base.common.reflect.TypeReference;

/**
 * 消息队列定义
 * @param <T> 消息队列元素类型
 */
public class MessageQueueDefinition<T> {

    /** 消息队列名称 */
    private final String name;
    /** 消息队列元素类型 */
    private final TypeReference<T> elementType;

    /**
     * 消息队列定义构造函数
     * @param name 消息队列名称
     * @param elementType 消息队列元素的类型引用
     */
    protected MessageQueueDefinition(String name, TypeReference<T> elementType) {
        this.name = name;
        this.elementType = elementType;
    }

    /**
     * 获得一个消息队列定义
     * @param <T> 消息队列元素类型
     * @param name 消息队列名称
     * @param elementType 消息队列元素类型引用
     * @return 消息队列定义实例
     */
    public static <T> MessageQueueDefinition<T> of(String name, TypeReference<T> elementType) {
        return new MessageQueueDefinition<>(name, elementType);
    }

    /**
     * 获得一个消息队列定义
     * @param <T> 消息队列元素类型
     * @param name 消息队列名称
     * @param elementType 消息队列元素类型
     * @return 消息队列定义实例
     */
    public static <T> MessageQueueDefinition<T> of(String name, Class<T> elementType) {
        return of(name, TypeReference.<T>of(elementType));
    }

    /**
     * 获得消息队列名称
     * @return 消息队列名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获得消息队列元素的类型引用
     * @return 消息队列元素的类型引用
     */
    public TypeReference<T> getElementType() {
        return elementType;
    }

    public Builder<T> newBuilder() {
        return new Builder<>(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elementType, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass().equals(obj.getClass())) {
            return false;
        }
        MessageQueueDefinition<?> other = (MessageQueueDefinition<?>) obj;
        return Objects.equals(name, other.name) && Objects.equals(elementType, other.elementType);
    }

    @Override
    public String toString() {
        return "MessageQueueDefinition [name=" + name + ", elementType=" + elementType + "]";
    }

    public static class Builder<T> {
        private String name;
        private TypeReference<T> elementType;

        protected Builder(MessageQueueDefinition<T> definition) {
            this.name = definition.name;
            this.elementType = definition.elementType;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> elementType(TypeReference<T> elementType) {
            this.elementType = elementType;
            return this;
        }

        public MessageQueueDefinition<T> build() {
            return new MessageQueueDefinition<>(name, elementType);
        }
    }
}
