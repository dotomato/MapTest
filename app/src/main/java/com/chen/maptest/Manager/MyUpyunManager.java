package com.chen.maptest.Manager;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.upyun.library.common.Params;
import com.upyun.library.common.UploadManager;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.listener.UpProgressListener;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen on 17-2-26.
 * Copyright *
 */

public class MyUpyunManager {

    private static MyUpyunManager mMyUpyunManager=null;

    public interface UploadProgress{
        void onProgress(float progress);
        void onComplete(boolean isSuccess,String url);
    }

    public static MyUpyunManager getIns(){
        if (mMyUpyunManager==null)
            mMyUpyunManager = new MyUpyunManager();
        return mMyUpyunManager;
    }

    public MyUpyunManager(){

    }

    public MyUpyunManager upload_image(String Space, Uri imageUri,@Nullable final UploadProgress mUploadProgress) {

        String KEY = "+DJHGAFPnK18RGVkcz9pURnH+AI=";

        final String SPACE = "icon-server";
        final String savePath = "/MapTest/"+Space+"/{year}{mon}{day}/{random32}{.suffix}";

        final String HOST = "http://icon-server.b0.upaiyun.com";

        final Map<String, Object> paramsMap = new HashMap<>();
        //上传空间
        paramsMap.put(Params.BUCKET, SPACE);
        //保存路径，任选其中一个
        paramsMap.put(Params.SAVE_KEY, savePath);
//        paramsMap.put(Params.PATH, savePath);
        //可选参数（详情见api文档介绍）
//        paramsMap.put(Params.RETURN_URL, "httpbin.org/post");

        //进度回调，可为空
        UpProgressListener progressListener = new UpProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWrite, final long contentLength) {
                if (mUploadProgress!=null){
                    mUploadProgress.onProgress(bytesWrite*1.0f/contentLength);
                }
            }
        };

        //结束回调，不可为空
        UpCompleteListener completeListener = new UpCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess, String result) {
                JsonParser parse =new JsonParser();
                JsonObject json = (JsonObject) parse.parse(result);
                int code = json.get("code").getAsInt();
                String url = json.get("url").getAsString();
                if (mUploadProgress!=null){
                    if (code!=200 || !isSuccess)
                        mUploadProgress.onComplete(false,null);
                    else
                        mUploadProgress.onComplete(true,HOST+url);
                }
            }
        };
        File file = null;
        try {
            file = new File(new URI(imageUri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        UploadManager.getInstance().formUpload(file, paramsMap, KEY, completeListener, progressListener);
        return this;
    }
}
