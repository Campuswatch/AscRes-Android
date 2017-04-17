package com.campuswatch.ascres_android.map;

import android.location.Location;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.campuswatch.ascres_android.UserRepository;
import com.campuswatch.ascres_android.models.Alert;
import com.campuswatch.ascres_android.models.Report;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.utils.DateUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.campuswatch.ascres_android.UserRepository.alertID;
import static com.campuswatch.ascres_android.UserRepository.isEmergency;
import static com.campuswatch.ascres_android.utils.DateUtil.isExpired;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

class MapsPresenter implements
        MapsActivityMVP.Presenter {

    private MapsActivityMVP.Model model;
    private MapsActivityMVP.View view;
    private UserRepository repo;
    private Location location;

    MapsPresenter(MapsActivityMVP.Model model,
                  UserRepository repo) {
        this.model = model;
        this.repo = repo;
    }

    @Override
    public void setView(MapsActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public User getUser() {
        return repo.getUser();
    }

    @Override
    public void sendReport(LatLng latLng, int category) {
        Report report = new Report(latLng.latitude, latLng.longitude, 0,
                DateUtil.getTimeInMillis(), category, repo.getUser().getUid());
        model.sendReportFirebase(report);
    }

    @Override
    public void startAlerts() {
        alertID = UUID.randomUUID().toString();

        model.getAlertReference()
                .child(String.valueOf(0))
                .child(alertID)
                .setValue(new Alert(repo.getUser().getUid(), location.getLatitude(),
                        location.getLongitude(), 0,
                        DateUtil.getTimeInMillis(), false, true))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isEmergency = true;
                        view.showChatFab();
                        view.hideHelpButton();
                        setAlertListener();
                    } else {
                        view.makeToast("Error, reconnecting...", Toast.LENGTH_SHORT);
                        startAlerts();
                    }
                });
    }

    @Override
    public void setAlertListener() {
        DatabaseReference ref = model.getAlertReference()
                .child(String.valueOf(0))
                .child(alertID);

        ref.child("emergency").addValueEventListener(emergencyValueListener);
        ref.child("dispatched").addValueEventListener(dispatchedValueListener);
    }

    @Override
    public void onMapReady() {
        if (repo.getUser().getPhone().equals("")) {
            view.showUpdatePhoneDialog();
        } view.setUserDrawer(repo.getUser());
    }

    @Override
    public void setEmergencyLocation(Location location) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("lat", location.getLatitude());
        map.put("lon", location.getLongitude());
        model.getAlertReference()
                .child(String.valueOf(0))
                .child(alertID)
                .updateChildren(map);
    }

    @Override
    public void setLatestLocation(Location location) {
        if (view != null) {
            if (this.location == null) {
                onFirstLocationUpdate(location);
            } this.location = location;
        }
    }

    @SuppressWarnings("VisibleForTests")
    @Override
    public void setUserUpdate(String name, String phone, String email, Uri image) {
        final User[] user = {null};
        model.getImageRef().child(repo.getUser().getUid()).child(repo.getUser().getUid())
                .putFile(image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user[0] = updateInfo(name, phone, email, task.getResult().getDownloadUrl());
            } else {
                user[0] = updateInfo(name, phone, email, image);
            }
            model.saveUserFirebase(user[0]);
            view.setUserDrawer(user[0]);
        });
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void clearLocation() {
        location = null;
    }

    private void onFirstLocationUpdate(Location location) {
        view.setMapLocation(location);
        view.showHelpButton();
    }

    private User updateInfo(String name, String phone, String email, Uri image) {
        User user = repo.getUser();
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setImage(image.toString());
        repo.setUser(user);
        repo.saveUserPrefs();
        return user;
    }

//    private void setCampusValue(int campus) {
//        repo.getUser().setCampus(campus);
//        model.getReportReference().child(String.valueOf(campus))
//                .addValueEventListener(reportEventListener);
//    }

    private ValueEventListener reportEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null) {
                List<Report> list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Report report = snapshot.getValue(Report.class);
                    if (!isExpired(report.getTimestamp())) {
                        list.add(report);
                    }
                }
                view.getReportMarkers(list);
                list.clear();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };

    private ValueEventListener emergencyValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.getValue(Boolean.class) != null) {
                if (!dataSnapshot.getValue(Boolean.class)) {
                    isEmergency = false;
                    alertID = null;
                    view.showHelpButton();
                    view.setHelpButton(false);
                    view.hideChatFab();
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener dispatchedValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.getValue(Boolean.class) != null) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    view.makeSnackbar("Help is on the way", Snackbar.LENGTH_LONG);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
