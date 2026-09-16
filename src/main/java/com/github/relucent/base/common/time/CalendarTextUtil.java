package com.github.relucent.base.common.time;

/**
 * 日期文本工具类，目前只考虑中文显示(CN)
 */
public class CalendarTextUtil {

	// =================================Fields=================================================
	private static final String YEAR_CN_TEXTS = "年";

	private static final String[] HALFY_EAR_CN_TEXTS = { "上半年", "下半年"//
	};
	private static final String[] MONTH_CN_TEXTS = { //
			"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" //
	};//
	private static final String[] QUARTER_CN_TEXTS = { //
			"", "一季度", "二季度", "三季度", "四季度" //
	};//
	private static final String[] DATE_CN_TEXTS = { null, //
			"1日", "2日", "3日", "4日", "5日", "6日", "7日", "8日", "9日", "10日", //
			"11日", "12日", "13日", "14日", "15日", "16日", "17日", "18日", "19日", "20日", //
			"21日", "22日", "23日", "24日", "25日", "26日", "27日", "28日", "29日", "30日", //
			"31日" };//

	// =================================Constructors===========================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected CalendarTextUtil() {
	}

	// =================================Methods===============================================
	/**
	 * 显示日期的文本字符串<br>
	 * 取值约定与{@link CalendarUtil#getFieldValue(java.util.Calendar, DateUnit)}保持一致：
	 * <ul>
	 * <li>{@link DateUnit#YEAR} 年份（如 2026）</li>
	 * <li>{@link DateUnit#HALFYEAR} 0(上半年) 或 1(下半年)</li>
	 * <li>{@link DateUnit#QUARTER} 季度 1-4</li>
	 * <li>{@link DateUnit#MONTH} 月份 0-11</li>
	 * <li>{@link DateUnit#DATE} 日期 1-31</li>
	 * </ul>
	 * 取值越界或单位不支持时返回{@code null}（{@link DateUnit#WEEK} 周几没有统一的中文文本约定，不支持）
	 * @param unit  日历字段枚举
	 * @param value 日历字段的值
	 * @return 日期的文本字符串
	 */
	public static String getText(DateUnit unit, int value) {
		switch (unit) {
		case YEAR: // 年
			return value + YEAR_CN_TEXTS;
		case HALFYEAR:// 半年
			return (value == 0 || value == 1) ? HALFY_EAR_CN_TEXTS[value] : null;
		case QUARTER:// 季度(1-4)
			return (1 <= value && value <= 4) ? QUARTER_CN_TEXTS[value] : null;
		case MONTH:// 月份(0-11)
			return (0 <= value && value <= 11) ? MONTH_CN_TEXTS[value] : null;
		case DATE:// 日期(1-31)
			return (1 <= value && value <= 31) ? DATE_CN_TEXTS[value] : null;
		default:
			return null;
		}
	}
}
