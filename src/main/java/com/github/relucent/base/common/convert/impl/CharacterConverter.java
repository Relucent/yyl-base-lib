package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.convert.Converter;

/**
 * 字符类型转换器
 * @author YYL
 * @version 2012-12-11
 * @see Converter
 */
public class CharacterConverter implements BasicConverter<Character> {

    public static final CharacterConverter INSTANCE = new CharacterConverter();

    public Character convertInternal(Object source, Class<? extends Character> toType) {

        if (source == null) {
            return null;
        }
        if (source instanceof Character) {
            return (Character) source;
        }
        if (source instanceof Boolean) {
            return Character.valueOf((char) (((Boolean) source).booleanValue() ? 1 : 0));
        }
        String string = source.toString();
        if (!string.isEmpty()) {
            return string.charAt(0);
        }
        return null;
    }
}
