package com.chen.maptest.MapAdapter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chen.maptest.GlobalVar;
import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;

import java.util.HashMap;

import static com.mapbox.mapboxsdk.maps.MapView.REGION_DID_CHANGE;
import static com.mapbox.mapboxsdk.maps.MapView.REGION_DID_CHANGE_ANIMATED;
import static com.mapbox.mapboxsdk.maps.MapView.REGION_WILL_CHANGE;
import static com.mapbox.mapboxsdk.maps.MapView.REGION_WILL_CHANGE_ANIMATED;

/**
 * Created by chen on 17-3-26.
 * Copyright *
 */

public class MmapAdapterActivity extends AppCompatActivity implements MapboxMap.OnMarkerClickListener, MapView.OnMapChangedListener {


    private final static String TAG = "MmapAdapterActivity";
    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    private MapView mMapView;
    private MapboxMap mMap;

    private MarkerOptions mMarkerOption;
    private MarkerOptions mReadMarkerOption;
    private Marker mReadMark;
    private Icon mReadIcon;
    private HashMap<String, Marker> markerMap;
    private HashMap<String, PointSimpleData> PSDMap;
    private Icon mIcon;
    private Projection mProjection;
    private boolean firstshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        firstshow = true;
        markerMap = new HashMap<>();
        PSDMap = new HashMap<>();

        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.MapBox_access_token));
        initPremisstion();
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMap = mapboxMap;
                initMmap();
            }
        });
    }

    private void initPremisstion() {
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

    @SuppressWarnings("MissingPermission")
    private void initMmap() {
        LocationEngine locationEngine = LocationSource.getLocationEngine(this);
        locationEngine.activate();
        locationEngine.addLocationEngineListener(new LocationEngineListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onLocationChanged(Location location) {
                if (mMapAdaterCallback != null)
                    mMapAdaterCallback.MyGPSRecive(new MyLatlng(location.getLatitude(), location.getLongitude()));
                if (firstshow) {
                    mMapAdaterCallback.firstLocation(new MyLatlng(location.getLatitude(), location.getLongitude()));
                    firstshow = false;
                }
            }
        });
        locationEngine.requestLocationUpdates();

//        mMap.setOnMapTouchListener(this);
        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMapStatusChangeListener(this);
        mMapView.addOnMapChangedListener(this);

        mMap.setMyLocationEnabled(true);

        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAttributionEnabled(false);
        mUiSettings.setLogoEnabled(true);

        mProjection = mMap.getProjection();


        mMarkerOption= new MarkerOptions();
        float scale = 0.5f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(scale, scale);
        Bitmap b0 = BitmapFactory.decodeResource(getResources(),R.drawable.map_msg_icon);
        Bitmap b1 = Bitmap.createBitmap(b0, 0, 0, b0.getWidth(), b0.getHeight(), scaleMatrix, true);
        mIcon = IconFactory.recreate("MarkerIcon",b1);

        mReadMarkerOption= new MarkerOptions();
        Bitmap b2 = BitmapFactory.decodeResource(getResources(),R.drawable.down_arrow2);
        Bitmap b3 = Bitmap.createBitmap(b2, 0, 0, b2.getWidth(), b2.getHeight(), scaleMatrix, true);
        mReadIcon = IconFactory.recreate("ReadMarkerIcon",b3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    public void setMapAdaterCallback(MapAdaterCallback var){
        mMapAdaterCallback=var;
    }
    MapAdaterCallback mMapAdaterCallback=null;

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (mMapAdaterCallback!=null) {
            mMapAdaterCallback.MyMarkerClick(PSDMap.get(marker.getTitle()));
        }
        return true;   //false会移动地图到marker点，true不会
    }

    public View getMapView(){
        return mMapView;
    }

    @Override
    public void onMapChanged(int change) {
        switch (change){
            case REGION_WILL_CHANGE_ANIMATED:
                if (mMapAdaterCallback!=null)
                    mMapAdaterCallback.MyCameraChangeStart();
                break;
            case REGION_DID_CHANGE:
            case REGION_DID_CHANGE_ANIMATED:
                if (mMapAdaterCallback!=null)
                    mMapAdaterCallback.MyCameraChangeFinish();
                break;
        }
    }

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

    public void addMarker(PointSimpleData psd){
        mMarkerOption.position(new LatLng(psd.latitude,psd.longitude))
                .icon(mIcon);
        Marker marker = mMap.addMarker(mMarkerOption);
        marker.setTitle(psd.pointID);
        markerMap.put(psd.pointID,marker);
        PSDMap.put(psd.pointID,psd);
    }

    public void addReadMarker(MyLatlng latlng){
        if (mReadMark!=null)
            mReadMark.remove();

        mReadMarkerOption.position(latlng.toLatlng())
                .icon(mReadIcon);
        mReadMark = mMap.addMarker(mReadMarkerOption);
    }

    public void removeReadMarker(){
        if (mReadMark!=null)
            mReadMark.remove();
    }

    public void gotoLocation2(MyLatlng latlng){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate =
                CameraUpdateFactory.newLatLng(latlng.toLatlng());
        mMap.easeCamera(mCameraUpdate,500);
    }

    public void gotoLocation2(MyLatlng latlng, double zoom){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate =
                CameraUpdateFactory.newLatLngZoom(latlng.toLatlng(), zoom);
        mMap.animateCamera(mCameraUpdate,0);
    }

    public void rmAllMarker(){
        for (Marker var:markerMap.values()) {
            var.remove();
        }
    }

    public MyLatlng pointToMyLatlng(PointF p){
        return mProjection==null?new MyLatlng(-1,-1):new MyLatlng(mProjection.fromScreenLocation(p)) ;
    }

    public PointF myLatlgnToPoint(MyLatlng l){
        return mProjection==null?new PointF(-1,-1):mProjection.toScreenLocation(l.toLatlng()) ;
    }
}