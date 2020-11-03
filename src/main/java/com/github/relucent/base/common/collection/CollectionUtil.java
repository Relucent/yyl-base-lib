package com.github.relucent.base.common.collection;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

/**
 * 集合工具类
 * @author _yyl
 */
public class CollectionUtil {

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
