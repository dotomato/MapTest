package com.chen.maptest.MVPs.Editpoint;

import android.content.Intent;

import java.util.List;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class EditPresenter implements EditContract.Presenter {



    private final EditContract.View mMainView;

    public EditPresenter(EditFragment MainView) {
        mMainView = MainView;
        mMainView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void onResult(int requestCode, Intent data) {

    }

    @Override
    public void commit(String msgTitle, String msgText, List<String> albumUrls) {

    }

    @Override
    public void tryexit() {
        mMainView.getActivity().finish();
    }
}
