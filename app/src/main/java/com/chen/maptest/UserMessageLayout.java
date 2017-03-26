package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.EdittextSizeChangeEvent;
import com.chen.maptest.MyView.InnerEdge;
import com.chen.maptest.MyView.MyBlurImageView;
import com.chen.maptest.MyView.MyTimeShow;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.MyView.MyPullZoomScrollView;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.MyView.QuickPageAdapter;
import com.chen.maptest.Utils.UserIconWarp;
import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

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
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.chen.maptest.Utils.MyUtils.pickFromGallery;
import static com.chen.maptest.Utils.MyUtils.setEditTextEditable;

/**
 * Created by chen on 17-2-3.
 * Copyright *
 */

public class UserMessageLayout extends MyPullZoomScrollView implements MyPullZoomScrollView.OnPullZoomListener, MyUpyunManager.UploadProgress {

    private final static String TAG = "UserMessageLayout";


    static public final int SELECT_ALBUM_IMG = 0;

    @BindView(R.id.usericon)
    public ImageView mUserIcon;

    @BindView(R.id.username)
    public TextView mUserName;

    @BindView(R.id.userdescript)
    public TextView mUserDescirpt;

    @BindView(R.id.space)
    public Space mSpace;

    @BindView(R.id.blurimg)
    public MyBlurImageView mBlurImg;

    @BindView(R.id.noblurimg)
    public ImageView mNoBlurImg;

    @BindView(R.id.sendbutton)
    public CircularProgressButton mSendButton;

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

    public EdittextSizeChangeEvent mMsgEdittext;

    public NestedScrollView mMsgScroll;

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

    private void init(Context context){
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);

        View view1 = LayoutInflater.from(mContext).inflate(R.layout.ump_msgshow,null,false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.ump_showspace,null,false);
        mMsgEdittext = (EdittextSizeChangeEvent)view1.findViewById(R.id.msgedittext);
        mMsgScroll = (NestedScrollView)view1.findViewById(R.id.msgscroll);
        mMsgEdittext.setSizeChangeCallback(new EdittextSizeChangeEvent.SizeChangeCallback() {
            @Override
            public void SizeChangeCallback(int w, int h) {
                mMsgScroll.setNestedScrollingEnabled(mMsgScroll.getHeight()<h);
            }
        });

        viewlist =new ArrayList<>();
        viewlist.add(view1);
        viewlist.add(view2);
        mViewPager.setAdapter(new QuickPageAdapter<>(viewlist));
        mViewPager.setPageTransformer(false,new ParallaxPagerTransformer());    //实现消息文字和时间滑动不同步

        //设置显示效果
        OutlineProvider.setOutline(mUserIcon,OutlineProvider.SHAPE_OVAL);
        mMsgEdittext.getPaint().setFakeBoldText(true);
        mUserName.getPaint().setFakeBoldText(true);

        //下拉放大效果
        setOnPullZoomListener(this);

        mSendButton.setIndeterminateProgressMode(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        //设置zoomview刚好与MsgMain的高度填满屏幕
        int h = MeasureSpec.getSize(heightMeasureSpec);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) zoomview.getLayoutParams();
        lp.height = (h) - mMsgMain.getHeight();
        if (lp.height<0)
            lp.height=0;
        zoomview.setLayoutParams(lp);
        setZoomView(zoomview); //设置zoomview时必须先设置好其LayoutParams，所以在这里设置
    }

    //显示消息主体、获取图片
    public void initShow(int mode, PointData pd){
        mMode = mode;
        mViewPager.setCurrentItem(0,false);
        scrollTo(0,0);
        mMsgScroll.scrollTo(0,0);
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
                mEditLayout.setVisibility(VISIBLE);
                mSendButton.setProgress(0);

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
                mEditLayout.setVisibility(GONE);
                mSendButton.setProgress(0);

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

    private boolean spaceInto = false;
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev){
        //在这里判断是否属于Space区间，然后回调
        if (spaceInto || ev.getY()<mSpace.getHeight()-getScrollY() ) {
            spaceInto = true;
            if (mSpaceTouchCallback != null)
                mSpaceTouchCallback.spaceTouchcallback(ev);
            if (ev.getAction()==MotionEvent.ACTION_UP)
                spaceInto=false;
            return true;
        }
        return super.onTouchEvent(ev);
    }


    //根据下拉超过的距离判断是否收起
    private int lastY;
    @Override
    public void onPullZooming(int newScrollValue) {
        lastY = newScrollValue;
    }

    @Override
    public void onPullZoomEnd() {
        if (lastY<-200) {
            if (mMode==MainActivity.MODE_EDIT)
                tryExit();
            else if (mExitCallback!=null)
                mExitCallback.exitCallback();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollCallback!=null)
            mScrollCallback.scrollCallback(t);
    }


    public int getSpaceHeight(){
        return zoomview.getLayoutParams().height;
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

    @OnClick(R.id.addimgbutton)
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
        //studing
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


    //空白区域回调
    interface SpaceTouchCallback {
        void spaceTouchcallback(MotionEvent ev);
    }
    public void setSpaceTouchCallback(SpaceTouchCallback var){
        mSpaceTouchCallback =var;
    }
    SpaceTouchCallback mSpaceTouchCallback =null;



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
