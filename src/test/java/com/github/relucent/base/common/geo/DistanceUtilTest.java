package com.github.relucent.base.common.geo;

import org.junit.Assert;
import org.junit.Test;

public class DistanceUtilTest {
	@Test
	public void testEllipsoidalDistance() {
		double s1 = DistanceUtil.getEllipsoidalDistance(39.941D, 116.45D, 39.94D, 116.451D);
		Assert.assertTrue(Math.abs(s1 - 140.11) < 1);

		double s2 = DistanceUtil.getEllipsoidalDistance(39.91, 116.40, 31.24, 121.50);
		Assert.assertTrue(Math.abs(s2 - 1068215.34) < 1500);

		double s3 = DistanceUtil.getEllipsoidalDistance(31.24, 121.50, 40.689225, -74.0445);
		Assert.assertTrue(Math.abs(s3 - 11858629.44) < 15000);
	}
}