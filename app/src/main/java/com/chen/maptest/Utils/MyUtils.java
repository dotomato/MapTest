package com.chen.maptest.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

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


}
