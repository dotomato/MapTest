package com.chen.maptest;

import android.app.Activity;
import android.content.Context;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public interface BaseView<T> {

    void setPresenter(T presenter);

    Activity getActivity();
}
