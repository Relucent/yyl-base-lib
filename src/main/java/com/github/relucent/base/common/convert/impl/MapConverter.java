package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.relucent.base.common.bean.copier.BeanCopier;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeUtil;
import com.github.relucent.base.common.reflect.internal.ObjectConstructorCache;

/**
 * {@link Map} 转换器
 */
public class MapConverter implements Converter<Map<?, ?>> {

    public static MapConverter INSTANCE = new MapConverter();

    @Override
    public Map<?, ?> convert(Object source, Type toType) {
        final Class<?> mapType = TypeUtil.getClass(toType);
        Map<?, ?> target = newMap(mapType);
        if (source != null && target != null) {
            new BeanCopier(source, target).copy();
        }
        return target;
    }

    /**
     * 创建 {@code Map}对象
     * @param mapType {@code Map}对象类型
     * @return {@code Map}对象的实例
     */
    private static Map<?, ?> newMap(final Class<?> mapType) {

        // 目标类型是抽象类，创建 LinkedHashMap
        if (mapType.isAssignableFrom(AbstractMap.class) || mapType.isInterface()) {
            return new LinkedHashMap<>();
        }

        // 直接实例化
        try {
            return (Map<?, ?>) mapType.newInstance();
        } catch (Exception ignore) {
            // ignore
        }

        // 使用对象构造器强制构建
        try {
            return (Map<?, ?>) ObjectConstructorCache.INSTANCE.get(TypeReference.of(mapType)).construct();
        } catch (Exception ignore) {
            return null;
        }
    }
}
