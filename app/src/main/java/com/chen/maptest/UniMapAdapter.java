package com.chen.maptest;

import android.view.MotionEvent;

/**
 * Created by chen on 17-2-19.
 * Copyright *
 */


interface MapAdaterCallback{
    void MyTouch(MotionEvent motionEvent);
    void MyMarkerClick(PointSimpleData psd);
    void MyCameraChangeFinish();
}
