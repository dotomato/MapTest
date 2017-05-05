package com.chen.maptest.ComViews;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * Created by chen on 17-3-6.
 * Copyright *
 */

public class MyBlurImageView extends android.support.v7.widget.AppCompatImageView {
    private Context mContext;
    private int mRadis=4;
    private int mDownSample=8;
    private int mColor = Color.argb(64,50,50,50);
    private ColorFilterTransformation ct;
    private BlurTransformation bt;

    public MyBlurImageView(Context context) {
        super(context);
        init(context);
    }

    public MyBlurImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyBlurImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
        if (!isInEditMode()) {
            ct = new ColorFilterTransformation(mContext, mColor);
            bt = new BlurTransformation(mContext, mRadis, mDownSample);
        }
    }

    public void setSrc(Integer res){
        Glide.with(mContext).load(res).centerCrop().bitmapTransform(bt, ct).into(this);
    }

    public void setSrc(String res){
        Glide.with(mContext).load(res).centerCrop().bitmapTransform(bt,ct).into(this);
    }

    public void setSrc(Uri res){
        Glide.with(mContext).load(res).centerCrop().bitmapTransform(bt,ct).into(this);
    }

//    public int getRadis() {
//        return mRadis;
//    }
//
//    public void setRadis(int mRadis) {
//        this.mRadis = mRadis;
//    }
//
//    public int getDownSample() {
//        return mDownSample;
//    }
//
//    public void setDownSample(int mDownSample) {
//        this.mDownSample = mDownSample;
//    }
//
//    public int getColor() {
//        return mColor;
//    }

//    public void setColor(int mColor) {
//        this.mColor = mColor;
//    }
}
