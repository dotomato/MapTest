package com.chen.maptest.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;

import com.chen.maptest.GlobalVar;
import com.chen.maptest.MyModel.Userinfo;
import com.google.gson.Gson;

/**
 * Created by chen on 17-2-18.
 * Copyright *
 */

public class MyUtils {

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static String UritoFullName(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.ImageColumns.DATA};
        Cursor cur = context.getContentResolver().query(uri, proj, null, null, null);
        String fullname = null;
        if (cur == null) {
            return null;
        } else {
            int index = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cur.moveToFirst();
            fullname = cur.getString(index);
            cur.close();
        }
        return fullname;
    }

    public static void pickFromGallery(Activity context,int requesrCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        context.startActivityForResult(Intent.createChooser(intent, "选择头像"), requesrCode);
    }


    public static <T> T pojoCopy(T obj){
        Gson gson = new Gson();
        String st = gson.toJson(obj);
        return (T)gson.fromJson(st,obj.getClass());
    }

    public static void setEditTextEditable(EditText editText, boolean value){
        if(value){
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
        }else{
            editText.setFocusableInTouchMode(false);
            editText.clearFocus();
        }
    }
}
