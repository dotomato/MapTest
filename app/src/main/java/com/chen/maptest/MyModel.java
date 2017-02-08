package com.chen.maptest;

import java.util.List;

/**
 * Created by chen on 17-2-7.
 * Copyright *
 */

class BaseResult{
    int statue;
    String errorMessage;
}

class PointData{
    String pointID;
    String userID;
    String message;
    double latitude;
    double longitude;
    String time;
}

class PointSimpleData{
    String pointID;
    double latitude;
    double longitude;
}

class ApiTestResult extends BaseResult{

}

class NewPointData{
    PointData pointData;
}

class NewPointResult extends BaseResult{
    PointData pointData;
}

class GetPointData{
    String pointID;
}

class GetPointResult extends BaseResult{
    PointData pointData;
}

class SelectAreaData{
    double left_top_latitude;
    double left_top_longitude;
    double right_bottom_latitude;
    double right_bottom_longitude;
    int limitStrat;
    int limitCount;
}

class SelectAreaResult extends BaseResult{
    int pointsCount;
    List<PointSimpleData> points;
}
