package com.reactlibrary.core.models;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPPoint;

public class Bounds {
    @SerializedName("northeast")
    private MPPoint northEast;
    @SerializedName("southwest")
    private MPPoint southWest;

    public MPPoint getNorthEast() {
        return northEast;
    }

    public MPPoint getSouthWest() {
        return southWest;
    }
}
