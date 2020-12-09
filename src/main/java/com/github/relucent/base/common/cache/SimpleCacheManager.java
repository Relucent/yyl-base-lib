package com.github.relucent.base.common.cache;

/**
 * 缓存管理器接口类
 */
public interface SimpleCacheManager {

    /**
     * 获得对象缓存实例
     * 
     * <pre>
     * // 获得一个user缓存 
     * String cacheName = "custom";
     * Class&gt;CustomBean&gt; elementType = CustomBean.class;
     * Duration ttl = Duration.ofMinutes(30L);
     * Duration maxIdleTime = Duration.ofMinutes(30L);
     * CacheDefinition definition = CacheDefinition.of(cacheName, elementType, ttl, maxIdleTime)
     * SimpleCache&gt;CustomBean&gt; cache = manager.getCache(definition);
     * </pre>
     * 
     * @param <T> 缓存对象类型
     * @param <T> 缓存的对象类型 获取缓存对象
     * @param definition 缓存定义信息
     * @return 对象缓存
     */
    <T> SimpleCache<T> getCache(SimpleCacheDefinition<T> definition);
}
