package com.github.relucent.base.common.lang;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.github.relucent.base.common.constant.ArrayConstant;

/**
 * 数组工具类
 * @author YYL
 */
public class ArrayUtil {
    /**
     * 在列表或数组中找不到元素时的索引值：{@code -1}
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ArrayUtil() {
    }

    // ===# Object[]
    /**
     * 判断数组是否为空
     * @param <T> 数组元素泛型
     * @param array 对象的数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(final T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param <T> 数组元素泛型
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    /**
     * 返回指定数组的长度<br>
     * 如果输入数组为{@code null}，则返回{@code 0}<br>
     * 
     * <pre>
     * ArrayUtil.getLength(null)            = 0
     * ArrayUtil.getLength([])              = 0
     * ArrayUtil.getLength([null])          = 1
     * ArrayUtil.getLength([true, false])   = 2
     * ArrayUtil.getLength([1, 2, 3])       = 3
     * ArrayUtil.getLength(["a", "b", "c"]) = 3
     * </pre>
     * 
     * @param array 需要获取长度的数组
     * @return 数组的长度, 如果数组为 {@code null}则返回 {@code 0}
     * @throws IllegalArgumentException 如果对象参数不是数组
     */
    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 查找指定对象在数组中的索引
     * @param <T> 数组元素类型
     * @param array 要搜索的数组
     * @param objectToFind 要查找的对象
     * @return 对象在数组中的索引，如果没有找到返回-1
     */
    public static <T> int indexOf(final T[] array, final T objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * 从指定索引开始查找指定对象在数组中的索引
     * @param <T> 数组元素类型
     * @param array 要搜索的数组
     * @param objectToFind 要查找的对象
     * @param startIndex 开始搜索的索引
     * @return 对象在数组中的索引，如果没有找到返回-1
     */
    public static <T> int indexOf(final T[] array, final T objectToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        if (objectToFind == null) {
            for (; index < array.length; index++) {
                if (array[index] == null) {
                    return index;
                }
            }
        } else {
            for (; index < array.length; index++) {
                if (objectToFind.equals(array[index])) {
                    return index;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 从指定索引开始查找指定对象在数组中的最后一个索引
     * @param <T> 数组元素类型
     * @param array 要搜索的数组
     * @param objectToFind 要查找的对象
     * @return 对象在数组中的最后一个索引，如果没有找到返回-1
     */
    public static <T> int lastIndexOf(final T[] array, final T objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定对象在数组中的最后一个索引
     * @param <T> 数组元素类型
     * @param array 要搜索的数组
     * @param objectToFind 要查找的对象
     * @param startIndex 开始搜索的索引
     * @return 对象在数组中的最后一个索引，如果没有找到返回-1
     */
    public static <T> int lastIndexOf(final T[] array, final T objectToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }
        if (index >= array.length) {
            index = array.length - 1;
        }
        if (objectToFind == null) {
            for (; index >= 0; index--) {
                if (array[index] == null) {
                    return index;
                }
            }
        } else {
            for (; index >= 0; index--) {
                if (objectToFind.equals(array[index])) {
                    return index;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的对象
     * @param <T> 数组元素类型
     * @param array 要搜索的数组
     * @param objectToFind 要查找的对象
     * @return 如果要查找的对象在数组中，返回{@code true}；否则返回 {@code false}
     */
    public static <T> boolean contains(final T[] array, final T objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 获得对象数组中第一个元素
     * @param <T> 元素类型泛型
     * @param array 要搜索的数组
     * @return 数组第一个元素,如果数组为空返回{@code null}
     */
    public static <T> T getFirst(T[] array) {
        return (array == null || array.length == 0) ? null : array[0];
    }

    /**
     * 数组中是否包含{@code null}元素,
     * @param <T> 数组元素类型
     * @param array 被检查的对象的数组
     * @return 如果数组中包含{@code null}元素返回true，否则返回false
     */
    public static <T> boolean hasNull(T[] array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (null == element) {
                    return true;
                }
            }
        }
        return false;
    }

    // ===# byte[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final byte[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final byte[] array, final byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final byte[] array, final byte valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final byte[] array, final byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final byte[] array, final byte valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final byte[] array, final byte valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# short[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final short[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final short[] array, final short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final short[] array, final short valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final short[] array, final short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final short[] array, final short valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final short[] array, final short valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# int[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final int[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final int[] array, final int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final int[] array, final int valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final int[] array, final int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final int[] array, final int valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final int[] array, final int valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# long[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final long[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final long[] array, final long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final long[] array, final long valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final long[] array, final long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final long[] array, final long valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final long[] array, final long valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# float[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final float[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final float[] array, final float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final float[] array, final float valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final float[] array, final float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final float[] array, final float valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final float[] array, final float valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# double[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final double[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final double[] array, final double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final double[] array, final double valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final double[] array, final double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final double[] array, final double valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final double[] array, final double valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# boolean[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final boolean[] array, final boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final boolean[] array, final boolean valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final boolean[] array, final boolean valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    // ===# char[]
    /**
     * 判断数组是否为空
     * @param array 对象的数组
     * @return 是否为空
     */
    public static boolean isEmpty(final char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否为非空
     * @param array 对象的数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final char[] array) {
        return !isEmpty(array);
    }

    /**
     * 查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final char[] array, final char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }

    /**
     * 从指定索引开始查找指定值在数组中的索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int indexOf(final char[] array, final char valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            index = 0;
        }
        for (; index < array.length; index++) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final char[] array, final char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }

    /**
     * 从指定索引开始查找指定值在数组中的最后一个索引
     * @param array 要搜索的数组
     * @param valueToFind 要查找的值
     * @param startIndex 开始搜索的索引
     * @return 指定值在数组中的索引，如果没有找到返回-1
     */
    public static int lastIndexOf(final char[] array, final char valueToFind, final int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        int index = startIndex;
        if (index < 0) {
            return INDEX_NOT_FOUND;
        }

        if (index >= array.length) {
            index = array.length - 1;
        }
        for (; index >= 0; index--) {
            if (valueToFind == array[index]) {
                return index;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 检查指定数组中是否包含要查找的值
     * @param array 要查找的值
     * @param valueToFind 要查找的值
     * @return 如果要查找的值在数组中返回{@code true}，否则返回 {@code false}
     */
    public static boolean contains(final char[] array, final char valueToFind) {
        return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
    }

    /**
     * 将{@code null}更改为空数组引用。<br>
     * 作为一种内存优化技术，传入的空数组将被静态常量类{@code ArrayConstant.EMPTY_OBJECT_ARRAY}引用覆盖<br>
     * @param array 可能为{@code null}的数组
     * @return 如果数组为{@code null}或者是一个空数组，则返回空数组引用，否则原样返回
     */
    public static Object[] nullToEmpty(final Object[] array) {
        if (isEmpty(array)) {
            return ArrayConstant.EMPTY_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 将{@code null}更改为空数组引用。<br>
     * 作为一种内存优化技术，传入的空数组将被静态常量类{@code ArrayConstant.EMPTY_CLASS_ARRAY}引用覆盖<br>
     * @param array 可能为{@code null}的数组
     * @return 如果数组为{@code null}或者是一个空数组，则返回空数组引用，否则原样返回
     */
    public static Class<?>[] nullToEmpty(final Class<?>[] array) {
        if (isEmpty(array)) {
            return ArrayConstant.EMPTY_CLASS_ARRAY;
        }
        return array;
    }

    // To Wrapper Object
    // -----------------------------------------------------------------------
    /**
     * 将布尔值基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code boolean}数组
     * @return {@code Boolean} 数组
     */
    public static Boolean[] toObject(final boolean[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        final Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (array[i] ? Boolean.TRUE : Boolean.FALSE);
        }
        return result;
    }

    /**
     * 将字节基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code byte}数组
     * @return {@code Byte} 数组
     */
    public static Byte[] toObject(final byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_BYTE_OBJECT_ARRAY;
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Byte.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将字符基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Character}数组
     * @return {@code char} 数组
     */
    public static Character[] toObject(final char[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        final Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Character.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将双精度浮点基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Double}数组
     * @return {@code double} 数组
     */
    public static Double[] toObject(final double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Double.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将浮点基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Float}数组
     * @return {@code float} 数组
     */
    public static Float[] toObject(final float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Float.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将整型基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Integer}数组
     * @return {@code int} 数组
     */
    public static Integer[] toObject(final int[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Integer.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将长整型基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Long}数组
     * @return {@code long} 数组
     */
    public static Long[] toObject(final long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_LONG_OBJECT_ARRAY;
        }
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Long.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将短整型基础类型数组转换为对象数组。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Short}数组
     * @return {@code short} 数组
     */
    public static Short[] toObject(final short[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_SHORT_OBJECT_ARRAY;
        }
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Short.valueOf(array[i]);
        }
        return result;
    }

    // Array toPrimitive
    // ----------------------------------------------------------------------

    /**
     * 将布尔对象数组({@code Boolean[]})转换为布尔基础类型数组({@code boolean[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Boolean}数组
     * @return {@code boolean} 数组, 如果输入数组为{@code null} 则返回 {@code null}
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static boolean[] toPrimitive(final Boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    /**
     * 将布尔对象数组({@code Boolean[]})转换为布尔基础类型数组({@code boolean[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Boolean}数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code boolean} 数组, 如果输入数组为{@code null} 则返回 {@code null}
     */
    public static boolean[] toPrimitive(final Boolean[] array, final boolean valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            final Boolean b = array[i];
            result[i] = (b == null ? valueForNull : b.booleanValue());
        }
        return result;
    }

    /**
     * 将字节对象数组({@code Byte[]})转换为字节基础类型数组({@code byte[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Byte}数组
     * @param array a {@code Byte} array, may be {@code null}
     * @return {@code byte} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    /**
     * 将字节对象数组({@code Byte[]})转换为字节基础类型数组({@code byte[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Byte}数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code byte} 数组
     */
    public static byte[] toPrimitive(final Byte[] array, final byte valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            final Byte b = array[i];
            result[i] = (b == null ? valueForNull : b.byteValue());
        }
        return result;
    }

    /**
     * 将字符对象数组({@code Character[]})转换为字符基础类型数组({@code char[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Character} 数组
     * @return a {@code char} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static char[] toPrimitive(final Character[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    /**
     * 将字符对象数组({@code Character[]})转换为字符基础类型数组({@code char[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Character} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return a {@code char} 数组
     */
    public static char[] toPrimitive(final Character[] array, final char valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            final Character b = array[i];
            result[i] = (b == null ? valueForNull : b.charValue());
        }
        return result;
    }

    /**
     * 将整型对象数组({@code Integer[]})转换为长整型基础类型数组({@code int[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Integer} 数组
     * @return {@code int} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static int[] toPrimitive(final Integer[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    /**
     * 将整型对象数组({@code Long[]})转换为长整型基础类型数组({@code long[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Long} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code long} 数组
     */
    public static long[] toPrimitive(final Long[] array, final long valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            final Long b = array[i];
            result[i] = (b == null ? valueForNull : b.longValue());
        }
        return result;
    }

    /**
     * 将长整型对象数组({@code Long[]})转换为整型基础类型数组({@code long[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Long} 数组
     * @return {@code long} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static long[] toPrimitive(final Long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    /**
     * 将长整型对象数组({@code Integer[]})转换为整型基础类型数组({@code int[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Integer} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code int} 数组
     */
    public static int[] toPrimitive(final Integer[] array, final int valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return ArrayConstant.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            final Integer b = array[i];
            result[i] = (b == null ? valueForNull : b.intValue());
        }
        return result;
    }

    /**
     * 将短整型对象数组({@code Short[]})转换为短整型基础类型数组({@code short[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Short} 数组
     * @return {@code short} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static short[] toPrimitive(final Short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }

    /**
     * 将短整型对象数组({@code Short[]})转换为短整型基础类型数组({@code short[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Short} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code short} 数组
     */
    public static short[] toPrimitive(final Short[] array, final short valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            final Short b = array[i];
            result[i] = (b == null ? valueForNull : b.shortValue());
        }
        return result;
    }

    /**
     * 将双精度浮点对象数组({@code Double[]})转换为双精度浮点基础类型数组({@code double[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Double} 数组
     * @return {@code double} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static double[] toPrimitive(final Double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    /**
     * 将双精度浮点对象数组({@code Double[]})转换为双精度浮点基础类型数组({@code double[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Double} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code double} 数组
     */
    public static double[] toPrimitive(final Double[] array, final double valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            final Double b = array[i];
            result[i] = (b == null ? valueForNull : b.doubleValue());
        }
        return result;
    }

    /**
     * 将浮点对象数组({@code Float[]})转换为浮点基础类型数组({@code float[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Float} 数组
     * @return {@code float} 数组
     * @throws NullPointerException 如果数组元素为{@code null}
     */
    public static float[] toPrimitive(final Float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    /**
     * 将浮点对象数组({@code Float[]})转换为浮点基础类型数组({@code float[]})。<br>
     * 对于{@code null}输入数组，此方法返回{@code null}。
     * @param array {@code Float} 数组
     * @param valueForNull 当数组元素为{@code null}时，要插入的值
     * @return {@code float} 数组
     */
    public static float[] toPrimitive(final Float[] array, final float valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayConstant.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            final Float b = array[i];
            result[i] = (b == null ? valueForNull : b.floatValue());
        }
        return result;
    }

    /**
     * 从包装器类型数组创建基元类型数组。<br>
     * 此方法为{@code null}输入数组返回{@code null}。<br>
     * @param array 包装器对象的数组
     * @return 对应基元类型的数组，或原始数组
     */
    public static Object toPrimitive(final Object array) {
        if (array == null) {
            return null;
        }
        final Class<?> ct = array.getClass().getComponentType();
        final Class<?> pt = ClassUtil.wrapperToPrimitive(ct);
        if (Boolean.TYPE.equals(pt)) {
            return toPrimitive((Boolean[]) array);
        }
        if (Byte.TYPE.equals(pt)) {
            return toPrimitive((Byte[]) array);
        }
        if (Character.TYPE.equals(pt)) {
            return toPrimitive((Character[]) array);
        }
        if (Long.TYPE.equals(pt)) {
            return toPrimitive((Long[]) array);
        }
        if (Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[]) array);
        }
        if (Short.TYPE.equals(pt)) {
            return toPrimitive((Short[]) array);
        }
        if (Double.TYPE.equals(pt)) {
            return toPrimitive((Double[]) array);
        }
        if (Float.TYPE.equals(pt)) {
            return toPrimitive((Float[]) array);
        }
        return array;
    }

    // Array Edit
    // ----------------------------------------------------------------------
    /**
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容
     * @param <T> 数组元素类型
     * @param array 数组
     * @param filter 过滤器接口，用于定义过滤规则，{@code null}返回原集合
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Predicate<T> filter) {
        if (array == null || filter == null) {
            return array;
        }
        List<T> result = new ArrayList<>();
        for (T element : array) {
            if (filter.test(element)) {
                result.add(element);
            }
        }
        Class<?> componentType = array.getClass().getComponentType();
        T[] newArray = newArray(componentType, result.size());
        return result.toArray(newArray);
    }

    /**
     * 按照指定规则，转换数组中的元素
     * @param <T> 原数组类型
     * @param <R> 目标数组类型
     * @param array 被转换的数组
     * @param componentType 目标的元素类型
     * @param mapper 转换规则函数
     * @return 转换后的数组
     */
    public static <T, R> R[] map(T[] array, Class<R> componentType, Function<? super T, ? extends R> mapper) {
        final R[] result = newArray(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    /**
     * 创建具有指定组件类型和长度的新数组
     * @param <T> 数组元素类型
     * @param componentType 数组元素类型
     * @param length 数组长度
     * @return 新数组
     * @see Array#newInstance(Class, int)
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    /**
     * 获取数组对象的元素类型
     * @param array 数组对象
     * @return 元素类型
     */
    public static Class<?> getComponentType(Object array) {
        return array != null ? array.getClass().getComponentType() : null;
    }

    // Generic array
    // ----------------------------------------------------------------------
    /**
     * 将可变参数转换为数组
     * @param <T> 元素类型
     * @param varargs 可变参数
     * @return 数组
     */
    @SafeVarargs
    public static <T> T[] asArray(T... varargs) {
        return varargs;
    }
}
