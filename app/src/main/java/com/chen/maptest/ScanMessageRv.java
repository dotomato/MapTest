package com.chen.maptest;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.MyModel.PointSimpleData;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyModel.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.MyView.ListViewItemData;
import com.chen.maptest.MyView.MyListViewAdapter;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;
import com.chen.maptest.Utils.UserIconWarp;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.wrapper.EmptyWrapper;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.zhy.adapter.recyclerview.wrapper.LoadMoreWrapper;

import net.steamcrafted.materialiconlib.MaterialIconView;

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

public class ScanMessageRv extends ListView {
    private Context mContext;

    private MyListViewAdapter2 mAdapter;

    private List<PointSimpleData> mDatas;
    private final String TAG = "ScanMessageRv";

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
        mAdapter = new MyListViewAdapter2(mContext,R.layout.layout_scan_item,mDatas);
        setAdapter(mAdapter);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mOnRecyclerViewItemClickListener != null) {
                        PointSimpleData psd = mDatas.get(i);
                        mOnRecyclerViewItemClickListener.onItemClickListener(view, psd);
                    }
            }
        });
        setAdapter(mAdapter);
    }

    class MyListViewAdapter2 extends ArrayAdapter<PointSimpleData> {


        private int resourceID;
        private LayoutInflater mLayoutInflater;
        private Context mContext;
        private List<PointSimpleData> inerData;

        public MyListViewAdapter2(@NonNull Context context, @LayoutRes int resource, @NonNull List<PointSimpleData> objects) {
            super(context, resource, objects);
            mContext = context;
            resourceID = resource;
            inerData = objects;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            MyListViewItem2Holder holder;
            PointSimpleData psd = inerData.get(position);
            if (convertView==null){
                convertView = mLayoutInflater.inflate(resourceID,ScanMessageRv.this,false);
                holder = new MyListViewItem2Holder();
                holder.userIcon = (ImageView)convertView.findViewById(R.id.usericon);
                holder.userName = (TextView)convertView.findViewById(R.id.username);
                holder.smallMsg = (TextView)convertView.findViewById(R.id.smallmsg);
                OutlineProvider.setOutline(convertView,OutlineProvider.SHAPE_RECT);
                OutlineProvider.setOutline(holder.userIcon,OutlineProvider.SHAPE_OVAL);
                convertView.setTag(holder);
            } else{
                holder = (MyListViewItem2Holder)convertView.getTag();
            }

            UserIconWarp.just(mContext, psd.userIcon, holder.userIcon);
            holder.userName.setText(psd.userName);
            holder.smallMsg.setText(psd.smallMsg);
            return convertView;
        }

        private class MyListViewItem2Holder {
            ImageView userIcon;
            TextView userName;
            TextView smallMsg;
        }

    }

    public void setScanData(final List<PointSimpleData> points){
        mDatas.clear();
        mDatas.addAll(points);
        mAdapter.notifyDataSetChanged();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClickListener(View View, PointSimpleData psd);
    }
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.mOnRecyclerViewItemClickListener = recyclerViewItemClickListener;
    }

}
