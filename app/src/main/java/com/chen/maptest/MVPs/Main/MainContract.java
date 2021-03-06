package com.chen.maptest.MVPs.Main;

import android.graphics.PointF;

import com.chen.maptest.BasePresenter;
import com.chen.maptest.BaseView;
import com.chen.maptest.MapAdapter.MyLatlng;
import com.chen.maptest.NetDataType.PointData;
import com.chen.maptest.NetDataType.UserComment;
import com.chen.maptest.NetDataType.UserLikeCommentResult;

import java.util.Date;
import java.util.List;

/**
 * Created by chen on 17-5-4.
 * Copyright *
 */

interface MainContract {
    interface View extends BaseView<Presenter> {

        void moveMap(MyLatlng center, boolean delay);

        void zoomMap(float zoom);

        void addMarker(MyLatlng l, String pointID, String usericon, String msgSmallText, String userID);

        void showPoint(String msgTitle, String msgText, String msgAlbum, Date time,
                       int msgLikeNum, boolean isLike);

        void showPointUser(String username, String usericon);

        void showNewpointShine(MyLatlng l, long delay);

        void upPointShower(MyLatlng l);

        void downPointShower();

        void upPointEditer();

        void downPointEditer();

        boolean isUped();

        boolean isEditing();

        void finish();

        void replaceMsgAlbum(String fullName);

        void setUploadProgress(int progress, int visibility);

        void showComment(List<UserComment> comments, int commentNum);

        void showCommentEmpty(boolean isEmpty);

        void updateComment(UserLikeCommentResult mVar);

        void clearComment();

        void updatePoint(int pointLikeNum, boolean isLike);
    }

    interface Presenter extends BasePresenter {

        void mapMove(MyLatlng lefttop, MyLatlng rightbottom, MyLatlng center);

        void clickPoint(String pointID, String userID);

        void newPointButton(MyLatlng l);

        void retLocation();

        void reciveLocation(MyLatlng l);

        void pointLike(boolean isLike);

        void commentLike(String commentID, boolean isLike);

        void pointComment(String content);

        void onBackPressed();

        void sendNewpointButton(String msgTitle, String msgText, String msgAlbum, MyLatlng l, boolean hasAlbum);
    }
}
