package com.chen.maptest;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;

import com.chen.maptest.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by chen on 17-3-8.
 * Copyright *
 */

public class MyMapIcon extends ConstraintLayout {

    public static final int ICON_ARROW = 0;
    public static final int ICON_FLAG = 1;

    private static final String TAG="MyMapIcon";

    private int mDuration = 500;

    private Context mContext;
    private MaterialIconView mIconFlag;

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
    }


    private boolean xinited = false;
    @Override
    protected void onLayout(boolean changed,
                                     int l, int t, int r, int b){
        super.onLayout(changed,l,t,r,b);
        if(!xinited) {
            mIconFlag.setY(this.getHeight() / 2 - mIconFlag.getHeight() / 2);
            mIconFlag.setX(this.getWidth() / 2 - mIconFlag.getWidth() / 2);
            xinited=true;
        }
    }

    private int lIconMode = -1;
    public void switchIcon(int mode){
        if (mode==lIconMode)
            return;
        lIconMode = mode;
//
//        mIconFlag.setY(this.getHeight()/2-mIconFlag.getHeight()/2);
//        mIconFlag.setX(this.getWidth()/2-mIconFlag.getWidth()/2);
        switch (mode){
            case ICON_ARROW:
                mIconFlag.animate().alpha(0).setDuration(mDuration).start();
                break;
            case ICON_FLAG:
                mIconFlag.animate().alpha(1).setDuration(mDuration).start();
                break;
        }
    }


}
