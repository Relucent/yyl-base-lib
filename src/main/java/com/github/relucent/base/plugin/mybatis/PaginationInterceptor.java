package com.github.relucent.base.plugin.mybatis;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.relucent.base.common.jdbc.DelegatingDialect;
import com.github.relucent.base.common.jdbc.Dialect;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.page.Pagination;

/**
 * 用于MyBatis的分页查询插件.<br>
 * 用于提供数据库的物理分页查询功能.<br>
 * @see org.apache.ibatis.plugin.Interceptor
 * @author _yyl
 */
@SuppressWarnings({ "unchecked" })
@Intercepts({ //
		@Signature(type = Executor.class, method = "query", args = { //
				MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { //
				MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, //
				CacheKey.class, BoundSql.class })//
})
public class PaginationInterceptor implements Interceptor {

	// ==============================Fields===========================================
	private static final String COUNT_SUFFIX = "_COUNT";
	private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);
	private static final Field ADDITIONAL_PARAMETERS_FIELD;
	static {
		try {
			ADDITIONAL_PARAMETERS_FIELD = BoundSql.class.getDeclaredField("additionalParameters");
			ADDITIONAL_PARAMETERS_FIELD.setAccessible(true);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
	protected final Logger logger = Logger.getLogger(getClass());
	private final Map<String, MappedStatement> countMsCache = new ConcurrentHashMap<>();
	private final DelegatingDialect dialect = new DelegatingDialect();

	// ==============================Methods==========================================
	/**
	 * 插件拦截方法
	 * @param ivk MyBatis调用
	 * @return 查询结果
	 */
	@Override
	public Object intercept(Invocation ivk) throws Throwable {
		try {
			Executor executor = (Executor) ivk.getTarget();
			Object[] args = ivk.getArgs();

			MappedStatement ms = (MappedStatement) args[0];
			Object parameter = args[1];
			RowBounds rowBounds = (RowBounds) args[2];
			ResultHandler<?> resultHandler = (ResultHandler<?>) args[3];

			CacheKey cacheKey;
			BoundSql boundSql;

			if (args.length == 4) {
				boundSql = ms.getBoundSql(parameter);
				cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
			}
			// args.length == 6
			else {
				cacheKey = (CacheKey) args[4];
				boundSql = (BoundSql) args[5];
			}

			// 数据库方言切换
			dialect.route(executor.getTransaction().getConnection());

			// 获得分页当前条件
			Pagination pagination = MybatisHelper.getCurrentPagination();

			// 判断是否需要进行分页(是否插件分页)
			if (pagination != null) {
				// 查询总数
				long total = obtainTotalCount(dialect, executor, ms, parameter, resultHandler, boundSql);

				// 暂存总数
				MybatisHelper.setTotalCount(total);

				// 当查询总数为 0 时，直接返回空的结果
				if (total == 0) {
					return new ArrayList<>();
				}

				// 根据分页条件对象创建分页对象
				rowBounds = new RowBounds((int) pagination.getOffset(), (int) pagination.getLimit());
			}

			// 判断是否需要进行分页查询
			int offset = rowBounds.getOffset();
			int limit = rowBounds.getLimit();
			if (offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT) {
				// 根据方言获得分页SQL
				String pagedSql = dialect.getLimitSql(boundSql.getSql(), offset, limit);
				BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pagedSql, boundSql.getParameterMappings(), parameter);

				// 添加动态SQL，可能会产生的临时参数
				for (Map.Entry<String, Object> entry : getAdditionalParameter(boundSql).entrySet()) {
					pageBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
				}

				// 执行分页查询
				return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
			}

			// 无分页的情况
			return ivk.proceed();
		} finally {
			dialect.release();
		}
	}

	/**
	 * 用于生成代理类
	 * @param target 要拦截的对象
	 * @return 包装类
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 获得配置的参数
	 * @param properties 配置的参数
	 */
	@Override
	public void setProperties(Properties properties) {
		// _ignore_
	}

	// ==============================ProcessMethods===================================
	/**
	 * 进行 COUNT 查询
	 * @param dialect 数据库方言
	 * @param executor 执行器
	 * @param ms 映射语句处理对象
	 * @param parameter 参数对象
	 * @param resultHandler 结果处理对象
	 * @param boundSql 绑定SQL对象
	 * @return 总记录数
	 */
	private Long obtainTotalCount(Dialect dialect, Executor executor, MappedStatement ms, Object parameter, ResultHandler<?> resultHandler,
			BoundSql boundSql) throws SQLException, IllegalArgumentException, IllegalAccessException {
		String countMsId = ms.getId() + COUNT_SUFFIX;

		// 判断是否存在手写的 count 查询
		MappedStatement countMs = getMappedStatement(ms.getConfiguration(), countMsId);
		if (countMs != null) {
			return executeManualCount(executor, countMs, parameter, boundSql, resultHandler);
		}

		countMs = countMsCache.get(countMsId);
		// 自动创建
		if (countMs == null) {
			// 根据当前的 MS 创建一个 COUNT MS
			countMs = createCountMappedStatement(ms, countMsId);
			// 缓存 COUNT MS
			countMsCache.put(countMsId, countMs);
		}

		return executeAutoCount(dialect, executor, countMs, parameter, boundSql, resultHandler);
	}

	/**
	 * 尝试获取已经存在的在映射语句处理对象
	 * @param configuration 配置对象
	 * @param msId 映射语句ID
	 * @return 映射语句处理对象
	 */
	private static MappedStatement getMappedStatement(Configuration configuration, String msId) {
		MappedStatement ms = null;
		try {
			ms = configuration.getMappedStatement(msId, false);
		} catch (Throwable t) {
			// ignore
		}
		return ms;
	}

	/** 执行手动设置的 count 查询 */
	private static Long executeManualCount(Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
			ResultHandler<?> resultHandler) throws SQLException {
		CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);
		BoundSql countBoundSql = countMs.getBoundSql(parameter);
		return ((Number) executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql).get(0)).longValue();
	}

	/** 执行自动生成的 count 查询 */
	private static Long executeAutoCount(Dialect dialect, Executor executor, MappedStatement countMs, Object parameter, BoundSql boundSql,
			ResultHandler<?> resultHandler) throws SQLException, IllegalArgumentException, IllegalAccessException {

		// 创建 count 查询的缓存 key
		CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, boundSql);

		// 调用方言获取 COUNT SQL
		String countSql = dialect.getCountSql(boundSql.getSql());

		// 创建 Count BoundSql
		BoundSql countBoundSql = new BoundSql(countMs.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);

		// 添加动态SQL，可能会产生的临时参数
		for (Map.Entry<String, Object> entry : getAdditionalParameter(boundSql).entrySet()) {
			countBoundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
		}

		// 执行 count 查询
		return ((Number) executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql).get(0)).longValue();
	}

	/**
	 * 创建 COUNT映射语句处理对象
	 * @param ms
	 * @param newMsId
	 * @return COUNT映射语句处理对象
	 */
	private static MappedStatement createCountMappedStatement(MappedStatement ms, String newMsId) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), newMsId, ms.getSqlSource(), ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), Long.class, EMPTY_RESULTMAPPING).build();
		resultMaps.add(resultMap);
		builder.resultMaps(resultMaps);
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		return builder.build();
	}

	/**
	 * 获取 BoundSql 属性值 additionalParameters
	 * @param boundSql BoundSql
	 * @return additionalParameters属性
	 */
	private static Map<String, Object> getAdditionalParameter(BoundSql boundSql) throws IllegalAccessException {
		return (Map<String, Object>) ADDITIONAL_PARAMETERS_FIELD.get(boundSql);
	}
}
