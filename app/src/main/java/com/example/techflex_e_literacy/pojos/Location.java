package com.example.techflex_e_literacy.pojos;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
