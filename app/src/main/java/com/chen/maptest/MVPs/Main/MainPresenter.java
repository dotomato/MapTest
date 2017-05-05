package com.chen.maptest.MVPs.Main;

import android.graphics.PointF;
import android.util.Log;

import com.chen.maptest.DateType.PointData;
import com.chen.maptest.DateType.PointDataResult;
import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.DateType.SelectAreaData;
import com.chen.maptest.DateType.SelectAreaResult;
import com.chen.maptest.DateType.Userinfo;
import com.chen.maptest.DateType.UserinfoResult;
import com.chen.maptest.GlobalVar;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.Utils.OnceRunner;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mMainView;


    private boolean shouldInitonResume;
    private OnceRunner mSelectHelper;
    private MyLatlng lt;
    private MyLatlng rb;


    public MainPresenter(MainContract.View mainView) {
        mMainView = mainView;
        mMainView.setPresenter(this);

        shouldInitonResume = false;

        initSelectHelper();
    }

    @Override
    public void start() {
    }

    @Override
    public void destroy() {
        mSelectHelper.stop();
    }

    @Override
    public void mapMove(MyLatlng lefttop, MyLatlng rightbottom, MyLatlng center) {
        lt = lefttop;
        rb = rightbottom;
        GlobalVar.viewLatlng = center;
        mSelectHelper.start();
    }

    @Override
    public void clickPoint(PointSimpleData psd) {
        MyLatlng l =new MyLatlng(psd.latitude,psd.longitude);
        mMainView.moveMap(l);

        PointData gpd = new PointData();
        gpd.pointID=psd.pointID;
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        mMainView.upPointShower();
                        mMainView.showPoint(mVar.pointData);
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
                        mMainView.showPointUser(mVar.userinfo);
                    }
                });
    }

    @Override
    public void newPoint(MyLatlng l) {
        mMainView.upPointShower();
    }

    @Override
    public void retLocation() {
        if (GlobalVar.gpsLatlng != null){
            mMainView.moveMap(GlobalVar.gpsLatlng);
        }
    }

    @Override
    public void reciveLocation(MyLatlng l) {
        GlobalVar.gpsLatlng = l;
    }

    @Override
    public void pointLike(String pointID, boolean isLike) {

    }

    @Override
    public void commentList(String commentID, boolean isLike) {

    }

    @Override
    public void pointComment(String pointID, String content) {

    }

    @Override
    public void onBackPressed() {
        if (mMainView.isUped()) {
            mMainView.downPointShower();
        } else {
            mMainView.finish();
        }
    }


    private void initSelectHelper(){
        mSelectHelper = new OnceRunner() {
            @Override
            protected void call() {
                selectArea();
            }
        };
        mSelectHelper.setInternal(400);
        new Thread(mSelectHelper).start();
    }

    private void selectArea(){
        SelectAreaData sad = new SelectAreaData();
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
                        for (PointSimpleData psd:mVar.points) {

                        }
                    }
                });
    }
}
