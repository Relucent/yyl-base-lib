package com.github.relucent.base.common.convert.impl;

import java.util.Map;

import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.convert.BasicConverter;

/**
 * MAP映射表类型转换器
 * @author YYL
 */
public class MapxConverter implements BasicConverter<Mapx> {

    public static final MapxConverter INSTANCE = new MapxConverter();

    @SuppressWarnings("rawtypes")
    @Override
    public Mapx convertInternal(Object source, Class<? extends Mapx> toType) {
        try {
            if (source instanceof Map) {
                Mapx result = new Mapx();
                for (Object o : (((Map) source).entrySet())) {
                    Map.Entry entry = (Map.Entry) o;
                    result.put(String.valueOf(entry.getKey()), entry.getValue());
                }
                return result;
            }
        } catch (Exception ignore) {
            // Ignore//
        }
        return null;
    }
}
