package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.ClassLoaderUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 类转换器，将类名转换为类<br>
 * 注意：加载类时会初始化类（调用static模块内容和初始化static属性）<br>
 */
public class ClassConverter implements BasicConverter<Class<?>> {

    public static ClassConverter INSTANCE = new ClassConverter();

    /**
     * 将类名转换为{@code Class}对象
     * @param source 类名（或类对象）
     * @param toType 目标类型
     * @return 对应的{@code Class}对象；类名非法或类不存在时返回{@code null}
     */
    public Class<?> convertInternal(Object source, Class<? extends Class<?>> toType) {
        try {
            return ClassLoaderUtil.loadClass(StringUtil.string(source), true);
        } catch (Exception ignore) {
            // 类名非法或类不存在，按转换失败处理
            return null;
        }
    }
}
