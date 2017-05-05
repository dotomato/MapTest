package com.chen.maptest.MapAdapter;

import android.view.MotionEvent;

import com.chen.maptest.NetDataType.*;

/**
 * Created by chen on 17-2-19.
 * Copyright *
 */

public interface MapAdaterCallback{
    void MyTouch(MotionEvent motionEvent);
    void MyMarkerClick(String pointID, String userID);
    void MyCameraChangeStart();
    void MyCameraChangeFinish();
    void MyGPSRecive(MyLatlng latlng);
    void firstLocation(MyLatlng latlng);
}
