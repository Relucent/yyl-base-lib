package com.github.relucent.base.common.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
     * @return 集合第一个元素,如果集合为空返回{@code null}
     */
    public static <E> E getFirst(List<E> collection) {
        return (collection == null || collection.isEmpty()) ? null : collection.get(0);
    }

    /**
     * 返回第一个匹配的元素
     * @param <E> 元素类型泛型
     * @param collection 集合对象
     * @param predicate 匹配条件
     * @return 集合中第一个匹配的元素，如果没有找到则返回{@code null}
     */
    public static <E> E getFirst(List<E> collection, Predicate<E> predicate) {
        if (isEmpty(collection)) {
            return null;
        }
        for (E element : collection) {
            if (predicate.test(element)) {
                return element;
            }
        }
        return null;
    }

    /**
     * 返回匹配的元素列表
     * @param <E> 元素类型泛型
     * @param collection 集合对象
     * @param predicate 匹配条件
     * @return 集合中第一个匹配的元素，如果没有找到则返回{@code null}
     * @return 集合中匹配的元素列表
     */
    public static <E> List<E> find(List<E> collection, Predicate<E> predicate) {
        List<E> result = new ArrayList<>();
        if (isEmpty(collection)) {
            return result;
        }
        for (E element : collection) {
            if (predicate.test(element)) {
                result.add(element);
            }
        }
        return result;
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
        return map(list, mapper, ArrayList::new);
    }

    /**
     * 转换集合对象元素
     * @param <T> 集合元素
     * @param <R> 新集合元素
     * @param <C> 新集合对象类型
     * @param collection 集合对象
     * @param mapper 转换方式
     * @param supplier 新集合构建器
     * @return 新集合对象
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T, R, C extends Collection<R>> C map(Collection<T> collection, Function<T, R> mapper, Supplier<C> supplier) {
        if (supplier == null) {
            supplier = (Supplier) ArrayList::new;
        }
        if (collection == null || mapper == null) {
            return supplier.get();
        }
        return collection.stream().map(mapper).collect(Collectors.toCollection(supplier));
    }

    /**
     * 转换集合对象为映射表
     * @param <T> 原始元素类型
     * @param <K> 映射表的键类型
     * @param <U> 值映射函数的输出类型
     * @param <M> 生成的｛@code Map｝的类型
     * @param collection 集合对象
     * @param keyMapper 生成键的映射函数
     * @param valueMapper 生成值的映射函数
     * @param mergeFunction 合并函数，用于解决两者之间的冲突
     * @param mapSupplier 返回映射表的构造器
     * @return 映射表
     */
    public static <T, K, U, M extends Map<K, U>> Map<K, U> toMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier) {
        Map<K, U> map = mapSupplier.get();
        if (!CollectionUtil.isEmpty(collection)) {
            for (T element : collection) {
                map.merge(keyMapper.apply(element), valueMapper.apply(element), mergeFunction);
            }
        }
        return map;
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

    /**
     * 获取指定的集合（Collection或者iterator）的大小， 此方法可以按如下方式处理对象：
     * <ul>
     * <li>Collection {@code Collection#size()}
     * <li>Map - {@code Map#size()}
     * <li>Array - {@code array.length}
     * <li>Iterator - 迭代器中剩余的元素数
     * <li>Enumeration - 枚举中剩余的元素数
     * </ul>
     * @param object 要获取其大小的对象
     * @return 指定集合的大小，如果对象为null，则为0
     * @throws IllegalArgumentException 无法识别对象
     */
    public static int size(final Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map<?, ?>) {
            total = ((Map<?, ?>) object).size();
        } else if (object instanceof Collection<?>) {
            total = ((Collection<?>) object).size();
        } else if (object instanceof Iterable<?>) {
            total = nextIteratorAndGetSize(((Iterable<?>) object).iterator());
        } else if (object instanceof Object[]) {
            total = ((Object[]) object).length;
        } else if (object instanceof Iterator<?>) {
            total = nextIteratorAndGetSize((Iterator<?>) object);
        } else if (object instanceof Enumeration<?>) {
            final Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
        } else {
            try {
                total = Array.getLength(object);
            } catch (final IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
        return total;
    }

    /**
     * 返回给定迭代器中包含的元素数
     * @param iterator 要检查的迭代器
     * @return 迭代器中包含的元素数
     */
    private static int nextIteratorAndGetSize(final Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }
}
