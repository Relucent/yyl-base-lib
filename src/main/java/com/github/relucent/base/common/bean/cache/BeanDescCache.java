package com.github.relucent.base.common.bean.cache;

import java.util.function.Supplier;

import com.github.relucent.base.common.bean.introspector.BeanDesc;
import com.github.relucent.base.common.collection.WeakConcurrentMap;

/**
 * Bean属性缓存工厂类<br>
 * 缓存用于防止多次反射造成的性能问题<br>
 */
public class BeanDescCache {

    // ==============================Constants========================================
    /** Bean属性缓存实例 */
    public static final BeanDescCache INSTANCE = new BeanDescCache();

    // ==============================Fields===========================================
    private final WeakConcurrentMap<Class<?>, BeanDesc> cache = new WeakConcurrentMap<>();

    // ==============================Constructors=====================================
    /** 单例模式 */
    private BeanDescCache() {
    }

    // ==============================Methods==========================================
    /**
     * 获得Bean信息描述{@link BeanDesc}
     * @param beanClass Bean的类
     * @param supplier 对象不存在时创建对象的函数
     * @return Bean信息描述{@link BeanDesc}
     */
    public BeanDesc getBeanDesc(Class<?> beanClass, Supplier<BeanDesc> supplier) {
        return cache.computeIfAbsent(beanClass, (key) -> supplier.get());
    }

    /**
     * 获得Bean信息描述{@link BeanDesc}
     * @param beanClass Bean的类
     * @return Bean信息描述{@link BeanDesc}
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
