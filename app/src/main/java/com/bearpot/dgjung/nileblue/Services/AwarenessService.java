package com.bearpot.dgjung.nileblue.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bearpot.dgjung.nileblue.Database.LocationStateDBHelper;
import com.bearpot.dgjung.nileblue.MainActivity;
import com.bearpot.dgjung.nileblue.R;
import com.bearpot.dgjung.nileblue.VO.WeatherVo;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * Created by dg.jung on 2017-11-14.
 */

public class AwarenessService extends Service implements LocationListener {

    private NotificationManager notificationManager;
    private Notification notification;

    private SnapshotClient snapshotClient;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;

    private LocationStateDBHelper locationStateDBHelper;

    private double currentLatitude;
    private double currentLongitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("EYEDEAR", "Start Awareness Service");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        snapshotClient = Awareness.getSnapshotClient(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationStateDBHelper = new LocationStateDBHelper(getApplicationContext(), "nileblue.db", null, 1);

        getWeather();
        getLastLocation();
        getLocation();

        return START_STICKY;
    }

    private void notifyResult(String result) {
        Log.d("EYEDEAR", "Notify weather");

        Intent intent = new Intent(AwarenessService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AwarenessService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Weather Information")
                    .setContentText(result)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(777, notification);
    }

    public void getWeather() {
        Log.d("EYEDEAR", "Get Weather");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        snapshotClient.getWeather().addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
            @Override
            public void onComplete(@NonNull Task<WeatherResponse> task) {
                if ((task.isSuccessful() && task.getResult() != null)) {
                    WeatherResponse result = task.getResult();

                    float temperature = result.getWeather().getTemperature(2);
                    float feelsLikeTemperature = result.getWeather().getFeelsLikeTemperature(2);
                    float dewPoint = result.getWeather().getDewPoint(2);
                    int huminity = result.getWeather().getHumidity();
                    int[] conditions = result.getWeather().getConditions();

                    Log.d("EYEDEAR", "온도 : " + String.valueOf(temperature));
                    Log.d("EYEDEAR", "체감 온도 : " + String.valueOf(feelsLikeTemperature));
                    Log.d("EYEDEAR", "이슬점 : " + String.valueOf(dewPoint));
                    Log.d("EYEDEAR", "습도 : " + String.valueOf(huminity));

                    for (int i = 0; i < conditions.length; i++) {
                        Log.d("EYEDEAR", String.valueOf(conditions[i]));
                    }

                    notifyResult(String.valueOf(Math.round(temperature*100.0)/100.0)+"℃");

                    /*Intent intent = new Intent(AwarenessService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("weatherInfo", new WeatherVo(conditions,temperature,feelsLikeTemperature,dewPoint,huminity));
                    startActivity(intent);*/

                } else {
                    notifyResult("Couldn't get weather information");
                }
            }
        });
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        snapshotClient.getLocation().addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationResponse> task) {
                if ((task.isSuccessful() && task.getResult() != null)) {
                    LocationResponse result = task.getResult();

                    currentLatitude = result.getLocation().getLatitude();
                    currentLongitude = result.getLocation().getLongitude();

                    locationStateDBHelper.updateLocation(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                    Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLat()));
                    Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLng()));

                } else {

                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        locationStateDBHelper.updateLocation(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
        Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLat()));
        Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLng()));
    }

    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();
                            locationStateDBHelper.updateLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));

                            Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLat()));
                            Log.d("EYEDEAR", String.valueOf(locationStateDBHelper.getLocationLng()));

                        } else {
                            Log.d("DGJUNG", "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }
}
