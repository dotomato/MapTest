package com.chen.maptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements
        AMap.OnMapTouchListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener{


    private final static String TAG = "MainActivity";

    private final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    public MyAmapManeger mMyAmapManeger;

    public SelectHelper mSelectHelper;

    @BindView(R.id.map)
    public MapView mMapView;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;

    @BindView(R.id.user_edit_layout)
    public UserEditLayout mUserEditLayout;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPremisstion();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMapView.onCreate(savedInstanceState);
        initAmap();
        initLayout();
        Myserver.apiTest();

        mSelectHelper = new SelectHelper();
        new Thread(mSelectHelper).start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        switchShowMode(MODE_MAP,300);
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
        aMap.setOnCameraChangeListener(this);

    }

    private void initLayout(){
        mUserMessageLayout.setOverScrollCallback(new TopEventScrollView.OverScrollCallback() {
            @Override
            public void onOverScroll(ScrollView scrollView) {
                switchShowMode(MODE_MAP,300);
            }
        });


        mUserMessageLayout.setSpaceTouchEventCallback(new UserMessageLayout.SpaceTouchEventCallback() {
            @Override
            public void onSpaceTouchEvent(MotionEvent ev) {
                ev.offsetLocation(0,-mMapView.getY());
                mMapView.dispatchTouchEvent(ev);
            }
        });


        mUserEditLayout.setOverScrollCallback(new TopEventScrollView.OverScrollCallback() {
            @Override
            public void onOverScroll(ScrollView scrollView) {
                switchShowMode(MODE_MAP,300);
            }
        });


        mUserEditLayout.setSpaceTouchEventCallback(new UserEditLayout.SpaceTouchEventCallback() {
            @Override
            public void onSpaceTouchEvent(MotionEvent ev) {
                ev.offsetLocation(0,-mMapView.getY());
                mMapView.dispatchTouchEvent(ev);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mMyAmapManeger.onDestroy();
        mSelectHelper.stop();
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
        mMyAmapManeger.gotoLocation2(marker.getPosition());
        GetPointData gpd = new GetPointData();
        gpd.pointID=marker.getTitle();
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<GetPointResult>() {
                    @Override
                    void call() {
                        mUserMessageLayout.setUserMessage(mVar.pointData.userMessage);
                        mUserMessageLayout.setUserDescript(mVar.pointData.userID);
                        switchShowMode(MODE_MESSAGE,300);
                        mUserMessageLayout.initshow();
                    }
                });
        return true;   //false会移动地图到marker点，true不会
    }

    final static int MODE_MAP = 0;
    final static int MODE_MESSAGE = 1;
    final static int MODE_EDIT = 2;

    int lmode=-1;
    int mMode;
    long mDuration;
    public void switchShowMode(int mode, long duration){
        mMode = mode;
        mDuration = duration;
        if (lmode==MODE_EDIT) {
            mUserEditLayout.tryExit(new UserEditLayout.ExitCallback() {
                @Override
                public void call() {
                    _switchShowMode();
                }
            });
        } else {
            _switchShowMode();
        }
    }

    public void switchShowMode_force(int mode, long duration){
        mMode = mode;
        mDuration = duration;
        _switchShowMode();
    }

    private void _switchShowMode(){
        lmode=mMode;

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int dh = frame.height();
        int spaceHeight;

        mMapView.clearAnimation();
        mUserMessageLayout.clearAnimation();
        mUserEditLayout.clearAnimation();

        switch (mMode){
            case MODE_MAP:
                mMapView.animate().y(0).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(dh).setDuration(mDuration).start();
                mUserEditLayout.animate().y(dh).setDuration(mDuration).start();
                mFloatingActionButton.show();
                break;
            case MODE_MESSAGE:
                spaceHeight = mUserMessageLayout.getSpaceHeight();
                mMapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mUserEditLayout.animate().y(dh).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                break;
            case MODE_EDIT:
                spaceHeight = mUserEditLayout.getSpaceHeight();
                mMapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(dh).setDuration(mDuration).start();
                mUserEditLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                break;
        }
    }

    @OnClick(R.id.sendButton)
    public void newPoint(){
        LatLng l = mMyAmapManeger.getCurLatlng();

        NewPointData npd = new NewPointData();
        PointData pd = new PointData();

        pd.userID="开发客户端v0.01";
        pd.userMessage=mUserEditLayout.getUserEdit();

        pd.latitude = l.latitude;
        pd.longitude = l.longitude;

        npd.pointData = pd;

        Myserver.getApi().newPoint(npd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<NewPointResult>() {
                    @Override
                    void call() {
                        Log.d(TAG, "newPoint result: " + mVar.statue + " " + mVar.pointData.pointID);
                        selectArea();
                        switchShowMode_force(MODE_MAP,300);
                    }
                });
    }

    private void selectArea(){
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
                    void call() {
                        mMyAmapManeger.rmAllMarker();
                        Log.d(TAG, "selectArea result: " + mVar.statue + " " + mVar.pointsCount);
                        for (PointSimpleData psd:mVar.points) {
                            mMyAmapManeger.addMarker(psd);
                        }
                    }
                });
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    private class SelectHelper implements Runnable {

        private int mStatue;
        private static final int FINISH=0;
        private static final int RUN=1;
        private static final int STOP=2;

        @Override
        public void run() {
            while(true){
                if (mStatue==RUN) {
                    selectArea();
                    mStatue = FINISH;
                }
                if (mStatue==STOP){
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void call() {
            this.mStatue = RUN;
        }

        public void stop(){
            this.mStatue = STOP;
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        mSelectHelper.call();
    }

    @Override
    public void onBackPressed() {
        if (lmode==MODE_MAP)
            super.onBackPressed();
        else
            switchShowMode(MODE_MAP,300);
    }

    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        switchShowMode(MODE_EDIT,300);
    }
}
