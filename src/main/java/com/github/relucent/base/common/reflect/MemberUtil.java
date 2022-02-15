package com.github.relucent.base.common.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.relucent.base.common.lang.ClassUtil;

/**
 * 反射相关信息工具类<br>
 * 包含使用{@link java.lang.reflect.Method}/{@link java.lang.reflect.Constructor}的常用代码。<br>
 * 算法参考：org.apache.commons.lang3.reflect.MemberUtils <br>
 */
public class MemberUtil {

    // =================================Fields================================================
    /** *按“可升级性”排序的原始类型数组 */
    private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = { //
            Byte.TYPE, //
            Short.TYPE, //
            Character.TYPE, //
            Integer.TYPE, //
            Long.TYPE, //
            Float.TYPE, //
            Double.TYPE //
    };

    // =================================Constructors===========================================
    /**
     * 工具类私有构造
     */
    protected MemberUtil() {
    }

    // =================================Methods================================================
    /**
     * 判断构造器与实际参数是否匹配
     * @param constructor 构造函数
     * @param parameterTypes 参数类型列表
     * @return 构造器的参数类型是否匹配
     */
    static boolean isMatchingConstructor(final Constructor<?> constructor, final Class<?>[] parameterTypes) {
        return isMatchingExecutable(constructor, parameterTypes);
    }

    /**
     * 判断方法与实际参数是否匹配
     * @param method 方法
     * @param parameterTypes 参数类型列表
     * @return 方法的参数类型是否匹配
     */
    static boolean isMatchingMethod(final Method method, final Class<?>[] parameterTypes) {
        return isMatchingExecutable(method, parameterTypes);
    }

    static boolean isMatchingExecutable(final Executable method, final Class<?>[] parameterTypes) {
        final Class<?>[] methodParameterTypes = method.getParameterTypes();

        if (ClassUtil.isAssignable(parameterTypes, methodParameterTypes, true)) {
            return true;
        }

        if (method.isVarArgs()) {
            int i;
            for (i = 0; i < methodParameterTypes.length - 1 && i < parameterTypes.length; i++) {
                if (!ClassUtil.isAssignable(parameterTypes[i], methodParameterTypes[i], true)) {
                    return false;
                }
            }
            final Class<?> varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
            for (; i < parameterTypes.length; i++) {
                if (!ClassUtil.isAssignable(parameterTypes[i], varArgParameterType, true)) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     * 比较两个构造器与运行时参数类型的匹配度，以便根据比较结果，获取最佳匹配
     * @param left 构造函数
     * @param right 构造函数
     * @param 要与{@code left}/{@code right}匹配的实际运行时参数类型
     * @return 比较的结果
     */
    static int compareConstructorFit(final Constructor<?> left, final Constructor<?> right, final Class<?>[] actual) {
        return compareParameterTypes(left, right, actual);
    }

    /**
     * 比较两个方法与运行时参数类型的匹配度，以便根据比较结果，获取最佳匹配
     * @param left 方法
     * @param right 方法
     * @param 要与{@code left}/{@code right}匹配的实际运行时参数类型
     * @return 比较的结果
     */
    static int compareMethodFit(final Method left, final Method right, final Class<?>[] actual) {
        return compareParameterTypes(left, right, actual);
    }

    /**
     * 返回{@link Member}是否可访问。
     * @param member 检查的成员对象
     * @return {@link Member}是否可访问
     */
    static boolean isAccessible(final Member member) {
        return member != null && Modifier.isPublic(member.getModifiers()) && !member.isSynthetic();
    }

    /**
     * 设置禁止Java语言访问检查（使其签名为私有的情况下也可以被外部调用）
     * @param <T> AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     */
    static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        return setAccessible(accessibleObject, true);
    }

    /**
     * 将此对象的{@code accessible}标志设置为指定的布尔值。<br>
     * 值{@code true}表示反射对象在使用时应该禁止Java语言访问检查。<br>
     * 值{@code false}表示反射的对象应该强制执行Java语言访问检查。<br>
     * @param <T> AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @param flag {@code accessible}标记的新值
     * @return 被设置可访问的对象
     */
    static <T extends AccessibleObject> T setAccessible(T accessibleObject, boolean flag) {
        if (accessibleObject != null) {
            try {
                accessibleObject.setAccessible(flag);
            } catch (final SecurityException se) {
                // Ignore
            }
        }
        return accessibleObject;
    }

    // =================================ToolMethods============================================
    /**
     * 比较两个可执行对象与运行时参数类型的匹配度，以便根据比较结果，获取最佳匹配
     * @param left 可执行对象（构造函数或者方法）
     * @param right 可执行对象（构造函数或者方法）
     * @param 要与{@code left}/{@code right}匹配的实际运行时参数类型
     * @return 比较的结果
     */
    private static int compareParameterTypes(final Executable left, final Executable right, final Class<?>[] actual) {
        final float leftCost = getTotalTransformationCost(actual, left);
        final float rightCost = getTotalTransformationCost(actual, right);
        return Float.compare(leftCost, rightCost);
    }

    /**
     * 返回源参数列表中每个类的对象转换成本之和
     * @param srcArgs 源参数
     * @param executable 用于计算转换成本的可执行对象（构造函数或者方法）
     * @return 转换成本
     */
    private static float getTotalTransformationCost(final Class<?>[] srcArgs, final Executable executable) {
        final Class<?>[] destArgs = executable.getParameterTypes();
        final boolean isVarArgs = executable.isVarArgs();

        // “source”和“destination”分别是实际参数和声明的参数。
        float totalCost = 0.0f;
        final long normalArgsLen = isVarArgs ? destArgs.length - 1 : destArgs.length;
        if (srcArgs.length < normalArgsLen) {
            return Float.MAX_VALUE;
        }
        for (int i = 0; i < normalArgsLen; i++) {
            totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]);
        }
        if (isVarArgs) {
            // 当isVarArgs为true时，srcArgs和dstArgs的长度可能不同。
            // 有两种特殊情况需要考虑：
            final boolean noVarArgsPassed = srcArgs.length < destArgs.length;
            final boolean explicitArrayForVarags = srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1] != null
                    && srcArgs[srcArgs.length - 1].isArray();

            final float varArgsCost = 0.001f;
            final Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
            if (noVarArgsPassed) {
                // 当没有传递 VarArgs 时，最佳匹配是最通用的匹配类型，而不是最具体的。
                totalCost += getObjectTransformationCost(destClass, Object.class) + varArgsCost;
            } else if (explicitArrayForVarags) {
                final Class<?> sourceClass = srcArgs[srcArgs.length - 1].getComponentType();
                totalCost += getObjectTransformationCost(sourceClass, destClass) + varArgsCost;
            } else {
                // 这是典型的VarArgs
                for (int i = destArgs.length - 1; i < srcArgs.length; i++) {
                    final Class<?> srcClass = srcArgs[i];
                    totalCost += getObjectTransformationCost(srcClass, destClass) + varArgsCost;
                }
            }
        }
        return totalCost;
    }

    /**
     * 获取将源类转换为目标类所需的步骤数。
     * @param srcClass 源参数类型
     * @param destClass 目标参数类型
     * @return 对象转换的成本
     */
    private static float getObjectTransformationCost(Class<?> srcClass, final Class<?> destClass) {
        if (destClass.isPrimitive()) {
            return getPrimitivePromotionCost(srcClass, destClass);
        }
        float cost = 0.0f;
        while (srcClass != null && !destClass.equals(srcClass)) {
            if (destClass.isInterface() && ClassUtil.isAssignable(srcClass, destClass)) {
                // 对接口匹配的轻微处罚。
                // 仍然需要一个精确匹配来覆盖接口匹配，
                // 但是接口匹配应该覆盖我们必须覆盖的任何内容找一个超类。
                cost += 0.25f;
                break;
            }
            cost++;
            srcClass = srcClass.getSuperclass();
        }
        // 如果目标类为空，则表示一直在进行对象匹配。通过增加1.5的成本来惩罚这一点。
        if (srcClass == null) {
            cost += 1.5f;
        }
        return cost;
    }

    /**
     * 获取将原始类型升级为其他类型所需的步骤数
     * @param srcClass 源参数类型
     * @param destClass 目标参数类型
     * @return 转换的成本
     */
    private static float getPrimitivePromotionCost(final Class<?> srcClass, final Class<?> destClass) {
        if (srcClass == null) {
            return 1.5f;
        }
        float cost = 0.0f;
        Class<?> cls = srcClass;
        if (!cls.isPrimitive()) {
            // 轻微的拆箱处罚
            cost += 0.1f;
            cls = ClassUtil.wrapperToPrimitive(cls);
        }
        for (int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; i++) {
            if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
                cost += 0.1f;
                if (i < ORDERED_PRIMITIVE_TYPES.length - 1) {
                    cls = ORDERED_PRIMITIVE_TYPES[i + 1];
                }
            }
        }
        return cost;
    }
}
