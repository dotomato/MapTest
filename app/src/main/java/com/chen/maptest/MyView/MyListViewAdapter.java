package com.chen.maptest.MyView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.maptest.R;

import java.util.List;

/**
 * Created by chen on 17-2-25.
 * Copyright *
 */

public class MyListViewAdapter extends ArrayAdapter<ListViewItemData>{


    private int resourceID;
    private LayoutInflater mLayoutInflater;
    private List<ListViewItemData> mData;

    public MyListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListViewItemData> objects) {
        super(context, resource, objects);
        resourceID = resource;
        mLayoutInflater = LayoutInflater.from(context);
        mData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MyListViewItemHolder holder;
        if (convertView==null){
            convertView = mLayoutInflater.inflate(resourceID,null);
            holder = new MyListViewItemHolder();
            holder.mIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.mTextView = (TextView)convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else{
            holder = (MyListViewItemHolder)convertView.getTag();
        }
        ListViewItemData data= mData.get(position);
        holder.mTextView.setText(data.text);
        holder.mIcon.setImageResource(data.imageRes);
        return convertView;
    }

    private class MyListViewItemHolder {
        ImageView mIcon;
        TextView mTextView;
    }

}
