package com.chen.maptest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.chen.maptest.MapAdapter.BmapAdapterActivity;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.Utils.OnceRunner;
import com.jaeger.library.StatusBarUtil;

import java.util.UUID;

public class MainActivity extends BmapAdapterActivity implements MapAdaterCallback, UserMessageLayout.NewPointFinish {


    private final static String TAG = "MainActivity";
    private static final boolean SHOULD_CUR = false;


    static public final int SELECT_ALBUM_IMG = 0;

    private OnceRunner mSelectHelper;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    @BindView(R.id.viewpager)
    public View mUMHeader;

    private View mapView;

    private ActionBarDrawerToggle mDrawerToggle;

    private boolean shouldInitonResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        StatusBarUtil.setColorForDrawerLayout(this,(DrawerLayout) findViewById(R.id.activity_main), Color.WHITE,0);

        mapView = getMapView();

        initLayout();

        Myserver.apiTest();

        initUserinfo();

        mSelectHelper = new OnceRunner() {
            @Override
            protected void call() {
                selectArea();
            }
        };
        mSelectHelper.setInternal(1000);
        new Thread(mSelectHelper).start();

        setMapAdaterCallback(this);

        setBoardcastReceiver();

        switchShowMode(MODE_MAP,300);
    }

    private void setBoardcastReceiver() {
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(GlobalConst.UPDATE_USERINFO_VIEW);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case GlobalConst.UPDATE_USERINFO_VIEW:
                        shouldInitonResume=true;
                        break;
                }
            }
        },ifilter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume(){
        super.onResume();
        if (shouldInitonResume) {
            initUserView();
            shouldInitonResume = false;
        }
    }

    private void initGlobalVar(){
//        GlobalVar.viewLatlng = new MyLatlng(-1,-1);
//        GlobalVar.mUserinfo = new Userinfo();
    }

    private void initLayout(){
        mUserMessageLayout.setExitCallback(new UserMessageLayout.ExitCallback() {
            @Override
            public void call() {
                switchShowMode_force(MODE_MAP,300);
            }
        });


        mUserMessageLayout.setSpaceTouchEventCallback(new UserMessageLayout.SpaceTouchEventCallback() {
            @Override
            public void onSpaceTouchEvent(MotionEvent ev) {
                ev.offsetLocation(0,-mapView.getY());
                mapView.dispatchTouchEvent(ev);
            }
        });

        mUserMessageLayout.setNewPointFinishCallback(this);
//        drawerLayoutinit();
    }

    private void initUserinfo(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String userID = pref.getString("userID",null);
        if (userID==null) {
            userID = genUserID();


            UserID nuid = new UserID();
            nuid.userID=userID;
            Myserver.getApi().newuser(nuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<UserIDResult>() {
                        @Override
                        public void call() {
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userID", mVar.userinfo.userID);
                            editor.apply();

                            GlobalVar.mUserinfo = mVar.userinfo;
                            initUserView();
                        }
                    });
        } else {
            UserID nuid = new UserID();
            nuid.userID=userID;
            Myserver.getApi().getuser(nuid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<UserIDResult>() {
                        @Override
                        public void call() {
                            GlobalVar.mUserinfo = mVar.userinfo;
                            initUserView();
                        }
                    });
        }
    }

    private String genUserID(){
        return UUID.randomUUID().toString();
    }


    private void initUserView(){
//        if (GlobalVar.mUserinfo==null)
//            return;
        mLeftDrawerLayout.initUserView();
    }

    private void drawerLayoutinit(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSelectHelper.stop();
    }

    public void MyTouch(MotionEvent motionEvent) {

    }

    public void MyMarkerClick(PointSimpleData psd) {
        gotoLocation2(new MyLatlng(psd.latitude,psd.longitude));
        GetPointData gpd = new GetPointData();
        gpd.pointID=psd.pointID;
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<GetPointResult>() {
                    @Override
                    public void call() {
                        switchShowMode(MODE_MESSAGE,300);
                        mUserMessageLayout.initshow(MODE_MESSAGE,mVar.pointData);
                    }
                });

        UserID nuid = new UserID();
        nuid.userID=psd.userID;
        Myserver.getApi().getuser(nuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserIDResult>() {
                    @Override
                    public void call() {
                        mUserMessageLayout.initshow2(mVar.userinfo);
                    }
                });
    }


    public void MyCameraChangeFinish() {
        GlobalVar.viewLatlng = getViewLatlng();

        mSelectHelper.start();
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        GlobalVar.gpsLatlng = latlng;
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
            mUserMessageLayout.tryExit();
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

        mapView.clearAnimation();
        mUserMessageLayout.clearAnimation();

        switch (mMode){
            case MODE_MAP:
                mapView.animate().y(0).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(dh).setDuration(mDuration).start();
                mFloatingActionButton.show();
                mUMHeader.setVisibility(View.INVISIBLE);
                break;
            case MODE_MESSAGE:
                spaceHeight = mUserMessageLayout.getSpaceHeight();
                mapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                mUMHeader.setVisibility(View.VISIBLE);
                break;
            case MODE_EDIT:
                spaceHeight = mUserMessageLayout.getSpaceHeight();
                mapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                mUMHeader.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void selectArea(){
        SelectAreaData sad = new SelectAreaData();
        MyLatlng lt = getLeftTopLatlng();
        MyLatlng rb = getRightBottomLatlng();
        sad.left_top_latitude = lt.latitude;
        sad.left_top_longitude = lt.longitude;
        sad.right_bottom_latitude = rb.latitude;
        sad.right_bottom_longitude = rb.longitude;

        Myserver.getApi().selectArea(sad)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<SelectAreaResult>() {
                    @Override
                    public void call() {
                        rmAllMarker();
                        Log.d(TAG, "selectArea result: " + mVar.statue + " " + mVar.pointsCount);
                        for (PointSimpleData psd:mVar.points) {
                            addMarker(psd);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.activity_main);
        if(dl.isDrawerOpen(mLeftDrawerLayout))
            dl.closeDrawer(mLeftDrawerLayout);
        else if (lmode==MODE_MAP)
            super.onBackPressed();
        else
            switchShowMode(MODE_MAP,300);
    }

    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        if (GlobalVar.mUserinfo==null
                || (SHOULD_CUR && GlobalVar.viewLatlng==null)
                || (!SHOULD_CUR && GlobalVar.gpsLatlng==null)){
            Toast.makeText(this,"还没有连上网络",Toast.LENGTH_SHORT).show();
            return;
        }

        switchShowMode(MODE_EDIT,300);
        mUserMessageLayout.initshow(MODE_EDIT,null);
        mUserMessageLayout.initshow2(GlobalVar.mUserinfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode){
            case SELECT_ALBUM_IMG:
                mUserMessageLayout.ResultCallback(requestCode,resultCode,data);
                break;
        }
    }

    @Override
    public void NPFcall() {
        selectArea();
        switchShowMode_force(MODE_MAP,300);
    }
}
