package com.chen.maptest.MVPs.Main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.chen.maptest.GlobalConst;
import com.chen.maptest.GlobalVar;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.MapAdapter.MapAdapterLayout;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.MVPs.Main.Views.MyMapIcon;
import com.chen.maptest.DateType.PointData;
import com.chen.maptest.DateType.PointDataResult;
import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.DateType.SelectAreaData;
import com.chen.maptest.DateType.SelectAreaResult;
import com.chen.maptest.DateType.Userinfo;
import com.chen.maptest.DateType.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MVPs.Main.Views.MapUI;
import com.chen.maptest.ComViews.OutlineProvider;
import com.chen.maptest.ComViews.QuickPageAdapter;
import com.chen.maptest.ComViews.ScrollableViewPager;
import com.chen.maptest.R;
import com.chen.maptest.Utils.OnceRunner;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainFragment extends Fragment implements
        MapAdaterCallback, ScanMessageRv.OnRecyclerViewItemClickListener {


    public final static int MODE_SCAN = 0;
    public final static int MODE_MESSAGE = 1;
    public final static int MODE_EDIT = 2;

    private final static boolean SHOULD_CUR = false;
    private static final String TAG = "MainFragment";

    private OnceRunner mSelectHelper;

    public ViewGroup mUpView;
    public MyMapIcon mMyMapIcon;
    public FloatingActionButton mFloatingActionButton;
    public ViewGroup mBottomViewGroup;
    public ScrollableViewPager mViewpager;
    public VerticalSeekBar mZoombar;
    public ViewGroup mZoomView;
    public ScanView mScanViewGroup;
    public MapUI mMapui;
    public MapAdapterLayout mMapAdapter;

    private View mapView;

    //    private ActionBarDrawerToggle mDrawerToggle;
    public UserMessageLayout mUserMessageLayout;

    private boolean shouldInitonResume;
    private View v1;
    private View v2;
    private CommentLayout mCommentLayout;
    private MyUM mUserManager;
    private Unbinder unbinder;

    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mView = inflater.inflate(R.layout.main_frag, container, false);
        unbinder = ButterKnife.bind(this, mView);

        mUpView = (ViewGroup)mView.findViewById(R.id.upview);
        mMyMapIcon = (MyMapIcon)mView.findViewById(R.id.mymapicon);
        mFloatingActionButton = (FloatingActionButton)mView.findViewById(R.id.floatingActionButton);
        mBottomViewGroup = (ViewGroup)mView.findViewById(R.id.messageviewgroup);
        mViewpager = (ScrollableViewPager)mView.findViewById(R.id.viewpager);
        mZoombar = (VerticalSeekBar)mView.findViewById(R.id.zoombar);
        mZoomView = (ViewGroup)mView.findViewById(R.id.zoomview);
        mScanViewGroup = (ScanView)mView.findViewById(R.id.scanviewgroup);
        mMapui = (MapUI)mView.findViewById(R.id.mapui);
        mMapAdapter = (MapAdapterLayout)mView.findViewById(R.id.mapAdapter);

        shouldInitonResume = false;

        mMapAdapter.onCreate(savedInstanceState);
        mapView = mMapAdapter.getMapView();
        mMapAdapter.setMapAdaterCallback(this);

        initLayout();

        Myserver.apiTest();

        MyUM.inituserinfo(getActivity(),new MyUM.UserInitFinish() {
            @Override
            public void OnUserInitFinish() {
                initUserView();
            }
        });

        initSelectHelper();

        setBoardcastReceiver();

        switchShowMode(MODE_SCAN,300);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapAdapter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapAdapter.onResume();
        if (shouldInitonResume) {
            initUserView();
            shouldInitonResume = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapAdapter.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapAdapter.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapAdapter.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSelectHelper.stop();
        mMapAdapter.onDestroy();
    }


    private void initLayout(){

        mScanViewGroup.initview(this);


        v1 = LayoutInflater.from(getActivity()).inflate(R.layout.main_frag_usermessage,null,false);
        v2 = LayoutInflater.from(getActivity()).inflate(R.layout.main_frag_comment,null,false);
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
                mMapAdapter.onZoomCtrl(i*1.0/100);
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
        ((MainActivity)getActivity()).mLeftDrawerLayout.initUserView();
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

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new BroadcastReceiver() {
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
        MyLatlng l1 = mMapAdapter.pointToMyLatlng(getCenterpUper());
        MyLatlng l2 = mMapAdapter.pointToMyLatlng(getCenterp());
        return new MyLatlng(l.latitude+l2.latitude-l1.latitude,l.longitude+l2.longitude-l1.longitude);
    }


    public void MyCameraChangeStart() {
    }

    public void MyCameraChangeFinish() {
        GlobalVar.viewLatlng = mMapAdapter.pointToMyLatlng(getCenterpUper());
        mSelectHelper.start();
        mZoombar.setProgress((int) (mMapAdapter.getZoom()*100));
        Log.d(TAG,"zoom"+mMapAdapter.getZoom()+" LatLng"+GlobalVar.viewLatlng.toLatlng());
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        GlobalVar.gpsLatlng = latlng;
    }

    @Override
    public void firstLocation(final MyLatlng latlng) {
        mMapAdapter.gotoLocation(latlng,15);
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
                        mMapAdapter.gotoLocationSmooth(calUperLatlng(
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
                mMapAdapter.gotoLocationSmooth(mMapAdapter.pointToMyLatlng(getCenterpUper()));
                mMapui.setCenter(getCenterp());

                mMyMapIcon.switchIcon(MyMapIcon.ICON_HIDE);
                mMapAdapter.removeReadMarker();

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
                mMapAdapter.gotoLocationSmooth(calUperLatlng(mMapAdapter.pointToMyLatlng(getCenterp())));
                mMapui.setCenter(getCenterpUper());

                mMyMapIcon.switchIcon(MyMapIcon.ICON_FLAG);
                mMyMapIcon.gotoLalng(getCenterpUper());

                mMapAdapter.removeReadMarker();

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
        MyLatlng lt = mMapAdapter.pointToMyLatlng(new PointF(0,0));
        MyLatlng rb = mMapAdapter.pointToMyLatlng(new PointF(mapView.getWidth(),mScanViewGroup.getTop()));
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
                        mMapAdapter.rmAllMarker();
                        Log.d(TAG, "selectArea result: " + mVar.statue + " " + mVar.pointsCount);
                        for (PointSimpleData psd:mVar.points) {
                            mMapAdapter.addMarker(psd);
                        }
                        mScanViewGroup.setScanData(mVar.points);
                    }
                });
    }

    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        if (GlobalVar.mUserd ==null
                || (!SHOULD_CUR && GlobalVar.viewLatlng==null)
                || (SHOULD_CUR && GlobalVar.gpsLatlng==null)){
            Toast.makeText(getActivity(),"还没有连上网络",Toast.LENGTH_SHORT).show();
            return;
        }
        switchShowMode(MODE_EDIT,300);
        mUserMessageLayout.initShow(MODE_EDIT,null);
        mUserMessageLayout.initShow2(MyUM.getui());
    }

    public void onBackPressed(){
        if (lmode== MODE_SCAN)
            getActivity().finish();
        else
            switchShowMode(MODE_SCAN,300);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    public void onItemClickListener(View View, PointSimpleData psd) {
        MyMarkerClick(psd);
    }

    @OnClick(R.id.zoominbutton)
    public void zoomin(){
        mMapAdapter.onZoomCtrl(mZoombar.getProgress()*1.0/100-0.1);
    }


    @OnClick(R.id.zoomoutbutton)
    public void zoomout(){
        mMapAdapter.onZoomCtrl(mZoombar.getProgress()*1.0/100+0.1);
    }

    @OnClick(R.id.retlocalbutton)
    public void retlocal(){
        if (GlobalVar.gpsLatlng!=null)
            mMapAdapter.gotoLocationSmooth(GlobalVar.gpsLatlng);
    }

}
