package com.chen.maptest.MyView;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.chen.maptest.AlbumActivity;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;

/**
 * Created by chen on 17-3-6.
 * Copyright *
 */

public class MyBlurImageView extends ImageView {
    private Context mContext;
    private int mRadis=4;
    private int mDownSample=16;
    private int mColor = Color.argb(128,50,50,50);
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
    }

    public void setSrc(Integer res){
        BlurTransformation bt = new BlurTransformation(mContext,mRadis,mDownSample);
        ColorFilterTransformation ct = new ColorFilterTransformation(mContext, Color.argb(128,50,50,50));
        Glide.with(mContext).load(res).centerCrop().bitmapTransform(bt,ct).into(this);
    }

    public void setSrc(String res){
        BlurTransformation bt = new BlurTransformation(mContext,mRadis,mDownSample);
        ColorFilterTransformation ct = new ColorFilterTransformation(mContext, Color.argb(128,50,50,50));
        Glide.with(mContext).load(res).centerCrop().bitmapTransform(bt,ct).into(this);
    }

    public int getRadis() {
        return mRadis;
    }

    public void setRadis(int mRadis) {
        this.mRadis = mRadis;
    }

    public int getDownSample() {
        return mDownSample;
    }

    public void setDownSample(int mDownSample) {
        this.mDownSample = mDownSample;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }
}
