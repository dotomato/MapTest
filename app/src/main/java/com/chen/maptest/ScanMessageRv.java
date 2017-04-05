package com.chen.maptest;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyModel.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.UserIconWarp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-4-2.
 * Copyright *
 */

public class ScanMessageRv extends RecyclerView {
    private Context mContext;

    private CommonAdapter<PointSimpleData> mAdapter;

    private AddAnimateRunnable mAddAnimateRunnable;

    private List<PointSimpleData> mDatas;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private EmptyWrapper mEmptyWrapper;

    public ScanMessageRv(Context context) {
        super(context);
        init(context);
    }

    public ScanMessageRv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context var){
        mContext=var;
        mDatas = new ArrayList<>();
    }

    public void initview(){


        setLayoutManager(new LinearLayoutManager(mContext , LinearLayoutManager.VERTICAL, false));
//        setHasFixedSize(true);

        mAdapter = new CommonAdapter<PointSimpleData>(mContext, R.layout.layout_scan_item, mDatas){
            @Override
            protected void convert(final com.zhy.adapter.recyclerview.base.ViewHolder holder,final PointSimpleData psd, int position) {
                Userinfo nuid = new Userinfo();
                nuid.userID = psd.userID;
                Myserver.getApi().getuser(nuid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MyAction1<UserinfoResult>() {
                            @Override
                            public void call() {
                                UserIconWarp.just(mContext, mVar.userinfo.userIcon, (ImageView) holder.getView(R.id.usericon));
                                holder.setText(R.id.username, mVar.userinfo.userName);
                            }
                        });

                OutlineProvider.setOutline(holder.getConvertView(),OutlineProvider.SHAPE_RECT);
                OutlineProvider.setOutline(holder.getView(R.id.usericon),OutlineProvider.SHAPE_OVAL);

                holder.setText(R.id.smallmsg,psd.smallMsg);

                holder.setOnClickListener(R.id.item_root, new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnRecyclerViewItemClickListener != null) {
                            mOnRecyclerViewItemClickListener.onItemClickListener(view, psd);
                        }
                    }
                });
            }
        };


        mEmptyWrapper = new EmptyWrapper(mAdapter);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_message,this,false);
        OutlineProvider.setOutline(emptyView,OutlineProvider.SHAPE_RECT);
        mEmptyWrapper.setEmptyView(emptyView);


//        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mEmptyWrapper);
//        View footerView = LayoutInflater.from(mContext).inflate(R.layout.layout_footer_message,this,false);
//        OutlineProvider.setOutline(footerView,OutlineProvider.SHAPE_RECT);
//        mHeaderAndFooterWrapper.addFootView(footerView);

//        LoadMoreWrapper mLoadMoreWrapper = new LoadMoreWrapper(mEmptyWrapper);
//        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
//        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener()
//        {
//            @Override
//            public void onLoadMoreRequested()
//            {
//            }
//        });

        setAdapter(mEmptyWrapper);
        mAddAnimateRunnable = new AddAnimateRunnable();

        DefaultItemAnimator da = new DefaultItemAnimator();
        da.setRemoveDuration(0);
        da.setAddDuration(350);
        setItemAnimator(da);
    }

    public void setScanData(final List<PointSimpleData> points){
        int j = mDatas.size();
        mDatas.clear();
        mEmptyWrapper.notifyItemRangeRemoved(0,j);

        Observable.interval(0,100, TimeUnit.MILLISECONDS)
                .take(points.size())
                .map(new Func1<Long, PointSimpleData>() {
                    @Override
                    public PointSimpleData call(Long aLong) {
                        return points.get(aLong.intValue());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PointSimpleData>() {
                    @Override
                    public void call(PointSimpleData psd) {
                        mDatas.add(psd);
                        mEmptyWrapper.notifyItemRangeInserted(mDatas.size(),1);
                    }
        });
    }


    public interface OnRecyclerViewItemClickListener {
        void onItemClickListener(View View, PointSimpleData psd);
    }
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.mOnRecyclerViewItemClickListener = recyclerViewItemClickListener;
    }

    private class AddAnimateRunnable implements Runnable{

        private int curItem;
        private boolean canRun;
        private List<PointSimpleData> points;

        public void setData(List<PointSimpleData> points){
            this.points = points;
            this.canRun = true;
            this.curItem= 0;
        }
        public void stop(){
            canRun=false;
            points=null;
        }

        @Override
        public void run() {
            while(curItem!=points.size() && canRun){
                mDatas.add(points.get(curItem));
                mHeaderAndFooterWrapper.notifyItemInserted(curItem);
                curItem++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
