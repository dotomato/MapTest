package com.chen.maptest.MyView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.chen.maptest.Utils.MyUtils;

/**
 * Created by chen on 17-2-18.
 * Copyright *
 */

public class TopEventScrollView extends ScrollView {

    private final static String TAG = "TopEventScrollView";

    private float lastY;

    private float delatY;

    private OverScrollCallback mOverScrollCallback=null;

    public void setDelatY(float delatY) {
        if (delatY<0)
            delatY=0;
        else
            delatY = delatY;
    }

    public interface OverScrollCallback{
        void onOverScroll(ScrollView scrollView);
    }

    public TopEventScrollView(Context context) {
        super(context);
        init(context);
    }

    public TopEventScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopEventScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        delatY = MyUtils.dip2px(context,5);
    }


    private boolean upFlag=true;
    private boolean down_moveFlag=false;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG,ev.toString());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                down_moveFlag = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!down_moveFlag){
                    lastY = (int) ev.getY();
                    down_moveFlag = true;
                    break;
                } else {
                    int deltY = (int) (ev.getY() - lastY);
                    lastY = (int) ev.getY();
                    if (getScrollY() == 0 && (deltY > delatY) && upFlag) {
                        if (mOverScrollCallback != null)
                            mOverScrollCallback.onOverScroll(this);
                        upFlag = false;
                    }
                    break;
                }
            case MotionEvent.ACTION_UP:
                upFlag=true;
                down_moveFlag=false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOverScrollCallback(OverScrollCallback var){
        mOverScrollCallback = var;
    }
}
