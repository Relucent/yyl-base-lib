package com.github.relucent.base.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 类型引用类，用于获取完整的泛型类型信息的泛型抽象类
 * @param <T> 引用类型的泛型
 * @author YYL
 */
public abstract class TypeReference<T> {

    /** 类型 */
    private final Type type;
    /** 原始类 */
    private final Class<? super T> rawType;

    /**
     * 返回包装{@code type}的类型引用类的实例
     * @param <T> 引用类型的泛型
     * @param type 指定的类型
     * @return 的类型引用类的实例
     */
    public static TypeReference<?> of(Type type) {
        return new SimpleTypeReference<Object>(type);
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
     * 构造函数 <br>
     * 
     * <pre>
     * TypeReference&lt;List&lt;String&gt;&gt; t = new TypeReference&lt;List&lt;String&gt;&gt;() {
     * };
     * </pre>
     */
    @SuppressWarnings("unchecked")
    protected TypeReference() {
        this.type = getSuperclassTypeParameter(getClass());
        this.rawType = (Class<? super T>) TypeUtil.getClass(type);
    }

    /**
     * 私有构造，定制指定类型的引用类
     * @param type 指定类型
     */
    @SuppressWarnings("unchecked")
    private TypeReference(Type type) {
        this.type = TypeUtil.canonicalize(Preconditions.checkNotNull(type));
        this.rawType = (Class<? super T>) TypeUtil.getClass(type);
    }

    /**
     * 返回引用的类型
     * @return 引用的类型
     */
    public final Type getType() {
        return type;
    }

    /**
     * 返回此类型的原始（非泛型）类型
     * @return 此类型的原始（非泛型）类型
     */
    public final Class<? super T> getRawType() {
        return rawType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
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
        return Objects.equals(type, other.type);
    }

    @Override
    public String toString() {
        return "TypeReference(" + (type instanceof Class ? ((Class<?>) type).getName() : type.toString()) + ")";
    }

    /**
     * 获得超类泛型参数
     * @return 泛型参数
     */
    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return TypeUtil.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    /** 简单的类型引用实现类 */
    private static final class SimpleTypeReference<T> extends TypeReference<T> {
        SimpleTypeReference(Type type) {
            super(type);
        }
    }
}
