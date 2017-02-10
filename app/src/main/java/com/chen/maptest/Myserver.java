package com.chen.maptest;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
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
    final static private String BASEURL = "http://192.168.1.106:5001";
    final static private String VERSION = "/api/v0.01";

    interface MyserverInterface{

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

    }




    private static MyserverInterface mServer=null;

    static MyserverInterface getApi(){
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
        getApi().apitest("api test message!")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ApiTestResult>() {
                    @Override
                    public void call(ApiTestResult var) {
                        Log.d(TAG, "api test result: " + var.statue);
                    }
                });
    }
}

//一个封装好了服务器已知错误和意外错误的Action1
abstract class MyAction1<T> implements Observer<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public final void onError(Throwable e) {
        e.printStackTrace();
        error(-101, "服务器发生未知错误，或者是转换返回数据体时出错");
    }

    @Override
    public final void onNext(T var){
        BaseResult var2 = (BaseResult) var;
        if (var2.statue != 100) {
            error(var2.statue, var2.errorMessage);
        } else {
            mVar = var;
            call();
        }
    }

    T mVar;

    void call(){   }

    void error(int statue, String errorMessage){
        Log.e("MyAction1","statue="+statue+" errorMessage="+errorMessage);
    }
}