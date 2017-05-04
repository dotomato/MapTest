package com.chen.maptest.MapAdapter;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MyLatlng {
    //TODO 完成坐标的MapBox系与标准系的转换
    public double latitude;
    public double longitude;

    public MyLatlng(double v1, double v2) {
        latitude=v1;
        longitude=v2;
    }

    public MyLatlng(LatLng latlng){
        latitude = latlng.getLatitude();
        longitude = latlng.getLongitude();
    }

    public LatLng toLatlng(){
        return new LatLng(latitude,longitude);
    }

}
