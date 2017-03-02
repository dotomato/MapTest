package com.chen.maptest;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;


import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.MyView.TopEventScrollView;

import com.chen.maptest.MyModel.*;
import com.chen.maptest.Utils.UserIconWarp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import butterknife.BindView;

/**
 * Created by chen on 17-2-3.
 * Copyright *
 */

public class UserMessageLayout extends TopEventScrollView {

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

    @BindView(R.id.sendbutton)
    public Button mSendButton;

    @BindView(R.id.time)
    public TextView mTimeText;

    @BindView(R.id.usereditmessage)
    public EditText mEditMessage;

    @BindView(R.id.messagelayout)
    public ViewGroup mMessageLayout;

    @BindView(R.id.editlayout)
    public ViewGroup mEditLayout;

    private PointData mPointData;
    private Context mContext;

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

        OutlineProvider.setOutline(mUserIcon,OutlineProvider.SHAPE_OVAL);
    }

    public void setUserIcon(String var){
        Observable.just(var)
                .map(new Func1<String, Bitmap>(){

                    @Override
                    public Bitmap call(String s) {
                        return BitmapFactory.decodeFile(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        mUserIcon.setImageBitmap(bitmap);
                    }
                });
    }

    public void initshow(int mode,@Nullable PointData pd){
        mPointData = pd;

        switch (mode) {
            case MainActivity.MODE_EDIT:
                mEditMessage.setText("");
                mEditMessage.setFocusable(true);
                //TODO 恢复编辑有问题

                mMessageLayout.setVisibility(GONE);
                mEditLayout.setVisibility(VISIBLE);
                break;
            case MainActivity.MODE_MESSAGE:
                if (pd==null)
                    return;
                mEditMessage.setText(pd.userMessage);
                mEditMessage.setFocusable(false);

                mMessageLayout.setVisibility(VISIBLE);
                mEditLayout.setVisibility(GONE);

                DateFormat time =  SimpleDateFormat.getDateTimeInstance();
                String datatime = time.format(new Date(pd.pointTime*1000));
                mTimeText.setText(datatime);
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
    interface SpaceTouchEventCallback{
        void onSpaceTouchEvent(MotionEvent ev);
    }
    public void setSpaceTouchEventCallback(SpaceTouchEventCallback var){
        mSpaceTouchEventCallback=var;
    }


    public int getSpaceHeight(){
        return mSpace.getHeight();
    }


    ExitCallback mExitCallback=null;
    interface ExitCallback{
        void call();
    }

    public void tryExit(ExitCallback var){
        mExitCallback = var;
        if (TextUtils.isEmpty(mEditMessage.getText())){
            if (var!=null)
                var.call();
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

    public NewPointData getNewPointData(){
        MainActivity.MyLatlng l = GlobalVar.viewLatlng;

        NewPointData npd = new NewPointData();
        PointData pd = new PointData();

        pd.latitude = l.latitude;
        pd.longitude = l.longitude;
        pd.userID = GlobalVar.mUserinfo.userID;
        pd.userMessage = mEditMessage.getText().toString();

        npd.pointData = pd;
        return npd;
    }




}
