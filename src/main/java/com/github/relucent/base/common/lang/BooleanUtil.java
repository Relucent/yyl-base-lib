package com.github.relucent.base.common.lang;

/**
 * Boolean类型相关工具类
 */
public class BooleanUtil {

    // ==============================Fields===========================================
    /** 表示为真的字符串 */
    private static final String[] TRUE_VALUES = { "1", "T", "Y", "TRUE", "YES", "ON", "O", "是", "对", "真", "對", "√" };
    /** 表示为假的字符串 */
    private static final String[] FALSE_VALUES = { "0", "F", "N", "FALSE", "NO", "OFF", "X", "否", "错", "假", "錯", "×" };

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected BooleanUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 转换字符串为boolean值
     * @param value 字符串
     * @return boolean值
     */
    public static boolean toPrimitiveBoolean(String value) {
        if (StringUtil.isNotBlank(value)) {
            return ArrayUtil.contains(TRUE_VALUES, value.trim().toUpperCase());
        }
        return false;
    }

    /**
     * 转换字符串为Boolean对象<br>
     * 其他情况返回{@code null}
     * @param value 字符串
     * @return Boolean对象
     */
    public static Boolean toBoolean(String value) {
        if (StringUtil.isNotBlank(value)) {
            if (ArrayUtil.contains(TRUE_VALUES, value.trim().toUpperCase())) {
                return Boolean.TRUE;
            }
            if (ArrayUtil.contains(FALSE_VALUES, value.trim().toUpperCase())) {
                return Boolean.FALSE;
            }
        }
        return null;
    }
}
