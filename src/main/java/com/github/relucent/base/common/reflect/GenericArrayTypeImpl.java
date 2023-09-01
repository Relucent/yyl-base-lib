package com.github.relucent.base.common.reflect;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * {@link GenericArrayType} 接口实现
 */
class GenericArrayTypeImpl implements GenericArrayType, Serializable {

    // =================================Constants==============================================
    private static final long serialVersionUID = 0;

    // =================================Fields=================================================
    private final Type componentType;

    // =================================Constructors===========================================
    public GenericArrayTypeImpl(Type componentType) {
        this.componentType = TypeUtil.canonicalize(componentType);
    }

    // =================================Methods================================================
    public Type getGenericComponentType() {
        return componentType;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GenericArrayType && TypeUtil.equals(this, (GenericArrayType) o);
    }

    @Override
    public int hashCode() {
        return componentType.hashCode();
    }

    @Override
    public String toString() {
        return TypeUtil.typeToString(componentType) + "[]";
    }
}