package com.chen.maptest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by chen on 17-2-18.
 * Copyright *
 */

public class TopEventScrollView extends ScrollView {

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
        delatY = MyUtils.dip2px(context,20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltY = (int) (ev.getY() - lastY);
                lastY = (int) ev.getY();
                if (getScrollY() == 0 && (deltY > delatY)) {
                    if (mOverScrollCallback!=null)
                        mOverScrollCallback.onOverScroll(this);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setOverScrollCallback(OverScrollCallback var){
        mOverScrollCallback = var;
    }
}
