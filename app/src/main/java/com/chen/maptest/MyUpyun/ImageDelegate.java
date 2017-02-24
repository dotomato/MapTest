package com.chen.maptest.MyUpyun;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-2-25.
 * Copyright *
 */

public class ImageDelegate {
    public static final int SRC_TAG = 1;

    private WeakReference<ImageView> mImageView;
    private String mSrc;

    public ImageDelegate(ImageView imageView){
        mImageView=new WeakReference<>(imageView);
    }

    public ImageDelegate setSrc(String src){
        ImageView i = mImageView.get();
        if (i!=null){
            mSrc = src;
            i.setTag(SRC_TAG,mSrc);
        }
        return this;
    }

    public ImageDelegate doit(){
        Observable.just(mSrc)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        ImageView i = mImageView.get();
                        if (i!=null) {
                            if (mSrc.equals(i.getTag(SRC_TAG))) {
                                i.setImageBitmap(bitmap);
                            }
                        }
                    }
                });
        return this;
    }
}
