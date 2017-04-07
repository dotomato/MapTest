package com.chen.maptest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.R;
import com.chen.maptest.ScanMessageRv;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen on 17-4-7.
 * Copyright *
 */


public class ScanView extends FrameLayout {

    private Context mContext;

    @BindView(R.id.scan_message_layout)
    public ScanMessageRv mScanMessageRv;

    @BindView(R.id.emptylayout)
    public ViewGroup mEmptyViewGroup;


    public ScanView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ScanView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void initview(ScanMessageRv.OnRecyclerViewItemClickListener callback) {
        mScanMessageRv.initview();
        mScanMessageRv.setOnRecyclerViewItemClickListener(callback);
    }

    public void setScanData(List<PointSimpleData> points) {
        mScanMessageRv.setScanData(points);
        if (points.size()==0){
            mEmptyViewGroup.setVisibility(VISIBLE);
            mEmptyViewGroup.setAlpha(0);
            mEmptyViewGroup.animate().alpha(1).setDuration(300).start();
        } else {
            mEmptyViewGroup.setVisibility(GONE);
        }
    }
}
