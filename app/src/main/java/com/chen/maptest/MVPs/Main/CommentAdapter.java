package com.chen.maptest.MVPs.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.NetDataType.UserComment;
import com.chen.maptest.R;
import com.chen.maptest.Utils.ImageWrap;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chen on 17-5-6.
 * Copyright *
 */

final class CommentAdapter extends BaseAdapter {

    private final Context mContext;
    private MainContract.Presenter mPresenter;
    private List<UserComment> mData;

    CommentAdapter(Context context, List<UserComment> data){
        mContext = context;
        mData = data;
    }

    void setMainPresenter(MainContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_frag_commnet_item, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        UserComment uc = mData.get(position);

        ImageWrap.iconjust(mContext, uc.userIcon,holder.usericon);
        holder.username.setText(uc.userName);
        holder.usercomment.setText(uc.userComment);
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        holder.usercommenttime.setText(sdf.format(new Date(uc.commentTime*1000)));
        holder.likebutton.setChecked(MyUM.islikecomment(uc.commentID),false);
        holder.likenum.setText(String.valueOf(uc.commentLikeNum));

        holder.likebutton.setOnClickListener(new OnLikeButtonClick(uc, holder.likebutton));

        return convertView;
    }

    private class Holder{
        ImageView usericon;
        TextView usercomment;
        TextView username;
        TextView usercommenttime;
        ShineButton likebutton;
        TextView likenum;

        Holder(View view){
            usericon = (ImageView)view.findViewById(R.id.usericon);
            usercomment = (TextView)view.findViewById(R.id.usercomment);
            username = (TextView)view.findViewById(R.id.username);
            usercommenttime = (TextView)view.findViewById(R.id.commenttime);
            likebutton = (ShineButton)view.findViewById(R.id.likebutton);
            likenum = (TextView)view.findViewById(R.id.likenum);
        }
    }


    private class OnLikeButtonClick implements ShineButton.OnClickListener {

        UserComment uc;
        ShineButton sb;
        OnLikeButtonClick(UserComment var, ShineButton var2){ uc = var; sb = var2;}

        @Override
        public void onClick(View v) {
            mPresenter.commentLike(uc.commentID,  !MyUM.islikecomment(uc.commentID));
        }
    }
}
