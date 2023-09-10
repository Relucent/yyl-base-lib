package com.github.relucent.base.common.time.format;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.lang.LocaleUtil;

/**
 * {@link java.text.SimpleDateFormat} 的线程安全版本，用于解析日期字符串并转换为 {@link Date} 对象<br>
 * JDK默认的 {code SimpleDateFormat}不是线程安全，所以在多线程环境中，创建线程独立的实例，并且不能作为静态成员多线程公用。<br>
 * 而{@code FastDatePrinter}是线程安全的，因此可以作为静态成员实例，并在多线程环境中安全使用。<br>
 * 此类在大多数格式化情况下可以直接替换{@code SimpleDateFormat}，在性能上两者基本一致。<br>
 * 参考：org.apache.commons.lang3.time.FastDateParser<br>
 */
class FastDateParser implements DateParser, Serializable {

    // =================================Static=================================================
    /** 序列化支持 */
    private static final long serialVersionUID = 1L;

    static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");

    /**
     * 用于排序正则表达式替换项的比较器<br>
     * 备选方案应先订购较长的，后订购较短的 ('february' 在'feb'之前)<br>
     * 所有条目必须按区域设置小写<br>
     */
    private static final Comparator<String> LONGER_FIRST_LOWERCASE = Comparator.reverseOrder();

    // =================================Fields=================================================
    private final String pattern;
    private final TimeZone timeZone;
    private final Locale locale;
    private final int century;
    private final int startYear;

    private transient List<StrategyAndWidth> patterns;

    // =================================Constructors===========================================
    /**
     * 构造函数
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     */
    protected FastDateParser(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    /**
     * 构造函数
     * @param pattern 日期格式（与{@link java.text.SimpleDateFormat}兼容）
     * @param timeZone {@link TimeZone} 时区
     * @param locale {@link Locale} 地区
     * @param centuryStart 世纪开始时间， 100年期间的开始用作2位数年份解析的“默认世纪”。如果centuryStart为null，则默认为now-80年
     */
    protected FastDateParser(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        this.pattern = pattern;
        this.timeZone = timeZone;
        this.locale = LocaleUtil.defaultLocale(locale);

        final Calendar definingCalendar = Calendar.getInstance(timeZone, this.locale);

        final int centuryStartYear;
        if (centuryStart != null) {
            definingCalendar.setTime(centuryStart);
            centuryStartYear = definingCalendar.get(Calendar.YEAR);
        } else if (this.locale.equals(JAPANESE_IMPERIAL)) {
            centuryStartYear = 0;
        } else {
            // 从80年前到现在的20年
            definingCalendar.setTime(new Date());
            centuryStartYear = definingCalendar.get(Calendar.YEAR) - 80;
        }
        century = centuryStartYear / 100 * 100;
        startYear = centuryStartYear - century;

        init(definingCalendar);
    }

    /**
     * 通过定义字段初始化派生字段，在构造函数和readObject调用的（反序列化）
     * @param definingCalendar {@link java.util.Calendar} 用于初始化此FastDateParser的实例
     */
    private void init(final Calendar definingCalendar) {

        patterns = new ArrayList<>();

        final StrategyParser fm = new StrategyParser(pattern, locale, definingCalendar);
        for (;;) {
            final StrategyAndWidth field = fm.getNextStrategy();
            if (field == null) {
                break;
            }
            patterns.add(field);
        }
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

    // =================================SerializingMethods=====================================
    /**
     * 在序列化后创建对象，此实现重新初始化瞬态属性。
     * @param in 对象流，从中反序列化对象
     * @throws IOException 如果存在IO问题
     * @throws ClassNotFoundException 如果找不到对应类
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
        init(definingCalendar);
    }

    // =================================ParseMethods===========================================
    @Override
    public Object parseObject(final String source) throws ParseException {
        return parse(source);
    }

    @Override
    public Date parse(final String source) throws ParseException {
        final ParsePosition pp = new ParsePosition(0);
        final Date date = parse(source, pp);
        if (date == null) {
            // Add a note re supported date range
            if (locale.equals(JAPANESE_IMPERIAL)) {
                throw new ParseException("(The " + locale + " locale does not support dates before 1868 AD)\n" + "Unparseable date: \"" + source,
                        pp.getErrorIndex());
            }
            throw new ParseException("Unparseable date: " + source, pp.getErrorIndex());
        }
        return date;
    }

    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return parse(source, pos);
    }

    /**
     * 将字符串解析成日期<br>
     * 如果解析失败，则将错误索引设置为失败字段之前的位置，这与 {@link java.text.SimpleDateFormat#parse(String, ParsePosition)}存在差异。<br>
     * （SimpleDateFormat 会将错误索引设置为失败字段之后的位置）<br>
     * 若要确定解析是否成功，调用程序必须检查{@link ParsePosition#getIndex()}给定的当前解析位置是否已更新。<br>
     * 如果输入缓冲区已经被完全解析，那么索引则索引将指向输入字符串的末尾之后<br>
     * @param source 需要解析的日期字符串
     * @param pos 解析位置记录
     * @return 解析后的日期对象
     */
    @Override
    public Date parse(final String source, final ParsePosition pos) {
        // timing tests indicate getting new instance is 19% faster than cloning
        final Calendar cal = Calendar.getInstance(timeZone, locale);
        cal.clear();

        return parse(source, pos, cal) ? cal.getTime() : null;
    }

    /**
     * 将字符串解析成日期。<br>
     * 使用已解析的字段更新日历。<br>
     * 成功后，将更新ParsePosition索引，以指示消耗了多少源文本。并非所有源文本都需要使用。<br>
     * 如果解析失败，ParsePosition错误索引将更新为与提供的格式不匹配的源文本的偏移量。<br>
     * @param source 需要解析的日期字符串
     * @param pos 解析位置记录。输入时，在源中的位置开始解析，输出时，更新位置
     * @param calendar要 在其中设置已解析字段的日历
     * @return 解析成功返回{@code true}，否则返回{@code false}
     * @throws IllegalArgumentException 当日历设置为严格模式，并且解析的字段超出范围时抛出异常
     */
    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        final ListIterator<StrategyAndWidth> lt = patterns.listIterator();
        while (lt.hasNext()) {
            final StrategyAndWidth strategyAndWidth = lt.next();
            final int maxWidth = strategyAndWidth.getMaxWidth(lt);
            if (!strategyAndWidth.strategy.parse(this, calendar, source, pos, maxWidth)) {
                return false;
            }
        }
        return true;
    }

    // =================================OtherMethods===========================================
    /**
     * 将日期调整到适当的世纪内
     * @param twoDigitYear 调整年份
     * @return 介于 centuryStart（包含） centuryStart+100（不包含）之间的值
     */
    private int adjustYear(final int twoDigitYear) {
        final int trial = century + twoDigitYear;
        return twoDigitYear >= startYear ? trial : trial + 100;
    }

    // =================================BasicMethods===========================================
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof FastDateParser)) {
            return false;
        }
        final FastDateParser other = (FastDateParser) obj;
        return pattern.equals(other.pattern) && timeZone.equals(other.timeZone) && locale.equals(other.locale);
    }

    @Override
    public int hashCode() {
        return pattern.hashCode() + 13 * (timeZone.hashCode() + 13 * locale.hashCode());
    }

    @Override
    public String toString() {
        return "FastDateParser [pattern=" + pattern //
                + ", timeZone=" + timeZone //
                + ", locale=" + locale //
                + ", century=" + century //
                + ", startYear=" + startYear //
                + "]";
    }

    // =================================Internals==============================================

    /** 内部工具类 */
    private static class Helper {

        private static final Strategy NUMBER_MONTH_STRATEGY = new NumberStrategy(Calendar.MONTH) {
            @Override
            int modify(final FastDateParser parser, final int iValue) {
                return iValue - 1;
            }
        };
        private static final Strategy LITERAL_YEAR_STRATEGY = new NumberStrategy(Calendar.YEAR);
        private static final Strategy WEEK_OF_YEAR_STRATEGY = new NumberStrategy(Calendar.WEEK_OF_YEAR);
        private static final Strategy WEEK_OF_MONTH_STRATEGY = new NumberStrategy(Calendar.WEEK_OF_MONTH);
        private static final Strategy DAY_OF_YEAR_STRATEGY = new NumberStrategy(Calendar.DAY_OF_YEAR);
        private static final Strategy DAY_OF_MONTH_STRATEGY = new NumberStrategy(Calendar.DAY_OF_MONTH);
        private static final Strategy DAY_OF_WEEK_STRATEGY = new NumberStrategy(Calendar.DAY_OF_WEEK) {
            @Override
            int modify(final FastDateParser parser, final int iValue) {
                return iValue == 7 ? Calendar.SUNDAY : iValue + 1;
            }
        };
        private static final Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new NumberStrategy(Calendar.DAY_OF_WEEK_IN_MONTH);
        private static final Strategy HOUR_OF_DAY_STRATEGY = new NumberStrategy(Calendar.HOUR_OF_DAY);
        private static final Strategy HOUR24_OF_DAY_STRATEGY = new NumberStrategy(Calendar.HOUR_OF_DAY) {
            @Override
            int modify(final FastDateParser parser, final int iValue) {
                return iValue == 24 ? 0 : iValue;
            }
        };

        private static final Strategy HOUR12_STRATEGY = new NumberStrategy(Calendar.HOUR) {
            @Override
            int modify(final FastDateParser parser, final int iValue) {
                return iValue == 12 ? 0 : iValue;
            }
        };
        private static final Strategy HOUR_STRATEGY = new NumberStrategy(Calendar.HOUR);
        private static final Strategy MINUTE_STRATEGY = new NumberStrategy(Calendar.MINUTE);
        private static final Strategy SECOND_STRATEGY = new NumberStrategy(Calendar.SECOND);
        private static final Strategy MILLISECOND_STRATEGY = new NumberStrategy(Calendar.MILLISECOND);

        /** 每个字段的解析策略缓存（因为字段是有限的，所以可以用数组长度是固定的） */
        @SuppressWarnings("unchecked")
        private static final ConcurrentMap<Locale, Strategy>[] STRATEGY_CACHES = new ConcurrentMap[Calendar.FIELD_COUNT];

        /**
         * 获取特定字段的解析策略缓存（地区和策略的映射表）
         * @param field 日历字段
         * @return 段的解析策略缓存
         */
        static ConcurrentMap<Locale, Strategy> getCache(final int field) {
            synchronized (STRATEGY_CACHES) {
                if (STRATEGY_CACHES[field] == null) {
                    STRATEGY_CACHES[field] = new ConcurrentHashMap<>(3);
                }
                return STRATEGY_CACHES[field];
            }
        }

        /**
         * 构造针对字段的解析策略
         * @param field 日历的字段
         * @param locale 地区
         * @param definingCalendar 日历对象，用于解析日期字符串
         * @return 针对字段的解析策略
         */
        static Strategy getLocaleSpecificStrategy(final int field, final Locale locale, final Calendar definingCalendar) {
            final ConcurrentMap<Locale, Strategy> cache = Helper.getCache(field);
            Strategy strategy = cache.get(locale);
            if (strategy == null) {
                strategy = field == Calendar.ZONE_OFFSET ? new TimeZoneStrategy(locale)
                        : new CaseInsensitiveTextStrategy(field, definingCalendar, locale);
                final Strategy inCache = cache.putIfAbsent(locale, strategy);
                if (inCache != null) {
                    return inCache;
                }
            }
            return strategy;
        }

        /**
         * 获得日历字段的所有名称的映射以及其locale(本地语言环境)相应的字段，并将匹配模式加到正则中<br>
         * @param calendar 用于获取数值的日历对象
         * @param locale 显示名称的地区
         * @param field 插入的字段
         * @param regex 要构建的正则表达式
         * @return 字符串显示名称到字段值的映射
         */
        static Map<String, Integer> appendDisplayNames(final Calendar calendar, Locale locale, final int field, final StringBuilder regex) {
            final Map<String, Integer> values = new HashMap<>();
            locale = LocaleUtil.defaultLocale(locale);
            final Map<String, Integer> displayNames = calendar.getDisplayNames(field, Calendar.ALL_STYLES, locale);
            final TreeSet<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE);
            for (final Map.Entry<String, Integer> displayName : displayNames.entrySet()) {
                final String key = displayName.getKey().toLowerCase(locale);
                if (sorted.add(key)) {
                    values.put(key, displayName.getValue());
                }
            }
            for (final String symbol : sorted) {
                simpleQuote(regex, symbol).append('|');
            }
            return values;
        }

        /**
         * 根据日期格式获得字段解析策略
         * @param f 日期格式的子序列
         * @param width 解析宽度
         * @param locale 地区
         * @param definingCalendar 日历（会在其中设置已解析的字段）
         * @return 用于字段解析的策略
         */
        static Strategy getStrategy(final char f, final int width, final Locale locale, final Calendar definingCalendar) {
            switch (f) {
            default:
                throw new IllegalArgumentException("Format '" + f + "' not supported");
            case 'D':
                return DAY_OF_YEAR_STRATEGY;
            case 'E':
                return getLocaleSpecificStrategy(Calendar.DAY_OF_WEEK, locale, definingCalendar);
            case 'F':
                return DAY_OF_WEEK_IN_MONTH_STRATEGY;
            case 'G':
                return getLocaleSpecificStrategy(Calendar.ERA, locale, definingCalendar);
            case 'H': // Hour in day (0-23)
                return HOUR_OF_DAY_STRATEGY;
            case 'K': // Hour in am/pm (0-11)
                return HOUR_STRATEGY;
            case 'M':
                return width >= 3 ? getLocaleSpecificStrategy(Calendar.MONTH, locale, definingCalendar) : NUMBER_MONTH_STRATEGY;
            case 'S':
                return MILLISECOND_STRATEGY;
            case 'W':
                return WEEK_OF_MONTH_STRATEGY;
            case 'a':
                return getLocaleSpecificStrategy(Calendar.AM_PM, locale, definingCalendar);
            case 'd':
                return DAY_OF_MONTH_STRATEGY;
            case 'h': // Hour in am/pm (1-12), i.e. midday/midnight is 12, not 0
                return HOUR12_STRATEGY;
            case 'k': // Hour in day (1-24), i.e. midnight is 24, not 0
                return HOUR24_OF_DAY_STRATEGY;
            case 'm':
                return MINUTE_STRATEGY;
            case 's':
                return SECOND_STRATEGY;
            case 'u':
                return DAY_OF_WEEK_STRATEGY;
            case 'w':
                return WEEK_OF_YEAR_STRATEGY;
            case 'y':
            case 'Y':
                return width > 2 ? LITERAL_YEAR_STRATEGY : ABBREVIATED_YEAR_STRATEGY;
            case 'X':
                return ISO8601TimeZoneStrategy.getStrategy(width);
            case 'Z':
                if (width == 2) {
                    return ISO8601TimeZoneStrategy.ISO_8601_3_STRATEGY;
                }
                //$FALL-THROUGH$
            case 'z':
                return getLocaleSpecificStrategy(Calendar.ZONE_OFFSET, locale, definingCalendar);
            }
        }

        /**
         * 用于将文本经过正则转义处理加入到字符缓冲器中
         * @param buf 字符缓冲器 （用于构建正则表达式）
         * @param value 添加的值
         * @return 字符缓冲器
         */
        static StringBuilder simpleQuote(final StringBuilder buf, final String value) {
            for (int i = 0; i < value.length(); ++i) {
                final char c = value.charAt(i);
                switch (c) {
                case '\\':
                case '^':
                case '$':
                case '.':
                case '|':
                case '?':
                case '*':
                case '+':
                case '(':
                case ')':
                case '[':
                case '{':
                    buf.append('\\');
                default:
                    buf.append(c);
                }
            }
            if (buf.charAt(buf.length() - 1) == '.') {
                // trailing '.' is optional
                buf.append('?');
            }
            return buf;
        }
    }

    // =================================Strategies=============================================
    /**
     * 策略分析器，用于获取相关策略
     */
    private static class StrategyParser {
        private final String pattern;
        private final Locale locale;
        private final Calendar definingCalendar;
        private int currentIdx;

        StrategyParser(final String pattern, final Locale locale, final Calendar definingCalendar) {
            this.pattern = pattern;
            this.locale = locale;
            this.definingCalendar = definingCalendar;
        }

        /**
         * 获得下一个解析策略
         * @return 解析策略工具类
         */
        StrategyAndWidth getNextStrategy() {
            if (currentIdx >= pattern.length()) {
                return null;
            }

            final char c = pattern.charAt(currentIdx);
            if (isFormatLetter(c)) {
                return letterPattern(c);
            }
            return literal();
        }

        /**
         * 获得解析策略工具类
         * @param c 需要解析的字母
         * @return 解析策略工具类
         */
        private StrategyAndWidth letterPattern(final char c) {
            final int begin = currentIdx;
            while (++currentIdx < pattern.length()) {
                if (pattern.charAt(currentIdx) != c) {
                    break;
                }
            }
            final int width = currentIdx - begin;
            return new StrategyAndWidth(Helper.getStrategy(c, width, locale, definingCalendar), width);
        }

        /**
         * 获得字面值的解析策略工具类
         * @return 字面值的解析策略工具类
         */
        private StrategyAndWidth literal() {
            boolean activeQuote = false;

            final StringBuilder sb = new StringBuilder();
            while (currentIdx < pattern.length()) {
                final char c = pattern.charAt(currentIdx);
                if (!activeQuote && isFormatLetter(c)) {
                    break;
                } else if (c == '\'' && (++currentIdx == pattern.length() || pattern.charAt(currentIdx) != '\'')) {
                    activeQuote = !activeQuote;
                    continue;
                }
                ++currentIdx;
                sb.append(c);
            }

            if (activeQuote) {
                throw new IllegalArgumentException("Unterminated quote");
            }

            final String formatField = sb.toString();
            return new StrategyAndWidth(new CopyQuotedStrategy(formatField), formatField.length());
        }

        /**
         * 是否是格式字符
         * @param c 字符
         * @return 如果是格式字符则返回{@code true}
         */
        private static boolean isFormatLetter(final char c) {
            return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
        }
    }

    /**
     * 包含解析策略和字段宽度的工具类
     */
    private static class StrategyAndWidth {

        final Strategy strategy;
        final int width;

        StrategyAndWidth(final Strategy strategy, final int width) {
            this.strategy = strategy;
            this.width = width;
        }

        int getMaxWidth(final ListIterator<StrategyAndWidth> lt) {
            if (!strategy.isNumber() || !lt.hasNext()) {
                return 0;
            }
            final Strategy nextStrategy = lt.next().strategy;
            lt.previous();
            return nextStrategy.isNumber() ? width : 0;
        }

        @Override
        public String toString() {
            return "StrategyAndWidth [strategy=" + strategy + ", width=" + width + "]";
        }
    }

    /**
     * 解析单个字段的策略接口类
     */
    private abstract static class Strategy {

        /**
         * 字段是否是数字。
         * @return 如果字段是数字，则返回{@code true}
         */
        boolean isNumber() {
            return false;
        }

        /**
         * 将字符串解析成日期。<br>
         * 使用已解析的字段更新日历。<br>
         * 成功后，将更新ParsePosition索引，以指示消耗了多少源文本。并非所有源文本都需要使用。<br>
         * 如果解析失败，ParsePosition错误索引将更新为与提供的格式不匹配的源文本的偏移量。<br>
         * @param source 需要解析的日期字符串
         * @param pos 解析位置记录。输入时，在源中的位置开始解析，输出时，更新位置。
         * @param calendar要 在其中设置已解析字段的日历。
         * @return 解析成功返回{@code true}，否则返回{@code false}
         * @throws IllegalArgumentException 当日历设置为严格模式，并且解析的字段超出范围时抛出异常
         * @param parser 日期解析器
         * @param calendar 日历（用于在其中设置已解析的字段）
         * @param source 需要解析的日期字符串
         * @param pos 解析位置记录
         * @param maxWidth 最大宽度
         * @return 是否解析成功
         */
        abstract boolean parse(FastDateParser parser, Calendar calendar, String source, ParsePosition pos, int maxWidth);
    }

    /**
     * 解析单个字段的策略抽象类（包含正则规则）<br>
     */
    private abstract static class PatternStrategy extends Strategy {

        Pattern pattern;

        void createPattern(final StringBuilder regex) {
            createPattern(regex.toString());
        }

        void createPattern(final String regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Override
        boolean isNumber() {
            return false;
        }

        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            final Matcher matcher = pattern.matcher(source.substring(pos.getIndex()));
            if (!matcher.lookingAt()) {
                pos.setErrorIndex(pos.getIndex());
                return false;
            }
            pos.setIndex(pos.getIndex() + matcher.end(1));
            setCalendar(parser, calendar, matcher.group(1));
            return true;
        }

        abstract void setCalendar(FastDateParser parser, Calendar calendar, String value);

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [pattern=" + pattern + "]";
        }
    }

    /**
     * 引用字段解析策略
     */
    private static class CopyQuotedStrategy extends Strategy {

        private final String formatField;

        CopyQuotedStrategy(final String formatField) {
            this.formatField = formatField;
        }

        @Override
        boolean isNumber() {
            return false;
        }

        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            for (int idx = 0; idx < formatField.length(); ++idx) {
                final int sIdx = idx + pos.getIndex();
                if (sIdx == source.length()) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
                if (formatField.charAt(idx) != source.charAt(sIdx)) {
                    pos.setErrorIndex(sIdx);
                    return false;
                }
            }
            pos.setIndex(formatField.length() + pos.getIndex());
            return true;
        }

        @Override
        public String toString() {
            return "CopyQuotedStrategy [formatField=" + formatField + "]";
        }
    }

    /**
     * 文本字段解析策略
     */
    private static class CaseInsensitiveTextStrategy extends PatternStrategy {
        private final int field;
        final Locale locale;
        private final Map<String, Integer> lKeyValues;

        CaseInsensitiveTextStrategy(final int field, final Calendar definingCalendar, final Locale locale) {
            this.field = field;
            this.locale = LocaleUtil.defaultLocale(locale);

            final StringBuilder regex = new StringBuilder();
            regex.append("((?iu)");
            lKeyValues = Helper.appendDisplayNames(definingCalendar, locale, field, regex);
            regex.setLength(regex.length() - 1);
            regex.append(")");
            createPattern(regex);
        }

        @Override
        void setCalendar(final FastDateParser parser, final Calendar calendar, final String value) {
            final String lowerCase = value.toLowerCase(locale);
            Integer iVal = lKeyValues.get(lowerCase);
            if (iVal == null) {
                // match missing the optional trailing period
                iVal = lKeyValues.get(lowerCase + '.');
            }
            calendar.set(field, iVal.intValue());
        }

        @Override
        public String toString() {
            return "CaseInsensitiveTextStrategy [field=" + field + ", locale=" + locale + ", lKeyValues=" + lKeyValues + ", pattern=" + pattern + "]";
        }
    }

    /**
     * 数字字段解析策略
     */
    private static class NumberStrategy extends Strategy {

        private final int field;

        NumberStrategy(final int field) {
            this.field = field;
        }

        @Override
        boolean isNumber() {
            return true;
        }

        @Override
        boolean parse(final FastDateParser parser, final Calendar calendar, final String source, final ParsePosition pos, final int maxWidth) {
            int idx = pos.getIndex();
            int last = source.length();

            if (maxWidth == 0) {
                // if no maxWidth, strip leading white space
                for (; idx < last; ++idx) {
                    final char c = source.charAt(idx);
                    if (!Character.isWhitespace(c)) {
                        break;
                    }
                }
                pos.setIndex(idx);
            } else {
                final int end = idx + maxWidth;
                if (last > end) {
                    last = end;
                }
            }

            for (; idx < last; ++idx) {
                final char c = source.charAt(idx);
                if (!Character.isDigit(c)) {
                    break;
                }
            }

            if (pos.getIndex() == idx) {
                pos.setErrorIndex(idx);
                return false;
            }

            final int value = Integer.parseInt(source.substring(pos.getIndex(), idx));
            pos.setIndex(idx);

            calendar.set(field, modify(parser, value));
            return true;
        }

        int modify(final FastDateParser parser, final int value) {
            return value;
        }

        @Override
        public String toString() {
            return "NumberStrategy [field=" + field + "]";
        }
    }

    private static final Strategy ABBREVIATED_YEAR_STRATEGY = new NumberStrategy(Calendar.YEAR) {
        @Override
        int modify(final FastDateParser parser, final int iValue) {
            return iValue < 100 ? parser.adjustYear(iValue) : iValue;
        }
    };

    /**
     * 处理解析模式中时区字段的策略<br>
     */
    static class TimeZoneStrategy extends PatternStrategy {
        private static final String RFC_822_TIME_ZONE = "[+-]\\d{4}";
        private static final String GMT_OPTION = GmtTimeZone.GMT_ID + "[+-]\\d{1,2}:\\d{2}";

        private final Locale locale;
        private final Map<String, TzInfo> tzNames = new HashMap<>();

        /** 区域ID索引 */
        private static final int ID = 0;

        private static class TzInfo {
            final TimeZone zone;
            final int dstOffset;

            TzInfo(final TimeZone tz, final boolean useDst) {
                zone = tz;
                dstOffset = useDst ? tz.getDSTSavings() : 0;
            }
        }

        TimeZoneStrategy(final Locale locale) {
            this.locale = LocaleUtil.defaultLocale(locale);

            final StringBuilder sb = new StringBuilder();
            sb.append("((?iu)" + RFC_822_TIME_ZONE + "|" + GMT_OPTION);

            final Set<String> sorted = new TreeSet<>(LONGER_FIRST_LOWERCASE);

            final String[][] zones = DateFormatSymbols.getInstance(locale).getZoneStrings();
            for (final String[] zoneNames : zones) {
                // offset 0 is the time zone ID and is not localized
                final String tzId = zoneNames[ID];
                if (tzId.equalsIgnoreCase(GmtTimeZone.GMT_ID)) {
                    continue;
                }
                final TimeZone tz = TimeZone.getTimeZone(tzId);
                // offset 1 is long standard name
                // offset 2 is short standard name
                final TzInfo standard = new TzInfo(tz, false);
                TzInfo tzInfo = standard;
                for (int i = 1; i < zoneNames.length; ++i) {
                    switch (i) {
                    case 3: // offset 3 is long daylight savings (or summertime) name
                            // offset 4 is the short summertime name
                        tzInfo = new TzInfo(tz, true);
                        break;
                    case 5: // offset 5 starts additional names, probably standard time
                        tzInfo = standard;
                        break;
                    default:
                        break;
                    }
                    if (zoneNames[i] != null) {
                        final String key = zoneNames[i].toLowerCase(locale);
                        // ignore the data associated with duplicates supplied in
                        // the additional names
                        if (sorted.add(key)) {
                            tzNames.put(key, tzInfo);
                        }
                    }
                }
            }
            // order the regex alternatives with longer strings first, greedy
            // match will ensure longest string will be consumed
            for (final String zoneName : sorted) {
                Helper.simpleQuote(sb.append('|'), zoneName);
            }
            sb.append(")");
            createPattern(sb);
        }

        @Override
        void setCalendar(final FastDateParser parser, final Calendar calendar, final String timeZone) {
            final TimeZone tz = FastTimeZone.getGmtTimeZone(timeZone);
            if (tz != null) {
                calendar.setTimeZone(tz);
            } else {
                final String lowerCase = timeZone.toLowerCase(locale);
                TzInfo tzInfo = tzNames.get(lowerCase);
                if (tzInfo == null) {
                    // match missing the optional trailing period
                    tzInfo = tzNames.get(lowerCase + '.');
                }
                calendar.set(Calendar.DST_OFFSET, tzInfo.dstOffset);
                calendar.set(Calendar.ZONE_OFFSET, tzInfo.zone.getRawOffset());
            }
        }

        @Override
        public String toString() {
            return "TimeZoneStrategy [locale=" + locale + ", tzNames=" + tzNames + ", pattern=" + pattern + "]";
        }
    }

    /** ISO8601 时区解析策略 */
    private static class ISO8601TimeZoneStrategy extends PatternStrategy {

        // Z, +hh, -hh, +hhmm, -hhmm, +hh:mm or -hh:mm
        private static final Strategy ISO_8601_1_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}))");
        private static final Strategy ISO_8601_2_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}\\d{2}))");
        private static final Strategy ISO_8601_3_STRATEGY = new ISO8601TimeZoneStrategy("(Z|(?:[+-]\\d{2}(?::)\\d{2}))");

        ISO8601TimeZoneStrategy(final String pattern) {
            createPattern(pattern);
        }

        @Override
        void setCalendar(final FastDateParser parser, final Calendar calendar, final String value) {
            calendar.setTimeZone(FastTimeZone.getGmtTimeZone(value));
        }

        /**
         * 获得{@code ISO8601TimeZoneStrategy} 的工厂方法
         * @param tokenLen 日期格式中的时区字符串长度
         * @return 时区解析策略
         * @throws IllegalArgumentException 可以解析格式长度{@code tokenLen}的时区字符串，如果不存在这样的策略，将抛出异常
         */
        static Strategy getStrategy(final int tokenLen) {
            switch (tokenLen) {
            case 1:
                return ISO_8601_1_STRATEGY;// Z
            case 2:
                return ISO_8601_2_STRATEGY;// ZZ
            case 3:
                return ISO_8601_3_STRATEGY;// ZZZ
            default:
                throw new IllegalArgumentException("invalid number of X");
            }
        }
    }
}
