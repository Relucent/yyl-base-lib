package com.github.relucent.base.common.bean.mapping;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.github.relucent.base.common.bean.MapConfig;
import com.github.relucent.base.common.convert.ConvertUtil;

public class BeanMapPopulater {

    private MapConfig config;

    public BeanMapPopulater(MapConfig config) {
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public void populate(Object bean, Class<?> clazz, Map<String, Object> properties) {
        try {
            Set<String> exfields = config.findExcludeFields(clazz);
            Set<String> infields = config.findIncludeFields(clazz);
            Collection<String> defaultExcludes = MapConfig.DEFAULT_EXCLUDES;
            boolean isAdmitMode = infields.size() > 0;
            if (clazz == null) {
                throw new IllegalArgumentException("No bean class specified");
            }

            PropertyDescriptor[] descriptors = null;
            try {
                descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            } catch (IntrospectionException e) {
                descriptors = new PropertyDescriptor[0];
            }

            for (PropertyDescriptor descriptor : descriptors) {
                String field = descriptor.getName();
                // 排除的字段
                if (defaultExcludes.contains(field)) {
                    continue;
                }
                // 如果存在允许字段排除没有允许的字段
                if (isAdmitMode && !infields.contains(field)) {
                    continue;
                }

                if (exfields.contains(field)) {
                    continue;
                }
                if (descriptor.getWriteMethod() != null) {
                    Class<?> toType = descriptor.getPropertyType();
                    Method method = descriptor.getReadMethod();
                    Object property = properties.get(field);
                    // 基本的对象类型
                    if (ConvertUtil.isStandardType(toType)) {
                        property = ConvertUtil.convert(property, toType, null);
                        if (property == null) {
                            property = config.getFieldDefaultValue(clazz, field);
                        }
                        if (property == null) {
                            property = config.getFieldDefaultValue(field);
                        }
                        if (property == null) {
                            property = config.getTypeDefaultValue(toType);
                        }
                        method.invoke(bean, property);
                    }
                    // 非基本类型
                    else {
                        if (property instanceof Map) {
                            try {
                                property = newBean(toType, (Map<String, Object>) property);
                                method.invoke(bean, property);
                            } catch (Exception e) {
                                // Ignore.
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T> T newBean(Class<T> beanClass, Map<String, Object> properties) {
        T bean = null;
        try {
            if (beanClass.isInterface()) {
                bean = InterfaceProxyFactory.create(beanClass);
            } else {
                bean = beanClass.newInstance();
            }
            populate(bean, beanClass, properties);
            return bean;
        } catch (Exception e) {
            // Ignore
        }
        return bean;

    }
}
