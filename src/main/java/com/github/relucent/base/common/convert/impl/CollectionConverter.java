package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import com.github.relucent.base.common.collection.IteratorUtil;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeUtil;
import com.github.relucent.base.common.reflect.internal.ObjectConstructorCache;

/**
 * 集合类转换器
 */
public class CollectionConverter implements Converter<Collection<?>> {

    public static CollectionConverter INSTANCE = new CollectionConverter();

    @Override
    public Collection<?> convert(Object source, Type toType) {

        final Type elementType = TypeUtil.getTypeArgument(toType);

        Collection<?> target = newCollection(TypeUtil.getClass(toType), TypeUtil.getClass(elementType));

        if (target == null) {
            return null;
        }

        addElements(source, target, elementType);

        return target;
    }

    /**
     * 创建新的集合对象，返回具体的泛型集合
     * @param <T> 集合中元素类型泛型
     * @param collectionType 集合类型
     * @param elementType 集合中元素类型
     * @return 集合类型对应的实例
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> Collection<T> newCollection(final Class<?> collectionType, final Class<T> elementType) {

        // 抽象集合默认创建 ArrayList
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            return new ArrayList<>();
        }

        // List
        if (collectionType.isAssignableFrom(ArrayList.class)) {
            return new ArrayList<>();
        }

        // LinkedList
        if (collectionType.isAssignableFrom(LinkedList.class)) {
            return new LinkedList<>();
        }

        // Set
        if (collectionType.isAssignableFrom(HashSet.class)) {
            return new HashSet<>();
        }

        // LinkedHashSet
        if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            return new LinkedHashSet<>();
        }

        // TreeSet
        if (collectionType.isAssignableFrom(TreeSet.class)) {
            return new TreeSet<>();
        }

        // EnumSet
        if (collectionType.isAssignableFrom(EnumSet.class)) {
            return (Collection<T>) EnumSet.noneOf((Class<Enum>) elementType);
        }

        // 直接实例化
        try {
            return (Collection<T>) collectionType.newInstance();
        } catch (Exception ignore) {
            // ignore
        }

        // 使用对象构造器强制构建
        try {
            return (Collection<T>) ObjectConstructorCache.INSTANCE.get(TypeReference.of(collectionType)).construct();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 将对象元素添加到目标集合<br>
     * @param source 源对象
     * @param collection 目标集合
     * @param elementType 集合中元素类型
     */
    public void addElements(final Object source, final Collection<?> collection, final Type elementType) {
        if (collection != null) {
            for (Iterator<?> iterator = IteratorUtil.toIterator(source); iterator.hasNext();) {
                collection.add(ConvertUtil.convert(iterator.next(), elementType, null));
            }
        }
    }

}
