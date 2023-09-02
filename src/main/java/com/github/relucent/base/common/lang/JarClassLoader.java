package com.github.relucent.base.common.lang;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import com.github.relucent.base.common.exception.ExceptionUtil;
import com.github.relucent.base.common.io.FileUtil;
import com.github.relucent.base.common.net.UrlUtil;
import com.github.relucent.base.common.reflect.MethodUtil;

/**
 * 外部Jar的类加载器
 */
public class JarClassLoader extends URLClassLoader {

    // ==============================Fields===========================================
    // ...

    // ==============================StaticCreate=====================================
    /**
     * 加载Jar到ClassPath
     * @param dir jar文件或所在目录
     * @return JarClassLoader
     */
    public static JarClassLoader load(File dir) {
        final JarClassLoader loader = new JarClassLoader();
        loader.addJar(dir);// 查找加载所有jar
        loader.addURL(dir);// 查找加载所有class
        return loader;
    }

    /**
     * 加载Jar到ClassPath
     * @param jarFile jar文件或所在目录
     * @return JarClassLoader
     */
    public static JarClassLoader loadJar(File jarFile) {
        final JarClassLoader loader = new JarClassLoader();
        loader.addJar(jarFile);
        return loader;
    }

    /**
     * 加载Jar文件到指定loader中
     * @param loader {@link URLClassLoader}
     * @param jarFile 被加载的jar
     */
    public static void loadJar(URLClassLoader loader, File jarFile) {
        try {
            final Method method = MethodUtil.getMatchingMethod(URLClassLoader.class, "addURL", URL.class);
            if (method != null) {
                method.setAccessible(true);
                final List<File> jars = loopJar(jarFile);
                for (File jar : jars) {
                    MethodUtil.invoke(loader, method, jar.toURI().toURL());
                }
            }
        } catch (IOException e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 加载Jar文件到System ClassLoader中
     * @param jarFile 被加载的jar
     * @return System ClassLoader
     */
    public static URLClassLoader loadJarToSystemClassLoader(File jarFile) {
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        loadJar(urlClassLoader, jarFile);
        return urlClassLoader;
    }

    // ==============================Constructors=====================================
    /**
     * 构造
     */
    public JarClassLoader() {
        this(new URL[] {});
    }

    /**
     * 构造
     * @param urls 被加载的URL
     */
    public JarClassLoader(URL[] urls) {
        super(urls, ClassLoaderUtil.getClassLoader());
    }

    /**
     * 构造
     * @param urls 被加载的URL
     * @param classLoader 类加载器
     */
    public JarClassLoader(URL[] urls, ClassLoader classLoader) {
        super(urls, classLoader);
    }

    // ==============================Methods==========================================
    /**
     * 加载Jar文件，或者加载目录
     * @param jarFileOrDir jar文件或者jar文件所在目录
     */
    public void addJar(File jarFileOrDir) {
        if (isJarFile(jarFileOrDir)) {
            addURL(jarFileOrDir);
            return;
        }
        final List<File> jars = loopJar(jarFileOrDir);
        for (File jar : jars) {
            addURL(jar);
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    /**
     * 增加class所在目录或文件<br>
     * 如果为目录，此目录用于搜索class文件，如果为文件，需为jar文件
     * @param dir 目录
     */
    public void addURL(File dir) {
        super.addURL(UrlUtil.getURL(dir));
    }

    // ==============================PrivateMethods===================================
    /**
     * 递归获得Jar文件
     * @param file jar文件或者包含jar文件的目录
     * @return jar文件列表
     */
    private static List<File> loopJar(File file) {
        return FileUtil.loopFiles(file, JarClassLoader::isJarFile);
    }

    /**
     * 是否为jar文件
     * @param file 文件
     * @return 是否为jar文件
     */
    private static boolean isJarFile(File file) {
        if (!FileUtil.isFile(file)) {
            return false;
        }
        return file.getPath().toLowerCase().endsWith(".jar");
    }
}
