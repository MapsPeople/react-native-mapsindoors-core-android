package com.reactlibrary.core.models;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPPoint;

import javax.annotation.Nullable;

public class MPCameraPosition {
    @SerializedName("zoom")
    @Nullable
    public
    Float zoom;
    @SerializedName("tilt")
    @Nullable
    public
    Float tilt;
    @SerializedName("bearing")
    @Nullable
    public
    Float bearing;
    @SerializedName("target")
    @NonNull
    public
    MPPoint target;

    public MPCameraPosition(float zoom, float tilt, float bearing, MPPoint target) {
        this.zoom = zoom;
        this.tilt = tilt;
        this.bearing = bearing;
        this.target = target;
    }

}
