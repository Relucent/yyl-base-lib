package com.github.relucent.base.common.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.relucent.base.common.codec.CodecUtil;
import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.lang.ArrayUtil;

/**
 * 规范化工具类
 */
public class CanonicalUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected CanonicalUtil() {
    }

    /**
     * 根据参数映射构建规范查询字符串<br>
     * 包括查询参数和表单参数两者（Servlet合并视图）<br>
     * @param parameterMap 参数
     * @return 规范查询字符串
     */
    public static String buildCanonicalQueryString(Map<String, String[]> parameterMap) {
        try {
            if (parameterMap == null || parameterMap.isEmpty()) {
                return StringConstant.EMPTY;
            }

            StringBuilder writer = new StringBuilder();

            List<String> keys = new ArrayList<>(parameterMap.keySet());
            Collections.sort(keys);

            boolean first = true;
            for (String key : keys) {
                String[] values = parameterMap.get(key);
                if (ArrayUtil.isEmpty(values)) {
                    if (!first) {
                        writer.append('&');
                    }
                    writer.append(key);
                    first = false;
                    continue;
                }
                for (String value : values) {
                    if (!first) {
                        writer.append('&');
                    }
                    first = false;
                    writer.append(key);

                    if (value != null) {
                        writer.append('=');
                        writer.append(CodecUtil.encodeUriRfc3986(value));
                    }
                }
            }
            return writer.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build canonical query string", e);
        }
    }

    /**
     * 规范化路径
     * @param path 路径
     * @return 规范化的路径
     */
    public static String normalize(String path) {
        return normalize(path, true);
    }

    /**
     * 规范化路径
     * @param path             路径
     * @param replaceBackSlash 是否替换反斜杠(\)
     * @return 规范化的路径
     */
    private static String normalize(String path, boolean replaceBackSlash) {
        if (path == null) {
            return null;
        }
        String normalized = path;
        if ((replaceBackSlash) && (normalized.indexOf('\\') >= 0)) {
            normalized = normalized.replace('\\', '/');
        }
        if (normalized.equals("/.")) {
            return "/";
        }
        if (!(normalized.startsWith("/"))) {
            normalized = "/" + normalized;
        }
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return null;
            int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
        return normalized;
    }
}
