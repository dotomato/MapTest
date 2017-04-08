package com.chen.maptest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MapAdapter.MmapAdapterActivity;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.MyView.MapUI;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.MyView.QuickPageAdapter;
import com.chen.maptest.MyView.ScrollableViewPager;
import com.chen.maptest.Utils.OnceRunner;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends MmapAdapterActivity implements
        MapAdaterCallback, DrawerLayout.DrawerListener, ScanMessageRv.OnRecyclerViewItemClickListener {


    private final static String TAG = "MainActivity";

    public final static int MODE_SCAN = 0;
    public final static int MODE_MESSAGE = 1;
    public final static int MODE_EDIT = 2;

    private final static boolean SHOULD_CUR = false;

    private OnceRunner mSelectHelper;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    @BindView(R.id.upview)
    public ViewGroup mUpView;

    @BindView(R.id.mymapicon)
    public MyMapIcon mMyMapIcon;

    @BindView(R.id.activity_main)
    public DrawerLayout mRootView;

    @BindView(R.id.messageviewgroup)
    public ViewGroup mBottomViewGroup;

    @BindView(R.id.viewpager)
    public ScrollableViewPager mViewpager;

    @BindView(R.id.zoombar)
    public VerticalSeekBar mZoombar;

    @BindView(R.id.zoomview)
    public ViewGroup mZoomView;

    @BindView(R.id.scanviewgroup)
    public ScanView mScanViewGroup;

    @BindView(R.id.mapui)
    public MapUI mMapui;

    private View mapView;

//    private ActionBarDrawerToggle mDrawerToggle;
    public UserMessageLayout mUserMessageLayout;

    private boolean shouldInitonResume;
    private View v1;
    private View v2;
    private CommentLayout mCommentLayout;
    private MyUM mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shouldInitonResume = false;

        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mapView = getMapView();
        setMapAdaterCallback(this);

        initLayout();

        Myserver.apiTest();

        MyUM.inituserinfo(this,new MyUM.UserInitFinish() {
            @Override
            public void OnUserInitFinish() {
                initUserView();
            }
        });

        initSelectHelper();

        setBoardcastReceiver();

        switchShowMode(MODE_SCAN,300);

    }

    private void initLayout(){
        mRootView.addDrawerListener(this);

        mScanViewGroup.initview(this);


        v1 = LayoutInflater.from(this).inflate(R.layout.layout_user_message,null,false);
        v2 = LayoutInflater.from(this).inflate(R.layout.layout_comment,null,false);
        List<View> viewlist = new ArrayList<>();
        viewlist.add(v1);
        viewlist.add(v2);
        mViewpager.setAdapter(new QuickPageAdapter<>(viewlist));
        mViewpager.setPageTransformer(true, new FlipHorizontalTransformer());
        v1.setCameraDistance(1e5f);
        v2.setCameraDistance(1e5f);

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        v1.setVisibility(View.VISIBLE);
                        v2.setVisibility(View.GONE);
                        break;
                    case 1:
                        v1.setVisibility(View.GONE);
                        v2.setVisibility(View.VISIBLE);
                        mCommentLayout.initShowStub();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mUserMessageLayout = (UserMessageLayout) v1.findViewById(R.id.user_message_layout);
        mUserMessageLayout.setExitCallback(new UserMessageLayout.ExitCallback() {
            @Override
            public void exitCallback() {
                switchShowMode_force(MODE_SCAN,300);
            }
        });
        mUserMessageLayout.callback(new UserMessageLayout.NewPointFinishCallback() {
            @Override
            public void newPointFinishCallback() {
                selectArea();
                switchShowMode_force(MODE_SCAN,300);
                mMyMapIcon.shine_button(getCenterpUper());
            }
        });
        mUserMessageLayout.setViewPager(mViewpager);
        OutlineProvider.setOutline(mUserMessageLayout,OutlineProvider.SHAPE_RECT);

        mCommentLayout = (CommentLayout)v2.findViewById(R.id.comment_layout);

        mZoombar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b)
                    return;
                onZoomCtrl(i*1.0/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initUserView(){
        mLeftDrawerLayout.initUserView();
    }


    private void initSelectHelper(){
        mSelectHelper = new OnceRunner() {
            @Override
            protected void call() {
                if (lmode == MODE_SCAN)
                    selectArea();
            }
        };
        mSelectHelper.setInternal(400);
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

    public void MyTouch(MotionEvent motionEvent) {
    }

    public PointF getCenterp(){
        return new PointF(mapView.getWidth()/2,mapView.getHeight()/2);
    }

    public PointF getCenterpUper(){
        return new PointF(mapView.getWidth()/2,(mapView.getTop()+mBottomViewGroup.getTop())/2);
    }

    public PointF getCenterpLower(){
        return new PointF(mapView.getWidth()/2,(mapView.getTop()+mScanViewGroup.getTop())/2);
    }

    public MyLatlng calUperLatlng(MyLatlng l){
        MyLatlng l1 = pointToMyLatlng(getCenterpUper());
        MyLatlng l2 = pointToMyLatlng(getCenterp());
        return new MyLatlng(l.latitude+l2.latitude-l1.latitude,l.longitude+l2.longitude-l1.longitude);
    }


    public void MyCameraChangeStart() {
    }

    public void MyCameraChangeFinish() {
        GlobalVar.viewLatlng = pointToMyLatlng(getCenterpUper());
        mSelectHelper.start();
        mZoombar.setProgress((int) (getZoom()*100));
        Log.d(TAG,"zoom"+getZoom()+" LatLng"+GlobalVar.viewLatlng.toLatlng());
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        GlobalVar.gpsLatlng = latlng;
    }

    @Override
    public void firstLocation(final MyLatlng latlng) {
        gotoLocation(latlng,15);
//        mapView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                gotoLocationSmooth(calUperLatlng(latlng));
//            }
//        },100);
    }


    public void MyMarkerClick(PointSimpleData psd) {
        MyLatlng l =new MyLatlng(psd.latitude,psd.longitude);

//        removeReadMarker();
//        addReadMarker(l);

        PointData gpd = new PointData();
        gpd.pointID=psd.pointID;
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        gotoLocationSmooth(calUperLatlng(
                                new MyLatlng(mVar.pointData.latitude,mVar.pointData.longitude)));
                        switchShowMode(MODE_MESSAGE,300);
                        mUserMessageLayout.initShow(MODE_MESSAGE,mVar.pointData);
                        mCommentLayout.initShow(0,mVar.pointData);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void _switchShowMode(){
        lmode=mMode;

        switch (mMode){
            case MODE_SCAN:
                gotoLocationSmooth(pointToMyLatlng(getCenterpUper()));
                mMapui.setCenter(getCenterp());

                mMyMapIcon.switchIcon(MyMapIcon.ICON_HIDE);
                removeReadMarker();

                mBottomViewGroup.animate().alpha(0).setDuration(mDuration).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mBottomViewGroup.setVisibility(View.GONE);
                    }
                }).start();

                mZoomView.setVisibility(View.VISIBLE);
                mZoomView.animate().alpha(1).setDuration(mDuration).start();
                mScanViewGroup.setVisibility(View.VISIBLE);
                mScanViewGroup.animate().alpha(1).setDuration(mDuration).start();

                mFloatingActionButton.show();

                break;
            case MODE_EDIT:
                gotoLocationSmooth(calUperLatlng(pointToMyLatlng(getCenterp())));
                mMapui.setCenter(getCenterpUper());

                mMyMapIcon.switchIcon(MyMapIcon.ICON_FLAG);
                mMyMapIcon.gotoLalng(getCenterpUper());

                removeReadMarker();

                mBottomViewGroup.setVisibility(View.VISIBLE);
                mBottomViewGroup.animate().alpha(1).setDuration(mDuration).start();
                mViewpager.setCurrentItem(0,false);
                mViewpager.setScrollAble(false);

                mZoomView.animate().alpha(0).setDuration(mDuration).start();
                mScanViewGroup.animate().alpha(0).setDuration(mDuration).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mScanViewGroup.setVisibility(View.GONE);
                        mZoomView.setVisibility(View.GONE);
                    }
                }).start();

                mFloatingActionButton.hide();
                break;
            case MODE_MESSAGE:
                mMapui.setCenter(getCenterpUper());
                mMyMapIcon.switchIcon(MyMapIcon.ICON_HIDE);

                mBottomViewGroup.setVisibility(View.VISIBLE);
                mBottomViewGroup.animate().alpha(1).setDuration(mDuration).start();
                mViewpager.setCurrentItem(0,false);
                mViewpager.setScrollAble(true);

                mZoomView.animate().alpha(0).setDuration(mDuration).start();
                mScanViewGroup.animate().alpha(0).setDuration(mDuration).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mScanViewGroup.setVisibility(View.GONE);
                        mZoomView.setVisibility(View.GONE);
                    }
                }).start();
                mFloatingActionButton.hide();
                break;
        }
    }

    private void selectArea(){
        SelectAreaData sad = new SelectAreaData();
        MyLatlng lt = pointToMyLatlng(new PointF(0,0));
        MyLatlng rb = pointToMyLatlng(new PointF(mapView.getWidth(),mScanViewGroup.getTop()));
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
                        mScanViewGroup.setScanData(mVar.points);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.activity_main);
        if(dl.isDrawerOpen(mLeftDrawerLayout))
            dl.closeDrawer(mLeftDrawerLayout);
        else if (lmode== MODE_SCAN) {
//            this.finish();
//            getApplication().onTerminate();
            super.onBackPressed();
        }
        else
            switchShowMode(MODE_SCAN,300);
    }

    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        if (GlobalVar.mUserd ==null
                || (!SHOULD_CUR && GlobalVar.viewLatlng==null)
                || (SHOULD_CUR && GlobalVar.gpsLatlng==null)){
            Toast.makeText(this,"还没有连上网络",Toast.LENGTH_SHORT).show();
            return;
        }
        switchShowMode(MODE_EDIT,300);
        mUserMessageLayout.initShow(MODE_EDIT,null);
        mUserMessageLayout.initShow2(MyUM.getui());
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

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
//        if (mMyMapIcon!=null)
//            mMyMapIcon.switchAni(MyMapIcon.ANI_DOWN);
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onItemClickListener(View View, PointSimpleData psd) {
        MyMarkerClick(psd);
    }

    @OnClick(R.id.zoominbutton)
    public void zoomin(){
        super.onZoomCtrl(mZoombar.getProgress()*1.0/100-0.1);
    }


    @OnClick(R.id.zoomoutbutton)
    public void zoomout(){
        super.onZoomCtrl(mZoombar.getProgress()*1.0/100+0.1);
    }

    @OnClick(R.id.retlocalbutton)
    public void retlocal(){
        if (GlobalVar.gpsLatlng!=null)
            gotoLocationSmooth(GlobalVar.gpsLatlng);
    }


}
