package com.github.relucent.base.common.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.relucent.base.common.bean.cache.BeanDescCache;
import com.github.relucent.base.common.bean.cache.BeanInfoCache;
import com.github.relucent.base.common.bean.introspector.BeanDesc;
import com.github.relucent.base.common.bean.mapping.BeanMapDescriber;
import com.github.relucent.base.common.bean.mapping.BeanMapPopulater;
import com.github.relucent.base.common.bean.mapping.BeanMapper;
import com.github.relucent.base.common.lang.ClassUtil;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.reflect.ModifierUtil;

/**
 * JavaBean 工具类：用于实例化bean，检查bean属性类型、复制bean属性等。<br>
 * @author YYL
 */
public class BeanUtil {

    // ==============================Fields===========================================
    private static final Logger LOGGER = Logger.getLogger(BeanUtil.class);

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected BeanUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取{@link BeanDesc} Bean描述信息
     * @param beanClass Bean的类
     * @return Bean对象的描述信息
     */
    public static BeanDesc getBeanDesc(Class<?> beanClass) {
        return BeanDescCache.INSTANCE.getBeanDesc(beanClass);
    }

    // -------------------------------------------------------------------------------
    /**
     * 获取{@link BeanInfo} Bean 信息
     * @param beanClass Bean的类
     * @return {@link BeanInfo}
     */
    public static BeanInfo getBeanInfo(Class<?> beanClass) {
        return BeanInfoCache.INSTANCE.getBeanInfo(beanClass);
    }

    /**
     * 获取{@link BeanInfo} Bean 信息（直接获取）
     * @param beanClass Bean的类
     * @return {@link BeanInfo}
     */
    public static BeanInfo getBeanInfoDirectly(Class<?> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------------
    /**
     * 判断是否有Setter方法<br>
     * 判定方法是否存在只有一个参数的setXXX方法
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean hasSetter(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为Bean对象<br>
     * 判定方法是否存在只有无参数的getXXX方法或者isXXX方法
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean hasGetter(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 0) {
                    final String name = method.getName();
                    if (name.startsWith("get") || name.startsWith("is")) {
                        if (!"getClass".equals(name) && !"getDeclaringClass".equals(name) && !"getMetaClass".equals(name)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 指定类中是否有public类型字段(static字段除外)
     * @param clazz 待测试类
     * @return 是否有public类型字段
     */
    public static boolean hasPublicField(final Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (final Field field : clazz.getFields()) {
                // 非static的public字段
                if (ModifierUtil.isPublic(field) && !ModifierUtil.isStatic(field)) {
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------
    /**
     * 判断是否为可读的Bean对象
     * @param clazz 待测试类
     * @return 是否为可读的Bean对象
     * @see #hasGetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isReadableBean(final Class<?> clazz) {
        return clazz != null && (hasGetter(clazz) || hasPublicField(clazz));
    }

    /**
     * 判断是否为可写的Bean对象
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @see #hasSetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isWritableBean(final Class<?> clazz) {
        return clazz != null && (hasSetter(clazz) || hasPublicField(clazz));
    }

    // -------------------------------------------------------------------------------
    public static Map<String, Object> describe(Object bean) {
        return describe(bean, MapConfig.DEFAULT);
    }

    public static Map<String, Object> describe(Object bean, MapConfig config) {
        return new BeanMapDescriber(config).describe(bean);
    }

    public static void populate(Object bean, Map<String, Object> map) {
        populate(bean, map, MapConfig.DEFAULT);
    }

    public static void populate(Object bean, Map<String, Object> map, MapConfig config) {
        new BeanMapPopulater(config).populate(bean, bean.getClass(), map);
    }

    public static <T> T newBean(Class<T> beanClass, Map<String, Object> properties) {
        return newBean(beanClass, properties, MapConfig.DEFAULT);
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
        if (Boolean.TYPE.equals(type)) {
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
        return invokePrivateMethod(object, methodName, new Object[] { param });
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
                    LOGGER.error("!", e);
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
                    LOGGER.error("!", e);
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
                    LOGGER.error("!", e);
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
            LOGGER.error("!", e);
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
