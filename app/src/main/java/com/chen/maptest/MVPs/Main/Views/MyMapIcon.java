package com.chen.maptest.MVPs.Main.Views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;
import com.sackcentury.shinebuttonlib.ShineButton;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by chen on 17-3-8.
 * Copyright *
 */

public class MyMapIcon {

    public static void shine_button(Context context, final ConstraintLayout viewGroup, PointF p){
        int tw = MyUtils.dip2px(context,35);

        final ShineButton shineButtonJava = new ShineButton(context);
        shineButtonJava.setBtnColor(Color.GRAY);
        shineButtonJava.setBtnFillColor(Color.rgb(255,64,129));
        shineButtonJava.setAllowRandomColor(true);
        shineButtonJava.setShapeResource(R.raw.star);
        shineButtonJava.setClickAnimDuration(1000);
        shineButtonJava.setShineTurnAngle(180);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(tw,tw);
        shineButtonJava.setLayoutParams(layoutParams);
        viewGroup.addView(shineButtonJava);
        shineButtonJava.setX(p.x-tw/2);
        shineButtonJava.setY(p.y-tw/2);
        shineButtonJava.setChecked(true,true);

        Observable.interval(500, TimeUnit.MILLISECONDS)
            .take(4)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    switch (aLong.intValue()){
                        case 0:case 1: break;
                        case 2:
                            shineButtonJava.animate().alpha(0).scaleX(0.3f).scaleY(0.3f).setDuration(300).start();
                            break;
                        case 3:
                            viewGroup.removeView(shineButtonJava);
                            break;
                    }
                }
            });
    }
}
