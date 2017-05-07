package com.chen.maptest.MVPs.Main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.chen.maptest.GlobalConst;
import com.chen.maptest.JsonDataType.Message;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.Manager.MyUpyunManager;
import com.chen.maptest.NetDataType.PointComment;
import com.chen.maptest.NetDataType.PointData;
import com.chen.maptest.NetDataType.PointData2;
import com.chen.maptest.NetDataType.PointDataResult;
import com.chen.maptest.NetDataType.PointSimpleData;
import com.chen.maptest.NetDataType.SelectAreaData;
import com.chen.maptest.NetDataType.SelectAreaResult;
import com.chen.maptest.NetDataType.UserLikeComment;
import com.chen.maptest.NetDataType.UserLikeCommentResult;
import com.chen.maptest.NetDataType.UserLikePoint;
import com.chen.maptest.NetDataType.UserLikePointResult;
import com.chen.maptest.NetDataType.UserNewComment;
import com.chen.maptest.NetDataType.UserNewCommentResult;
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

import id.zelory.compressor.Compressor;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    private String mAlbumImageURL;
    private String _msgTitle;
    private String _msgText;
    private MyLatlng _l;
    private PointData mPointData;



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

        mMainView.setUploadProgress(0, View.INVISIBLE);
        mMainView.showCommentEdit(true);
        mMainView.showPointLiker(true);

        mPointData = new PointData();
        mPointData.pointID=pointID;
        Myserver.getApi().getPoint(mPointData)
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
                        mMainView.showPoint(" ", mj.text,mj.albumURL,new Date(mVar.pointData.pointTime*1000),
                                mVar.pointData.pointLikeNum, MyUM.islikepoint(mVar.pointData.pointID));
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

        Myserver.getApi().getpointcomment(mPointData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointComment>() {
                    @Override
                    public void call() {
                        mMainView.showComment(mVar.userCommentList, mVar.userCommentCount);
                        mMainView.showCommentEmpty(mVar.userCommentCount == 0);
                    }
                });
    }

    @Override
    public void newPointButton(MyLatlng l) {
        if (mMainView.isEditing() || mMainView.isUped())
            return;
        mMainView.upPointEditer();
        mMainView.setUploadProgress(0, View.INVISIBLE);
        mMainView.showPointUser(MyUM.getui().userName,MyUM.getui().userIcon);
        mMainView.showPoint("","","", Calendar.getInstance().getTime(),0,false);
        mMainView.showCommentEmpty(false);
        mMainView.showCommentEdit(false);
        mMainView.showPointLiker(false);
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
    public void pointLike(boolean isLike) {
        UserLikePoint ulp = new UserLikePoint();
        ulp.pointID = mPointData.pointID;
        ulp.isLike = isLike;
        ulp.userID = GlobalVar.mUserd.ui2.userinfo.userID;
        ulp.userID2 = GlobalVar.mUserd.ui2.userID2;
        Myserver.getApi().userlikepoint(ulp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserLikePointResult>() {
                    @Override
                    public void call() {
                        mMainView.updatePoint(mVar.pointLikeNum,mVar.isLike);
                        MyUM.likepoint(mPointData.pointID, mVar.isLike);
                    }
                });
    }

    @Override
    public void commentLike(String commentID, boolean isLike) {
        UserLikeComment ulc = new UserLikeComment();
        ulc.commentID = commentID;
        ulc.isLike = isLike;
        ulc.userID = MyUM.getuid();
        ulc.userID2 = MyUM.getuid2();
        Myserver.getApi().userlikecomment(ulc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserLikeCommentResult>() {
                    @Override
                    public void call() {
                        if (mVar.isLike != MyUM.islikecomment(mVar.commentID))
                            MyUM.likecomment(mVar.commentID,mVar.isLike);
                        mMainView.updateComment(mVar);
                    }
                });
    }

    @Override
    public void pointComment(String content) {
        UserNewComment unc = new UserNewComment();
        unc.pointID = mPointData.pointID;
        unc.userID = MyUM.getuid();
        unc.userID2 = MyUM.getuid2();
        unc.userComment = content;

        Myserver.getApi().newcomment(unc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserNewCommentResult>() {
                    @Override
                    public void call() {
//                        mUserComment.setText("");
                        Myserver.getApi().getpointcomment(mPointData)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new MyAction1<PointComment>() {
                                    @Override
                                    public void call() {
                                        mMainView.showComment(mVar.userCommentList, mVar.userCommentCount);
                                        mMainView.showCommentEmpty(mVar.userCommentCount == 0);
                                    }
                                });
                        mMainView.clearComment();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (mMainView.isUped()) {
            mMainView.downPointShower();
            mMainView.showCommentEdit(false);
        } else if (mMainView.isEditing()) {
            mMainView.downPointEditer();
            mMainView.showCommentEdit(false);
        } else {
            mMainView.finish();
        }
    }

    @Override
    public void sendNewpointButton(String msgTitle, String msgText, String msgAlbum, MyLatlng l, boolean hasAlbum) {
        _msgTitle = msgTitle;
        _msgText = msgText;
        _l = l;
        if (hasAlbum){
            mMainView.setUploadProgress(10, View.VISIBLE);
            new Compressor.Builder(mMainView.getActivity())
                    .setMaxWidth(1080)
                    .setMaxHeight(1920)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath())
                    .build()
                    .compressToFileAsObservable(new File(msgAlbum))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<File>() {
                        @Override
                        public void call(File file) {
                            mMainView.setUploadProgress(20, View.VISIBLE);
                            MyUpyunManager.getIns().upload_image("MessageAlbum",Uri.fromFile(file),MainPresenter.this);
                        }
                    });
        } else {
            mMainView.setUploadProgress(10, View.VISIBLE);
            sendNewPoint(false);
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
        mMainView.setUploadProgress((int) (progress*70)+20, View.VISIBLE);
    }

    @Override
    public void onComplete(boolean isSuccess, String url) {
        mAlbumImageURL = url;
        sendNewPoint(true);
    }

    private void sendNewPoint(boolean hasAlbum){
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
            mj.albumURL= GlobalConst.NO_ALBUM;
        }

        Gson gson = new Gson();
        pd.userMessage = gson.toJson(mj);

        pd2.pointData = pd;
        pd2.userID2 = GlobalVar.mUserd.ui2.userID2;



        Myserver.getApi().newPoint(pd2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
                        mMainView.setUploadProgress(100, View.VISIBLE);
                        mMainView.downPointEditer();
                        mMainView.showNewpointShine(_l,500);
                        selectArea();
                    }

                    public void error(int statue, String errorMessage){
                        mMainView.setUploadProgress(0, View.INVISIBLE);
                    }
                });
    }
}
