package com.campuswatch.ascres_android.map;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
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
import static com.campuswatch.ascres_android.UserRepository.isEmergency;
import static com.campuswatch.ascres_android.utils.ChooserUtil.incidentChooser;
import static com.campuswatch.ascres_android.utils.ChooserUtil.spotChooser;
import static com.campuswatch.ascres_android.utils.DateUtil.convertTimestampDateTime;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ReportDialogFragment.NoticeDialogListener,
        UserUpdateFragment.UserUpdateListener,
        GoogleMap.OnMapLongClickListener,
        MapsActivityMVP.View,
        OnMapReadyCallback {

    @Inject
    MapsActivityMVP.Presenter presenter;
    @Inject
    GoogleApiClient googleApiClient;
    @Inject
    MapsActivityMVP.Client client;

    @BindView(R.id.help_map_button)
    CircularProgressBar helpButton;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.map_toolbar)
    Toolbar mapToolbar;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_phone)
    TextView userPhone;
    @BindView(R.id.user_email)
    TextView userEmail;
    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.help_flame)
    ImageView helpFlame;
    @BindView(R.id.user_update_button)
    ImageButton userEditButton;
    @BindView(R.id.chat_fab)
    FloatingActionButton chatFab;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_maps);

        ((App) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        presenter.setView(this);
        client.setClient(googleApiClient);

        helpButton.setOnTouchListener(helpButtonListener);
        chatFab.setOnClickListener(fabListener);
        drawer.addDrawerListener(drawerListener);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initializeActionBar();
        registerClientCallbacks();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isEmergency){
            client.disconnectClient();
        }
    }

    @Override
    public void onBackPressed() {
        if (isEmergency) {
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
        initializeMap(googleMap);
        presenter.onMapReady();
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

    private void initializeMap(GoogleMap map) {
        if (checkPermission()) {
//            MapStyleOptions style = MapStyleOptions
//                    .loadRawResourceStyle(MapsActivity.this, R.raw.mapstyle);
//            map.setMapStyle(style);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setIndoorLevelPickerEnabled(false);
            map.setInfoWindowAdapter(new ReportWindowAdapter(this));
            map.setBuildingsEnabled(false);
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setOnMapLongClickListener(this);
            this.map = map;
        }
    }

    @Override
    public void onDialogClick(DialogFragment dialog, int category, LatLng latlng) {
        presenter.sendReport(latlng, category);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        ReportDialogFragment fragment = new ReportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", latLng);
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "report");
    }

    @Override
    public void makeSnackbar(String msg, int length) {
        Snackbar.make(findViewById(android.R.id.content), msg, length).show();
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
        userPhone.setText(user.getPhone());
        userEmail.setText(user.getEmail());
        Glide.with(this).load(user.getImage())
                .placeholder(R.drawable.logo_full_resize)
                .bitmapTransform(new ImageTransform(this))
                .into(userImage);
    }

    @Override
    public void showChatFab() {
        if (chatFab != null && chatFab.getVisibility() == GONE) {
            chatFab.setVisibility(VISIBLE);
        }
    }

    @Override
    public void hideChatFab() {
        if (chatFab != null && chatFab.getVisibility() == VISIBLE) {
            chatFab.setVisibility(GONE);
        }
    }

    @Override
    public void showHelpButton() {
        if (helpButton != null && helpButton.getVisibility() == GONE) {
            helpButton.setVisibility(VISIBLE);
            helpFlame.setVisibility(VISIBLE);
        }
    }

    @Override
    public void hideHelpButton() {
        if (helpButton != null && helpButton.getVisibility() == VISIBLE) {
            helpButton.setVisibility(GONE);
            helpFlame.setVisibility(GONE);
        }
    }

    @Override
    public void showUpdatePhoneDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.logo_full);
        alertDialogBuilder.setTitle("Reminder");
        alertDialogBuilder.setMessage("Please update phone number");
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> userUpdateFragment());
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private void userUpdateFragment() {
        closeDrawer();
        UserUpdateFragment fragment = UserUpdateFragment.newInstance(
                presenter.getUser().serialize());
        fragment.show(getSupportFragmentManager(), "updateFragment");
    }

    @Override
    public void OnUserUpdated(String name, String phone, String email, Uri image) {
        presenter.setUserUpdate(name, phone, email, image);
    }

    @Override
    public boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void setHelpButton(boolean isEmergency) {
        if (isEmergency){
            if (helpButton != null) {
                helpButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.orange_button));
                helpButton.setBorderProgressColor(ContextCompat.getColor(this, R.color.colorAccent));
                helpButton.setValue(100, true);
            }
        } else {
            if (helpButton != null) {
                helpButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.green_button));
                helpButton.setBorderProgressColor(ContextCompat.getColor(this, R.color.colorPrimary));
                helpButton.setValue(0, true);
            }
        }
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
                setHelpButton(true);
                presenter.startAlerts();
            } else if ((event.getAction() == MotionEvent.ACTION_UP) && (boolean) view.getTag()) {
                helpButton.setValue(0, true);
            } return false;
        }
    };

    private void initializeActionBar() {
        setSupportActionBar(mapToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.toolbar_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}

        @Override
        public void onDrawerOpened(View drawerView) {
            mapToolbar.animate()
                    .translationY(-mapToolbar.getBottom())
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mapToolbar.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

        @Override
        public void onDrawerStateChanged(int newState) {}
    };

    View.OnClickListener fabListener = v ->
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

    private void drawTestCircle(double lat, double lon, int radius) {
        map.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(radius)
                .strokeColor(Color.RED));
    }
}
