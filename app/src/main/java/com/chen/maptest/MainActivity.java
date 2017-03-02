package com.chen.maptest;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.chen.maptest.MapAdapter.BmapAdapterActivity;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyView.TopEventScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.Utils.OnceRunner;

import java.util.UUID;

public class MainActivity extends BmapAdapterActivity implements MapAdaterCallback {


    private final static String TAG = "MainActivity";

    private OnceRunner mSelectHelper;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    private View mapView;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mapView = getMapView();

        initGlobalVar();

        initLayout();

        initUserinfo();

        Myserver.apiTest();

        mSelectHelper = new OnceRunner() {
            @Override
            protected void call() {
                selectArea();
            }
        };
        mSelectHelper.setInternal(1000);
        new Thread(mSelectHelper).start();

        setMapAdaterCallback(this);

        switchShowMode(MODE_MAP,300);
    }

    @Override
    protected void onResume(){
        super.onResume();
        initUserView();
    }

    private void initGlobalVar(){
        GlobalVar.curLatlng = new MyLatlng(-1,-1);
        GlobalVar.mUserinfo = new Userinfo();
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
                ev.offsetLocation(0,-mapView.getY());
                mapView.dispatchTouchEvent(ev);
            }
        });
        drawerLayoutinit();
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
        if (GlobalVar.mUserinfo==null)
            return;
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
                        GlobalVar.mUserinfo = mVar.userinfo;
                        mUserMessageLayout.initshow2(mVar.userinfo);
                    }
                });
    }


    public void MyCameraChangeFinish() {
        setCurLatlng(GlobalVar.curLatlng);
        mSelectHelper.start();
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
            mUserMessageLayout.tryExit(new UserMessageLayout.ExitCallback() {
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

        mapView.clearAnimation();
        mUserMessageLayout.clearAnimation();

        switch (mMode){
            case MODE_MAP:
                mapView.animate().y(0).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(dh).setDuration(mDuration).start();
                mFloatingActionButton.show();
                break;
            case MODE_MESSAGE:
                spaceHeight = mUserMessageLayout.getSpaceHeight();
                mapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                break;
            case MODE_EDIT:
                spaceHeight = mUserMessageLayout.getSpaceHeight();
                mapView.animate().y(-(dh-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                break;
        }
    }

    @OnClick(R.id.sendbutton)
    public void newPoint(){
        NewPointData npd = mUserMessageLayout.getNewPointData();

        Myserver.getApi().newPoint(npd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<NewPointResult>() {
                    @Override
                    public void call() {
                        Log.d(TAG, "newPoint result: " + mVar.statue + " " + mVar.pointData.pointID);
                        selectArea();
                        switchShowMode_force(MODE_MAP,300);
                    }
                });
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
        if (lmode==MODE_MAP)
            super.onBackPressed();
        else
            switchShowMode(MODE_MAP,300);
    }

    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        switchShowMode(MODE_EDIT,300);
        PointData pd = MyModelFactory.getEditDeafultPointData();
        MyLatlng l = getCurLatlng();
        pd.latitude = l.latitude;
        pd.longitude = l.longitude;
        mUserMessageLayout.initshow(MODE_EDIT,pd);

        if (GlobalVar.mUserinfo!=null)
            mUserMessageLayout.initshow2(GlobalVar.mUserinfo);
    }
}
