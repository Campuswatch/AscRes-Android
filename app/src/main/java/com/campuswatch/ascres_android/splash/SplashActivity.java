package com.campuswatch.ascres_android.splash;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.campuswatch.ascres_android.UserRepository;
import com.campuswatch.ascres_android.map.MapsActivity;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.root.App;
import com.campuswatch.ascres_android.signup.SignUpActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import static com.campuswatch.ascres_android.Constants.LOCATION_PERMISSION_REQUEST;
import static com.campuswatch.ascres_android.Constants.REQUEST_CHECK_SETTINGS;
import static com.campuswatch.ascres_android.Constants.REQUEST_CLIENT_CONNECT;
import static com.campuswatch.ascres_android.Constants.UPDATE_INTERVAL;
import static com.campuswatch.ascres_android.Constants.USER_DATA;

/**
 * Thought of by samwyz for the most part on 4/13/17.
 */

public class SplashActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    @Inject
    GoogleApiClient client;
    @Inject
    UserRepository repo;

    private LocationSettingsRequest locationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) getApplication()).getComponent().inject(this);
        locationSettingsRequest = buildLocationRequest();
        initializeClient();
    }

    private void checkLocationSettings() {

        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(client,
                        locationSettingsRequest);

        pendingResult.setResultCallback(result -> {
            Status status = result.getStatus();
            onCheckSettingsRequest(status);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                onCheckSettingsResult(resultCode);
                break;
            case REQUEST_CLIENT_CONNECT:
                onResolveClientConnection(resultCode);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionComplete();
                } else showSnackbarRationale();
        }
    }

    private boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void checkPermissions() {
        if (!hasPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showSnackbarRationale();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            }
        } else permissionComplete();
    }

    private void showSnackbarRationale() {
        Snackbar.make(findViewById(android.R.id.content),
                "Device location required for emergency assistance", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", view -> ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST)).show();
    }

    private void permissionComplete() {
        if (isLoggedIn()) {
            loadUserFromPrefs();
            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            unregisterClientCallbacks();
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
            unregisterClientCallbacks();
            finish();
        }
    }

    private void loadUserFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(USER_DATA, Context.MODE_PRIVATE);
        if (prefs.getString(USER_DATA, null) != null) {
            repo.setUser(User.create(prefs.getString(USER_DATA, null)));
        }
    }

    private static LocationSettingsRequest buildLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        return builder.build();
    }

    private void onCheckSettingsRequest(Status status) {
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                checkPermissions();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(
                            SplashActivity.this,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                } break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Toast.makeText(SplashActivity.this, "Location settings unavailable",
                        Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                break;
        }
    }

    private void onCheckSettingsResult(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                checkPermissions();
                break;
            case Activity.RESULT_CANCELED:
                Snackbar.make(findViewById(android.R.id.content),
                        "Device location required for emergency assistance",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v -> checkLocationSettings())
                        .show();
                break;
            default:
                break;
        }
    }

    private void onResolveClientConnection(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                break;
            case ConnectionResult.INTERNAL_ERROR | ConnectionResult.NETWORK_ERROR:
                client.connect();
                break;
            default:
                Toast.makeText(this, "Unable to connect to client, please retry",
                        Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void initializeClient() {
        client.registerConnectionCallbacks(this);
        client.registerConnectionFailedListener(this);
        client.connect();
    }

    private void unregisterClientCallbacks() {
        client.unregisterConnectionCallbacks(this);
        client.unregisterConnectionFailedListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection lost, attempting to reconnect", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CLIENT_CONNECT);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
