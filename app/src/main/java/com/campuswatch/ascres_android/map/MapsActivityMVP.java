package com.campuswatch.ascres_android.map;

import android.location.Location;
import android.net.Uri;

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
        void makeSnackbar(String msg, int length);
        void makeToast(String msg, int length);
        void animateMapLocation(Location location);
        void setMapLocation(Location location);
        void getReportMarkers(List<Report> reports);
        void setUserDrawer(User user);
        void showChatFab();
        void hideChatFab();
        void showHelpButton();
        void hideHelpButton();
        void setHelpButton(boolean isEmergency);
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
        void setUserUpdate(String name, String phone, String email, Uri image);
    }

    interface Model
    {
        void saveUserFirebase(User user);
        void sendReportFirebase(Report report);
        DatabaseReference getReportReference();
        DatabaseReference getAlertReference();
        DatabaseReference getUserReference();
        StorageReference getImageRef();
    }

    interface Client
    {
        void setClient(GoogleApiClient client);
        void connectClient();
        void disconnectClient();
        void requestLocationUpdates();
        void subscribeToLocationUpdates();
        void unsubscribeToLocationUpdates();
    }
}
