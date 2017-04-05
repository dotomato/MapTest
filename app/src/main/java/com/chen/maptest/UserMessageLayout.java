package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
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
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.UserIconWarp;
import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chen.maptest.Utils.MyUtils.pickFromGallery;
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

    @BindView(R.id.messagelayout_1)
    public ViewGroup ml1;

    @BindView(R.id.messagelayout_2)
    public ViewGroup ml2;

    @BindView(R.id.sendbutton)
    public CircularProgressButton mSendButton;

    @BindView(R.id.likenum)
    public TextView mLikeNum;

    @BindView(R.id.likebutton)
    public ShineButton mLikeButton;

    private Context mContext;

    private ViewPager mParent_ViewPager;
    private int mMode;
    private boolean hasAlbumUpload;
    private Uri mAlbumImageUri;
    private String mAlbumImageURL;

    private List<View> viewlist;
    private float x1;
    private float x2;
    private PointData mPointData;


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

        setClickable(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (mParent_ViewPager==null)
//                    return;
//                if (mParent_ViewPager.getCurrentItem()==0)
                    toggleMode2();
            }
        });

        mSendButton.setIndeterminateProgressMode(true);
    }

    @Override
    protected void onLayout(boolean changed,
                            int l, int t, int r, int b){
        super.onLayout(changed,l,t,r,b);
        x1 = mMsgScroll.getX();
        x2 = mMyTimeShow.getX();
    }

    public void setViewPager(ViewPager var){
        mParent_ViewPager = var;
    }

    //显示消息主体、获取图片
    public void initShow(int mode, PointData pd){
        mPointData = pd;
        mMode = mode;
        switchMode2(MODE2_TEXT);
        switch (mode) {
            case MainActivity.MODE_EDIT:
                //用户填写数据初始化
                hasAlbumUpload=false;
                mAlbumImageUri=null;
                mAlbumImageURL=null;

                mMsgEdittext.setText("");
                mMsgEdittext.setHint("你在这里的所闻所想");
                setEditTextEditable(mMsgEdittext,true);

                mMyTimeShow.setTime(Calendar.getInstance().getTime());

                mBlurImg.setSrc(R.drawable.default_album);
                Glide.with(mContext).load(R.drawable.default_album).into(mNoBlurImg);

                ml1.setVisibility(GONE);
                ml2.setVisibility(VISIBLE);

                mSendButton.setProgress(0);
                break;
            case MainActivity.MODE_MESSAGE:

                Gson gson = new Gson();
                MessageJson mj = gson.fromJson(mPointData.userMessage,MessageJson.class);

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

                mMyTimeShow.setTime(new Date(mPointData.pointTime*1000));

                DecimalFormat decimalFormat=new DecimalFormat(".00");
                String la=decimalFormat.format(mPointData.latitude);
                String lo=decimalFormat.format(mPointData.longitude);
                mLocationDes.setText("经度:"+la+"   纬度:"+lo);

                mLikeNum.setText(String.valueOf(mPointData.pointLikeNum));
                mLikeButton.setChecked(GlobalVar.mUserinfo2.userinfo.userLikePointIDList.contains(mPointData.pointID),false);

                ml1.setVisibility(VISIBLE);
                ml2.setVisibility(GONE);
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
            mMsgScroll.animate().x(x1).setDuration(MODE2_DURATION/2).start();
            mMyTimeShow.animate().x(x2).setDuration(MODE2_DURATION/2).start();
            mInnerEdge.setShadowAlpha(1);

        } else {
            mBlurImg.animate().alpha(0).scaleX(1.05f).scaleY(1.05f).setDuration(MODE2_DURATION).start();
            mNoBlurImg.animate().scaleX(1.05f).scaleY(1.05f).setDuration(MODE2_DURATION).start();
            mMsgScroll.animate().x(-mMsgScroll.getWidth()-300).setDuration(MODE2_DURATION/2).start();
            mMyTimeShow.animate().x(getWidth()+100).setDuration(MODE2_DURATION/2).start();
            mInnerEdge.setShadowAlpha(0);
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

    @OnClick(R.id.albumbutton)
    public void addimg(){
        pickFromGallery((Activity) mContext,SELECT_ALBUM_IMG, "选择封面");
    }


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

    @OnClick(R.id.sendbutton)
    public void newPoint(){
        mSendButton.setProgress(50);
        if (hasAlbumUpload){
            MyUpyunManager.getIns().upload_image("MessageAlbum",mAlbumImageUri,this);
        } else
            uploadnoewpoint();
    }

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
                        mSendButton.setProgress(100);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mNewPointFinishCallbackCallback !=null)
                                    mNewPointFinishCallbackCallback.newPointFinishCallback();
                            }
                        },1000);
                    }

                    public void error(int statue, String errorMessage){
                        mSendButton.setProgress(-1);
                    }
                });
    }

    @OnClick(R.id.likebutton)
    public void OnLikeButtonClike(){
        UserLikePoint ulp = new UserLikePoint();
        ulp.pointID = mPointData.pointID;
        ulp.isLike = mLikeButton.isChecked();
        ulp.userID = GlobalVar.mUserinfo2.userinfo.userID;
        ulp.userID2 = GlobalVar.mUserinfo2.userID2;
        Myserver.getApi().userlikepoint(ulp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserLikePointResult>() {
                    @Override
                    public void call() {
                        updatePoint(mVar);
                    }
                });
    }

    private void updatePoint(UserLikePointResult mVar) {
        if (!mPointData.pointID.equals(mVar.pointID))
            return;
        mLikeButton.setChecked(mVar.isLike);
        mLikeNum.setText(String.valueOf(mVar.pointLikeNum));

        List<String> ulpd = GlobalVar.mUserinfo2.userinfo.userLikePointIDList;
        boolean isContain = ulpd.contains(mVar.pointID);
        if (mVar.isLike && !isContain)
            ulpd.add(mVar.pointID);
        else if (!mVar.isLike && isContain)
            ulpd.remove(mVar.pointID);
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


}
