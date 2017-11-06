package com.bearpot.dgjung.nileblue.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.bearpot.dgjung.nileblue.Database.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by dg.jung on 2017-11-06.
 */

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private DBHelper dbhelper;
    Location mLastLocation;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        dbhelper = new DBHelper(getApplicationContext(), "nileblue.db", null,1);
        getLastLocation();

        return START_STICKY;
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
                            dbhelper.updateLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));

                            Log.d("EYEDEAR", String.valueOf(dbhelper.getLocationLat()));
                            Log.d("EYEDEAR", String.valueOf(dbhelper.getLocationLng()));

                        } else {
                            Log.d("DGJUNG", "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
