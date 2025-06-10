package com.github.relucent.base.common.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URLStreamHandler;

import com.github.relucent.base.common.codec.CodecUtil;
import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.constant.UrlConstant;
import com.github.relucent.base.common.exception.ExceptionUtil;
import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.ClassLoaderUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * URL（Uniform Resource Locator）统一资源定位符相关工具类
 */
public class UrlUtil {

    /**
     * 将{@link URI}转换为{@link URL}
     * @param uri {@link URI}
     * @return URL对象
     * @see URI#toURL()
     */
    public static URL toURL(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     * @param url URL
     * @return URL对象
     */
    public static URL toURL(String url) {
        return toURL(url, null);
    }

    /**
     * 通过一个字符串形式的URL地址创建URL对象
     * @param url     URL
     * @param handler {@link URLStreamHandler}
     * @return URL对象
     */
    public static URL toURL(String url, URLStreamHandler handler) {
        if (url == null) {
            return null;
        }

        // 兼容Spring的ClassPath路径
        if (url.startsWith(UrlConstant.CLASSPATH_URL_PREFIX)) {
            url = url.substring(UrlConstant.CLASSPATH_URL_PREFIX.length());
            return ClassLoaderUtil.getClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                throw ExceptionUtil.propagate(e);
            }
        }
    }

    /**
     * 获取string协议的URL，类似于string:///xxxxx
     * @param content 正文
     * @return URL
     */
    public static URI getStringURI(String content) {
        if (content == null) {
            return null;
        }
        if (!content.startsWith("string:///")) {
            content = "string:///" + content;
        }
        return URI.create(content);
    }

    /**
     * 将URL字符串转换为URL对象
     * @param urlStr URL字符串
     * @return URL
     */
    public static URL toUrlForHttp(String urlStr) {
        return toUrlForHttp(urlStr, null);
    }

    /**
     * 将URL字符串转换为URL对象
     * @param url     URL字符串
     * @param handler {@link URLStreamHandler}
     * @return URL
     */
    public static URL toUrlForHttp(String url, URLStreamHandler handler) {
        AssertUtil.notBlank(url, "Url is blank !");
        // 编码空白符，防止空格引起的请求异常
        url = encodeBlank(url);
        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 单独编码URL中的空白符，空白符编码为%20
     * @param url URL字符串
     * @return 编码后的字符串
     */
    public static String encodeBlank(CharSequence url) {
        if (url == null) {
            return null;
        }
        int len = url.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = url.charAt(i);
            if (' ' == c) {
                sb.append("%20");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     * @param file URL对应的文件对象
     * @return URL
     */
    public static URL getURL(File file) {
        AssertUtil.notNull(file, "File is null !");
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(File... files) {
        final URL[] urls = new URL[files.length];
        try {
            for (int i = 0; i < files.length; i++) {
                urls[i] = files[i].toURI().toURL();
            }
        } catch (MalformedURLException e) {
            throw ExceptionUtil.propagate(e);
        }

        return urls;
    }

    /**
     * 获取URL中域名部分，只保留URL中的协议（Protocol）、Host，其它为null。
     * @param url URL
     * @return 域名的URI
     */
    public static URI getHost(URL url) {
        if (url == null) {
            return null;
        }
        try {
            return new URI(url.getProtocol(), url.getHost(), null, null);
        } catch (URISyntaxException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 编码URL<br>
     * 将%开头的16进制表示的内容解码。
     * @param url URL
     * @return 编码后的URL
     * @see CodecUtil#encodeURI(String)
     */
    public static String encode(String url) {
        return CodecUtil.encodeURI(url);
    }

    /**
     * 解码URL<br>
     * 将%开头的16进制表示的内容解码。
     * @param url URL
     * @return 解码后的URL
     */
    public static String decode(String url) {
        return decode(url, CharsetConstant.UTF_8.name());
    }

    /**
     * 解码application/x-www-form-urlencoded字符<br>
     * 将%开头的16进制表示的内容解码。
     * @param content URL
     * @param charset 编码
     * @return 解码后的URL
     */
    public static String decode(String content, String charset) {
        try {
            return URLDecoder.decode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 获得path部分<br>
     * @param uriStr URI路径
     * @return path
     */
    public static String getPath(String uriStr) {
        return toURI(uriStr).getPath();
    }

    /**
     * 从URL对象中获取不被编码的路径Path<br>
     * 对于本地路径，URL对象的getPath方法对于包含中文或空格时会被编码，导致本读路径读取错误。<br>
     * 此方法将URL转为URI后获取路径用于解决路径被编码的问题
     * @param url {@link URL}
     * @return 路径
     */
    public static String getDecodedPath(URL url) {
        if (url == null) {
            return null;
        }
        // URL对象的getPath方法对于包含中文或空格的问题
        String path = toURI(url).getPath();
        return path != null ? path : url.getPath();
    }

    /**
     * 转URL为URI
     * @param url URL
     * @return URI
     */
    public static URI toURI(URL url) {
        return toURI(url, false);
    }

    /**
     * 转URL为URI
     * @param url      URL
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     */
    public static URI toURI(URL url, boolean isEncode) {
        return url == null ? null : toURI(url.toString(), isEncode);
    }

    /**
     * 转字符串为URI
     * @param location 字符串路径
     * @return URI
     */
    public static URI toURI(String location) {
        return toURI(location, false);
    }

    /**
     * 转字符串为URI
     * @param location 字符串路径
     * @param isEncode 是否编码参数中的特殊字符（默认UTF-8编码）
     * @return URI
     */
    public static URI toURI(String location, boolean isEncode) {
        if (isEncode) {
            try {
                location = URLEncoder.encode(location, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw ExceptionUtil.propagate(e);
            }
        }
        try {
            return new URI(StringUtil.trim(location));
        } catch (URISyntaxException e) {
            throw ExceptionUtil.propagate(e);
        }
    }
}
