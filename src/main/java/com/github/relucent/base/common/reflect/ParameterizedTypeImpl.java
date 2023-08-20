package com.github.relucent.base.common.reflect;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * {@link ParameterizedType} 接口实现，用于重新定义泛型类型
 */
@SuppressWarnings("serial")
class ParameterizedTypeImpl implements ParameterizedType, Serializable {

    // =================================Fields=================================================
    private final Type[] actualTypeArguments;
    private final Type ownerType;
    private final Type rawType;

    // =================================Constructors===========================================
    /**
     * 构造
     * @param actualTypeArguments 实际的泛型参数类型
     * @param ownerType 拥有者类型
     * @param rawType 原始类型
     */
    public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
        this.actualTypeArguments = actualTypeArguments;
        this.ownerType = ownerType;
        this.rawType = rawType;
    }
    // =================================Methods================================================

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final Type useOwner = ownerType;
        final Class<?> raw = (Class<?>) rawType;
        if (useOwner == null) {
            builder.append(raw.getName());
        } else {
            if (useOwner instanceof Class<?>) {
                builder.append(((Class<?>) useOwner).getName());
            } else {
                builder.append(useOwner.toString());
            }
            builder.append('.').append(raw.getSimpleName());
        }
        builder.append('<');
        if (ArrayUtil.isNotEmpty(actualTypeArguments)) {
            boolean isFirst = true;
            for (Type type : actualTypeArguments) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    builder.append(", ");
                }
                String typeStr;
                if (type instanceof Class) {
                    typeStr = ((Class<?>) type).getName();
                } else {
                    typeStr = type.getTypeName();
                }
                builder.append(typeStr);
            }
        }
        builder.append('>');
        return builder.toString();
    }
}
