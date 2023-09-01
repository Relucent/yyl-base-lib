package com.github.relucent.base.common.reflect;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * {@link ParameterizedType} 接口实现，用于重新定义泛型类型
 */
class ParameterizedTypeImpl implements ParameterizedType, Serializable {

    // =================================Constants==============================================
    private static final long serialVersionUID = 0;

    // =================================Fields=================================================
    private final Type ownerType;
    private final Type rawType;
    private final Type[] actualTypeArguments;

    // =================================Constructors===========================================
    /**
     * 构造
     * @param ownerType 拥有者类型
     * @param rawType 原始类型
     * @param actualTypeArguments 实际的泛型参数类型
     */
    public ParameterizedTypeImpl(Type ownerType, Type rawType, Type[] actualTypeArguments) {
        if (rawType instanceof Class<?>) {
            Class<?> rawTypeAsClass = (Class<?>) rawType;
            boolean isStaticOrTopLevelClass = Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null;
            Preconditions.checkArgument(ownerType != null || isStaticOrTopLevelClass);
        }
        this.ownerType = ownerType == null ? null : TypeUtil.canonicalize(ownerType);
        this.rawType = TypeUtil.canonicalize(rawType);
        this.actualTypeArguments = actualTypeArguments.clone();
        for (int t = 0, length = this.actualTypeArguments.length; t < length; t++) {
            Preconditions.checkNotNull(this.actualTypeArguments[t]);
            Preconditions.checkNotPrimitive(this.actualTypeArguments[t]);
            this.actualTypeArguments[t] = TypeUtil.canonicalize(this.actualTypeArguments[t]);
        }
    }

    // =================================Methods================================================
    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return actualTypeArguments.clone();
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
