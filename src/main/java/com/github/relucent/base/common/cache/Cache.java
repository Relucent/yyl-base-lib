package com.github.relucent.base.common.cache;

/**
 * 缓存接口类
 * @param <T> 缓存对象类型
 */
public interface Cache<T> {

    /**
     * 读取缓存的对象
     * @param key 缓存对象的KEY
     * @return 缓存的对象
     */
    T get(String key);

    /**
     * 设置缓存的对象
     * @param key 缓存对象的KEY
     * @param value 缓存的对象
     */
    void put(String key, T value);

    /**
     * 删除缓存的对象
     * @param key 缓存对象的KEY
     */
    void remove(String key);

    /**
     * 清空缓存
     */
    void clear();
}
