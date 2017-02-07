package com.chen.maptest;

import android.util.Log;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-2-5.
 * Copyright *
 */

class Myserver {

    final static private String TAG = "Myserver";

//    final static private String BASEURL = "www.dotomato.win";
    final static private String BASEURL = "http://10.0.2.2:5000";
    final static private String VERSION = "/api/v0.01";

    interface MyserverInterface{

        @GET(VERSION+"/pointlist")
        Observable<String> getPointList(@Query("location") String location);

        @GET(VERSION+"/apitest")
        Observable<ApiTestResult> apitest(@Query("q") String var);

    }

    public class ApiTestResult{
        String statue;
        List<String> data;
    }

    private static MyserverInterface mServer=null;

    static MyserverInterface getServer(){
        if (mServer==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            mServer = retrofit.create(MyserverInterface.class);
        }
        return mServer;
    }

    static void apiTest(){
        getServer().apitest("api test message!")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiTestResult>() {
                    @Override
                    public void call(ApiTestResult var) {
                        Log.d(TAG, "api test result: " + var.statue +" "+var.data.get(1));
                    }
                });
    }
}
