package com.example.techflex_e_literacy.mappojos;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("lat")
    private double lati;
    @SerializedName("lng")
    private double longi;

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
