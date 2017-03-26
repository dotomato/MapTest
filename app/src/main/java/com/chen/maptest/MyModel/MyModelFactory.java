package com.chen.maptest.MyModel;

/**
 * Created by chen on 17-2-7.
 * Copyright *
 */

public class MyModelFactory{
    public static PointData getEditDeafultPointData(){
        PointData result = new PointData();
        result.userID = "开发客户端v0.01";
        return result;
    }
}