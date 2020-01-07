package com.github.relucent.base.common.codec;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * 莫尔斯电码的编码和解码实现<br>
 * 莫尔斯电码（又译为摩斯密码，Morse code）是一种时通时断的信号代码，通过不同的排列顺序来表达不同的英文字母、数字和标点符号。<br>
 * 摩尔斯电码由三种类型的信号组成，分别为：短信号（滴）、长信号（嗒）和分隔符。三种信号通常习惯使用“.”、“-”、“/”表示。<br>
 * 摩尔斯电码有一个密码表，用来映射密码。密码表如下：（注意：字母都会转换为大写，0 为短信号，1 为长信号。）<br>
 * 参考：https://github.com/hustcc/xmorse <br>
 */
public class Morse {

    /** 默认短标记 */
    public static final char DEFAULT_DIT = '.';
    /** 默认长标记 */
    public static final char DEFAULT_DAH = '-';
    /** 默认分隔符(点和划之间的停顿) */
    public static final char DEFAULT_SPACE = '/';

    /** 映射字符表 */
    private static final char[] ALPHABETS;
    /** 映射莫尔斯电码表 */
    private static final String[] DICTIONARIES;

    static {
        // Map of Morse code patterns to supported characters.
        Map<Character, String> standard = new LinkedHashMap<>();
        // Letters
        standard.put('A', "01"); // A
        standard.put('B', "1000"); // B
        standard.put('C', "1010"); // C
        standard.put('D', "100"); // D
        standard.put('E', "0"); // E
        standard.put('F', "0010"); // F
        standard.put('G', "110"); // G
        standard.put('H', "0000"); // H
        standard.put('I', "00"); // I
        standard.put('J', "0111"); // J
        standard.put('K', "101"); // K
        standard.put('L', "0100"); // L
        standard.put('M', "11"); // M
        standard.put('N', "10"); // N
        standard.put('O', "111"); // O
        standard.put('P', "0110"); // P
        standard.put('Q', "1101"); // Q
        standard.put('R', "010"); // R
        standard.put('S', "000"); // S
        standard.put('T', "1"); // T
        standard.put('U', "001"); // U
        standard.put('V', "0001"); // V
        standard.put('W', "011"); // W
        standard.put('X', "1001"); // X
        standard.put('Y', "1011"); // Y
        standard.put('Z', "1100"); // Z
        // Numbers
        standard.put('0', "11111"); // 0
        standard.put('1', "01111"); // 1
        standard.put('2', "00111"); // 2
        standard.put('3', "00011"); // 3
        standard.put('4', "00001"); // 4
        standard.put('5', "00000"); // 5
        standard.put('6', "10000"); // 6
        standard.put('7', "11000"); // 7
        standard.put('8', "11100"); // 8
        standard.put('9', "11110"); // 9
        // Punctuation
        standard.put('.', "010101"); // Full stop
        standard.put(',', "110011"); // Comma
        standard.put('?', "001100"); // Question mark
        standard.put('\'', "011110"); // Apostrophe
        standard.put('!', "101011"); // Exclamation mark
        standard.put('/', "10010"); // Slash
        standard.put('(', "10110"); // Left parenthesis
        standard.put(')', "101101"); // Right parenthesis
        standard.put('&', "01000"); // Ampersand
        standard.put(':', "111000"); // Colon
        standard.put(';', "101010"); // Semicolon
        standard.put('=', "10001"); // Equal sign
        standard.put('+', "01010"); // Plus sign
        standard.put('-', "100001"); // Hyphen-minus
        standard.put('_', "001101"); // Low line
        standard.put('"', "010010"); // Quotation mark
        standard.put('$', "0001001"); // Dollar sign

        // 莫尔斯电码映射表
        Set<Entry<Character, String>> entrySet = standard.entrySet();
        ALPHABETS = new char[entrySet.size()];
        DICTIONARIES = new String[ALPHABETS.length];
        int index = 0;
        for (Map.Entry<Character, String> entry : entrySet) {
            ALPHABETS[index] = entry.getKey();
            DICTIONARIES[index] = entry.getValue();
            index++;;
        }
    }

    /**
     * 将文本字符串编码为莫尔斯电码
     * @param text 文本字符
     * @return 莫尔斯电码
     */
    public static final String encode(String text) {
        return encode(text, null);
    }

    /**
     * 将文本字符串编码为莫尔斯电码
     * @param text 文本字符
     * @param option 标记符设置
     * @return 莫尔斯电码
     */
    public static final String encode(String text, MarkOption option) {
        if (text == null) {
            throw new EncoderException("text can not be null");
        }
        // 字符串转大写
        text = text.toUpperCase();
        // 获得字符数组
        char[] chars = text.toCharArray();
        // 开始构建莫尔斯电码
        StringBuilder buffer = new StringBuilder();
        boolean first = true;
        // 遍历字符数组
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }
            String token = encodeChar(ch);
            if (first) {
                first = false;
            } else {
                buffer.append(DEFAULT_SPACE);
            }
            buffer.append(token);
        }

        // 规范化莫尔斯电码
        char dit = DEFAULT_DIT;
        char dah = DEFAULT_DAH;
        char space = DEFAULT_SPACE;
        if (option != null) {
            dit = option.getDit();
            dah = option.getDah();
            space = option.getSpace();
        }
        for (int index = 0; index < buffer.length(); index++) {
            char ch = buffer.charAt(index);
            switch (ch) {
                case '0':
                    buffer.setCharAt(index, dit);
                    break;
                case '1':
                    buffer.setCharAt(index, dah);
                    break;
                case DEFAULT_SPACE:
                    buffer.setCharAt(index, space);
                    break;
                default:
                    break;
            }
        }
        return buffer.toString();
    }

    /**
     * 莫尔斯电码解码
     * @param code 莫尔斯电码
     * @return 解码后的文本
     */
    public static String decode(String code) {
        return decode(code, null);
    }

    /**
     * 莫尔斯电码解码
     * @param code 莫尔斯电码
     * @param option 标记符设置
     * @return 解码后的文本
     */
    public static String decode(String code, MarkOption option) {
        if (code == null) {
            throw new DecoderException("morse code can not be null");
        }
        try {
            // 规范化莫尔斯电码
            char dit = DEFAULT_DIT;
            char dah = DEFAULT_DAH;
            char space = DEFAULT_SPACE;
            if (option != null) {
                dit = option.getDit();
                dah = option.getDah();
                space = option.getSpace();
            }
            char[] chars = code.toCharArray();
            for (int index = 0; index < chars.length; index++) {
                char ch = chars[index];
                if (ch == dit) {
                    chars[index] = '0';
                } else if (ch == dah) {
                    chars[index] = '1';
                } else if (ch == space) {
                    chars[index] = DEFAULT_SPACE;
                }
            }
            code = new String(chars);
            // 构建解码文本
            StringBuilder buffer = new StringBuilder();
            // 解析莫尔斯电码
            StringTokenizer tokenizer = new StringTokenizer(new String(chars), DEFAULT_SPACE + "");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                char ch = decodeChar(token);
                buffer.append(ch);
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new DecoderException("Decoding error,morse-code or mark-option error !", e);
        }
    }

    /**
     * 字符转莫尔斯电码
     * @param ch 字符
     * @return 莫尔斯电码
     */
    private static String encodeChar(char ch) {
        // 查找字符对应映射
        for (int i = 0; i < ALPHABETS.length; i++) {
            char alphabet = ALPHABETS[i];
            if (alphabet == ch) {
                return DICTIONARIES[i];
            }
        }
        // 没有对应映射，说明是非标准的字符，使用 UNICODE
        return Integer.toBinaryString(ch);
    }


    /**
     * 莫尔斯电码转字符
     * @param code 莫尔斯电码
     * @return 字符
     */
    private static char decodeChar(String code) {
        // 查找字符对应映射
        for (int i = 0; i < DICTIONARIES.length; i++) {
            String dictionarie = DICTIONARIES[i];
            if (dictionarie.equals(code)) {
                return ALPHABETS[i];
            }
        }
        // 没有对应映射，说明是非标准的字符，使用 UNICODE
        return (char) Integer.parseInt(code, 2);
    }

    /**
     * 标记符号设置
     */
    public static class MarkOption {

        /** 短标记或点 */
        private char dit = DEFAULT_DIT;
        /** 长标记或短划线 */
        private char dah = DEFAULT_DAH;
        /** 分隔符(点和划之间的停顿) */
        private char space = DEFAULT_SPACE;

        /**
         * 获得短标记符号
         * @return 短标记符号
         */
        public char getDit() {
            return dit;
        }

        /**
         * 设置短标记符号
         * @param dit 短标记符号
         * @return 当前对象的引用
         */
        public MarkOption setDit(char dit) {
            this.dit = dit;
            return this;
        }

        /**
         * 获得长标记符号
         * @return 长标记符号
         */
        public char getDah() {
            return dah;
        }

        /**
         * 设置长标记符号
         * @param dah 长标记符号
         * @return 当前对象的引用
         */
        public MarkOption setDah(char dah) {
            this.dah = dah;
            return this;
        }

        /**
         * 获得分隔符号
         * @return 分隔符号
         */
        public char getSpace() {
            return space;
        }

        /**
         * 设置分隔符号
         * @param space 分隔符号
         * @return 当前对象的引用
         */
        public MarkOption setSpace(char space) {
            this.space = space;
            return this;
        }
    }
}
