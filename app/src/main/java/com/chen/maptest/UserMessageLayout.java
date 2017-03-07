package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.InnerEdge;
import com.chen.maptest.MyView.MyBlurImageView;
import com.chen.maptest.MyView.MyTimeShow;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.MyView.MyPullZoomScrollView;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.MyView.QuickPageAdapter;
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.UserIconWarp;
import com.google.gson.Gson;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.chen.maptest.Utils.MyUtils.pickFromGallery;
import static com.chen.maptest.Utils.MyUtils.setEditTextEditable;

/**
 * Created by chen on 17-2-3.
 * Copyright *
 */

public class UserMessageLayout extends MyPullZoomScrollView implements MyPullZoomScrollView.OnPullZoomListener, MyUpyunManager.UploadProgress {

    private final static String TAG = "UserMessageLayout";

    public ImageView mNameBar;

    public ImageView mUserIcon;

    public TextView mUserName;

    public TextView mUserDescirpt;

    @BindView(R.id.space)
    public Space mSpace;

    @BindView(R.id.blurimg)
    public MyBlurImageView mBlurImg;

    @BindView(R.id.noblurimg)
    public ImageView mNoBlurImg;

    @BindView(R.id.sendbutton)
    public Button mSendButton;

    @BindView(R.id.messagelayout)
    public ViewGroup mMessageLayout;

    @BindView(R.id.editlayout)
    public ViewGroup mEditLayout;

    @BindView(R.id.zoomview)
    public ViewGroup zoomview;

    @BindView(R.id.addimgbutton)
    public Button mAddimgButton;

    @BindView(R.id.viewpager)
    public ViewPager mViewPager;

    @BindView(R.id.timeshow)
    public MyTimeShow mMyTimeShow;

    @BindView(R.id.msgmain)
    public ViewGroup mMsgMain;

    @BindView(R.id.inneredge)
    public InnerEdge mInnerEdge;

    @BindView(R.id.locationdes)
    public TextView mLocationDes;

    public EditText mMsgEdittext;



    private Context mContext;
    private int mMode;
    private boolean hasAlbumUpload;
    private Uri mAlbumImageUri;
    private String mAlbumImageURL;

    private List<View> viewlist;


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

    @Override
    public void onProgress(float progress) {

    }

    @Override
    public void onComplete(boolean isSuccess, String url) {
        mAlbumImageURL = url;
        uploadnoewpoint();
    }



    public interface NewPointFinish{
        void NPFcall();
    }

    public void setNewPointFinishCallback(NewPointFinish npf) {
        this.mNewPointFinishCallback = npf;
    }

    private NewPointFinish mNewPointFinishCallback =null;

    private void init(Context context){
        mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) zoomview.getLayoutParams();
        lp.height = (b-t) - mMsgMain.getLayoutParams().height;
        if (lp.height<0)
            lp.height=0;
        zoomview.setLayoutParams(lp);
        setZoomView(zoomview);
        super.onLayout(changed,l,t,r,b);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);

        View view1 = LayoutInflater.from(mContext).inflate(R.layout.ump_msgshow,null,false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.ump_showspace,null,false);
        mMsgEdittext = (EditText)view1.findViewById(R.id.msgedittext);

        mUserName = (TextView)findViewById(R.id.username);
        mUserDescirpt = (TextView)findViewById(R.id.userdescript);
        mUserIcon = (ImageView)findViewById(R.id.usericon);

        viewlist =new ArrayList<>();
        viewlist.add(view1);
        viewlist.add(view2);
        mViewPager.setAdapter(new QuickPageAdapter<>(viewlist));
        mViewPager.setPageTransformer(false,new ParallaxPagerTransformer());

        OutlineProvider.setOutline(mUserIcon,OutlineProvider.SHAPE_OVAL);
        mMsgEdittext.getPaint().setFakeBoldText(true);
        mUserName.getPaint().setFakeBoldText(true);
        setOnPullZoomListener(this);
    }

    public void initShow(int mode, @Nullable PointData pd){
        mMode = mode;
        mViewPager.setCurrentItem(0,false);
        scrollTo(0,0);
        switch (mode) {
            case MainActivity.MODE_EDIT:
                hasAlbumUpload=false;
                mMsgEdittext.setText("");
                setEditTextEditable(mMsgEdittext,true);

                mMessageLayout.setVisibility(GONE);
                mEditLayout.setVisibility(VISIBLE);

                mMyTimeShow.setTime(Calendar.getInstance().getTime());

                mBlurImg.setSrc(R.drawable.default_album);
                Glide.with(mContext).load(R.drawable.default_album).into(mNoBlurImg);
                break;
            case MainActivity.MODE_MESSAGE:
                if (pd==null)
                    return;

                Gson gson = new Gson();
                MessageJson mj = gson.fromJson(pd.userMessage,MessageJson.class);

                if (mj.ver==100) {
                    mMsgEdittext.setText(mj.text);
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
                mEditLayout.setVisibility(GONE);

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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (mMode==MainActivity.MODE_MESSAGE)
//            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev){
        if (ev.getY()<mSpace.getHeight()-getScrollY() ) {
            if (mSpaceTouchEventCallback != null)
                mSpaceTouchEventCallback.onSpaceTouchEvent(ev);
            return true;
        }
        return super.onTouchEvent(ev);
    }


    SpaceTouchEventCallback mSpaceTouchEventCallback=null;

    private int lastY;
    @Override
    public void onPullZooming(int newScrollValue) {
        lastY = newScrollValue;
    }

    @Override
    public void onPullZoomEnd() {
        if (lastY<-100) {
            if (mMode==MainActivity.MODE_EDIT)
                tryExit();
            else if (mExitCallback!=null)
                mExitCallback.call();
        }
    }

    interface SpaceTouchEventCallback{
        void onSpaceTouchEvent(MotionEvent ev);
    }
    public void setSpaceTouchEventCallback(SpaceTouchEventCallback var){
        mSpaceTouchEventCallback=var;
    }


    public int getSpaceHeight(){
        return mSpace.getHeight();
    }


    public void setExitCallback(ExitCallback mExitCallback) {
        this.mExitCallback = mExitCallback;
    }

    ExitCallback mExitCallback=null;
    interface ExitCallback{
        void call();
    }


    public void tryExit(){
        if (TextUtils.isEmpty(mMsgEdittext.getText()) && !hasAlbumUpload){
            if (mExitCallback!=null)
                mExitCallback.call();
            return;
        }
        new AlertDialog.Builder(mContext).setMessage("要保存已输入的内容吗？")
                .setPositiveButton("保存",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 保存已输入的内容
                        if (mExitCallback!=null)
                            mExitCallback.call();
                    }})
                .setNegativeButton("不保存",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mExitCallback!=null)
                            mExitCallback.call();
                    }})
                .setNeutralButton("取消", null)
                .show();//在按键响应事件中显示此对话框
    }

    @OnClick(R.id.addimgbutton)
    public void addimg(){
        pickFromGallery((Activity) mContext,MainActivity.SELECT_ALBUM_IMG, "选择封面");
    }

    public void ResultCallback(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case MainActivity.SELECT_ALBUM_IMG:

                Observable.just(data.getData())
                        .map(new Func1<Uri, File>() {
                            @Override
                            public File call(Uri uri) {
                                File outfile = new File(mContext.getCacheDir(), "UserAlbum"+UUID.randomUUID().toString()+".jpeg");
                                Bitmap bm = MyUtils.getBitmapSmall(uri.getPath(), 1080 * 720);
                                MyUtils.saveBitmap(outfile,bm);
                                return outfile;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                hasAlbumUpload=true;
                                mAlbumImageUri = Uri.fromFile(file);
                                clearAlbumImgUri();
                                mBlurImg.setSrc(mAlbumImageUri);
                                Glide.with(mContext).load(mAlbumImageUri).into(mNoBlurImg);
                            }
                        });
                break;
        }
    }

    @OnClick(R.id.sendbutton)
    public void newPoint(){
        if (hasAlbumUpload){
            MyUpyunManager.getIns().upload_image(mAlbumImageUri,this);
        } else
            uploadnoewpoint();

    }

    private void clearAlbumImgUri(){
        mBlurImg.setImageURI(null);
        mNoBlurImg.setImageURI(null);
    }

    private void uploadnoewpoint(){
        MainActivity.MyLatlng l = GlobalVar.viewLatlng;

        NewPointData npd = new NewPointData();
        PointData pd = new PointData();

        pd.latitude = l.latitude;
        pd.longitude = l.longitude;
        pd.userID = GlobalVar.mUserinfo.userID;

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

        npd.pointData = pd;

        Myserver.getApi().newPoint(npd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<NewPointResult>() {
                    @Override
                    public void call() {
                        Log.d(TAG, "newPoint result: " + mVar.statue + " " + mVar.pointData.pointID);
                        if (mNewPointFinishCallback !=null)
                            mNewPointFinishCallback.NPFcall();
                    }
                });
    }

    private class ParallaxPagerTransformer implements ViewPager.PageTransformer {
        private final float speed = 0.6f;
        private final float scale = 0.05f;

        @Override
        public void transformPage(View view, float position) {
            if (view.equals(viewlist.get(0))){
                if (position > -1 && position < 1) {
                    float width = view.getWidth();
                    mMyTimeShow.setTranslationX((position * width * speed));
                    mBlurImg.setAlpha(1+position);
                    mInnerEdge.setShadowAlpha(1+position);
                    mNoBlurImg.setScaleX(1-position*scale);
                    mNoBlurImg.setScaleY(1-position*scale);
                }
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollCallback!=null)
            mScrollCallback.callback(t);
    }


    public interface ScrollCallback {
        void callback(int t);
    }
    private ScrollCallback mScrollCallback=null;
    public void setScrollCallback(ScrollCallback scrollCallback) {
        this.mScrollCallback = scrollCallback;
    }
}
