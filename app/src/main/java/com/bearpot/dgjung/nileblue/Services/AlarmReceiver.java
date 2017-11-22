package com.bearpot.dgjung.nileblue.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.bearpot.dgjung.nileblue.R;

/**
 * Created by dg.jung on 2017-11-21.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;
    private Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Weather Information");
        mBuilder.setContentText("앱에서 확인하세요.");

        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);

        notificationManager.notify(111, mBuilder.build());
    }
}
