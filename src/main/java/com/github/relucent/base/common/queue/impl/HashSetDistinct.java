package com.github.relucent.base.common.queue.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.relucent.base.common.queue.Distinct;

/**
 * 基于HashSet的去重器(如果元素很多，会比较占用内存)
 */
public class HashSetDistinct<T extends Serializable> implements Distinct<T> {

    private final Set<String> store = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private final DistinctDigester<T> digester;

    public HashSetDistinct(DistinctDigester<T> digester) {
        this.digester = digester;
    }

    @Override
    public boolean add(T element) {
        String digest = getDigest(element);
        return store.add(digest);
    }

    @Override
    public void reomve(T element) {
        String digest = getDigest(element);
        store.remove(digest);
    }

    @Override
    public void clear() {
        store.clear();
    }

    /**
     * 获得元素摘要
     * @param element 元素
     * @return 元素摘要
     */
    protected String getDigest(T element) {
        return digester.apply(element);
    }
}
