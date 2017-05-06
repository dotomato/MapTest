package com.chen.maptest.MVPs.Main.Views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.chen.maptest.R;

/**
 * Created by chen on 17-5-7.
 * Copyright *
 */

public class CommentFooterView extends LinearLayout {

    public CommentFooterView(Context context) {
        super(context);
    }

    public CommentFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEmpty(boolean empty){
        View emptyview = findViewById(R.id.commentEmptyView);
        if (empty){
            emptyview.setVisibility(VISIBLE);
        } else {
            emptyview.setVisibility(INVISIBLE);
        }
    }
}
