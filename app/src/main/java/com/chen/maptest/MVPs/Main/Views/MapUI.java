package com.chen.maptest.MVPs.Main.Views;

import android.content.Context;
import android.graphics.PointF;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created by chen on 17-4-9.
 * Copyright *
 */

public class MapUI extends ConstraintLayout {
    private Context mContext;

    public MapUI(Context context) {
        super(context);
        init(context);
    }

    public MapUI(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapUI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
    }

    public void setCenter(PointF p){
        if (p==null)
            return;
        int w = this.getWidth();
        int h = this.getHeight();
        setX(p.x-w/2);
        setY(p.y-h/2);
    }
}
