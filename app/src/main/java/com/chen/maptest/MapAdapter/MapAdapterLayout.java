package com.chen.maptest.MapAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.R;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import static com.mapbox.mapboxsdk.maps.MapView.REGION_WILL_CHANGE_ANIMATED;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MapAdapterLayout extends FrameLayout implements MapboxMap.OnMarkerClickListener, MapView.OnMapChangedListener {
    private Context mContext;

    private final static String TAG = "MapAdapterLayout";

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

    public MapAdapterLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MapAdapterLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapAdapterLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;

        firstshow = true;
        markerMap = new HashMap<>();
        PSDMap = new HashMap<>();

    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mMapView = (MapView) findViewById(R.id.map);
    }

    public void onCreate(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        if (!isInEditMode()){
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(MapboxMap mapboxMap) {
                    mMap = mapboxMap;
                    initMmap();
                }
            });
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initMmap() {
        LocationEngine locationEngine = LocationSource.getLocationEngine(mContext);
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

        mMapView.setStyleUrl("mapbox://styles/mapbox/dark-v9");

        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAttributionEnabled(false);
        mUiSettings.setLogoEnabled(true);
        mUiSettings.setLogoGravity(Gravity.TOP | Gravity.LEFT);

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

        mMap.setMaxZoomPreference(15);
    }

    public void onStart() {
        mMapView.onStart();
    }

    public void onResume() {
        mMapView.onResume();
    }

    public void onPause() {
        mMapView.onPause();
    }

    public void onStop() {
        mMapView.onStop();
    }

    public void onLowMemory() {
        mMapView.onLowMemory();
    }


    public void onDestroy() {
        mMapView.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        mMapView.onSaveInstanceState(outState);
    }

    public void setMapAdaterCallback(MapAdaterCallback var){
        mMapAdaterCallback=var;
    }
    MapAdaterCallback mMapAdaterCallback=null;

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (mMapAdaterCallback!=null) {
            PointSimpleData psd = PSDMap.get(marker.getTitle());
            if (psd == null)
                return true;
            mMapAdaterCallback.MyMarkerClick(psd);
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

    public void addMarker(PointSimpleData psd){
        mMarkerOption.position(new LatLng(psd.latitude,psd.longitude))
                .icon(mIcon);
        Marker marker = mMap.addMarker(mMarkerOption);
        marker.setTitle(psd.pointID);
        markerMap.put(psd.pointID,marker);
        PSDMap.put(psd.pointID,psd);
    }

    public void addReadMarker(MyLatlng latlng){
        if (mMap == null) {
            Log.w(TAG,"Map Box is not ready!");
            return;
        }
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

    public void gotoLocationSmooth(MyLatlng latlng){
        if (mMap == null) {
            Log.w(TAG,"Map Box is not ready!");
            return;
        }
        CameraUpdate mCameraUpdate =
                CameraUpdateFactory.newLatLng(latlng.toLatlng());
        mMap.easeCamera(mCameraUpdate,500);
    }

    public void gotoLocation(MyLatlng latlng, double zoom){
        if (mMap == null) {
            Log.w(TAG,"Map Box is not ready!");
            return;
        }
        CameraUpdate mCameraUpdate =
                CameraUpdateFactory.newLatLngZoom(latlng.toLatlng(), zoom);
        mMap.animateCamera(mCameraUpdate,1000);
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

    public void onZoomCtrl(double z){
        if (mMap == null) {
            Log.w(TAG,"Map Box is not ready!");
            return;
        }
        if (z>1)
            z = 1;
        else if (z<0)
            z = 0;
        double v1 = mMap.getMaxZoomLevel();
        double v2 = mMap.getMinZoomLevel();
        double v3 = v2 + (v1 - v2)*z;
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(v3);
        mMap.animateCamera(cameraUpdate,100);
    }

    public double getZoom(){
        if (mMap == null) {
            Log.w(TAG,"Map Box is not ready!");
            return 0;
        }
        double v = mMap.getCameraPosition().zoom;
        return (v-mMap.getMinZoomLevel())/(mMap.getMaxZoomLevel()-mMap.getMinZoomLevel());
    }
}
