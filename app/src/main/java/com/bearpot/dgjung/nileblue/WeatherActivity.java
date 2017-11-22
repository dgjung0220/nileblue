package com.bearpot.dgjung.nileblue;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.bearpot.dgjung.nileblue.R;
import com.bearpot.dgjung.nileblue.Services.AlarmReceiver;

/**
 * Created by dg.jung on 2017-11-14.
 */

public class WeatherActivity extends AppCompatActivity {
    private TimePicker setTime;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_main);
        setTime = findViewById(R.id.alarm_time);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.time_confirm:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("EYEDEAR", String.valueOf(setTime.getHour()) + "," + setTime.getMinute());
                    returnToMain();
                }
        }
    }

    public void returnToMain() {
        Intent intent = new Intent(WeatherActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
