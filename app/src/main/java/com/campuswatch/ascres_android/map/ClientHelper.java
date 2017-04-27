package com.campuswatch.ascres_android.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import static com.campuswatch.ascres_android.Constants.UPDATE_INTERVAL;
import static com.campuswatch.ascres_android.map.MapsActivity.IS_EMERGENCY;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

class ClientHelper implements MapsActivityMVP.Client, LocationListener {

    private MapsActivityMVP.Presenter presenter;
    private GoogleApiClient client;

    ClientHelper(MapsActivityMVP.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onLocationChanged(Location location) {
        presenter.setLatestLocation(location);
        if (IS_EMERGENCY) {
            presenter.setEmergencyLocation(location);
        }
    }

    @Override
    public void setClient(GoogleApiClient client) {
        this.client = client;
    }

    @Override
    public void connectClient() {
        if (!client.isConnected()) {
            client.connect();
        }
    }

    @Override
    public void disconnectClient() {
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    @Override
    public void requestLocationUpdates() {
        LocationRequest locationRequest = buildLocationRequest();
        if (checkPermission() && client.isConnected()) {
            FusedLocationApi.requestLocationUpdates(client, locationRequest, ClientHelper.this);
        }
    }

    @Override
    public void removeLocationUpdates() {
        if (client.isConnected()) {
            FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    private static LocationRequest buildLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(client.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
