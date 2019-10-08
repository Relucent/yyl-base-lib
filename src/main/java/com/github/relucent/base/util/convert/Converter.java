package com.github.relucent.base.util.convert;

/**
 * 类型转换接口
 * @param <T> 转换到的目标类型
 * @author YYL
 */
public interface Converter<T> {

    /**
     * 类型转换
     * @param source 初始类型对象
     * @param toType 需要转换的类型
     * @param vDefault 默认值对象(如果无法转换，则返回改对象)
     * @return 类型转换后的对象
     */
    public T convert(Object source, Class<? extends T> toType, T vDefault);

    /**
     * 支持转换的类型
     * @param toType 转换的类型
     * @return 如果支持该类型的转换，则返回true，否则返回false.
     */
    public boolean support(Class<? extends T> toType);
}
