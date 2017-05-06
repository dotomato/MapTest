package com.chen.maptest.MapAdapter;

import android.animation.ObjectAnimator;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.maptest.R;
import com.chen.maptest.Utils.Animate;
import com.chen.maptest.Utils.ImageWrap;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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

public class MapAdapterLayout extends FrameLayout implements  MapView.OnMapChangedListener {
    private Context mContext;

    private final static String TAG = "MapAdapterLayout";

    private static MapView mMapView;
    private MapboxMap mMap;

    private MarkerOptions mReadMarkerOption;
    private Marker mReadMark;
    private Icon mReadIcon;
    private HashMap<String, MarkerView> markerMap;
    private Projection mProjection;
    private boolean firstshow;

    public static MapView getMapView(){
        return mMapView;
    }

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

        mMapView.addOnMapChangedListener(this);

        mMap.setMyLocationEnabled(true);

        mMapView.setStyleUrl("mapbox://styles/mapbox/light-v9");

        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAttributionEnabled(false);
        mUiSettings.setLogoEnabled(true);
        mUiSettings.setLogoGravity(Gravity.TOP | Gravity.START);

        mProjection = mMap.getProjection();


        mReadMarkerOption= new MarkerOptions();
        float scale = 0.5f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.postScale(scale, scale);
        Bitmap b2 = BitmapFactory.decodeResource(getResources(),R.drawable.down_arrow2);
        Bitmap b3 = Bitmap.createBitmap(b2, 0, 0, b2.getWidth(), b2.getHeight(), scaleMatrix, true);
        mReadIcon = IconFactory.recreate("ReadMarkerIcon",b3);

        mMap.setMaxZoomPreference(17);

        mMap.getMarkerViewManager().addMarkerViewAdapter(new MarkerViewAdapter(mContext, mMap));

    }

    private class MarkerViewAdapter extends MapboxMap.MarkerViewAdapter<MsgMarker> {

        private LayoutInflater inflater;
        private MapboxMap mapboxMap;

        MarkerViewAdapter(@NonNull Context context, @NonNull MapboxMap mapboxMap) {
            super(context);
            this.inflater = LayoutInflater.from(context);
            this.mapboxMap = mapboxMap;
        }

        @Nullable
        @Override
        public View getView(@NonNull MsgMarker marker, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView==null) {
                convertView = inflater.inflate(R.layout.main_frag_markerviewlayout, parent, false);
            }
            ImageView userIcon = (ImageView)convertView.findViewById(R.id.msgUserIcon);
            TextView userSmallText = (TextView)convertView.findViewById(R.id.userSmallText);
            ImageWrap.iconjust(mContext,marker.getUserIcon(),userIcon);
            userSmallText.setText(marker.getUserSmallTest());
            return convertView;
        }

        @Override
        public boolean onSelect(
                @NonNull final MsgMarker marker, @NonNull final View convertView, boolean reselectionForViewReuse) {
            ObjectAnimator animator = Animate.tada(convertView,1);
            animator.start();
            mMapAdaterCallback.MyMarkerClick(marker.getPointID(), marker.getUserID());
            return false;
        }
    }

    public void setMapAdaterCallback(MapAdaterCallback var){
        mMapAdaterCallback=var;
    }
    MapAdaterCallback mMapAdaterCallback=null;

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

    public void addMarker(MyLatlng l, String pointID, String usericon, String msgSmallText, String userID){
        if (markerMap.containsKey(pointID))
            return;
        MsgMarkOptions op = new MsgMarkOptions(usericon,msgSmallText,pointID,userID);
        op.position(l.toLatlng());
        MarkerView marker = mMap.addMarker(op);
        markerMap.put(pointID,marker);
    }

//    public void addReadMarker(MyLatlng latlng){
//        if (mMap == null) {
//            Log.w(TAG,"Map Box is not ready!");
//            return;
//        }
//        if (mReadMark!=null)
//            mReadMark.remove();
//
//        mReadMarkerOption.position(latlng.toLatlng())
//                .icon(mReadIcon);
//        mReadMark = mMap.addMarker(mReadMarkerOption);
//    }
//
//    public void removeReadMarker(){
//        if (mReadMark!=null)
//            mReadMark.remove();
//    }

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
        for (MarkerView var:markerMap.values()) {
            var.remove();
        }
    }

    public void rmUnuseMarkger(){

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
