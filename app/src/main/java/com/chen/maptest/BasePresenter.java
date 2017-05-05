package com.chen.maptest;

import android.content.Intent;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public interface BasePresenter {

    void start();

    void destroy();

    void onResult(int requestCode, Intent data);
}
