package com.example.pabili.Model;

public class SukiStore {
    private double latitude;
    private double longitude;
    private String storeName;
    private double distanceAwayMeters;
    //private String description;

    public SukiStore() {

    }

    public SukiStore(String storeName, double latitude, double longitude, double distanceAwayMeters) {
        super();
        this.storeName = storeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceAwayMeters = distanceAwayMeters;
    }
    //public SukiStore(String storeName, double latitude, double longitude, double distanceAwayMeters, String description) {
    //    super();
    //    this.storeName = storeName;
    //    this.latitude = latitude;
    //    this.longitude = longitude;
    //    this.distanceAwayMeters = distanceAwayMeters;
    //    this.description = description;
    //}

    public String getStoreName(){
        return this.storeName;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public double getDistanceAwayMeters(){
        return this.distanceAwayMeters;
    }


}