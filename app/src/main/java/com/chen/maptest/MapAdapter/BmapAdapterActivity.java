package com.chen.maptest.MapAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;


import com.chen.maptest.GlobalVar;
import com.chen.maptest.MyModel.*;
import com.chen.maptest.R;

/**
 * Created by chen on 17-2-19.
 * Copyright *
 */



public class BmapAdapterActivity extends AppCompatActivity implements
        BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMapTouchListener, BaiduMap.OnMarkerClickListener ,
        BDLocationListener, BaiduMap.OnMapLoadedCallback{

    @Override
    public void onMapLoaded() {
        mProjection = bMap.getProjection();
        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyCameraChangeFinish();
    }

    public class MyLatlng {
        //TODO 完成坐标的百度系与标准系的转换
        public double latitude;
        public double longitude;

        public MyLatlng(double v1, double v2) {
            latitude=v1;
            longitude=v2;
        }

        public MyLatlng(LatLng latlng){
            latitude = latlng.latitude;
            longitude = latlng.longitude;
        }

        public LatLng toLatlng(){
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
        MapStatusUpdate mCameraUpdate =
                MapStatusUpdateFactory.newLatLngZoom(latlng.toLatlng(), bMap.getMapStatus().zoom);
        bMap.animateMapStatus(mCameraUpdate,500);
    }

    public void gotoGpsLocation(){
        gotoLocation(gpsLocation);
    }

    public MyLatlng getLeftTopLatlng(){
        return new MyLatlng(mProjection.fromScreenLocation(new Point(0,0)));
    }

    public MyLatlng getRightBottomLatlng(){
        int a = mMapView.getWidth();
        int b = mMapView.getBottom();
        return new MyLatlng(mProjection.fromScreenLocation(new Point(a,b)));
    }

    public MyLatlng getGPSLatlng(){
        return gpsLocation==null?new MyLatlng(-1,-1):new MyLatlng(gpsLocation.getLatitude(),gpsLocation.getLongitude());
    }

    public MyLatlng getViewLatlng(){
        int a = mMapView.getWidth();
        int b = mMapView.getBottom();
        return mProjection==null?new MyLatlng(-1,-1):new MyLatlng(mProjection.fromScreenLocation(new Point(a/2,b/2))) ;
    }

    public void rmAllMarker(){
        for (Marker var:markerMap.values()) {
            var.remove();
        }
    }

    public void addMarker(PointSimpleData psd){
        mMarkerOption.position(new LatLng(psd.latitude,psd.longitude))
                .draggable(false)
                .icon(mBitmapDescriptor)
                .anchor(0.5f,0.5f)
                .flat(true);     //设置marker平贴地图效果// 将Marker设置为贴地显示，可以双指下拉地图查看效果
        Marker marker = (Marker) bMap.addOverlay(mMarkerOption);
        marker.setTitle(psd.pointID);
        markerMap.put(psd.pointID,marker);
        PSDMap.put(psd.pointID,psd);
    }




    private final static String TAG = "AmapAdapterActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    private MapView mMapView;
    private BaiduMap bMap;

    private LocationClient mlocationClient;
    private Projection mProjection;

    private MarkerOptions mMarkerOption;
    private BitmapDescriptor mBitmapDescriptor;
    private HashMap<String,Marker> markerMap;
    private HashMap<String,PointSimpleData> PSDMap;
    private boolean firstshow;


    MapAdaterCallback mMapAdaterCallback=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPremisstion();
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.map);

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

        bMap = mMapView.getMap();
        bMap.setOnMapTouchListener(this);
        bMap.setOnMarkerClickListener(this);
        bMap.setOnMapStatusChangeListener(this);
        bMap.setOnMapLoadedCallback(this);

        // 设置定位监听
//        bMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        bMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
//        bMap.setMyLocationType(BaiduMap.LOCATION_TYPE_LOCATE);

        UiSettings mUiSettings = bMap.getUiSettings();
        //设置滑动手势
        mUiSettings.setScrollGesturesEnabled(true);
        //设置缩放手势
        mUiSettings.setZoomGesturesEnabled(true);
        //设置倾斜手势
//        mUiSettings.setTiltGesturesEnabled(false);
        //设置旋转手势
        mUiSettings.setRotateGesturesEnabled(false);
        //设置放大缩小指示器
//        mUiSettings.setZoomControlsEnabled(false);
        //设置指南针
        mUiSettings.setCompassEnabled(false);
        //设置定位按钮
//        mUiSettings.setMyLocationButtonEnabled(false);
        //设置比例尺控件
//        mUiSettings.setScaleControlsEnabled(false);
        //设置logo位置
        mMapView.setLogoPosition(LogoPosition.logoPostionleftBottom);

        //得到坐标转换器，放在OnMapLoad后获取
//        mProjection = bMap.getProjection();

        firstshow=true;

        markerMap = new HashMap<>();
        PSDMap = new HashMap<>();

        mMarkerOption= new MarkerOptions();
        mBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.press_xingxing_small));


        mlocationClient = new LocationClient(getApplicationContext());
        LocationClientOption mLocationOption = new LocationClientOption();
        mLocationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationOption.setScanSpan(3000);
//        mLocationOption.setOpenGps(true);
        mLocationOption.setIgnoreKillProcess(false);
        mlocationClient.setLocOption(mLocationOption);
        mlocationClient.start();//启动定位
        mlocationClient.registerLocationListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mlocationClient.stop();
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
            mMapAdaterCallback.MyMarkerClick(PSDMap.get(marker.getTitle()));
        }
        return true;   //false会移动地图到marker点，true不会
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyTouch(motionEvent);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyCameraChangeFinish();
    }


    private BDLocation gpsLocation;
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
//        bdLocation.getLocType();
        gpsLocation = bdLocation;

        if (mMapAdaterCallback!=null)
            mMapAdaterCallback.MyGPSRecive(getGPSLatlng());

        if (firstshow) {
            gotoLocation(bdLocation);
            firstshow = false;
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    private void gotoLocation(BDLocation amapLocation){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        MapStatusUpdate mCameraUpdate =
            MapStatusUpdateFactory.newLatLngZoom(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),18);
        bMap.animateMapStatus(mCameraUpdate,500);
    }

}
