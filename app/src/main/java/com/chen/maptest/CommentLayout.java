package com.chen.maptest;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.chen.maptest.MyModel.PointData;
import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.MyModel.UserNewComment;
import com.chen.maptest.MyView.OutlineProvider;
import com.dd.CircularProgressButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chen on 17-4-2.
 * Copyright *
 */

public class CommentLayout extends ConstraintLayout {

    private Context mContext;

    @BindView(R.id.recyclerview)
    public RecyclerView commentRv;

    @BindView(R.id.commentsendbutton)
    public CircularProgressButton mSendButton;

    CommonAdapter<PointSimpleData> mAdapter;
    List<PointSimpleData> mDatas;
    private PointData mPointData;

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

    @Override
    protected void onLayout(boolean changed,
                            int l, int t, int r, int b){
        super.onLayout(changed,l,t,r,b);
    }

    private void initview(){
        setClickable(true);

        commentRv.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.VERTICAL, false));
        commentRv.setHasFixedSize(true);

        mAdapter = new CommonAdapter<PointSimpleData>(mContext, R.layout.layout_scan_item, mDatas){
            @Override
            protected void convert(final com.zhy.adapter.recyclerview.base.ViewHolder holder,final PointSimpleData psd, int position) {

            }
        };

        EmptyWrapper mEmptyWrapper = new EmptyWrapper(mAdapter);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_comment,commentRv,false);
        OutlineProvider.setOutline(emptyView,OutlineProvider.SHAPE_RECT);
        mEmptyWrapper.setEmptyView(emptyView);

        commentRv.setAdapter(mEmptyWrapper);
        commentRv.setItemAnimator(new DefaultItemAnimator());

        mSendButton.setProgress(0);
    }

    @OnClick(R.id.commentsendbutton)
    public void commentSendButttonClick(){
        UserNewComment unc = new UserNewComment();
        unc.pointID = mPointData.pointID;
        unc.userID = GlobalVar.mUserinfo2.userinfo.userID;
        unc.userID2 = GlobalVar.mUserinfo2.userID2;
    }

    public void initShow(int mode, PointData pointData) {
        mPointData = pointData;
    }
}
