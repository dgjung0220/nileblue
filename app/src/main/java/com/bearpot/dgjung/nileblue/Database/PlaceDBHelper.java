package com.bearpot.dgjung.nileblue.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bearpot.dgjung.nileblue.VO.PlaceVo;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg.jung on 2017-11-13.
 */

public class PlaceDBHelper extends SQLiteOpenHelper {

    public PlaceDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {}

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public void save (PlaceVo place) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("place_id", place.getPlace_id());
        values.put("name", place.getPlaceName());
        values.put("Lat", place.getLocation().getLat());
        values.put("Lng", place.getLocation().getLng());
        db.insert("PLACES", null, values);
    }

    public void delete() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM PLACES");
    }
    public List<PlaceVo> select() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<PlaceVo> results = new ArrayList<PlaceVo>();

        Cursor cursor = db.query("PLACES", null, null, null, null, null, null);

        while(cursor.moveToNext()) {
            String place_id = cursor.getString(cursor.getColumnIndex("place_id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            double lat = cursor.getDouble(cursor.getColumnIndex("Lat"));
            double lng = cursor.getDouble(cursor.getColumnIndex("Lng"));

            Log.d("EYEDEAR", place_id +","+name);
            results.add(new PlaceVo(place_id, name, lat, lng));
        }

        cursor.close();
        return results;
    }
}
