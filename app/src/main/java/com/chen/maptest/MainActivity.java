package com.chen.maptest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener{


    private final static String TAG = "MainActivity";


    @BindView(R.id.leftDrawer)
    public LeftDrawLayout mLeftDrawerLayout;

    @BindView(R.id.activity_main)
    public DrawerLayout mRootView;

    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(this, getString(R.string.MapBox_access_token));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRootView.addDrawerListener(this);

        mMainFragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, mMainFragment);
        transaction.commit();
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
        super.onActivityResult(requestCode, resultCode, data);
        mMainFragment.onActivityResult(requestCode, resultCode, data);
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

}
