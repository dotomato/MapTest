package com.chen.maptest.MyServer;

import android.util.Log;

import com.chen.maptest.MyModel.*;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chen on 17-2-5.
 * Copyright *
 */

public class Myserver {

    final static private String TAG = "Myserver";

//    final static private String BASEURL = "www.dotomato.win";
    final static private String BASEURL = "http://192.168.1.106:5001";
//    final static private String BASEURL = "https://cj.1994.io";
    final static private String VERSION = "/api/v0.01";

    public interface MyserverInterface{

        @GET(VERSION+"/apitest")
        Observable<ApiTestResult> apitest(@Query("q") String var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/newpoint")
        Observable<NewPointResult> newPoint(@Body NewPointData var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/selectarea")
        Observable<SelectAreaResult> selectArea(@Body SelectAreaData var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/getpoint")
        Observable<GetPointResult> getPoint(@Body GetPointData var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/newuser")
        Observable<UserIDResult> newuser(@Body UserID var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/getuser")
        Observable<UserIDResult> getuser(@Body UserID var);

        @Headers({"Content-Type: application/json","Accept: application/json"})
        @POST(VERSION+"/updateuser")
        Observable<UserIDResult> updateuser(@Body Userinfo var);
    }


    private static MyserverInterface mServer=null;

    public static MyserverInterface getApi(){
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

    public static void apiTest(){
        getApi().apitest("api test message!")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyAction1<ApiTestResult>() {
                    @Override
                    public void call() {
                        Log.d(TAG, "api test result: " + mVar.statue);
                    }
                });
    }
}

