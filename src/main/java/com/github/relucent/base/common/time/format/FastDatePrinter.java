package com.github.relucent.base.common.time.format;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.relucent.base.common.exception.ExceptionUtil;
import com.github.relucent.base.common.lang.LocaleUtil;

/**
 * {@link java.text.SimpleDateFormat} 的线程安全版本，用于将 {@link Date} 格式化输出<br>
 * JDK默认的 {code SimpleDateFormat}不是线程安全，所以在多线程环境中，创建线程独立的实例，并且不能作为静态成员多线程公用。<br>
 * 而{@code FastDatePrinter}是线程安全的，因此可以作为静态成员实例，并在多线程环境中安全使用。<br>
 * 此类在大多数格式化情况下可以直接替换{@code SimpleDateFormat}<br>
 * 参考：org.apache.commons.lang3.time.FastDatePrinter<br>
 */
class FastDatePrinter implements DatePrinter, Serializable {

    // =================================Static=================================================
    /** 序列化支持 */
    private static final long serialVersionUID = 1L;

    /** 规则空数组 */
    private static final Rule[] EMPTY_RULE_ARRAY = new Rule[0];

    /** 最大数值长度 （log10(Integer.MAX_VALUE) ~= 9.3） */
    private static final int MAX_DIGITS = 10;

    /**
     * 完全长度的日期或时间样式。例如：“1970年1月1日 星期四”或“Thursday, January 1, 1970”
     */
    public static final int FULL = DateFormat.FULL;
    /**
     * 较长的日期时间样式。例如：“1970年1月1日”或“January 1, 1970”
     */
    public static final int LONG = DateFormat.LONG;
    /**
     * 中等长度的时间样式。例如：“1970-1-1”或“Jan 1, 1970”
     */
    public static final int MEDIUM = DateFormat.MEDIUM;
    /**
     * 较短的日期时间样式。例如：“70-1-1”或“1/1/70”
     */
    public static final int SHORT = DateFormat.SHORT;

    // =================================Fields=================================================
    /** 日期格式 */
    private final String pattern;

    /** 时区 */
    private final TimeZone timeZone;

    /** 地区 */
    private final Locale locale;

    /** 解析的规则 */
    private transient Rule[] rules;

    /** 估计的最大长度 */
    private transient int maxLengthEstimate;

    // =================================Constructors===========================================
    /**
     * 构造函数<br>
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @throws NullPointerException 如果 pattern、timeZone、locale 为{@code null}
     */
    protected FastDatePrinter(final String pattern, final TimeZone timeZone, final Locale locale) {
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = LocaleUtil.defaultLocale(locale);
        init();
    }

    /**
     * 初始化实例以供首次使用
     */
    private void init() {
        final List<Rule> rulesList = parsePattern();
        rules = rulesList.toArray(EMPTY_RULE_ARRAY);
        int length = 0;
        for (int i = rules.length; --i >= 0;) {
            length += rules[i].estimateLength();
        }
        maxLengthEstimate = length;
    }

    // =================================ParseMethods===========================================
    /**
     * 返回给定模式的规则列表
     * @return 规则列表
     * @throws IllegalArgumentException 如果日期格式{@code pattern}无效
     */
    protected List<Rule> parsePattern() {
        final DateFormatSymbols symbols = new DateFormatSymbols(locale);
        final List<Rule> rules = new ArrayList<>();

        final String[] ERAs = symbols.getEras();
        final String[] months = symbols.getMonths();
        final String[] shortMonths = symbols.getShortMonths();
        final String[] weekdays = symbols.getWeekdays();
        final String[] shortWeekdays = symbols.getShortWeekdays();
        final String[] AmPmStrings = symbols.getAmPmStrings();

        final int length = pattern.length();
        final int[] indexRef = new int[1];

        for (int i = 0; i < length; i++) {
            indexRef[0] = i;
            final String token = parseToken(pattern, indexRef);
            i = indexRef[0];

            final int tokenLen = token.length();
            if (tokenLen == 0) {
                break;
            }

            Rule rule;
            final char c = token.charAt(0);

            switch (c) {
            case 'G': // era designator (text)
                rule = new TextField(Calendar.ERA, ERAs);
                break;
            case 'y': // year (number)
            case 'Y': // week year
                if (tokenLen == 2) {
                    rule = TwoDigitYearField.INSTANCE;
                } else {
                    rule = selectNumberRule(Calendar.YEAR, Math.max(tokenLen, 4));
                }
                if (c == 'Y') {
                    rule = new WeekYear((NumberRule) rule);
                }
                break;
            case 'M': // month in year (text and number)
                if (tokenLen >= 4) {
                    rule = new TextField(Calendar.MONTH, months);
                } else if (tokenLen == 3) {
                    rule = new TextField(Calendar.MONTH, shortMonths);
                } else if (tokenLen == 2) {
                    rule = TwoDigitMonthField.INSTANCE;
                } else {
                    rule = UnpaddedMonthField.INSTANCE;
                }
                break;
            case 'd': // day in month (number)
                rule = selectNumberRule(Calendar.DAY_OF_MONTH, tokenLen);
                break;
            case 'h': // hour in am/pm (number, 1..12)
                rule = new TwelveHourField(selectNumberRule(Calendar.HOUR, tokenLen));
                break;
            case 'H': // hour in day (number, 0..23)
                rule = selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen);
                break;
            case 'm': // minute in hour (number)
                rule = selectNumberRule(Calendar.MINUTE, tokenLen);
                break;
            case 's': // second in minute (number)
                rule = selectNumberRule(Calendar.SECOND, tokenLen);
                break;
            case 'S': // millisecond (number)
                rule = selectNumberRule(Calendar.MILLISECOND, tokenLen);
                break;
            case 'E': // day in week (text)
                rule = new TextField(Calendar.DAY_OF_WEEK, tokenLen < 4 ? shortWeekdays : weekdays);
                break;
            case 'u': // day in week (number)
                rule = new DayInWeekField(selectNumberRule(Calendar.DAY_OF_WEEK, tokenLen));
                break;
            case 'D': // day in year (number)
                rule = selectNumberRule(Calendar.DAY_OF_YEAR, tokenLen);
                break;
            case 'F': // day of week in month (number)
                rule = selectNumberRule(Calendar.DAY_OF_WEEK_IN_MONTH, tokenLen);
                break;
            case 'w': // week in year (number)
                rule = selectNumberRule(Calendar.WEEK_OF_YEAR, tokenLen);
                break;
            case 'W': // week in month (number)
                rule = selectNumberRule(Calendar.WEEK_OF_MONTH, tokenLen);
                break;
            case 'a': // am/pm marker (text)
                rule = new TextField(Calendar.AM_PM, AmPmStrings);
                break;
            case 'k': // hour in day (1..24)
                rule = new TwentyFourHourField(selectNumberRule(Calendar.HOUR_OF_DAY, tokenLen));
                break;
            case 'K': // hour in am/pm (0..11)
                rule = selectNumberRule(Calendar.HOUR, tokenLen);
                break;
            case 'X': // ISO 8601
                rule = ISO8601Rule.getRule(tokenLen);
                break;
            case 'z': // time zone (text)
                if (tokenLen >= 4) {
                    rule = new TimeZoneNameRule(timeZone, locale, TimeZone.LONG);
                } else {
                    rule = new TimeZoneNameRule(timeZone, locale, TimeZone.SHORT);
                }
                break;
            case 'Z': // time zone (value)
                if (tokenLen == 1) {
                    rule = TimeZoneNumberRule.INSTANCE_NO_COLON;
                } else if (tokenLen == 2) {
                    rule = ISO8601Rule.ISO8601_HOURS_COLON_MINUTES;
                } else {
                    rule = TimeZoneNumberRule.INSTANCE_COLON;
                }
                break;
            case '\'': // literal text
                final String sub = token.substring(1);
                if (sub.length() == 1) {
                    rule = new CharacterLiteral(sub.charAt(0));
                } else {
                    rule = new StringLiteral(sub);
                }
                break;
            default:
                throw new IllegalArgumentException("Illegal pattern component: " + token);
            }

            rules.add(rule);
        }

        return rules;
    }

    /**
     * 分析日期格式中的符号
     * @param pattern 日期格式
     * @param indexRef 索引引用，用于返回
     * @return 解析的符号
     */
    protected String parseToken(final String pattern, final int[] indexRef) {
        final StringBuilder buf = new StringBuilder();

        int i = indexRef[0];
        final int length = pattern.length();

        char c = pattern.charAt(i);
        if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
            // Scan a run of the same character, which indicates a time
            // pattern.
            buf.append(c);

            while (i + 1 < length) {
                final char peek = pattern.charAt(i + 1);
                if (peek == c) {
                    buf.append(c);
                    i++;
                } else {
                    break;
                }
            }
        } else {
            // This will identify token as text.
            buf.append('\'');

            boolean inLiteral = false;

            for (; i < length; i++) {
                c = pattern.charAt(i);

                if (c == '\'') {
                    if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
                        // '' is treated as escaped '
                        i++;
                        buf.append(c);
                    } else {
                        inLiteral = !inLiteral;
                    }
                } else if (!inLiteral && (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')) {
                    i--;
                    break;
                } else {
                    buf.append(c);
                }
            }
        }

        indexRef[0] = i;
        return buf.toString();
    }

    /**
     * 获取所需填充的适当规则
     * @param field 要获取规则的时间字段
     * @param padding 所需的填充长度
     * @return 正确的填充新规则
     */
    protected NumberRule selectNumberRule(final int field, final int padding) {
        switch (padding) {
        case 1:
            return new UnpaddedNumberField(field);
        case 2:
            return new TwoDigitNumberField(field);
        default:
            return new PaddedNumberField(field, padding);
        }
    }

    // =================================FormatMethods==========================================
    /**
     * 格式化毫秒{@code long}
     * @param millis 要格式化的毫秒值
     * @return 格式化后的日期时间字符串
     */
    String format(final Object obj) {
        if (obj instanceof Date) {
            return format((Date) obj);
        } else if (obj instanceof Calendar) {
            return format((Calendar) obj);
        } else if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        } else {
            throw new IllegalArgumentException("Unknown class: " + (obj == null ? "<null>" : obj.getClass().getName()));
        }
    }

    /**
     * 格式化毫秒{@code long}
     * @param millis 要格式化的毫秒值
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final long millis) {
        final Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRulesToString(c);
    }

    /**
     * 格式化 {@code Date}对象，使用{@code GregorianCalendar}（格里高利历）
     * @param date 要格式化的日期对象
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final Date date) {
        final Calendar c = newCalendar();
        c.setTime(date);
        return applyRulesToString(c);
    }

    /**
     * 格式化 {@code Calendar}对象
     * @param calendar 要格式化的日历对象
     * @return 格式化后的日期时间字符串
     */
    @Override
    public String format(final Calendar calendar) {
        return format(calendar, new StringBuilder(maxLengthEstimate)).toString();
    }

    /**
     * 格式化毫秒{@code long}，并将结果追加到字符缓冲器 {@code StringBuffer}中
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param millis 要格式化的毫秒值t
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        final Calendar c = newCalendar();
        c.setTimeInMillis(millis);
        return applyRules(c, buf);
    }

    /**
     * 格式化日期对象{@code Date}，并将结果追加到字符缓冲器 {@code StringBuffer}中。
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param date 要格式化的日期对象
     * @param 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        final Calendar c = newCalendar();
        c.setTime(date);
        return applyRules(c, buf);
    }

    /**
     * 格式化日历对象{@code Calendar}，并将结果追加到字符缓冲器 {@code StringBuffer}中。
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param calendar 要格式化的日历对象
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    @Override
    public <B extends Appendable> B format(Calendar calendar, final B buf) {
        // do not pass in calendar directly, this will cause TimeZone of FastDatePrinter to be ignored
        if (!calendar.getTimeZone().equals(timeZone)) {
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(timeZone);
        }
        return applyRules(calendar, buf);
    }

    /**
     * 通过将规则应用于指定的日历来执行格式设置
     * @param <B> 字符缓冲器{@code Appendable}的类型，通常是StringBuilder或StringBuffer
     * @param calendar 要格式化的日历对象
     * @param buf 要追加内容的字符缓冲器
     * @return 追加内容的字符缓冲器
     */
    private <B extends Appendable> B applyRules(final Calendar calendar, final B buf) {
        try {
            for (final Rule rule : rules) {
                rule.appendTo(buf, calendar);
            }
        } catch (final IOException ioe) {
            throw ExceptionUtil.propagate(ioe);
        }
        return buf;
    }

    /**
     * 通过将日期打印器的规则应用于给定日历，创建该日历的字符串表示形式
     * @param calendar 要格式化的日历对象
     * @return 给定日历的字符串表示
     */
    private String applyRulesToString(final Calendar calendar) {
        return applyRules(calendar, new StringBuilder(maxLengthEstimate)).toString();
    }

    /**
     * 创建方法 {@code Calendar}对象，使用 FastDatePrinter 的时区和地区
     * @return {@code Calendar}对象实例
     */
    private Calendar newCalendar() {
        return Calendar.getInstance(timeZone, locale);
    }

    // =================================AccessorsMethods=======================================
    /**
     * 获取日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @return 日期格式
     */
    @Override
    public String getPattern() {
        return pattern;
    }

    /**
     * 获得所使用的时区
     * @return {@link TimeZone}时区
     */
    @Override
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 获得所使用的地区
     * @return {@link TimeZone}地区
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * 获取将生成字符串最大长度的估计值。<br>
     * 实际格式化生成的日期字符串长度总是小于或等于这个数值。
     * @return 格式化后的日期字符串最大长度
     */
    public int getMaxLengthEstimate() {
        return maxLengthEstimate;
    }

    // =================================BasicMethods===========================================
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        final FastDatePrinter other = (FastDatePrinter) obj;
        return pattern.equals(other.pattern) && timeZone.equals(other.timeZone) && locale.equals(other.locale);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode() + 13 * (timeZone.hashCode() + 13 * locale.hashCode());
    }

    @Override
    public String toString() {
        return "FastDatePrinter[" + pattern + "," + locale + "," + timeZone.getID() + "]";
    }

    // =================================SerializingMethods=====================================
    /**
     * 在序列化后创建对象，此实现重新初始化瞬态属性。
     * @param in 对象流，从中反序列化对象
     * @throws IOException 如果存在IO问题
     * @throws ClassNotFoundException 如果找不到对应类
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    // =================================Internals==============================================
    /** 内部工具类 */
    private static class Helper {

        private static final ConcurrentMap<TimeZoneDisplayKey, String> TIME_ZONE_DISPLAY_CACHE = new ConcurrentHashMap<>(7);

        /**
         * 获取显示名称
         * @param timeZone 时区
         * @param daylight 是否夏令时
         * @param style 样式，{@code TimeZone.LONG} 或者{@code TimeZone.SHORT}
         * @param locale 地区
         * @return 时区的文本名称
         */
        static String getTimeZoneDisplay(final TimeZone timeZone, final boolean daylight, final int style, final Locale locale) {
            final TimeZoneDisplayKey key = new TimeZoneDisplayKey(timeZone, daylight, style, locale);
            String value = TIME_ZONE_DISPLAY_CACHE.get(key);
            if (value == null) {
                // This is a very slow call, so cache the results.
                value = timeZone.getDisplayName(daylight, style, locale);
                final String prior = TIME_ZONE_DISPLAY_CACHE.putIfAbsent(key, value);
                if (prior != null) {
                    value = prior;
                }
            }
            return value;
        }

        /**
         * 将两位数字追加到给定的缓冲区（此处使用了一个优化的算法）
         * @param buffer 字符缓冲区
         * @param value 追加的数值
         */
        private static void appendDigits(final Appendable buffer, final int value) throws IOException {
            buffer.append((char) (value / 10 + '0'));
            buffer.append((char) (value % 10 + '0'));
        }

        /**
         * 将所有数字追加到给定的字符缓冲器中
         * @param buffer 字符缓冲器中
         * @param value 要从添加的数字的值
         * @param minFieldWidth 字段最小宽度，如果宽度不够会补0
         */
        private static void appendFullDigits(final Appendable buffer, int value, int minFieldWidth) throws IOException {
            // specialized paths for 1 to 4 digits -> avoid the memory allocation from the temporary work array
            // see LANG-1248
            if (value < 10000) {
                // less memory allocation path works for four digits or less

                int nDigits = 4;
                if (value < 1000) {
                    --nDigits;
                    if (value < 100) {
                        --nDigits;
                        if (value < 10) {
                            --nDigits;
                        }
                    }
                }
                // left zero pad
                for (int i = minFieldWidth - nDigits; i > 0; --i) {
                    buffer.append('0');
                }

                switch (nDigits) {
                case 4:
                    buffer.append((char) (value / 1000 + '0'));
                    value %= 1000;
                case 3:
                    if (value >= 100) {
                        buffer.append((char) (value / 100 + '0'));
                        value %= 100;
                    } else {
                        buffer.append('0');
                    }
                case 2:
                    if (value >= 10) {
                        buffer.append((char) (value / 10 + '0'));
                        value %= 10;
                    } else {
                        buffer.append('0');
                    }
                case 1:
                    buffer.append((char) (value + '0'));
                }
            } else {
                // more memory allocation path works for any digits

                // build up decimal representation in reverse
                final char[] work = new char[MAX_DIGITS];
                int digit = 0;
                while (value != 0) {
                    work[digit++] = (char) (value % 10 + '0');
                    value = value / 10;
                }

                // pad with zeros
                while (digit < minFieldWidth) {
                    buffer.append('0');
                    --minFieldWidth;
                }

                // reverse
                while (--digit >= 0) {
                    buffer.append(work[digit]);
                }
            }
        }
    }

    // =================================Rules==================================================
    /**
     * 规则
     */
    private interface Rule {
        /**
         * 返回结果的估计长度
         * @return 估计长度
         */
        int estimateLength();

        /**
         * 根据规则实现将指定日历的值追加到字符缓冲器中
         * @param buf 字符缓冲器
         * @param calendar 日历对象
         * @throws IOException 如果发生IO异常
         */
        void appendTo(Appendable buf, Calendar calendar) throws IOException;
    }

    /**
     * 数字处理规则接口类
     */
    private interface NumberRule extends Rule {
        /**
         * 根据规则实现将指定数字追加到字符缓冲器中
         * @param buf 字符缓冲器
         * @param value 充要添加到字符缓冲器的数字
         * @throws IOException 如果发生IO异常
         */
        void appendTo(Appendable buffer, int value) throws IOException;
    }

    /**
     * 输出单个字符常量的规则
     */
    private static class CharacterLiteral implements Rule {
        private final char value;

        CharacterLiteral(final char value) {
            this.value = value;
        }

        @Override
        public int estimateLength() {
            return 1;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(value);
        }
    }

    /**
     * 用于输出常量字符串的规则
     */
    private static class StringLiteral implements Rule {
        private final String value;

        StringLiteral(final String value) {
            this.value = value;
        }

        @Override
        public int estimateLength() {
            return value.length();
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(value);
        }
    }

    /**
     * 用于从一组字符串中查找到一个字符串输出的规则
     */
    private static class TextField implements Rule {
        private final int field;
        private final String[] values;

        TextField(final int field, final String[] values) {
            this.field = field;
            this.values = values;
        }

        @Override
        public int estimateLength() {
            int max = 0;
            for (int i = values.length; --i >= 0;) {
                final int len = values[i].length();
                if (len > max) {
                    max = len;
                }
            }
            return max;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            buffer.append(values[calendar.get(field)]);
        }
    }

    /**
     * 输出未填充的数字的规则
     */
    private static class UnpaddedNumberField implements NumberRule {
        private final int field;

        UnpaddedNumberField(final int field) {
            this.field = field;
        }

        @Override
        public int estimateLength() {
            return 4;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(field));
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + '0'));
            } else if (value < 100) {
                Helper.appendDigits(buffer, value);
            } else {
                Helper.appendFullDigits(buffer, value, 1);
            }
        }
    }

    /**
     * 输出月份数字的规则（不做填充，如果数字是一位数，不会在前面补0）
     */
    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        UnpaddedMonthField() {
        }

        @Override
        public int estimateLength() {
            return 2;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 10) {
                buffer.append((char) (value + '0'));
            } else {
                Helper.appendDigits(buffer, value);
            }
        }
    }

    /**
     * 填充数字的规则
     */
    private static class PaddedNumberField implements NumberRule {
        private final int field;
        private final int size;

        PaddedNumberField(final int field, final int size) {
            if (size < 3) {
                // Should use UnpaddedNumberField or TwoDigitNumberField.
                throw new IllegalArgumentException();
            }
            this.field = field;
            this.size = size;
        }

        @Override
        public int estimateLength() {
            return size;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(field));
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            Helper.appendFullDigits(buffer, value, size);
        }
    }

    /**
     * 输出两位数字的规则
     */
    private static class TwoDigitNumberField implements NumberRule {
        private final int field;

        TwoDigitNumberField(final int field) {
            this.field = field;
        }

        @Override
        public int estimateLength() {
            return 2;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(field));
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            if (value < 100) {
                Helper.appendDigits(buffer, value);
            } else {
                Helper.appendFullDigits(buffer, value, 2);
            }
        }
    }

    /**
     * 输出两位数的年份的规则
     */
    private static class TwoDigitYearField implements NumberRule {

        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        TwoDigitYearField() {
        }

        @Override
        public int estimateLength() {
            return 2;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.YEAR) % 100);
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            Helper.appendDigits(buffer, value % 100);
        }
    }

    /**
     * 输出两位数的月份的规则
     */
    private static class TwoDigitMonthField implements NumberRule {

        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        TwoDigitMonthField() {
        }

        @Override
        public int estimateLength() {
            return 2;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            appendTo(buffer, calendar.get(Calendar.MONTH) + 1);
        }

        @Override
        public final void appendTo(final Appendable buffer, final int value) throws IOException {
            Helper.appendDigits(buffer, value);
        }
    }

    /**
     * 用于输出小时的输出规则（按照十二小时，0~12）
     */
    private static class TwelveHourField implements NumberRule {
        private final NumberRule rule;

        TwelveHourField(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public int estimateLength() {
            return rule.estimateLength();
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            int value = calendar.get(Calendar.HOUR);
            if (value == 0) {
                value = calendar.getLeastMaximum(Calendar.HOUR) + 1;
            }
            rule.appendTo(buffer, value);
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            rule.appendTo(buffer, value);
        }
    }

    /**
     * 用于输出小时的输出规则（按照二十四，0~24）
     */
    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule rule;

        TwentyFourHourField(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public int estimateLength() {
            return rule.estimateLength();
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            int value = calendar.get(Calendar.HOUR_OF_DAY);
            if (value == 0) {
                value = calendar.getMaximum(Calendar.HOUR_OF_DAY) + 1;
            }
            rule.appendTo(buffer, value);
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            rule.appendTo(buffer, value);
        }
    }

    /**
     * 用于输出一周中第几天（周几）的规则
     */
    private static class DayInWeekField implements NumberRule {
        private final NumberRule rule;

        DayInWeekField(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public int estimateLength() {
            return rule.estimateLength();
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            final int value = calendar.get(Calendar.DAY_OF_WEEK);
            rule.appendTo(buffer, value == Calendar.SUNDAY ? 7 : value - 1);
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            rule.appendTo(buffer, value);
        }
    }

    /**
     * 用于输出某年中第几周的规则
     */
    private static class WeekYear implements NumberRule {
        private final NumberRule rule;

        WeekYear(final NumberRule rule) {
            this.rule = rule;
        }

        @Override
        public int estimateLength() {
            return rule.estimateLength();
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            rule.appendTo(buffer, calendar.getWeekYear());
        }

        @Override
        public void appendTo(final Appendable buffer, final int value) throws IOException {
            rule.appendTo(buffer, value);
        }
    }

    // -----------------------------------------------------------------------

    /**
     * 时区格式化规则，用于输出时区名称
     */
    private static class TimeZoneNameRule implements Rule {
        private final Locale locale;
        private final int style;
        private final String standard;
        private final String daylight;

        /**
         * 构造时区格式化规则
         * @param timeZone 时区
         * @param locale 地区
         * @param style 日期时间样式
         */
        TimeZoneNameRule(final TimeZone timeZone, final Locale locale, final int style) {
            this.locale = LocaleUtil.defaultLocale(locale);
            this.style = style;
            this.standard = Helper.getTimeZoneDisplay(timeZone, false, style, locale);
            this.daylight = Helper.getTimeZoneDisplay(timeZone, true, style, locale);
        }

        @Override
        public int estimateLength() {
            // We have no access to the Calendar object that will be passed to
            // appendTo so base estimate on the TimeZone passed to the
            // constructor
            return Math.max(standard.length(), daylight.length());
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            final TimeZone zone = calendar.getTimeZone();
            if (calendar.get(Calendar.DST_OFFSET) == 0) {
                buffer.append(Helper.getTimeZoneDisplay(zone, false, style, locale));
            } else {
                buffer.append(Helper.getTimeZoneDisplay(zone, true, style, locale));
            }
        }
    }

    /**
     * 时区格式化规则，用于输出 {@code +/-HHMM} 或者 {@code +/-HH:MM}这类数字形式的规则
     */
    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);

        final boolean colon;

        /**
         * 时区格式化规则
         * @param colon 是否添加冒号。如果为{@code true}，则会在HH和MM之间添加冒号
         */
        TimeZoneNumberRule(final boolean colon) {
            this.colon = colon;
        }

        @Override
        public int estimateLength() {
            return 5;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {

            int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);

            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }

            final int hours = offset / (60 * 60 * 1000);
            Helper.appendDigits(buffer, hours);

            if (colon) {
                buffer.append(':');
            }

            final int minutes = offset / (60 * 1000) - 60 * hours;
            Helper.appendDigits(buffer, minutes);
        }
    }

    /**
     * 时区格式化规则，用于输出{@code +/-HHMM} 或者{@code +/-HH:MM}这类数字形式的时区。
     */
    private static class ISO8601Rule implements Rule {

        // Sign TwoDigitHours or Z
        static final ISO8601Rule ISO8601_HOURS = new ISO8601Rule(3);
        // Sign TwoDigitHours Minutes or Z
        static final ISO8601Rule ISO8601_HOURS_MINUTES = new ISO8601Rule(5);
        // Sign TwoDigitHours : Minutes or Z
        static final ISO8601Rule ISO8601_HOURS_COLON_MINUTES = new ISO8601Rule(6);

        /**
         * 工厂方法
         * @param tokenLen 指示要格式化的时区字符串的长度
         * @return 时区格式化规则
         * @throws IllegalArgumentException 可以处理长度为{@code tokenLen}的时区格式，如果不存在这样的规则，将抛出异常
         */
        static ISO8601Rule getRule(final int tokenLen) {
            switch (tokenLen) {
            case 1:
                return ISO8601_HOURS;
            case 2:
                return ISO8601_HOURS_MINUTES;
            case 3:
                return ISO8601_HOURS_COLON_MINUTES;
            default:
                throw new IllegalArgumentException("invalid number of X");
            }
        }

        final int length;

        ISO8601Rule(final int length) {
            this.length = length;
        }

        @Override
        public int estimateLength() {
            return length;
        }

        @Override
        public void appendTo(final Appendable buffer, final Calendar calendar) throws IOException {
            int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
            if (offset == 0) {
                buffer.append("Z");
                return;
            }

            if (offset < 0) {
                buffer.append('-');
                offset = -offset;
            } else {
                buffer.append('+');
            }

            final int hours = offset / (60 * 60 * 1000);
            Helper.appendDigits(buffer, hours);

            if (length < 5) {
                return;
            }

            if (length == 6) {
                buffer.append(':');
            }

            final int minutes = offset / (60 * 1000) - 60 * hours;
            Helper.appendDigits(buffer, minutes);
        }
    }

    // ----------------------------------------------------------------------
    /**
     * 充当时区名称的复合键的内部类
     */
    private static class TimeZoneDisplayKey {

        private final TimeZone timeZone;
        private final int style;
        private final Locale locale;

        /**
         * 构造具有指定属性的{@code TimeZoneDisplayKey}的实例。
         * @param timeZone 时区
         * @param daylight 是否调整夏令时的样式
         * @param style 时区样式
         * @param locale 地区
         */
        TimeZoneDisplayKey(final TimeZone timeZone, final boolean daylight, final int style, final Locale locale) {
            this.timeZone = timeZone;
            if (daylight) {
                this.style = style | 0x80000000;
            } else {
                this.style = style;
            }
            this.locale = LocaleUtil.defaultLocale(locale);
        }

        @Override
        public int hashCode() {
            return (style * 31 + locale.hashCode()) * 31 + timeZone.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TimeZoneDisplayKey) {
                final TimeZoneDisplayKey other = (TimeZoneDisplayKey) obj;
                return timeZone.equals(other.timeZone) && style == other.style && locale.equals(other.locale);
            }
            return false;
        }
    }
}
