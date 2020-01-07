package com.github.relucent.base.common.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.github.relucent.base.common.io.FilenameUtil;

/**
 * WEB工具类
 * @author YYL
 */
public class WebUtil {

    /**
     * 获得SessionId
     * @param request HTTP请求
     * @return SessionId
     */
    public static String getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : session.getId();
    }

    /**
     * 获得请求访问的URI
     * @param request HTTP请求
     * @return 请求的URI
     */
    public static String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        if (requestUri.toLowerCase().startsWith(contextPath.toLowerCase())) {
            String path = requestUri.substring(contextPath.length());
            return path.isEmpty() ? "/" : path;
        }
        return requestUri;
    }

    /**
     * 获得请求的URI(统一资源标识符)
     * @param request HTTP请求
     * @return 请求的URI
     */
    public static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return normalize(decodeAndCleanUriString(request, uri));
    }

    /**
     * 获得请求的上下文路径
     * @param request HTTP请求
     * @return 请求的上下文路径
     */
    public static String getContextPath(HttpServletRequest request) {
        String contextPath = (String) request.getAttribute("javax.servlet.include.context_path");
        if (contextPath == null) {
            contextPath = request.getContextPath();
        }
        if ("/".equals(contextPath)) {
            contextPath = "";
        }
        return decodeRequestString(request, contextPath);
    }

    @SuppressWarnings("deprecation")
    public static String decodeRequestString(HttpServletRequest request, String source) {
        String enc = determineEncoding(request);
        try {
            return URLDecoder.decode(source, enc);
        } catch (UnsupportedEncodingException ex) {
            return URLDecoder.decode(source);
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
     * 获得内容描述
     * @param path 文件名(或者文件路径)
     * @param request HTTP请求
     * @return 内容描述
     */
    public static String getContentDispositionFilename(String path, HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT").toLowerCase();
        String filename = FilenameUtil.getName(path);
        try {
            // firefox | chrome
            if (userAgent.indexOf("firefox") >= 0 || userAgent.indexOf("chrome") >= 0) {
                filename = "\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
            }
            // msie | safari
            else {
                filename = URLEncoder.encode(filename, "UTF-8");
                filename = filename.replace("+", "%20");
            }
        } catch (UnsupportedEncodingException e) {}
        return filename;
    }

    /**
     * 判断是否AJAX请求
     * @param request HTTP请求
     * @return 如果是AJAX请求返回true,如果不是则返回false.
     */
    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-with"));
    }

    /**
     * 向页面返回 JSON 格式数据
     * @param json JSON字符串
     * @param request HTTP请求
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    public static void writeJson(String json, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setCharacterEncoding("UTF-8");
        setNoCacheHeader(response);
        response.setContentType("application/json; charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().print(json);
    }

    /**
     * 设置不缓存
     * @param response HTTP响应
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 文件下载
     * @param file 下载的文件
     * @param request HTTP请求
     * @param response HTTP响应
     * @param mode 下载模式
     * @throws IOException IO异常
     */
    public static void download(DownloadFile file, HttpServletRequest request, HttpServletResponse response,
            DownloadMode mode) throws IOException {
        String name = file.getName();
        String contentType = file.getContentType();
        String filename = WebUtil.getContentDispositionFilename(name, request);
        String contentDisposition = mode.name() + ";filename=" + filename;
        response.setContentType(contentType);
        response.setHeader("content-disposition", contentDisposition);
        file.writeTo(response.getOutputStream());
    }


    /**
     * 规范化路径
     * @param path 路径
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
            if (index < 0) break;
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0) break;
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0) break;
            if (index == 0) return null;
            int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
        return normalized;
    }

    /**
     * 获得HTTP请求的编码格式
     * @param request HTTP请求
     * @return 请求的编码格式
     */
    private static String determineEncoding(HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = "ISO-8859-1";
        }
        return enc;
    }

    /**
     * 解码和清理URI字符串
     * @param request HTTP请求
     * @param uri URI字符串
     * @return 处理后的URI字符串
     */
    private static String decodeAndCleanUriString(HttpServletRequest request, String uri) {
        uri = decodeRequestString(request, uri);
        int semicolonIndex = uri.indexOf(';');
        return ((semicolonIndex != -1) ? uri.substring(0, semicolonIndex) : uri);
    }

    /** 文件下载模式 */
    public static enum DownloadMode {
        /** 附件 */
        attachment,
        /** 内联 */
        inline
    }

    /** 下载文件(接口类) */
    public static interface DownloadFile {
        /**
         * 获得文件名称
         * @return 文件名称
         */
        String getName();

        /**
         * 获得内容类型
         * @return 内容类型
         */
        String getContentType();

        /**
         * 将内容写到输出流
         * @param output 输出流
         */
        void writeTo(OutputStream output);

        /**
         * 获得文件长度
         * @return 文件长度
         */
        long getLength();
    }

    /** 下载文件(实现类) */
    public static class DownloadSimpleFile implements DownloadFile {
        /** 文件名称 */
        private String name;
        /** 内容类型 */
        private String contentType;
        /** 文件内容 */
        private byte[] content;
        /** 文件长度 */
        private long length;

        public DownloadSimpleFile(String name, String contentType, byte[] content, long length) {
            super();
            this.name = name;
            this.contentType = contentType;
            this.content = content;
            this.length = length;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public void writeTo(OutputStream output) {
            try {
                output.write(content);
            } catch (IOException e) {
                // Ignore
            }
        }

        @Override
        public long getLength() {
            return length;
        }
    }
}
