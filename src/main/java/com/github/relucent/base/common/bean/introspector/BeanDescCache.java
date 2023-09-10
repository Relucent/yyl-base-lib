package com.github.relucent.base.common.bean.introspector;

import java.util.function.Supplier;

import com.github.relucent.base.common.collection.WeakConcurrentMap;

/**
 * Bean属性缓存工厂类<br>
 * 缓存用于防止多次反射造成的性能问题<br>
 */
public class BeanDescCache {

    /** Bean属性缓存实例 */
    public static final BeanDescCache INSTANCE = new BeanDescCache();

    private final WeakConcurrentMap<Class<?>, BeanDesc> cache = new WeakConcurrentMap<>();

    /** 单例模式 */
    private BeanDescCache() {
    }

    /**
     * 获得属性名和{@link BeanDesc}Map映射
     * @param beanClass Bean的类
     * @param supplier 对象不存在时创建对象的函数
     * @return 属性名和{@link BeanDesc}映射
     */
    public BeanDesc getBeanDesc(Class<?> beanClass, Supplier<BeanDesc> supplier) {
        return cache.computeIfAbsent(beanClass, (key) -> supplier.get());
    }

    /**
     * 获得Bean信息描述
     * @param beanClass Bean的类
     * @return 属性名和{@link BeanDesc}映射
     */
    public BeanDesc getBeanDesc(Class<?> beanClass) {
        return cache.computeIfAbsent(beanClass, (key) -> new BeanDesc(beanClass));
    }

    /**
     * 清空全局的Bean属性缓存
     */
    public void clear() {
        this.cache.clear();
    }
}
