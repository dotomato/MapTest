package com.chen.maptest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.maptest.MyView.ListViewItemData;
import com.chen.maptest.MyView.MyListViewAdapter;
import com.chen.maptest.MyView.OutlineProvider;
import com.chen.maptest.Utils.UserIconWarp;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;

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

        mUserName.getPaint().setFakeBoldText(true);
        OutlineProvider.setOutline(mHeader.findViewById(R.id.usericon),OutlineProvider.SHAPE_OVAL);


        mData.add(new ListViewItemData("私信", MainActivity.class, MaterialDrawableBuilder.IconValue.EMAIL_OUTLINE));
        mData.add(new ListViewItemData("回复",MainActivity.class, MaterialDrawableBuilder.IconValue.COMMENT_PROCESSING_OUTLINE));
        mData.add(new ListViewItemData("我的",MainActivity.class, MaterialDrawableBuilder.IconValue.ACCOUNT_OUTLINE));
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
