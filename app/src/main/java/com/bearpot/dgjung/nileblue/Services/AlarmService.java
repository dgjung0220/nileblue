package com.bearpot.dgjung.nileblue.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bearpot.dgjung.nileblue.Database.AlarmTimeHelper;
import com.bearpot.dgjung.nileblue.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dg.jung on 2017-11-21.
 */

public class AlarmService extends Service {

    IBinder mBinder = new AlarmBinder();

    private AlarmManager alarmManager;
    private NotificationManager notificationManager;
    private AlarmTimeHelper alarmTimeHelper;

    public class AlarmBinder extends Binder {
        public AlarmService getService() {
            return AlarmService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmTimeHelper = new AlarmTimeHelper(getApplicationContext(), "nileblue.db", null,1);

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        calendar.set(Calendar.YEAR, Integer.parseInt(new SimpleDateFormat("yyyy").format(date)));
        calendar.set(Calendar.MONTH, Integer.parseInt(new SimpleDateFormat("MM").format(date)));
        calendar.set(Calendar.DATE, Integer.parseInt(new SimpleDateFormat("dd").format(date)));
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimeHelper.getHourForAlarm());
        calendar.set(Calendar.MINUTE, alarmTimeHelper.getMinuteForAlarm());
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this,"알람이 설정되었습니다. "+calendar.getTimeInMillis(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}