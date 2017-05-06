package com.chen.maptest.MVPs.Main.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chen.maptest.NetDataType.UserComment;

import java.util.List;

/**
 * Created by chen on 17-5-6.
 * Copyright *
 */

public class CommentAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<UserComment> data;

    CommentAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
