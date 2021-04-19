package com.github.relucent.base.common.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * 集合工具类
 * @author _yyl
 */
public class CollectionUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected CollectionUtil() {
    }

    /**
     * 判断集合是否为空
     * @param collection 集合
     * @return 集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 获得集合第一个元素
     * @param <E> 元素类型泛型
     * @param collection 集合对象
     * @return 集合第一个元素,如果数组为空返回NULL
     */
    public static <E> E getFirst(List<E> collection) {
        return (collection == null || collection.isEmpty()) ? null : collection.get(0);
    }

    /**
     * 转换集合对象为数组对象
     * @param <C> 集合对象泛型
     * @param <E> 集合对象元素泛型
     * @param collection 集合对象
     * @param componentType 集合元素类型
     * @return 数组对象
     */
    public static <C extends Collection<?>, E> E[] toArray(C collection, Class<E> componentType) {
        int length = collection == null ? 0 : collection.size();
        @SuppressWarnings("unchecked")
        E[] array = (E[]) Array.newInstance(componentType, length);
        if (length == 0) {
            return array;
        }
        return collection.toArray(array);
    }

    /**
     * 转换集合对象为数组对象
     * @param <C> 集合对象泛型
     * @param <E> 集合对象元素泛型
     * @param collection 集合对象
     * @param generator 产生所需类型和给定长度的新数组的函数
     * @return 数组对象
     */
    public static <C extends Collection<?>, E> E[] toArray(C collection, IntFunction<E[]> generator) {
        int length = collection == null ? 0 : collection.size();
        if (length == 0) {
            return generator.apply(length);
        }
        return collection.stream().toArray(generator);
    }

    /**
     * 转换集合对象元素
     * @param <T> 原始元素类型
     * @param <R> 转换后的元素类型
     * @param list 集合对象
     * @param mapper 转换方式
     * @return 转换后的集合列表
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (list == null || mapper == null) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 将所有指定元素添加到指定集合
     * @param <T> 要添加的元素和集合的类
     * @param collection 要在其中插入元素的集合
     * @param elements 要插入集合的元素
     * @return 如果集合发生更改则返回true，否则返回false
     */
    public static <T> boolean addAll(Collection<? super T> collection, T[] elements) {
        boolean result = false;
        if (ArrayUtil.isNotEmpty(elements)) {
            for (T element : elements) {
                result |= collection.add(element);
            }
        }
        return result;
    }

    /**
     * 获得集合对象中对象的索引
     * @param collection 集合对象
     * @param typeToFind 查找的对象类型
     * @return 对象的索引
     */
    public static int indexOfType(List<?> collection, Class<?> typeToFind) {
        for (int i = 0; i < collection.size(); i++) {
            Object element = collection.get(i);
            if (typeToFind.isInstance(element)) {
                return i;
            }
        }
        return -1;
    }
}
