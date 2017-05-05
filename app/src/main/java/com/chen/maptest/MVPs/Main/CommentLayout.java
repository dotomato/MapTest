package com.chen.maptest.MVPs.Main;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.NetDataType.PointComment;
import com.chen.maptest.NetDataType.PointData;
import com.chen.maptest.NetDataType.UserComment;
import com.chen.maptest.NetDataType.UserLikeComment;
import com.chen.maptest.NetDataType.UserLikeCommentResult;
import com.chen.maptest.NetDataType.UserNewComment;
import com.chen.maptest.NetDataType.UserNewCommentResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.ComViews.OutlineProvider;
import com.chen.maptest.R;
import com.chen.maptest.Utils.ImageWrap;
import com.dd.CircularProgressButton;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @BindView(R.id.usercommentedit)
    public EditText mUserComment;

    CommonAdapter<UserComment> mAdapter;
    private List<UserComment> mDatas;
    private PointData mPointData;
    private boolean hadGetComment;
    private EmptyWrapper mEmptyWrapper;
    private HeaderAndFooterWrapper mHeaderAndFooterWarpper;

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
//        setClickable(true);

        mSendButton.setIndeterminateProgressMode(true);

        commentRv.setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.VERTICAL, false));
        commentRv.setHasFixedSize(true);

        mAdapter = new CommonAdapter<UserComment>(mContext, R.layout.main_frag_commnet_item, mDatas){
            @Override
            protected void convert(final com.zhy.adapter.recyclerview.base.ViewHolder holder,
                                   UserComment uc, int position) {
                ImageWrap.iconjust(mContext, uc.userIcon, (ImageView) holder.getView(R.id.usericon));
                OutlineProvider.setOutline(holder.getView(R.id.usericon),OutlineProvider.SHAPE_OVAL);
                holder.setText(R.id.username,uc.userName);
                holder.setText(R.id.usercomment,uc.userComment);
                DateFormat sdf = SimpleDateFormat.getDateInstance();
                holder.setText(R.id.commenttime,sdf.format(new Date(uc.commentTime*1000)));
                holder.setOnClickListener(R.id.likebutton, new OnLikeButtonClick(uc));
                ShineButton sb = holder.getView(R.id.likebutton);
                sb.setChecked(MyUM.islikecomment(uc.commentID),false);
                holder.setText(R.id.likenum,String.valueOf(uc.commentLikeNum));
            }
        };

        mEmptyWrapper = new EmptyWrapper(mAdapter);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.main_frag_comment_empty,commentRv,false);
        mEmptyWrapper.setEmptyView(emptyView);

        mHeaderAndFooterWarpper = new HeaderAndFooterWrapper(mEmptyWrapper);
        View footerView = LayoutInflater.from(mContext).inflate(R.layout.main_frag_comment_foorter,commentRv,false);
        mHeaderAndFooterWarpper.addFootView(footerView);

        commentRv.setAdapter(mHeaderAndFooterWarpper);
        commentRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mSendButton.setProgress(0);
    }

    private class OnLikeButtonClick implements View.OnClickListener {

        UserComment uc;
        OnLikeButtonClick(UserComment var){
            uc = var;
        }

        @Override
        public void onClick(View view) {
            UserLikeComment ulc = new UserLikeComment();
            ulc.commentID = uc.commentID;
            ulc.isLike = ((ShineButton) view).isChecked();
            ulc.userID = MyUM.getuid();
            ulc.userID2 = MyUM.getuid2();
            Myserver.getApi().userlikecomment(ulc)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<UserLikeCommentResult>() {
                        @Override
                        public void call() {
                            updateComment(mVar);
                        }
                    });
        }
    }

    private void updateComment(UserLikeCommentResult mVar) {
        for (UserComment uc:mDatas) {
            if (uc.commentID.equals(mVar.commentID)) {
                uc.commentLikeNum = mVar.commentLikeNum;
                if (mVar.isLike != MyUM.islikecomment(mVar.commentID))
                    MyUM.likecomment(mVar.commentID,mVar.isLike);
                mHeaderAndFooterWarpper.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.commentsendbutton)
    public void commentSendButttonClick(){
        UserNewComment unc = new UserNewComment();
        unc.pointID = mPointData.pointID;
        unc.userID = MyUM.getuid();
        unc.userID2 = MyUM.getuid2();
        unc.userComment = mUserComment.getText().toString();
        mSendButton.setProgress(50);

        Myserver.getApi().newcomment(unc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserNewCommentResult>() {
                    @Override
                    public void call() {
                        mUserComment.setText("");
                        mSendButton.setProgress(100);
                        initShow(0,mPointData);
                        initShowStub();
                        mSendButton.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSendButton.setProgress(0);
                            }
                        },2000);
                    }
                });

    }

    public void initShow(int mode, PointData pointData) {
        mPointData = pointData;
        hadGetComment = false;
        int j = mDatas.size();
        mDatas.clear();
        mHeaderAndFooterWarpper.notifyItemRangeRemoved(0,j);
        mUserComment.setText("");
    }

    public void initShowStub(){
        if (hadGetComment)
            return;
        Myserver.getApi().getpointcomment(mPointData)
                .delay(150, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<PointComment>() {
                    @Override
                    public void call() {
                        mDatas.clear();
                        mDatas.addAll(mVar.userCommentList);
                        mHeaderAndFooterWarpper.notifyItemRangeInserted(1,mDatas.size());
                        hadGetComment = true;
                    }
                });
    }
}
