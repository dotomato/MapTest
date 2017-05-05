package com.chen.maptest.MVPs.Main;

import com.chen.maptest.BasePresenter;
import com.chen.maptest.BaseView;
import com.chen.maptest.DateType.PointData;
import com.chen.maptest.DateType.PointSimpleData;
import com.chen.maptest.DateType.Userinfo;
import com.chen.maptest.MapAdapter.MyLatlng;

import java.util.List;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {

        void moveMap(MyLatlng center);

        void zoomMap(float zoom);

        void showPoints(List<PointSimpleData> data);

        void showPoint(PointData pd);

        void upPointShower();

        void showPointUser(Userinfo ui);

        void downPointShower();

        boolean isUped();

        void finish();
    }

    interface Presenter extends BasePresenter {

        void mapMove(MyLatlng lefttop, MyLatlng rightbottom, MyLatlng center);

        void clickPoint(PointSimpleData psd);

        void newPoint(MyLatlng l);

        void retLocation();

        void reciveLocation(MyLatlng l);

        void pointLike(String pointID, boolean isLike);

        void commentList(String commentID, boolean isLike);

        void pointComment(String pointID, String content);

        void onBackPressed();
    }
}
