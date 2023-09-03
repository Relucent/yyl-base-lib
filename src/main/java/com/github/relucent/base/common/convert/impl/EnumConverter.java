package com.github.relucent.base.common.convert.impl;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.EnumUtil;
import com.github.relucent.base.common.lang.NumberUtil;

/**
 * 枚举类型转换器
 * @author YYL
 */
@SuppressWarnings("rawtypes")
public class EnumConverter implements BasicConverter<Enum> {

    public static final EnumConverter INSTANCE = new EnumConverter();

    // 枚举成员数不能超过65536，所以枚举的编号不会超过5位（当然正常情况不会创建那么多成员的枚举）
    private static int ORDINAL_STRING_LENGTH = 5;

    @SuppressWarnings("unchecked")
    @Override
    public Enum convertInternal(Object source, Class<? extends Enum> toType) {

        // 源对象为空，目标类型不是枚举
        if (source == null || toType == null || !toType.isEnum()) {
            return null;
        }

        // 源对象就是目标类型
        if (toType.isInstance(source)) {
            return (Enum) source;
        }

        // 数值类型
        if (source instanceof Number) {
            int ordinal = ((Number) source).intValue();
            return EnumUtil.getEnumAt(toType, ordinal);
        }

        // 字符串类型
        if (source instanceof CharSequence) {
            String name = source.toString();
            try {
                return Enum.valueOf(toType, name);
            } catch (Exception ignore) {
                // 可能是一个字符串类型的数字
                if (NumberUtil.isDigits(name) && name.length() <= ORDINAL_STRING_LENGTH) {
                    return EnumUtil.getEnumAt(toType, Integer.parseInt(name));
                }
            }
        }

        // 无法转换，返回null
        return null;
    }
}
