package com.chen.maptest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen on 17-2-18.
 * Copyright *
 */



public class UserEditLayout extends TopEventScrollView{



    private final static String TAG = "UserEditLayout";

    @BindView(R.id.usericon)
    public ImageView mUserIcon;

    @BindView(R.id.username)
    public TextView mUserName;

    @BindView(R.id.userdescript)
    public TextView mUserDescirpt;

    @BindView(R.id.usermessage)
    public TextView mUserMessage;

    @BindView(R.id.space)
    public Space mSpace;


    private Context mContext;

    public UserEditLayout(Context context) {
        super(context);
        init(context);
    }

    public UserEditLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UserEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }


    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if (ev.getY()<mSpace.getHeight()-getScrollY() ) {
            if (mSpaceTouchEventCallback != null)
                mSpaceTouchEventCallback.onSpaceTouchEvent(ev);
            return true;
        }
        return super.onTouchEvent(ev);
    }

    SpaceTouchEventCallback mSpaceTouchEventCallback=null;
    interface SpaceTouchEventCallback{
        void onSpaceTouchEvent(MotionEvent ev);
    }
    public void setSpaceTouchEventCallback(SpaceTouchEventCallback var){
        mSpaceTouchEventCallback=var;
    }

    public int getSpaceHeight(){
        return mSpace.getHeight();
    }
}
