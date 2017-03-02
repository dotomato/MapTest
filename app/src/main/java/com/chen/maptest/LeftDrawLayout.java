package com.chen.maptest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.maptest.MyView.ListViewItemData;
import com.chen.maptest.MyView.MyListViewAdapter;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.Utils.UserIconWarp;

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


    public ImageView mUsericon;
    public TextView mUserName;
    public TextView mUserDes;

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

        mUsericon = (ImageView) mHeader.findViewById(R.id.usericon);
        mUserName = (TextView)mHeader.findViewById(R.id.username);
        mUserDes = (TextView)mHeader.findViewById(R.id.userdes);

        OutlineProvider.setOutline(mHeader.findViewById(R.id.usericon),OutlineProvider.SHAPE_OVAL);

        mData.add(new ListViewItemData("私信", MainActivity.class, android.R.drawable.ic_dialog_email));
        mData.add(new ListViewItemData("回复",MainActivity.class, android.R.drawable.ic_dialog_info));
        mData.add(new ListViewItemData("我的",MainActivity.class, android.R.drawable.ic_dialog_map));
        MyListViewAdapter ia = new MyListViewAdapter(mContext,R.layout.layout_listviewitem,mData);
        setAdapter(ia);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    if (GlobalVar.mUserinfo != null) {
                        Intent intent = new Intent(mContext, UserinfoActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext,"还没有连上服务器……",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ListViewItemData id = mData.get(i-1);
                    Intent intent = new Intent(mContext, id.activity);
//                    mContext.startActivity(intent);
                    Toast.makeText(mContext,"正在建设中……",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initUserView(){
        mUserName.setText(GlobalVar.mUserinfo.userName);
        mUserDes.setText(GlobalVar.mUserinfo.userDes);
        UserIconWarp.just(mContext,GlobalVar.mUserinfo.userIcon,mUsericon);
    }
}
