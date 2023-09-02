package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 数组类型转换器
 * @author YYL
 * @version 2020-01-01
 * @see Converter
 */
public class ArrayConverter implements Converter<Object> {

    public static final ArrayConverter INSTANCE = new ArrayConverter();

    @Override
    public Object convert(Object source, Class<? extends Object> toType) {
        if (source == null) {
            return null;
        }
        try {
            Class<?> targetComponentType = toType.getComponentType();
            return source.getClass().isArray() ? convertArrayToArray(source, targetComponentType) : convertObjectToArray(source, targetComponentType);
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 数组到数组的转换
     * @param source 被转换的数组值
     * @param targetComponentType 要转换的数组元素类型
     * @return 转换后的数组
     */
    private Object convertArrayToArray(Object source, Class<?> targetComponentType) {

        Class<?> valueComponentType = ArrayUtil.getComponentType(source);

        if (valueComponentType == targetComponentType) {
            return source;
        }

        int length = ArrayUtil.getLength(source);
        Object result = Array.newInstance(targetComponentType, length);

        for (int i = 0; i < length; i++) {
            Array.set(result, i, convertComponentType(Array.get(source, i), targetComponentType));
        }
        return result;
    }

    /**
     * 非数组到数组的转换
     * @param source 被转换值
     * @param targetComponentType 要转换的数组元素类型
     * @return 转换后的数组
     */
    @SuppressWarnings("unchecked")
    private Object convertObjectToArray(Object source, Class<?> targetComponentType) {
        if (source instanceof CharSequence) {
            // 字符数组
            if (targetComponentType == char.class || targetComponentType == Character.class) {
                return convertArrayToArray(source.toString().toCharArray(), targetComponentType);
            }
            // 字节数组
            if (targetComponentType == byte.class) {
                return StringUtil.getBytes(source.toString());
            }
            // 纯字符串情况下按照逗号分隔
            return convertArrayToArray(StringUtil.split(source.toString(), StringConstant.COMMA), targetComponentType);
        }

        // List转数组
        if (source instanceof List) {
            List<?> list = (List<?>) source;
            Object result = Array.newInstance(targetComponentType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(result, i, convertComponentType(list.get(i), targetComponentType));
            }
            return result;
        }
        // 集合转数组
        if (source instanceof Collection) {
            Collection<?> collection = (Collection<?>) source;
            Object result = Array.newInstance(targetComponentType, collection.size());
            int i = 0;
            for (Object element : collection) {
                Array.set(result, i, convertComponentType(element, targetComponentType));
                i++;
            }
            return result;
        }
        // 可循环对象转数组
        if (source instanceof Iterable) {
            List<Object> temp = new ArrayList<>();
            Iterable.class.cast(source).forEach(temp::add);
            return convertObjectToArray(temp, targetComponentType);
        }
        // 迭代器转数组
        if (source instanceof Iterator) {
            List<Object> temp = new ArrayList<>();
            Iterator.class.cast(source).forEachRemaining(temp::add);
            return convertObjectToArray(temp, targetComponentType);
        }

        // 单元素数组
        return convertToSingleElementArray(source, targetComponentType);
    }

    /**
     * 转换元素类型
     * @param value 值
     * @param targetComponentType 要转换的数组元素类型
     * @return 转换后的值
     */
    private Object convertComponentType(Object value, Class<?> targetComponentType) {
        return ConvertUtil.convert(value, targetComponentType);
    }

    /**
     * 单元素数组
     * @param value 被转换的值
     * @param targetComponentType 要转换的数组元素类型
     * @return 数组，只包含一个元素
     */
    private Object[] convertToSingleElementArray(Object value, Class<?> targetComponentType) {
        Object[] array = ArrayUtil.newArray(targetComponentType, 1);
        array[0] = convertComponentType(value, targetComponentType);
        return array;
    }
}
