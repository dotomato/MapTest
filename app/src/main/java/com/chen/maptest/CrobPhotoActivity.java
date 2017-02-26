package com.chen.maptest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.chen.maptest.MyView.CropImageLayout;
import com.chen.maptest.Utils.MyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrobPhotoActivity extends Activity {

    private static final String TAG = "CrobPhotoActivity";

    private final static String PFNKEY = "photofilename";
    private final static String ORKEY = "currentorientation";

    public static void start(Context context, String fullName, int currentOrientation) {
        Intent i = new Intent(context, CrobPhotoActivity.class);
        i.putExtra(PFNKEY, fullName);
        i.putExtra(ORKEY, currentOrientation);
        context.startActivity(i);
    }

    @BindView(R.id.cropimagelayout)
    CropImageLayout mCropImageLayout;

    private int mCurrentOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CrobPhotoActivity onCreate");
        setContentView(R.layout.activity_crobphoto);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String fullname = i.getStringExtra(PFNKEY);
        mCurrentOrientation = i.getIntExtra(ORKEY, CropImageLayout.ORIENTATION_UP);
//
//        Bitmap bitmap = MyUtils.getBitmapSmall(fullname);
//        mCropImageLayout.setCropImage(bitmap);
        mCropImageLayout.setCropImage(fullname);
    }


//    @OnClick(R.id.button_upload)
//    void fun1() {
//        String fullname = mCropImageLayout.getCroppedImage(mCurrentOrientation);
////        UploadActivity.start(CrobPhotoActivity.this, fullname);
//        finish();
//    }
//
//    @OnClick(R.id.button_upload_cancel)
//    void fun2() {
//        finish();
//    }
}
