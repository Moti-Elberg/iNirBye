package com.example.motielberg.inirbye.ActivitiesAndFrags;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.motielberg.inirbye.Controller.PlaceAdapter;
import com.example.motielberg.inirbye.DB.Place;
import com.example.motielberg.inirbye.R;
import com.example.motielberg.inirbye.services.AudioService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.motielberg.inirbye.DB.Constants.PLACE;
import static com.example.motielberg.inirbye.DB.Constants.RC;

public class MapAct extends FragmentActivity implements PlaceAdapter.OnItemSelectListener, OnMapReadyCallback, View.OnClickListener, LocationListener {
    private GoogleMap mMap;
    private FavoritesFrag favoritesFrag;
    private InfoFrag infoFrag;
    private LinearLayout sweep;
    private ImageView btnUp;
    private LocationManager locationManager;
    private Marker marker, placeMarker;
    private ObjectAnimator fragBouncer, buttonFlip;
    private SearchFrag searchFrag;
    private float startY, endY, deg = 180;
    private boolean is2ndFrag;
    private boolean isFramDown;
    private PlaceAdapter adapter;
    private Bundle bundle;
    private markerToMapReceiver receiver;
    private SupportMapFragment mapFragment;
    private FrameLayout fragCon2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sweep = findViewById(R.id.sweep);
        btnUp = findViewById(R.id.btnUp);
        btnUp.setOnClickListener(this);
        receiver= new markerToMapReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(PLACE));
        // create new fragments
        infoFrag = new InfoFrag();
        searchFrag = new SearchFrag();
        favoritesFrag = new FavoritesFrag();
        adapter = new PlaceAdapter(this, 0);
        bundle = new Bundle();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragCon, searchFrag);
        if (findViewById(R.id.fragCon2) != null) {
            transaction.replace(R.id.fragCon2, infoFrag);
            getSupportFragmentManager().beginTransaction()
                    .hide(infoFrag)
                    .commit();
            is2ndFrag = true;
        }
        transaction.commit();
        // location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // settings a new geocoder
        // geocoder = new Geocoder(this);
        // find id for 2 buttons and addressing them to onClick method
        findViewById(R.id.btnFindLoc).setOnClickListener(this);
        findViewById(R.id.btnSettings).setOnClickListener(this);

        // calls getUserLocation method
        getUserLocation();
        // play music
        startService(new Intent(this, AudioService.class));
    }

    // setting up the map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onClick(View v) {
        // if marker is not empty - remove
        switch (v.getId()) {
            case R.id.btnFindLoc:
                if (marker != null) {
                    marker.remove();
                }
                // calls getUserLocation method
                getUserLocation();
                break;

            case R.id.btnSettings:
                // open settings
                startActivity(new Intent(this, SettingsAct.class));
                break;

            case R.id.btnUp:
                int s3rd = getResources().getDisplayMetrics().heightPixels / 3;
                int s6th = getResources().getDisplayMetrics().heightPixels - s3rd;
                // Animate frag container
                if (!isFramDown) {
                    fragBouncer = ObjectAnimator.ofFloat(sweep, "translationY", 0, s6th);
                    fragBouncer.setDuration(1000);
                    fragBouncer.setInterpolator(new BounceInterpolator());
                    fragBouncer.start();
                    buttonFlip = ObjectAnimator.ofFloat(btnUp, "rotation", deg);
                    buttonFlip.setDuration(500);
                    buttonFlip.start();
                } else {
                    fragBouncer = ObjectAnimator.ofFloat(sweep, "translationY", s6th, 0);
                    fragBouncer.setDuration(1000);
                    fragBouncer.setInterpolator(new BounceInterpolator());
                    fragBouncer.start();
                    buttonFlip = ObjectAnimator.ofFloat(btnUp, "rotation", deg - deg);
                    buttonFlip.setDuration(500);
                    buttonFlip.start();
                }
                isFramDown = !isFramDown;
                break;
        }
    }

    // get user location & permission request
    private void getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 100, this);
        }
    }

    // asks permission for access location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Error! no location permission granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 13));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        sp.edit().putFloat("longitude", (float) location.getLongitude()).commit();
        sp.edit().putFloat("latitude", (float) location.getLatitude()).commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void sendPlace(Place place) {
        // sends place to information fragment
        bundle.putSerializable("place", place);
        infoFrag.setArguments(bundle);
        if (is2ndFrag) {
            getSupportFragmentManager().beginTransaction()
                    .show(infoFrag)
                    .replace(R.id.fragCon2, infoFrag)
                    .detach(infoFrag).attach(infoFrag)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragCon, infoFrag)
                    .commit();
        }
        LatLng selPlace=new LatLng(place.getLat(),place.getLng());
        if (placeMarker!=null) {
            placeMarker.remove();
        }
        placeMarker=mMap.addMarker(new MarkerOptions().position(selPlace).alpha(0.8f).title(place.getPlaceName()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selPlace, 14));
        }

    // define the receiver class
    private class markerToMapReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy(); // stop audio service
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}