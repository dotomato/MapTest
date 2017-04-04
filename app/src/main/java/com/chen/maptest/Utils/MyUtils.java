package com.chen.maptest.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;

import com.google.gson.Gson;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static void pickFromGallery(Activity context, int requesrCode, String title) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        context.startActivityForResult(Intent.createChooser(intent, title), requesrCode);
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

    public static Bitmap getBitmapSmall(String localImagePath, int maxNumOfPixels) {

        Bitmap temBitmap = null;

        try {
            BitmapFactory.Options outOptions = new BitmapFactory.Options();

            // 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
            outOptions.inJustDecodeBounds = true;

            // 加载获取图片的宽高
            BitmapFactory.decodeFile(localImagePath, outOptions);

            outOptions.inSampleSize = computeSampleSize(outOptions, -1, maxNumOfPixels);
            ;
            // 重新设置该属性为false，加载图片返回
            outOptions.inJustDecodeBounds = false;
            temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return temBitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    static public void saveBitmap(File file, Bitmap bm) {
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    // 将 s 进行 BASE64 编码
//    public static String getBASE64(String s) {
//        if (s == null) return null;
//        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
//    }
//
//    // 将 BASE64 编码的字符串 s 进行解码
//    public static String getFromBASE64(String s) {
//        if (s == null) return null;
//        return new String(Base64.decode(s.getBytes(),Base64.DEFAULT));
//    }
}
