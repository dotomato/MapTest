package com.chen.maptest.MVPs.Main;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chen.maptest.MVPs.MainLeftDraw.LeftDrawLayout;
import com.chen.maptest.Manager.MyUM;
import com.chen.maptest.MyServer.Myserver;
import com.chen.maptest.R;
import com.chen.maptest.Utils.MyUtils;
import com.mapbox.mapboxsdk.Mapbox;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener, Toolbar.OnMenuItemClickListener {


    private final static String TAG = "MainActivity";


    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    @BindView(R.id.activity_main)
    public DrawerLayout mRootView;

    private MainFragment mMainFragment;
    private MainPresenter mMainPresenter;
    private Toolbar mToolbar;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.MapBox_access_token));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        ButterKnife.bind(this);
        mRootView.addDrawerListener(this);

        Myserver.apiTest();

        MyUM.inituserinfo(this,new MyUM.UserInitFinish() {
            @Override
            public void OnUserInitFinish() {
                initUserView();
            }
        });



        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setVisibility(View.GONE);

        mToolbar.setOnMenuItemClickListener(this);

        mMainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (mMainFragment == null) {
            // Create the fragment
            mMainFragment = new MainFragment();
            MyUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mMainFragment, R.id.contentFrame);
        }

        // Create the presenter
        mMainPresenter = new MainPresenter(mMainFragment);
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
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.activity_main);
        if(dl.isDrawerOpen(mLeftDrawerLayout))
            dl.closeDrawer(mLeftDrawerLayout);
        else
            mMainFragment.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        mMainPresenter.onResult(requestCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void initUserView(){
        mLeftDrawerLayout.initUserView();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.complete:
                mMainFragment.sendNewpoint();
                break;
        }
        return true;
    }
}
