package com.reactlibrary.core.models;

import android.location.Location;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPPositionProvider;
import com.mapsindoors.core.MPPositionResultInterface;

public class PositionResult implements MPPositionResultInterface {
    @SerializedName("point")
    private MPPoint mPoint;
    @SerializedName("floorIndex")
    private Integer mFloorIndex;
    @SerializedName("bearing")
    private Float mBearing;
    @SerializedName("accuracy")
    private Float mAccuracy;
    @SerializedName("providerName")
    private String mProviderName;

    @Nullable
    @Override
    public MPPoint getPoint() {
        return mPoint;
    }

    @Override
    public boolean hasFloor() {
        return mFloorIndex != null;
    }

    @Override
    public int getFloorIndex() {
        if (hasFloor()) {
            return mFloorIndex;
        }else {
            return 0;
        }
    }

    @Override
    public void setFloorIndex(int i) {
        mFloorIndex = i;
    }

    @Override
    public boolean hasBearing() {
        return mBearing != null;
    }

    @Override
    public float getBearing() {
        if (mBearing != null) {
            return mBearing;
        }else {
            return 0;
        }
    }

    @Override
    public void setBearing(float v) {
        mBearing = v;
    }

    @Override
    public boolean hasAccuracy() {
        return mAccuracy != null;
    }

    @Override
    public float getAccuracy() {
        if (mAccuracy != null) {
            return mAccuracy;
        }else {
            return 0;
        }
    }

    @Override
    public void setAccuracy(float v) {
        mAccuracy = v;
    }

    @Nullable
    @Override
    public MPPositionProvider getProvider() {
        return null;
    }

    @Override
    public void setProvider(@Nullable MPPositionProvider mpPositionProvider) {
        //Unimplemented
    }

    @Nullable
    @Override
    public Location getAndroidLocation() {
        if (mPoint != null) {
            Location location = new Location(mProviderName);
            location.setBearing(mBearing);
            location.setAccuracy(mAccuracy);
            location.setLatitude(mPoint.getLat());
            location.setLongitude(mPoint.getLng());
            return location;
        }else {
            return null;
        }
    }

    @Override
    public void setAndroidLocation(@Nullable Location location) {
        //unimplemented
    }
}
