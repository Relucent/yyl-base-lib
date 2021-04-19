package com.github.relucent.base.common.lang;

/**
 * 数组工具类
 * @author _yyl
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
     * 将可变参数转换为数组
     * @param <T> 元素类型
     * @param varargs 可变参数
     * @return 数组
     */
    @SafeVarargs
    public static <T> T[] toArray(T... varargs) {
        return varargs;
    }
}
