package com.chen.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

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
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener, AMap.OnMapTouchListener, CloudSearch.OnCloudSearchListener, AMap.OnMarkerClickListener {


    private final static String TAG = "MainActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    @BindView(R.id.map)
    public MapView mMapView;

    private AMap aMap;
    private UiSettings mUiSettings;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Projection mProjection;
    private CloudSearch mCloudSearch;

    private boolean firstshow;



    private ArrayList<MsgModel> mMsgs;

    private class MsgModel{
        CloudItem mCloudItem;
        Marker mMarker;

        public MsgModel(CloudItem cloudItem,Marker marker){
            mCloudItem=cloudItem;
            mMarker=marker;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPremisstion();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
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
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        // 设置定位监听
        aMap.setLocationSource(MainActivity.this);
// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
// 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        //设置地图手势监听器
        aMap.setOnMapTouchListener(this);

        mUiSettings = aMap.getUiSettings();
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

        mCloudSearch = new CloudSearch(this);// 初始化查询类
        mCloudSearch.setOnCloudSearchListener(this);// 设置回调函数
        // 设置中心点及检索范围

        firstshow=true;

        aMap.setOnMarkerClickListener(this);

        mMsgs = new ArrayList<MsgModel>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
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
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
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
                if (firstshow) {
                    gotoLocation(amapLocation);
                    makeQuery(amapLocation);
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

    private void gotoLocation2(LatLng latlng){
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(
                new CameraPosition(latlng,
                        18,0,0));
        aMap.animateCamera(mCameraUpdate,500,null);
    }

    private void makeQuery(AMapLocation amapLocation){
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
                amapLocation.getLatitude(), amapLocation.getLongitude()), 4000);
        //设置查询条件 mTableID是将数据存储到数据管理台后获得。
        try {
            CloudSearch.Query mQuery = new CloudSearch.Query("58941b3a7bbf195ae87f2565", "", bound);
            mCloudSearch.searchCloudAsyn(mQuery);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        Log.d(TAG,"onTouch "+motionEvent.toString());
    }


    @Override
    public void onCloudSearched(CloudResult cloudResult, int i) {
        Log.d(TAG,"onCloudSearched "+cloudResult.toString()+" "+cloudResult.getTotalCount());

        ArrayList<CloudItem> mResult = cloudResult.getClouds();
        MarkerOptions markerOption = new MarkerOptions();
        BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.press_xingxing));

        for (CloudItem item: mResult) {
            Log.d(TAG,"detail "+item.toString());
            LatLonPoint lf = item.getLatLonPoint();
            markerOption.position(new LatLng(lf.getLatitude(),lf.getLongitude()))
                    .alpha(0.5f)
                    .draggable(false)
                    .icon(bd)
                    .anchor(0.5f,0.5f)
                    .setFlat(true);     //设置marker平贴地图效果// 将Marker设置为贴地显示，可以双指下拉地图查看效果
            Marker marker = aMap.addMarker(markerOption);
            MsgModel mm = new MsgModel(item,marker);
            mMsgs.add(mm);
        }
       // markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int i) {
        Log.d(TAG,"onCloudItemDetailSearched "+cloudItemDetail.toString()+" "+i);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        gotoLocation2(marker.getPosition());
        MsgModel mm = findMsg(marker);
        if (mm!=null){
            Log.d(TAG,"onMarkerClick "+mm.toString());
        }
        return true;   //false会移动地图到marker点，true不会
    }

    private MsgModel findMsg(Marker marker){
        for (MsgModel mm:mMsgs) {
            if (mm.mMarker.equals(marker))
                return mm;
        }
        return null;
    }
}
