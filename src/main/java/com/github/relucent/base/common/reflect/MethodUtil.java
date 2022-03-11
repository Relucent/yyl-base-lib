package com.github.relucent.base.common.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.exception.ExceptionHelper;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.ClassUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 方法{@code java.lang.reflect.Method}相关反射工具类<br>
 */
public class MethodUtil {

    // =================================Fields================================================
    private static final Comparator<Method> METHOD_BY_SIGNATURE = (m1, m2) -> m1.toString().compareTo(m2.toString());

    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected MethodUtil() {
    }
    // =================================Methods================================================

    /**
     * 返回类的全部公共方法
     * @param clazz 方法所属的类
     * @return 方法列表
     */
    public static Method[] getPublicMethods(Class<?> clazz) {
        return clazz == null ? null : clazz.getMethods();
    }

    /**
     * 返回具有指定名称和参数的可访问方法（即可以通过反射调用的方法）。如果找不到这样的方法，返回{@code null}。
     * @param clazz 方法所属的类
     * @param methodName 方法名
     * @param parameterTypes 参数类型数组
     * @return 方法
     */
    public static Method getPublicMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 获得类的所有方法，包括类本身和继承来的所有{@code public}、{@code protected}、 default(package)和 {@code private}方法。
     * @param clazz 方法所属的类
     * @return 方法列表
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        CollectionUtil.addAll(methods, clazz.getDeclaredMethods());
        final Class<?>[] superclasses = ClassUtil.getAllSuperClasses(clazz);
        for (final Class<?> klass : superclasses) {
            CollectionUtil.addAll(methods, klass.getDeclaredMethods());
        }
        return methods.toArray(ArrayConstant.EMPTY_METHOD_ARRAY);
    }

    /**
     * 查找与给定名称匹配且具有兼容参数的可访问方法。<br>
     * 兼容参数意味着每个方法参数都可以从给定的参数中赋值。<br>
     * 该方法可以通过传入包装器类来匹配基元参数。例如，{@code Boolean}将匹配一个基本的{@code Boolean}参数。
     * @param clazz 方法所属的类
     * @param methodName find method with this name
     * @param parameterTypes find method with most compatible parameters
     * @return The accessible method
     */
    public static Method getMatchingPublicMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        try {
            final Method method = clazz.getMethod(methodName, parameterTypes);
            MemberUtil.setAccessible(method);
            return method;
        } catch (final NoSuchMethodException e) {
            // Ignore
        }
        // 搜索所有可访问方法
        final Method[] methods = clazz.getMethods();
        final List<Method> matchingMethods = new ArrayList<>();
        for (final Method method : methods) {
            // 比较名称和参数
            if (method.getName().equals(methodName) && MemberUtil.isMatchingMethod(method, parameterTypes)) {
                matchingMethods.add(method);
            }
        }

        // 通过签名对方法进行排序，以强制确定结果
        Collections.sort(matchingMethods, METHOD_BY_SIGNATURE);

        // 最佳匹配
        Method bestMatch = null;
        for (final Method method : matchingMethods) {
            // 获取方法的可访问版本
            if (bestMatch == null || MemberUtil.compareMethodFit(method, bestMatch, parameterTypes) < 0) {
                bestMatch = method;
            }
        }
        if (bestMatch != null) {
            MemberUtil.setAccessible(bestMatch);
        }

        if (bestMatch != null && bestMatch.isVarArgs() && bestMatch.getParameterTypes().length > 0 && parameterTypes.length > 0) {
            final Class<?>[] methodParameterTypes = bestMatch.getParameterTypes();
            final Class<?> methodParameterComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
            final String methodParameterComponentTypeName = ClassUtil.primitiveToWrapper(methodParameterComponentType).getName();
            final Class<?> lastParameterType = parameterTypes[parameterTypes.length - 1];
            final String parameterTypeName = (lastParameterType == null) ? null : lastParameterType.getName();
            final String parameterTypeSuperClassName = (lastParameterType == null) ? null : lastParameterType.getSuperclass().getName();
            if (parameterTypeName != null && parameterTypeSuperClassName != null && !methodParameterComponentTypeName.equals(parameterTypeName)
                    && !methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                return null;
            }
        }
        return bestMatch;
    }

    /**
     * 查找指定方法，查找范围包括类本身和继承来的所有{@code public}、{@code protected}、 default(package)和 {@code private}方法。<br>
     * 如果找不到对应的方法则返回{@code null}。<br>
     * @param clazz 方法所属的类，如果为{@code null}返回{@code null}
     * @param methodName 方法名
     * @param parameterTypes 参数类型数组
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMatchingMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
        if (clazz == null) {
            return null;
        }
        if (StringUtil.isEmpty(methodName)) {
            return null;
        }
        final Method[] methods = getAllMethods(clazz);
        if (ArrayUtil.isEmpty(methods)) {
            return null;
        }

        Method inexactMatch = null;
        for (final Method method : methods) {
            // 方法名不相同，排除
            if (!methodName.equals(method.getName())) {
                continue;
            }
            // 参数类型完全匹配
            if (Objects.deepEquals(parameterTypes, method.getParameterTypes())) {
                return method;
            }
            // 参数类型不精确匹配(可以通过安全的转型进行匹配)
            if (ClassUtil.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                if (inexactMatch == null) {
                    inexactMatch = method;
                } else if (distanceForMatching(parameterTypes, method.getParameterTypes()) //
                        < distanceForMatching(parameterTypes, inexactMatch.getParameterTypes())) {
                    inexactMatch = method;
                }
            }
        }
        return inexactMatch;
    }

    // Method Invoke
    // ----------------------------------------------------------------------
    /**
     * 调用名称匹配不带参数的方法。
     * @param object 方法的所属对象
     * @param forceAccess 是否强制访问调用方法，即使它不可访问
     * @param methodName 方法名称
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName) {
        return invokeMethod(object, forceAccess, methodName, ArrayConstant.EMPTY_OBJECT_ARRAY, ArrayConstant.EMPTY_CLASS_ARRAY);
    }

    /**
     * 调用参数类型兼容匹配的方法。
     * @param object 方法的所属对象
     * @param forceAccess 是否强制访问调用方法，即使它不可访问
     * @param methodName 方法名称
     * @param args 参数数组, {@code null} 被视为没有参数
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, Object... args) {
        args = ArrayUtil.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassUtil.toClass(args);
        return invokeMethod(object, forceAccess, methodName, args, parameterTypes);
    }

    /**
     * 调用参数类型兼容匹配的方法。
     * @param object 方法的所属对象
     * @param forceAccess 是否强制访问调用方法，即使它不可访问
     * @param methodName 方法名称
     * @param args 参数数组, {@code null} 被视为没有参数
     * @param parameterTypes 参数的类型数组, {@code null} 被视为没有参数
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName, Object[] args,
            Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);

        final String messagePrefix;
        Method method = null;

        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(), methodName, parameterTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingPublicMethod(object.getClass(), methodName, parameterTypes);
        }

        if (method == null) {
            throw ExceptionHelper.error(messagePrefix + methodName + "() on object: " + object.getClass().getName());
        }
        args = toVarArgs(method, args);

        return invoke(method, object, args);
    }

    /**
     * 调用参数类型完全匹配的方法
     * @param object 方法的对象
     * @param methodName 方法名称
     * @param args 方法的参数数组，{@code null}视为空数组
     * @param parameterTypes 方法的参数类型数组，{@code null}视为空数组
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeExactMethod(final Object object, final String methodName, Object[] args, Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);
        final Method method = getPublicMethod(object.getClass(), methodName, parameterTypes);
        if (method == null) {
            throw ExceptionHelper.error("No such accessible method: " + methodName + "() on class: " + object.getClass());
        }
        return invoke(method, object, args);
    }

    /**
     * 调用参数类型兼容匹配的{@code static}方法。
     * @param clazz 静态方法所属的类
     * @param methodName 方法名称
     * @param args 方法的参数数组，{@code null}视为空数组
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeStaticMethod(final Class<?> clazz, final String methodName, Object... args) {
        args = ArrayUtil.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassUtil.toClass(args);
        return invokeStaticMethod(clazz, methodName, args, parameterTypes);
    }

    /**
     * 调用参数类型与对象类型匹配{@code static}方法。
     * @param clazz 静态方法所属的类
     * @param methodName 方法名称
     * @param args 方法的参数数组，{@code null}视为空数组
     * @param parameterTypes 方法的参数类型数组，{@code null}视为空数组
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeStaticMethod(final Class<?> clazz, final String methodName, Object[] args, Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);
        final Method method = getMatchingPublicMethod(clazz, methodName, parameterTypes);
        if (method == null) {
            throw ExceptionHelper.error("No such accessible method: " + methodName + "() on class: " + clazz.getName());
        }
        args = toVarArgs(method, args);
        return invoke(method, null, args);
    }

    /**
     * 调用参数参数类型完全匹配的{@code static}方法。
     * @param clazz 静态方法所属的类
     * @param methodName 方法名称
     * @param args 方法的参数数组，{@code null}视为空数组
     * @param parameterTypes 方法的参数类型数组，{@code null}视为空数组
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    public static Object invokeExactStaticMethod(final Class<?> clazz, final String methodName, Object[] args, Class<?>[] parameterTypes) {
        args = ArrayUtil.nullToEmpty(args);
        parameterTypes = ArrayUtil.nullToEmpty(parameterTypes);
        final Method method = getPublicMethod(clazz, methodName, parameterTypes);
        if (method == null) {
            throw ExceptionHelper.error("No such accessible method: " + methodName + "() on class: " + clazz.getName());
        }
        return invoke(method, null, args);
    }

    // ----------------------------------------------------------------------
    /**
     * 返回一个规范形式的参数数组
     * @param method 方法
     * @param args 方法的参数数组
     * @return 规范形式的参数数组
     */
    private static Object[] toVarArgs(final Method method, Object[] args) {
        if (method.isVarArgs()) {
            final Class<?>[] methodParameterTypes = method.getParameterTypes();
            args = getVarArgs(args, methodParameterTypes);
        }
        return args;
    }

    /**
     * 给定一个传递给可变参数类型(VarArgs)方法的参数数组，返回一个规范形式的参数数组，即一个具有声明数量的参数的数组，其最后一个参数是可变参数类型的数组。
     * @param args 传递给可变参数类型方法的参数数组
     * @param methodParameterTypes 方法参数类型的声明数组
     * @return 传递给方法的可变参数数组
     */
    static Object[] getVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length && (args[args.length - 1] == null
                || args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1]))) {
            // 参数数组已经是该方法的标准形式
            return args;
        }

        // 构造一个与方法声明的参数类型匹配的新数组
        final Object[] newArgs = new Object[methodParameterTypes.length];

        // 拷贝正常（非VarArgs）参数 （最后一个可变参数不拷贝）
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);

        // 构造一个新的可变参数数组
        final Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        final int varArgLength = args.length - methodParameterTypes.length + 1;

        Object varArgsArray = null;
        // 可变参数部分是基本类型
        if (varArgComponentType.isPrimitive()) {
            varArgsArray = Array.newInstance(ClassUtil.primitiveToWrapper(varArgComponentType), varArgLength);
            // 将可变参数复制到可变参数数组中，考虑到基本类型的隐式转换
            for (int i = 0, j = methodParameterTypes.length - 1; i < varArgLength; i++) {
                Object value = Array.get(args, j + i);
                value = ConvertUtil.convert(value, varArgComponentType, null);
                Array.set(varArgsArray, i, value);
            }
            // 最后参数是基本类型，需要从包装器类型拆箱到基元类型
            varArgsArray = ArrayUtil.toPrimitive(varArgsArray);
        }
        // 可变参数部分是对象类型
        else {
            varArgsArray = Array.newInstance(varArgComponentType, varArgLength);
            // 将可变参数复制到可变参数数组中
            System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);
        }

        // 将可变参数数组(VarArgs)存储到要返回的数组的最后一个位置
        newArgs[methodParameterTypes.length - 1] = varArgsArray;

        // 返回规范的参数数组（最后一个位置是可变参数数组）
        return newArgs;
    }

    // Internal tools
    // ----------------------------------------------------------------------
    /**
     * 返回可赋值参数类类型之间的继承跃点总数（需要转换才能匹配的数量），如果参数不可赋值，则返回-1。<br>
     * 该方法不是通用的，仅用于 getMatchingMethod 的特定用途。<br>
     * @param classArray 参数类型数组
     * @param toClassArray 比较的参数类型数组
     * @return 可赋值参数类类型之间的继承跃点总数
     */
    private static int distanceForMatching(final Class<?>[] classArray, final Class<?>[] toClassArray) {
        int answer = 0;
        // 参数之间不能赋值
        if (!ClassUtil.isAssignable(classArray, toClassArray, true)) {
            return -1;
        }
        // 计算跃点数
        for (int offset = 0; offset < classArray.length; offset++) {
            // 参数一致
            if (classArray[offset].equals(toClassArray[offset])) {
                continue;
            }
            // 需要装箱或者拆箱才能匹配
            if (ClassUtil.isAssignable(classArray[offset], toClassArray[offset], true)
                    && !ClassUtil.isAssignable(classArray[offset], toClassArray[offset], false)) {
                answer++;
                continue;
            }
            answer = answer + 2;
        }
        return answer;
    }

    // invoke
    // ----------------------------------------------------------------------
    /**
     * 调用方法
     * @param method 调用的方法
     * @param object 调用方法的对象
     * @param args 方法的参数数组，{@code null}视为空数组
     * @return 调用的方法返回的值
     * @throws RuntimeException 如果请求的方法无法通过反射访问或者调用失败
     */
    private static Object invoke(final Method method, final Object object, final Object[] args) {
        try {
            return method.invoke(null, args);
        } catch (Exception e) {
            throw ExceptionHelper.propagate(e);
        }
    }
}
