package com.github.relucent.base.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.lang.Assert;
import com.github.relucent.base.common.lang.ClassUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 字段{@code java.lang.reflect.Field}相关反射工具类<br>
 */
public class FieldUtil {
    // =================================Fields================================================

    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected FieldUtil() {
    }

    // =================================Methods================================================
    /**
     * 获取给定类及其父类（如果有）的所有字段。<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。<br>
     * @param clazz 查找的 {@link Class}
     * @return 字段数组
     * @throws IllegalArgumentException 如果类为{@code null}
     */
    public static Field[] getAllFields(final Class<?> clazz) {
        Assert.notNull(clazz, "The class must not be null");
        final List<Field> allFields = getAllFieldList(clazz);
        return allFields.toArray(ArrayConstant.EMPTY_FIELD_ARRAY);
    }

    /**
     * 按名称获取一个可访问的{@link Field}，将考虑超类/接口。
     * @param clazz 反射查找的类{@link Class}，不能是{@code null}
     * @param fieldName 字段名称
     * @param forceAccess 如果为{@code true}会匹配所有字段（{@code public}、{@code protected}、 default(package)和 {@code private}方法）；{@code false}只会匹配{@code public}字段。 如果为{@code fasle}则只匹配{@code public}字段
     * @return 字段
     * @throws IllegalArgumentException 传入 class 参数为{@code null},或者方法名为空
     */
    public static Field getField(final Class<?> clazz, final String fieldName, final boolean forceAccess) {
        Assert.notNull(clazz, "The class must not be null");
        Assert.isTrue(StringUtil.isBlank(fieldName), "The field name must not be blank/empty");

        for (Class<?> klass = clazz; klass != null; klass = klass.getSuperclass()) {
            try {
                final Field field = klass.getDeclaredField(fieldName);
                // 会检查非公共作用域，并返回准确的结果
                if (!Modifier.isPublic(field.getModifiers())) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        continue;
                    }
                }
                return field;
            } catch (final NoSuchFieldException ex) { // NOPMD
                // ignore
            }
        }
        // 检查公共接口字段
        Field match = null;
        for (final Class<?> klass : ClassUtil.getAllInterfaces(clazz)) {
            try {
                final Field test = klass.getField(fieldName);
                Assert.isTrue(match == null,
                        String.format(
                                "Reference to field %s is ambiguous relative to %s; a matching field exists on two or more implemented interfaces.", //
                                fieldName, clazz));
                match = test;
            } catch (final NoSuchFieldException ex) {
                // Ignore
            }
        }
        return match;
    }

    /**
     * 获取类中使用指定注解的所有字段。<br>
     * @param clazz 反射查找的类{@link Class}，不能是{@code null}
     * @param annotationType 要匹配的字段上必须存在的{@link Annotation}
     * @return 字段数组
     * @throws IllegalArgumentException 如果类或注释为{@code null}
     */
    public static Field[] getFieldsWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "The annotation class must not be null");
        final Field[] allFields = getAllFields(clazz);
        final List<Field> annotatedFields = new ArrayList<>();
        for (final Field field : allFields) {
            if (field.getAnnotation(annotationType) != null) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields.toArray(ArrayConstant.EMPTY_FIELD_ARRAY);
    }

    // read write
    // ----------------------------------------------------------------------
    /**
     * 从对象字段读取值
     * @param target 对象
     * @param fieldName 字段名称
     * @param forceAccess 如果为{@code true}会匹配所有字段（{@code public}、{@code protected}、 default(package)和 {@code private}方法）；{@code false}只会匹配{@code public}字段。 如果为{@code fasle}则只匹配{@code public}字段
     * @return 字段的值
     * @throws RuntimeException 没有找到匹配的字段，或者该字段不能访问
     */
    public static Object readField(final Object target, final String fieldName, final boolean forceAccess) {
        Assert.notNull(target, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, String.format("Cannot locate field %s on %s", fieldName, cls));
        return readField(field, target);
    }

    /**
     * 从对象字段读取值
     * @param field 字段
     * @param target 要调用的对象，如果是{@code null}则表示是{@code static}字段
     * @return 字段值
     * @throws RuntimeException 传入字段为{@code null}，或者无法访问该字段
     */
    public static Object readField(final Field field, final Object target) {
        Assert.notNull(field, "The field must not be null");
        MemberUtil.setAccessible(field);
        try {
            return field.get(target);
        } catch (Exception e) {
            throw ExceptionHelper.propagate(e);
        }
    }

    /**
     * 从类的静态字段读取值
     * @param clazz 类
     * @param fieldName 字段名
     * @return 字段值
     * @throws RuntimeException 没有找到匹配的字段，或者该字段不能访问
     */
    public static Object readStaticField(final Class<?> clazz, final String fieldName) throws IllegalAccessException {
        final Field field = getField(clazz, fieldName, true);
        Assert.notNull(field, String.format("Cannot locate field '%s' on %s", fieldName, clazz));
        return readStaticField(field);
    }

    /**
     * 从静态字段读取值
     * @param field 字段
     * @return 字段值
     * @throws RuntimeException 传入字段错误，没有找到匹配的字段，或者该字段不能访问
     */
    public static Object readStaticField(final Field field) throws IllegalAccessException {
        Assert.notNull(field, "The field must not be null");
        MemberUtil.setAccessible(field);
        return readField(field, (Object) null);
    }

    /**
     * 将值写入对象的字段
     * @param target 对象
     * @param fieldName 字段名称
     * @param value 字段值
     * @param forceAccess 如果为{@code true}会匹配所有字段（{@code public}、{@code protected}、 default(package)和 {@code private}方法）；{@code false}只会匹配{@code public}字段。 如果为{@code fasle}则只匹配{@code public}字段
     * @throws RuntimeException 如果无法访问该字段
     */
    public static void writeField(final Object target, final String fieldName, final Object value, final boolean forceAccess) {
        Assert.notNull(target, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, String.format("Cannot locate declared field %s.%s", cls.getName(), fieldName));
        writeField(field, target, value);
    }

    /**
     * 将值写入对象的字段
     * @param field 需要写入值的字段 to write
     * @param target 需要写入值的对象，{@code null}表示是一个{@code static} 字段
     * @param value 字段值
     * @throws RuntimeException 如果无法访问该字段
     */
    public static void writeField(final Field field, final Object target, final Object value) {
        Assert.notNull(field, "The field must not be null");
        MemberUtil.setAccessible(field);
        try {
            field.set(target, value);
        } catch (Exception e) {
            throw ExceptionHelper.propagate(e);
        }
    }

    /**
     * 将值写入静态字段
     * @param clazz 类
     * @param fieldName 字段名称
     * @param forceAccess 如果为{@code true}会匹配所有字段（{@code public}、{@code protected}、 default(package)和 {@code private}方法）；{@code false}只会匹配{@code public}字段。 如果为{@code fasle}则只匹配{@code public}字段
     * @throws RuntimeException 如果无法访问该字段
     */
    public static void writeStaticField(final Class<?> clazz, final String fieldName, final Object value, final boolean forceAccess) {
        final Field field = getField(clazz, fieldName, forceAccess);
        Assert.isTrue(field != null, String.format("Cannot locate declared field %s.%s", clazz.getName(), fieldName));
        writeStaticField(field, value);
    }

    /**
     * 将值写入静态字段
     * @param field 需要写入值的字段 to write
     * @param value 字段值
     * @throws RuntimeException 如果无法访问该字段
     */
    public static void writeStaticField(final Field field, final Object value) {
        writeField(field, (Object) null, value);
    }

    // =================================ToolMethods============================================

    /**
     * 获取给定类及其父类（如果有）的所有字段。
     * @param clzss 查找的 {@link Class}
     * @return 字段列表
     * @throws IllegalArgumentException 如果类为{@code null}
     */
    private static List<Field> getAllFieldList(final Class<?> clzss) {
        Assert.notNull(clzss, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = clzss;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }
}
