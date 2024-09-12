package com.mapsindoorsrn.core.models;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import com.mapsindoors.core.MPRouteStopIconProvider;

public class BitmapStopIconConfig extends MPRouteStopIconProvider {

    private final Bitmap bitmap;

    public BitmapStopIconConfig(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Nullable
    @Override
    public Bitmap getImage() {
        return bitmap;
    }
}
