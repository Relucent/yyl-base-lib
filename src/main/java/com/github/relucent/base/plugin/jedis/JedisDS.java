package com.github.relucent.base.plugin.jedis;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.plugin.redis.RedisConstant;
import com.github.relucent.base.plugin.redis.RedisInfoEntry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.SetParams;

/**
 * Redis 数据源 (基于Jedis封装)
 * @author YYL
 */
public class JedisDS implements Closeable {

    // ==============================StaticFields====================================
    private static final Logger LOG = Logger.getLogger(JedisDS.class);

    // ==============================StaticMethods====================================
    /**
     * 获得默认的连接池配置
     * @return 连接池配置
     */
    public static JedisPoolConfig getDefaultPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 主要的配置项目
        config.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL); // 最大连接，默认8
        config.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE); // 允许最大空闲连接数，默认8
        config.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE); // 保证的最小空闲连接，默认0
        config.setBlockWhenExhausted(true);// 当资源池用尽后，调用者是否要等待
        config.setMaxWait(GenericObjectPoolConfig.DEFAULT_MAX_WAIT);// 当资源池连接用尽后，调用者的最大等待时间（单位为毫秒），默认-1（表示永不超时）
        // 有效性检测
        config.setTestOnBorrow(true);// 向资源池借用连接时是否做连接有效性检测（ping），检测到的无效连接将会被移除；默认false
        config.setTestOnReturn(true);// 向资源池归还连接时是否做连接有效性检测（ping），检测到无效连接将会被移除
        config.setJmxEnabled(true);// 是否开启JMX监控
        // 空闲对象检测
        config.setTestWhileIdle(true); // 是否开启空闲资源检测，默认false
        config.setMinEvictableIdleTime(Duration.ofMillis(60000));// 资源池中资源的最小空闲时间（单位为毫秒），达到此值后空闲资源将被移除；默认30分钟
        config.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));// 空闲资源的检测周期（单位为毫秒），考虑优化设置为30秒
        config.setNumTestsPerEvictionRun(-1);// 做空闲资源检测时，每次检测资源的个数，默认3；如果设置为 -1，就是对所有连接做空闲监测。
        return config;
    }

    /**
     * 构造数据源
     * @return _Redis 数据源
     */
    public static Builder builder() {
        return new Builder();
    }

    // ==============================Fields===========================================
    /** _Jedis 连接池 */
    private final JedisPool pool;
    /** 发布订阅存储表 */
    private final JedisPubSubs pubSubs;
    /** 分布式锁存储表 */
    private final Map<String, JedisDistributedLock> distributedLockMap = new ConcurrentHashMap<>();

    // ==============================Constructors=====================================
    protected JedisDS(Builder builder) {
        pool = new JedisPool(
                // 连接池配置
                builder.poolConfig,
                // 地址
                builder.host,
                // 端口
                builder.port,
                // 连接超时时间
                builder.connectionTimeout,
                // 读取数据超时时间
                builder.soTimeout,
                // 密码
                builder.password,
                // 数据库序号
                builder.database,
                // 客户端名称
                builder.clientName,
                // 是否使用SSL
                builder.ssl,
                // SSL Socket 工厂
                builder.sslSocketFactory,
                // SSL 参数
                builder.sslParameters,
                // 主机名验证程序
                builder.hostnameVerifier);
        this.pubSubs = new JedisPubSubs(pool);
    }

    // ==============================Methods==========================================
    /**
     * 获取{@link Jedis}
     * @return {@link Jedis}
     */
    public Jedis getJedis() {
        return this.pool.getResource();
    }

    /**
     * 向 Redis服务器发送一个 PING，如果服务器运作正常的话，会返回一个 PONG。<br>
     * 通常用于测试与服务器的连接是否仍然生效，或者用于测量延迟值。
     * @return <code>PONG</code>
     */
    public String ping() {
        try (Jedis jedis = getJedis()) {
            return jedis.ping();
        }
    }

    /**
     * 设置指定键的值
     * @param key 键
     * @param value 值
     */
    public void setString(String key, String value) {
        AssertUtil.notNull(key, "non null key required");
        try (Jedis jedis = getJedis()) {
            jedis.set(key, value);
        }
    }

    /**
     * 设置指定键的值
     * @param key 键
     * @param value 值
     * @param expire 有效时间
     */
    public void setString(String key, String value, Duration expire) {
        AssertUtil.notNull(key, "non null key required");
        try (Jedis jedis = getJedis()) {
            jedis.set(key, value, SetParams.setParams().px(expire.toMillis()));
        }
    }

    /**
     * 获取给定键的值
     * @param key 键
     * @return 值
     */
    public String getString(String key) {
        AssertUtil.notNull(key, "non null key required");
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        }
    }

    /**
     * 执行 Redis 数据访问操作
     * @param <T> 返回的结果对象类型
     * @param action 指定操作的回调对象
     * @return 操作返回的结果对象，或{@code null}
     */
    public <T> T execute(Function<Jedis, T> action) {
        T result = null;
        try (Jedis jedis = getJedis()) {
            result = action.apply(jedis);
        }
        return result;
    }

    /**
     * 从 Redis 中删除多个值
     * @param keys 需要删除值对应的键列表
     * @return 删除个数，0表示无key可删除
     */
    public Long del(String... keys) {
        if (ArrayUtil.isEmpty(keys)) {
            return 0L;
        }
        try (Jedis jedis = getJedis()) {
            return jedis.del(keys);
        }
    }

    /**
     * 获得发布订阅操作类
     * @return 发布订阅操作类
     */
    public JedisPubSubs getPubSubs() {
        return pubSubs;
    }

    /**
     * 获得分布式锁
     * @param name 锁名称
     * @return 分布式锁
     */
    public Lock getLock(String name) {
        return distributedLockMap.computeIfAbsent(name, k -> new JedisDistributedLock(pool, pubSubs, name));
    }

    /**
     * 获得 _Redis详细信息
     * @return _Redis 详细条目列表
     */
    public List<RedisInfoEntry> getRedisInfo() {
        try (Jedis jedis = getJedis()) {
            Properties properties = new Properties();
            try (Reader reader = new StringReader(jedis.info())) {
                properties.load(reader);
            } catch (IOException e) {
                LOG.error("redis.info()", e);
            }
            List<RedisInfoEntry> info = new ArrayList<>();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = ConvertUtil.toString(entry.getKey(), StringConstant.EMPTY);
                String value = ConvertUtil.toString(entry.getValue(), StringConstant.EMPTY);
                String description = RedisConstant.getInfoDescription(name);

                RedisInfoEntry infoEntry = new RedisInfoEntry();
                infoEntry.setName(name);
                infoEntry.setValue(value);
                infoEntry.setDescription(description);
                info.add(infoEntry);
            }
            return info;
        }
    }

    /**
     * 关闭数据源
     */
    @Override
    public void close() {
        IoUtil.closeQuietly(this.pool);
    }

    // ==============================Builder==========================================
    /** 构建器 */
    public static class Builder {
        /** 连接池配置 */
        private JedisPoolConfig poolConfig = getDefaultPoolConfig();
        /** 地址 */
        private String host = Protocol.DEFAULT_HOST;
        /** 端口 */
        private int port = Protocol.DEFAULT_PORT;
        /** 连接超时时间(毫秒) */
        private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
        /** 读取数据超时时间(毫秒) */
        private int soTimeout = Protocol.DEFAULT_TIMEOUT;
        /** 密码 */
        private String password;
        /** 数据库序号 */
        private int database = Protocol.DEFAULT_DATABASE;
        /** 客户端名称 */
        private String clientName;
        /** 是否使用SSL */
        private boolean ssl = false;
        /** SslSocket 工厂 */
        private SSLSocketFactory sslSocketFactory;
        /** SSL 参数 */
        private SSLParameters sslParameters;
        /** 主机名验证程序 */
        private HostnameVerifier hostnameVerifier;

        /**
         * 设置连接池
         * @param poolConfig 连接池配置项
         * @return 该对象的引用
         */
        public Builder setPoolConfig(JedisPoolConfig poolConfig) {
            this.poolConfig = poolConfig;
            return this;
        }

        /**
         * 设置地址
         * @param host 地址
         * @return 该对象的引用
         */
        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        /**
         * 设置端口
         * @param port 端口
         * @return 该对象的引用
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /**
         * 设置连接超时时间(毫秒)
         * @param connectionTimeout 连接超时时间
         * @return 该对象的引用
         */
        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * 设置读取数据超时时间
         * @param soTimeout 读取数据超时时间(毫秒)
         * @return 该对象的引用
         */
        public Builder setSoTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
            return this;
        }

        /**
         * 设置密码
         * @param password 密码
         * @return 该对象的引用
         */
        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        /**
         * 设置数据库序号
         * @param database 数据库序号
         * @return 该对象的引用
         */
        public Builder setDatabase(int database) {
            this.database = database;
            return this;
        }

        /**
         * 设置客户端名称
         * @param clientName 客户端名称
         * @return 该对象的引用
         */
        public Builder setClientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        /**
         * 设置是否使用SSL
         * @param ssl 是否使用SSL
         * @return 该对象的引用
         */
        public Builder setSsl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        /**
         * 设置 SslSocket 工厂
         * @param sslSocketFactory SslSocket 工厂
         * @return 该对象的引用
         */
        public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 设置 SSL 参数
         * @param sslParameters SSL 参数
         * @return 该对象的引用
         */
        public Builder setSslParameters(SSLParameters sslParameters) {
            this.sslParameters = sslParameters;
            return this;
        }

        /**
         * 设置主机名验证程序
         * @param hostnameVerifier 主机名验证程序
         * @return 该对象的引用
         */
        public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 创建 {@code JedisDS}
         * @return {@code JedisDS} 对象实例
         */
        public JedisDS build() {
            return new JedisDS(this);
        }
    }
}
