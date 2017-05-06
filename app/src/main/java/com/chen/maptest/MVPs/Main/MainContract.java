package com.chen.maptest.MVPs.Main;

import android.graphics.PointF;

import com.chen.maptest.BasePresenter;
import com.chen.maptest.BaseView;
import com.chen.maptest.MapAdapter.MyLatlng;

import java.util.Date;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

interface MainContract {
    interface View extends BaseView<Presenter> {

        void moveMap(MyLatlng center);

        void zoomMap(float zoom);

        void addMarker(MyLatlng l, String pointID, String usericon, String msgSmallText, String userID);

        void showPoint(String msgTitle, String msgText, String msgAlbum, Date time);

        void showPointUser(String username, String usericon);

        void showNewpointShine(MyLatlng l, long delay);

        void upPointShower();

        void downPointShower();

        void upPointEditer();

        void downPointEditer();

        boolean isUped();

        boolean isEditing();

        void finish();

        void replaceMsgAlbum(String fullName);

        void setUploadProgress(int progress);
    }

    interface Presenter extends BasePresenter {

        void mapMove(MyLatlng lefttop, MyLatlng rightbottom, MyLatlng center);

        void clickPoint(String pointID, String userID);

        void newPointButton(MyLatlng l);

        void retLocation();

        void reciveLocation(MyLatlng l);

        void pointLike(String pointID, boolean isLike);

        void commentList(String commentID, boolean isLike);

        void pointComment(String pointID, String content);

        void onBackPressed();

        void sendNewpoint(String msgTitle, String msgText, String msgAlbum,MyLatlng l,boolean hasAlbum);
    }
}
