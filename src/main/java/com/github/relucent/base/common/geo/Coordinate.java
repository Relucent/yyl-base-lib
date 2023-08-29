package com.github.relucent.base.common.geo;

import java.io.Serializable;

/**
 * 经纬度坐标<br>
 * @author YYL
 */
@SuppressWarnings("serial")
public class Coordinate implements Serializable {
    // ==============================Fields============================================
    /** 经度(Longitude:x) */
    private double longitudeX = 0D;
    /** 纬度(latitude:y) */
    private double latitudeY = 0D;

    // ==============================Constructors=====================================
    public Coordinate() {
    }

    /**
     * 构造
     * @param other {@link Coordinate}
     */
    public Coordinate(Coordinate other) {
        this.longitudeX = other.getLongitudeX();
        this.latitudeY = other.getLatitudeY();
    }

    // ==============================Methods==========================================
    /**
     * 校验经纬度是否在合理范围内
     * @return 如果经纬度是否在合理范围内返回{@code true}
     */
    public boolean check() {
        return Math.abs(latitudeY) <= 90 && Math.abs(longitudeX) <= 180;
    }

    // ==============================PropertyAccessors================================
    public double getLatitudeY() {
        return latitudeY;
    }

    public void setLatitudeY(double latitude) {
        this.latitudeY = latitude;
    }

    public double getLongitudeX() {
        return longitudeX;
    }

    public void setLongitudeX(double longitude) {
        this.longitudeX = longitude;
    }

    // ==============================OverrideMethods==================================
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(longitudeX);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitudeY);
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
        if (Double.doubleToLongBits(longitudeX) != Double.doubleToLongBits(other.longitudeX)) {
            return false;
        }
        if (Double.doubleToLongBits(latitudeY) != Double.doubleToLongBits(other.latitudeY)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CoordinateXY [longitudeX=" + longitudeX + ", latitudeY=" + latitudeY + "]";
    }
}
