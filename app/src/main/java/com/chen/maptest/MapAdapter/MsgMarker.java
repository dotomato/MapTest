package com.chen.maptest.MapAdapter;

import com.mapbox.mapboxsdk.annotations.BaseMarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.MarkerView;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class MsgMarker extends MarkerView {

    private String userIcon;
    private String userSmallTest;
    private String pointID;
    private String userID;

    public MsgMarker(BaseMarkerViewOptions baseMarkerViewOptions, String userIcon, String userSmallTest,
                     String pointID, String userID) {
        super(baseMarkerViewOptions);
        this.userIcon = userIcon;
        this.userSmallTest = userSmallTest;
        this.pointID = pointID;
        this.userID = userID;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public String getUserSmallTest() {
        return userSmallTest;
    }

    public String getPointID() {
        return pointID;
    }

    public String getUserID() {
        return userID;
    }
}
