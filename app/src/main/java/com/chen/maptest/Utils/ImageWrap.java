package com.chen.maptest.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chen.maptest.R;

/**
 * Created by chen on 17-3-2.
 * Copyright *一
 */

public class ImageWrap {

    public static void iconjust(Context context, String path, ImageView imageview){
        if (path.equals("no_icon")) {
            Glide.with(context).load(R.drawable.usericon).asBitmap().into(imageview);
        } else {
            Glide.with(context).load(path).asBitmap().into(imageview);
        }
    }

    public static void albumjust(Context context, String path, ImageView imageview){
        if (path.equals("no_img")) {
            Glide.with(context).load(R.drawable.default_album).asBitmap().into(imageview);
        } else {
            Glide.with(context).load(path).asBitmap().into(imageview);
        }
    }
}
