package com.github.relucent.base.common.bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JavaObject JSON 映射配置(Bean转Map解析器的配置项)
 * @version 0.02-20091211
 * @author YYL
 */
public class MapConfig {

    // ========================================Fields=========================================
    /** 默认的解析器配置 */
    public static final MapConfig DEFAULT = new MapConfig();
    /** 将Object转换Map时需要转换的字段 */
    private Set<String> includeFields = new HashSet<String>();// Properties
    /** 将Object转换Map时需要排除的字段 */
    private Set<String> excludeFields = new HashSet<String>();// Properties
    /** 将某个类型Object转换Map时需要转换的字段 */
    private Map<Class<?>, Set<String>> specificExcludeFieldMap = new HashMap<Class<?>, Set<String>>();// Properties
    /** 将某个类型Object转换Map时需要排除的字段 */
    private Map<Class<?>, Set<String>> specificIncludeFieldMap = new HashMap<Class<?>, Set<String>>();// Properties
    /** 默认排除的字段(全局) */
    public static final Set<String> DEFAULT_EXCLUDES;
    /** 设置过转换方式的对象类型(specificExcludeFieldMap或者specificIncludeFieldMap包括的类型) */
    private Set<Class<?>> configTypes = new HashSet<Class<?>>();
    /** 转换最大层级数(防止无限层解析下去) */
    public int resolveBeanDepth = 0xF;
    /** 转换时是否将NULL的字段添加到MAP */
    public boolean ignoreNull = true;

    /** MAP转Object时，某个字段的默认值(值NULL的时候将采用默认值) */
    private Map<String, Object> fieldDefaultValueMap = new HashMap<String, Object>();
    /** MAP转Object时，某个字段的默认值(针对某个类型Object的某个字段) */
    private Map<Class<?>, Map<String, Object>> specificFieldDefaultValueMap = new HashMap<Class<?>, Map<String, Object>>();
    /** MAP转Object时，某个类型字段的默认值(值NULL的时候将采用默认值) */
    private Map<Class<?>, Object> typeDefaultEmptyValueMap = new HashMap<Class<?>, Object>();

    // cache
    static {
        Set<String> set = new HashSet<String>();
        set.add("class");
        set.add("declaringClass");
        set.add("metaClass");
        set.add("hibernateLazyInitializer");
        set.add("toJsonProxyExternalRepresentation");
        DEFAULT_EXCLUDES = Collections.<String>unmodifiableSet(set);
    }

    // ========================================Methods========================================
    /**
     * 转换时需要序列化的字段
     * @param includes 设置需要包含的字段
     */
    public void setIncludeFields(String... includes) {
        for (String include : includes) {
            includeFields.add(include);
        }
    }

    /**
     * 转换时需要排除的字段
     * @param excludes 设置需要排除的字段
     */
    public void setExcludeFields(String... excludes) {
        for (String exclude : excludes) {
            excludeFields.add(exclude);
        }
    }

    /**
     * 转换时需要排除的字段
     * @param clazz 指定的类
     * @param excludes 设置需要排除的字段
     */
    public void setExcludeFields(Class<?> clazz, String... excludes) {
        configTypes.add(clazz);
        Set<String> specificExcludeFields = specificExcludeFieldMap.get(clazz);
        if (specificExcludeFields == null) {
            specificExcludeFields = new HashSet<String>();
            specificExcludeFieldMap.put(clazz, specificExcludeFields);
        }
        for (String exclude : excludes) {
            specificExcludeFields.add(exclude);
        }
    }

    /**
     * 转换时需要序列化的字段(如果不指定，默认序列化是全部字段)
     * @param clazz 指定的类
     * @param includes 设置需要包含的字段
     */
    public void setIncludeFields(Class<?> clazz, String... includes) {
        configTypes.add(clazz);
        Set<String> specificIncludeFields = specificIncludeFieldMap.get(clazz);
        if (specificIncludeFields == null) {
            specificIncludeFields = new HashSet<String>();
            specificIncludeFieldMap.put(clazz, specificIncludeFields);
        }
        for (String include : includes) {
            specificIncludeFields.add(include);
        }
    }

    public Set<String> findExcludeFields(Class<?> clazz) {
        return findExcludeFields(clazz, true);
    }

    private Set<String> findExcludeFields(Class<?> clazz, boolean flag) {
        Set<String> exfields = new HashSet<String>();
        if (flag && excludeFields != null) {
            exfields.addAll(excludeFields);
        }
        Set<String> sefs = specificExcludeFieldMap.get(clazz);
        if (sefs != null) {
            exfields.addAll(sefs);
        }
        return exfields;
    }

    public Set<String> findIncludeFields(Class<?> clazz) {
        return findIncludeFields(clazz, true);
    }

    private Set<String> findIncludeFields(Class<?> clazz, boolean flag) {
        Set<String> infields = new HashSet<String>();
        if (flag && includeFields != null) {
            infields.addAll(includeFields);
        }
        Set<String> sifs = specificIncludeFieldMap.get(clazz);
        if (sifs != null) {
            infields.addAll(sifs);
        }
        return infields;
    }

    public Set<Class<?>> getConfigTypes() {
        return configTypes;
    }

    public void setFieldDefaultValue(String filed, Object value) {
        fieldDefaultValueMap.put(filed, value);
    }

    public Object getFieldDefaultValue(String filed) {
        return fieldDefaultValueMap.get(filed);
    }

    public void setFieldDefaultValue(Class<?> clazz, String filed, Object value) {
        Map<String, Object> fieldDefaultValue = specificFieldDefaultValueMap.get(clazz);
        if (fieldDefaultValue == null) {
            fieldDefaultValue = new HashMap<String, Object>();
            specificFieldDefaultValueMap.put(clazz, fieldDefaultValue);
        }
        fieldDefaultValue.put(filed, value);
    }

    public Object getFieldDefaultValue(Class<?> clazz, String filed) {
        Map<String, Object> fieldDefaultValue = specificFieldDefaultValueMap.get(clazz);
        if (fieldDefaultValue != null) {
            return fieldDefaultValue.get(filed);
        }
        return null;
    }

    //
    public <T> void setTypeDefaultValue(Class<T> type, T value) {
        typeDefaultEmptyValueMap.put(type, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypeDefaultValue(Class<T> type) {
        return (T) typeDefaultEmptyValueMap.get(type);
    }

    @SuppressWarnings({ "unused" })
    private String findClassName(Class<?> clazz) {
        String className = clazz.getName();
        int index = className.indexOf("$$EnhancerByCGLIB$$");
        if (index != -1) {
            className = className.substring(0, index);
        }
        return className;
    }
}
