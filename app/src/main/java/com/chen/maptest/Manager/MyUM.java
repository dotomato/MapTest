package com.chen.maptest.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.maptest.GlobalVar;
import com.chen.maptest.MyModel.UserLikeCommentIDListResult;
import com.chen.maptest.MyModel.UserLikePointIDListResult;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyModel.Userinfo2;
import com.chen.maptest.MyModel.Userinfo2List;
import com.chen.maptest.MyModel.Userinfo2Result;
import com.chen.maptest.MyModel.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-4-6.
 * Copyright *
 */

public class MyUM {

    private Context mContext;
    public MyUM(Context var){
        mContext = var;
        uiinited = false;
        ulinited = 0;
    }

    private static boolean uiinited;
    private static int ulinited;

    public static boolean isinited(){
        return uiinited && (ulinited == 2);
    }

    public void inituserinfo(){
        SharedPreferences pref = mContext.getSharedPreferences("data",mContext.MODE_PRIVATE);
        String userID = pref.getString("userID",null);
        if (userID==null) {
            Userinfo ui = new Userinfo();
            ui.userDes="please give me a new ID!";
            Myserver.getApi().newuser(ui)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<Userinfo2Result>() {
                        @Override
                        public void call() {
                            SharedPreferences pref = mContext.getSharedPreferences("data",mContext.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userID", mVar.userinfo2.userinfo.userID);
                            editor.putString("userID2", mVar.userinfo2.userID2);
                            editor.apply();

                            GlobalVar.mUserd = new Userinfo2List();
                            GlobalVar.mUserd.ui2 = mVar.userinfo2;
                            GlobalVar.mUserd.userLikeCommentIDList = mVar.userLikeCommentIDList;
                            GlobalVar.mUserd.userLikePointIDList = mVar.userLikePointIDList;

                            uiinited = true;
                            initlist();
                        }
                    });
        } else {
            Userinfo ui = new Userinfo();
            ui.userID=userID;
            Myserver.getApi().getuser(ui)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyAction1<UserinfoResult>() {
                        @Override
                        public void call() {
                            SharedPreferences pref = mContext.getSharedPreferences("data",mContext.MODE_PRIVATE);

                            GlobalVar.mUserd = new Userinfo2List();
                            GlobalVar.mUserd.ui2 = new Userinfo2();
                            GlobalVar.mUserd.ui2.userinfo = mVar.userinfo;
                            GlobalVar.mUserd.ui2.userID2 = pref.getString("userID2",null);

                            uiinited = true;
                            initlist();
                        }
                    });

        }
    }

    private void initlist(){
        Myserver.getApi().getuserlikecommentidlist(GlobalVar.mUserd.ui2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserLikeCommentIDListResult>() {
                    @Override
                    public void call() {
                        GlobalVar.mUserd.userLikeCommentIDList = mVar.userLikeCommentIDList;
                        ulinited ++;
                        if (mUserdInitFinish!=null&& isinited())
                            mUserdInitFinish.OnUserInitFinish();
                    }
                });
        Myserver.getApi().getuserlikepointidlist(GlobalVar.mUserd.ui2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<UserLikePointIDListResult>() {
                    @Override
                    public void call() {
                        GlobalVar.mUserd.userLikePointIDList = mVar.userLikePointIDList;
                        ulinited ++;
                        if (mUserdInitFinish!=null&& isinited())
                            mUserdInitFinish.OnUserInitFinish();
                    }
                });
    }

    public void setmUserdInitFinish(UserInitFinish mUserdInitFinish) {
        this.mUserdInitFinish = mUserdInitFinish;
    }

    public interface UserInitFinish{
        void OnUserInitFinish();
    }
    private UserInitFinish mUserdInitFinish=null;
    
    public static Userinfo2 getui2(){
        return GlobalVar.mUserd.ui2;
    }
    
    public static void setui2(Userinfo2 ui2){
        GlobalVar.mUserd.ui2 = ui2;
    }
    
    public static Userinfo getui(){
        return GlobalVar.mUserd.ui2.userinfo;
    }
    
    public static void setui(Userinfo ui){
        GlobalVar.mUserd.ui2.userinfo = ui;
    }
    
    public static String getuid(){
        return GlobalVar.mUserd.ui2.userinfo.userID;
    }
    
    public static void setuid(String uid){
        GlobalVar.mUserd.ui2.userinfo.userID = uid;
    }

    public static String getuid2(){
        return GlobalVar.mUserd.ui2.userID2;
    }

    public static void setuid2(String uid2){
        GlobalVar.mUserd.ui2.userID2 = uid2;
    }

    public static boolean islikepoint(String pointID){
        return GlobalVar.mUserd.userLikePointIDList.contains(pointID);
    }

    public static boolean islikecomment(String commentID){
        return GlobalVar.mUserd.userLikeCommentIDList.contains(commentID);
    }

    public static void likecomment(String commentID, boolean islike){
        if (islike)
            GlobalVar.mUserd.userLikeCommentIDList.add(commentID);
        else
            GlobalVar.mUserd.userLikeCommentIDList.remove(commentID);
    }

    public static void likepoint(String pointID, boolean islike){
        if (islike)
            GlobalVar.mUserd.userLikePointIDList.add(pointID);
        else
            GlobalVar.mUserd.userLikePointIDList.remove(pointID);
    }
}
