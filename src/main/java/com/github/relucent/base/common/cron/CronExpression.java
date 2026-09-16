package com.github.relucent.base.common.cron;

/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 */
import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * 提供 Unix 风格 Cron 表达式的解析与求值能力。<br>
 * Cron 表达式可以表示复杂的时间规则， 例如“每周一至周五 8:00 执行”或“每月最后一个周五 1:30 执行”。<br>
 * 
 * Cron 表达式由 6 个必填字段和 1 个可选的年份字段组成，各字段之间使用空白字符分隔， 字段顺序如下：<br>
 * 
 * <pre>
 * 秒       0-59       , - * /
 * 分       0-59       , - * /
 * 时       0-23       , - * /
 * 日       1-31       , - * ? / L W
 * 月       0-11       , - * /
 * 星期     1-7        , - * ? / L #
 * 年       1970-2199  , - * /        （可选）
 * </pre>
 *
 * 月份可以使用 JAN-DEC 表示，星期可以使用 SUN-SAT 表示，名称不区分大小写。<br>
 *
 * <b>特殊字符说明：</b><br>
 * {@code *} 表示该字段的所有取值。例如分钟字段的 {@code *} 表示每分钟执行。<br>
 * {@code ?} 仅允许用于日和星期字段，表示不指定具体值。 当只需要指定日和星期中的一个字段时使用。<br>
 * {@code -} 表示范围。例如 {@code 10-12} 表示 10、11、12 点。<br>
 * {@code ,} 表示多个指定值。例如 {@code MON,WED,FRI} 表示周一、周三和周五。<br>
 * {@code /} 表示步长。例如秒字段的 {@code 0/15} 表示 0、15、30、45 秒， {@code 5/15} 表示 5、20、35、50 秒。在 {@code /} 前使用
 * {@code *}，表示从字段的最小值开始按指定步长取值。<br>
 * {@code L} 表示最后一个值（Last），仅允许用于日和星期字段：<br>
 * 日字段中的 {@code L} 表示当月最后一天；<br>
 * 星期字段单独使用 {@code L} 时表示星期六（7）， 与其它值组合时，例如 {@code 6L}，表示当月最后一个星期五；<br>
 * {@code L-3} 表示当月倒数第 3 天。<br>
 * 使用 {@code L} 时不建议同时指定列表或范围，否则可能产生非预期结果。<br>
 * {@code W} 仅允许用于日字段，表示距离指定日期最近的工作日（周一至周五）。<br>
 * 例如 {@code 15W} 表示距离 15 日最近的工作日：15 日为周六时在 14 日执行， 15 日为周日时在 16 日执行，15 日为工作日时在 15 日当天执行。<br>
 * {@code W} 只能用于单个日期，不能用于范围或列表。 如果 {@code 1W} 中的 1 日为周六，则在 3 日（周一）执行， 因为计算结果不会跨月回退到上个月。<br>
 * {@code LW} 表示当月最后一个工作日。<br>
 * {@code #} 仅允许用于星期字段，表示当月第 n 个指定星期几。<br>
 * 例如 {@code 6#3} 表示当月第 3 个星期五， {@code 2#1} 表示当月第 1 个星期一， {@code 4#5} 表示当月第 5 个星期三。<br>
 * 如果当月不存在第 n 个指定星期几，则该月不会触发。 使用 {@code #} 时星期字段只能包含一个表达式， 例如 {@code 3#1,6#3} 不合法。<br>
 *
 * <b>取值规则：</b><br>
 * 每个字段本质上都对应一个可以开启或关闭的数字集合：<br>
 * 秒和分为 0-59，小时为 0-23，日为 0-31， 月为 0-11（JAN-DEC）。<br>
 *
 * {@code /} 按指定步长开启集合中的值。 需要注意，{@code 7/6} 在月份字段中只会开启 7 月， 而不是表示“每 6 个月一次”。<br>
 *
 * <b>注意事项：</b><br>
 * 日和星期两个字段同时指定时支持并不完整， 通常需要在其中一个字段使用 {@code ?}。<br>
 * 支持溢出范围，即范围左侧的值可以大于右侧的值。 例如 {@code 22-2} 表示每天 22 点至次日凌晨 2 点， {@code NOV-FEB} 表示 11 月至次年 2 月。<br>
 * 溢出范围应谨慎使用。过度使用可能产生没有实际意义的时间区间， 且本类没有定义这类表达式的具体解释方式。 例如 {@code 0 0 14-6 ? * FRI-MON}。<br>
 * 
 * @author Sharada Jambula
 * @author James House
 * @author Contributions from Mads Henderson
 * @author Refactoring from CronTrigger to CronExpression by Aaron Craven
 */
public final class CronExpression implements Serializable {

	private static final long serialVersionUID = 12423409423L;

	protected static final int SECOND = 0;
	protected static final int MINUTE = 1;
	protected static final int HOUR = 2;
	protected static final int DAY_OF_MONTH = 3;
	protected static final int MONTH = 4;
	protected static final int DAY_OF_WEEK = 5;
	protected static final int YEAR = 6;
	protected static final int ALL_SPEC_INT = 99; // 通配符 '*' 对应的内部标记值
	protected static final int NO_SPEC_INT = 98; // 未指定 '?' 对应的内部标记值
	protected static final Integer ALL_SPEC = ALL_SPEC_INT;
	protected static final Integer NO_SPEC = NO_SPEC_INT;

	protected static final Map<String, Integer> monthMap = new HashMap<String, Integer>(20);
	protected static final Map<String, Integer> dayMap = new HashMap<String, Integer>(60);
	static {
		monthMap.put("JAN", 0);
		monthMap.put("FEB", 1);
		monthMap.put("MAR", 2);
		monthMap.put("APR", 3);
		monthMap.put("MAY", 4);
		monthMap.put("JUN", 5);
		monthMap.put("JUL", 6);
		monthMap.put("AUG", 7);
		monthMap.put("SEP", 8);
		monthMap.put("OCT", 9);
		monthMap.put("NOV", 10);
		monthMap.put("DEC", 11);

		dayMap.put("SUN", 1);
		dayMap.put("MON", 2);
		dayMap.put("TUE", 3);
		dayMap.put("WED", 4);
		dayMap.put("THU", 5);
		dayMap.put("FRI", 6);
		dayMap.put("SAT", 7);
	}

	private final String cronExpression;
	private TimeZone timeZone = null;
	protected transient TreeSet<Integer> seconds;
	protected transient TreeSet<Integer> minutes;
	protected transient TreeSet<Integer> hours;
	protected transient TreeSet<Integer> daysOfMonth;
	protected transient TreeSet<Integer> months;
	protected transient TreeSet<Integer> daysOfWeek;
	protected transient TreeSet<Integer> years;
	protected transient TreeSet<Integer> others;

	protected transient boolean lastdayOfWeek = false;
	protected transient int nthdayOfWeek = 0;
	protected transient boolean lastdayOfMonth = false;
	protected transient boolean nearestWeekday = false;
	protected transient int lastdayOffset = 0;
	protected transient boolean expressionParsed = false;

	public static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;

	/**
	 * 基于给定参数构造一个新的 <CODE>CronExpression</CODE>。
	 * @param cronExpression 新对象所表示的 cron 表达式字符串
	 * @throws java.text.ParseException 若字符串无法解析为合法的 <CODE>CronExpression</CODE>
	 */
	public CronExpression(String cronExpression) throws ParseException {
		if (cronExpression == null) {
			throw new IllegalArgumentException("cronExpression cannot be null");
		}

		this.cronExpression = cronExpression.toUpperCase(Locale.US);

		buildExpression(this.cronExpression);
	}

	/**
	 * 以已有实例为蓝本,构造一个与之相同的新 {@code CronExpression}。
	 * @param expression 待复制的已有 cron 表达式
	 */
	public CronExpression(CronExpression expression) {
		/* 此处不调用另一个构造器,因为需要吞掉 ParseException;同时省略了部分合法性检查,因为这些检查在此逻辑下不可能触发。 */
		this.cronExpression = expression.getCronExpression();
		try {
			buildExpression(cronExpression);
		} catch (ParseException ex) {
			throw new AssertionError();
		}
		if (expression.getTimeZone() != null) {
			setTimeZone((TimeZone) expression.getTimeZone().clone());
		}
	}

	/**
	 * 判断给定日期是否满足该 cron 表达式。 注意:毫秒会被忽略,因此落在同一秒不同毫秒的两个 Date 在此方法中结果总是一致。
	 * @param date 待判断的日期
	 * @return 给定日期是否满足 cron 表达式
	 */
	public boolean isSatisfiedBy(Date date) {
		Calendar testDateCal = Calendar.getInstance(getTimeZone());
		testDateCal.setTime(date);
		testDateCal.set(Calendar.MILLISECOND, 0);
		Date originalDate = testDateCal.getTime();

		testDateCal.add(Calendar.SECOND, -1);

		Date timeAfter = getTimeAfter(testDateCal.getTime());

		return ((timeAfter != null) && (timeAfter.equals(originalDate)));
	}

	/**
	 * 返回晚于给定日期/时间、且满足该 cron 表达式的下一个日期/时间。
	 * @param date 从此日期/时间开始向后搜索下一个合法日期/时间
	 * @return 下一个合法的日期/时间
	 */
	public Date getNextValidTimeAfter(Date date) {
		return getTimeAfter(date);
	}

	/**
	 * 返回晚于给定日期/时间、且<strong>不满足</strong>该表达式的下一个日期/时间。
	 * @param date 从此日期/时间开始向后搜索下一个不合法的日期/时间
	 * @return 下一个不合法的日期/时间
	 */
	public Date getNextInvalidTimeAfter(Date date) {
		long difference = 1000;

		// 回退到最近的整秒,以保证差值计算准确
		Calendar adjustCal = Calendar.getInstance(getTimeZone());
		adjustCal.setTime(date);
		adjustCal.set(Calendar.MILLISECOND, 0);
		Date lastDate = adjustCal.getTime();

		Date newDate;

		// 待办(FUTURE_TODO,QUARTZ-481):需改进!下面的解法很差,性能取决于 cron 表达式本身,但至少是一种可行的方案。

		// 不断取出下一个包含的时间,直到相邻间隔大于一秒为止。
		// 此时 lastDate 即为最后一个合法触发时间,返回紧随其后的那一秒。
		while (difference == 1000) {
			newDate = getTimeAfter(lastDate);
			if (newDate == null)
				break;

			difference = newDate.getTime() - lastDate.getTime();

			if (difference == 1000) {
				lastDate = newDate;
			}
		}

		return new Date(lastDate.getTime() + 1000);
	}

	/**
	 * 返回本 <code>CronExpression</code> 求值所使用的时间 zone。
	 * @return 时间 zone
	 */
	public TimeZone getTimeZone() {
		if (timeZone == null) {
			timeZone = TimeZone.getDefault();
		}

		return timeZone;
	}

	/**
	 * 设置本 <code>CronExpression</code> 求值所使用的时间 zone。
	 * @param timeZone 时间 zone
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * 返回 <CODE>CronExpression</CODE> 的字符串表示。
	 * @return <CODE>CronExpression</CODE> 的字符串表示
	 */
	@Override
	public String toString() {
		return cronExpression;
	}

	/**
	 * 判断给定的 cron 表达式能否被解析为合法的 cron 表达式。
	 * @param cronExpression 待判断的表达式
	 * @return 给定表达式是否为合法的 cron 表达式
	 */
	public static boolean isValidExpression(String cronExpression) {

		try {
			new CronExpression(cronExpression);
		} catch (ParseException pe) {
			return false;
		}

		return true;
	}

	public static void validateExpression(String cronExpression) throws ParseException {

		new CronExpression(cronExpression);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// 表达式解析相关方法
	//
	////////////////////////////////////////////////////////////////////////////

	protected void buildExpression(String expression) throws ParseException {
		expressionParsed = true;

		try {

			if (seconds == null) {
				seconds = new TreeSet<Integer>();
			}
			if (minutes == null) {
				minutes = new TreeSet<Integer>();
			}
			if (hours == null) {
				hours = new TreeSet<Integer>();
			}
			if (daysOfMonth == null) {
				daysOfMonth = new TreeSet<Integer>();
			}
			if (months == null) {
				months = new TreeSet<Integer>();
			}
			if (daysOfWeek == null) {
				daysOfWeek = new TreeSet<Integer>();
			}
			if (years == null) {
				years = new TreeSet<Integer>();
			}
			if (others == null) {
				others = new TreeSet<Integer>();
			}

			int exprOn = SECOND;

			StringTokenizer exprsTok = new StringTokenizer(expression, " \t", false);

			while (exprsTok.hasMoreTokens() && exprOn <= YEAR) {
				String expr = exprsTok.nextToken().trim();

				// 若 'L' 与其他"日"值混用则抛异常
				if (exprOn == DAY_OF_MONTH && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
					throw new ParseException(
							"Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
				}
				// 若 'L' 与其他"星期"值混用则抛异常
				if (exprOn == DAY_OF_WEEK && expr.indexOf('L') != -1 && expr.length() > 1 && expr.contains(",")) {
					throw new ParseException(
							"Support for specifying 'L' with other days of the week is not implemented", -1);
				}
				if (exprOn == DAY_OF_WEEK && expr.indexOf('#') != -1
						&& expr.indexOf('#', expr.indexOf('#') + 1) != -1) {
					throw new ParseException("Support for specifying multiple \"nth\" days is not implemented.", -1);
				}

				StringTokenizer vTok = new StringTokenizer(expr, ",");
				while (vTok.hasMoreTokens()) {
					String v = vTok.nextToken();
					storeExpressionVals(0, v, exprOn);
				}

				exprOn++;
			}

			if (exprOn <= DAY_OF_WEEK) {
				throw new ParseException("Unexpected end of expression.", expression.length());
			}

			if (exprOn <= YEAR) {
				storeExpressionVals(0, "*", YEAR);
			}

			TreeSet<Integer> dow = getSet(DAY_OF_WEEK);
			TreeSet<Integer> dom = getSet(DAY_OF_MONTH);

			// 沿用下方 UnsupportedOperationException 中的判定逻辑
			boolean dayOfMSpec = !dom.contains(NO_SPEC);
			boolean dayOfWSpec = !dow.contains(NO_SPEC);

			if (!dayOfMSpec || dayOfWSpec) {
				if (!dayOfWSpec || dayOfMSpec) {
					throw new ParseException(
							"Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.",
							0);
				}
			}
		} catch (ParseException pe) {
			throw pe;
		} catch (Exception e) {
			throw new ParseException("Illegal cron expression format (" + e.toString() + ")", 0);
		}
	}

	protected int storeExpressionVals(int pos, String s, int type) throws ParseException {

		int incr = 0;
		int i = skipWhiteSpace(pos, s);
		if (i >= s.length()) {
			return i;
		}
		char c = s.charAt(i);
		if ((c >= 'A') && (c <= 'Z') && (!s.equals("L")) && (!s.equals("LW")) && (!s.matches("^L-[0-9]*[W]?"))) {
			String sub = s.substring(i, i + 3);
			int sval = -1;
			int eval = -1;
			if (type == MONTH) {
				sval = getMonthNumber(sub) + 1;
				if (sval <= 0) {
					throw new ParseException("Invalid Month value: '" + sub + "'", i);
				}
				if (s.length() > i + 3) {
					c = s.charAt(i + 3);
					if (c == '-') {
						i += 4;
						sub = s.substring(i, i + 3);
						eval = getMonthNumber(sub) + 1;
						if (eval <= 0) {
							throw new ParseException("Invalid Month value: '" + sub + "'", i);
						}
					}
				}
			} else if (type == DAY_OF_WEEK) {
				sval = getDayOfWeekNumber(sub);
				if (sval < 0) {
					throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
				}
				if (s.length() > i + 3) {
					c = s.charAt(i + 3);
					if (c == '-') {
						i += 4;
						sub = s.substring(i, i + 3);
						eval = getDayOfWeekNumber(sub);
						if (eval < 0) {
							throw new ParseException("Invalid Day-of-Week value: '" + sub + "'", i);
						}
					} else if (c == '#') {
						try {
							i += 4;
							nthdayOfWeek = Integer.parseInt(s.substring(i));
							if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
								throw new Exception();
							}
						} catch (Exception e) {
							throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
						}
					} else if (c == 'L') {
						lastdayOfWeek = true;
						i++;
					}
				}

			} else {
				throw new ParseException("Illegal characters for this position: '" + sub + "'", i);
			}
			if (eval != -1) {
				incr = 1;
			}
			addToSet(sval, eval, incr, type);
			return (i + 3);
		}

		if (c == '?') {
			i++;
			if ((i + 1) < s.length() && (s.charAt(i) != ' ' && s.charAt(i + 1) != '\t')) {
				throw new ParseException("Illegal character after '?': " + s.charAt(i), i);
			}
			if (type != DAY_OF_WEEK && type != DAY_OF_MONTH) {
				throw new ParseException("'?' can only be specified for Day-of-Month or Day-of-Week.", i);
			}
			if (type == DAY_OF_WEEK && !lastdayOfMonth) {
				int val = daysOfMonth.last();
				if (val == NO_SPEC_INT) {
					throw new ParseException("'?' can only be specified for Day-of-Month -OR- Day-of-Week.", i);
				}
			}

			addToSet(NO_SPEC_INT, -1, 0, type);
			return i;
		}

		if (c == '*' || c == '/') {
			if (c == '*' && (i + 1) >= s.length()) {
				addToSet(ALL_SPEC_INT, -1, incr, type);
				return i + 1;
			} else if (c == '/' && ((i + 1) >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t')) {
				throw new ParseException("'/' must be followed by an integer.", i);
			} else if (c == '*') {
				i++;
			}
			c = s.charAt(i);
			if (c == '/') { // 是否指定了步长(增量)?
				i++;
				if (i >= s.length()) {
					throw new ParseException("Unexpected end of string.", i);
				}

				incr = getNumericValue(s, i);

				i++;
				if (incr > 10) {
					i++;
				}
				checkIncrementRange(incr, type, i);
			} else {
				incr = 1;
			}

			addToSet(ALL_SPEC_INT, -1, incr, type);
			return i;
		} else if (c == 'L') {
			i++;
			if (type == DAY_OF_MONTH) {
				lastdayOfMonth = true;
			}
			if (type == DAY_OF_WEEK) {
				addToSet(7, 7, 0, type);
			}
			if (type == DAY_OF_MONTH && s.length() > i) {
				c = s.charAt(i);
				if (c == '-') {
					ValueSet vs = getValue(0, s, i + 1);
					lastdayOffset = vs.value;
					if (lastdayOffset > 30)
						throw new ParseException("Offset from last day must be <= 30", i + 1);
					i = vs.pos;
				}
				if (s.length() > i) {
					c = s.charAt(i);
					if (c == 'W') {
						nearestWeekday = true;
						i++;
					}
				}
			}
			return i;
		} else if (c >= '0' && c <= '9') {
			int val = Integer.parseInt(String.valueOf(c));
			i++;
			if (i >= s.length()) {
				addToSet(val, -1, -1, type);
			} else {
				c = s.charAt(i);
				if (c >= '0' && c <= '9') {
					ValueSet vs = getValue(val, s, i);
					val = vs.value;
					i = vs.pos;
				}
				i = checkNext(i, s, val, type);
				return i;
			}
		} else {
			throw new ParseException("Unexpected character: " + c, i);
		}

		return i;
	}

	private void checkIncrementRange(int incr, int type, int idxPos) throws ParseException {
		if (incr > 59 && (type == SECOND || type == MINUTE)) {
			throw new ParseException("Increment > 60 : " + incr, idxPos);
		} else if (incr > 23 && (type == HOUR)) {
			throw new ParseException("Increment > 24 : " + incr, idxPos);
		} else if (incr > 31 && (type == DAY_OF_MONTH)) {
			throw new ParseException("Increment > 31 : " + incr, idxPos);
		} else if (incr > 7 && (type == DAY_OF_WEEK)) {
			throw new ParseException("Increment > 7 : " + incr, idxPos);
		} else if (incr > 12 && (type == MONTH)) {
			throw new ParseException("Increment > 12 : " + incr, idxPos);
		}
	}

	protected int checkNext(int pos, String s, int val, int type) throws ParseException {

		int end = -1;
		int i = pos;

		if (i >= s.length()) {
			addToSet(val, end, -1, type);
			return i;
		}

		char c = s.charAt(pos);

		if (c == 'L') {
			if (type == DAY_OF_WEEK) {
				if (val < 1 || val > 7)
					throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
				lastdayOfWeek = true;
			} else {
				throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
			}
			TreeSet<Integer> set = getSet(type);
			set.add(val);
			i++;
			return i;
		}

		if (c == 'W') {
			if (type == DAY_OF_MONTH) {
				nearestWeekday = true;
			} else {
				throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
			}
			if (val > 31)
				throw new ParseException(
						"The 'W' option does not make sense with values larger than 31 (max number of days in a month)",
						i);
			TreeSet<Integer> set = getSet(type);
			set.add(val);
			i++;
			return i;
		}

		if (c == '#') {
			if (type != DAY_OF_WEEK) {
				throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
			}
			i++;
			try {
				nthdayOfWeek = Integer.parseInt(s.substring(i));
				if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new ParseException("A numeric value between 1 and 5 must follow the '#' option", i);
			}

			TreeSet<Integer> set = getSet(type);
			set.add(val);
			i++;
			return i;
		}

		if (c == '-') {
			i++;
			c = s.charAt(i);
			int v = Integer.parseInt(String.valueOf(c));
			end = v;
			i++;
			if (i >= s.length()) {
				addToSet(val, end, 1, type);
				return i;
			}
			c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				ValueSet vs = getValue(v, s, i);
				end = vs.value;
				i = vs.pos;
			}
			if (i < s.length() && ((c = s.charAt(i)) == '/')) {
				i++;
				c = s.charAt(i);
				int v2 = Integer.parseInt(String.valueOf(c));
				i++;
				if (i >= s.length()) {
					addToSet(val, end, v2, type);
					return i;
				}
				c = s.charAt(i);
				if (c >= '0' && c <= '9') {
					ValueSet vs = getValue(v2, s, i);
					int v3 = vs.value;
					addToSet(val, end, v3, type);
					i = vs.pos;
					return i;
				} else {
					addToSet(val, end, v2, type);
					return i;
				}
			} else {
				addToSet(val, end, 1, type);
				return i;
			}
		}

		if (c == '/') {
			if ((i + 1) >= s.length() || s.charAt(i + 1) == ' ' || s.charAt(i + 1) == '\t') {
				throw new ParseException("'/' must be followed by an integer.", i);
			}

			i++;
			c = s.charAt(i);
			int v2 = Integer.parseInt(String.valueOf(c));
			i++;
			if (i >= s.length()) {
				checkIncrementRange(v2, type, i);
				addToSet(val, end, v2, type);
				return i;
			}
			c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				ValueSet vs = getValue(v2, s, i);
				int v3 = vs.value;
				checkIncrementRange(v3, type, i);
				addToSet(val, end, v3, type);
				i = vs.pos;
				return i;
			} else {
				throw new ParseException("Unexpected character '" + c + "' after '/'", i);
			}
		}

		addToSet(val, end, 0, type);
		i++;
		return i;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public String getExpressionSummary() {
		StringBuilder buf = new StringBuilder();

		buf.append("seconds: ");
		buf.append(getExpressionSetSummary(seconds));
		buf.append("\n");
		buf.append("minutes: ");
		buf.append(getExpressionSetSummary(minutes));
		buf.append("\n");
		buf.append("hours: ");
		buf.append(getExpressionSetSummary(hours));
		buf.append("\n");
		buf.append("daysOfMonth: ");
		buf.append(getExpressionSetSummary(daysOfMonth));
		buf.append("\n");
		buf.append("months: ");
		buf.append(getExpressionSetSummary(months));
		buf.append("\n");
		buf.append("daysOfWeek: ");
		buf.append(getExpressionSetSummary(daysOfWeek));
		buf.append("\n");
		buf.append("lastdayOfWeek: ");
		buf.append(lastdayOfWeek);
		buf.append("\n");
		buf.append("nearestWeekday: ");
		buf.append(nearestWeekday);
		buf.append("\n");
		buf.append("NthDayOfWeek: ");
		buf.append(nthdayOfWeek);
		buf.append("\n");
		buf.append("lastdayOfMonth: ");
		buf.append(lastdayOfMonth);
		buf.append("\n");
		buf.append("years: ");
		buf.append(getExpressionSetSummary(years));
		buf.append("\n");

		return buf.toString();
	}

	protected String getExpressionSetSummary(java.util.Set<Integer> set) {

		if (set.contains(NO_SPEC)) {
			return "?";
		}
		if (set.contains(ALL_SPEC)) {
			return "*";
		}

		StringBuilder buf = new StringBuilder();

		Iterator<Integer> itr = set.iterator();
		boolean first = true;
		while (itr.hasNext()) {
			Integer iVal = itr.next();
			String val = iVal.toString();
			if (!first) {
				buf.append(",");
			}
			buf.append(val);
			first = false;
		}

		return buf.toString();
	}

	protected String getExpressionSetSummary(java.util.ArrayList<Integer> list) {

		if (list.contains(NO_SPEC)) {
			return "?";
		}
		if (list.contains(ALL_SPEC)) {
			return "*";
		}

		StringBuilder buf = new StringBuilder();

		Iterator<Integer> itr = list.iterator();
		boolean first = true;
		while (itr.hasNext()) {
			Integer iVal = itr.next();
			String val = iVal.toString();
			if (!first) {
				buf.append(",");
			}
			buf.append(val);
			first = false;
		}

		return buf.toString();
	}

	protected int skipWhiteSpace(int i, String s) {
		for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++) {
			;
		}

		return i;
	}

	protected int findNextWhiteSpace(int i, String s) {
		for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++) {
			;
		}

		return i;
	}

	protected void addToSet(int val, int end, int incr, int type) throws ParseException {

		TreeSet<Integer> set = getSet(type);

		if (type == SECOND || type == MINUTE) {
			if ((val < 0 || val > 59 || end > 59) && (val != ALL_SPEC_INT)) {
				throw new ParseException("Minute and Second values must be between 0 and 59", -1);
			}
		} else if (type == HOUR) {
			if ((val < 0 || val > 23 || end > 23) && (val != ALL_SPEC_INT)) {
				throw new ParseException("Hour values must be between 0 and 23", -1);
			}
		} else if (type == DAY_OF_MONTH) {
			if ((val < 1 || val > 31 || end > 31) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
				throw new ParseException("Day of month values must be between 1 and 31", -1);
			}
		} else if (type == MONTH) {
			if ((val < 1 || val > 12 || end > 12) && (val != ALL_SPEC_INT)) {
				throw new ParseException("Month values must be between 1 and 12", -1);
			}
		} else if (type == DAY_OF_WEEK) {
			if ((val == 0 || val > 7 || end > 7) && (val != ALL_SPEC_INT) && (val != NO_SPEC_INT)) {
				throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
			}
		}

		if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT) {
			if (val != -1) {
				set.add(val);
			} else {
				set.add(NO_SPEC);
			}

			return;
		}

		int startAt = val;
		int stopAt = end;

		if (val == ALL_SPEC_INT && incr <= 0) {
			incr = 1;
			set.add(ALL_SPEC); // 放入一个标记,同时也填充具体取值
		}

		if (type == SECOND || type == MINUTE) {
			if (stopAt == -1) {
				stopAt = 59;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 0;
			}
		} else if (type == HOUR) {
			if (stopAt == -1) {
				stopAt = 23;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 0;
			}
		} else if (type == DAY_OF_MONTH) {
			if (stopAt == -1) {
				stopAt = 31;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 1;
			}
		} else if (type == MONTH) {
			if (stopAt == -1) {
				stopAt = 12;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 1;
			}
		} else if (type == DAY_OF_WEEK) {
			if (stopAt == -1) {
				stopAt = 7;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 1;
			}
		} else if (type == YEAR) {
			if (stopAt == -1) {
				stopAt = MAX_YEAR;
			}
			if (startAt == -1 || startAt == ALL_SPEC_INT) {
				startAt = 1970;
			}
		}

		// 若范围结束值小于起始值,则需要溢出到次日/次月等。
		// 做法是加上该类型的最大取值,再用"取模最大值"来确定实际累加的值。
		int max = -1;
		if (stopAt < startAt) {
			switch (type) {
			case SECOND:
				max = 60;
				break;
			case MINUTE:
				max = 60;
				break;
			case HOUR:
				max = 24;
				break;
			case MONTH:
				max = 12;
				break;
			case DAY_OF_WEEK:
				max = 7;
				break;
			case DAY_OF_MONTH:
				max = 31;
				break;
			case YEAR:
				throw new IllegalArgumentException("Start year must be less than stop year");
			default:
				throw new IllegalArgumentException("Unexpected type encountered");
			}
			stopAt += max;
		}

		for (int i = startAt; i <= stopAt; i += incr) {
			if (max == -1) {
				// 即没有可溢出的上限
				set.add(i);
			} else {
				// 对最大值取模,得到真实取值
				int i2 = i % max;

				// 1 起始的范围不应包含 0,而应包含其最大值
				if (i2 == 0 && (type == MONTH || type == DAY_OF_WEEK || type == DAY_OF_MONTH)) {
					i2 = max;
				}

				set.add(i2);
			}
		}
	}

	TreeSet<Integer> getSet(int type) {
		switch (type) {
		case SECOND:
			return seconds;
		case MINUTE:
			return minutes;
		case HOUR:
			return hours;
		case DAY_OF_MONTH:
			return daysOfMonth;
		case MONTH:
			return months;
		case DAY_OF_WEEK:
			return daysOfWeek;
		case YEAR:
			return years;
		default:
			return others;
		}
	}

	protected ValueSet getValue(int v, String s, int i) {
		char c = s.charAt(i);
		StringBuilder s1 = new StringBuilder(String.valueOf(v));
		while (c >= '0' && c <= '9') {
			s1.append(c);
			i++;
			if (i >= s.length()) {
				break;
			}
			c = s.charAt(i);
		}
		ValueSet val = new ValueSet();

		val.pos = (i < s.length()) ? i : i + 1;
		val.value = Integer.parseInt(s1.toString());
		return val;
	}

	protected int getNumericValue(String s, int i) {
		int endOfVal = findNextWhiteSpace(i, s);
		String val = s.substring(i, endOfVal);
		return Integer.parseInt(val);
	}

	protected int getMonthNumber(String s) {
		Integer integer = monthMap.get(s);

		if (integer == null) {
			return -1;
		}

		return integer;
	}

	protected int getDayOfWeekNumber(String s) {
		Integer integer = dayMap.get(s);

		if (integer == null) {
			return -1;
		}

		return integer;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// 计算相关方法
	//
	////////////////////////////////////////////////////////////////////////////

	public Date getTimeAfter(Date afterTime) {

		// 计算仅基于公历年份。
		Calendar cl = new java.util.GregorianCalendar(getTimeZone());

		// 向后推进一秒,因为我们要算的是"给定时间之后"的时刻
		afterTime = new Date(afterTime.getTime() + 1000);
		// CronTrigger 不处理毫秒
		cl.setTime(afterTime);
		cl.set(Calendar.MILLISECOND, 0);

		boolean gotOne = false;
		// 循环直到算出下一个时间,或已超过 end 时间
		while (!gotOne) {

			// if (endTime != null && cl.getTime().after(endTime)) return null;
			if (cl.get(Calendar.YEAR) > 2999) { // 防止无限循环...
				return null;
			}

			SortedSet<Integer> st = null;
			int t = 0;

			int sec = cl.get(Calendar.SECOND);
			int min = cl.get(Calendar.MINUTE);

			// 计算秒.................................................
			st = seconds.tailSet(sec);
			if (st != null && st.size() != 0) {
				sec = st.first();
			} else {
				sec = seconds.first();
				min++;
				cl.set(Calendar.MINUTE, min);
			}
			cl.set(Calendar.SECOND, sec);

			min = cl.get(Calendar.MINUTE);
			int hr = cl.get(Calendar.HOUR_OF_DAY);
			t = -1;

			// 计算分.................................................
			st = minutes.tailSet(min);
			if (st != null && st.size() != 0) {
				t = min;
				min = st.first();
			} else {
				min = minutes.first();
				hr++;
			}
			if (min != t) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, min);
				setCalendarHour(cl, hr);
				continue;
			}
			cl.set(Calendar.MINUTE, min);

			hr = cl.get(Calendar.HOUR_OF_DAY);
			int day = cl.get(Calendar.DAY_OF_MONTH);
			t = -1;

			// 计算时...................................................
			st = hours.tailSet(hr);
			if (st != null && st.size() != 0) {
				t = hr;
				hr = st.first();
			} else {
				hr = hours.first();
				day++;
			}
			if (hr != t) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.DAY_OF_MONTH, day);
				setCalendarHour(cl, hr);
				continue;
			}
			cl.set(Calendar.HOUR_OF_DAY, hr);

			day = cl.get(Calendar.DAY_OF_MONTH);
			int mon = cl.get(Calendar.MONTH) + 1;
			// '+ 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
			t = -1;
			int tmon = mon;

			// 计算日...................................................
			boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
			boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
			if (dayOfMSpec && !dayOfWSpec) { // 按"日"规则计算日
				st = daysOfMonth.tailSet(day);
				if (lastdayOfMonth) {
					if (!nearestWeekday) {
						t = day;
						day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
						day -= lastdayOffset;
						if (t > day) {
							mon++;
							if (mon > 12) {
								mon = 1;
								tmon = 3333; // 确保下方 mon != tmon 的判定失败
								cl.add(Calendar.YEAR, 1);
							}
							day = 1;
						}
					} else {
						t = day;
						day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
						day -= lastdayOffset;

						java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
						tcal.set(Calendar.SECOND, 0);
						tcal.set(Calendar.MINUTE, 0);
						tcal.set(Calendar.HOUR_OF_DAY, 0);
						tcal.set(Calendar.DAY_OF_MONTH, day);
						tcal.set(Calendar.MONTH, mon - 1);
						tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

						int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
						int dow = tcal.get(Calendar.DAY_OF_WEEK);

						if (dow == Calendar.SATURDAY && day == 1) {
							day += 2;
						} else if (dow == Calendar.SATURDAY) {
							day -= 1;
						} else if (dow == Calendar.SUNDAY && day == ldom) {
							day -= 2;
						} else if (dow == Calendar.SUNDAY) {
							day += 1;
						}

						tcal.set(Calendar.SECOND, sec);
						tcal.set(Calendar.MINUTE, min);
						tcal.set(Calendar.HOUR_OF_DAY, hr);
						tcal.set(Calendar.DAY_OF_MONTH, day);
						tcal.set(Calendar.MONTH, mon - 1);
						Date nTime = tcal.getTime();
						if (nTime.before(afterTime)) {
							day = 1;
							mon++;
						}
					}
				} else if (nearestWeekday) {
					t = day;
					day = daysOfMonth.first();

					java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
					tcal.set(Calendar.SECOND, 0);
					tcal.set(Calendar.MINUTE, 0);
					tcal.set(Calendar.HOUR_OF_DAY, 0);
					tcal.set(Calendar.DAY_OF_MONTH, day);
					tcal.set(Calendar.MONTH, mon - 1);
					tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

					int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
					int dow = tcal.get(Calendar.DAY_OF_WEEK);

					if (dow == Calendar.SATURDAY && day == 1) {
						day += 2;
					} else if (dow == Calendar.SATURDAY) {
						day -= 1;
					} else if (dow == Calendar.SUNDAY && day == ldom) {
						day -= 2;
					} else if (dow == Calendar.SUNDAY) {
						day += 1;
					}

					tcal.set(Calendar.SECOND, sec);
					tcal.set(Calendar.MINUTE, min);
					tcal.set(Calendar.HOUR_OF_DAY, hr);
					tcal.set(Calendar.DAY_OF_MONTH, day);
					tcal.set(Calendar.MONTH, mon - 1);
					Date nTime = tcal.getTime();
					if (nTime.before(afterTime)) {
						day = daysOfMonth.first();
						mon++;
					}
				} else if (st != null && st.size() != 0) {
					t = day;
					day = st.first();
					// 避免越过短月(如二月)的边界
					int lastDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
					if (day > lastDay) {
						day = daysOfMonth.first();
						mon++;
					}
				} else {
					day = daysOfMonth.first();
					mon++;
				}

				if (day != t || mon != tmon) {
					cl.set(Calendar.SECOND, 0);
					cl.set(Calendar.MINUTE, 0);
					cl.set(Calendar.HOUR_OF_DAY, 0);
					cl.set(Calendar.DAY_OF_MONTH, day);
					cl.set(Calendar.MONTH, mon - 1);
					// '- 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
					continue;
				}
			} else if (dayOfWSpec && !dayOfMSpec) { // 按"星期"规则计算日
				if (lastdayOfWeek) {
					int dow = daysOfWeek.first(); // 目标星期几
					int cDow = cl.get(Calendar.DAY_OF_WEEK); // 当前星期几
					int daysToAdd = 0;
					if (cDow < dow) {
						daysToAdd = dow - cDow;
					}
					if (cDow > dow) {
						daysToAdd = dow + (7 - cDow);
					}

					int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

					if (day + daysToAdd > lDay) { // 是否已经错过了最后一个?
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon);
						// 此处无需 '- 1',因为月份在进位
						continue;
					}

					// 找到当月该星期几最后一次出现的位置...
					while ((day + daysToAdd + 7) <= lDay) {
						daysToAdd += 7;
					}

					day += daysToAdd;

					if (daysToAdd > 0) {
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day);
						cl.set(Calendar.MONTH, mon - 1);
						// 此处需 '- 1',因为月份未进位
						continue;
					}

				} else if (nthdayOfWeek != 0) {
					// 是否寻找当月第 N 个
					int dow = daysOfWeek.first(); // 目标星期几
					int cDow = cl.get(Calendar.DAY_OF_WEEK); // 当前星期几
					int daysToAdd = 0;
					if (cDow < dow) {
						daysToAdd = dow - cDow;
					} else if (cDow > dow) {
						daysToAdd = dow + (7 - cDow);
					}

					boolean dayShifted = false;
					if (daysToAdd > 0) {
						dayShifted = true;
					}

					day += daysToAdd;
					int weekOfMonth = day / 7;
					if (day % 7 > 0) {
						weekOfMonth++;
					}

					daysToAdd = (nthdayOfWeek - weekOfMonth) * 7;
					day += daysToAdd;
					if (daysToAdd < 0 || day > getLastDayOfMonth(mon, cl.get(Calendar.YEAR))) {
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon);
						// 此处无需 '- 1',因为月份在进位
						continue;
					} else if (daysToAdd > 0 || dayShifted) {
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day);
						cl.set(Calendar.MONTH, mon - 1);
						// 此处需 '- 1',因为月份未进位
						continue;
					}
				} else {
					int cDow = cl.get(Calendar.DAY_OF_WEEK); // 当前星期几
					int dow = daysOfWeek.first(); // 目标星期几
					st = daysOfWeek.tailSet(cDow);
					if (st != null && st.size() > 0) {
						dow = st.first();
					}

					int daysToAdd = 0;
					if (cDow < dow) {
						daysToAdd = dow - cDow;
					}
					if (cDow > dow) {
						daysToAdd = dow + (7 - cDow);
					}

					int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

					if (day + daysToAdd > lDay) { // 是否会越过月末?
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, 1);
						cl.set(Calendar.MONTH, mon);
						// 此处无需 '- 1',因为月份在进位
						continue;
					} else if (daysToAdd > 0) { // 是否需要切换日期?
						cl.set(Calendar.SECOND, 0);
						cl.set(Calendar.MINUTE, 0);
						cl.set(Calendar.HOUR_OF_DAY, 0);
						cl.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
						cl.set(Calendar.MONTH, mon - 1);
						// '- 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
						continue;
					}
				}
			} else { // dayOfWSpec && !dayOfMSpec
				throw new UnsupportedOperationException(
						"Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
			}
			cl.set(Calendar.DAY_OF_MONTH, day);

			mon = cl.get(Calendar.MONTH) + 1;
			// '+ 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
			int year = cl.get(Calendar.YEAR);
			t = -1;

			// 检测那些永远无法生成合法触发时间的表达式,避免无限循环...
			if (year > MAX_YEAR) {
				return null;
			}

			// 计算月...................................................
			st = months.tailSet(mon);
			if (st != null && st.size() != 0) {
				t = mon;
				mon = st.first();
			} else {
				mon = months.first();
				year++;
			}
			if (mon != t) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.HOUR_OF_DAY, 0);
				cl.set(Calendar.DAY_OF_MONTH, 1);
				cl.set(Calendar.MONTH, mon - 1);
				// '- 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
				cl.set(Calendar.YEAR, year);
				continue;
			}
			cl.set(Calendar.MONTH, mon - 1);
			// '- 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始

			year = cl.get(Calendar.YEAR);
			t = -1;

			// 计算年...................................................
			st = years.tailSet(year);
			if (st != null && st.size() != 0) {
				t = year;
				year = st.first();
			} else {
				return null; // 年份用尽...
			}

			if (year != t) {
				cl.set(Calendar.SECOND, 0);
				cl.set(Calendar.MINUTE, 0);
				cl.set(Calendar.HOUR_OF_DAY, 0);
				cl.set(Calendar.DAY_OF_MONTH, 1);
				cl.set(Calendar.MONTH, 0);
				// '- 1' 是因为 Calendar 中该字段是 0 起始,而本实现采用 1 起始
				cl.set(Calendar.YEAR, year);
				continue;
			}
			cl.set(Calendar.YEAR, year);

			gotOne = true;
		} // while( !done )

		return cl.getTime();
	}

	/**
	 * 将日历推进到指定小时,特别注意处理夏令时相关问题。
	 * @param cal  待操作的日历
	 * @param hour 目标小时
	 */
	protected void setCalendarHour(Calendar cal, int hour) {
		cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
		if (cal.get(java.util.Calendar.HOUR_OF_DAY) != hour && hour != 24) {
			cal.set(java.util.Calendar.HOUR_OF_DAY, hour + 1);
		}
	}

	protected boolean isLeapYear(int year) {
		return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
	}

	protected int getLastDayOfMonth(int monthNum, int year) {

		switch (monthNum) {
		case 1:
			return 31;
		case 2:
			return (isLeapYear(year)) ? 29 : 28;
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;
		default:
			throw new IllegalArgumentException("Illegal month number: " + monthNum);
		}
	}

	private void readObject(java.io.ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
		stream.defaultReadObject();
		try {
			buildExpression(cronExpression);
		} catch (Exception ignore) {
		} // 不会发生
	}

	private static class ValueSet {
		private int value;
		private int pos;
	}
}
