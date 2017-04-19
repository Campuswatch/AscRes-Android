package com.campuswatch.ascres_android.utils;

import com.campuswatch.ascres_android.R;

import static com.campuswatch.ascres_android.Constants.REPORT_ASSAULT;
import static com.campuswatch.ascres_android.Constants.REPORT_HARASSMENT;
import static com.campuswatch.ascres_android.Constants.REPORT_SUSPICIOUS;
import static com.campuswatch.ascres_android.Constants.REPORT_THEFT;
import static com.campuswatch.ascres_android.Constants.REPORT_THREAT;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class ChooserUtil {

    public static int spotChooser(int category) {
        int chosen = 0;
        switch (category) {
            case (REPORT_THREAT):
                chosen = R.drawable.spot3;
                break;
            case (REPORT_ASSAULT):
                chosen = R.drawable.spot4;
                break;
            case (REPORT_HARASSMENT):
                chosen = R.drawable.spot5;
                break;
            case (REPORT_THEFT):
                chosen = R.drawable.spot2;
                break;
            case (REPORT_SUSPICIOUS):
                chosen = R.drawable.spot1;
                break;
        }
        return chosen;
    }

    public static String incidentChooser(int category) {
        String result = null;
        switch (category) {
            case (REPORT_THREAT):
                result = "MASS PUBLIC THREAT";
                break;
            case (REPORT_ASSAULT):
                result = "ASSAULT";
                break;
            case (REPORT_HARASSMENT):
                result = "HARASSMENT";
                break;
            case (REPORT_THEFT):
                result = "THEFT";
                break;
            case (REPORT_SUSPICIOUS):
                result = "SUSPICIOUS ACTIVITY";
                break;
        }
        return result;
    }
}
