package com.chen.maptest.MVPs.Editpoint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class EditActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private EditFragment mEditFragment;
    private EditPresenter mEditPresenter;
    private Toolbar mToolbar;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_act);

        mEditFragment = (EditFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mEditFragment == null) {
            mEditFragment = new EditFragment();
            MyUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mEditFragment, R.id.contentFrame);
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        Drawable drawable = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.ARROW_LEFT)
                .setColor(Color.WHITE)
                .setSizeDp(25)
                .build();
        mToolbar.setNavigationIcon(drawable);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditPresenter.tryexit();
            }
        });

        mToolbar.setOnMenuItemClickListener(this);

        // Create the presenter
        mEditPresenter = new EditPresenter(mEditFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialMenuInflater
                .with(this)
                .setDefaultColor(Color.WHITE)
                .inflate(R.menu.main_toolbar_menu, menu);
        mMenu = menu;
        return true;
    }


    @Override
    public void onBackPressed() {
        mEditPresenter.tryexit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode){
            case EditFragment.ALBUMREQ:
                if (data != null) {
                    Uri imageUri = data.getData();
                    mEditFragment.addAlbum(MyUtils.UritoFullName(this, imageUri));
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.complete:
                mEditFragment.commit();
                break;
        }
        return true;
    }
}
