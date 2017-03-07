package com.chen.maptest.MyView;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.chen.maptest.R;

import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * Created by chen on 17-3-8.
 * Copyright *
 */

public class MyMapIcon extends ConstraintLayout {

    public static final int ICON_ARROW = 0;
    public static final int ICON_FLAG = 1;


    public static final int ANI_UP = 0;
    public static final int ANI_DOWN = 1;


    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    private int mDuration = 500;

    private Context mContext;
    private MaterialIconView mIconArrow;
    private MaterialIconView mIconFlag;
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
        mIconArrow = (MaterialIconView)findViewById(R.id.icondown);
        mIconFlag = (MaterialIconView)findViewById(R.id.iconflag);
    }

    private int lIconMode = -1;
    public void switchIcon(int mode){
        if (mode==lIconMode)
            return;
        lIconMode = mode;
        switch (mode){
            case ICON_ARROW:
                mIconArrow.animate().alpha(1).setDuration(mDuration).start();
                mIconFlag.animate().alpha(0).setDuration(mDuration).start();
                mCurIcon = mIconArrow;
//                mIconDown.animate().y(0).setDuration(mDuration).start();
                break;
            case ICON_FLAG:
                mIconArrow.animate().alpha(0).setDuration(mDuration).start();
                mIconFlag.animate().alpha(1).setDuration(mDuration).start();
                mCurIcon = mIconFlag;
                break;
        }
    }

    private int lAniMode = -1;
    public void switchAni(int mode){
        if (mode==lAniMode)
            return;
        lAniMode = mode;
        switch (mode){
            case ANI_DOWN:
                mCurIcon.animate().translationY(0).setDuration(mDuration).start();
                break;
            case ANI_UP:
                mCurIcon.animate().translationY(-50).setDuration(mDuration).start();
                break;
        }
    }

}
