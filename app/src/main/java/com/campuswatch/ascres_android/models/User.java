package com.campuswatch.ascres_android.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private String image;

    public User(String uid, String name, String email, String phone, String image) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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
