package com.chen.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.HashMap;


import com.chen.maptest.MyModel.*;


/**
 * Created by chen on 17-2-19.
 * Copyright *
 */




public class AmapAdapterActivity extends AppCompatActivity implements
        AMap.OnMapTouchListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener, LocationSource, AMapLocationListener {

    class MyLatlng {
        //TODO 完成坐标的高德系与标准系的转换
        double latitude;
        double longitude;

        MyLatlng(double v1, double v2) {
            latitude=v1;
            longitude=v2;
        }

        MyLatlng(LatLng latlng){
            latitude = latlng.latitude;
            longitude = latlng.longitude;
        }

        LatLng toLatlng(){
            return new LatLng(latitude,longitude);
        }

    }

    public void setMapAdaterCallback(MapAdaterCallback var){
        mMapAdaterCallback=var;
    }

    public View getMapView(){
        return mMapView;
    }

    public void gotoLocation2(MyLatlng latlng){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latlng.toLatlng(), aMap.getCameraPosition().zoom,0,0));
        aMap.animateCamera(mCameraUpdate,500,null);
    }

    public void gotoGpsLocation(){
        gotoLocation(gpsLocation);
    }

    MyLatlng getLeftTopLatlng(){
        return new MyLatlng(mProjection.fromScreenLocation(new Point(0,0)));
    }

    MyLatlng getRightBottomLatlng(){
        int a = mMapView.getWidth();
        int b = mMapView.getBottom();
        return new MyLatlng(mProjection.fromScreenLocation(new Point(a,b)));
    }

    MyLatlng getCurLatlng(){
        int a = mMapView.getWidth();
        int b = mMapView.getBottom();
        return new MyLatlng(mProjection.fromScreenLocation(new Point(a/2,b/2)));
    }

    void rmAllMarker(){
        for (Marker var:markerMap.values()) {
            var.remove();
        }
    }

    void addMarker(PointSimpleData psd){
        mMarkerOption.position(new LatLng(psd.latitude,psd.longitude))
                .draggable(false)
                .icon(mBitmapDescriptor)
                .anchor(0.5f,0.5f)
                .setFlat(true);     //设置marker平贴地图效果// 将Marker设置为贴地显示，可以双指下拉地图查看效果
        Marker marker = aMap.addMarker(mMarkerOption);
        marker.setTitle(psd.pointID);
        markerMap.put(psd.pointID,marker);
//        marker.setObject(psd);
    }




    private final static String TAG = "AmapAdapterActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    private MapView mMapView;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private Projection mProjection;

    private MarkerOptions mMarkerOption;
    private BitmapDescriptor mBitmapDescriptor;

    private HashMap<String,Marker> markerMap;
    private boolean firstshow;


    MapAdaterCallback mMapAdaterCallback=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPremisstion();

        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        initAmap();
    }


    private void initPremisstion(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
        }
    }

    private void initAmap(){

        aMap = mMapView.getMap();
        aMap.setOnMapTouchListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnCameraChangeListener(this);

        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        UiSettings mUiSettings = aMap.getUiSettings();
        //设置滑动手势
        mUiSettings.setScrollGesturesEnabled(true);
        //设置缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        //设置倾斜手势
        mUiSettings.setTiltGesturesEnabled(false);
        //设置旋转手势
        mUiSettings.setRotateGesturesEnabled(false);
        //设置放大缩小指示器
        mUiSettings.setZoomControlsEnabled(false);
        //设置指南针
        mUiSettings.setCompassEnabled(false);
        //设置定位按钮
        mUiSettings.setMyLocationButtonEnabled(false);
        //设置比例尺控件
        mUiSettings.setScaleControlsEnabled(false);
        //设置logo位置
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);

        //得到坐标转换器
        mProjection = aMap.getProjection();

        firstshow=true;

        markerMap = new HashMap<>();

        mMarkerOption= new MarkerOptions();
        mBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.press_xingxing_small));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mlocationClient.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mMapAdaterCallback!=null) {
            PointSimpleData psd = new PointSimpleData();
            psd.pointID = marker.getTitle();
            psd.latitude = marker.getPosition().latitude;
            psd.longitude = marker.getPosition().longitude;
            mMapAdaterCallback.MyMarkerClick(psd);
        }
        return true;   //false会移动地图到marker点，true不会
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyTouch(motionEvent);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyCameraChangeFinish();
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);

            mLocationOption.setMockEnable(true);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private AMapLocation gpsLocation;
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                gpsLocation = amapLocation;
                if (firstshow) {
                    gotoLocation(amapLocation);
                    firstshow = false;
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    private void gotoLocation(AMapLocation amapLocation){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),
                        18,0,0));
        aMap.animateCamera(mCameraUpdate,500,null);
    }

}