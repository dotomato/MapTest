package com.chen.maptest;

import android.content.Context;
import android.util.Log;

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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

/**
 * Created by chen on 17-2-9.
 * Copyright *
 */

class MyAmapManeger implements LocationSource, AMapLocationListener{


    AMapLocation mLocationData;


    private Context mContext;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private Projection mProjection;

    private MapView mMapView;
    private AMap aMap;
    private boolean firstshow;

    MyAmapManeger(Context context, MapView mapView){
        mContext = context;
        mMapView = mapView;

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

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
    }

    void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(mContext);
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

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                mLocationData = amapLocation;
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

    void gotoLocation(AMapLocation amapLocation){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),
                        18,0,0));
        aMap.animateCamera(mCameraUpdate,500,null);
    }

    void gotoLocation2(LatLng latlng){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latlng,
                        18,0,0));
        aMap.animateCamera(mCameraUpdate,500,null);
    }

    //    MarkerOptions markerOption = new MarkerOptions();
//    BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(BitmapFactory
//            .decodeResource(getResources(),R.drawable.press_xingxing));
//
//        for (CloudItem item: mResult) {
//        Log.d(TAG,"detail "+item.toString());
//        LatLonPoint lf = item.getLatLonPoint();
//        markerOption.position(new LatLng(lf.getLatitude(),lf.getLongitude()))
//                .alpha(0.5f)
//                .draggable(false)
//                .icon(bd)
//                .anchor(0.5f,0.5f)
//                .setFlat(true);     //设置marker平贴地图效果// 将Marker设置为贴地显示，可以双指下拉地图查看效果
//        Marker marker = aMap.addMarker(markerOption);
//        MsgModel mm = new MsgModel(item,marker);
//        mMsgs.add(mm);
//    }
}
