package com.github.relucent.base.common.lang;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 枚举工具类
 */
public class EnumUtil {

    /**
     * 判断指定类是否为枚举类
     * @param clazz 类
     * @return 是否为枚举类
     */
    public static boolean isEnum(Class<?> clazz) {
        return clazz != null && clazz.isEnum();
    }

    /**
     * 判断指定对象是否为枚举
     * @param object 对象
     * @return 是否为枚举类
     */
    public static boolean isEnum(Object object) {
        return object != null && object.getClass().isEnum();
    }

    /**
     * 获取给定位置的枚举值
     * @param <E> 枚举类型泛型
     * @param enumClass 枚举类
     * @param index 枚举索引
     * @return 枚举值，null表示无此对应枚举
     */
    public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
        final E[] enumConstants = enumClass.getEnumConstants();
        return index >= 0 && index < enumConstants.length ? enumConstants[index] : null;
    }

    /**
     * 枚举类中所有枚举对象的name列表
     * @param clazz 枚举类
     * @return name列表
     */
    public static String[] getNames(Class<? extends Enum<?>> clazz) {
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (enums == null) {
            return null;
        }
        String[] names = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            names[i] = enums[i].name();
        }
        return names;
    }

    /**
     * 通过条件获取枚举，获取不到时为 {@code null}
     * @param enumClass 枚举类
     * @param predicate 条件
     * @param <E> 枚举类型
     * @return 对应枚举 ，获取不到时为 {@code null}
     */
    public static <E extends Enum<E>> E findFirst(Class<E> enumClass, Predicate<? super E> predicate) {
        return Arrays.stream(enumClass.getEnumConstants()).filter(predicate).findFirst().orElse(null);
    }
}
