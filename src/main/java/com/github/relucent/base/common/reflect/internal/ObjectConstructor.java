package com.github.relucent.base.common.reflect.internal;

/**
 * 对象构造器，该实例可用于构建对象<br>
 */
public interface ObjectConstructor<T> {

    /**
     * 构建实例
     * @return 返回一个实例
     */
    public T construct();
}