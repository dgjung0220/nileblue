package com.bearpot.dgjung.nileblue.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dg.jung on 2017-11-06.
 */

public class LocationStateDBHelper extends SQLiteOpenHelper {

    public LocationStateDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TABLE : CURRENT LOCATION STATE
        db.execSQL("CREATE TABLE IF NOT EXISTS LOCATION(" +
                "Lat double, " +
                "Lng double)");
        db.execSQL("CREATE TABLE IF NOT EXISTS MEMO(" +
                "memo_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Lat double, " +
                "Lng double, " +
                "description String)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PLACES(" +
                "place_id  String," +
                "name String," +
                "Lat double, " +
                "Lng double, " +
                "rating String," +
                "icon String," +
                "openNow boolean)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ALARMTIME(" +
                "alarm_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "hour INTEGER," +
                "minute INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public void updateLocation(String lat, String lng) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM LOCATION");
        db.execSQL("INSERT OR REPLACE INTO LOCATION VALUES ("+lat+","+lng+")");
    }

    public double getLocationLat() {
        SQLiteDatabase db = getReadableDatabase();
        double result = 0;

        Cursor cursor = db.rawQuery("SELECT Lat FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result = cursor.getDouble(0);
        }
        return result;
    }

    public double getLocationLng() {
        SQLiteDatabase db = getReadableDatabase();
        double result = 0;

        Cursor cursor = db.rawQuery("SELECT Lng FROM LOCATION", null);
        while (cursor.moveToNext()) {
            result = cursor.getDouble(0);
        }
        cursor.close();
        return result;
    }

}
