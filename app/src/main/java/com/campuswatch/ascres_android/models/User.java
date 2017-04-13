package com.campuswatch.ascres_android.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class User {

    public String serialize() {
        Gson gson = new GsonBuilder()
                .create();
        return gson.toJson(this);
    }

    static public User create(String serializedData) {
        Gson gson = new GsonBuilder()
                .create();
        return gson.fromJson(serializedData, User.class);
    }
}
