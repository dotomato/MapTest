package com.chen.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


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

public class UserMessageLayout extends ConstraintLayout {

    @BindView(R.id.usericon)
    public ImageView mUserIcon;

    @BindView(R.id.username)
    public TextView mUserName;

    @BindView(R.id.userdescript)
    public TextView mUserDescirpt;

    @BindView(R.id.usermessage)
    public TextView mUserMessage;


    @BindView(R.id.topeventscrollview)
    public TopEventScrollView mTopEventScrollVew;


    public UserMessageLayout(Context context) {
        super(context);
    }

    public UserMessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        mTopEventScrollVew.scrollTo(0,0);
    }
}
