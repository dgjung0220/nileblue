package com.bearpot.dgjung.nileblue.VO;

import java.io.Serializable;

/**
 * Created by dg.jung on 2017-11-09.
 */

public class LocationVo implements Serializable {
    private double lat;
    private double lng;

    public LocationVo(){}
    public LocationVo(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
}
