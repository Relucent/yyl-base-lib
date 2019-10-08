package com.github.relucent.base.util.json.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;

/**
 * JSON解析器
 */
class JsonTokener {

    private int index;
    private Reader reader;
    private char lastChar;
    private boolean useLastChar;
    private static final Pattern DATE_PATTERN = Pattern.compile("^new Date\\((\\d+)\\)$");

    /**
     * 构造函数
     * @param reader JSON字符流
     */
    public JsonTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.useLastChar = false;
        this.index = 0;
    }

    /**
     * 回退一个字符 .
     */
    private void back() {
        if (useLastChar || index <= 0) {
            throw syntaxError("Stepping back two steps is not supported");
        }
        index -= 1;
        useLastChar = true;
    }

    /**
     * 获取源字符串中的下一个字符
     * @return 下一个字符，如果源字符串结束则返回0
     */
    private char next() {
        if (this.useLastChar) {
            this.useLastChar = false;
            if (this.lastChar != 0) {
                this.index += 1;
            }
            return this.lastChar;
        }
        int c;
        try {
            c = this.reader.read();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

        if (c <= 0) { // End of stream
            this.lastChar = 0;
            return 0;
        }
        this.index += 1;
        this.lastChar = (char) c;
        return this.lastChar;
    }

    /**
     * 获取后续n个字符
     * @param n 需要获取的字符个数
     * @return 字符组成的字符串
     */
    private String next(int n) {
        if (n == 0) {
            return "";
        }

        char[] buffer = new char[n];
        int pos = 0;

        if (this.useLastChar) {
            this.useLastChar = false;
            buffer[0] = this.lastChar;
            pos = 1;
        }

        try {
            int len;
            while ((pos < n) && ((len = reader.read(buffer, pos, n - pos)) != -1)) {
                pos += len;
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
        this.index += pos;

        if (pos < n) {
            throw syntaxError("Substring bounds error");
        }

        this.lastChar = buffer[n - 1];
        return new String(buffer);
    }

    /**
     * 在字符串中获得下一个字符，跳过空格和注释
     * @return 下一个字符, 如果没有更多的字符返回0
     */
    private char nextClean() {
        for (;;) {
            char c = next();
            if (c == '/') {
                switch (next()) {
                    case '/':
                        do {
                            c = next();
                        } while (c != '\n' && c != '\r' && c != 0);
                        break;
                    case '*':
                        for (;;) {
                            c = next();
                            if (c == 0) {
                                throw syntaxError("Unclosed comment");
                            }
                            if (c == '*') {
                                if (next() == '/') {
                                    break;
                                }
                                back();
                            }
                        }
                        break;
                    default:
                        back();
                        return '/';
                }
            } else if (c == '#') {
                do {
                    c = next();
                } while (c != '\n' && c != '\r' && c != 0);
            } else if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     * 将字符返回到下一个关闭引号字符
     * @param 引号字符(双引号",或者单引号')
     * @return 一个字符串，如果引号未关闭则抛出异常
     */
    private String nextString(char quote) {
        char c;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            c = next();
            switch (c) {
                case 0:
                case '\n':
                case '\r':
                    throw syntaxError("Unterminated string");
                case '\\':
                    c = next();
                    switch (c) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                            sb.append((char) Integer.parseInt(next(4), 16));
                            break;
                        case 'x':
                            sb.append((char) Integer.parseInt(next(2), 16));
                            break;
                        default:
                            sb.append(c);
                    }
                    break;
                default:
                    if (c == quote) {
                        return sb.toString();
                    }
                    sb.append(c);
            }
        }
    }

    /**
     * 获取下一个值。该值可以是布尔型，数字，字符串，List，Map或null对象
     * @return 一个对象
     */
    private Object nextValue() {

        char c = nextClean();
        switch (c) {
            case '"':
            case '\'':
                return nextString(c);
            case '{':
                back();
                return nextMap();
            case '[':
            case '(':
                back();
                return nextList();
        }

        /* 积累字符，直到文本的结尾或格式化字符 (积累的字符可能是true,false,number,null) */
        StringBuilder buffer = new StringBuilder();
        char b = c;// initial
        // 空格(Ascii_20)之前的都是不可见字符
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            buffer.append(c);
            c = next();
        }
        back();

        String s = buffer.toString().trim();
        if (s.equals("")) {
            throw syntaxError("Missing value");
        }
        if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("null")) {
            return null;
        }

        /* 尝试将字符串转化为数字(支持十进制，八进制[0-]与十六进制[0x]转换)，如果不能转换成数字，那么当做字符串处理。 */
        if ((b >= '0' && b <= '9') || b == '-' || b == '+') {
            try {
                if (s.indexOf('.') > -1 || s.indexOf('e') > -1 || s.indexOf('E') > -1 || "-0".equals(s)) {
                    Double d = Double.valueOf(s);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long myLong = new Long((b == '0')// 0- 0x-
                            ? ((s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X'))
                                    ? Long.parseLong(s.substring(2), 16)
                                    : Long.parseLong(s, 8))
                            : Long.parseLong(s));
                    if (myLong.longValue() == myLong.intValue()) {
                        return Integer.valueOf(myLong.intValue());
                    }
                    return myLong;
                }
            } catch (Exception e) {
                /* Ignore the error */
            }
        }

        // 处理日期类型 new Date(TimeMillis)
        Matcher dateMatcher = DATE_PATTERN.matcher(s);
        if (DATE_PATTERN.matcher(s).matches()) {
            if (dateMatcher.find()) {
                String msel = dateMatcher.group(1);
                return new Date(Long.parseLong(msel));
            }
        }

        return s;
    }

    /**
     * 获取下一个值(该值是Map)
     * @return MAP对象
     */
    protected Mapx nextMap() {
        Mapx map = new Mapx();

        char c;
        String key;

        if (nextClean() != '{') {
            throw syntaxError("A JSON Object text must begin with '{'");
        }
        for (;;) {
            c = nextClean();
            switch (c) {
                case 0:
                    throw syntaxError("A JSON Object text must end with '}'");
                case '}':
                    return map;
                default:
                    back();
                    key = nextValue().toString();
            }

            /* 键值对分割符号应该是 “:”，但是也容忍 '=' 和 '=>' */
            c = nextClean();
            if (c == '=') {
                if (next() != '>') {
                    back();
                }
            } else if (c != ':') {
                throw syntaxError("Expected a ':' after a key");
            }
            map.put(key, nextValue());

            /* 容忍多余的 ,和 ; */
            switch (nextClean()) {
                case ';':
                case ',':
                    if (nextClean() == '}') {
                        return map;
                    }
                    back();
                    break;
                case '}':
                    return map;
                default:
                    throw syntaxError("Expected a ',' or '}'");
            }
        }
    }

    /**
     * 获取下一个值(该值是List)
     * @return LIST对象
     */
    protected Listx nextList() {

        Listx list = new Listx();

        char c = nextClean();
        char q;
        if (c == '[') {
            q = ']';
        } else if (c == '(') {
            q = ')';
        } else {
            throw syntaxError("A JSON Array text must start with '['");
        }
        if (nextClean() == ']') {
            return list;
        }
        back();
        for (;;) {
            if (nextClean() == ',') {
                back();
                list.add(null);
            } else {
                back();
                list.add(nextValue());
            }
            c = nextClean();
            switch (c) {
                case ';':
                case ',':
                    if (nextClean() == ']') {
                        return list;
                    }
                    back();
                    break;
                case ']':
                case ')':
                    if (q != c) {
                        throw syntaxError("Expected a '" + new Character(q) + "'");
                    }
                    return list;
                default:
                    throw syntaxError("Expected a ',' or ']'");
            }
        }
    }

    /**
     * 解析JSON字符串
     * @return JAVA对象
     */
    protected static Object decode(String json) {
        return new JsonTokener(new StringReader("[" + json + "]")).nextList().get(0);
    }

    /**
     * 抛出语法错误异常
     * @param string 异常信息
     * @return 运行时异常(其实不会真正返回，因为异常已经抛出)
     */
    private RuntimeException syntaxError(String message) {
        throw new RuntimeException(message);
    }

}
