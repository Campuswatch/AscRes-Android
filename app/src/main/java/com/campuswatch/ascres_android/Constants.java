package com.campuswatch.ascres_android;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class Constants {

    public static final String APP_TAG = "Ascension-Residential";
    //user shared preferences
    public static final String USER_DATA = "user";
    //location update
    public static final long UPDATE_INTERVAL = 500;
    //time in milliseconds
    public static final long WEEK_IN_MILLISECONDS = 604800000;
    //report types
    public static final int REPORT_THREAT = 0;
    public static final int REPORT_ASSAULT = 1;
    public static final int REPORT_HARASSMENT = 2;
    public static final int REPORT_THEFT = 3;
    public static final int REPORT_SUSPICIOUS = 4;
    //request code for location settings and permissions
    public static final int REQUEST_CHECK_SETTINGS = 1001;
    public static final int REQUEST_CLIENT_CONNECT = 1002;
    public static final int LOCATION_PERMISSION_REQUEST = 1003;
    //request codes for image intents and permissions
    public static final int IMAGE_GALLERY_REQUEST = 2001;
    public static final int IMAGE_CAPTURE_CODE = 2002;
    public static final int STORAGE_PERMISSION_REQUEST = 2003;
    //request code for sign in
    public static final int SIGN_IN = 3001;
}
