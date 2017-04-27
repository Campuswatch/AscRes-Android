package com.campuswatch.ascres_android.map;

import com.campuswatch.ascres_android.models.Report;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class MapsModel implements MapsActivityMVP.Model {

    private DatabaseReference alertRef;
    private DatabaseReference reportRef;
    private StorageReference imageRef;

    MapsModel() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        alertRef = firebase.getReference("alerts");
        reportRef = firebase.getReference("reports");
        imageRef = storage.getReference("images");
    }

    @Override
    public void sendReportFirebase(Report report) {
        reportRef.push().setValue(report);
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
    public StorageReference getImageRef() {
        return imageRef;
    }
}
