package com.chen.maptest.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ScrollView;

import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;

/**
 * Created by chen on 17-2-18.
 * Copyright *
 */

public class MyPullZoomScrollView extends ScrollView {

    private final static String TAG = "MyPullZoomScrollView";

    private static final float FRICTION = 2.0f;
    private static final float ALPHAY1 = -50f;
    private static final float ALPHAY2 = -200f;

    protected ViewGroup mZoomView;

    public void setAlphaView(View mAlphaView) {
        this.mAlphaView = mAlphaView;
    }

    protected View mAlphaView;

    private boolean isZooming = false;
    private boolean mIsBeingDragged = false;
    private boolean isZoomEnabled = true;
    private float mLastMotionY=-1;
    private float mDiffMotionY;
    private float ScrollValue=0;
    private int newScrollValue;
    private int mHeaderHeight;
    private int mTouchSlop;

    private OnPullZoomListener onPullZoomListener;
    private ScalingRunnable mScalingRunnable;

    public MyPullZoomScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public MyPullZoomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyPullZoomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mScalingRunnable = new ScalingRunnable();
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
    }

    public void setZoomView(ViewGroup zoomView){
        mZoomView = zoomView;
        mHeaderHeight = zoomView.getLayoutParams().height;
    }



    public boolean isPullToZoomEnabled() {
        return isZoomEnabled;
    }

    public boolean isZooming() {
        return isZooming;
    }

    public void setZoomEnabled(boolean isZoomEnabled) {
        this.isZoomEnabled = isZoomEnabled;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!mScalingRunnable.isFinished())
            return true;

        if (!isPullToZoomEnabled() || mZoomView==null) {
            return super.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mLastMotionY==-1)
                    mLastMotionY = event.getY();
                mDiffMotionY = event.getY() - mLastMotionY;
                mLastMotionY = event.getY();
                if (getScrollY()==0) {
                    ScrollValue = ScrollValue - mDiffMotionY / FRICTION;
                    newScrollValue = Math.round(ScrollValue);
                    isZooming = ScrollValue<0;
                    pullEvent();
                    if (isZooming)
                        return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ScrollValue = 0;
                mLastMotionY=-1;
                if (isZooming()) {
                    smoothScrollToTop();
                    if (onPullZoomListener != null) {
                        onPullZoomListener.onPullZoomEnd();
                    }
                    isZooming = false;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void pullEvent() {
        pullHeaderToZoom(newScrollValue);
        if (onPullZoomListener != null) {
            onPullZoomListener.onPullZooming(newScrollValue);
        }
    }

    private void pullHeaderToZoom(int newScrollValue) {
        ViewGroup.LayoutParams zoomLayoutParams = mZoomView.getLayoutParams();
        zoomLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;

        mZoomView.setLayoutParams(zoomLayoutParams);

        final int ALPHAY = mHeaderHeight - zoomLayoutParams.height;
        if (mAlphaView!=null) {
            final float k = 1.0f/(ALPHAY1-ALPHAY2);
            final float b = -k*ALPHAY2;
            float a = ALPHAY*k+b;
            if (a>1) a=1;
            if (a<0) a=0;
            mAlphaView.setAlpha(a);
        }
    }

    protected void smoothScrollToTop() {
        mScalingRunnable.startAnimation(200L);
    }

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float paramAnonymousFloat) {
            float f = paramAnonymousFloat - 1.0F;
            return 1.0F + f * (f * (f * (f * f)));
        }
    };

    private class ScalingRunnable implements Runnable {
        long mDuration;
        boolean mIsFinished = true;
        float mScale;
        long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams localLayoutParams;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * sInterpolator.getInterpolation(f1);
                    localLayoutParams = mZoomView.getLayoutParams();
                    if (f2 > 1.0F) {
                        localLayoutParams.height = ((int) (f2 * mHeaderHeight));
                        mZoomView.setLayoutParams(localLayoutParams);
                        post(this);
                        final int ALPHAY = mHeaderHeight - localLayoutParams.height;
                        if (mAlphaView!=null) {
                            final float k = 1.0f/(ALPHAY1-ALPHAY2);
                            final float b = -k*ALPHAY2;
                            float a = ALPHAY*k+b;
                            if (a>1) a=1;
                            if (a<0) a=0;
                            mAlphaView.setAlpha(a);
                        }
                        return;
                    }
                    mIsFinished = true;
                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = ((float) (mZoomView.getBottom()) / mHeaderHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }


    public interface OnPullZoomListener {
        void onPullZooming(int newScrollValue);

        void onPullZoomEnd();
    }

    public void setOnPullZoomListener(OnPullZoomListener onPullZoomListener) {
        this.onPullZoomListener = onPullZoomListener;
    }
}
