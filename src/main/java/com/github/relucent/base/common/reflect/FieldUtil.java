package com.github.relucent.base.common.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.github.relucent.base.common.collection.WeakConcurrentMap;
import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.ClassUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 字段{@code java.lang.reflect.Field}相关反射工具类<br>
 */
public class FieldUtil {
    // =================================Fields================================================
    /**
     * 字段缓存
     */
    private static final WeakConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new WeakConcurrentMap<>();

    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected FieldUtil() {
    }

    // =================================Methods================================================
    /**
     * 获取字段名，读取注解的值作为名称
     * @param field 字段
     * @return 字段名
     */
    public static String getFieldName(Field field) {
        return field == null ? null : field.getName();
    }

    /**
     * 查找指定类中是否包含指定名称对应的字段，包括所有字段（包括非public字段），也包括父类和Object类的字段
     * @param beanClass 被查找字段的类,不能为null
     * @param name 字段名
     * @return 是否包含字段
     */
    public static boolean hasField(Class<?> beanClass, String name) {
        return getField(beanClass, name) != null;
    }

    /**
     * 是否为父类引用字段<br>
     * 当字段所在类是对象子类时（对象中定义的非static的class），会自动生成一个以"this$0"为名称的字段，指向父类对象
     * @param field 字段
     * @return 是否为父类引用字段
     */
    public static boolean isOuterClassField(Field field) {
        return "this$0".equals(field.getName());
    }

    /**
     * 使用反射获取,获取给定类的所有字段列表（无缓存）<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     * @param clzss 查找的 {@link Class}
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     */
    private static Field[] getFieldsDirectly(final Class<?> clzss, boolean withSuperClassFields) {
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = clzss;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = withSuperClassFields ? currentClass.getSuperclass() : null;
        }
        return allFields.toArray(ArrayConstant.EMPTY_FIELD_ARRAY);
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段（使用缓存）<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     * @param beanClass 类
     * @return 字段列表
     */
    public static Field[] getFields(Class<?> beanClass) {
        AssertUtil.notNull(beanClass);
        return FIELDS_CACHE.computeIfAbsent(beanClass, () -> getFieldsDirectly(beanClass, true));
    }

    /**
     * 查找指定类中的指定name的字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回{@code null}
     * @param beanClass 被查找字段的类,不能为null
     * @param name 字段名
     * @return 字段
     */
    public static Field getField(Class<?> beanClass, String name) {
        final Field[] fields = getFields(beanClass);
        for (Field field : fields) {
            if (name.equals(getFieldName(field))) {
                return field;
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有满足条件的字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     * @param beanClass 类
     * @param fieldFilter field过滤器，过滤掉不需要的field
     * @return 字段列表
     */
    public static Field[] getFields(Class<?> beanClass, Predicate<Field> fieldFilter) {
        return ArrayUtil.filter(getFields(beanClass), fieldFilter);
    }

    /**
     * 获取指定类中字段名和字段对应的有序Map（包括其父类中的字段）<br>
     * 如果子类与父类中存在同名字段，那么返回的是子类字段。
     * @param beanClass 类
     * @return 字段名和字段对应的Map
     */
    public static Map<String, Field> getFieldMap(Class<?> beanClass) {
        final Field[] fields = getFields(beanClass);
        final Map<String, Field> map = new LinkedHashMap<>(fields.length);
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    // =================================ReadMethods============================================
    /**
     * 获取字段值
     * @param obj 对象，如果是static字段，则传{@code null}
     * @param field 字段
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, Field field) {
        if (field == null) {
            return null;
        }
        if (obj instanceof Class) {
            obj = null;
        }
        MemberUtil.setAccessible(field);
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.propagate("IllegalAccess for " + field.getDeclaringClass() + "." + field.getName(), e);
        }
    }

    /**
     * 获取静态字段值
     * @param field 字段
     * @return 字段值
     */
    public static Object getStaticFieldValue(Field field) {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     * @param obj 对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || StringUtil.isBlank(fieldName)) {
            return null;
        }
        Class<?> beanClass = obj instanceof Class ? (Class<?>) obj : obj.getClass();
        Field field = getField(beanClass, fieldName);
        return getFieldValue(obj, field);
    }

    // =================================WriteMethods===========================================
    /**
     * 设置字段值<br>
     * 若字段类型是原始类型而传入的值是 null，则会将字段设置为对应原始类型的默认值（见 {@link ClassUtil#getDefaultValue(Class)}）<br>
     * @param obj 对象，如果是static字段，此参数为null
     * @param field 字段
     * @param value 值，当值类型与字段类型不匹配时，会尝试转换
     */
    public static void setFieldValue(Object obj, Field field, Object value) {
        AssertUtil.notNull(field, "Field in [" + obj + "] not exist !");
        final Class<?> fieldType = field.getType();
        if (value != null) {
            if (!fieldType.isAssignableFrom(value.getClass())) {
                // 类型不同的字段，尝试转换，转换失败则使用原对象类型
                final Object targetValue = ConvertUtil.convert(value, fieldType);
                if (targetValue != null) {
                    value = targetValue;
                }
            }
        } else {
            // 获取null对应默认值，防止原始类型造成空指针问题
            value = ClassUtil.getDefaultValue(fieldType);
        }
        MemberUtil.setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.propagate("IllegalAccess for " + obj + "." + field.getName(), e);
        }
    }

    /**
     * 设置字段值<br>
     * @param obj 对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value 值，当值类型与字段类型不匹配时，会尝试转换
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) {
        AssertUtil.notNull(obj);
        AssertUtil.notBlank(fieldName);
        final Field field = getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
        AssertUtil.notNull(field, "Field [" + fieldName + "] is not exist in [" + obj.getClass().getName() + "]");
        setFieldValue(obj, field, value);
    }
}
