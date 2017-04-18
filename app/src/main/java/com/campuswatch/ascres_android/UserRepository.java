package com.campuswatch.ascres_android;

import android.content.SharedPreferences;

import com.campuswatch.ascres_android.models.User;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class UserRepository {

    private static final String USER_DATA = "user";

    public static boolean isEmergency;
    public static String alertID;
    private SharedPreferences prefs;
    private User user;

    public UserRepository(SharedPreferences prefs) {
        this.prefs = prefs;
        loadUser();
    }

    public void setUser(User user){
        this.user = user;
        saveUser();
    }

    public User getUser(){
        return user;
    }

    private void saveUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_DATA, user.serialize());
        editor.apply();
    }

    private void loadUser() {
        user = User.create(prefs.getString(USER_DATA, null));
    }

}
