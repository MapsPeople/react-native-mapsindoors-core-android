package com.mapsindoorsrn.core.models;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPRouteStopIconConfig;

public class RouteStopIconConfigModel {
    @Nullable
    @SerializedName("numbered")
    private Boolean numbered;
    @Nullable
    @SerializedName("color")
    private String color;
    @Nullable
    @SerializedName("label")
    private String label;

    public MPRouteStopIconConfig toMPRouteStopIconConfig(Context context) {
        MPRouteStopIconConfig.Builder builder = new MPRouteStopIconConfig.Builder(context);
        if (numbered != null) {
            builder.setNumbered(numbered);
        }
        if (color != null) {
            builder.setColor(Color.parseColor(color));
        }
        if (label != null) {
            builder.setLabel(label);
        }
        return builder.build();
    }
}
