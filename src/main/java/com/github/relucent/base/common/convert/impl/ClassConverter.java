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

    public Class<?> convertInternal(Object source, Class<? extends Class<?>> toType) {
        return ClassLoaderUtil.loadClass(StringUtil.string(source), true);
    }
}
