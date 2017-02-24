package com.chen.maptest;

import android.view.MotionEvent;

import com.chen.maptest.MyModel.*;

/**
 * Created by chen on 17-2-19.
 * Copyright *
 */


interface MapAdaterCallback{
    void MyTouch(MotionEvent motionEvent);
    void MyMarkerClick(PointSimpleData psd);
    void MyCameraChangeFinish();
}
