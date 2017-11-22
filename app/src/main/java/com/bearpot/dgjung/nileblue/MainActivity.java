package com.bearpot.dgjung.nileblue;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Database.LocationStateDBHelper;
import com.bearpot.dgjung.nileblue.Database.MemoDBHelper;
import com.bearpot.dgjung.nileblue.Database.PlaceDBHelper;
import com.bearpot.dgjung.nileblue.Services.AlarmService;
import com.bearpot.dgjung.nileblue.Services.GeofenceTransitionsIntentService;
import com.bearpot.dgjung.nileblue.Services.WeatherFloatingService;
import com.bearpot.dgjung.nileblue.Services.AwarenessService;
import com.bearpot.dgjung.nileblue.Services.GetRecommandPlace;
import com.bearpot.dgjung.nileblue.VO.LocationVo;
import com.bearpot.dgjung.nileblue.VO.MemoVo;
import com.bearpot.dgjung.nileblue.VO.PlaceVo;
import com.bearpot.dgjung.nileblue.VO.WeatherVo;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private LocationStateDBHelper locationStateDbHelper;
    private MemoDBHelper memoDBHelper;
    private PlaceDBHelper placeDBHelper;

    private GetRecommandPlace recommandPlace;
    private List<PlaceVo> recommandPlaceList;
    private GoogleMap gMap;
    private Intent intent;

    /* Weather */
    private SnapshotClient snapshotClient;
    private TextView weather_temperature = null;
    private TextView weather_huminity;
    private TextView weather_dewPoint;

    /* GeoFence */
    private ArrayList<Geofence> mGeofenceList;

    /* AlarmService */
    private AlarmService alarmService;
    boolean isAlarmServiceOn = false;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationStateDbHelper = new LocationStateDBHelper(getApplicationContext(), "nileblue.db", null, 1);
        memoDBHelper = new MemoDBHelper(getApplicationContext(), "nileblue.db", null, 1);
        placeDBHelper = new PlaceDBHelper(getApplicationContext(), "nileblue.db", null, 1);

        recommandPlace = new GetRecommandPlace();
        mGeofenceList = new ArrayList<Geofence>();

        // Google Map Fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                AlarmBinder alarmBinder = (AlarmBinder) iBinder;
                alarmService = alarmBinder.getService();
                isAlarmServiceOn = true;

                Log.d("EYEDEAR","onServiceConnected.");
                alarmService.setAlarm(MainActivity.this);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isAlarmServiceOn = false;
                Log.d("EYEDEAR","onServiceDisconnected.");
            }
        };*/

        //bindService(new Intent(MainActivity.this, AlarmService.class), conn, Context.BIND_AUTO_CREATE);
        getWeather();
        startService(new Intent(MainActivity.this, AwarenessService.class));
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

        LocationServices.getGeofencingClient(this).addGeofences(getGeofencingRequeset(), getGeofenceTransitionPendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("EYEDEAR","Geofencing");
                    }
                });

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

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Marker marker = googleMap.addMarker(new MarkerOptions().position(list.get(i).getLatLng()));
                marker.setTag(list.get(i).getMemoId() + "/" + list.get(i).getDescription());
                createGeofences(list.get(i).getLatLng().latitude, list.get(i).getLatLng().longitude);
            }
        }
    }

    private GeofencingRequest getGeofencingRequeset() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(mGeofenceList);

        Log.d("DONGGOO", "getGeofencingRequest");

        return builder.build();
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        Log.d("DONGGOO", "getGeofenceTransitionPendingIntent");
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void createGeofences(double latitude, double longitude) {
        String id = UUID.randomUUID().toString();

        Log.d("DONGGOO", "Create Geofence");

        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setLoiteringDelay(1000)
                .build();

        mGeofenceList.add(fence);
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
                        recommandPlaceList = recommandPlace.sendByHttp(locationStateDbHelper.getLocationLat(), locationStateDbHelper.getLocationLng(), 5000, "restaurant");

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
                        Toast.makeText(this, recommandPlaceList.size() + "개의 추천 장소가 표시됩니다.", Toast.LENGTH_SHORT).show();
                        onMapReady(gMap);
                    }
                }

                break;

            case R.id.weather_context:
                intent = new Intent(this, WeatherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                
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
        intent.putExtra("memoInfo", new MemoVo(memo_id, lat, lon, description));
        startActivity(intent);
    }

    public void getWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        snapshotClient = Awareness.getSnapshotClient(this);
        snapshotClient.getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
            @Override
            public void onComplete(@NonNull Task<WeatherResponse> task) {
                if ((task.isSuccessful() && task.getResult() != null)) {
                    WeatherResponse result = task.getResult();

                    float temperature = Math.round(result.getWeather().getTemperature(2)*100.0f) / 100.0f;
                    float feelsLikeTemperature = Math.round(result.getWeather().getFeelsLikeTemperature(2) * 100.0f) / 100.0f;
                    float dewPoint = Math.round(result.getWeather().getDewPoint(2) * 100.0f) / 100.0f;
                    int huminity = result.getWeather().getHumidity();
                    int[] conditions = result.getWeather().getConditions();

                    LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mView = mInflater.inflate(R.layout.weather_floating_main, null);

                    LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    mParams.topMargin = 230;
                    mParams.leftMargin = 20;
                    mView.setAlpha(0.8f);
                    getWindow().addContentView(mView, mParams);

                    weather_temperature = mView.findViewById(R.id.weather_temperature);
                    weather_huminity = mView.findViewById(R.id.weather_huminity);
                    weather_dewPoint = mView.findViewById(R.id.weather_dewPoint);

                    weather_temperature.setText(temperature+"/" + feelsLikeTemperature + "℃" );
                    weather_huminity.setText(huminity+"%");
                    weather_dewPoint.setText(dewPoint+"℃");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        placeDBHelper.delete();
        //stopService(new Intent(this, WeatherFloatingService.class));
        //unbindService(conn);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}