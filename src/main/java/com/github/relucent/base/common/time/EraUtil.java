package com.github.relucent.base.common.time;

import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseEra;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Locale;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * {@link Era} 纪元工具类<br>
 * 本类全部方法均为宽容语义：参数为 {@code null} 或无法确定结果时返回 {@code null}（或默认值），不抛出异常。<br>
 * 纪元的中文名称取自 JDK 的语言数据（{@link Era#getDisplayName(TextStyle, Locale)}），常见取值：<br>
 * {@code IsoEra.BCE}=公元前、{@code IsoEra.CE}=公元、{@code MinguoEra.BEFORE_ROC}=民国前、
 * {@code MinguoEra.ROC}=民国、{@code JapaneseEra.HEISEI}=平成、{@code HijrahEra.AH}=伊斯兰历。
 * 
 * <pre>{@code
 * Era era = EraUtil.from(LocalDate.of(-100, 1, 1)); // IsoEra.BCE
 * boolean bce = EraUtil.isBce(era); // true
 * String name = EraUtil.getName(era); // 公元前
 * int value = EraUtil.getValue(era, -1); // 0
 * Era ce = EraUtil.of(IsoChronology.INSTANCE, 1); // IsoEra.CE
 * }</pre>
 * 
 * @author YYL
 */
public class EraUtil {

	// =================================Constructors===========================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected EraUtil() {
	}

	// =================================Methods================================================
	/**
	 * 判断纪元是否为公元前<br>
	 * 判断依据是纪元在其历法中的数值：ISO 历法的 {@code BCE}、民国历的 {@code BEFORE_ROC}、佛历的 {@code BEFORE_BE} 数值均不大于 0，视为公元前。<br>
	 * 日本年号（明治、大正、昭和、平成、令和）是纪年序列而不是公元前后的划分，一律返回 {@code false}。
	 * @param era 纪元对象，可以为 {@code null}
	 * @return 如果是公元前返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isBce(final Era era) {
		if (era == null || era instanceof JapaneseEra) {
			return false;
		}
		return era.getValue() <= 0;
	}

	/**
	 * 判断纪元是否为公元后（即不属于公元前纪元）<br>
	 * 本方法是 {@link #isBce(Era)} 的取反，因此 ISO 历法的 {@code CE}、民国历的 {@code ROC}、佛历的 {@code BE}、 伊斯兰历的 {@code AH} 以及日本年号均返回
	 * {@code true}。
	 * @param era 纪元对象，可以为 {@code null}
	 * @return 如果不是公元前返回 {@code true}，否则返回 {@code false}
	 */
	public static boolean isCe(final Era era) {
		return era != null && !isBce(era);
	}

	/**
	 * 根据历法和纪元数值获得纪元对象<br>
	 * 宽容语义：无法确定时返回 {@code null}
	 * @param chronology 历法，可以为 {@code null}
	 * @param eraValue   纪元数值
	 * @return 纪元对象，历法为 {@code null} 或该历法不存在此数值的纪元时返回 {@code null}
	 */
	public static Era of(final Chronology chronology, final int eraValue) {
		if (chronology == null) {
			return null;
		}
		try {
			return chronology.eraOf(eraValue);
		} catch (RuntimeException ignore) {
			// 该历法不存在对应数值的纪元，例如 ISO 历法的纪元数值只有 0（BCE）和 1（CE）
			return null;
		}
	}

	/**
	 * 根据历法和纪元数值获得纪元对象<br>
	 * 宽容语义：无法确定时返回默认值
	 * @param chronology   历法，可以为 {@code null}
	 * @param eraValue     纪元数值
	 * @param defaultValue 默认值
	 * @return 纪元对象，无法确定时返回默认值
	 */
	public static Era of(final Chronology chronology, final int eraValue, final Era defaultValue) {
		return ObjectUtil.defaultIfNull(of(chronology, eraValue), defaultValue);
	}

	/**
	 * 安全获取纪元的数值
	 * @param era 纪元对象，可以为 {@code null}
	 * @return 纪元的数值（ISO 历法的 {@code BCE} 为 0、{@code CE} 为 1），参数为 {@code null} 时返回 {@code null}
	 */
	public static Integer getValue(final Era era) {
		return era == null ? null : Integer.valueOf(era.getValue());
	}

	/**
	 * 安全获取纪元的数值
	 * @param era          纪元对象，可以为 {@code null}
	 * @param defaultValue 默认值
	 * @return 纪元的数值，参数为 {@code null} 时返回默认值
	 */
	public static int getValue(final Era era, final int defaultValue) {
		return era == null ? defaultValue : era.getValue();
	}

	/**
	 * 获取纪元的中文名称<br>
	 * 等价于 {@code getName(era, Locale.CHINA)}
	 * @param era 纪元对象，可以为 {@code null}
	 * @return 纪元的中文名称（如"公元前"、"公元"、"民国"、"平成"），参数为 {@code null} 时返回 {@code null}
	 * @see #getName(Era, Locale)
	 */
	public static String getName(final Era era) {
		return getName(era, Locale.CHINA);
	}

	/**
	 * 获取纪元的显示名称，名称内容取自 JDK 的语言数据
	 * @param era    纪元对象，可以为 {@code null}
	 * @param locale 语言环境，为 {@code null} 时使用 {@link Locale#CHINA}
	 * @return 纪元的显示名称，参数为 {@code null} 时返回 {@code null}
	 */
	public static String getName(final Era era, final Locale locale) {
		if (era == null) {
			return null;
		}
		Locale targetLocale = ObjectUtil.defaultIfNull(locale, Locale.CHINA);
		try {
			return era.getDisplayName(TextStyle.FULL, targetLocale);
		} catch (RuntimeException ignore) {
			// 语言数据缺失时退化为枚举名称
			return era.toString();
		}
	}

	/**
	 * 从时间对象中提取纪元<br>
	 * 宽容语义：时间对象为 {@code null} 或该对象不包含纪元字段（如 {@code LocalTime}、{@code Instant}）时返回 {@code null}
	 * @param temporal 时间对象，可以为 {@code null}
	 * @return 纪元对象，无法提取时返回 {@code null}
	 */
	public static Era from(final TemporalAccessor temporal) {
		if (temporal == null || !temporal.isSupported(ChronoField.ERA)) {
			return null;
		}
		// 时间对象自身携带历法时使用其历法（如 JapaneseDate、MinguoDate），否则按 ISO 历法处理
		Chronology chronology = temporal.query(TemporalQueries.chronology());
		return of(chronology == null ? IsoChronology.INSTANCE : chronology, temporal.get(ChronoField.ERA));
	}

	/**
	 * 从时间对象中提取纪元<br>
	 * 宽容语义：无法提取时返回默认值
	 * @param temporal     时间对象，可以为 {@code null}
	 * @param defaultValue 默认值
	 * @return 纪元对象，无法提取时返回默认值
	 */
	public static Era from(final TemporalAccessor temporal, final Era defaultValue) {
		return ObjectUtil.defaultIfNull(from(temporal), defaultValue);
	}
}
