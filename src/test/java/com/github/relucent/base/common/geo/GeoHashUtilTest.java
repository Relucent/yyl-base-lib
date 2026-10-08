package com.github.relucent.base.common.geo;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link GeoHashUtil} 测试
 */
public class GeoHashUtilTest {

    /**
     * 验证 encode/decode 往返一致，覆盖常规坐标与精确边界坐标
     */
    @Test
    public void testEncodeDecodeRoundTrip() {
        double[][] points = { //
                { 116.45, 39.94 }, { 0, 0 }, { -179, 0 }, { -179.9, 0 }, { 179, 0 }, //
                { -90, 45 }, { 10, -10 }, { 121.50, 31.24 }, { -74.0445, 40.689225 }, //
                // 精确边界（修复前 decode 静默返回 0,0）
                { -180, 0 }, { 0, -90 }, { 180, 0 }, { 0, 90 }, { -179.999, 0 }, { 0, -89.999 } //
        };
        for (double[] p : points) {
            Coordinate c = new Coordinate();
            c.setLongitudeX(p[0]);
            c.setLatitudeY(p[1]);
            String hash = GeoHashUtil.encode(c);
            Coordinate d = GeoHashUtil.decode(hash);
            Assert.assertTrue("encode/decode round-trip failed for (" + p[0] + "," + p[1] + ") -> " + hash,
                    Math.abs(d.getLongitudeX() - p[0]) < 1e-4 && Math.abs(d.getLatitudeY() - p[1]) < 1e-4);
        }
    }

    /**
     * 验证原点编码结果与自身解码自洽
     */
    @Test
    public void testEncodeKnownOrigin() {
        Coordinate c = new Coordinate();
        c.setLongitudeX(0);
        c.setLatitudeY(0);
        Assert.assertEquals("s00000000000", GeoHashUtil.encode(c));

        Coordinate d = GeoHashUtil.decode("s00000000000");
        Assert.assertTrue(Math.abs(d.getLongitudeX()) < 1e-4);
        Assert.assertTrue(Math.abs(d.getLatitudeY()) < 1e-4);
    }

    /**
     * 验证 decode 对非法字符（标准字母表排除的 a/i/l/o 及大写字母）抛出 IllegalArgumentException，而非 NullPointerException
     */
    @Test
    public void testDecodeIllegalCharacter() {
        assertIllegal("a00000000000");
        assertIllegal("A00000000000");
        assertIllegal("00000000000i");
        assertIllegal("00000000000l");
        assertIllegal("00000000000o");
    }

    private void assertIllegal(String hash) {
        try {
            GeoHashUtil.decode(hash);
            Assert.fail("Expected IllegalArgumentException for illegal geohash: " + hash);
        } catch (IllegalArgumentException e) {
            // 符合预期
        }
    }
}
