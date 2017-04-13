package com.campuswatch.ascres_android.map;

import com.campuswatch.ascres_android.models.Report;
import com.campuswatch.ascres_android.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class MapsModel implements MapsActivityMVP.Model {

    private DatabaseReference userRef;
    private DatabaseReference alertRef;
    private DatabaseReference reportRef;
    private StorageReference imageRef;

    MapsModel() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        userRef = firebase.getReference("users");
        alertRef = firebase.getReference("alerts-test");
        reportRef = firebase.getReference("reports-test");
        imageRef = storage.getReference("images");
    }

    @Override
    public void saveUserFirebase(User user) {
        userRef.child(user.getUid()).setValue(user);
    }

    @Override
    public void sendReportFirebase(Report report) {
        reportRef.child(String.valueOf(report.getCampus())).push().setValue(report);
    }

    @Override
    public DatabaseReference getReportReference() {
        return reportRef;
    }

    @Override
    public DatabaseReference getAlertReference() {
        return alertRef;
    }

    @Override
    public DatabaseReference getUserReference() {
        return userRef;
    }

    @Override
    public StorageReference getImageRef() {
        return imageRef;
    }
}
