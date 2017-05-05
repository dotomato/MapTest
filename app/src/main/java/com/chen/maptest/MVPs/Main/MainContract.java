package com.chen.maptest.MVPs.Main;

import com.chen.maptest.BasePresenter;
import com.chen.maptest.BaseView;
import com.chen.maptest.NetDataType.PointSimpleData;
import com.chen.maptest.MapAdapter.MyLatlng;

import java.util.Date;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {

        void moveMap(MyLatlng center);

        void zoomMap(float zoom);

        void addMarker(MyLatlng l, String pointID, String usericon, String msgSmallText, String userID);

        void showPoint(String msgTitle, String msgText, String msgAlbum, Date time);

        void showPointUser(String username, String usericon);

        void upPointShower();

        void downPointShower();

        boolean isUped();

        void finish();
    }

    interface Presenter extends BasePresenter {

        void mapMove(MyLatlng lefttop, MyLatlng rightbottom, MyLatlng center);

        void clickPoint(String pointID, String userID);

        void newPoint(MyLatlng l);

        void retLocation();

        void reciveLocation(MyLatlng l);

        void pointLike(String pointID, boolean isLike);

        void commentList(String commentID, boolean isLike);

        void pointComment(String pointID, String content);

        void onBackPressed();
    }
}
