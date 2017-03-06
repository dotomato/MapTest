package com.chen.maptest.MyView;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chen.maptest.R;

/**
 * Created by chen on 17-3-6.
 * Copyright *
 */

public class InnerEdge extends ConstraintLayout {
    private Context mContext;

    private ImageView edge1;
    private ImageView edge2;
    private ImageView edge3;
    private ImageView edge4;
    public InnerEdge(Context context) {
        super(context);
        init(context);
    }

    public InnerEdge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InnerEdge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context var){
        mContext = var;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        edge1 = (ImageView)findViewById(R.id.edge1);
        edge2 = (ImageView)findViewById(R.id.edge2);
        edge3 = (ImageView)findViewById(R.id.edge3);
        edge4 = (ImageView)findViewById(R.id.edge4);

        OutlineProvider.setOutline(edge1,OutlineProvider.SHAPE_RECT);
        OutlineProvider.setOutline(edge2,OutlineProvider.SHAPE_RECT);
        OutlineProvider.setOutline(edge3,OutlineProvider.SHAPE_RECT);
        OutlineProvider.setOutline(edge4,OutlineProvider.SHAPE_RECT);
    }
}
