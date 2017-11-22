package com.bearpot.dgjung.nileblue.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.MainActivity;
import com.bearpot.dgjung.nileblue.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

/**
 * Created by dg.jung on 2017-11-14.
 */

public class GeofenceTransitionsIntentService extends IntentService {

    private NotificationManager notificationManager;
    private Notification notification;

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {super.onCreate();}

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()) {
            int errorcode = geofencingEvent.getErrorCode();
            Log.d("GEOFENCE", "Location Services Error : " + errorcode);
        } else {
            int transitionType = geofencingEvent.getGeofenceTransition();

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.d("GEOFENCE", "GEOFENCE_ENTER");
            } else if (Geofence.GEOFENCE_TRANSITION_DWELL == transitionType) {
                Log.d("GEOFENCE", "GEOFENCE_DWELL");
                notifyResult();
            }
        }
    }

    private void notifyResult() {
        Log.d("EYEDEAR", "Notify weather");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("근처에 메모가 있습니다.")
                    .setContentText("자세히 보려면 클릭")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(666, notification);
    }
}
