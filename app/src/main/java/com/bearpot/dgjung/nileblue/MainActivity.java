package com.bearpot.dgjung.nileblue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Database.DBHelper;
import com.bearpot.dgjung.nileblue.Services.LocationService;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, OnMapReadyCallback {

    private DBHelper dbHelper;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    //private GoogleApiClient awarenessClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(getApplicationContext(), "nileblue.db", null,1);

        // Google Map Fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Google Awareness API
        /*awarenessClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {}
                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .build();
        awarenessClient.connect();*/

        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d("EYEDEAR", String.valueOf(dbHelper.getLocationLat()));
        Log.d("EYEDEAR", String.valueOf(dbHelper.getLocationLng()));

        googleMap.setOnMapLongClickListener(this);

        LatLng lastPosition = new LatLng(dbHelper.getLocationLat(), dbHelper.getLocationLng());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 13));

        int REQUEST_CODE_LOCATION = 2;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("EYEDEAR", String.valueOf(latLng));
    }
}
