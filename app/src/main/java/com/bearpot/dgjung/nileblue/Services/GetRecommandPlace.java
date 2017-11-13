package com.bearpot.dgjung.nileblue.Services;

import android.util.Log;

import com.bearpot.dgjung.nileblue.VO.PlaceVo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dg.jung on 2017-11-13.
 */

public class GetRecommandPlace {

    private ArrayList<PlaceVo> recommandPlace;

    public GetRecommandPlace() {}
    public List<PlaceVo> sendByHttp(double lat, double lng, int myRadius, String type) {
        recommandPlace = new ArrayList<PlaceVo>();

        String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
        HttpGet get = new HttpGet(URL + "?location=" + lat + "," + lng
                + "&radius=" + myRadius
                + "&types=" + type
                + "&key=" + "AIzaSyAv7acRpN8ZEL0QXqQNLpjm4cezHILCVOk");
        Log.d("EYEDEAR", "Place API 이용 내역 : "+
                URL +
                "?location=" + lat + "," + lng
                + "&radius=" + myRadius
                + "&name=" + type
                + "&key=" + "AIzaSyAv7acRpN8ZEL0QXqQNLpjm4cezHILCVOk");

        DefaultHttpClient client = new DefaultHttpClient();

        try {
            HttpResponse response = client.execute(get);
            HttpEntity resEntry = response.getEntity();

            String jsonString = EntityUtils.toString(resEntry);
            JSONObject jsonObject = new JSONObject(jsonString);
            String code = jsonObject.getString("status");
            Log.d("EYEDEAR", code);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                String place_id = jsonArray.getJSONObject(i).getString("place_id");
                String placeName = jsonArray.getJSONObject(i).getString("name");
                double placeLat = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                double placeLng = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                recommandPlace.add(new PlaceVo(place_id, placeName, placeLat, placeLng));
            }
        } catch(Exception e) {
            e.printStackTrace();
            //client.getConnectionManager().shutdown();
        }

        return recommandPlace;
    }
}
