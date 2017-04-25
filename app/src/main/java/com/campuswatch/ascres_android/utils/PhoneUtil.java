package com.campuswatch.ascres_android.utils;

/**
 * Thought of by samwyz for the most part on 4/25/17.
 */

public class PhoneUtil {

    public static String formatPhoneNumber(String number){
        number  =   number.substring(0, number.length()-4) + "-" + number.substring(number.length()-4, number.length());
        number  =   number.substring(0,number.length()-8)+")"+number.substring(number.length()-8,number.length());
        number  =   number.substring(0, number.length()-12)+"("+number.substring(number.length()-12, number.length());
        return number;
    }
}
