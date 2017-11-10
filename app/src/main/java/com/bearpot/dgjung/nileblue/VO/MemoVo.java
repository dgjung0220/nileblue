package com.bearpot.dgjung.nileblue.VO;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by dg.jung on 2017-11-09.
 */

public class MemoVo implements Serializable {
    private int memo_id;
    private LocationVo location;
    private String description;

    public MemoVo(){}
    public MemoVo(int memo_id, String description) {
        this.memo_id = memo_id;
        this.description = description;
    }
    public MemoVo(int memo_id, LocationVo locationVo, String description) {
        this.memo_id = memo_id;
        this.location = locationVo;
        this.description = description;
    }
    public MemoVo(int memo_id, double lat, double lng, String description) {
            this.memo_id = memo_id;
            this.location = new LocationVo(lat,lng);
            this.description = description;
    }
    public int getMemoId() {
        return memo_id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription() {
        this.description = description;
    }

    public LocationVo getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        return new LatLng(location.getLat(), location.getLng());
    }
}
