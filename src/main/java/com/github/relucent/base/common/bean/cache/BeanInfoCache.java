package com.github.relucent.base.common.bean.cache;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import com.github.relucent.base.common.collection.WeakConcurrentMap;

/**
 * {@code BeanInfo}缓存工厂类<br>
 */
public class BeanInfoCache {

    // ==============================Constants========================================
    /** Bean属性缓存实例 */
    public static final BeanInfoCache INSTANCE = new BeanInfoCache();

    // ==============================Fields===========================================
    private final WeakConcurrentMap<Class<?>, BeanInfo> cache = new WeakConcurrentMap<>();

    // ==============================Constructors=====================================
    /** 单例模式 */
    private BeanInfoCache() {
    }

    // ==============================Methods==========================================
    /**
     * 获得Bean信息{@link BeanInfo}
     * @param beanClass Bean的类
     * @return Bean信息{@link BeanInfo}
     */
    public BeanInfo getBeanInfo(Class<?> beanClass) {
        BeanInfo info = cache.get(beanClass);
        if (info != null) {
            return info;
        }
        try {
            info = Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            // ignore
        }
        if (info != null) {
            cache.putIfAbsent(beanClass, info);
            info = cache.get(beanClass);
        }
        return info;
    }

    /**
     * 清空全局的Bean属性缓存
     */
    public void clear() {
        this.cache.clear();
    }
}
