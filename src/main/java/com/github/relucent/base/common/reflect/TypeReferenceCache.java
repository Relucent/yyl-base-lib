package com.github.relucent.base.common.reflect;

import java.lang.reflect.Type;

import com.github.relucent.base.common.collection.WeakConcurrentMap;

/**
 * 类型引用类缓存<br>
 */
public class TypeReferenceCache {

    // =================================Instances==============================================
    /** 类型引用类缓存实例 */
    public static final TypeReferenceCache INSTANCE = new TypeReferenceCache();

    // =================================Fields=================================================
    /** 构建器缓存 */
    private final WeakConcurrentMap<Type, TypeReference<?>> cache = new WeakConcurrentMap<>();

    // =================================Constructors===========================================
    /** 单例模式 */
    private TypeReferenceCache() {
    }

    // =================================Methods================================================
    /**
     * 根据类型，获得一个类型引用
     * @param <T> 类型的泛型
     * @param type 类型
     * @return 类型引用
     */
    @SuppressWarnings("unchecked")
    public <T> TypeReference<T> get(Type type) {
        return (TypeReference<T>) cache.computeIfAbsent(type, key -> TypeReference.of(key));
    }
}
