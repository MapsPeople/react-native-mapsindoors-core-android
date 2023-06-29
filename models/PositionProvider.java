package com.reactlibrary.core.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapsindoors.core.MPPositionProvider;
import com.mapsindoors.core.MPPositionResultInterface;
import com.mapsindoors.core.OnPositionUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class PositionProvider implements MPPositionProvider {
    private String mName;
    private List<OnPositionUpdateListener> onPositionUpdateListeners;
    private MPPositionResultInterface latest;

    public PositionProvider(String name) {
        mName = name;
        onPositionUpdateListeners = new ArrayList<>();
    }


    @Override
    public void addOnPositionUpdateListener(@NonNull OnPositionUpdateListener onPositionUpdateListener) {
        onPositionUpdateListeners.add(onPositionUpdateListener);
    }

    @Override
    public void removeOnPositionUpdateListener(@NonNull OnPositionUpdateListener onPositionUpdateListener) {
        onPositionUpdateListeners.remove(onPositionUpdateListener);
    }

    @Nullable
    @Override
    public MPPositionResultInterface getLatestPosition() {
        return latest;
    }

    public void updatePosition(@Nullable MPPositionResultInterface position) {
        latest = position;
        for (OnPositionUpdateListener onPositionUpdateListener : onPositionUpdateListeners) {
            onPositionUpdateListener.onPositionUpdate(position);
        }
    }
}
