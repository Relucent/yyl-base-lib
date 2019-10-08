package com.github.relucent.base.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.relucent.base.util.bean.mapping.BeanMapDescriber;
import com.github.relucent.base.util.bean.mapping.BeanMapPopulater;
import com.github.relucent.base.util.bean.mapping.BeanMapper;

/**
 * BEAN操作工具类，扩展Apache Commons BeanUtils，提供一些反射方面缺失的封装。
 * @author _yyl
 */
public class BeanUtil {

    private static final Logger LOG = LoggerFactory.getLogger(BeanUtil.class);

    public static Map<String, Object> describe(Object bean) {
        return describe(bean, MapConfig._DEFAULT);
    }

    public static Map<String, Object> describe(Object bean, MapConfig config) {
        return new BeanMapDescriber(config).describe(bean);
    }

    public static void populate(Object bean, Map<String, Object> map) {
        populate(bean, map, MapConfig._DEFAULT);
    }

    public static void populate(Object bean, Map<String, Object> map, MapConfig config) {
        new BeanMapPopulater(config).populate(bean, bean.getClass(), map);
    }

    public static <T> T newBean(Class<T> beanClass, Map<String, Object> properties) {
        return newBean(beanClass, properties, MapConfig._DEFAULT);
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

    /**
     * 暴力设置当前类声明的 private/protected 属性
     * @param object 对象
     * @param propertyName 属性名
     * @param newValue 新值
     * @throws IllegalAccessException 访问异常
     * @throws NoSuchFieldException 没有对应字段
     */
    public static void setDeclaredProperty(Object object, String propertyName, Object newValue) throws IllegalAccessException, NoSuchFieldException {
        assertNotNull(object);
        assertHasText(propertyName);
        Field field = object.getClass().getDeclaredField(propertyName);
        setDeclaredProperty(object, field, newValue);
    }

    /**
     * 暴力获取当前类声明的private/protected属性
     * @param object 对象
     * @param propertyName 属性名
     * @return 获取到的属性
     * @throws IllegalAccessException 访问异常
     * @throws NoSuchFieldException 没有对应字段
     */
    public static Object getDeclaredProperty(Object object, String propertyName) throws IllegalAccessException, NoSuchFieldException {
        assertNotNull(object);
        assertHasText(propertyName);
        Field field = object.getClass().getDeclaredField(propertyName);
        return getDeclaredProperty(object, field);
    }

    /**
     * 暴力设置当前类声明的private/protected属性
     * @param object 对象
     * @param field 属性名
     * @param newValue 新值
     * @throws IllegalAccessException 访问异常
     */
    public static void setDeclaredProperty(Object object, Field field, Object newValue) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(object, newValue);
        field.setAccessible(accessible);
    }

    /**
     * 暴力获取当前类声明的private/protected属性
     * @param object 对象
     * @param field 属性
     * @return 获取到的属性
     * @throws IllegalAccessException 访问异常
     */
    public static Object getDeclaredProperty(Object object, Field field) throws IllegalAccessException {
        assertNotNull(object);
        assertNotNull(field);
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        Object result = field.get(object);
        field.setAccessible(accessible);
        return result;
    }

    /**
     * 获得field的getter名称
     * @param type 类型
     * @param fieldName 属性名
     * @return 取值方法名
     */
    public static String getAccessorName(Class<?> type, String fieldName) {
        assertHasText(fieldName);
        assertNotNull(type);
        if (type.getName().equals("boolean")) {
            return "is" + capitalizeString(fieldName);
        } else {
            return "get" + capitalizeString(fieldName);
        }
    }

    /**
     * 获得field的getter名称
     * @param type 类型
     * @param fieldName 属性名
     * @return 取值方法名
     */
    public static Method getAccessor(Class<?> type, String fieldName) {
        try {
            return type.getMethod(getAccessorName(type, fieldName));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 调用当前类声明的private/protected方法
     * @param object 对象
     * @param methodName 方法名
     * @param param 方法参数
     * @return 方法调用结果
     * @throws IllegalAccessException 访问异常
     * @throws NoSuchMethodException 没有对应方法
     * @throws InvocationTargetException 调用目标异常
     */
    public static Object invokePrivateMethod(Object object, String methodName, Object param)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return invokePrivateMethod(object, methodName, new Object[] {param});
    }

    /**
     * 循环向上转型,获取对象的DeclaredField.
     * @param object 对象实例
     * @param propertyName 属性名
     * @return 返回对应的Field
     * @throws NoSuchFieldException 如果没有该Field时抛出
     */
    public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException {
        assertNotNull(object);
        assertHasText(propertyName);
        return getDeclaredField(object.getClass(), propertyName);
    }

    /**
     * 循环向上转型,获取对象的DeclaredField.
     * @param clazz 类型
     * @param propertyName 属性名
     * @return 返回对应的Field
     * @throws NoSuchFieldException 如果没有该Field时抛出.
     */
    public static Field getDeclaredField(Class<?> clazz, String propertyName) throws NoSuchFieldException {
        assertNotNull(clazz);
        assertHasText(propertyName);
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException ex) {
                // Field不在当前类定义,继续向上转型
            }
        }
        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }

    /**
     * 暴力获取对象变量值,忽略private,protected修饰符的限制.
     * @param object 对象实例
     * @param propertyName 属性名
     * @return 强制获得属性值
     * @throws NoSuchFieldException 如果没有该Field时抛出.
     */
    public static Object forceGetProperty(final Object object, final String propertyName) throws NoSuchFieldException {
        assertNotNull(object);
        assertHasText(propertyName);
        final Field field = getDeclaredField(object, propertyName);
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            /** * run. */
            public Object run() {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Object result = null;
                try {
                    result = field.get(object);
                } catch (IllegalAccessException e) {
                    LOG.error("!", e);
                }
                field.setAccessible(accessible);
                return result;
            }
        });
    }

    /**
     * 暴力设置对象变量值,忽略private,protected修饰符的限制.
     * @param object 对象实例
     * @param propertyName 属性名
     * @param newValue 赋予的属性值
     * @throws NoSuchFieldException 如果没有该Field时抛出.
     */
    public static void forceSetProperty(final Object object, final String propertyName, final Object newValue) throws NoSuchFieldException {
        assertNotNull(object);
        assertHasText(propertyName);
        final Field field = getDeclaredField(object, propertyName);
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            /** * run. */
            public Object run() {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    field.set(object, newValue);
                } catch (IllegalAccessException e) {
                    LOG.error("!", e);
                }
                field.setAccessible(accessible);
                return null;
            }
        });
    }

    /**
     * 暴力调用对象函数,忽略private,protected修饰符的限制.
     * @param object 对象实例
     * @param methodName 方法名
     * @param params 方法参数
     * @return Object 方法调用返回的结果对象
     * @throws NoSuchMethodException 如果没有该Method时抛出.
     */
    public static Object invokePrivateMethod(final Object object, final String methodName, final Object... params) throws NoSuchMethodException {
        assertNotNull(object);
        assertHasText(methodName);
        Class<?>[] types = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            types[i] = params[i].getClass();
        }
        Class<?> clazz = object.getClass();
        Method method = null;
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                method = superClass.getDeclaredMethod(methodName, types);
                break;
            } catch (NoSuchMethodException ex) {
                // 方法不在当前类定义,继续向上转型
            }
        }
        if (method == null) {
            throw new NoSuchMethodException("No Such Method:" + clazz.getSimpleName() + methodName);
        }
        final Method m = method;
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            /** * run. */
            public Object run() {
                boolean accessible = m.isAccessible();
                m.setAccessible(true);
                Object result = null;
                try {
                    result = m.invoke(object, params);
                } catch (Exception e) {
                    LOG.error("!", e);
                }
                m.setAccessible(accessible);
                return result;
            }
        });
    }

    /**
     * 按Field的类型取得Field列表.
     * @param object 对象实例
     * @param type 类型
     * @return 属性对象列表
     */
    public static List<Field> getFieldsByType(Object object, Class<?> type) {
        List<Field> list = new ArrayList<Field>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType().isAssignableFrom(type)) {
                list.add(field);
            }
        }

        return list;
    }

    /**
     * 按FieldName获得Field的类型.
     * @param type 类型
     * @param name 属性名
     * @return 属性的类型
     * @throws NoSuchFieldException 指定属性不存在时，抛出异常
     */
    public static Class<?> getPropertyType(Class<?> type, String name) throws NoSuchFieldException {
        return getDeclaredField(type, name).getType();
    }

    /**
     * 获得field的getter函数名称.
     * @param type 类型
     * @param fieldName 属性名
     * @return getter方法名
     * @throws NoSuchFieldException field不存在时抛出异常
     */
    public static String getGetterName(Class<?> type, String fieldName) throws NoSuchFieldException {
        assertNotNull(type);
        assertHasText(fieldName);
        Class<?> fieldType = getDeclaredField(type, fieldName).getType();

        if ((fieldType == boolean.class) || (fieldType == Boolean.class)) {
            return "is" + capitalizeString(fieldName);
        } else {
            return "get" + capitalizeString(fieldName);
        }
    }

    /**
     * 获得field的getter函数,如果找不到该方法,返回null.
     * @param type 类型
     * @param fieldName 属性名
     * @return getter方法对象
     */
    public static Method getGetterMethod(Class<?> type, String fieldName) {
        try {
            return type.getMethod(getGetterName(type, fieldName));
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            LOG.error("!", e);
        }
        return null;
    }

    private static String capitalizeString(String s) {
        if (s == null || s.length() == 0) {
            return s;
        } else {
            char ac[] = s.toCharArray();
            ac[0] = Character.toUpperCase(ac[0]);
            return new String(ac);
        }
    }

    private static void assertNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("[Assertion failed] - this argument is required; it must not be null");
        }
    }

    private static void assertHasText(CharSequence text) {
        if (text != null && text.length() > 0) {
            for (int i = 0, I = text.length(); i < I; i++) {
                if (!Character.isWhitespace(text.charAt(i))) {
                    return;
                }
            }
        }
        throw new IllegalArgumentException("[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }
}
