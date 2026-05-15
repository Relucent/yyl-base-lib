package com.github.relucent.base.plugin.jackson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.JsonHandler;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.time.DateUtil;
import com.github.relucent.base.plugin.jackson.databind.BigDecimalPowerDeserializer;
import com.github.relucent.base.plugin.jackson.databind.BigDecimalPowerSerializer;
import com.github.relucent.base.plugin.jackson.databind.ListxDeserializer;
import com.github.relucent.base.plugin.jackson.databind.MapxDeserializer;

/**
 * JSON处理器 （基于JACKSON实现）
 */
public class JacksonHandler implements JsonHandler {

	// ===================================Fields==============================================
	public static final JacksonHandler DEFAULT = new JacksonHandler();
	private final Logger logger = Logger.getLogger(getClass());
	private final ObjectMapper objectMapper;
	private final ObjectWriter prettyWriter;
	private final ObjectWriter ignoreNullWriter;

	// ===================================Constructors========================================
	/**
	 * 构造函数(使用指定 {@link ObjectMapper})
	 * @param objectMapper 对象映射
	 */
	public JacksonHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper.copy();
		this.prettyWriter = objectMapper.copy().writerWithDefaultPrettyPrinter();
		this.ignoreNullWriter = objectMapper.copy().setSerializationInclusion(JsonInclude.Include.NON_NULL).writer();
	}

	/**
	 * 构造函数(默认)
	 */
	public JacksonHandler() {
		this(getDefaultObjectMapper());
	}

	// ===================================OverrideMethods=====================================
	/**
	 * 将JAVA对象编码为JSON字符串
	 * @param src JAVA对象
	 * @return 对象的JSON字符串
	 */
	@Override
	public String encode(Object src) {
		try {
			return objectMapper.writeValueAsString(src);
		} catch (Throwable e) {
			logger.warn("#", e);
			return null;
		}
	}

	/**
	 * 将JAVA对象编码为带缩进打印格式的JSON字符串对象
	 * @param value JAVA对象
	 * @return 对象的JSON字符串
	 */
	public String encodePretty(Object value) {
		try {
			return prettyWriter.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 将JAVA对象编码为带缩进打印格式的JSON字符串对象，忽略null字段
	 * @param value JAVA对象
	 * @return 对象的JSON字符串
	 */
	public String encodeIgnoreNull(Object value) {
		try {
			return ignoreNullWriter.writeValueAsString(value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 将JSON字符串解码为JAVA对象
	 * @param <T>  对象泛型
	 * @param json 对象的JSON字符串
	 * @param type JAVA对象类型
	 * @return JSON对应的JAVA对象，如果无法解析将返回NULL
	 */
	@Override
	public <T> T decode(String json, Class<T> type) {
		return decode(json, type, null);
	}

	/**
	 * 将JSON字符串，解码为JAVA对象
	 * @param <T>          对象泛型
	 * @param json         JSON字符串
	 * @param type         JAVA对象类型
	 * @param defaultValue 默认值
	 * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
	 */
	public <T> T decode(String json, Class<T> type, T defaultValue) {
		try {
			return objectMapper.readValue(json, type);
		} catch (Throwable e) {
			logger.warn("#", e);
			return defaultValue;
		}
	}

	/**
	 * 将JSON字符串，解码为JAVA对象
	 * @param <T>   对象泛型
	 * @param json  JSON字符串
	 * @param token 类型标记
	 * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
	 */
	@Override
	public <T> T decode(String json, TypeReference<T> token) {
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			JavaType valueType = typeFactory.constructType(token.getType());
			return objectMapper.readValue(json, valueType);
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 将JSON字符串，解码为Map对象(该方法依赖于JACKSON类库)
	 * @param json JSON字符串
	 * @return JSON对应的Map对象，如果无法解析将返回NULL.
	 */
	@Override
	public Mapx decodeMap(String json) {
		try {
			return JacksonConvertUtil.convertObject((ObjectNode) objectMapper.readTree(json));
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 将JSON字符串，解码为List对象
	 * @param json JSON字符串
	 * @return JSON对应的List对象，如果无法解析将返回NULL.
	 */
	@Override
	public Listx decodeList(String json) {
		try {
			return JacksonConvertUtil.convertArray((ArrayNode) objectMapper.readTree(json));
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	// ===================================Methods=============================================
	/**
	 * 将JSON对象转化为指定类型的JAVA对象
	 * @param <T>  JAVA对象泛型
	 * @param node JAVA对象（可序列化的）
	 * @param type JAVA对象类型
	 * @return 转换后的JAVA对象
	 */
	public <T> T convertValue(Object node, Class<T> type) {
		if (node == null) {
			return null;
		}
		return objectMapper.convertValue(node, type);
	}

	/**
	 * 将JSON对象转化为指定类型的JAVA对象
	 * @param <T>   JAVA对象泛型
	 * @param node  JAVA对象（可序列化的）
	 * @param token 类型标记
	 * @return 转换后的JAVA对象
	 */
	public <T> T convertValue(Object node, TypeReference<T> token) {
		if (node == null) {
			return null;
		}
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			JavaType valueType = typeFactory.constructType(token.getType());
			return objectMapper.convertValue(node, valueType);
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 将JSON对象转化为指定类型的JAVA对象
	 * @param <T>   JAVA对象泛型
	 * @param node  JAVA对象（可序列化的）
	 * @param token 类型标记
	 * @return 转换后的JAVA对象
	 */
	public <T> T convertValue(Object node, com.fasterxml.jackson.core.type.TypeReference<T> token) {
		if (node == null) {
			return null;
		}
		try {
			TypeFactory typeFactory = objectMapper.getTypeFactory();
			JavaType valueType = typeFactory.constructType(token.getType());
			return objectMapper.convertValue(node, valueType);
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 将JSON字符串，解码为JAVA对象
	 * @param <T>   对象泛型
	 * @param json  JSON字符串
	 * @param token 类型标记
	 * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
	 */
	public <T> T decode(String json, com.fasterxml.jackson.core.type.TypeReference<T> token) {
		try {
			return objectMapper.readValue(json, token);
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 将JSON字符串解码为JSON对象
	 * @param json 对象的JSON字符串
	 * @return JSON对象，如果无法解析将返回NULL
	 */
	public JsonNode decode(String json) {
		try {
			return objectMapper.readValue(json, JsonNode.class);
		} catch (Exception e) {
			logger.warn("#", e);
			return null;
		}
	}

	/**
	 * 将JSON对象转化为指定类型的JAVA对象
	 * @param <T>   JAVA对象泛型
	 * @param node  JSON对象
	 * @param token 类型标记
	 * @return 转换后的JAVA对象
	 */
	public <T> T convertValue(JsonNode node, com.fasterxml.jackson.core.type.TypeReference<T> token) {
		if (node == null) {
			return null;
		}
		try {
			return objectMapper.convertValue(node, token);
		} catch (Throwable e) {
			logger.error("#", e);
			return null;
		}
	}

	/**
	 * 创建一个 JSON对象
	 * @return JSON对象
	 */
	public ObjectNode createObjectNode() {
		return objectMapper.createObjectNode();
	}

	/**
	 * 创建一个 JSON数组对象
	 * @return JSON数组对象
	 */
	public ArrayNode createArrayNode() {
		return objectMapper.createArrayNode();
	}

	// ===================================StaticMethods=======================================
	/**
	 * 获得默认的默认的对象映射
	 * @return 默认的对象映射
	 */
	public static ObjectMapper getDefaultObjectMapper() {
		ObjectMapper om = new ObjectMapper();

		// JSON 解析特性
		om.enable(JsonParser.Feature.ALLOW_COMMENTS); // 支持注释
		om.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES); // 支持字段名不加引号
		om.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES); // 支持单引号字符串

		// 反序列化特性
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 忽略未知字段
		om.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY); // 单值解析为数组
		om.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE); // 枚举未知值用默认
		om.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE); // 禁止时区自动调整

		// 日期格式定义（不一定生效，看WRITE_DATES_AS_TIMESTAMPS是否启用）
		om.setDateFormat(new SimpleDateFormat(DateUtil.DATETIME_FORMAT));

		// 序列化特性
		om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); // 空 Bean 输出 {}
		// om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 日期输出 ISO 字符串
		om.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);// DATE输出毫秒
		om.getSerializationConfig().without(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);// 按类定义顺序

		// 时区设置
		// om.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		om.setTimeZone(TimeZone.getTimeZone("UTC"));

		// 支持结束
		om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// 反序列化忽略不需要的字段
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 自动注册 JavaTimeModule、Jdk8Module 等
		om.findAndRegisterModules();

		SimpleModule module = new SimpleModule();

		// 将大数字转换为 String 类型
		module.addSerializer(BigDecimal.class, BigDecimalPowerSerializer.INSTANCE);
		module.addDeserializer(BigDecimal.class, BigDecimalPowerDeserializer.INSTANCE);

		// 日期序列化与反序列化
		// module.addSerializer(Date.class, DatePowerSerializer.INSTANCE);
		// module.addDeserializer(Date.class, DatePowerDeserializer.INSTANCE);

		// 扩展集合类反序列化
		module.addDeserializer(Mapx.class, MapxDeserializer.INSTANCE);
		module.addDeserializer(Listx.class, ListxDeserializer.INSTANCE);

		om.registerModule(module);
		om.findAndRegisterModules();// JSR310

		return om;
	}
}
