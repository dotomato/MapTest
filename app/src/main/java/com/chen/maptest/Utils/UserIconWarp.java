package com.chen.maptest.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chen.maptest.R;

/**
 * Created by chen on 17-3-2.
 * Copyright *一
 */

public class UserIconWarp {

    public static void just(Context context, String path, ImageView imageview){
        if (path.equals("no_icon")) {
            Glide.with(context).load(R.drawable.usericon).asBitmap().into(imageview);
        } else {
            Glide.with(context).load(path).asBitmap().into(imageview);
        }
    }
}
