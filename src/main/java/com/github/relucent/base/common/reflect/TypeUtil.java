package com.github.relucent.base.common.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 类型工具类
 */
public class TypeUtil {

    // =================================Fields=================================================
    private static final ParameterizedType[] EMPTY_PARAMETERIZED_TYPE_ARRAY = new ParameterizedType[0];

    // =================================Constructors===========================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected TypeUtil() {
    }

    // =================================TypeMethods============================================
    /**
     * 获得给定类的第一个泛型参数
     * @param type 被检查的类型，必须是已经确定泛型类型的类型
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     * @param type 被检查的类型，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，即第几个泛型类型
     * @return {@link Type}
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (typeArguments != null && typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     * @param type 指定类型
     * @return 所有泛型参数类型
     */
    public static Type[] getTypeArguments(Type type) {
        if (type == null) {
            return null;
        }

        final ParameterizedType parameterizedType = toParameterizedType(type);
        return (parameterizedType == null) ? null : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(final Type type) {
        return toParameterizedType(type, 0);
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * {@code
     *   class A<T>
     *   class B extends A<String>;
     * }
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B对应的{@link ParameterizedType}，从而获取到String
     * @param type {@link Type}
     * @param interfaceIndex 实现的第几个接口
     * @return {@link ParameterizedType}
     */
    public static ParameterizedType toParameterizedType(final Type type, final int interfaceIndex) {
        if (type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (type instanceof Class) {
            final ParameterizedType[] generics = getGenerics((Class<?>) type);
            if (generics.length > interfaceIndex) {
                return generics[interfaceIndex];
            }
        }
        return null;
    }

    /**
     * 获得Type对应的原始类
     * @param type {@link Type}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Type type) {
        if (type != null) {
            if (type instanceof Class) {
                return (Class<?>) type;
            } else if (type instanceof ParameterizedType) {
                return (Class<?>) ((ParameterizedType) type).getRawType();
            } else if (type instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) type).getBounds()[0];
            } else if (type instanceof WildcardType) {
                final Type[] upperBounds = ((WildcardType) type).getUpperBounds();
                if (upperBounds.length == 1) {
                    return getClass(upperBounds[0]);
                }
            }
        }
        return null;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null <br>
     *
     * <pre>
     * 此方法可以处理：
     * 1. 泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     * 2. 泛型变量，类似于T
     * </pre>
     *
     * @param type 类
     * @param typeVariable 泛型变量，例如T等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Type typeVariable) {
        if (typeVariable instanceof ParameterizedType) {
            return getActualType(type, (ParameterizedType) typeVariable);
        }

        if (typeVariable instanceof TypeVariable) {
            return ActualTypeMapCache.INSTANCE.getActualType(type, (TypeVariable<?>) typeVariable);
        }

        // 没有需要替换的泛型变量，原样输出
        return typeVariable;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null 此方法可以处理复杂的泛型化对象，类似于Map&lt;User, Key&lt;Long&gt;&gt;
     * @param type 类
     * @param parameterizedType 泛型变量，例如List&lt;T&gt;等
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, ParameterizedType parameterizedType) {
        // 字段类型为泛型参数类型，解析对应泛型类型为真实类型，类似于List<T> a
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        // 泛型对象中含有未被转换的泛型变量
        if (TypeUtil.hasTypeVariable(actualTypeArguments)) {
            actualTypeArguments = getActualTypes(type, parameterizedType.getActualTypeArguments());
            if (ArrayUtil.isNotEmpty(actualTypeArguments)) {
                // 替换泛型变量为实际类型，例如List<T>变为List<String>
                parameterizedType = new ParameterizedTypeImpl(actualTypeArguments, parameterizedType.getOwnerType(), parameterizedType.getRawType());
            }
        }
        return parameterizedType;
    }

    /**
     * 获得泛型变量对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     * @param type 类
     * @param typeVariables 泛型变量数组，例如T等
     * @return 实际类型数组，可能为Class等
     */
    public static Type[] getActualTypes(Type type, Type... typeVariables) {
        return ActualTypeMapCache.INSTANCE.getActualTypes(type, typeVariables);
    }

    /**
     * 是否未知类型<br>
     * type为null或者{@link TypeVariable} 都视为未知类型
     * @param type Type类型
     * @return 是否未知类型
     */
    public static boolean isUnknown(Type type) {
        return type == null || type instanceof TypeVariable;
    }

    /**
     * 指定泛型数组中是否含有泛型变量
     * @param types 泛型数组
     * @return 是否含有泛型变量
     */
    public static boolean hasTypeVariable(Type... types) {
        for (Type type : types) {
            if (type instanceof TypeVariable) {
                return true;
            }
        }
        return false;
    }

    // =================================FieldMethods===========================================

    /**
     * 获取字段对应的Type类型<br>
     * @param field 字段
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getType(Field field) {
        return field == null ? null : field.getGenericType();
    }

    /**
     * 获得字段的泛型类型<br>
     * @param clazz Bean类
     * @param fieldName 字段名
     * @return 字段的泛型类型
     */
    public static Type getFieldType(Class<?> clazz, String fieldName) {
        return getType(FieldUtil.getField(clazz, fieldName));
    }

    /**
     * 获得Field对应的原始类<br>
     * @param field {@link Field}
     * @return 原始类，如果无法获取原始类，返回{@code null}
     */
    public static Class<?> getClass(Field field) {
        return field == null ? null : field.getType();
    }

    /**
     * 获得泛型字段对应的泛型实际类型，如果此变量没有对应的实际类型，返回null
     * @param type 实际类型明确的类
     * @param field 字段
     * @return 实际类型，可能为Class等
     */
    public static Type getActualType(Type type, Field field) {
        return field == null ? null : getActualType(ObjectUtil.defaultIfNull(type, field.getDeclaringClass()), field.getGenericType());
    }

    // =================================MethodMethods==========================================
    /**
     * 获取方法的第一个参数类型<br>
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getFirstParamType(Method method) {
        return getParamType(method, 0);
    }

    /**
     * 获取方法的第一个参数类
     * @param method 方法
     * @return 第一个参数类型，可能为{@code null}
     */
    public static Class<?> getFirstParamClass(Method method) {
        return getParamClass(method, 0);
    }

    /**
     * 获取方法的参数类型<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     * @param method 方法
     * @param index 第几个参数的索引，从0开始计数
     * @return {@link Type}，可能为{@code null}
     */
    public static Type getParamType(Method method, int index) {
        Type[] types = getParamTypes(method);
        if (types != null && types.length > index) {
            return types[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类
     * @param method 方法
     * @param index 第几个参数的索引，从0开始计数
     * @return 参数类，可能为{@code null}
     */
    public static Class<?> getParamClass(Method method, int index) {
        Class<?>[] classes = getParamClasses(method);
        if (classes != null && classes.length > index) {
            return classes[index];
        }
        return null;
    }

    /**
     * 获取方法的参数类型列表<br>
     * 优先获取方法的GenericParameterTypes，如果获取不到，则获取ParameterTypes
     * @param method 方法
     * @return {@link Type}列表，可能为{@code null}
     * @see Method#getGenericParameterTypes()
     * @see Method#getParameterTypes()
     */
    public static Type[] getParamTypes(Method method) {
        return method == null ? null : method.getGenericParameterTypes();
    }

    /**
     * 解析方法的参数类型列表<br>
     * 依赖jre\lib\rt.jar
     * @param method t方法
     * @return 参数类型类列表
     * @see Method#getGenericParameterTypes
     * @see Method#getParameterTypes
     */
    public static Class<?>[] getParamClasses(Method method) {
        return method == null ? null : method.getParameterTypes();
    }

    /**
     * 获取方法的返回值类型<br>
     * 获取方法的GenericReturnType
     * @param method 方法
     * @return {@link Type}，可能为{@code null}
     * @see Method#getGenericReturnType()
     * @see Method#getReturnType()
     */
    public static Type getReturnType(Method method) {
        return method == null ? null : method.getGenericReturnType();
    }

    /**
     * 解析方法的返回类型类列表
     * @param method 方法
     * @return 返回值类型的类
     * @see Method#getGenericReturnType
     * @see Method#getReturnType
     */
    public static Class<?> getReturnClass(Method method) {
        return method == null ? null : method.getReturnType();
    }

    // =================================ClassMethods===========================================
    /**
     * 获取指定类所有泛型父类和泛型接口
     * @param clazz 类
     * @return 泛型父类或接口数组
     */
    public static ParameterizedType[] getGenerics(final Class<?> clazz) {
        final List<ParameterizedType> result = new ArrayList<>();
        // 泛型父类
        final Type genericSuper = clazz.getGenericSuperclass();
        if (genericSuper != null && !Object.class.equals(genericSuper)) {
            final ParameterizedType parameterizedType = toParameterizedType(genericSuper);
            if (parameterizedType != null) {
                result.add(parameterizedType);
            }
        }
        // 泛型接口
        final Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (ArrayUtil.isNotEmpty(genericInterfaces)) {
            for (final Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    result.add((ParameterizedType) genericInterface);
                }
            }
        }
        return result.toArray(EMPTY_PARAMETERIZED_TYPE_ARRAY);
    }

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map，例如：
     * @param clazz 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<Type, Type> getTypeMap(Class<?> clazz) {
        return ActualTypeMapCache.INSTANCE.get(clazz);
    }
}
