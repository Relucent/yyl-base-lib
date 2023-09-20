package com.github.relucent.base.common.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.github.relucent.base.common.bean.cache.BeanDescCache;
import com.github.relucent.base.common.bean.cache.BeanInfoCache;
import com.github.relucent.base.common.bean.introspector.BeanDesc;
import com.github.relucent.base.common.bean.mapping.BeanMapDescriber;
import com.github.relucent.base.common.bean.mapping.BeanMapPopulater;
import com.github.relucent.base.common.bean.mapping.BeanMapper;
import com.github.relucent.base.common.lang.ClassUtil;
import com.github.relucent.base.common.reflect.ModifierUtil;

/**
 * JavaBean 工具类：用于实例化bean，检查bean属性类型、复制bean属性等。<br>
 * @author YYL
 */
public class BeanUtil {

    // ==============================Fields===========================================
    // ...

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected BeanUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取{@link BeanDesc} Bean描述信息
     * @param beanClass Bean的类
     * @return Bean对象的描述信息
     */
    public static BeanDesc getBeanDesc(Class<?> beanClass) {
        return BeanDescCache.INSTANCE.getBeanDesc(beanClass);
    }

    // -------------------------------------------------------------------------------
    /**
     * 获取{@link BeanInfo} Bean 信息
     * @param beanClass Bean的类
     * @return {@link BeanInfo}
     */
    public static BeanInfo getBeanInfo(Class<?> beanClass) {
        return BeanInfoCache.INSTANCE.getBeanInfo(beanClass);
    }

    /**
     * 获取{@link BeanInfo} Bean 信息（直接获取）
     * @param beanClass Bean的类
     * @return {@link BeanInfo}
     */
    public static BeanInfo getBeanInfoDirectly(Class<?> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------------
    /**
     * 判断是否有Setter方法<br>
     * 判定方法是否存在只有一个参数的setXXX方法
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean hasSetter(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为Bean对象<br>
     * 判定方法是否存在只有无参数的getXXX方法或者isXXX方法
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean hasGetter(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 0) {
                    final String name = method.getName();
                    if (name.startsWith("get") || name.startsWith("is")) {
                        if (!"getClass".equals(name) && !"getDeclaringClass".equals(name) && !"getMetaClass".equals(name)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 指定类中是否有public类型字段(static字段除外)
     * @param clazz 待测试类
     * @return 是否有public类型字段
     */
    public static boolean hasPublicField(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Field field : clazz.getFields()) {
                // 非static的public字段
                if (ModifierUtil.isPublic(field) && !ModifierUtil.isStatic(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------
    /**
     * 判断是否为可读的Bean对象
     * @param clazz 待测试类
     * @return 是否为可读的Bean对象
     * @see #hasGetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isReadableBean(final Class<?> clazz) {
        return clazz != null && (hasGetter(clazz) || hasPublicField(clazz));
    }

    /**
     * 判断是否为可写的Bean对象
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @see #hasSetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isWritableBean(final Class<?> clazz) {
        return clazz != null && (hasSetter(clazz) || hasPublicField(clazz));
    }

    // -------------------------------------------------------------------------------
    public static Map<String, Object> describe(Object bean) {
        return describe(bean, MapConfig.DEFAULT);
    }

    public static Map<String, Object> describe(Object bean, MapConfig config) {
        return new BeanMapDescriber(config).describe(bean);
    }

    public static void populate(Object bean, Map<String, Object> map) {
        populate(bean, map, MapConfig.DEFAULT);
    }

    public static void populate(Object bean, Map<String, Object> map, MapConfig config) {
        new BeanMapPopulater(config).populate(bean, bean.getClass(), map);
    }

    public static <T> T newBean(Class<T> beanClass, Map<String, Object> properties) {
        return newBean(beanClass, properties, MapConfig.DEFAULT);
    }

    public static <T> T newBean(Class<T> beanClass, Map<String, Object> properties, MapConfig config) {
        return new BeanMapPopulater(config).newBean(beanClass, properties);
    }

    /**
     * 将源对象的内容合并到目标对象中 只对其中8个基本类型和String, Date字段进行处理
     * @param src 来源对象
     * @param dest 接待对象
     */
    public static void simpleMerge(Object dest, Object src) {
        BeanMapper.copy(src, dest);
    }
}
