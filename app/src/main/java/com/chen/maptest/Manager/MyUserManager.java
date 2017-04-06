package com.chen.maptest.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.maptest.GlobalVar;
import com.chen.maptest.MyModel.Userinfo;
import com.chen.maptest.MyModel.Userinfo2;
import com.chen.maptest.MyModel.Userinfo2Result;
import com.chen.maptest.MyModel.UserinfoResult;
import com.chen.maptest.MyServer.MyAction1;
import com.chen.maptest.MyServer.Myserver;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-4-6.
 * Copyright *
 */

public class MyUserManager {

    private Context mContext;
    public MyUserManager(Context var){
        mContext = var;
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
                            editor.putString("userID", mVar.userinfo.userID);
                            editor.putString("userID2", mVar.userID2);
                            editor.apply();

                            GlobalVar.mUserinfo2 = new Userinfo2();
                            GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                            GlobalVar.mUserinfo2.userID2 = mVar.userID2;
                            if (mUserInitFinish!=null)
                                mUserInitFinish.OnUserInitFinish();
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

                            GlobalVar.mUserinfo2 = new Userinfo2();
                            GlobalVar.mUserinfo2.userinfo = mVar.userinfo;
                            GlobalVar.mUserinfo2.userID2 = pref.getString("userID2",null);
                            if (mUserInitFinish!=null)
                                mUserInitFinish.OnUserInitFinish();
                        }
                    });
        }
    }

    public void setmUserInitFinish(UserInitFinish mUserInitFinish) {
        this.mUserInitFinish = mUserInitFinish;
    }

    public interface UserInitFinish{
        void OnUserInitFinish();
    }
    private UserInitFinish mUserInitFinish=null;
}
