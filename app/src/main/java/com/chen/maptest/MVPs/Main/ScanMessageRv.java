package com.chen.maptest.MVPs.Main;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.ComViews.OutlineProvider;
import com.chen.maptest.R;
import com.chen.maptest.Utils.UserIconWarp;

import java.util.ArrayList;
import java.util.List;

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
        mAdapter = new MyListViewAdapter2(mContext, R.layout.main_frag_scan_item,mDatas);
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
