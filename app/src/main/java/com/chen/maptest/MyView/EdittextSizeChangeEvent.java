package com.chen.maptest.MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by chen on 17-3-9.
 * Copyright *
 */

public class EdittextSizeChangeEvent extends android.support.v7.widget.AppCompatEditText {

    private Context mContext;

    public EdittextSizeChangeEvent(Context context) {
        super(context);
        init(context);
    }

    public EdittextSizeChangeEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EdittextSizeChangeEvent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        if (mSizeChangeCallback!=null)
            mSizeChangeCallback.SizeChangeCallback(w,h);
    }

    public interface SizeChangeCallback{
        void SizeChangeCallback(int w, int h);
    }
    private SizeChangeCallback mSizeChangeCallback=null;
    public void setSizeChangeCallback(SizeChangeCallback var){
        mSizeChangeCallback = var;
    }
}
