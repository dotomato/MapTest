package com.chen.maptest.MyView;

import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by chen on 17-2-25.
 * Copyright *
 */

public class OutlineProvider {

    public static final int SHAPE_RECT = 0;
    public static final int SHAPE_OVAL = 1;

    public static void setOutline(View view,int shape){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (shape) {
                case SHAPE_RECT:
                    view.setOutlineProvider(getRectOutline());
                    break;
                case SHAPE_OVAL:
                    view.setOutlineProvider(getOvalOutline());
                    break;
            }
        }
    }


    private static ViewOutlineProvider getRectOutline() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outline.setRect(0, 0, view.getWidth(), view.getHeight());
                    }
                }
            };
        }
        return null;
    }

    private static ViewOutlineProvider getOvalOutline() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        outline.setOval(0, 0, view.getWidth(), view.getHeight());
                    }
                }
            };
        }
        return null;
    }


}
