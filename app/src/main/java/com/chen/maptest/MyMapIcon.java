package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import com.chen.maptest.Utils.MyUtils;
import com.sackcentury.shinebuttonlib.ShineButton;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by chen on 17-3-8.
 * Copyright *
 */

public class MyMapIcon extends ConstraintLayout {

    public static final int ICON_HIDE = 0;
    public static final int ICON_FLAG = 1;
//    public static final int ICON_ARROW = 2;

    private static final String TAG="MyMapIcon";

    private int mDuration = 500;

    private Context mContext;
    private MaterialIconView mIconFlag;
//    private MaterialIconView mIconArrow;
    private MaterialIconView mCurIcon;

    public MyMapIcon(Context context) {
        super(context);
        init(context);
    }

    public MyMapIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyMapIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIconFlag = (MaterialIconView) findViewById(R.id.iconflag);
//        mIconArrow = (MaterialIconView) findViewById(R.id.iconarrow);
    }


//    private boolean xinited = false;
//    @Override
//    protected void onLayout(boolean changed,
//                                     int l, int t, int r, int b){
//        super.onLayout(changed,l,t,r,b);
//        if(!xinited) {
//            mIconFlag.setY(this.getHeight() / 2 - mIconFlag.getHeight() / 2);
//            mIconFlag.setX(this.getWidth() / 2 - mIconFlag.getWidth() / 2);
//            xinited=true;
//        }
//    }

    private int lIconMode = -1;
    public void switchIcon(int mode){
        if (mode==lIconMode)
            return;
        lIconMode = mode;
        switch (mode){
            case ICON_HIDE:
                mIconFlag.animate().alpha(0).setDuration(mDuration).start();
//                mIconArrow.animate().alpha(0).setDuration(mDuration).start();
                mCurIcon = null;
                break;
            case ICON_FLAG:
                mIconFlag.animate().alpha(1).setDuration(mDuration).start();
//                mIconArrow.animate().alpha(0).setDuration(mDuration).start();
                mCurIcon = mIconFlag;
                break;
//            case ICON_ARROW:
//                mIconFlag.animate().alpha(0).setDuration(mDuration).start();
//                mIconArrow.animate().alpha(1).setDuration(mDuration).start();
//                mCurIcon = mIconArrow;
//                break;
        }
    }

    public void shine_button(PointF p){

        int tw = MyUtils.dip2px(mContext,35);

        final ShineButton shineButtonJava = new ShineButton(mContext);
        shineButtonJava.setBtnColor(Color.GRAY);
        shineButtonJava.setBtnFillColor(Color.rgb(255,64,129));
        shineButtonJava.setAllowRandomColor(true);
        shineButtonJava.setShapeResource(R.raw.star);
        shineButtonJava.setClickAnimDuration(1000);
        shineButtonJava.setShineTurnAngle(180);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(tw,tw);
        shineButtonJava.setLayoutParams(layoutParams);
        addView(shineButtonJava);
        shineButtonJava.setX(p.x-tw/2);
        shineButtonJava.setY(p.y-tw/2);
        shineButtonJava.setChecked(true,true);

        Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(4)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    switch (aLong.intValue()){
                        case 0:case 1: break;
                        case 2:
                            shineButtonJava.animate().alpha(0).scaleX(0.3f).scaleY(0.3f).setDuration(300).start();
                            break;
                        case 3:
                            removeView(shineButtonJava);
                            break;
                    }
                }
            });
    }


    public void gotoLalng(PointF p) {
        if (mCurIcon == null)
            return;
        mCurIcon.setX(p.x-mIconFlag.getWidth()/2);
        mCurIcon.setY(p.y-mIconFlag.getHeight()/2);
    }
}
