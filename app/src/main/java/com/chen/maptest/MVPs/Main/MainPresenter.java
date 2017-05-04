package com.chen.maptest.MVPs.Main;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View mMainView;

    public MainPresenter(MainContract.View mainView) {
        mMainView = mainView;
        mMainView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
