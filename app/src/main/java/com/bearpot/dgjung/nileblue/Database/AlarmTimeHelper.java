package com.bearpot.dgjung.nileblue.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dg.jung on 2017-11-21.
 */

public class AlarmTimeHelper extends SQLiteOpenHelper {

    public AlarmTimeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void setAlarmTime(int hour, int minute) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM LOCATION");
        db.execSQL("INSERT OR REPLACE INTO LOCATION VALUES ("+hour+","+minute+")");
    }

    public int getHourForAlarm() {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT hour FROM ALARMTIME",null);
        while(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
    public int getMinuteForAlarm() {
        SQLiteDatabase db = getReadableDatabase();
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT hour FROM ALARMTIME",null);
        while(cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
}
