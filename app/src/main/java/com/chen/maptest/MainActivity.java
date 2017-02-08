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
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AMap.OnMapTouchListener, AMap.OnMarkerClickListener {


    private final static String TAG = "MainActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    MyAmapManeger mmyAmapManeger;

    @BindView(R.id.map)
    public MapView mMapView;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPremisstion();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);
        initAmap();

        Myserver.apiTest();
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
        mmyAmapManeger = new MyAmapManeger(this, mMapView);

        AMap aMap = mMapView.getMap();
        aMap.setOnMapTouchListener(this);
        aMap.setOnMarkerClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mmyAmapManeger.onDestroy();
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
    public void onTouch(MotionEvent motionEvent) {
//        Log.d(TAG,"onTouch "+motionEvent.toString());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mmyAmapManeger.gotoLocation2(marker.getPosition());
        return true;   //false会移动地图到marker点，true不会
    }


    @OnClick(R.id.button)
    public void newPoint(){
        if (mmyAmapManeger.mLocationData==null)
            return;
        NewPointData npd = new NewPointData();
        PointData pd = new PointData();

        Random random = new Random();
        pd.userID="ID"+random.nextInt();
        pd.message="Test message!";
        pd.latitude = mmyAmapManeger.mLocationData.getLatitude();
        pd.longitude = mmyAmapManeger.mLocationData.getLongitude();

        npd.pointData = pd;

        Myserver.getServer().newPoint(npd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<NewPointResult>() {
                    @Override
                    void call(NewPointResult var) {
                        Log.d(TAG, "newPoint result: " + var.statue + " " + var.pointData.pointID);
                    }
                });
    }
}
