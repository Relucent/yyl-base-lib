package com.github.relucent.base.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型类型捕获类，用于捕获{@code T}的实际类型
 * @param <T> 泛型类型
 * @author YYL
 */
abstract class TypeCapture<T> {

    /**
     * 返回捕获的类型
     * @return 捕获的类型
     */
    final Type capture() {
        Type superClass = getClass().getGenericSuperclass();
        // sanity check, should never happen
        if (!(superClass instanceof ParameterizedType)) {
            throw new IllegalArgumentException(superClass + " isn't parameterized");
        }
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }
}