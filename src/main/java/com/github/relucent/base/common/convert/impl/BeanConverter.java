package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Type;
import java.util.Map;

import com.github.relucent.base.common.bean.BeanUtil;
import com.github.relucent.base.common.bean.copier.BeanCopier;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeUtil;
import com.github.relucent.base.common.reflect.internal.ObjectConstructorCache;

/**
 * Bean转换器
 */
public class BeanConverter implements Converter<Object> {

    public static BeanConverter INSTANCE = new BeanConverter();

    @Override
    public Object convert(Object source, Type toType) {
        if (source == null) {
            return null;
        }
        final Class<?> beanType = TypeUtil.getClass(toType);
        if (source instanceof Map || BeanUtil.isWritableBean(source.getClass())) {
            Object target = newBean(beanType);
            new BeanCopier(source, target, toType).copy();
            return target;
        }
        return null;
    }

    /**
     * 创建 Bean 对象
     * @param beanType 对象类型
     * @return 对象的实例
     */
    private static Object newBean(final Class<?> beanType) {
        // 直接实例化
        try {
            return beanType.newInstance();
        } catch (Exception ignore) {
            // ignore
        }

        // 使用对象构造器强制构建
        try {
            return ObjectConstructorCache.INSTANCE.get(TypeReference.of(beanType)).construct();
        } catch (Exception ignore) {
            return null;
        }
    }
}
