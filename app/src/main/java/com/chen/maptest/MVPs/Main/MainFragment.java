package com.chen.maptest.MVPs.Main;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chen.maptest.DateType.Userinfo;
import com.chen.maptest.MapAdapter.MapAdapterLayout;
import com.chen.maptest.MapAdapter.MapAdaterCallback;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.DateType.PointData;
import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainFragment extends Fragment implements MainContract.View, MapAdaterCallback {

    private static final String TAG = "MainFragment";

    final float h2Radio = 1/3.0f;

    @BindView(R.id.floatingActionButton)
    public FloatingActionButton mFloatingActionButton;

    @BindView(R.id.mapAdapter)
    public MapAdapterLayout mMapAdapter;

    @BindView(R.id.zoombar)
    public VerticalSeekBar mZoombar;

    @BindView(R.id.zoomCtrl)
    public ViewGroup mZoomCtrl;

    @BindView(R.id.msgScrollView)
    public ViewGroup mMsgScrollView;

    @BindView(R.id.msgContentLayout)
    public ViewGroup mMsgContentLayout;

    @BindView(R.id.msgText)
    public TextView mMsgText;

    private View mMapView;
    private Unbinder unbinder;
    private MainContract.Presenter mPresenter;

    private int self_w;
    private int self_h;
    private View mView;
    private int h2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.main_frag, container, false);
        unbinder = ButterKnife.bind(this, mView);

        initMap(savedInstanceState);

        initLayout();

        return mView;
    }

    private void initMap(Bundle savedInstanceState) {
        mMapAdapter.onCreate(savedInstanceState);
        mMapView = MapAdapterLayout.getMapView();
        mMapAdapter.setMapAdaterCallback(this);
    }

    private void initLayout(){
        mZoombar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b) return;
                mMapAdapter.onZoomCtrl(i*1.0/100);}
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mMsgScrollView.setVisibility(View.GONE);

        ViewTreeObserver vto = mView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                self_w = mView.getWidth();
                self_h = mView.getHeight();
                h2 = (int) (self_h*h2Radio);
                Log.i(TAG,""+self_w+" "+self_h);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onResume();
    }

    @Override
    public void onPause() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        if (MapAdapterLayout.getMapView()!=null)
            MapAdapterLayout.getMapView().onDestroy();
        mPresenter.destroy();
        super.onDestroy();
    }



    public void MyTouch(MotionEvent motionEvent) {
    }

    public void MyCameraChangeStart() {
    }

    public void MyCameraChangeFinish() {
        mZoombar.setProgress((int) (mMapAdapter.getZoom()*100));

        int w = mMapView.getWidth();
        int h = mMapView.getHeight();
        MyLatlng lefttop = mMapAdapter.pointToMyLatlng(new PointF(0,0));
        MyLatlng rightbottom = mMapAdapter.pointToMyLatlng(new PointF(w,h));
        MyLatlng center = mMapAdapter.pointToMyLatlng(new PointF(w/2,h/2));

        mPresenter.mapMove(lefttop, rightbottom, center);
    }

    @Override
    public void MyGPSRecive(MyLatlng latlng) {
        mPresenter.reciveLocation(latlng);
    }

    @Override
    public void firstLocation(final MyLatlng latlng) {
        mMapAdapter.gotoLocation(latlng,15);
    }


    public void MyMarkerClick(PointSimpleData psd) {
        mPresenter.clickPoint(psd);
    }


    @OnClick(R.id.floatingActionButton)
    public void floatingClick(){
        int w = mMapView.getWidth();
        int h = mMapView.getHeight();
        MyLatlng center = mMapAdapter.pointToMyLatlng(new PointF(w/2,h/2));
        mPresenter.newPoint(center);
    }

    public void onBackPressed(){
        mPresenter.onBackPressed();
    }

    @Override
    public void finish(){
        getActivity().finish();
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
        mPresenter.retLocation();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void moveMap(MyLatlng center) {
        mMapAdapter.gotoLocationSmooth(center);
    }

    @Override
    public void zoomMap(float zoom) {
        mMapAdapter.onZoomCtrl(zoom);
    }

    @Override
    public void showPoints(List<PointSimpleData> data) {

    }

    @Override
    public void showPoint(PointData pd) {

    }

    int dura = 300;
    boolean isUped = false;
    String testStr = "";
    @Override
    public void upPointShower() {
        if (isUped)
            return;

        int lh = View.MeasureSpec.makeMeasureSpec(self_h - h2, View.MeasureSpec.EXACTLY);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mMsgScrollView.getLayoutParams();
        lp.height = lh;
        mMsgScrollView.setLayoutParams(lp);

        isUped = true;
        testStr += this.getString(R.string.manytext);
        mMsgText.setText(testStr);
        mMsgScrollView.setVisibility(View.VISIBLE);
        mMapAdapter.animate().translationY(h2 /2 - self_h/2).setDuration(dura).start();
        mMsgScrollView.animate().translationY(-self_h+ h2).setDuration(dura).start();
        mMsgContentLayout.requestLayout();
    }

    @Override
    public void downPointShower() {
        if (!isUped)
            return;
        isUped = false;
        mMapAdapter.animate().translationY(0).setDuration(dura).start();
        mMsgScrollView.animate().translationY(0).setDuration(dura).start();
        MyUtils.setGoneAfterAnimate(mMsgScrollView,mMsgScrollView.animate());
    }

    @Override
    public boolean isUped() {
        return isUped;
    }

    @Override
    public void showPointUser(Userinfo ui) {
    }

}
