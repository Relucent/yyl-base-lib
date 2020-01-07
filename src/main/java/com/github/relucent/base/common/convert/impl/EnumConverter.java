package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.Converter;

/**
 * 枚举类型转换器
 * @author YYL
 */
@SuppressWarnings("rawtypes")
public class EnumConverter implements Converter<Enum> {

	public static final EnumConverter INSTANCE = new EnumConverter();

	@SuppressWarnings("unchecked")
	@Override
	public Enum convert(Object source, Class<? extends Enum> toType, Enum vDefault) {
		try {
			if (toType.isEnum()) {
				if (toType.isInstance(source)) {
					return (Enum) source;
				}
				String name = source.toString();
				return Enum.valueOf(toType, name);
			}
		} catch (Exception e) {
			/* Ignore the error */
		}
		return vDefault;
	}

	@Override
	public boolean support(Class<? extends Enum> toType) {
		return Enum.class.isAssignableFrom(toType);
	}
}
