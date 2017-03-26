package com.chen.maptest.MyView;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created by chen on 17-3-9.
 * Copyright *
 */

public class FixRadioConstraintLayout extends ConstraintLayout {
    public FixRadioConstraintLayout(Context context) {
        super(context);
    }

    public FixRadioConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixRadioConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int ws = MeasureSpec.getSize(widthMeasureSpec);
        int hm = MeasureSpec.getMode(heightMeasureSpec);
        int hs = ws*4/3;
        int h = MeasureSpec.makeMeasureSpec(hs,hm);
        super.onMeasure(widthMeasureSpec,h);
    }
}
