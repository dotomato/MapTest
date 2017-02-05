package com.chen.maptest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                .map(new Function<String, Bitmap>(){

                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return BitmapFactory.decodeFile(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
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
}
