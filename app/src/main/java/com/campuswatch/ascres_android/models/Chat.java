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
}
