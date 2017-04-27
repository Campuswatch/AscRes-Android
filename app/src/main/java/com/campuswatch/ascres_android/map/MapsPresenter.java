package com.campuswatch.ascres_android.map;

import android.location.Location;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.campuswatch.ascres_android.root.UserRepository;
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
    private Snackbar dispatchSnackbar;

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
    public void setPhoneUpdate(String phone) {
        User user = repo.getUser();
        user.setPhone(phone);
        repo.setUser(user);
        view.setUserDrawer(user);
    }

    @SuppressWarnings("VisibleForTests")
    @Override
    public void setImageUpdate(Uri image) {
        User user = repo.getUser();
        model.getImageRef().child(user.getUid()).child(user.getUid())
                .putFile(image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.setImage(task.getResult().getDownloadUrl().toString());
                repo.setUser(user);
                view.setUserDrawer(user);
            } else view.makeToast("Error uploading image", Toast.LENGTH_SHORT);
        });
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
                    view.showAlertUI(true);

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
        view.showAlertUI(false);
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

                if (dispatchSnackbar != null && dispatchSnackbar.isShown()) {
                    dispatchSnackbar.dismiss();
                }

                IS_EMERGENCY = false;
                ALERT_ID = null;

                view.showAlertUI(false);

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
                dispatchSnackbar = view.makeSnackbar("Help is on the way", Snackbar.LENGTH_INDEFINITE);

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
