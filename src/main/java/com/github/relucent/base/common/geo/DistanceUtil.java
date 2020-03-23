package com.github.relucent.base.common.geo;

/**
 * 距离计算
 */
public class DistanceUtil {

	/** 地球赤道半径为6378137米 */
	private static final double EARTH_RADIUS = 6378137.00;

	/**
	 * 通过经纬度计算两点之间的距离(单位：米)<br>
	 * 备注：该算法与WGS84计算距离误差在 0.15% 左右（ 例如：1000千米误差在1.5千米，10000千米误差会达到150千米）。<br>
	 * @param lat1 坐标1的纬度
	 * @param lng1 坐标1的经度
	 * @param lat2 坐标2的纬度
	 * @param lng2 坐标2的经度
	 * @return 两坐标点之间的距离(单位：米)
	 */
	public static double getEllipsoidalDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double c = (2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2))));
		return Math.round(c * EARTH_RADIUS);
	}

	/**
	 * 根据角度获取弧度（ 弧度=角度×π÷180）
	 * @param degree 角度
	 * @return 弧度
	 */
	private static double rad(double degree) {
		return degree * Math.PI / 180.0;
	}
}
