package com.chen.maptest.Utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class Animate {

    public static ObjectAnimator tada(View view, float shakeFactor) {

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .95f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.9f, .95f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .95f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.9f, .95f),
                Keyframe.ofFloat(1f, 1f)
        );


        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY).
                setDuration(300);
    }

    /*
      private void animateGrow(@NonNull final MarkerView marker, @NonNull final View convertView, int duration) {
        convertView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        Animator animator = AnimatorInflater.loadAnimator(convertView.getContext(), R.animator.scale_up);
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                convertView.setLayerType(View.LAYER_TYPE_NONE, null);
                mMap.selectMarker(marker);
            }
        });
        animator.setTarget(convertView);
        animator.start();
    }

     */
}
