package com.github.relucent.base.common.lang;

/**
 * 断言，断言某些对象或值是否符合规定，否则抛出异常。主要用于做变量检查。
 * @author YYL
 */
public class Assert {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected Assert() {
    }

    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object must be null");
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            fail(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this object must not be null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            fail(message);
        }
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            fail(message);
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            fail(message);
        }
    }

    public static void notEmpty(String value) {
        notEmpty(value, "[Assertion failed] - this string must not be empty");
    }

    public static void notEmpty(String value, String message) {
        if (value == null || value.length() == 0) {
            fail(message);
        }
    }

    public static void noNullElements(Object[] objects) {
        noNullElements(objects, "[Assertion failed] - this array must not contain any null objects");
    }

    public static void noNullElements(Object[] objects, String message) {
        for (Object obj : objects) {
            if (obj == null) {
                fail(message);
            }
        }
    }

    public static void fail(String message) {
        throw new IllegalArgumentException(message);
    }
}
