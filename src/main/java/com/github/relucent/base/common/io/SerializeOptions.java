package com.github.relucent.base.common.io;

import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.collection.ConcurrentHashSet;

/**
 * 序列化工具配置类
 */
public class SerializeOptions {

	// =================================Constants==============================================
	/**
	 * 反序列化默认放行的低危类型白名单。<br>
	 * 只允许纯值类型：基础值类型 + {@code java.time} 不可变值类型；<br>
	 * 不含任何已知存在反序列化利用风险的Gadget类：<br>
	 * {@code java.util.PriorityQueue} / {@code java.rmi.*} / {@code java.lang.reflect.*} ；<br>
	 * 也不含 {@code java.util.*} 容器。<br>
	 * 构造时并入 {@link #whiteClassSet}， 调用方仍可用 {@link #accept(Class[])} 追加、用 {@link #clearAllowlist()} 复位为严格空白名单。
	 */
	private static final List<String> DEFAULT_ALLOWLIST = Arrays.asList(//
			"java.lang.String", //
			"java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", //
			"java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", //
			"java.math.BigInteger", "java.math.BigDecimal", //
			"java.util.Date", "java.util.UUID", "java.util.Currency", "java.util.Locale", //
			"java.time.Instant", "java.time.Duration", "java.time.Period", //
			"java.time.LocalDate", "java.time.LocalTime", "java.time.LocalDateTime", //
			"java.time.OffsetTime", "java.time.OffsetDateTime", "java.time.ZonedDateTime", //
			"java.time.Year", "java.time.YearMonth", "java.time.MonthDay", "java.time.ZoneOffset" //
	);

	// =================================Fields=================================================
	/** 白名单 */
	private final Set<String> whiteClassSet = new ConcurrentHashSet<>(DEFAULT_ALLOWLIST);
	/** 黑名单 */
	private final Set<String> blackClassSet = new ConcurrentHashSet<>();

	/**
	 * 是否允许白名单之外的类通过反序列化校验。<br>
	 * 默认 {@code false}（失败即拒绝）。当白名单为空且未开启本开关时，任何类都将被拒绝， 防止“空白名单 = 放行全部”的漏洞。<br>
	 * 显式调用 {@link #setAllowUnlisted(boolean)} 开启后， 退化为宽松模式：仅黑名单生效，白名单之外的类全部放行。<br>
	 */
	private boolean allowUnlisted = false;

	// =================================Methods===============================================
	/**
	 * 接受反序列化的类，用于反序列化验证
	 * @param acceptClasses 接受反序列化的类
	 */
	public void accept(final Class<?>... acceptClasses) {
		for (final Class<?> acceptClass : acceptClasses) {
			this.whiteClassSet.add(acceptClass.getName());
		}
	}

	/**
	 * 禁止反序列化的类，用于反序列化验证
	 * @param refuseClasses 禁止反序列化的类
	 */
	public void refuse(final Class<?>... refuseClasses) {
		for (final Class<?> acceptClass : refuseClasses) {
			this.blackClassSet.add(acceptClass.getName());
		}
	}

	/**
	 * 清空白名单（含默认低危默认项），复位为严格空白名单：此时仅黑名单生效， 其余类（含默认放行类型）一律拒绝。
	 */
	public void clearAllowlist() {
		this.whiteClassSet.clear();
	}

	/**
	 * 设置是否允许白名单之外的类通过反序列化校验。
	 * @param allowUnlisted 为 {@code true} 时，未命中白名单的类只要不命中黑名单即放行（旧版默认行为）； 为 {@code false}（默认）时，仅白名单命中才放行，空白名单将拒绝所有类
	 */
	public void setAllowUnlisted(final boolean allowUnlisted) {
		this.allowUnlisted = allowUnlisted;
	}

	/**
	 * 验证反序列化的类是否合规
	 * @param className 类名
	 * @throws InvalidClassException 如果不合规则抛出该异常
	 */
	void checkClassName(final String className) throws InvalidClassException {

		// 黑名单优先：命中黑名单直接拒绝
		if (CollectionUtil.isNotEmpty(blackClassSet) && blackClassSet.contains(className)) {
			throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
		}

		// 白名单命中直接放行（移除原 java.* 整包放行，避免 java.beans.EventHandler / java.rmi.* 等 gadget 被绕过，P1-2）
		if (CollectionUtil.isNotEmpty(whiteClassSet) && whiteClassSet.contains(className)) {
			return;
		}

		// 未命中白名单：取决于 allowUnlisted 开关（默认 false，失败即拒绝；空白名单不再放行全部，P1-1）
		if (allowUnlisted) {
			return;
		}

		throw new InvalidClassException("Unauthorized deserialization attempt", className);
	}
}
