package com.chen.maptest.MVPs.Album;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.chen.maptest.MVPs.Main.MainActivity;
import com.chen.maptest.Manager.PremissionM;
import com.chen.maptest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.album)
    public ViewGroup mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_act);
        ButterKnife.bind(this);

        PremissionM.initPremisstion(this);
    }

    @OnClick(R.id.album)
    public void albumclick(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
