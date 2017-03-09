package com.chen.maptest.MyView;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

/**
 * Created by chen on 17-2-25.
 * Copyright *
 */

public class ListViewItemData {
    public String text;
    public Class activity;
    public MaterialDrawableBuilder.IconValue imageRes;
    public ListViewItemData(String v1,Class v2,MaterialDrawableBuilder.IconValue v3){
        text=v1;
        activity=v2;
        imageRes=v3;
    }
}
