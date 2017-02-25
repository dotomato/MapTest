package com.chen.maptest;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.chen.maptest.MyView.ListViewItemData;
import com.chen.maptest.MyView.MyListViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by chen on 17-2-25.
 * Copyright *
 */

public class LeftDrawLayout extends ListView {

    private Context mContext;

    private ArrayList<ListViewItemData> mData = new ArrayList<>();

    private View mHeader;

    public LeftDrawLayout(Context context) {
        super(context);
        init(context);
    }

    public LeftDrawLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LeftDrawLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }


    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.bind(this);
        listviewInit();
    }

    private void listviewInit(){
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_listview_header,this,false);
        addHeaderView(mHeader);

        mData.add(new ListViewItemData("设置",MainActivity.class));
        mData.add(new ListViewItemData("设置2",MainActivity.class));
        mData.add(new ListViewItemData("设置3",MainActivity.class));
        MyListViewAdapter ia = new MyListViewAdapter(mContext,R.layout.layout_listviewitem,mData);
        setAdapter(ia);
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItemData id = mData.get(i);
                Intent intent = new Intent(mContext,id.activity);
                mContext.startActivity(intent);
            }
        });
    }


}
