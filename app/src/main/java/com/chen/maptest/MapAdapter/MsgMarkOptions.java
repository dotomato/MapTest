package com.chen.maptest.MapAdapter;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.annotations.BaseMarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by chen on 17-5-5.
 * Copyright *
 */

public class MsgMarkOptions extends BaseMarkerViewOptions<MsgMarker, MsgMarkOptions> {

    private String mUserIcon;
    private String mUserSmallTest;
    private String pointID;
    private String userID;

    public MsgMarkOptions(String userIcon, String userSmallTest,
                          String pointID, String userID) {
        this.mUserIcon = userIcon;
        this.mUserSmallTest = userSmallTest;
        this.pointID = pointID;
        this.userID = userID;
    }

    @Override
    public MsgMarkOptions getThis() {
        return this;
    }

    @Override
    public MsgMarker getMarker() {
        return new MsgMarker(this, mUserIcon, mUserSmallTest, pointID, userID);
    }

    public static final Parcelable.Creator<MsgMarkOptions> CREATOR
            = new Parcelable.Creator<MsgMarkOptions>() {
        public MsgMarkOptions createFromParcel(Parcel in) {
            return new MsgMarkOptions(in);
        }

        public MsgMarkOptions[] newArray(int size) {
            return new MsgMarkOptions[size];
        }
    };

    protected MsgMarkOptions(Parcel in) {
        position((LatLng) in.readParcelable(LatLng.class.getClassLoader()));
        snippet(in.readString());
        title(in.readString());
        flat(in.readByte() != 0);
        anchor(in.readFloat(), in.readFloat());
        infoWindowAnchor(in.readFloat(), in.readFloat());
        rotation(in.readFloat());
        visible(in.readByte() != 0);
        alpha(in.readFloat());
        if (in.readByte() != 0) {
            // this means we have an icon
            String iconId = in.readString();
            Bitmap iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
            Icon icon = IconFactory.recreate(iconId, iconBitmap);
            icon(icon);
        }
        mUserIcon = in.readString();
        mUserSmallTest = in.readString();
        pointID = in.readString();
        userID = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(getPosition(), flags);
        out.writeString(getSnippet());
        out.writeString(getTitle());
        out.writeByte((byte) (isFlat() ? 1 : 0));
        out.writeFloat(getAnchorU());
        out.writeFloat(getAnchorV());
        out.writeFloat(getInfoWindowAnchorU());
        out.writeFloat(getInfoWindowAnchorV());
        out.writeFloat(getRotation());
        out.writeByte((byte) (isVisible() ? 1 : 0));
        out.writeFloat(getAlpha());
        Icon icon = getIcon();
        out.writeByte((byte) (icon != null ? 1 : 0));
        if (icon != null) {
            out.writeString(getIcon().getId());
            out.writeParcelable(getIcon().getBitmap(), flags);
        }
        out.writeString(mUserIcon);
        out.writeString(mUserSmallTest);
        out.writeString(pointID);
        out.writeString(userID);
    }

}
