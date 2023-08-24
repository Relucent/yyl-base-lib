package com.github.relucent.base.common.bean.info;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.github.relucent.base.common.reflect.AnnotationUtil;
import com.github.relucent.base.common.reflect.FieldUtil;
import com.github.relucent.base.common.reflect.MethodUtil;
import com.github.relucent.base.common.reflect.ModifierUtil;
import com.github.relucent.base.common.reflect.TypeUtil;

/**
 * 属性描述，包括了字段、getter、setter和相应的方法执行
 */
public class PropDesc {

    // ==============================Fields===========================================
    /**
     * 字段
     */
    protected final Field field;
    /**
     * Getter方法
     */
    protected final Method getter;
    /**
     * Setter方法
     */
    protected final Method setter;

    // ==============================Constructors=====================================
    /**
     * 构造<br>
     * Getter和Setter方法设置为默认可访问
     * @param field 字段
     * @param getter get方法
     * @param setter set方法
     */
    public PropDesc(Field field, Method getter, Method setter) {
        this.field = field;
        this.getter = MethodUtil.setAccessible(getter);
        this.setter = MethodUtil.setAccessible(setter);
    }

    // ==============================Methods==========================================
    /**
     * 获取字段名，如果存在Alias注解，读取注解的值作为名称
     * @return 字段名
     */
    public String getFieldName() {
        return FieldUtil.getFieldName(field);
    }

    /**
     * 获取字段名称
     * @return 字段名
     */
    public String getRawFieldName() {
        return field == null ? null : field.getName();
    }

    /**
     * 获取字段
     * @return 字段
     */
    public Field getField() {
        return field;
    }

    /**
     * 获得字段类型<br>
     * 先获取字段的类型，如果字段不存在，则获取Getter方法的返回类型，否则获取Setter的第一个参数类型
     * @return 字段类型
     */
    public Type getFieldType() {
        if (field != null) {
            return TypeUtil.getType(field);
        }
        return findPropType(getter, setter);
    }

    /**
     * 获得字段类型<br>
     * 先获取字段的类型，如果字段不存在，则获取Getter方法的返回类型，否则获取Setter的第一个参数类型
     * @return 字段类型
     */
    public Class<?> getFieldClass() {
        if (field != null) {
            return TypeUtil.getClass(field);
        }
        return findPropClass(getter, setter);
    }

    /**
     * 获取Getter方法，可能为{@code null}
     * @return Getter方法
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * 获取Setter方法，可能为{@code null}
     * @return {@link Method}Setter 方法对象
     */
    public Method getSetter() {
        return setter;
    }

    /**
     * 检查属性是否可读（即是否可以通过{@link #getValue(Object)}获取到值）
     * @param checkTransient 是否检查Transient关键字或注解
     * @return 是否可读
     */
    public boolean isReadable(boolean checkTransient) {
        // 检查是否有getter方法或是否为public修饰
        if (getter == null && !Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        // 检查transient关键字和@Transient注解
        if (checkTransient && isTransientForGet()) {
            return false;
        }

        return true;
    }

    /**
     * 获取属性值<br>
     * 首先调用字段对应的Getter方法获取值，如果Getter方法不存在，则判断字段如果为public，则直接获取字段值<br>
     * 此方法不检查任何注解，使用前需调用 {@link #isReadable(boolean)} 检查是否可读
     * @param bean Bean对象
     * @return 字段值
     */
    public Object getValue(Object bean) {
        if (getter != null) {
            return MethodUtil.invoke(bean, getter);
        } else if (ModifierUtil.isPublic(field)) {
            return FieldUtil.getFieldValue(bean, field);
        }

        return null;
    }

    /**
     * 检查属性是否可读（即是否可以通过{@link #getValue(Object)}获取到值）
     * @param checkTransient 是否检查Transient关键字或注解
     * @return 是否可读
     */
    public boolean isWritable(boolean checkTransient) {
        // 检查是否有getter方法或是否为public修饰
        if (setter == null && !ModifierUtil.isPublic(field)) {
            return false;
        }

        // 检查transient关键字和@Transient注解
        if (checkTransient && isTransientForSet()) {
            return false;
        }

        return true;
    }

    /**
     * 设置Bean的字段值<br>
     * 首先调用字段对应的Setter方法，如果Setter方法不存在，则判断字段如果为public，则直接赋值字段值<br>
     * 此方法不检查任何注解，使用前需调用 {@link #isWritable(boolean)} 检查是否可写
     * @param bean Bean对象
     * @param value 值，必须与字段值类型匹配
     */
    public void setValue(Object bean, Object value) {
        if (setter != null) {
            MethodUtil.invoke(bean, setter, value);
        } else if (ModifierUtil.isPublic(field)) {
            FieldUtil.setFieldValue(bean, field, value);
        }
    }

    // ==============================PrivateMethods===================================
    /**
     * 通过Getter和Setter方法中找到属性类型
     * @param getter Getter方法
     * @param setter Setter方法
     * @return {@link Type}
     */
    private Type findPropType(Method getter, Method setter) {
        Type type = null;
        if (getter != null) {
            type = TypeUtil.getReturnType(getter);
        }
        if (type == null && setter != null) {
            type = TypeUtil.getParamType(setter, 0);
        }
        return type;
    }

    /**
     * 通过Getter和Setter方法中找到属性类型
     * @param getter Getter方法
     * @param setter Setter方法
     * @return {@link Type}
     */
    private Class<?> findPropClass(Method getter, Method setter) {
        Class<?> type = null;
        if (getter != null) {
            type = TypeUtil.getReturnClass(getter);
        }
        if (type == null && setter != null) {
            type = TypeUtil.getFirstParamClass(setter);
        }
        return type;
    }

    /**
     * 字段和Getter方法是否为Transient关键字修饰的
     * @return 是否为Transient关键字修饰的
     */
    private boolean isTransientForGet() {

        // 检查字段
        if (ModifierUtil.isTransient(field)) {
            return true;
        }
        // 检查Getter方法
        if (getter != null) {
            return AnnotationUtil.hasAnnotation(getter, Transient.class);
        }
        return false;
    }

    /**
     * 字段和Getter方法是否为Transient关键字修饰的
     * @return 是否为Transient关键字修饰的
     */
    private boolean isTransientForSet() {
        // 检查字段
        if (ModifierUtil.isTransient(field)) {
            return true;
        }
        // 检查Getter方法
        if (setter != null) {
            return AnnotationUtil.hasAnnotation(setter, Transient.class);
        }
        return false;
    }
}
