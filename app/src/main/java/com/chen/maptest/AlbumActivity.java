package com.chen.maptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.album)
    public ViewGroup mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void spaceTouchcallback(Subscriber<? super Object> subscriber) {
//                Glide.get(AlbumActivity.this).clearDiskCache();
//                subscriber.onNext(null);
//            }
//        }).subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Action1<Object>() {
//            @Override
//            public void spaceTouchcallback(Object o) {
//                Glide.get(AlbumActivity.this).clearMemory();
//                BitmapPool bp = Glide.get(AlbumActivity.this).getBitmapPool();
//                BlurTransformation bt = new BlurTransformation(AlbumActivity.this,bp,4,16);
//                ColorFilterTransformation ct = new ColorFilterTransformation(bp, Color.argb(128,50,50,50));
//                Glide.with(AlbumActivity.this).load(R.drawable.imgtest).centerCrop().bitmapTransform(bt,ct).into(mImgtest);
//            }
//        });
    }

    @OnClick(R.id.album)
    public void albumclick(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
