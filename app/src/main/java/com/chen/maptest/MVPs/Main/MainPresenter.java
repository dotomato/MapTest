package com.chen.maptest.MVPs.Main;

import android.content.Intent;
import android.net.Uri;

import com.chen.maptest.JsonDataType.Message;
import com.chen.maptest.MVPs.Editpoint.EditActivity;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.Manager.MyUpyunManager;
import com.chen.maptest.NetDataType.PointData;
import com.chen.maptest.NetDataType.PointData2;
import com.chen.maptest.NetDataType.PointDataResult;
import com.chen.maptest.NetDataType.PointSimpleData;
import com.chen.maptest.NetDataType.SelectAreaData;
import com.chen.maptest.NetDataType.SelectAreaResult;
import com.chen.maptest.NetDataType.Userinfo;
import com.chen.maptest.NetDataType.UserinfoResult;
import com.chen.maptest.GlobalVar;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.OnceRunner;
import com.google.gson.Gson;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

class MainPresenter implements MainContract.Presenter, MyUpyunManager.UploadProgress {

    private final MainContract.View mMainView;


    private OnceRunner mSelectHelper;
    private MyLatlng lt;
    private MyLatlng rb;
    private MyLatlng ml;
    private String mAlbumImageURL;
    private String _msgTitle;
    private String _msgText;
    private MyLatlng _l;


    MainPresenter(MainContract.View mainView) {
        mMainView = mainView;
        mMainView.setPresenter(this);
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
    public void clickPoint(String pointID, String userID) {
        if (mMainView.isEditing())
            return;

        PointData gpd = new PointData();
        gpd.pointID=pointID;
        Myserver.getApi().getPoint(gpd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        mMainView.upPointShower();

                        MyLatlng l = new MyLatlng(mVar.pointData.latitude, mVar.pointData.longitude);
                        mMainView.moveMap(l);

                        Gson gson = new Gson();
                        Message mj = gson.fromJson(mVar.pointData.userMessage,Message.class);
                        mMainView.showPoint(" ", mj.text,mj.albumURL,new Date(mVar.pointData.pointTime*1000));
                    }
                });

        Userinfo nuid = new Userinfo();
        nuid.userID=userID;
        Myserver.getApi().getuser(nuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserinfoResult>() {
                    @Override
                    public void call() {
                        mMainView.showPointUser(mVar.userinfo.userName, mVar.userinfo.userIcon);
                    }
                });
    }

    @Override
    public void newPointButton(MyLatlng l) {
        if (mMainView.isEditing() || mMainView.isUped())
            return;
        mMainView.upPointEditer();
        mMainView.showPointUser(MyUM.getui().userName,MyUM.getui().userIcon);
        mMainView.showPoint("","","", Calendar.getInstance().getTime());
    }

    @Override
    public void onResult(int requestCode, Intent data) {
        switch (requestCode)
        {
            case MainFragment.ALBUM_REQUESR_CODE:
                Uri uri = data.getData();
                mMainView.replaceMsgAlbum(MyUtils.UritoFullName(mMainView.getActivity(),uri));
            default:
                break;
        }
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
        } else if (mMainView.isEditing()) {
            mMainView.downPointEditer();
        } else {
            mMainView.finish();
        }
    }

    @Override
    public void sendNewpoint(String msgTitle, String msgText, String msgAlbum,MyLatlng l, boolean hasAlbum) {
        _msgTitle = msgTitle;
        _msgText = msgText;
        _l = l;
        if (hasAlbum){
            MyUpyunManager.getIns().upload_image("MessageAlbum",Uri.fromFile(new File(msgAlbum)),this);
        } else {
            uploadnoewpoint(false);
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
                        for (PointSimpleData psd :mVar.points)
                            mMainView.addMarker(new MyLatlng(psd.latitude,psd.longitude),
                                    psd.pointID, psd.userIcon, psd.smallMsg, psd.userID);
                    }
                });
    }

    @Override
    public void onProgress(float progress) {
        mMainView.setUploadProgress((int) (progress*0.9));
    }

    @Override
    public void onComplete(boolean isSuccess, String url) {
        mAlbumImageURL = url;
        uploadnoewpoint(true);
    }

    private void uploadnoewpoint(boolean hasAlbum){
        MyLatlng l = GlobalVar.viewLatlng;

        PointData2 pd2 = new PointData2();
        PointData pd = new PointData();

        pd.latitude = _l.latitude;
        pd.longitude = _l.longitude;
        pd.userID = GlobalVar.mUserd.ui2.userinfo.userID;

        Message mj= new Message();
        mj.ver=100;
        mj.text = _msgText;
        mj.title = _msgTitle;
        if (hasAlbum){
            mj.albumURL=mAlbumImageURL;
        } else {
            mj.albumURL="no_img";
        }

        Gson gson = new Gson();
        pd.userMessage = gson.toJson(mj);

        pd2.pointData = pd;
        pd2.userID2 = GlobalVar.mUserd.ui2.userID2;


        mMainView.setUploadProgress(0);

        Myserver.getApi().newPoint(pd2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        mMainView.setUploadProgress(100);
                        mMainView.downPointEditer();
                        mMainView.showNewpointShine(_l,500);
                        selectArea();
                    }

                    public void error(int statue, String errorMessage){
                        mMainView.setUploadProgress(-1);
                    }
                });
    }
}
