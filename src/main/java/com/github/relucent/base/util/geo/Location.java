package com.github.relucent.base.util.geo;

import java.io.Serializable;

/**
 * 位置(坐标)
 */
@SuppressWarnings("serial")
public class Location implements Serializable {
	//==============================Fields============================================
	/** 纬度(latitude:y) */
	private double latitude = 0D;
	/** 经度(Longitude:x) */
	private double longitude = 0D;

	// ==============================Constructors=====================================
	public Location() {
	}

	/**
	 * 构造
	 * @param other {@link Location}
	 */
	public Location(Location other) {
		this(other.getLatitude(), other.getLongitude());
	}

	/**
	 * 构造
	 * @param latitude 纬度
	 * @param longitude 经度
	 */
	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
			throw new IllegalArgumentException("The supplied coordinates [latitude=" + latitude + ", longitude=" + longitude + "] are out of range.");
		}
	}

	// ==============================PropertyAccessors================================
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	// ==============================OverrideMethods==================================
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) {
			return false;
		}
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Place [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
}
