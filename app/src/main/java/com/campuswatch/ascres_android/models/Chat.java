package com.campuswatch.ascres_android.models;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class Chat {

    private String uid;
    private String text;
    private long timestamp;
    private String alert;
    private boolean isImage;

    public Chat() {
    }

    public Chat(String uid, String text, long timestamp, String alert, boolean isImage) {
        this.uid = uid;
        this.text = text;
        this.timestamp = timestamp;
        this.alert = alert;
        this.isImage = isImage;
    }

    public String getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAlert() {
        return alert;
    }

    public boolean getIsImage() {
        return isImage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public void setImage(boolean image) {
        isImage = image;
    }
}
