package com.chen.maptest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.chen.maptest.MyView.MyMapIcon;
import com.chen.maptest.Utils.OnceRunner;
import com.yalantis.ucrop.UCrop;

public class MainActivity extends BmapAdapterActivity implements
        MapAdaterCallback {


    private final static String TAG = "MainActivity";

    public final static int MODE_MAP = 0;
    public final static int MODE_MESSAGE = 1;
    public final static int MODE_EDIT = 2;

    private final boolean SHOULD_CUR = false;

    private OnceRunner mSelectHelper;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    @BindView(R.id.upview)
    public ViewGroup mUpView;

    @BindView(R.id.mymapicon)
    public MyMapIcon mMyMapIcon;

    private View mapView;

    private ActionBarDrawerToggle mDrawerToggle;

    private boolean shouldInitonResume = false;
    private int WindowHeight;
    private int spaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mapView = getMapView();

        initLayout();

        Myserver.apiTest();

        initUserinfo();

        initSelectHelper();

        setMapAdaterCallback(this);

        setBoardcastReceiver();

        switchShowMode(MODE_MAP,300);
    }

    private void initLayout(){
        mUserMessageLayout.setExitCallback(new UserMessageLayout.ExitCallback() {
            @Override
            public void exitCallback() {
                switchShowMode_force(MODE_MAP,300);
            }
        });


        mUserMessageLayout.setSpaceTouchCallback(new UserMessageLayout.SpaceTouchCallback() {
            @Override
            public void spaceTouchcallback(MotionEvent ev) {
                ev.offsetLocation(0,-mUpView.getY());
                mapView.dispatchTouchEvent(ev);
            }
        });

        mUserMessageLayout.callback(new UserMessageLayout.NewPointFinishCallback() {
            @Override
            public void newPointFinishCallback() {
                selectArea();
                switchShowMode_force(MODE_MAP,300);
            }
        });

        mUserMessageLayout.setScrollCallback(new UserMessageLayout.ScrollCallback() {
            @Override
            public void scrollCallback(int t) {
                mUpView.setTranslationY(-t/2-(WindowHeight - spaceHeight)/2);
            }
        });
    }

    private void initUserinfo(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String userID = pref.getString("userID",null);
        GlobalVar.mUserinfo2 = new Userinfo2();
        if (userID==null) {
            Userinfo ui = new Userinfo();
            ui.userDes="please give me a new ID!";
            Myserver.getApi().newuser(ui)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<Userinfo2Result>() {
                        @Override
                        public void call() {
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userID", mVar.userinfo.userID);
                            editor.putString("userID2", mVar.userID2);
                            editor.apply();

                            GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                            GlobalVar.mUserinfo2.userID2 = mVar.userID2;
                            initUserView();
                        }
                    });
        } else {
            Userinfo ui = new Userinfo();
            ui.userID=userID;
            Myserver.getApi().getuser(ui)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<UserinfoResult>() {
                        @Override
                        public void call() {
                            SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                            GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                            GlobalVar.mUserinfo2.userID2 = pref.getString("userID2",null);
                            initUserView();
                        }
                    });
        }
    }

    private void initUserView(){
        mLeftDrawerLayout.initUserView();
    }


    private void initSelectHelper(){
        mSelectHelper = new OnceRunner() {
            @Override
            protected void call() {
                selectArea();
            }
        };
        mSelectHelper.setInternal(1000);
        new Thread(mSelectHelper).start();
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

    @Override
    protected void onResume(){
        super.onResume();
        if (shouldInitonResume) {
            initUserView();
            shouldInitonResume = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSelectHelper.stop();
    }

    private boolean mCameraMovefinish = true;
    public void MyTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mMyMapIcon.switchAni(MyMapIcon.ANI_UP);
                break;
            case MotionEvent.ACTION_UP:
                if (mCameraMovefinish)
                    mMyMapIcon.switchAni(MyMapIcon.ANI_DOWN);
                break;
        }
    }

    public void MyMarkerClick(PointSimpleData psd) {
        gotoLocation2(new MyLatlng(psd.latitude,psd.longitude));
        PointData gpd = new PointData();
        gpd.pointID=psd.pointID;
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        switchShowMode(MODE_MESSAGE,300);
                        mUserMessageLayout.initShow(MODE_MESSAGE,mVar.pointData);
                    }
                });

        Userinfo nuid = new Userinfo();
        nuid.userID=psd.userID;
        Myserver.getApi().getuser(nuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserinfoResult>() {
                    @Override
                    public void call() {
                        mUserMessageLayout.initShow2(mVar.userinfo);
                    }
                });
    }

    public void MyCameraChangeStart() {
        mCameraMovefinish = false;
        mMyMapIcon.switchAni(MyMapIcon.ANI_UP);
    }


    public void MyCameraChangeFinish() {
        mCameraMovefinish = true;
        GlobalVar.viewLatlng = getViewLatlng();
        mMyMapIcon.switchAni(MyMapIcon.ANI_DOWN);
        mSelectHelper.start();
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        GlobalVar.gpsLatlng = latlng;
    }



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
        WindowHeight = frame.height();

        spaceHeight = mUserMessageLayout.getSpaceHeight();

        mUpView.clearAnimation();
        mUserMessageLayout.clearAnimation();

        switch (mMode){
            case MODE_MAP:
                mMyMapIcon.switchIcon(MyMapIcon.ICON_ARROW);
                mUpView.animate().y(0).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(WindowHeight).setDuration(mDuration).start();
                mFloatingActionButton.show();
                break;
            case MODE_MESSAGE:
                mMyMapIcon.switchIcon(MyMapIcon.ICON_ARROW);
                mUpView.animate().y(-(WindowHeight-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
                break;
            case MODE_EDIT:
                mMyMapIcon.switchIcon(MyMapIcon.ICON_FLAG);
                mUpView.animate().y(-(WindowHeight-spaceHeight)/2).setDuration(mDuration).start();
                mUserMessageLayout.animate().y(0).setDuration(mDuration).start();
                mFloatingActionButton.hide();
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
        if (GlobalVar.mUserinfo2==null
                || (SHOULD_CUR && GlobalVar.viewLatlng==null)
                || (!SHOULD_CUR && GlobalVar.gpsLatlng==null)){
            Toast.makeText(this,"还没有连上网络",Toast.LENGTH_SHORT).show();
            return;
        }

        switchShowMode(MODE_EDIT,300);
        mUserMessageLayout.initShow(MODE_EDIT,null);
        mUserMessageLayout.initShow2(GlobalVar.mUserinfo2.userinfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode){
            case UserMessageLayout.SELECT_ALBUM_IMG:
            case UCrop.REQUEST_CROP:
                mUserMessageLayout.ResultCallback(requestCode,resultCode,data);
                break;
        }
    }
}
