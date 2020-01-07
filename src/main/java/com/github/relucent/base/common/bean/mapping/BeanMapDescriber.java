package com.github.relucent.base.common.bean.mapping;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.relucent.base.common.bean.MapConfig;
import com.github.relucent.base.common.convert.ConvertUtil;

public class BeanMapDescriber {

    private MapConfig config;

    public BeanMapDescriber(MapConfig config) {
        this.config = config;
    }

    public Map<String, Object> describe(Object bean) {
        return describeBean(bean, 0);
    }

    @SuppressWarnings("rawtypes")
    private Object describeEntry(Object value, int depth) {
        if (value == null) {
            return null;
        } else if (ConvertUtil.isStandardType(value.getClass())) {
            return value;
        } else if (value instanceof Map) {
            return describeMap((Map) value, increDepth(depth));
        } else if (value instanceof Object[]) {
            return describeArray((Object[]) value, increDepth(depth));
        } else if (value instanceof Iterable) {
            return describeIterable((Iterable) value, increDepth(depth));
        }
        if (resolveBean(depth)) {
            return describeBean(value, depth);
        } else {
            return new HashMap<String, Object>(1);
        }
    }

    private Map<String, Object> describeBean(Object bean, int depth) {
        try {
            Class<?> clazz = bean.getClass();
            Set<String> exfields = config.findExcludeFields(clazz);
            Set<String> infields = config.findIncludeFields(clazz);
            Collection<String> defaultExcludes = MapConfig.DEFAULT_EXCLUDES;

            Map<String, Object> proxy = new HashMap<String, Object>();

            boolean isAdmitMode = infields.size() > 0;

            if (clazz == null) {
                throw new IllegalArgumentException("No bean class specified");
            }

            proxy.put("$class", clazz.getName());

            PropertyDescriptor[] descriptors = null;
            try {
                descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
            } catch (IntrospectionException e) {
                descriptors = new PropertyDescriptor[0];
            }
            // System.out.println(clazz.getName());
            for (int i = 0, j = descriptors.length; i < j; i++) {
                PropertyDescriptor descriptor = descriptors[i];
                String key = descriptor.getName();
                // 排除的字段
                if (defaultExcludes.contains(key)) {
                    continue;
                }

                // 如果存在允许字段优先考虑
                if (isAdmitMode && !infields.contains(key)) {
                    continue;
                }

                if (exfields.contains(key)) {
                    continue;
                }

                if (descriptor.getReadMethod() != null) {
                    Class<?> type = descriptor.getPropertyType();
                    // 是否深度解析Bean(如果否，则排除非基本的对象类型)
                    if (!resolveBean(depth) && !ConvertUtil.isStandardType(type)) {
                        continue;
                    }

                    // 可以替换为PropertyUtils.getProperty(bean, key)
                    Method method = descriptor.getReadMethod();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 0) {
                        continue;
                    }
                    Object value = method.invoke(bean, BeanMapper.EMPTY_OBJECT_ARRAY);
                    proxy.put(key, describeEntry(value, increDepth(depth)));
                }
            }
            return proxy;
        } catch (Exception ex) {
            // IllegalArgumentException IllegalAccessException
            ex.printStackTrace();
            return Collections.<String, Object>emptyMap();
        }
    }

    private Object[] describeArray(Object[] array, int depth) {
        if (resolveBean(depth)) {
            Object[] describe = new Object[array.length];
            for (int i = 0, j = array.length; i < j; i++) {
                describe[i] = describeEntry(array[i], increDepth(depth));
            }
            return describe;
        }
        return BeanMapper.EMPTY_OBJECT_ARRAY;
    }

    private Object[] describeIterable(java.lang.Iterable<?> iterable, int depth) {
        if (resolveBean(depth)) {
            ArrayList<Object> list = new ArrayList<Object>();
            for (java.util.Iterator<?> it = iterable.iterator(); it.hasNext();) {
                list.add(describeEntry(it.next(), increDepth(depth)));
            }
            return list.toArray();
        }
        return BeanMapper.EMPTY_OBJECT_ARRAY;
    }

    private Map<?, ?> describeMap(Map<?, ?> map, int depth) {
        Map<Object, Object> proxy = new HashMap<Object, Object>();
        if (resolveBean(depth)) {
            for (Iterator<?> names = map.keySet().iterator(); names.hasNext();) {
                Object name = names.next();
                if (name instanceof String) {
                    proxy.put(name, describeEntry(map.get(name), increDepth(depth)));
                }
            }
        }
        return proxy;
    }

    private int increDepth(final int depth) {
        return depth + 1;
    }

    private boolean resolveBean(int depth) {
        return config.resolveBeanDepth > depth;
    }
    // Standard Wrapped
}
