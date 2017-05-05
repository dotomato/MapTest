package com.chen.maptest.MVPs.Editpoint;

import com.chen.maptest.BasePresenter;
import com.chen.maptest.BaseView;

import java.util.List;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

interface EditContract {
    interface View extends BaseView<Presenter> {

        void addAlbum(String msgAlbumUrl);

        void setContent(String msgTitle, String msgText, List<String> albumUrls);

        String getMsgTitle();

        String getMsgText();

        List<String> getAlbumUrls();

        void setProgress(int progress);

        void finish();

    }

    interface Presenter extends BasePresenter {

        void commit(String msgTitle, String msgText, List<String> albumUrls);

        void tryexit();
    }
}
