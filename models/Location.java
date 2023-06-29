package com.reactlibrary.core.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPLocation;
import com.mapsindoors.core.MapsIndoors;

public class Location {
    @SerializedName("id")
    String id;

    @Nullable
    public MPLocation toMPLocation() {
        return MapsIndoors.getLocationById(id);
    }
}
