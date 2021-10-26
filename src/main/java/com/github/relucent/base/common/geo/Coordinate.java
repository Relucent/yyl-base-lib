package com.github.relucent.base.common.geo;

import java.io.Serializable;

/**
 * 位置(坐标)
 * @author YYL
 */
@SuppressWarnings("serial")
public class Coordinate implements Serializable {
    // ==============================Fields============================================
    /** 纬度(latitude:y) */
    private double latitude = 0D;
    /** 经度(Longitude:x) */
    private double longitude = 0D;

    // ==============================Constructors=====================================
    public Coordinate() {
    }

    /**
     * 构造
     * @param other {@link Coordinate}
     */
    public Coordinate(Coordinate other) {
        this(other.getLatitude(), other.getLongitude());
    }

    /**
     * 构造
     * @param latitude 纬度
     * @param longitude 经度
     */
    public Coordinate(double latitude, double longitude) {
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
        if (!(obj instanceof Coordinate)) {
            return false;
        }
        Coordinate other = (Coordinate) obj;
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
