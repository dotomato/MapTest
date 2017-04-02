package com.chen.maptest;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.MyView.OutlineProvider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chen on 17-4-2.
 * Copyright *
 */

public class CommentLayout extends ConstraintLayout {

    private Context mContext;

    @BindView(R.id.recyclerview)
    public RecyclerView commentRv;

    CommonAdapter<PointSimpleData> mAdapter;
    List<PointSimpleData> mDatas;

    public CommentLayout(Context context) {
        super(context);
        init(context);
    }

    public CommentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context var){
        mContext = var;
        mDatas = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);
        initview();

    }

    private void initview(){
        commentRv.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.VERTICAL, false));
        commentRv.setHasFixedSize(true);

        mAdapter = new CommonAdapter<PointSimpleData>(mContext, R.layout.layout_scan_item, mDatas){
            @Override
            protected void convert(final com.zhy.adapter.recyclerview.base.ViewHolder holder,final PointSimpleData psd, int position) {

            }
        };

        EmptyWrapper mEmptyWrapper = new EmptyWrapper(mAdapter);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_message,commentRv,false);
        OutlineProvider.setOutline(emptyView,OutlineProvider.SHAPE_RECT);
        mEmptyWrapper.setEmptyView(emptyView);

        commentRv.setAdapter(mEmptyWrapper);
        commentRv.setItemAnimator(new DefaultItemAnimator());
    }
}
