package com.github.relucent.base.common.reflect;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 类型引用类，用于获取完整的泛型类型信息的泛型抽象类
 * @param <T> 引用类型的泛型
 */
public abstract class TypeReference<T> extends TypeCapture<T> {

    /** 运行时类型 */
    private final Type runtimeType;

    /**
     * 构造函数 <br>
     * 
     * <pre>
     * TypeReference&lt;List&lt;String&gt;&gt; t = new TypeReference&lt;List&lt;String&gt;&gt;() {
     * };
     * </pre>
     */
    protected TypeReference() {
        this.runtimeType = capture();
    }

    /**
     * 私有构造，定制指定类型的引用类
     * @param type 指定类型
     */
    private TypeReference(Type type) {
        this.runtimeType = type;
    }

    /**
     * 返回包装{@code type}的类型引用类的实例
     * @param <T> 引用类型的泛型
     * @param type 指定的类型
     * @return 的类型引用类的实例
     */
    public static <T> TypeReference<T> of(Class<T> type) {
        return new SimpleTypeReference<T>(type);
    }

    /**
     * 返回引用的类型
     * @return 引用的类型
     */
    public final Type getType() {
        return runtimeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(runtimeType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TypeReference<?> other = (TypeReference<?>) obj;
        return Objects.equals(runtimeType, other.runtimeType);
    }

    @Override
    public String toString() {
        return "TypeReference [runtimeType=" + runtimeType + "]";
    }

    /** 简单的类型引用实现类 */
    private static final class SimpleTypeReference<T> extends TypeReference<T> {
        SimpleTypeReference(Type type) {
            super(type);
        }
    }
}
