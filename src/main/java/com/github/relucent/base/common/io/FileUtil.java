package com.github.relucent.base.common.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.github.relucent.base.common.constant.CharConstant;
import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.lang.ArrayUtil;
import com.github.relucent.base.common.lang.CharSequenceUtil;
import com.github.relucent.base.common.lang.ClassLoaderUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.UrlUtil;
import com.github.relucent.base.common.regex.RegexUtil;

/**
 * 文件工具类
 */
public class FileUtil {

	// ==============================Fields===========================================
	/** 绝对路径判断正则 */
	private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?");
	private static final String CLASSPATH_URL_PREFIX = "classpath:";
	private static final String FILE_URL_PREFIX = "file:";
	// ==============================Constructors=====================================

	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected FileUtil() {
	}

	// ==============================Methods==========================================
	/**
	 * 获得系统临时目录{@link File}
	 * @return 获得系统临时目录
	 */
	public static File getTempDirectory() {
		return new File(getTempDirectoryPath());
	}

	/**
	 * 获得系统临时目录路径
	 * @return 获得系统临时目录路径
	 */
	public static String getTempDirectoryPath() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * 获取用户路径（绝对路径）
	 * @return 用户路径
	 */
	public static String getUserHomePath() {
		return System.getProperty("user.home");
	}

	/**
	 * 判断是否为文件，如果file为null，则返回false
	 * @param file 文件
	 * @return 是否为文件
	 */
	public static boolean isFile(File file) {
		return (file != null) && file.isFile();
	}

	/**
	 * 判断是否为目录，如果file为null，则返回false
	 * @param file 文件
	 * @return 是否为目录
	 */
	public static boolean isDirectory(File file) {
		return (file != null) && file.isDirectory();
	}

	// ==============================IoMethods========================================
	/**
	 * 打开文件输入流{@link FileInputStream}(用于从文件中读取数据)。<br>
	 * @param file 要打开的文件, 不能为空 {@code null}
	 * @return 文件的输入流 {@link FileOutputStream} (用于从文件中读取数据)
	 */
	public static FileInputStream openInputStream(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IoRuntimeException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IoRuntimeException("File '" + file + "' cannot be read");
			}
		} else {
			throw new IoRuntimeException("File '" + file + "' does not exist");
		}
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	/**
	 * 获得一个文件读取器，使用UTF8字符集
	 * @param file 文件
	 * @return Reader对象
	 */
	public static Reader openReader(File file) {
		return openReader(file, CharsetConstant.DEFAULT);
	}

	/**
	 * 获得一个文件读取器
	 * @param file 文件
	 * @param encoding 要使用的编码
	 * @return Reader对象
	 */
	public static Reader openReader(File file, Charset encoding) {
		return IoUtil.toReader(openInputStream(file), encoding);
	}

	/**
	 * 打开文件输出流{@link FileOutputStream}(用于将数据写入文件)。 如果文件不存在，将创建该文件；如果父目录不存在，则检查并创建父目录。<br>
	 * @param file 要打开的文件, 不能为{@code null}
	 * @return 文件输出流 {@link FileOutputStream}(用于将数据写入文件)
	 * @throws IOException 如果指定的文件是一个目录
	 * @throws IOException 如果指定的文件不可写入
	 * @throws IOException 如果需要创建父目录但创建失败
	 */
	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	/**
	 * 打开文件输出流{@link FileOutputStream}(用于将数据写入文件)。 如果文件不存在，将创建该文件；如果父目录不存在，则检查并创建父目录。<br>
	 * @param file 要打开的文件, 不能为{@code null}
	 * @param append 如果为{@code true}, 字节将被添加到文件末尾，而不是覆盖。
	 * @return 文件输出流 {@link FileOutputStream}(用于将数据写入文件)
	 */
	public static FileOutputStream openOutputStream(File file, boolean append) {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IoRuntimeException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IoRuntimeException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IoRuntimeException("Directory '" + parent + "' could not be created");
				}
			}
		}
		try {
			return new FileOutputStream(file, append);
		} catch (FileNotFoundException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	/**
	 * 将文件的内容读取为字节数组
	 * @param file 文件
	 * @return 字节数组
	 */
	public static byte[] readByteArray(File file) {
		try (InputStream input = openInputStream(file)) {
			return IoUtil.toByteArray(input);
		} catch (IOException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	/**
	 * 将文件的内容读取为字符串
	 * @param file 文件
	 * @return 字符串
	 */
	public static String readString(File file) {
		return readString(file, CharsetConstant.DEFAULT);
	}

	/**
	 * 将文件的内容读取为字符串
	 * @param file 文件
	 * @param encoding 要使用的编码
	 * @return 字符串
	 */
	public static String readString(File file, Charset encoding) {
		try (InputStream input = openInputStream(file)) {
			return IoUtil.toString(input, encoding);
		} catch (IOException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	/**
	 * 从文件中读取数据
	 * @param file 文件
	 * @return 字符串列表
	 */
	public static List<String> readLines(File file) {
		return readLines(file, CharsetConstant.DEFAULT);
	}

	/**
	 * 从文件中读取数据
	 * @param file 文件
	 * @param encoding 要使用的编码
	 * @return 字符串列表
	 */
	public static List<String> readLines(File file, Charset encoding) {
		try (Reader reader = openReader(file, encoding)) {
			return IoUtil.readLines(reader);
		} catch (IOException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	/**
	 * 将数据写入文件
	 * @param file 文件
	 * @param data 要写入的数据
	 */
	public static void writeByteArrayToFile(File file, byte[] data) {
		try (FileOutputStream output = openOutputStream(file)) {
			output.write(data);
		} catch (IOException e) {
			throw IoRuntimeException.wrap(e);
		}
	}

	// ==============================PathMethods======================================
	/**
	 * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
	 * @param path 相对ClassPath的目录或者绝对路径目录
	 * @return File
	 */
	public static File toFile(String path) {
		return path == null ? null : new File(getAbsolutePath(path));
	}

	/**
	 * 获取绝对路径，相对于ClassPath的目录<br>
	 * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/<br>
	 * 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
	 * @param path 相对路径
	 * @return 绝对路径
	 */
	public static String getAbsolutePath(String path) {
		return getAbsolutePath(path, null);
	}

	/**
	 * 获取绝对路径<br>
	 * 此方法不会判定给定路径是否有效（文件或目录存在）
	 * @param path 相对路径
	 * @param baseClass 相对路径所相对的类
	 * @return 绝对路径
	 */
	public static String getAbsolutePath(String path, Class<?> baseClass) {
		String normalPath;
		if (path == null) {
			normalPath = StringConstant.EMPTY;
		} else {
			normalPath = normalize(path);
			if (isAbsolutePath(normalPath)) {
				// 给定的路径已经是绝对路径了
				return normalPath;
			}
		}

		final URL url;
		if (baseClass == null) {
			url = ClassLoaderUtil.getClassLoader().getResource(normalPath);
		} else {
			url = baseClass.getResource(normalPath);
		}
		if (url != null) {
			// 对于jar中文件包含file:前缀，需要去掉此类前缀
			return normalize(UrlUtil.getDecodedPath(url));
		}

		// 如果资源不存在，则返回一个拼接的资源绝对路径
		final String classPath = UrlUtil.getDecodedPath(ClassLoaderUtil.getClassLoader().getResource(""));
		if (classPath == null) {
			// throw new NullPointerException("ClassPath is null !");
			// 在jar运行模式中，ClassPath有可能获取不到，此时返回原始相对路径（此时获取的文件为相对工作目录）
			return path;
		}

		// 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
		return normalize(classPath.concat(Objects.requireNonNull(path)));
	}

	/**
	 * 给定路径已经是绝对路径<br>
	 * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断<br>
	 * 绝对路径判断条件是：
	 * <ul>
	 * <li>以/开头的路径</li>
	 * <li>满足类似于 c:/xxxxx，其中祖母随意，不区分大小写</li>
	 * <li>满足类似于 d:\xxxxx，其中祖母随意，不区分大小写</li>
	 * </ul>
	 * @param path 需要检查的Path
	 * @return 是否已经是绝对路径
	 */
	public static boolean isAbsolutePath(String path) {
		if (StringUtil.isEmpty(path)) {
			return false;
		}
		return CharConstant.SLASH == path.charAt(0) || RegexUtil.match(path, PATTERN_PATH_ABSOLUTE);
	}

	/**
	 * 标准化路径
	 * @param path 原路径
	 * @return 标准化后的路径
	 */
	public static String normalize(String path) {
		if (path == null) {
			return null;
		}

		// 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
		String pathToUse = StringUtil.removePrefixIgnoreCase(path, CLASSPATH_URL_PREFIX);
		// 去除file:前缀
		pathToUse = StringUtil.removePrefixIgnoreCase(pathToUse, FILE_URL_PREFIX);

		// 识别home目录形式，并转换为绝对路径
		if (CharSequenceUtil.startWith(pathToUse, '~')) {
			pathToUse = getUserHomePath() + pathToUse.substring(1);
		}

		// 统一使用斜杠
		pathToUse = pathToUse.replaceAll("[/\\\\]+", StringConstant.SLASH);
		// 去除开头空白符
		pathToUse = StringUtil.trimStart(pathToUse);
		// 兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
		if (path.startsWith("\\\\")) {
			pathToUse = "\\" + pathToUse;
		}

		String prefix = StringConstant.EMPTY;
		int prefixIndex = pathToUse.indexOf(StringConstant.COLON);
		if (prefixIndex > -1) {
			// 可能Windows风格路径
			prefix = pathToUse.substring(0, prefixIndex + 1);
			if (CharSequenceUtil.startWith(prefix, CharConstant.SLASH)) {
				// 去除类似于/C:这类路径开头的斜杠
				prefix = prefix.substring(1);
			}
			if (!prefix.contains(StringConstant.SLASH)) {
				pathToUse = pathToUse.substring(prefixIndex + 1);
			} else {
				// 如果前缀中包含/,说明非Windows风格path
				prefix = StringConstant.EMPTY;
			}
		}
		if (pathToUse.startsWith(StringConstant.SLASH)) {
			prefix += StringConstant.SLASH;
			pathToUse = pathToUse.substring(1);
		}

		String[] pathList = StringUtil.split(pathToUse, StringConstant.SLASH);

		List<String> pathElements = new LinkedList<>();
		int tops = 0;
		String element;
		for (int i = pathList.length - 1; i >= 0; i--) {
			element = pathList[i];
			// 只处理非.的目录，即只处理非当前目录
			if (!StringConstant.DOT.equals(element)) {

				if (StringConstant.DOUBLE_DOT.equals(element)) {
					tops++;
				} else {
					if (tops > 0) {
						// 有上级目录标记时按照个数依次跳过
						tops--;
					} else {
						// Normal path element found.
						pathElements.add(0, element);
					}
				}
			}
		}

		if (tops > 0 && StringUtil.isEmpty(prefix)) {
			// 只有相对路径补充开头的..，绝对路径直接忽略之
			while (tops-- > 0) {
				// 遍历完节点发现还有上级标注（即开头有一个或多个..），补充之
				// Normal path element found.
				pathElements.add(0, StringConstant.DOUBLE_DOT);
			}
		}

		return prefix + StringUtil.join(pathElements, StringConstant.SLASH);
	}

	// ==============================LoopMethods======================================
	/**
	 * 递归遍历目录以及子目录中的所有文件<br>
	 * 如果提供file为文件，直接返回过滤结果
	 * @param path 当前遍历文件或目录的路径
	 * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
	 * @return 文件列表
	 */
	public static List<File> loopFiles(String path, FileFilter fileFilter) {
		return loopFiles(toFile(path), fileFilter);
	}

	/**
	 * 递归遍历目录以及子目录中的所有文件<br>
	 * 如果提供file为文件，直接返回过滤结果
	 * @param file 当前遍历文件或目录
	 * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
	 * @return 文件列表
	 */
	public static List<File> loopFiles(File file, FileFilter fileFilter) {
		return loopFiles(file, -1, fileFilter);
	}

	/**
	 * 递归遍历目录并处理目录下的文件，可以处理目录或文件：
	 * <ul>
	 * <li>非目录则直接调用{@link Consumer}处理</li>
	 * <li>目录则递归调用此方法处理</li>
	 * </ul>
	 * @param file 文件或目录，文件直接处理
	 * @param consumer 文件处理器，只会处理文件
	 */
	public static void walkFiles(File file, Consumer<File> consumer) {
		if (file.isDirectory()) {
			final File[] subFiles = file.listFiles();
			if (ArrayUtil.isNotEmpty(subFiles)) {
				for (File tmp : subFiles) {
					walkFiles(tmp, consumer);
				}
			}
		} else {
			consumer.accept(file);
		}
	}

	/**
	 * 递归遍历目录以及子目录中的所有文件<br>
	 * 如果提供file为文件，直接返回过滤结果
	 * @param file 当前遍历文件或目录
	 * @param maxDepth 遍历最大深度，-1表示遍历到没有目录为止
	 * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
	 * @return 文件列表
	 */
	public static List<File> loopFiles(File file, int maxDepth, FileFilter fileFilter) {
		return PathUtil.loopFiles(file.toPath(), maxDepth, fileFilter);
	}

	/**
	 * 递归遍历目录以及子目录中的所有文件<br>
	 * 如果用户传入相对路径，则是相对classpath的路径<br>
	 * 如："test/aaa"表示"${classpath}/test/aaa"
	 * @param path 相对ClassPath的目录或者绝对路径目录
	 * @return 文件列表
	 */
	public static List<File> loopFiles(String path) {
		return loopFiles(toFile(path));
	}

	/**
	 * 递归遍历目录以及子目录中的所有文件
	 * @param file 当前遍历文件
	 * @return 文件列表
	 */
	public static List<File> loopFiles(File file) {
		return loopFiles(file, null);
	}

	// ==============================OtherMethods=====================================
	/**
	 * 开放文件权限读写权限(rw/rw/rw)
	 * @param file 文件
	 */
	public static void chmod666(File file) {
		if (file.exists()) {
			// 读允许
			if (file.setReadable(true, false)) {
				// ignore
			}
			// 写允许
			if (file.setWritable(true, false)) {
				// ignore
			}
		}
	}
}
