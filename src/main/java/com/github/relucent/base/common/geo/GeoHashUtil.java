package com.github.relucent.base.common.geo;

import java.text.DecimalFormat;
import java.util.BitSet;
import java.util.HashMap;

/**
 * 地理位置HAHS工具类<br>
 * Geohash 基本原理是将地球理解为一个二维平面，将平面递归分解成更小的子块，每个子块在一定经纬度范围内拥有相同的编码。这种方式，可以满足对小规模的数据进行经纬度的检索<br>
 * 纬线：地球仪上的横线，lat，赤道是最大的纬线，从赤道开始分为北纬和南纬，都是0-90°，纬线是角度数值；<br>
 * 经线：地球仪上的竖线，lng，子午线为0°，分为西经和东经，都是0-180°，经线也是角度数值；<br>
 * 经纬线和米的换算：经度或者纬度0.00001度，约等于1米，GPS只要精确到小数点后五位，就是10米范围内的精度；<br>
 * 经度0度的位置为本初子午线，在180度的位置转为西经，数字由大到小依次经过北美洲到达西欧.纬度0度的位置为赤道；<br>
 * 可以将地球看成一个基于经纬度线的坐标系<br>
 * 纬线就是平行于赤道平面的那些平面的周线，经线就是连接南北两极的大圆线的半圆弧<br>
 * 纬度分为北纬（正），南纬（负），赤道所在的纬度值为0。经度以本初子午线界（本初子午线经度为0），分为东经（正），西经（负）<br>
 * 纬度范围可表示为[-90o, 0o)，（0o, 90o]，经度范围可表示为[-180o, 0o)，（0o, 180o]<br>
 * GeoHash将二维的经纬度转换成字符串，每一个字符串代表了某一矩形区域。<br>
 * 区间[-90,90]进行二分为[-90,0)=0,[0,90]=1<br>
 * 0101 0111 1101 1111<br>
 * 0100 0110 1100 1110<br>
 * 0001 0011 1001 1011<br>
 * 0000 0010 1000 1010<br>
 * @author YYL
 */
public class GeoHashUtil {

    // ==============================Fields===========================================
    private static int NUMBITS = 6 * 5;

    // BASE32
    private static final char[] DIGITS = { //
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 0-9
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', // 10-19
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', // 20-29
            'y', 'z' // 30-31
    };

    private final static HashMap<Character, Integer> LOOKUP = new HashMap<Character, Integer>();
    static {
        for (int i = 0; i < DIGITS.length; i++) {
            LOOKUP.put(DIGITS[i], i);
        }
    }

    // ==============================Methods==========================================
    /**
     * 将GEOHASH字串解码成经纬值
     * @param geohash 待解码的GEOHASH字串
     * @return 经纬值数组
     */
    public static Location decode(String geohash) {
        StringBuilder buffer = new StringBuilder();
        for (char c : geohash.toCharArray()) {
            int i = LOOKUP.get(c) + 32;
            buffer.append(Integer.toString(i, 2).substring(1));
        }

        BitSet lonset = new BitSet();
        BitSet latset = new BitSet();

        // even bits
        for (int i = 0, j = 0; i < NUMBITS * 2; i += 2) {
            boolean isSet = false;
            if (i < buffer.length()) {
                isSet = buffer.charAt(i) == '1';
            }
            lonset.set(j++, isSet);
        }

        // odd bits
        for (int i = 1, j = 0; i < NUMBITS * 2; i += 2) {
            boolean isSet = false;
            if (i < buffer.length()) {
                isSet = buffer.charAt(i) == '1';
            }
            latset.set(j++, isSet);
        }
        double lat = decode(latset, -90, 90);
        double lon = decode(lonset, -180, 180);
        DecimalFormat df = new DecimalFormat("0.00000");

        Location location = new Location();
        location.setLatitude(Double.parseDouble(df.format(lat)));
        location.setLongitude(Double.parseDouble(df.format(lon)));
        return location;
    }

    /**
     * 根据二进制编码串和指定的数值变化范围，计算得到经/纬值
     * @param bs 经/纬二进制编码串
     * @param floor 下限
     * @param ceiling 上限
     * @return 经/纬值
     */
    private static double decode(BitSet bs, double floor, double ceiling) {
        double mid = 0;
        for (int i = 0; i < bs.length(); i++) {
            mid = (floor + ceiling) / 2;
            if (bs.get(i)) {
                floor = mid;
            } else {
                ceiling = mid;
            }
        }
        return mid;
    }

    /**
     * 根据经纬值得到 GEOHASH 字符串
     * @param location 经纬度
     * @return GEOHASH 字符串
     */
    public static String encode(Location location) {
        BitSet latbits = getBits(location.getLatitude(), -90, 90);
        BitSet lonbits = getBits(location.getLongitude(), -180, 180);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < NUMBITS; i++) {
            buffer.append((lonbits.get(i)) ? '1' : '0');
            buffer.append((latbits.get(i)) ? '1' : '0');
        }
        return base32(Long.parseLong(buffer.toString(), 2));
    }

    /**
     * 将二进制编码串转换成 GEOHASH 字串
     * @param i 二进制编码串
     * @return GEOHASH字串
     */
    private static String base32(long i) {
        char[] buf = new char[65];
        int charPos = 64;
        boolean negative = (i < 0);
        if (!negative)
            i = -i;
        while (i <= -32) {
            buf[charPos--] = DIGITS[(int) (-(i % 32))];
            i /= 32;
        }
        buf[charPos] = DIGITS[(int) (-i)];

        if (negative)
            buf[--charPos] = '-';
        return new String(buf, charPos, (65 - charPos));
    }

    /**
     * 得到经/纬度对应的二进制编码
     * @param lat 经/纬度
     * @param floor 下限
     * @param ceiling 上限
     * @return 二进制编码串
     */
    private static BitSet getBits(double lat, double floor, double ceiling) {
        BitSet buffer = new BitSet(NUMBITS);
        for (int i = 0; i < NUMBITS; i++) {
            double mid = (floor + ceiling) / 2;
            if (lat >= mid) {
                buffer.set(i);
                floor = mid;
            } else {
                ceiling = mid;
            }
        }
        return buffer;
    }
}