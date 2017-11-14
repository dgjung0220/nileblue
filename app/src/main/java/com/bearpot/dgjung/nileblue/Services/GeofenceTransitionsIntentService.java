package com.bearpot.dgjung.nileblue.Services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by dg.jung on 2017-11-14.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()) {
            int errorcode = geofencingEvent.getErrorCode();
            Log.d("EYEDEAR", "Location Services Error : " + errorcode);
        } else {
            int transitionType = geofencingEvent.getGeofenceTransition();

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.d("EYEDEAR", "GEOFENCE_ENTER");
            } else if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType) {
                Log.d("EYEDEAR", "GEOFENCE_DWELL");
            }
        }
    }
}
