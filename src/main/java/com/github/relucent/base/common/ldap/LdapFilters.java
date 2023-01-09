package com.github.relucent.base.common.ldap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * LDAP过滤条件工具类
 * @author YYL
 * @version 2012-10-13
 */
public class LdapFilters {

    // ==============================Constructors=====================================
    /**
     * 工具类私有构造
     */
    protected LdapFilters() {
    }

    // ==============================Methods==========================================
    /**
     * 等于 =
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapEqFilter eq(String attribute, String value) {
        return new LdapEqFilter(attribute, value);
    }

    /**
     * 不等于 !=
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapNeFilter ne(String attribute, String value) {
        return new LdapNeFilter(attribute, value);
    }

    /**
     * 大于 &gt;
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapGtFilter gt(String attribute, String value) {
        return new LdapGtFilter(attribute, value);
    }

    /**
     * 大于等于 &gt;=
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapGeFilter ge(String attribute, String value) {
        return new LdapGeFilter(attribute, value);
    }

    /**
     * 小于 &lt;
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLtFilter lt(String attribute, String value) {
        return new LdapLtFilter(attribute, value);
    }

    /**
     * 小于等于 &lt;=
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLeFilter le(String attribute, String value) {
        return new LdapLeFilter(attribute, value);
    }

    /**
     * 相似匹配 LIKE
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLikeFilter like(String attribute, String value) {
        return new LdapLikeFilter(attribute, value);
    }

    /**
     * 相似匹配。开头匹配，相当于SQL的 like 'key%'
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLikeStartFilter likeStart(String attribute, String value) {
        return new LdapLikeStartFilter(attribute, value);
    }

    /**
     * 相似匹配。结尾匹配，相当于SQL的 like '%key'
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLikeEndFilter likeEnd(String attribute, String value) {
        return new LdapLikeEndFilter(attribute, value);
    }

    /**
     * 精确匹配相似 LIKE 允许匹配值使用*模糊匹配
     * @param attribute LDAP属性
     * @param value 匹配的值
     * @return 条件对象
     */
    public static LdapLikeExactFilter likeExact(String attribute, String value) {
        return new LdapLikeExactFilter(attribute, value);
    }

    /**
     * 非 !
     * @param filter 过滤条件
     * @return 条件对象
     */
    public static LFilterNot not(LdapFilter filter) {
        return new LFilterNot(filter);
    }

    /**
     * 与 AND &amp;
     * @return AND条件对象
     */
    public static LdapAndFilter and() {
        return new LdapAndFilter();
    }

    /**
     * 或 OR |
     * @return OR条件对象
     */
    public static LdapOrFilter or() {
        return new LdapOrFilter();
    }

    /**
     * 自定义条件
     * @param expression 表达式
     * @return 条件对象
     */
    public static LdapExpressionFilter expression(String expression) {
        return new LdapExpressionFilter(expression);
    }

    // ==============================InnerClass=======================================
    /** 查询条件类 */
    public static interface LdapFilter {

        /**
         * 获得过滤条件的文本编码
         * @return 过滤条件的文本编码
         */
        String encode();

        /**
         * 获得过滤条件的文本编码，并加入到可变字符序列中
         * @param buffer 可变字符序列
         * @return 可变字符序列
         */
        StringBuffer encode(StringBuffer buffer);
    }

    /**
     * 查询条件抽象类
     */
    protected static abstract class AbstractBaseFilter implements LdapFilter {

        public abstract StringBuffer encode(StringBuffer buffer);

        @Override
        public String encode() {
            StringBuffer buffer = new StringBuffer(256);
            buffer = encode(buffer);
            return buffer.toString();
        }

        @Override
        public String toString() {
            return encode();
        }
    }

    /**
     * 二元逻辑过滤器抽象类
     */
    protected static abstract class AbstractBinaryLogicalFilter extends AbstractBaseFilter {

        protected List<LdapFilter> childrenFilter = new LinkedList<LdapFilter>();

        public StringBuffer encode(StringBuffer buff) {
            if (this.childrenFilter.size() <= 0) {
                return buff;
            }
            if (this.childrenFilter.size() == 1) {
                LdapFilter query = (LdapFilter) this.childrenFilter.get(0);
                return query.encode(buff);
            }
            buff.append("(" + getLogicalOperator());
            for (Iterator<LdapFilter> i = this.childrenFilter.iterator(); i.hasNext();) {
                LdapFilter query = (LdapFilter) i.next();
                buff = query.encode(buff);
            }
            buff.append(")");
            return buff;
        }

        protected abstract String getLogicalOperator();

        protected final AbstractBinaryLogicalFilter append(LdapFilter query) {
            this.childrenFilter.add(query);
            return this;
        }
    }

    /**
     * 比较逻辑过滤器抽象类
     */
    static abstract class AbstractCompareFilter extends AbstractBaseFilter {

        private final String attribute;
        private final String value;
        private final String encodedValue;

        public AbstractCompareFilter(String attribute, String value) {
            this.attribute = attribute;
            this.value = value;
            this.encodedValue = encodeValue(value);
        }

        String getEncodedValue() {
            return this.encodedValue;
        }

        protected String encodeValue(String value) {
            return LdapEncoder.filterEncode(value);
        }

        public AbstractCompareFilter(String attribute, int value) {
            this.attribute = attribute;
            this.value = String.valueOf(value);
            this.encodedValue = LdapEncoder.filterEncode(this.value);
        }

        public StringBuffer encode(StringBuffer buff) {
            buff.append('(');
            buff.append(this.attribute).append(getCompareString()).append(this.encodedValue);
            buff.append(')');

            return buff;
        }

        protected abstract String getCompareString();
    }

    /**
     * AND
     */
    public static class LdapAndFilter extends AbstractBinaryLogicalFilter {
        private static final String AMPERSAND = "&";

        protected String getLogicalOperator() {
            return AMPERSAND;
        }

        public LdapAndFilter and(LdapFilter query) {
            append(query);
            return this;
        }
    }

    /**
     * OR
     */
    public static class LdapOrFilter extends AbstractBinaryLogicalFilter {
        private static final String PIPE_SIGN = "|";

        public LdapOrFilter or(LdapFilter query) {
            append(query);
            return this;
        }

        protected String getLogicalOperator() {
            return PIPE_SIGN;
        }
    }

    /**
     * EQ =
     */
    public static class LdapEqFilter extends AbstractCompareFilter {
        private static final String EQUALS_SIGN = "=";

        public LdapEqFilter(String attribute, String value) {
            super(attribute, value);
        }

        public LdapEqFilter(String attribute, int value) {
            super(attribute, value);
        }

        protected String getCompareString() {
            return EQUALS_SIGN;
        }
    }

    /**
     * 大于等于 &gt;=
     */
    public static class LdapGeFilter extends AbstractCompareFilter {
        private static final String GREATER_THAN_OR_EQUALS = ">=";

        public LdapGeFilter(String attribute, String value) {
            super(attribute, value);
        }

        public LdapGeFilter(String attribute, int value) {
            super(attribute, value);
        }

        protected String getCompareString() {
            return GREATER_THAN_OR_EQUALS;
        }
    }

    /**
     * 小于等于 &lt;=
     */
    public static class LdapLeFilter extends AbstractCompareFilter {

        private static final String LESS_THAN_OR_EQUALS = "<=";

        public LdapLeFilter(String attribute, String value) {
            super(attribute, value);
        }

        public LdapLeFilter(String attribute, int value) {
            super(attribute, value);
        }

        protected String getCompareString() {
            return LESS_THAN_OR_EQUALS;
        }
    }

    /**
     * LIKE
     */
    public static class LdapLikeFilter extends LdapEqFilter {
        public LdapLikeFilter(String attribute, String value) {
            super(attribute, value);
        }

        protected String encodeValue(String value) {
            if (value == null) {
                return "";
            }
            return "*" + LdapEncoder.filterEncode(value) + "*";
        }
    }

    /**
     * LIKE 开头匹配，相当于SQL的 like 'key%'
     */
    public static class LdapLikeStartFilter extends LdapEqFilter {
        public LdapLikeStartFilter(String attribute, String value) {
            super(attribute, value);
        }

        protected String encodeValue(String value) {
            if (value == null) {
                return "";
            }
            return LdapEncoder.filterEncode(value) + "*";
        }
    }

    /**
     * LIKE 结尾匹配，相当于SQL的 like '%key'
     */
    public static class LdapLikeEndFilter extends LdapEqFilter {

        public LdapLikeEndFilter(String attribute, String value) {
            super(attribute, value);
        }

        protected String encodeValue(String value) {
            if (value == null) {
                return "";
            }
            return "*" + LdapEncoder.filterEncode(value);
        }
    }

    /**
     * LIKE字符串精确匹配，相当于SQL的 like 'key'
     */
    public static class LdapLikeExactFilter extends LdapEqFilter {
        public LdapLikeExactFilter(String attribute, String value) {
            super(attribute, value);
        }

        protected String encodeValue(String value) {
            if (value == null) {
                return "";
            }
            String[] substrings = value.split("\\*", -2);
            if (substrings.length == 1) {
                return LdapEncoder.filterEncode(substrings[0]);
            }
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < substrings.length; i++) {
                buff.append(LdapEncoder.filterEncode(substrings[i]));
                if (i < substrings.length - 1) {
                    buff.append("*");
                } else if (!substrings[i].equals(""))
                    ;
            }
            return buff.toString();
        }
    }

    /**
     * NOT
     */
    public static class LFilterNot extends AbstractBaseFilter {
        private final LdapFilter filter;

        public LFilterNot(LdapFilter filter) {
            this.filter = filter;
        }

        public StringBuffer encode(StringBuffer buff) {
            buff.append("(!");
            this.filter.encode(buff);
            buff.append(')');
            return buff;
        }
    }

    /**
     * 大于(大于等于 且 不等于)
     */
    public static class LdapGtFilter extends AbstractBinaryLogicalFilter {

        private static final String AND = "&";

        public LdapGtFilter(String attribute, String value) {
            append(new LFilterNot(new LdapEqFilter(attribute, value)));// (!=)
            append(new LdapGeFilter(attribute, value));// (>=)

        }

        public LdapGtFilter(String attribute, int value) {
            append(new LFilterNot(new LdapEqFilter(attribute, value)));// (!=)
            append(new LdapGeFilter(attribute, value));// (>=)
        }

        @Override
        protected String getLogicalOperator() {
            return AND;
        }
    }

    /**
     * 小于(小于等于 且 不等于)
     */
    public static class LdapLtFilter extends AbstractBinaryLogicalFilter {

        private static final String AND = "&";

        public LdapLtFilter(String attribute, String value) {
            append(new LFilterNot(new LdapEqFilter(attribute, value)));// (!=)
            append(new LdapLeFilter(attribute, value));// (<=)

        }

        public LdapLtFilter(String attribute, int value) {
            append(new LFilterNot(new LdapEqFilter(attribute, value)));// (!=)
            append(new LdapLeFilter(attribute, value));// (<=)
        }

        @Override
        protected String getLogicalOperator() {
            return AND;
        }
    }

    /**
     * NE 不等于
     */
    public static class LdapNeFilter extends AbstractBaseFilter {
        private final LFilterNot filter;

        public LdapNeFilter(String attribute, String value) {
            filter = new LFilterNot(new LdapEqFilter(attribute, value));
        }

        public LdapNeFilter(String attribute, int value) {
            filter = new LFilterNot(new LdapEqFilter(attribute, value));
        }

        @Override
        public StringBuffer encode(StringBuffer buffer) {
            return filter.encode(buffer);
        }
    }

    /**
     * 自定义表达式
     */
    public static class LdapExpressionFilter extends AbstractBaseFilter {
        private final String expression;

        public LdapExpressionFilter(String expression) {
            this.expression = expression;
        }

        @Override
        public StringBuffer encode(StringBuffer buffer) {
            buffer.append(expression);
            return buffer;
        }
    }
}
