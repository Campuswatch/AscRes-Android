package com.campuswatch.ascres_android.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

import static com.campuswatch.ascres_android.Constants.UPDATE_INTERVAL;
import static com.campuswatch.ascres_android.map.MapsActivity.IS_EMERGENCY;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

class ClientHelper implements MapsActivityMVP.Client, LocationListener {

    private MapsActivityMVP.Presenter presenter;
    private GoogleApiClient client;
    private PublishSubject<Location> locationPublisher;
    private Disposable locationDisposable;

    ClientHelper(MapsActivityMVP.Presenter presenter) {
        this.presenter = presenter;
        this.locationPublisher = PublishSubject.create();
    }

    @Override
    public void onLocationChanged(Location location) {
        locationPublisher.onNext(location);
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
        if (checkPermission()) {
            FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void subscribeToLocationUpdates() {
        locationDisposable = locationPublisher.subscribe(locationConsumer);
    }

    @Override
    public void unsubscribeToLocationUpdates() {
        if (locationDisposable != null){
            locationDisposable.dispose();
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

    private Consumer<Location> locationConsumer = new Consumer<Location>() {
        @Override
        public void accept(Location location) throws Exception {
            presenter.setLatestLocation(location);
            if (IS_EMERGENCY) {
                presenter.setEmergencyLocation(location);
            }
        }
    };
}
