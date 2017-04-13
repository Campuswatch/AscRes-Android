package com.campuswatch.ascres_android.models;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class Report {

    private double lat;
    private double lon;
    private int campus;
    private long timestamp;
    private int category;
    private String uid;

    public Report(){}

    public Report(double lat, double lon, int campus, long timestamp, int category, String uid) {

        this.lat = lat;
        this.lon = lon;
        this.campus = campus;
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

    public int getCampus() {
        return campus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCategory(){ return category; }

    public String getUid(){ return uid; }
}
