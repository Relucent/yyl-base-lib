package com.github.relucent.base.common.lang;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.constant.StringConstant;

/**
 * <h3>类工具类</h3><br>
 */
public class ClassUtil {

    // ==============================Fields===========================================
    /** 原始类型到包装类型的映射表 */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();
    static {
        PRIMITIVE_WRAPPER_MAP.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPER_MAP.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(Void.TYPE, Void.TYPE);
    }

    /** 包装类型到原始类型的映射表 */
    private static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new HashMap<>();
    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : PRIMITIVE_WRAPPER_MAP.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            if (!primitiveClass.equals(wrapperClass)) {
                WRAPPER_PRIMITIVE_MAP.put(wrapperClass, primitiveClass);
            }
        }
    }
    /** 包分隔符字符: {@code '&#x2e;' == {@value}} */
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ClassUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取对象类型
     * @param <T> 对象类型
     * @param object 对象
     * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(T object) {
        return null == object ? null : (Class<T>) object.getClass();
    }

    /**
     * 获取该类的封闭类<br>
     * 返回定义此类或匿名类所在的类，如果类本身是在包中定义的，返回{@code null}
     * @param clazz 类
     * @return 封闭类
     */
    public static Class<?> getEnclosingClass(Class<?> clazz) {
        return clazz == null ? null : clazz.getEnclosingClass();
    }

    // class name
    // ----------------------------------------------------------------------
    /**
     * 获取类的简单名称<br>
     * {@code cls.getSimpleName()} 的 {@code null}安全版本<br>
     * @param cls 要获取其简单名称的类
     * @return {@code cls}的简单名称，如果{@code cls}为{@code null}则返回空字符串
     * @see Class#getSimpleName()
     */
    public static String getSimpleName(final Class<?> cls) {
        return getSimpleName(cls, StringConstant.EMPTY);
    }

    /**
     * 获取类的简单名称<br>
     * {@code cls.getSimpleName()} 的 {@code null}安全版本<br>
     * @param cls 要获取其简单名称的类
     * @param valueIfNull 如果{@code cls}为{@code null}，返回的值
     * @return {@code cls}的简单名称，如果{@code cls}为{@code null}则返回{@code valueIfNull}
     * @see Class#getSimpleName()
     */
    public static String getSimpleName(final Class<?> cls, final String valueIfNull) {
        return cls == null ? valueIfNull : cls.getSimpleName();
    }

    /**
     * 获取对象的类简单名称<br>
     * {@code object.getClass().getSimpleName()} 的 {@code null}安全版本<br>
     * @param object 要获取其简单类名的对象
     * @return 获取对象的类简单名称，如果{@code object}为{@code null}则返回空字符串
     * @see Class#getSimpleName()
     */
    public static String getSimpleName(final Object object) {
        return getSimpleName(object, StringConstant.EMPTY);
    }

    /**
     * 获取对象的类简单名称<br>
     * {@code object.getClass().getSimpleName()} 的 {@code null}安全版本<br>
     * @param object 要获取其简单类名的对象
     * @param valueIfNull 如果{@code object}为{@code null}，返回的值
     * @return 获取对象的类简单名称，如果{@code object}为{@code null}则返回{@code valueIfNull}
     * @see Class#getSimpleName()
     */
    public static String getSimpleName(final Object object, final String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getSimpleName();
    }

    /**
     * 获取类的名称<br>
     * {@code cls.getName()} 的 {@code null}安全版本<br>
     * @param cls 要获取名称的类
     * @return {@code cls}的名称，如果{@code cls}为{@code null}则返回空字符串
     * @see Class#getName()
     */
    public static String getName(final Class<?> cls) {
        return getName(cls, StringConstant.EMPTY);
    }

    /**
     * 获取类的名称<br>
     * {@code cls.getName()} 的 {@code null}安全版本<br>
     * @param cls 要获取名称的类
     * @param valueIfNull 如果{@code cls}为{@code null}，返回的值
     * @return {@code cls}的简单名称，如果{@code cls}为{@code null}则返回{@code valueIfNull}
     * @see Class#getName()
     */
    public static String getName(final Class<?> cls, final String valueIfNull) {
        return cls == null ? valueIfNull : cls.getName();
    }

    /**
     * 获取对象的类名称<br>
     * {@code object.getClass().getName()} 的 {@code null}安全版本<br>
     * @param object 要获取其类名的对象
     * @return 对象的类名称，如果{@code object}为{@code null}则返回空字符串
     * @see Class#getName()
     */
    public static String getName(final Object object) {
        return getName(object, StringConstant.EMPTY);
    }

    /**
     * 获取对象的类名称<br>
     * {@code object.getClass().getName()} 的 {@code null}安全版本<br>
     * @param object 要获取其类名的对象
     * @param valueIfNull 如果{@code object}为{@code null}，返回的值
     * @return 对象的类名称，如果{@code object}为{@code null}则返回{@code valueIfNull}
     * @see Class#getName()
     */
    public static String getName(final Object object, final String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getName();
    }

    // Package name
    // ----------------------------------------------------------------------
    /**
     * 获取 {@code Object}的包名<br>
     * @param object 要获取其包名的对象
     * @param valueIfNull 如果为null，则返回的值
     * @return 对象的包名，如果对象为{@code null}则返回空字符串
     */
    public static String getPackageName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getPackageName(object.getClass());
    }

    /**
     * 获取 {@code Class}的包名<br>
     * @param cls 要获取其包名的类
     * @return 类的包名，如果类为{@code null}则返回空字符串
     */
    public static String getPackageName(final Class<?> cls) {
        if (cls == null) {
            return StringConstant.EMPTY;
        }
        return getPackageName(cls.getName());
    }

    /**
     * 从类名中获取包名， 传入的字符串被假定为一个类名<br>
     * @param className 要获取其包名的类名
     * @return 包名
     */
    public static String getPackageName(String className) {
        if (StringUtil.isEmpty(className)) {
            return StringConstant.EMPTY;
        }
        // Strip array encoding
        while (className.charAt(0) == '[') {
            className = className.substring(1);
        }
        // Strip Object type encoding
        if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
            className = className.substring(1);
        }
        final int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (i == -1) {
            return StringConstant.EMPTY;
        }
        return className.substring(0, i);
    }

    // Is assignable
    // ----------------------------------------------------------------------
    /**
     * 检查一个{@code Class}是否可以分配给另一个{@code Class}的变量。<br>
     * 与{@link Class#isAssignableFrom（java.lang.Class）}方法不同，该方法考虑了原始类型和{@code null}的扩展。<br>
     * 原始类型允许加宽转换，例如：将int指定给long、float或double，对于这些情况，此方法返回正确的结果。<br>
     * {@code Null}可以分配给任何引用类型。如果传入{@code null}，并且toClass 非原始类型，则此方法将返回{@code true}。<br>
     * 参考：<em><a href="http://docs.oracle.com/javase/specs/">The Java Language Specification</a></em>，第5.1.1、5.1.2和5.1.4节了解详细信息。
     * @param cls 要检查的类型
     * @param toClass 要尝试分配到的类型
     * @return 如果可以分配，返回{@code true}
     */
    public static boolean isAssignable(Class<?> cls, final Class<?> toClass) {
        return isAssignable(cls, toClass, true);
    }

    /**
     * 检查一个{@code Class}是否可以分配给另一个{@code Class}的变量。<br>
     * 与{@link Class#isAssignableFrom（java.lang.Class）}方法不同，该方法考虑了原始类型和{@code null}的扩展。<br>
     * 原始类型允许加宽转换，例如：将int指定给long、float或double，对于这些情况，此方法返回正确的结果。<br>
     * {@code Null}可以分配给任何引用类型。如果传入{@code null}，并且toClass 非原始类型，则此方法将返回{@code true}。<br>
     * 参考：<em><a href="http://docs.oracle.com/javase/specs/">The Java Language Specification</a></em>，第5.1.1、5.1.2和5.1.4节了解详细信息。
     * @param cls 要检查的类型
     * @param toClass 要尝试分配到的类型
     * @param autoboxing 是否在原始类型和包装类型之间使用隐式自动装箱
     * @return 如果可以分配，返回{@code true}
     */
    public static boolean isAssignable(Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        // 检查null，因为null不能分配给原始类型
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        // 自动包装:
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass) || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass) || Integer.TYPE.equals(toClass) || Long.TYPE.equals(toClass) || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should-never-get-here!
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    /**
     * 检查一个类数组是否可以分配给另一个类数组。<br>
     * 此方法为对参数数组中的每个元素调用 {@link #isAssignable(Class, Class) isAssignable}， 用来检查一组参数（第一个参数）是否与一组方法参数类型（第二个参数）适当兼容。<br>
     * 与{@link Class#isAssignableFrom（java.lang.Class）}方法不同，该方法考虑了原始类型和{@code null}的扩展。<br>
     * {@code Null}可以分配给任何引用类型。如果传入{@code null}，并且toClass 非原始类型，则此方法将返回{@code true}。<br>
     * 原始类型允许加宽转换，例如：将int指定给long、float或double，对于这些情况，此方法返回正确的结果。<br>
     * 参考：<em><a href="http://docs.oracle.com/javase/specs/">The Java Language Specification</a></em>，第5.1.1、5.1.2和5.1.4节了解详细信息。
     * @param classArray 要检查的类数组
     * @param toClassArray 要尝试分配到的类型数组
     * @return 如果可以分配，返回{@code true}
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray) {
        return isAssignable(classArray, toClassArray, true);
    }

    /**
     * 检查一个类数组是否可以分配给另一个类数组。<br>
     * 此方法为对参数数组中的每个元素调用 {@link #isAssignable(Class, Class) isAssignable}， 用来检查一组参数（第一个参数）是否与一组方法参数类型（第二个参数）适当兼容。<br>
     * 与{@link Class#isAssignableFrom（java.lang.Class）}方法不同，该方法考虑了原始类型和{@code null}的扩展。<br>
     * {@code Null}可以分配给任何引用类型。如果传入{@code null}，并且toClass 非原始类型，则此方法将返回{@code true}。<br>
     * 原始类型允许加宽转换，例如：将int指定给long、float或double，对于这些情况，此方法返回正确的结果。<br>
     * 参考：<em><a href="http://docs.oracle.com/javase/specs/">The Java Language Specification</a></em>，第5.1.1、5.1.2和5.1.4节了解详细信息。
     * @param classArray 要检查的类数组
     * @param toClassArray 要尝试分配到的类型数组
     * @param autoboxing 是否在原始类型和包装类型之间使用隐式自动装箱
     * @return 如果可以分配，返回{@code true}
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, final boolean autoboxing) {
        if (ArrayUtil.getLength(classArray) != ArrayUtil.getLength(toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = ArrayConstant.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = ArrayConstant.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
                return false;
            }
        }
        return true;
    }

    // ----------------------------------------------------------------------
    /**
     * 将指定的原始类转换为其对应的包装类
     * @param cls 要转换的类
     * @return 原始类对应的包装类
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = PRIMITIVE_WRAPPER_MAP.get(cls);
        }
        return convertedClass;
    }

    /**
     * 将指定的包装类转换为其对应的原始类
     * @param cls 要转换的类
     * @return 对应的包装类
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return WRAPPER_PRIMITIVE_MAP.get(cls);
    }

    // Inner class
    // ----------------------------------------------------------------------
    /**
     * 判断该类是否是一个内部类
     * @param cls 要检查的类
     * @return 如果是内部类则返回{@code true}
     */
    public static boolean isInnerClass(final Class<?> cls) {
        return cls != null && cls.getEnclosingClass() != null;
    }

    /**
     * 判断类是否是抽象类
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 判断类是否是枚举类型
     * @param type 要检查的类型
     * @return 是否为枚举类型
     */
    public static boolean isEnum(Class<?> type) {
        return null != type && type.isEnum();
    }

    /**
     * 判断类型是否是原始类型的封装类型 ({@link Boolean}, {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float})
     * @param type 要检查的类型
     * @return 如果参数是原始类型的封装类型的封裝类型则返回{@code true}，否则返回{@code flase}
     */
    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return WRAPPER_PRIMITIVE_MAP.containsKey(type);
    }

    /**
     * 判断类型是否是原始类型或者原始类型的封装类型 ({@link Boolean}, {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float})<br>
     * @param type 要检查的类型
     * @return 如果是原始类型或者原始类型的包装类型则返回{@code true}
     */
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    /**
     * 判断类是否是标准的类， 这个类必须<br>
     * 标准类包括：
     * 
     * <pre>
     * 1、非接口
     * 2、非抽象类
     * 3、非枚举
     * 4、非数组
     * 5、非注解
     * 6、非原始类型（boolean,int,long,double 等）
     * 7、非合成类（编译器自动生成的）
     * </pre>
     *
     * @param clazz 类
     * @return 是否为标准类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return clazz != null // 非空
                && !clazz.isInterface() // 非接口
                && !isAbstract(clazz) // 非抽象类
                && !clazz.isEnum() // 非枚举
                && !clazz.isArray() // 非数组
                && !clazz.isAnnotation() // 非注解
                && !clazz.isPrimitive()// 非原始类型
                && !clazz.isSynthetic();// 非合成类

    }

    /**
     * 获取指定类型分的默认值<br>
     * 默认值规则为：
     *
     * <pre>
     * 1、如果为布尔类型，返回 false
     * 1、如果为数值类型，返回 0
     * 2、非原始类型返回 {@code null}
     * </pre>
     *
     * @param clazz 类
     * @return 默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) {
                return false;
            }
            if (clazz == int.class) {
                return 0;
            }
            if (clazz == long.class) {
                return 0L;
            }
            if (clazz == double.class) {
                return 0D;
            }
            if (clazz == byte.class) {
                return (byte) 0;
            }
            if (clazz == char.class) {
                return (char) 0;
            }
            if (clazz == float.class) {
                return 0f;
            }
            if (clazz == short.class) {
                return (short) 0;
            }
        }
        return null;
    }
}
