package com.bearpot.dgjung.nileblue;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Database.LocationStateDBHelper;
import com.bearpot.dgjung.nileblue.Database.MemoDBHelper;
import com.bearpot.dgjung.nileblue.Services.LocationService;
import com.bearpot.dgjung.nileblue.VO.LocationVo;
import com.bearpot.dgjung.nileblue.VO.MemoVo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private LocationStateDBHelper locationStateDbHelper;
    private MemoDBHelper memoDBHelper;

    private Intent intent;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    //private GoogleApiClient awarenessClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationStateDbHelper = new LocationStateDBHelper(getApplicationContext(), "nileblue.db", null,1);
        memoDBHelper = new MemoDBHelper(getApplicationContext(), "nileblue.db", null,1);

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

        startService(new Intent(MainActivity.this, LocationService.class));
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng lastPosition = new LatLng(locationStateDbHelper.getLocationLat(), locationStateDbHelper.getLocationLng());
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

        googleMap.setOnMapLongClickListener(this);
        addAllMarker(googleMap);
        googleMap.setOnMarkerClickListener(this);

    }

    public boolean onMarkerClick(final Marker marker) {
        String description = (String) marker.getTag();

        if (description != null) {
            loadMemoActivity(Integer.parseInt(description.split("/")[0]), description.split("/")[1]);
            return true;
        }

        return false;
    }

    public void addAllMarker(GoogleMap googleMap) {
        List<MemoVo> list = memoDBHelper.getAllMemos();

        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(list.get(i).getLatLng()));
                marker.setTag(list.get(i).getMemoId()+"/"+list.get(i).getDescription());
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("EYEDEAR", String.valueOf(latLng));
        loadMemoActivity(new LocationVo(latLng.latitude, latLng.longitude));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                loadMemoActivity(new LocationVo(locationStateDbHelper.getLocationLat(), locationStateDbHelper.getLocationLng()));
                break;
        }
    }

    public void loadMemoActivity(LocationVo locationVo) {
        intent = new Intent(MainActivity.this, MemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentPosition", locationVo);
        startActivity(intent);
    }

    public void loadMemoActivity(int memo_id, String description) {
        intent = new Intent(MainActivity.this, MemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("memoInfo", new MemoVo(memo_id, description) );
        startActivity(intent);
    }
}