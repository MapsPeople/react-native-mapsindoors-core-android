package com.reactlibrary.core;

import android.app.Activity;

import com.facebook.react.bridge.ReadableMap;
import com.mapsindoors.core.MPIMapConfig;
import com.mapsindoors.core.OnResultReadyListener;
import com.reactlibrary.core.models.MPCameraPosition;
import com.reactlibrary.core.models.MPCameraUpdate;

public interface RCMapView {
    void animateCamera(MPCameraUpdate cameraUpdate, Integer duration, OnResultReadyListener onFinished);
    void moveCamera(MPCameraUpdate cameraUpdate);
    MPCameraPosition getCurrentCameraPosition();
    MPIMapConfig parseConfig(ReadableMap configMap, Activity activity);
}
