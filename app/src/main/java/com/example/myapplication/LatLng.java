package com.example.myapplication;

import java.io.Serializable;

/**
 * My personal LatLng class
 */

public class LatLng implements Serializable{
    private Double latitude;
    private Double longitude;

    public LatLng() {}

    public LatLng(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

   public Double getLatitude(){ return this.latitude; }
   public Double getLongitude(){ return this.longitude; }
}