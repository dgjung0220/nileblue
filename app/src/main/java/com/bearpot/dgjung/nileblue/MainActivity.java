package com.bearpot.dgjung.nileblue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Database.LocationStateDBHelper;
import com.bearpot.dgjung.nileblue.Database.MemoDBHelper;
import com.bearpot.dgjung.nileblue.Database.PlaceDBHelper;
import com.bearpot.dgjung.nileblue.Services.GetRecommandPlace;
import com.bearpot.dgjung.nileblue.Services.LocationService;
import com.bearpot.dgjung.nileblue.VO.LocationVo;
import com.bearpot.dgjung.nileblue.VO.MemoVo;
import com.bearpot.dgjung.nileblue.VO.PlaceVo;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private LocationStateDBHelper locationStateDbHelper;
    private MemoDBHelper memoDBHelper;
    private PlaceDBHelper placeDBHelper;

    private GetRecommandPlace recommandPlace;
    private List<PlaceVo> recommandPlaceList;
    private GoogleMap gMap;
    private Intent intent;
    //private GoogleApiClient awarenessClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationStateDbHelper = new LocationStateDBHelper(getApplicationContext(), "nileblue.db", null,1);
        memoDBHelper = new MemoDBHelper(getApplicationContext(), "nileblue.db", null,1);
        placeDBHelper = new PlaceDBHelper(getApplicationContext(), "nileblue.db", null,1);
        recommandPlace = new GetRecommandPlace();

        // ActionBar
        //getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF339999));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;

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
        addRecommandMarker(googleMap);

    }

    public boolean onMarkerClick(final Marker marker) {
        String description = (String) marker.getTag();

        String results[] = description.split("/");

        if (results.length == 1) {
            return false;
        } else {
            loadMemoActivity(Integer.parseInt(results[0]), marker.getPosition().latitude, marker.getPosition().longitude, results[1]);
            return true;
        }
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

    public void addRecommandMarker(GoogleMap googleMap) {
        List<PlaceVo> result = placeDBHelper.select();

        if (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                PlaceVo place = result.get(i);
                Marker marker = googleMap.addMarker(
                        new MarkerOptions().position(new LatLng(place.getLocation().getLat(), place.getLocation().getLng()))
                                .title(place.getPlaceName())
                                .alpha(0.5f)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                );
                marker.setTag(place.getPlace_id());
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d("EYEDEAR", String.valueOf(latLng));
        loadMemoActivity(new LocationVo(latLng.latitude, latLng.longitude));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recommand_place:
                placeDBHelper.delete();
                Thread getRecommandThread = new Thread() {
                    public void run() {
                        recommandPlaceList = recommandPlace.sendByHttp(locationStateDbHelper.getLocationLat(), locationStateDbHelper.getLocationLng(), 500, "point_of_interest");

                        if (recommandPlaceList != null && recommandPlaceList.size() > 0) {

                            for (int i = 0; i < recommandPlaceList.size(); i++) {
                                String place_id = recommandPlaceList.get(i).getPlace_id();
                                String placeName = recommandPlaceList.get(i).getPlaceName();
                                double placeLat = recommandPlaceList.get(i).getLocation().getLat();
                                double placeLng = recommandPlaceList.get(i).getLocation().getLng();

                                placeDBHelper.save(new PlaceVo(place_id, placeName, placeLat, placeLng));
                            }
                        }
                    }
                };
                getRecommandThread.start();

                try {
                    getRecommandThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (recommandPlaceList != null) {
                    if (recommandPlaceList.size() == 0) {
                        Toast.makeText(this, "추천할 장소가 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, recommandPlaceList.size() +"개의 추천 장소가 표시됩니다.", Toast.LENGTH_SHORT).show();
                        onMapReady(gMap);
                    }
                }

                break;
        }

        return super.onOptionsItemSelected(item);
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

    public void loadMemoActivity(int memo_id, double lat, double lon, String description) {
        intent = new Intent(MainActivity.this, MemoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("memoInfo", new MemoVo(memo_id, lat, lon, description) );
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placeDBHelper.delete();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}