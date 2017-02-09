package com.chen.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements AMap.OnMapTouchListener, AMap.OnMarkerClickListener {


    private final static String TAG = "MainActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    MyAmapManeger mMyAmapManeger;

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
        mMyAmapManeger = new MyAmapManeger(this, mMapView);

        AMap aMap = mMapView.getMap();
        aMap.setOnMapTouchListener(this);
        aMap.setOnMarkerClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mMyAmapManeger.onDestroy();
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
        mMyAmapManeger.gotoLocation2(marker.getPosition());
        return true;   //false会移动地图到marker点，true不会
    }


    @OnClick(R.id.button)
    public void newPoint(){
        if (mMyAmapManeger.mLocationData==null)
            return;
        NewPointData npd = new NewPointData();
        PointData pd = new PointData();

        Random random = new Random();
        pd.userID="ID"+random.nextInt();
        pd.message="Test message!";
        pd.latitude = mMyAmapManeger.mLocationData.getLatitude();
        pd.longitude = mMyAmapManeger.mLocationData.getLongitude();

        npd.pointData = pd;

        Myserver.getApi().newPoint(npd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<NewPointResult>() {
                    @Override
                    void call(NewPointResult var) {
                        Log.d(TAG, "newPoint result: " + var.statue + " " + var.pointData.pointID);
                    }
                });
    }

    @OnClick(R.id.button2)
    public void selectArea(){
        SelectAreaData sad = new SelectAreaData();
        LatLng lt = mMyAmapManeger.getLeftTopLatlng();
        LatLng rb = mMyAmapManeger.getRightBottomLatlng();
        sad.left_top_latitude = lt.latitude;
        sad.left_top_longitude = lt.longitude;
        sad.right_bottom_latitude = rb.latitude;
        sad.right_bottom_longitude = rb.longitude;

        Myserver.getApi().selectArea(sad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<SelectAreaResult>() {
                    @Override
                    void call(SelectAreaResult var) {
                        Log.d(TAG, "selectArea result: " + var.statue + " " + var.pointsCount);
                        for (PointSimpleData psd:var.points) {
                            mMyAmapManeger.addMarker(new LatLng(psd.latitude,psd.longitude),psd.pointID);
                        }
                    }
                });
    }
}
