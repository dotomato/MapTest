package com.chen.maptest.MVPs.Editpoint;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chen.maptest.Utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class AlbumGridViewAdapter extends BaseAdapter
{
    // 定义Context
    private Context mContext;
    private List<String> data;
    private int imageSize;
    // 定义整型数组 即图片源

    public AlbumGridViewAdapter(Context c)
    {
        mContext = c;
        data = new ArrayList<>();
        imageSize = MyUtils.dip2px(mContext,80);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(imageSize, imageSize));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        else
        {
            imageView = (ImageView) convertView;
        }

        Glide.with(mContext).load(data.get(position)).into(imageView);
        return imageView;
    }

    public void setData(List<String> data){
        this.data.clear();
        this.data.addAll(data);
    }

    public void addData(String url){
        this.data.add(url);
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }
}