package com.campuswatch.ascres_android.models;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class Alert {

    private String uid;
    private double lat;
    private double lon;
    private long timestamp;
    private boolean isDispatched;
    private boolean isEmergency;

    public Alert(){}

    public Alert(String uid, double lat, double lon, long timestamp, boolean isDispatched, boolean isEmergency) {
        this.uid = uid;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
        this.isDispatched = isDispatched;
        this.isEmergency = isEmergency;
    }

    public String getUid() {
        return uid;
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

    public boolean isDispatched() {
        return isDispatched;
    }

    public void setDispatched(boolean dispatched) {
        isDispatched = dispatched;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }
}
