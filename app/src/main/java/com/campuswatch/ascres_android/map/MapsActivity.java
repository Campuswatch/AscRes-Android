package com.campuswatch.ascres_android.map;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.campuswatch.ascres_android.R;
import com.campuswatch.ascres_android.chat.ChatActivity;
import com.campuswatch.ascres_android.models.Report;
import com.campuswatch.ascres_android.models.User;
import com.campuswatch.ascres_android.root.App;
import com.campuswatch.ascres_android.views.CircularProgressBar;
import com.campuswatch.ascres_android.views.ImageTransform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.campuswatch.ascres_android.Constants.REQUEST_CLIENT_CONNECT;
import static com.campuswatch.ascres_android.utils.ChooserUtil.incidentChooser;
import static com.campuswatch.ascres_android.utils.ChooserUtil.spotChooser;
import static com.campuswatch.ascres_android.utils.DateUtil.convertTimestampDateTime;
import static com.campuswatch.ascres_android.utils.PhoneUtil.formatPhoneNumber;

//TODO improve user update functionality
//TODO camera util to handle camera intent
//TODO permission fragment to handle permissions

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ReportDialogFragment.NoticeDialogListener,
        UserUpdateFragment.UserUpdateListener,
        GoogleMap.OnMapLongClickListener,
        MapsActivityMVP.View,
        OnMapReadyCallback {

    public static boolean IS_EMERGENCY;
    public static String ALERT_ID;

    @Inject
    MapsActivityMVP.Presenter presenter;
    @Inject
    GoogleApiClient googleApiClient;
    @Inject
    MapsActivityMVP.Client client;

    @BindView(R.id.map_container) CoordinatorLayout rootLayout;

    @BindView(R.id.help_map_button) CircularProgressBar helpButton;

    @BindView(R.id.drawer_layout) DrawerLayout drawer;

    @BindView(R.id.map_toolbar) Toolbar mapToolbar;

    @BindView(R.id.user_name) TextView userName;

    @BindView(R.id.user_phone) TextView userPhone;

    @BindView(R.id.user_image) ImageView userImage;

    @BindView(R.id.user_update_button) ImageButton userEditButton;

    @BindView(R.id.chat_fab) FloatingActionButton chatFab;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setSupportActionBar(mapToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter.setView(this);
        client.setClient(googleApiClient);

        registerClientCallbacks();
        client.connectClient();

        helpButton.setOnTouchListener(helpButtonListener);
        userEditButton.setOnClickListener(v -> userUpdateFragment());
        chatFab.setOnClickListener(chatFabListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.requestLocationUpdates();
        presenter.getReports();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!IS_EMERGENCY){
            client.removeLocationUpdates();
        }
    }

    @Override
    public void onBackPressed() {
        if (IS_EMERGENCY) {
            moveTaskToBack(true);
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterClientCallbacks();
        presenter.clearLocation();
        client.disconnectClient();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (checkPermission()) {
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
            googleMap.setInfoWindowAdapter(new ReportWindowAdapter(this));
            googleMap.setBuildingsEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setOnMapLongClickListener(this);
            this.map =googleMap;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void animateMapLocation(Location location) {
        if (map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            map.animateCamera(update);
        }
    }

    @Override
    public void setMapLocation(Location location) {
        if (map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15);
            map.moveCamera(update);
        }
    }

    @Override
    public void onDialogClick(DialogFragment dialog, int category, LatLng latlng) {
        presenter.sendReport(latlng, category);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        ReportDialogFragment fragment = ReportDialogFragment.newInstance(latLng);
        fragment.show(getFragmentManager(), "report");
    }

    @Override
    public Snackbar makeSnackbar(String msg, int length) {
        Snackbar s = Snackbar.make(rootLayout, msg, length);
        s.show();
        return s;
    }

    @Override
    public void makeToast(String msg, int length) {
        Toast.makeText(this, msg, length).show();
    }

    @Override
    public void getReportMarkers(List<Report> reports) {
        for (Report report : reports) {
            LatLng latLng = new LatLng(report.getLat(), report.getLon());
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(spotChooser(report.getCategory())))
                    .title(incidentChooser(report.getCategory()))
                    .flat(true)
                    .snippet(convertTimestampDateTime(report.getTimestamp())));
            marker.setTag(report);
        }
    }

    @Override
    public void setUserDrawer(User user) {
        userName.setText(user.getName());
        userPhone.setText(formatPhoneNumber(user.getPhone()));
        Glide.with(this).load(user.getImage())
                .placeholder(R.drawable.help_button)
                .bitmapTransform(new ImageTransform(this))
                .into(userImage);
    }

    @Override
    public void showAlertUI(boolean alert) {
        if (alert) {
            hideHelpButton();
            showChatFab();
        } else {
            showHelpButton();
            hideChatFab();
        }
    }

    private void showChatFab() {
        if (chatFab != null && chatFab.getVisibility() == GONE) {
            chatFab.setVisibility(VISIBLE);
        }
    }

    private void hideChatFab() {
        if (chatFab != null && chatFab.getVisibility() == VISIBLE) {
            chatFab.setVisibility(GONE);
        }
    }

    private void showHelpButton() {
        if (helpButton != null && helpButton.getVisibility() == GONE) {
            helpButton.setVisibility(VISIBLE);
            helpButton.setValue(0, true);
        }
    }

    private void hideHelpButton() {
        if (helpButton != null && helpButton.getVisibility() == VISIBLE) {
            helpButton.setVisibility(GONE);
        }
    }

    private void userUpdateFragment() {
        closeDrawer();
        UserUpdateFragment fragment = UserUpdateFragment.newInstance(
                presenter.getUser().serialize());
        fragment.show(getSupportFragmentManager(), "updateFragment");
    }

    @Override
    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                openDrawer();
                break;
            case R.id.myLocation:
                animateMapLocation(presenter.getLocation());
                break;
        } return super.onOptionsItemSelected(menuItem);
    }

    View.OnTouchListener helpButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            long eventDuration = event.getEventTime() - event.getDownTime();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setTag(true);
                helpButton.setValue(100, true);
            } else if (eventDuration >= 1500 && (boolean) view.getTag()) {
                view.setTag(false);
                presenter.startAlerts();
            } else if ((event.getAction() == MotionEvent.ACTION_UP) && (boolean) view.getTag()) {
                helpButton.setValue(0, true);
            } return false;
        }
    };

    private View.OnClickListener chatFabListener = v ->
            startActivity(new Intent(MapsActivity.this, ChatActivity.class));

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        client.requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        makeSnackbar("Connection lost, reconnecting...", Snackbar.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CLIENT_CONNECT);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CLIENT_CONNECT:
                onResolveClientConnection(resultCode);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void openDrawer() {
        if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void closeDrawer() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void registerClientCallbacks() {
        googleApiClient.registerConnectionCallbacks(this);
        googleApiClient.registerConnectionFailedListener(this);
    }

    private void unregisterClientCallbacks() {
        googleApiClient.unregisterConnectionCallbacks(this);
        googleApiClient.unregisterConnectionFailedListener(this);
    }

    private void onResolveClientConnection(int resultCode) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                break;
            case ConnectionResult.INTERNAL_ERROR | ConnectionResult.NETWORK_ERROR:
                client.connectClient();
                break;
            default:
                makeToast("An error occurred.  Please relaunch app", Toast.LENGTH_LONG);
                finish();
                break;
        }
    }

    @Override
    public void onImageUpdated(Uri image) {
        presenter.setImageUpdate(image);
    }

    @Override
    public void onPhoneUpdated(String phone) {
        presenter.setPhoneUpdate(phone);
    }
}
