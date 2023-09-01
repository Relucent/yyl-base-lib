package com.github.relucent.base.common.reflect;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import com.github.relucent.base.common.constant.ArrayConstant;

/**
 * {@link WildcardType} 接口实现<br>
 * WildcardType 是 Type 的子接口，用于描述形如{@code ? extends class A} 或{@code ? super class B} 的泛型参数表达式<br>
 */
class WildcardTypeImpl implements WildcardType, Serializable {

    // =================================Constants==============================================
    private static final long serialVersionUID = 0;

    // =================================Fields=================================================

    private final Type upperBound;
    private final Type lowerBound;

    // =================================Constructors===========================================
    public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
        Preconditions.checkArgument(lowerBounds.length <= 1);
        Preconditions.checkArgument(upperBounds.length == 1);

        if (lowerBounds.length == 1) {
            Preconditions.checkNotNull(lowerBounds[0]);
            Preconditions.checkNotPrimitive(lowerBounds[0]);
            Preconditions.checkArgument(upperBounds[0] == Object.class);
            this.lowerBound = TypeUtil.canonicalize(lowerBounds[0]);
            this.upperBound = Object.class;
        } else {
            Preconditions.checkNotNull(upperBounds[0]);
            Preconditions.checkNotPrimitive(upperBounds[0]);
            this.lowerBound = null;
            this.upperBound = TypeUtil.canonicalize(upperBounds[0]);
        }
    }

    // =================================Methods================================================
    public Type[] getUpperBounds() {
        return new Type[] { upperBound };
    }

    public Type[] getLowerBounds() {
        return lowerBound != null ? new Type[] { lowerBound } : ArrayConstant.EMPTY_TYPE_ARRAY;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof WildcardType && TypeUtil.equals(this, (WildcardType) other);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getLowerBounds()) ^ Arrays.hashCode(getUpperBounds());
    }

    @Override
    public String toString() {
        if (lowerBound != null) {
            return "? super " + TypeUtil.typeToString(lowerBound);
        } else if (upperBound == Object.class) {
            return "?";
        } else {
            return "? extends " + TypeUtil.typeToString(upperBound);
        }
    }
}