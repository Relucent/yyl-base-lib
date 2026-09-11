package com.github.relucent.base.common.json;

import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.impl.DefaultJsonHandler;
import com.github.relucent.base.common.json.impl.JsonWriter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.time.DateUtil;

public class JsonUtilTest {

	private Sample[] samples;

	@Before
	public void testBefore() {
		samples = new Sample[3];
		samples[0] = new Sample();
		samples[0].number = 0L;
		samples[0].string = "hello";
		samples[0].date = new Date(0);

		samples[1] = new Sample();
		samples[1].number = Long.MAX_VALUE;
		samples[1].string = "world";
		samples[1].date = new Date((DateUtil.MAX_MILLIS / 1000) * 1000); // 时间只保留秒

		samples[2] = new Sample();
		samples[2].number = Long.MIN_VALUE;
		samples[2].string = "json";
		samples[2].date = new Date((System.currentTimeMillis() / 1000) * 1000); // 时间只保留秒

		JsonUtil.setHandler(DefaultJsonHandler.INSTANCE);
	}

	@Test
	public void testEncodeAndDecode() {
		String json = JsonUtil.encode(samples);
		Sample[] decodeds = JsonUtil.decode(json, Sample[].class);
		Assert.assertEquals(samples.length, decodeds.length);

		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Sample decoded = decodeds[i];
			Assert.assertEquals(sample.number, decoded.number);
			Assert.assertEquals(sample.string, decoded.string);
			Assert.assertEquals(sample.date, decoded.date);
		}
	}

	@Test
	public void testDecodeTypeReference() {
		TypeReference<Sample[]> token = new TypeReference<Sample[]>() {
		};
		String json = JsonUtil.encode(samples);
		Sample[] decodeds = JsonUtil.decode(json, token);
		Assert.assertEquals(samples.length, decodeds.length);

		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Sample decoded = decodeds[i];
			Assert.assertEquals(sample.number, decoded.number);
			Assert.assertEquals(sample.string, decoded.string);
			Assert.assertEquals(sample.date, decoded.date);
		}
	}

	// ==============================Encode===========================================

	@Test
	public void testEncodeNullAndEmpty() {
		Assert.assertEquals("null", JsonUtil.encode(null));
		Assert.assertEquals("\"\"", JsonUtil.encode(""));
	}

	@Test
	public void testEncodeEscape() {
		Assert.assertEquals("\"a\\\"b\\\\c\\nd\\te\"", JsonUtil.encode("a\"b\\c\nd\te"));
		// 中文等常规非 ASCII 字符不转义，保持可读
		Assert.assertEquals("\"中文\"", JsonUtil.encode("中文"));
	}

	@Test
	public void testEncodeMapKeepInsertOrder() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("zeta", 1);
		map.put("alpha", 2);
		Assert.assertEquals("{\"zeta\":1,\"alpha\":2}", JsonUtil.encode(map));
	}

	@Test
	public void testEncodeMapIgnoreNullValueByDefault() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("a", 1);
		map.put("b", null);
		// 默认忽略 null 值
		Assert.assertEquals("{\"a\":1}", JsonUtil.encode(map));
	}

	@Test
	public void testEncodeDateAsDatetime() {
		// 默认将日期编码为日期时间字符串(本地时区)
		Assert.assertEquals("\"1970-01-01 08:00:00\"", JsonUtil.encode(new Date(0)));
	}

	@Test
	public void testEncodeNaNAndInfinityAsNull() {
		// NaN/Infinity 不是合法 JSON 数值，输出 null，保证跨系统可解析
		Assert.assertEquals("null", JsonUtil.encode(Double.NaN));
		Assert.assertEquals("null", JsonUtil.encode(Double.NEGATIVE_INFINITY));
		Assert.assertEquals("null", JsonUtil.encode(Double.POSITIVE_INFINITY));
		Assert.assertEquals("null", JsonUtil.encode(Float.NaN));
		Assert.assertEquals("null", JsonUtil.encode(Float.NEGATIVE_INFINITY));
		Assert.assertEquals("null", JsonUtil.encode(Float.POSITIVE_INFINITY));
		// Map 内嵌套场景
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("d", Double.NaN);
		map.put("f", Float.POSITIVE_INFINITY);
		Assert.assertEquals("{\"d\":null,\"f\":null}", JsonUtil.encode(map));
		// 正常数值不受影响
		Assert.assertEquals("1.5", JsonUtil.encode(1.5));
		Assert.assertEquals("1.5", JsonUtil.encode(1.5f));
	}

	@Test
	public void testEncodeDateAsTimestampsWithConfig() {
		// 通过自定义配置的 Handler 将日期编码为时间戳
		JsonConfig config = new JsonConfig.Builder().setWriteDateAsTimestamps(true).build();
		DefaultJsonHandler handler = new DefaultJsonHandler(config);
		Assert.assertEquals("0", handler.encode(new Date(0)));
	}

	// ==============================Decode===========================================

	@Test
	public void testDecodeFailureReturnsNull() {
		// 非法 JSON 返回 null，不抛出异常
		Assert.assertNull(JsonUtil.decode("{bad", Object.class));
		Assert.assertNull(JsonUtil.decode("", Object.class));
		Assert.assertNull(JsonUtil.decode(null, Object.class));
	}

	@Test
	public void testDecodeDefaultValue() {
		Assert.assertEquals("DFT", JsonUtil.decode("{bad", String.class, "DFT"));
		// JSON 字面量 null 也返回默认值
		Assert.assertEquals("DFT", JsonUtil.decode("null", String.class, "DFT"));
	}

	@Test
	public void testDecodeMapAndListTypeMismatch() {
		// 类型不匹配返回 null，不抛出异常
		Assert.assertNull(JsonUtil.decodeMap("[1,2]"));
		Assert.assertNull(JsonUtil.decodeList("{\"a\":1}"));
		Assert.assertNull(JsonUtil.decodeMap("\"str\""));
	}

	@Test
	public void testDecodeMapAndList() {
		Mapx map = JsonUtil.decodeMap("{\"a\":1,\"b\":\"x\"}");
		Assert.assertEquals(Integer.valueOf(1), map.getInteger("a"));
		Assert.assertEquals("x", map.getString("b"));
		Listx list = JsonUtil.decodeList("[1,2,3]");
		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testDecodeLenientSyntax() {
		// 宽容语法：单引号字符串
		Assert.assertEquals(Integer.valueOf(1), JsonUtil.decodeMap("{'a':1}").getInteger("a"));
		// 宽容语法：无引号 key
		Assert.assertEquals(Integer.valueOf(1), JsonUtil.decodeMap("{a:1}").getInteger("a"));
		// 宽容语法：注释
		Assert.assertEquals(Integer.valueOf(1), JsonUtil.decodeMap("{ // comment\n'a':1}").getInteger("a"));
	}

	@Test
	public void testDecodeNumberLenientSemantics() {
		// JSON.org 遗留宽容语义：前导 0 的数字按八进制解析(010 -> 8)，无法解析则回退为字符串(09)
		Assert.assertEquals(Integer.valueOf(8), JsonUtil.decode("010", Object.class));
		Assert.assertEquals("09", JsonUtil.decode("09", Object.class));
		// 十六进制
		Assert.assertEquals(Integer.valueOf(16), JsonUtil.decode("0x10", Object.class));
		// 小数与负零
		Assert.assertEquals(Double.valueOf(1.0), JsonUtil.decode("1.0", Object.class));
		Assert.assertEquals(Double.valueOf(-0.0), JsonUtil.decode("-0", Object.class));
		// 超出 long 范围的整数回退为字符串
		Assert.assertEquals("99999999999999999999", JsonUtil.decode("99999999999999999999", Object.class));
	}

	@Test
	public void testDecodeNewDateSyntax() {
		// 宽容语法：new Date(millis)
		Object value = JsonUtil.decode("new Date(0)", Object.class);
		Assert.assertTrue(value instanceof Date);
		Assert.assertEquals(0L, ((Date) value).getTime());
	}

	// ==============================Handler=========================================

	@Test(expected = IllegalArgumentException.class)
	public void testSetHandlerNullRejected() {
		JsonUtil.setHandler(null);
	}

	// ==============================Pretty===========================================

	@Test
	public void testPrettyIndentFactor() {
		// indentFactor=4 时每层缩进 4 个空格(不含换行符)
		JsonConfig config = new JsonConfig.Builder().setIndentFactor(4).build();
		StringWriter writer = new StringWriter();
		Map<String, Object> inner = new LinkedHashMap<>();
		inner.put("x", 1);
		Map<String, Object> outer = new LinkedHashMap<>();
		outer.put("a", java.util.Arrays.asList("aa", "bb"));
		outer.put("b", inner);
		new JsonWriter(writer, config).writeObject(outer);
		String json = writer.toString();
		// 既有风格：闭合括号与同级内容对齐(indentFactor=4，每层 4 个空格)
		Assert.assertEquals("{"//
				+ "\n    \"a\": ["//
				+ "\n        \"aa\","//
				+ "\n        \"bb\""//
				+ "\n        ],"//
				+ "\n    \"b\": {"//
				+ "\n        \"x\": 1"//
				+ "\n        }"//
				+ "\n    }", json);
	}

	private static class Sample {
		public Long number;
		public String string;
		public Date date;
	}
}
