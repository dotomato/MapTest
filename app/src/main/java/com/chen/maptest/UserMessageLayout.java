package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.InnerEdge;
import com.chen.maptest.MyView.MyBlurImageView;
import com.chen.maptest.MyView.MyTimeShow;
import com.chen.maptest.MyView.OutlineProvider;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.Utils.UserIconWarp;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import butterknife.ButterKnife;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chen.maptest.Utils.MyUtils.setEditTextEditable;

/**
 * Created by chen on 17-2-3.
 * Copyright *
 */

public class UserMessageLayout extends ConstraintLayout implements MyUpyunManager.UploadProgress {

    private final static String TAG = "UserMessageLayout";

    static public final int MODE2_TEXT = 0;
    static public final int MODE2_ALBUM = 1;

    static public final int MODE3_POS = 0;
    static public final int MODE3_NEG = 1;

    static public final int SELECT_ALBUM_IMG = 0;

    @BindView(R.id.usericon)
    public ImageView mUserIcon;

    @BindView(R.id.username)
    public TextView mUserName;

    @BindView(R.id.userdescript)
    public TextView mUserDescirpt;

    @BindView(R.id.blurimg)
    public MyBlurImageView mBlurImg;

    @BindView(R.id.noblurimg)
    public ImageView mNoBlurImg;

    @BindView(R.id.messagelayout)
    public ViewGroup mMessageLayout;

    @BindView(R.id.timeshow)
    public MyTimeShow mMyTimeShow;

    @BindView(R.id.inneredge)
    public InnerEdge mInnerEdge;

    @BindView(R.id.locationdes)
    public TextView mLocationDes;

    @BindView(R.id.msgedittext)
    public EditText mMsgEdittext;

    @BindView(R.id.msgscroll)
    public ScrollView mMsgScroll;

    private Context mContext;
    private int mMode;
    private boolean hasAlbumUpload;
    private Uri mAlbumImageUri;
    private String mAlbumImageURL;

    private List<View> viewlist;
    private float x1;
    private float x2;


    public UserMessageLayout(Context context) {
        super(context);
        init(context);
    }

    public UserMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);

        //设置显示效果
        OutlineProvider.setOutline(mUserIcon,OutlineProvider.SHAPE_OVAL);
        mMsgEdittext.getPaint().setFakeBoldText(true);
        mUserName.getPaint().setFakeBoldText(true);

        this.setClickable(true);


        setCameraDistance(50000);
    }

    @Override
    protected void onLayout(boolean changed,
                            int l, int t, int r, int b){
        super.onLayout(changed,l,t,r,b);
        x1 = mMsgScroll.getX();
        x2 = mMyTimeShow.getX();
    }

    //显示消息主体、获取图片
    public void initShow(int mode, PointData pd){
        mMode = mode;
        switchMode2(MODE2_TEXT);
        switchmode3(MODE3_POS);
        switch (mode) {
            case MainActivity.MODE_EDIT:
                //用户填写数据初始化
                hasAlbumUpload=false;
                mAlbumImageUri=null;
                mAlbumImageURL=null;

                mMsgEdittext.setText("");
                mMsgEdittext.setHint("你在这里的所闻所想");
                setEditTextEditable(mMsgEdittext,true);

                mMessageLayout.setVisibility(GONE);
//                mSendButton.setProgress(0);

                mMyTimeShow.setTime(Calendar.getInstance().getTime());

                mBlurImg.setSrc(R.drawable.default_album);
                Glide.with(mContext).load(R.drawable.default_album).into(mNoBlurImg);
                break;
            case MainActivity.MODE_MESSAGE:

                Gson gson = new Gson();
                MessageJson mj = gson.fromJson(pd.userMessage,MessageJson.class);

                if (mj.ver==100) {
                    mMsgEdittext.setText(mj.text);
                    mMsgEdittext.setHint("");
                    setEditTextEditable(mMsgEdittext, false);

                    if (!mj.albumURL.equals("no_img")) {
                        mBlurImg.setSrc(mj.albumURL);
                        Glide.with(mContext).load(mj.albumURL).into(mNoBlurImg);
                    } else {
                        mBlurImg.setSrc(R.drawable.default_album);
                        Glide.with(mContext).load(R.drawable.default_album).into(mNoBlurImg);
                    }
                }

                mMessageLayout.setVisibility(VISIBLE);
//                mSendButton.setProgress(0);

                mMyTimeShow.setTime(new Date(pd.pointTime*1000));

                DecimalFormat decimalFormat=new DecimalFormat(".00");
                String la=decimalFormat.format(pd.latitude);
                String lo=decimalFormat.format(pd.longitude);
                mLocationDes.setText("经度:"+la+"   纬度:"+lo);
                break;

        }
    }

    public void initShow2(Userinfo ui){
        mUserName.setText(ui.userName);
        mUserDescirpt.setText(ui.userDes);
        UserIconWarp.just(mContext,ui.userIcon,mUserIcon);
    }

    int lmode2;
    final int MODE2_DURATION = 600;
    public void switchMode2(int mode2){
        lmode2 = mode2;
        if (lmode2==MODE2_TEXT){
            mBlurImg.animate().alpha(1).scaleX(1f).scaleY(1f).setDuration(MODE2_DURATION).start();
            mNoBlurImg.animate().scaleX(1f).scaleY(1f).setDuration(MODE2_DURATION).start();
            mMsgScroll.animate().x(x1).setDuration(MODE2_DURATION).start();
            mMyTimeShow.animate().x(x2).setDuration(MODE2_DURATION).start();

        } else {
            mBlurImg.animate().alpha(0).scaleX(1.1f).scaleY(1.1f).setDuration(MODE2_DURATION).start();
            mNoBlurImg.animate().scaleX(1.1f).scaleY(1.1f).setDuration(MODE2_DURATION).start();
            mMsgScroll.animate().x(-mMsgScroll.getWidth()-300).setDuration(MODE2_DURATION).start();
            mMyTimeShow.animate().x(70).setDuration(MODE2_DURATION).start();
        }
    }

    public void toggleMode2(){
        if (lmode2==MODE2_TEXT)
            switchMode2(MODE2_ALBUM);
        else
            switchMode2(MODE2_TEXT);
    }

    public void tryExit(){
        if (TextUtils.isEmpty(mMsgEdittext.getText()) && !hasAlbumUpload){
            if (mExitCallback!=null)
                mExitCallback.exitCallback();
            return;
        }
        new AlertDialog.Builder(mContext).setMessage("要保存已输入的内容吗？")
                .setPositiveButton("保存",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 保存已输入的内容
                        if (mExitCallback!=null)
                            mExitCallback.exitCallback();
                    }})
                .setNegativeButton("不保存",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mExitCallback!=null)
                            mExitCallback.exitCallback();
                    }})
                .setNeutralButton("取消", null)
                .show();//在按键响应事件中显示此对话框
    }
//
//    @OnClick(R.id.addimgbutton)
//    public void addimg(){
//        pickFromGallery((Activity) mContext,SELECT_ALBUM_IMG, "选择封面");
//    }


    //接收从Activity传过来的Result,已经进行过resultCode==RESULT_OK判断了
    public void ResultCallback(int requestCode, int resultCode, Intent data){
        switch (requestCode){

            case SELECT_ALBUM_IMG:
                if (data != null) {
                    Uri imageUri = data.getData();
                    Uri mDestinationUri = Uri.fromFile(new File(mContext.getCacheDir(), "UserAlbum"+UUID.randomUUID().toString()+".jpeg"));

                    UCrop.of(imageUri, mDestinationUri)
                            .withAspectRatio(3, 4)
                            .withMaxResultSize(1080, 1440)
                            .start((Activity) mContext);
                }
                break;

            case UCrop.REQUEST_CROP:
                hasAlbumUpload=true;
                mAlbumImageUri = UCrop.getOutput(data);
                mBlurImg.setSrc(mAlbumImageUri);
                Glide.with(mContext).load(mAlbumImageUri).into(mNoBlurImg);
                break;
        }
    }

//    @OnClick(R.id.sendbutton)
//    public void newPoint(){
//        //studing
////        mSendButton.setProgress(50);
//        if (hasAlbumUpload){
//            MyUpyunManager.getIns().upload_image("MessageAlbum",mAlbumImageUri,this);
//        } else
//            uploadnoewpoint();
//    }

    //Upyun的回调
    @Override
    public void onProgress(float progress) {
    }

    @Override
    public void onComplete(boolean isSuccess, String url) {
        mAlbumImageURL = url;
        uploadnoewpoint();
    }

    private void uploadnoewpoint(){
        MainActivity.MyLatlng l = GlobalVar.viewLatlng;

        PointData2 pd2 = new PointData2();
        PointData pd = new PointData();

        pd.latitude = l.latitude;
        pd.longitude = l.longitude;
        pd.userID = GlobalVar.mUserinfo2.userinfo.userID;

        MessageJson mj= new MessageJson();
        mj.ver=100;
        mj.text = mMsgEdittext.getText().toString();
        if (hasAlbumUpload){
            mj.albumURL=mAlbumImageURL;
        } else {
            mj.albumURL="no_img";
        }

        Gson gson = new Gson();
        pd.userMessage = gson.toJson(mj);

        pd2.pointData = pd;
        pd2.userID2 = GlobalVar.mUserinfo2.userID2;

        Myserver.getApi().newPoint(pd2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointDataResult>() {
                    @Override
                    public void call() {
//                        mSendButton.setProgress(100);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mNewPointFinishCallbackCallback !=null)
                                    mNewPointFinishCallbackCallback.newPointFinishCallback();
                            }
                        },1000);
                    }

                    public void error(int statue, String errorMessage){
//                        mSendButton.setProgress(-1);
                    }
                });
    }

    //退出回调
    interface ExitCallback{
        void exitCallback();
    }
    public void setExitCallback(ExitCallback mExitCallback) {
        this.mExitCallback = mExitCallback;
    }
    ExitCallback mExitCallback=null;



    public interface NewPointFinishCallback {
        void newPointFinishCallback();
    }
    private NewPointFinishCallback mNewPointFinishCallbackCallback =null;
    public void callback(NewPointFinishCallback npf) {
        this.mNewPointFinishCallbackCallback = npf;
    }


    private float lastx;
    float dx;
    float dx_fator = 0.1f;
    private int lmode3 = MODE3_POS;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastx = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (lastx==-1){
                    lastx = event.getX();
                    break;
                }

                if (lastx>this.getRight() || lastx<this.getLeft())
                    break;

                dx = (event.getX()-lastx)*dx_fator;
                if (lmode3==MODE3_NEG)
                    dx=-dx;
                lastx = event.getX();

                Log.d(TAG,""+this.getRotationY()+" "+dx);

                float nx = this.getRotationY() + dx;
                if (nx>0)
                    nx=0;
                else if (nx<-180)
                    nx = -180;
                this.setRotationY(nx);
                break;

            case MotionEvent.ACTION_UP:
                float nx2 = this.getRotationY()+dx;

                if (lmode3==MODE3_POS ){
                    if (Math.abs(nx2)>30)
                        switchmode3(MODE3_NEG);
                    else {
                        switchmode3(MODE3_POS);
                        toggleMode2();
                    }
                } else if (lmode3==MODE3_NEG){
                    if (Math.abs(Math.abs(nx2)-180)>30)
                        switchmode3(MODE3_POS);
                    else {
                        switchmode3(MODE3_NEG);
                        toggleMode2();
                    }
                }
                break;
        }
        return true;
    }

    private void switchmode3(int mode3){
        lmode3 = mode3;
        if (mode3==MODE3_POS){
            if (this.getRotationY()>180)
                this.animate().rotationY(360).setDuration(MODE2_DURATION).start();
            else
                this.animate().rotationY(0).setDuration(MODE2_DURATION).start();
        } else {
            if (this.getRotationY()<0)
                this.animate().rotationY(-180).setDuration(MODE2_DURATION).start();
            else
                this.animate().rotationY(180).setDuration(MODE2_DURATION).start();
        }
    }

}
