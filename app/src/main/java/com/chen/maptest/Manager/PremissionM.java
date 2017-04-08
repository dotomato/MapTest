package com.chen.maptest.Manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by chen on 17-4-8.
 * Copyright *
 */

public class PremissionM {
    public final static int WRITE_COARSE_LOCATION_REQUEST_CODE = 0;

    public static void initPremisstion(Activity var) {
        if (ContextCompat.checkSelfPermission(var, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(var, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(var, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(var, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(var, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(var, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_COARSE_LOCATION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(var, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(var, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        WRITE_COARSE_LOCATION_REQUEST_CODE);
            }
        }
    }
}
