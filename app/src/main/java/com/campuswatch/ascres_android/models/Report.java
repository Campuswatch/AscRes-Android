package com.campuswatch.ascres_android.models;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class Report {

    private double lat;
    private double lon;
    private long timestamp;
    private int category;
    private String uid;

    public Report(){}

    public Report(double lat, double lon, long timestamp, int category, String uid) {

        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCategory(){ return category; }

    public String getUid(){ return uid; }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
