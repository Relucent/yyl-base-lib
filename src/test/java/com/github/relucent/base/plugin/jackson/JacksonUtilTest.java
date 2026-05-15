package com.github.relucent.base.plugin.jackson;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.constant.ZoneIdConstant;
import com.github.relucent.base.common.time.DateUtil;
import com.github.relucent.base.common.time.ZoneUtil;

public class JacksonUtilTest {

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

		ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
		JacksonUtil.setHandler(JacksonHandler.DEFAULT);
	}

	@Test
	public void testEncodeAndDecode() {
		String json = JacksonUtil.encode(samples);
		Sample[] decodeds = JacksonUtil.decode(json, Sample[].class);
		Assert.assertEquals(samples.length, decodeds.length);

		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Sample decoded = decodeds[i];
			Assert.assertEquals(sample.number, decoded.number);
			Assert.assertEquals(sample.string, decoded.string);
			Assert.assertEquals(sample.date, decoded.date);
		}
		System.out.println(json);
		Mapx[] mapxs = JacksonUtil.decode(json, Mapx[].class);
		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Mapx map = mapxs[i];
//			Assert.assertEquals(sample.number, map.getLong("number"));
//			Assert.assertEquals(sample.string, map.getString("string"));
			System.out.println("---------------------");
			System.out.println("1>" + DateUtil.format(sample.date));
			System.out.println("2>" + DateUtil.format(map.getDate("date")));

			System.out.println("1~" + sample.date.getTime());
			System.out.println("2~" + map.getDate("date").getTime());

			System.out.println("---------------------\n");

			Assert.assertEquals(sample.date, map.getDate("date"));
		}

		Listx listx = JacksonUtil.decode(json, Listx.class);
		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Mapx map = listx.getMap(i);
			Assert.assertEquals(sample.number, map.getLong("number"));
			Assert.assertEquals(sample.string, map.getString("string"));
			Assert.assertEquals(sample.date, map.getDate("date"));
		}
	}

	@Test
	public void testDecodeTypeReference() {
		TypeReference<Sample[]> token = new TypeReference<Sample[]>() {
		};
		String json = JacksonUtil.encode(samples);
		Sample[] decodeds = JacksonUtil.decode(json, token);
		Assert.assertEquals(samples.length, decodeds.length);

		for (int i = 0; i < samples.length; i++) {
			Sample sample = samples[i];
			Sample decoded = decodeds[i];
			Assert.assertEquals(sample.number, decoded.number);
			Assert.assertEquals(sample.string, decoded.string);
			Assert.assertEquals(sample.date, decoded.date);
		}
	}

	private static class Sample {
		public Long number;
		public String string;
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
		public Date date;
	}
}
