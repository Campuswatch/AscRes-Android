package com.campuswatch.ascres_android.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.campuswatch.ascres_android.Constants.WEEK_IN_MILLISECONDS;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class DateUtil {

    public static String convertTimestampDateTime(long milliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy '\n'h:mm a", Locale.US);
        return format.format(calendar.getTime());
    }

    public static String convertTimestampTime(long milliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
        return format.format(calendar.getTime());
    }



    public static long timeElapsedInMilliseconds(long fromWhenInMilliseconds){
        long currentTime = System.currentTimeMillis();
        return currentTime - fromWhenInMilliseconds;
    }

    public static long getTimeInMillis() {
        return System.currentTimeMillis();
    }

    public static boolean isExpired(long timestamp){
        boolean isExpired = false;
        if (timeElapsedInMilliseconds(timestamp) > WEEK_IN_MILLISECONDS){
            isExpired = true;
        } return isExpired;
    }
}
