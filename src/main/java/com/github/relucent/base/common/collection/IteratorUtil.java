package com.github.relucent.base.common.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * 迭代器{@link Iterator} 相关工具类
 */
public class IteratorUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected IteratorUtil() {
    }

    /**
     * 判断迭代器是否还有元素，如果迭代器为{@code null}，或者已经没有更多元素则返回{@code true}
     * @param iterator 迭代器对象
     * @return 迭代器是否还有元素
     * @see Iterator#hasNext()
     */
    public static boolean hasNext(final Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    /**
     * 从给定的对象中获取可能存在的{@link Iterator}，规则如下：<br>
     * 1、{@code null} - {@code null}<br>
     * 2、{@code Iterator} - 直接返回<br>
     * 2、{@code Iterable} - {@link Iterable#iterator()} <br>
     * 3、{@code Collection} -{@link Collection#iterator()}<br>
     * 4、{@code Enumeration} - {@link EnumerationIterator}<br>
     * 5、{@code Array} 数组对象 - {@link ArrayIterator}<br>
     * 6、{@code Map} - {@link Map#entrySet()}<br>
     * 7、{@code Dictionary} - {@link Dictionary#elements()}<br>
     * 8、{@code NodeList} - {@link NodeListIterator}<br>
     * 9、{@code Node} - {@link Node#getChildNodes()}<br>
     * 10、其他 - 返回单对象的{@link ArrayIterator}<br>
     * @param object 可以获取{@link Iterator}的对象
     * @return {@link Iterator}
     */
    public static Iterator<?> toIterator(final Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Iterator) {
            return (Iterator<?>) object;
        }
        if (object instanceof Iterable) {
            return ((Iterable<?>) object).iterator();
        }
        if (object instanceof Collection) {
            return ((Collection<?>) object).iterator();
        }
        if (ArrayUtil.isArray(object)) {
            return new ArrayIterator<>(object);
        }
        if (object instanceof Enumeration) {
            return new EnumerationIterator<>((Enumeration<?>) object);
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).entrySet().iterator();
        }
        if (object instanceof Dictionary) {
            return new EnumerationIterator<>(((Dictionary<?, ?>) object).elements());
        }
        if (object instanceof NodeList) {
            return new NodeListIterator((NodeList) object);
        }
        if (object instanceof Node) {
            return new NodeListIterator(((Node) object).getChildNodes());
        }
        return Arrays.asList(object).iterator();
    }
}
