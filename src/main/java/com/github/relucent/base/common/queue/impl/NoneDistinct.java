package com.github.relucent.base.common.queue.impl;

import com.github.relucent.base.common.queue.Distinct;

/**
 * 去重器的默认实现(不去重)
 */
public class NoneDistinct<T> implements Distinct<T> {

    @SuppressWarnings("rawtypes")
    private static final NoneDistinct INSTANCE = new NoneDistinct();


    @SuppressWarnings("unchecked")
    public static <T> NoneDistinct<T> instance() {
        return INSTANCE;
    }

    @Override
    public boolean add(T element) {
        return true;
    }

    @Override
    public void reomve(T element) {
        // ignore
    }

    @Override
    public void clear() {
        // ignore
    }
}
