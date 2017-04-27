package com.campuswatch.ascres_android.root;

import android.content.SharedPreferences;

import com.campuswatch.ascres_android.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Thought of by samwyz for the most part on 4/12/17.
 */

public class UserRepository {

    private static final String USER_DATA = "user";

    private DatabaseReference userRef;
    private SharedPreferences prefs;
    private User user;

    public UserRepository(SharedPreferences prefs) {
        this.userRef = FirebaseDatabase.getInstance().getReference("users");
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

        userRef.child(user.getUid()).setValue(user);
    }

    private void loadUser() {
        user = User.create(prefs.getString(USER_DATA, null));
    }

}
