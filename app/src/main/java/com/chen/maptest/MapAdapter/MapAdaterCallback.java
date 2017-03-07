package com.chen.maptest.MapAdapter;

import android.view.MotionEvent;

import com.chen.maptest.MainActivity;
import com.chen.maptest.MyModel.*;

/**
 * Created by chen on 17-2-19.
 * Copyright *
 */

public interface MapAdaterCallback{
    void MyTouch(MotionEvent motionEvent);
    void MyMarkerClick(PointSimpleData psd);
    void MyCameraChangeStart();
    void MyCameraChangeFinish();
    void MyGPSRecive(MainActivity.MyLatlng latlng);
}
