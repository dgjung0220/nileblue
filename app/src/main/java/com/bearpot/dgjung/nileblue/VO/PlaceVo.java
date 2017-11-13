package com.bearpot.dgjung.nileblue.VO;

import java.io.Serializable;

/**
 * Created by dg.jung on 2017-11-13.
 */

public class PlaceVo implements Serializable {
    private String place_id;
    private String name;
    private LocationVo locationVo;

    public PlaceVo() {}
    public PlaceVo(String place_id, String name, double lat, double lng) {
        this.place_id = place_id;
        this.name = name;
        this.locationVo = new LocationVo(lat,lng);
    }

    public String getPlace_id() {return place_id;}
    public String getPlaceName() {return name;}
    public LocationVo getLocation() {return locationVo;}
}
