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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.campuswatch.ascres_android.map.MapsActivity.ALERT_ID;
import static com.campuswatch.ascres_android.map.MapsActivity.IS_EMERGENCY;
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

    MapsPresenter(MapsActivityMVP.Model model, UserRepository repo) {
        this.model = model;
        this.repo = repo;
    }

    @Override
    public void setView(MapsActivityMVP.View view) {
        this.view = view;
        this.view.setUserDrawer(repo.getUser());
    }

    @Override
    public User getUser() {
        return repo.getUser();
    }

    @Override
    public void sendReport(LatLng latLng, int category) {
        Report report = new Report(latLng.latitude, latLng.longitude,
                DateUtil.getTimeInMillis(), category, repo.getUser().getUid());
        model.sendReportFirebase(report);
    }

    @Override
    public void startAlerts() {
        ALERT_ID = UUID.randomUUID().toString();

        model.getAlertReference()
                .child(ALERT_ID)
                .setValue(new Alert(repo.getUser().getUid(), location.getLatitude(),
                        location.getLongitude(),
                        DateUtil.getTimeInMillis(), false, true))
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        view.makeToast("Error, reconnecting...", Toast.LENGTH_SHORT);
                        startAlerts();
                        return;
                    }

                    IS_EMERGENCY = true;
                    view.showChatFab();
                    view.hideHelpButton();

                    model.getAlertReference().child(ALERT_ID).child("emergency")
                            .addValueEventListener(emergencyListener);
                    model.getAlertReference().child(ALERT_ID).child("dispatched")
                            .addValueEventListener(dispatchedListener);
                });
    }

    @Override
    public void setEmergencyLocation(Location location) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("lat", location.getLatitude());
        map.put("lon", location.getLongitude());
        model.getAlertReference()
                .child(ALERT_ID)
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

    @Override
    public void getReports() {
        model.getReportReference().addValueEventListener(reportEventListener);
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
        return user;
    }

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
        public void onCancelled(DatabaseError databaseError) {
            view.makeToast(databaseError.getMessage(), Toast.LENGTH_SHORT);
        }
    };

    private ValueEventListener emergencyListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.getValue(Boolean.class)) {

                model.getAlertReference().child(ALERT_ID).child("emergency")
                        .removeEventListener(this);

                model.getAlertReference().child(ALERT_ID).child("dispatched")
                        .removeEventListener(dispatchedListener);

                IS_EMERGENCY = false;
                ALERT_ID = null;

                view.showHelpButton();
                view.setHelpButton(false);
                view.hideChatFab();

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            view.makeToast(databaseError.getMessage(), Toast.LENGTH_SHORT);
        }
    };

    private ValueEventListener dispatchedListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue(Boolean.class)) {
                view.makeSnackbar("Help is on the way", Snackbar.LENGTH_LONG);

                model.getAlertReference().child(ALERT_ID).child("dispatched")
                        .removeEventListener(this);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            view.makeToast(databaseError.getMessage(), Toast.LENGTH_SHORT);
        }
    };
}
