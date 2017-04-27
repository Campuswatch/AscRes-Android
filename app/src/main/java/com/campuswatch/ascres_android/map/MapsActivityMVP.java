package com.campuswatch.ascres_android.map;

import android.location.Location;
import android.net.Uri;
import android.support.design.widget.Snackbar;

import com.campuswatch.ascres_android.models.Report;
import com.campuswatch.ascres_android.models.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public interface MapsActivityMVP {

    interface View
    {
        Snackbar makeSnackbar(String msg, int length);
        void makeToast(String msg, int length);
        void animateMapLocation(Location location);
        void setMapLocation(Location location);
        void getReportMarkers(List<Report> reports);
        void setUserDrawer(User user);
        void showAlertUI(boolean alert);
        boolean checkPermission();
    }

    interface Presenter
    {
        void setView(View view);
        void startAlerts();
        void setEmergencyLocation(Location location);
        void setLatestLocation(Location location);
        User getUser();
        Location getLocation();
        void clearLocation();
        void getReports();
        void sendReport(LatLng latLng, int category);
        void setPhoneUpdate(String phone);
        void setImageUpdate(Uri image);
    }

    interface Model
    {
        void sendReportFirebase(Report report);
        DatabaseReference getReportReference();
        DatabaseReference getAlertReference();
        StorageReference getImageRef();
    }

    interface Client
    {
        void setClient(GoogleApiClient client);
        void connectClient();
        void disconnectClient();
        void requestLocationUpdates();
        void removeLocationUpdates();
    }
}
