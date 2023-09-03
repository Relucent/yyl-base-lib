package com.github.relucent.base.common.convert.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.convert.BasicConverter;

/**
 * LIST集合类型转换器
 * @author YYL
 */
public class ListxConverter implements BasicConverter<Listx> {

    public static final ListxConverter INSTANCE = new ListxConverter();

    @Override
    public Listx convertInternal(Object source, Class<? extends Listx> toType) {
        try {
            if (source instanceof Collection) {
                Listx result = new Listx();
                result.addAll((Collection<?>) source);
                return result;
            } else if (source instanceof Object[]) {
                Listx result = new Listx();
                Collections.addAll(result, (Object[]) source);
                return result;
            } else if (source instanceof Iterable) {
                Listx result = new Listx();
                for (@SuppressWarnings("rawtypes")
                Iterator elements = (Iterator) source; elements.hasNext(); result.add(elements.next()))
                    ;
                return result;
            }
        } catch (Exception ignore) {
            // Ignore//
        }
        return null;
    }
}
