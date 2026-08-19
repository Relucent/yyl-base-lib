package com.github.relucent.base.common.lang;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 安全取值/设值工具类：判空才设值，取空不报错。 <br>
 * 1. set 系列：null 或空值不覆盖目标字段 <br>
 * 2. get 系列：入参为 null 时返回 null 或调用方指定的默认值 <br>
 */
public final class SafeValueUtil {

	// ==============================Constructors=====================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	private SafeValueUtil() {
	}

	// ==============================Methods==========================================

	// SET 系列：判空才设

	/**
	 * 值非 null 才调用 setter，null 不设置。 <br>
	 * 适用于 Date、Integer、Long 等任意对象类型字段 <br>
	 * @param <T>    字段类型
	 * @param setter 目标字段的 setter 方法引用（如 entity::setApplyDate）
	 * @param value  待设置的值，允许为 null
	 */
	public static <T> void setIfNotNull(Consumer<T> setter, T value) {
		if (value != null) {
			setter.accept(value);
		}
	}

	/**
	 * 字符串非 null 且非空串（""）才调用 setter。 <br>
	 * 注意：仅过滤空串，不去除首尾空白，如需要过滤空白请使用 {@link #setIfNotBlank} <br>
	 * @param setter 目标字段的 setter 方法引用（如 entity::setApplyType）
	 * @param value  待设置的字符串，允许为 null 或空串
	 */
	public static void setIfNotEmpty(Consumer<String> setter, String value) {
		if (value != null && !value.isEmpty()) {
			setter.accept(value);
		}
	}

	/**
	 * 字符串非 null 且去除首尾空白后非空（" "）才调用 setter。 <br>
	 * null、""、" " 均不设置 <br>
	 * @param setter 目标字段的 setter 方法引用（如 entity::setMemo）
	 * @param value  待设置的字符串，允许为 null 或纯空白
	 */
	public static void setIfNotBlank(Consumer<String> setter, String value) {
		if (StringUtil.isNotBlank(value)) {
			setter.accept(value);
		}
	}

	/**
	 * 集合非 null 且非空才调用 setter。 <br>
	 * 空集合（size == 0）不设置，避免覆盖已有值 <br>
	 * @param <T>    集合元素类型
	 * @param setter 目标字段的 setter 方法引用（如 entity::setTagList）
	 * @param value  待设置的集合，允许为 null 或空集合
	 */
	public static <T> void setIfNotEmpty(Consumer<Collection<T>> setter, Collection<T> value) {
		if (value != null && !value.isEmpty()) {
			setter.accept(value);
		}
	}

	/**
	 * Map 非 null 且非空才调用 setter。 <br>
	 * 空 Map（size == 0）不设置，避免覆盖已有值 <br>
	 * @param <K>    Map 键类型
	 * @param <V>    Map 值类型
	 * @param setter 目标字段的 setter 方法引用（如 entity::setExtMap）
	 * @param value  待设置的 Map，允许为 null 或空 Map
	 */
	public static <K, V> void setIfNotEmpty(Consumer<Map<K, V>> setter, Map<K, V> value) {
		if (value != null && !value.isEmpty()) {
			setter.accept(value);
		}
	}

	// GET 系列：null 返回 null

	/**
	 * 入参为 null 返回 null，否则原样返回。 <br>
	 * 语义化包装，用于让调用方显式表达"允许为 null"的取值意图 <br>
	 * @param <T>   值类型
	 * @param value 原始值，允许为 null
	 * @return 原值；value 为 null 时返回 null
	 */
	public static <T> T getOrNull(T value) {
		return value;
	}

	// GET 系列：null 返回默认值

	/**
	 * 通用兜底：value 为 null 时返回 defaultValue，否则返回 value。 <br>
	 * 适用于任意对象类型，如 BigDecimal、Date、实体对象等 <br>
	 * @param <T>          值类型
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回 value，否则返回 defaultValue
	 */
	public static <T> T getOrDefault(T value, T defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 字符串兜底：为 null 或空串（""）时返回 defaultValue，否则返回原值。 <br>
	 * 1. null 返回默认值 <br>
	 * 2. "" 返回默认值 <br>
	 * 3. 纯空白（" "）不拦截，原样返回 <br>
	 * @param value        原始字符串，允许为 null 或空串
	 * @param defaultValue 默认值，value 为 null 或空串时返回
	 * @return 非空字符串原样返回，否则返回 defaultValue
	 */
	public static String getString(String value, String defaultValue) {
		return (value != null && !value.isEmpty()) ? value : defaultValue;
	}

	/**
	 * 字符串兜底（严格版）：为 null、空串或纯空白时返回 defaultValue。 <br>
	 * null、""、" " 均返回默认值 <br>
	 * @param value        原始字符串，允许为 null 或纯空白
	 * @param defaultValue 默认值，value 为 null 或空白时返回
	 * @return 非空白字符串原样返回，否则返回 defaultValue
	 */
	public static String getStringIfNotBlank(String value, String defaultValue) {
		return (StringUtil.isNotBlank(value)) ? value : defaultValue;
	}

	/**
	 * Integer 安全拆箱：null 返回 defaultValue，避免 NPE。 <br>
	 * 用于 Map.get()、JSON 解析等可能产生 null 包装类型的场景 <br>
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回 value.intValue()，否则返回 defaultValue
	 */
	public static int getInt(Integer value, int defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * Long 安全拆箱：null 返回 defaultValue，避免 NPE。 <br>
	 *
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回 value.longValue()，否则返回 defaultValue
	 */
	public static long getLong(Long value, long defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * Double 安全拆箱：null 返回 defaultValue，避免 NPE。 <br>
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回 value.doubleValue()，否则返回 defaultValue
	 */
	public static double getDouble(Double value, double defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * Boolean 安全拆箱：null 返回 defaultValue，避免 NPE。 <br>
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回 value.booleanValue()，否则返回 defaultValue
	 */
	public static boolean getBoolean(Boolean value, boolean defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * BigDecimal 兜底：null 返回 defaultValue，避免后续计算 NPE。 <br>
	 * 常用于金额、费率等数值运算前的空值处理 <br>
	 * @param value        原始值，允许为 null
	 * @param defaultValue 默认值，value 为 null 时返回
	 * @return value 非 null 返回原值，否则返回 defaultValue
	 */
	public static BigDecimal getBigDecimal(BigDecimal value, BigDecimal defaultValue) {
		return value != null ? value : defaultValue;
	}

	/**
	 * 返回参数列表中第一个非 null 的值。 <br>
	 * 1. 按参数顺序从前向后遍历 <br>
	 * 2. 全部为 null 或入参数组为 null 时返回 null <br>
	 * 3. 常用于多级兜底取值，如 getFirstNonNull(缓存值, 数据库值, 默认值) <br>
	 * @param <T>    值类型
	 * @param values 候选值列表，允许为 null 或包含 null 元素
	 * @return 第一个非 null 的值；全部为 null 返回 null
	 */
	@SafeVarargs
	public static <T> T getFirstNonNull(T... values) {
		if (values != null) {
			for (T v : values) {
				if (v != null) {
					return v;
				}
			}
		}
		return null;
	}
}