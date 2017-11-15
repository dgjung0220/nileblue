package com.bearpot.dgjung.nileblue.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bearpot.dgjung.nileblue.R;
import com.bearpot.dgjung.nileblue.VO.WeatherVo;

/**
 * Created by dg.jung on 2017-11-15.
 */

public class WeatherFloatingService extends Service {
    private View mView;
    private WindowManager mManager;

    private float mTouchX, mTouchY;
    private int mViewX, mViewY;

    private WeatherVo weatherVo;
    private TextView weather_temperature = null;
    private TextView weather_huminity;
    private TextView weather_dewPoint;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weatherVo = (WeatherVo) intent.getSerializableExtra("weatherInfo");
        Log.d("EYEDEAR", weatherVo.toString());

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.weather_floating_main, null);

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager.addView(mView, mParams);

        weather_temperature = mView.findViewById(R.id.weather_temperature);
        weather_huminity = mView.findViewById(R.id.weather_huminity);
        weather_dewPoint = mView.findViewById(R.id.weather_dewPoint);

        weather_temperature.setText(weatherVo.getTemperature()+"/" + weatherVo.getFeelsLikeTemperature() + "℃" );
        weather_huminity.setText(weatherVo.getHuminity()+"%");
        weather_dewPoint.setText(weatherVo.getDewPoint()+"℃");

        View.OnTouchListener mTouchLister = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchX = motionEvent.getRawX();
                        mTouchY = motionEvent.getRawY();
                        mViewX = mParams.x;
                        mViewY = mParams.y;

                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        int x = (int) (motionEvent.getRawX() - mTouchX);
                        int y = (int) (motionEvent.getRawY() - mTouchY);

                        mParams.x = mViewX + x;
                        mParams.y = mViewY + y;

                        mManager.updateViewLayout(mView, mParams);

                        break;
                }
                return true;
            }
        };

        mView.setOnTouchListener(mTouchLister);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopSelf();
    }
}
