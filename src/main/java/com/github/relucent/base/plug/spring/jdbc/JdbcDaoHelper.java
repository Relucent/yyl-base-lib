package com.github.relucent.base.plug.spring.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.github.relucent.base.util.collection.CollectionUtil;
import com.github.relucent.base.util.jdbc.Dialect;
import com.github.relucent.base.util.page.Page;
import com.github.relucent.base.util.page.Pagination;
import com.github.relucent.base.util.page.SimplePage;
import com.github.relucent.base.util.thread.ThreadUtil;

/**
 * 基于JDBC的数据访问层支持工具类.<br>
 * @author _YYL
 */
public class JdbcDaoHelper {

    /**
     * 查询单个记录
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @return 分页查询結果
     */
    public static <T> T queryOne(String sql, Object[] args, RowMapper<T> rowMapper, JdbcTemplate jdbcTemplate) {
        return CollectionUtil.getFirst(jdbcTemplate.query(sql, args, rowMapper));
    }

    /**
     * 分页查询
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param offset 第一条记录索引
     * @param limit 每页显示记录数
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @return 分页查询結果
     */
    public static <T> Page<T> pagedQuery(String sql, Object[] args, long offset, long limit, RowMapper<T> rowMapper, JdbcTemplate jdbcTemplate,
            Dialect dialect) {
        long count = queryCount(sql, args, jdbcTemplate, dialect);
        if (count == 0L) {
            return new SimplePage<T>(offset, limit, new ArrayList<T>(), 0);
        }
        List<T> records = queryLimit(sql, args, offset, limit, rowMapper, jdbcTemplate, dialect);
        return new SimplePage<T>(offset, limit, records, count);
    }

    /**
     * 分页查询
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param pagination 分页条件
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @return 分页查询結果
     */
    public static <T> Page<T> pagedQuery(CharSequence sql, List<Object> args, Pagination pagination, RowMapper<T> rowMapper,
            JdbcTemplate jdbcTemplate, Dialect dialect) {
        return pagedQuery(sql.toString(), args.toArray(), pagination.getOffset(), pagination.getLimit(), rowMapper, jdbcTemplate, dialect);
    }

    /**
     * 分页查询
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param offset 第一条记录索引
     * @param limit 每页显示记录数
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @param parallel 是否采用并行查询模式
     * @return 分页查询結果
     */
    public static <T> Page<T> pagedQuery(final String sql, final Object[] args, final long offset, final long limit, final RowMapper<T> rowMapper,
            final JdbcTemplate jdbcTemplate, final Dialect dialect, final boolean parallel) {
        if (!parallel) {
            return pagedQuery(sql, args, offset, limit, rowMapper, jdbcTemplate, dialect);
        }
        final AtomicLong countReference = new AtomicLong(0);
        final AtomicReference<List<T>> recordsReference = new AtomicReference<>();
        ThreadUtil.startAndJoinDaemon(new Runnable() {
            @Override
            public void run() {
                countReference.set(queryCount(sql, args, jdbcTemplate, dialect));
            }
        }, new Runnable() {
            @Override
            public void run() {
                recordsReference.set(queryLimit(sql, args, offset, limit, rowMapper, jdbcTemplate, dialect));
            }
        });
        return new SimplePage<T>(offset, limit, recordsReference.get(), countReference.get());
    }

    /**
     * 分页查询
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param pagination 分页条件
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @param parallel 是否采用并行查询模式
     * @return 分页查询結果
     */
    public static <T> Page<T> pagedQuery(CharSequence sql, List<Object> args, Pagination pagination, RowMapper<T> rowMapper,
            JdbcTemplate jdbcTemplate, Dialect dialect, boolean parallel) {
        return pagedQuery(sql.toString(), args.toArray(), pagination.getOffset(), pagination.getLimit(), rowMapper, jdbcTemplate, dialect, parallel);
    }

    /**
     * 查询记录总数
     * @param sql 查询语句
     * @param args 查询参数
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @return 记录总数
     */
    private static long queryCount(String sql, Object[] args, JdbcTemplate jdbcTemplate, Dialect dialect) {
        return jdbcTemplate.queryForObject(dialect.getCountSql(sql), args, Long.class).longValue();
    }

    /**
     * 分页查询
     * @param <T> 查询的结果泛型
     * @param sql 查询语句
     * @param args 查询参数
     * @param offset 第一条记录索引
     * @param limit 每页显示记录数
     * @param rowMapper 行映射
     * @param jdbcTemplate JDBC模板
     * @param dialect 数据方言
     * @return 记录列表
     */
    private static <T> List<T> queryLimit(String sql, Object[] args, long offset, long limit, RowMapper<T> rowMapper, JdbcTemplate jdbcTemplate,
            Dialect dialect) {
        return jdbcTemplate.query(dialect.getLimitSql(sql, offset, limit), args, rowMapper);
    }
}
