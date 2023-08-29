package com.github.relucent.base.common.bean.info;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.reflect.FieldUtil;
import com.github.relucent.base.common.reflect.MethodUtil;

/**
 * Bean信息描述，为BeanInfo替代方案<br>
 * 查找Getter和Setter方法时会：
 *
 * <pre>
 * 1. 忽略字段和方法名的大小写
 * 2. Getter查找getXXX、isXXX、getIsXXX
 * 3. Setter查找setXXX、setIsXXX
 * 4. Setter忽略参数值与字段值不匹配的情况，因此有多个参数类型的重载时，会调用首次匹配的
 * </pre>
 */
@SuppressWarnings("serial")
public class BeanDesc implements Serializable {

    // ==============================Fields===========================================
    /**
     * Bean类
     */
    private final Class<?> beanClass;
    /**
     * 属性表
     */
    private final Map<String, BeanPropDesc> propMap = new LinkedHashMap<>();

    // ==============================Constructors=====================================
    /**
     * 构造
     * @param beanClass Bean类
     */
    public BeanDesc(Class<?> beanClass) {
        AssertUtil.notNull(beanClass);

        this.beanClass = beanClass;

        // 获取 Getter和Setter方法
        final Method[] gettersAndSetters = ArrayUtil.filter(MethodUtil.getAllMethods(beanClass), MethodUtil::isGetterOrSetterIgnoreCase);
        // 获取全部字段，进行比较过滤
        for (Field field : FieldUtil.getFields(this.beanClass)) {
            // 排除静态属性和对象子类
            if (!Modifier.isStatic(field.getModifiers()) && !FieldUtil.isOuterClassField(field)) {
                BeanPropDesc pd = createProp(field, gettersAndSetters);
                // 只有不存在时才放入，防止父类属性覆盖子类属性
                this.propMap.putIfAbsent(pd.getFieldName(), pd);
            }
        }
    }

    // ==============================Methods==========================================
    /**
     * 获取Bean的全类名
     * @return Bean的类名
     */
    public String getName() {
        return this.beanClass.getName();
    }

    /**
     * 获取Bean的简单类名
     * @return Bean的类名
     */
    public String getSimpleName() {
        return this.beanClass.getSimpleName();
    }

    /**
     * 获取字段属性表
     * @return 字段属性表
     */
    public Map<String, BeanPropDesc> getPropMap() {
        return this.propMap;
    }

    /**
     * 获取字段属性列表
     * @return {@link BeanPropDesc} 列表
     */
    public Collection<BeanPropDesc> getProps() {
        return this.propMap.values();
    }

    /**
     * 获取属性，如果不存在返回null
     * @param fieldName 字段名
     * @return {@link BeanPropDesc}
     */
    public BeanPropDesc getProp(String fieldName) {
        return this.propMap.get(fieldName);
    }

    /**
     * 获得字段名对应的字段对象，如果不存在返回null
     * @param fieldName 字段名
     * @return 字段值
     */
    public Field getField(String fieldName) {
        final BeanPropDesc desc = this.propMap.get(fieldName);
        return desc == null ? null : desc.getField();
    }

    /**
     * 获取Getter方法，如果不存在返回null
     * @param fieldName 字段名
     * @return Getter方法
     */
    public Method getGetter(String fieldName) {
        final BeanPropDesc desc = this.propMap.get(fieldName);
        return desc == null ? null : desc.getGetter();
    }

    /**
     * 获取Setter方法，如果不存在返回null
     * @param fieldName 字段名
     * @return Setter方法
     */
    public Method getSetter(String fieldName) {
        final BeanPropDesc desc = this.propMap.get(fieldName);
        return desc == null ? null : desc.getSetter();
    }

    // ==============================PrivateMethods===================================
    /**
     * 根据字段创建属性描述<br>
     * 查找Getter和Setter方法时会：
     *
     * <pre>
     * 1. 忽略字段和方法名的大小写
     * 2. Getter查找getXXX、isXXX、getIsXXX
     * 3. Setter查找setXXX、setIsXXX
     * 4. Setter忽略参数值与字段值不匹配的情况，因此有多个参数类型的重载时，会调用首次匹配的
     * </pre>
     *
     * @param field 字段
     * @param methods 类中所有的方法
     * @return {@link BeanPropDesc}
     */
    private BeanPropDesc createProp(final Field field, final Method[] methods) {
        final Method getter = getGetterMethod(field, methods);
        final Method setter = getSetterMethod(field, methods);
        return new BeanPropDesc(field, getter, setter);
    }

    /**
     * 查找字段对应的Getter方法
     * @param field 字段
     * @param methods 方法列表
     * @return 字段对应的Getter方法
     */
    private Method getGetterMethod(Field field, Method[] methods) {
        final String fieldName = field.getName();
        final Class<?> fieldType = field.getType();
        final boolean isBooleanField = fieldType == Boolean.class || fieldType == boolean.class;
        // 遍历匹配一次
        for (Method method : methods) {
            String methodName = method.getName();
            // 无参数，可能为Getter方法
            if (method.getParameterCount() == 0) {
                // 方法名与字段名匹配，则为Getter方法
                if (isMatchGetter(methodName, fieldName, isBooleanField, false)) {
                    return method;
                }
            }
        }
        // 忽略大小写重新匹配一次
        for (Method method : methods) {
            String methodName = method.getName();
            // 无参数，可能为Getter方法
            if (method.getParameterCount() == 0) {
                // 方法名与字段名匹配（忽略大小写），则为Getter方法
                if (isMatchGetter(methodName, fieldName, isBooleanField, true)) {
                    return method;
                }
            }
        }
        // 没有对应的方法
        return null;
    }

    /**
     * 查找字段对应的Setter方法
     * @param field 字段
     * @param methods 方法列表
     * @return 字段对应的Setter方法
     */
    private Method getSetterMethod(Field field, Method[] methods) {
        final String fieldName = field.getName();
        final Class<?> fieldType = field.getType();
        final boolean isBooleanField = fieldType == Boolean.class || fieldType == boolean.class;
        // 遍历匹配一次
        for (Method method : methods) {
            String methodName = method.getName();
            // 名称匹配
            if (isMatchSetter(methodName, fieldName, isBooleanField, false)) {
                // 参数类型和字段类型一致，或参数类型是字段类型的子类
                if (fieldType.isAssignableFrom(method.getParameterTypes()[0])) {
                    return method;
                }
            }
        }
        // 忽略大小写重新匹配一次
        for (Method method : methods) {
            String methodName = method.getName();
            // 名称匹配（忽略大小写）
            if (isMatchSetter(methodName, fieldName, isBooleanField, false)) {
                // 参数类型和字段类型一致，或参数类型是字段类型的子类
                if (fieldType.isAssignableFrom(method.getParameterTypes()[0])) {
                    return method;
                }
            }
        }
        // 没有对应的方法
        return null;
    }

    /**
     * 方法是否为Getter方法<br>
     * 匹配规则如下（忽略大小写）：
     *
     * <pre>
     * 字段名    -> 方法名
     * isName  -> isName
     * isName  -> isIsName
     * isName  -> getIsName
     * name     -> isName
     * name     -> getName
     * </pre>
     *
     * @param methodName 方法名
     * @param fieldName 字段名
     * @param isBooleanField 是否为Boolean类型字段
     * @param ignoreCase 匹配是否忽略大小写
     * @return 是否匹配
     */
    private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
        final String handledFieldName;
        if (ignoreCase) {
            // 全部转为小写，忽略大小写比较
            methodName = methodName.toLowerCase();
            handledFieldName = fieldName.toLowerCase();
            fieldName = handledFieldName;
        } else {
            handledFieldName = StringUtil.upperFirst(fieldName);
        }

        // 针对Boolean类型特殊检查
        if (isBooleanField) {
            if (fieldName.startsWith("is")) {
                // 字段已经是is开头
                if (methodName.equals(fieldName) // isName -> isName
                        || ("get" + handledFieldName).equals(methodName)// isName -> getIsName
                        || ("is" + handledFieldName).equals(methodName)// isName -> isIsName
                ) {
                    return true;
                }
            } else if (("is" + handledFieldName).equals(methodName)) {
                // 字段非is开头， name -> isName
                return true;
            }
        }

        // 包括boolean的任何类型只有一种匹配情况：name -> getName
        return ("get" + handledFieldName).equals(methodName);
    }

    /**
     * 方法是否为Setter方法<br>
     * 匹配规则如下（忽略大小写）：
     *
     * <pre>
     * 字段名       ： 方法名
     * isName ： setName
     * isName ： setIsName
     * name   ： setName
     * </pre>
     *
     * @param methodName 方法名
     * @param fieldName 字段名
     * @param isBooleanField 是否为Boolean类型字段
     * @param ignoreCase 匹配是否忽略大小写
     * @return 是否匹配
     */
    private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
        final String handledFieldName;
        if (ignoreCase) {
            // 全部转为小写，忽略大小写比较
            methodName = methodName.toLowerCase();
            handledFieldName = fieldName.toLowerCase();
            fieldName = handledFieldName;
        } else {
            handledFieldName = StringUtil.upperFirst(fieldName);
        }

        // 非标准Setter方法跳过
        if (!methodName.startsWith("set")) {
            return false;
        }

        // 针对Boolean类型特殊检查
        if (isBooleanField && fieldName.startsWith("is")) {
            // 字段是is开头
            if (("set" + StringUtil.removePrefix(fieldName, "is")).equals(methodName)// isName -》 setName
                    || ("set" + handledFieldName).equals(methodName)// isName -> setIsName
            ) {
                return true;
            }
        }

        // 包括boolean的任何类型只有一种匹配情况：name -> setName
        return ("set" + handledFieldName).equals(methodName);
    }
}
