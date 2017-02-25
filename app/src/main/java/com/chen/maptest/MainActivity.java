package com.chen.maptest;

import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class MainActivity extends BmapAdapterActivity implements MapAdaterCallback {


    private final static String TAG = "MainActivity";

    private SelectHelper mSelectHelper;

    @BindView(R.id.user_message_layout)
    public UserMessageLayout mUserMessageLayout;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.leftDrawer)
    public ViewGroup mLeftDrawerLayout;

    private View mapView;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mapView = getMapView();

        initLayout();

        Myserver.apiTest();

        mSelectHelper = new SelectHelper();
        new Thread(mSelectHelper).start();

        setMapAdaterCallback(this);


        switchShowMode(MODE_MAP,300);
    }

    @Override
    protected void onStart(){
        super.onStart();
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
    }


    public void MyCameraChangeFinish() {
        mSelectHelper.call();
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
        MyLatlng l = getCurLatlng();

        NewPointData npd = new NewPointData();
        PointData pd = mUserMessageLayout.getPD();

        pd.latitude = l.latitude;
        pd.longitude = l.longitude;

        npd.pointData = pd;

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
    }
}
