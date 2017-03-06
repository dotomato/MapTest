package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyUpyun.MyUpyunManager;
import com.chen.maptest.MyView.FixedScroller;
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
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @BindView(R.id.back)
    public ImageView mBack;

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

    @BindView(R.id.sendbutton)
    public Button mSendButton;

    @BindView(R.id.time)
    public TextView mTimeText;

    @BindView(R.id.messagelayout)
    public ViewGroup mMessageLayout;

    @BindView(R.id.editlayout)
    public ViewGroup mEditLayout;

    @BindView(R.id.zoomview)
    public ViewGroup zoomview;

    @BindView(R.id.viewpager)
    public ViewPager mViewpager;

    @BindView(R.id.addimgbutton)
    public Button mAddimgButton;

    @BindView(R.id.timeshow)
    public MyTimeShow mMyTimeShow;

    private EditText mMsgEdittext;

    private PointData mPointData;
    private Context mContext;
    private int mMode;
    private List<View> viewList;
    private boolean hasAlbumUpload;
    private Uri mAlbumImageUri;
    private String mAlbumImageURL;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);

        LayoutInflater inflater=LayoutInflater.from(mContext);
        mMsgEdittext  = (EditText) inflater.inflate(R.layout.ump_msgedittext, null);

        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        viewList.add(mMsgEdittext);
        mViewpager.setAdapter(new QuickPageAdapter<>(viewList));

        try{
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Interpolator sInterpolator = new AccelerateDecelerateInterpolator();
            FixedScroller scroller = new FixedScroller(mContext,sInterpolator);
            mScroller.set(mViewpager,scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }


        OutlineProvider.setOutline(mUserIcon,OutlineProvider.SHAPE_OVAL);
        setZoomView(zoomview);
//        setAlphaView(mViewpager);
        setOnPullZoomListener(this);

        if (!mBlurImg.isInEditMode())
            mBlurImg.setSrc(R.drawable.imgtest);
    }



    public void initshow(int mode,@Nullable PointData pd){
        mPointData = pd;
        mMode = mode;
        mViewpager.setCurrentItem(0,false);

        switch (mode) {
            case MainActivity.MODE_EDIT:
                hasAlbumUpload=false;
                mMsgEdittext.setText("");
                setEditTextEditable(mMsgEdittext,true);

                mMessageLayout.setVisibility(GONE);
                mEditLayout.setVisibility(VISIBLE);

                mMyTimeShow.setTime(Calendar.getInstance().getTime());

//                mImg1.setVisibility(INVISIBLE);
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
//                        Glide.with(mContext).load(mj.albumURL).into(mImg1);
//                        mImg1.setVisibility(VISIBLE);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mViewpager.setCurrentItem(1,true);
                            }
                        },1000);
                    } else {
//                        mImg1.setVisibility(INVISIBLE);
                    }
                }

                mMessageLayout.setVisibility(VISIBLE);
                mEditLayout.setVisibility(GONE);

                mMyTimeShow.setTime(new Date(pd.pointTime*1000));

                DecimalFormat decimalFormat=new DecimalFormat(".00");
                String la=decimalFormat.format(pd.latitude);
                String lo=decimalFormat.format(pd.longitude);
                mTimeText.setText("经度:"+la+"   纬度:"+lo);

                break;

        }
        scrollTo(0,0);
    }



    public void initshow2(Userinfo ui){
        mUserName.setText(ui.userName);
        mUserDescirpt.setText(ui.userDes);
        UserIconWarp.just(mContext,ui.userIcon,mUserIcon);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
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
        if (lastY<-200) {
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
                                File outfile = new File(mContext.getCacheDir(), "UserAlbum.jpeg");
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
//                                mImg1.setImageURI(null);
//                                mImg1.setImageURI(mAlbumImageUri);
//                                mImg1.setVisibility(VISIBLE);
//                                mViewpager.setCurrentItem(0,false);
//                                mViewpager.setCurrentItem(1,true);
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

}
