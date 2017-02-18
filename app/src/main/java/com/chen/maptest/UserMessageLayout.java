package com.chen.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;


import butterknife.ButterKnife;

import butterknife.OnTouch;
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

    @BindView(R.id.usericon)
    public ImageView mUserIcon;

    @BindView(R.id.username)
    public TextView mUserName;

    @BindView(R.id.userdescript)
    public TextView mUserDescirpt;

    @BindView(R.id.usermessage)
    public TextView mUserMessage;

    @BindView(R.id.space)
    public Space mSpace;

    private float spaceHight;

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
        spaceHight = MyUtils.dip2px(context,200);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);
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

    public void setUserName(String var){
        mUserName.setText(var);
    }

    public void setUserDescript(String var){
        mUserDescirpt.setText(var);
    }

    public void setUserMessage(String var){
        mUserMessage.setText(var);
    }

    public void initshow(){
        scrollTo(0,0);
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
}
