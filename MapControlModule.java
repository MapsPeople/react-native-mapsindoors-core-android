package com.reactlibrary.core;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapsindoors.core.MPBuilding;
import com.mapsindoors.core.MPFilter;
import com.mapsindoors.core.MPFilterBehavior;
import com.mapsindoors.core.MPFloor;
import com.mapsindoors.core.MPFloorSelectorInterface;
import com.mapsindoors.core.MPIMapConfig;
import com.mapsindoors.core.MPLocation;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPSelectionBehavior;
import com.mapsindoors.core.MPVenue;
import com.mapsindoors.core.MapControl;
import com.mapsindoors.core.MapsIndoors;
import com.mapsindoors.core.OnFloorSelectionChangedListener;
import com.mapsindoors.core.OnFloorUpdateListener;
import com.mapsindoors.core.OnLiveLocationUpdateListener;
import com.mapsindoors.core.OnMapControlReadyListener;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.models.MPCameraEvent;
import com.mapsindoors.core.models.MPCameraEventListener;
import com.mapsindoors.core.models.MPMapStyle;
import com.reactlibrary.core.models.Filter;
import com.reactlibrary.core.models.Location;
import com.reactlibrary.core.models.MPCameraUpdate;
import com.reactlibrary.core.models.MPError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapControlModule extends ReactContextBaseJavaModule implements MPCameraEventListener, OnFloorUpdateListener, MPFloorSelectorInterface {
    private RCMapView mMapView;
    private final Gson gson = new Gson();
    private MapControl mMapControl;
    private OnMapControlReadyListener mcListener;
    private final ReactApplicationContext mCtx;

    public MapControlModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mCtx = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "MapControlModule";
    }

    private void reject(Promise promise, MIError error) {
        promise.reject("MapControlError", gson.toJson(MPError.fromMIError(error)));
    }

    public void setOnMapControlReadyListener(OnMapControlReadyListener listener) {
       mcListener = listener;
    }

    private void emit(Listener listener, Object data) {
        mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(listener.method, data);
    }

    public void setView(RCMapView view) {
        mMapView = view;
    }

    @ReactMethod
    public void initMapControl(ReadableMap config, final Promise promise) {
        getCurrentActivity().runOnUiThread(()-> {
            MPIMapConfig mpMapConfig = mMapView.parseConfig(config, getCurrentActivity());
            MapControl.create(mpMapConfig, (mapControl, miError) -> {
                mcListener.onMapControlReady(mapControl,miError);
                if (mapControl != null) {
                    mMapControl = mapControl;
                    promise.resolve(null);
                } else {
                    reject(promise, miError);
                }
            });
        });
    }

    @ReactMethod
    public void setFilterWithLocations(String locationsString, String filterBehaviorString, final Promise promise) {
        ArrayList<String> locationIds = gson.fromJson(locationsString, new TypeToken<List<String>>() {}.getType());
        List<MPLocation> locations = new ArrayList<>();
        for (String locationId : locationIds) {
            locations.add(MapsIndoors.getLocationById(locationId));
        }
        MPFilterBehavior filterBehavior = gson.fromJson(filterBehaviorString, MPFilterBehavior.class);
        if (mMapControl != null) {
            mMapControl.setFilter(locations, filterBehavior);
            promise.resolve(true);
        }else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void setFilter(String filterString, String filterBehaviorString, final Promise promise) {
        MPFilter filter = gson.fromJson(filterString, Filter.class).toMPFilter();
        MPFilterBehavior filterBehavior = gson.fromJson(filterBehaviorString, MPFilterBehavior.class);
        if (mMapControl != null && filter != null) {
            mMapControl.setFilter(filter, filterBehavior, null);
            promise.resolve(true);
        }else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void clearFilter(final Promise promise) {
        if (mMapControl != null) {
            mCtx.runOnUiQueueThread(() -> {
                mMapControl.clearFilter();
                promise.resolve(null);
            });
        }
    }

    @ReactMethod
    public void showUserPosition(boolean show, final Promise promise) {
        if (mMapControl != null) {
            mMapControl.showUserPosition(show);
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void goTo(String entityString, String entityType, final Promise promise) {
        switch (entityType) {
            case "MPLocation": {
                MPLocation location = gson.fromJson(entityString, Location.class).toMPLocation();
                mCtx.runOnUiQueueThread(() -> mMapControl.goTo(location));
                break;
            }
            case "MPFloor": {
                MPFloor floor = gson.fromJson(entityString, MPFloor.class);
                mCtx.runOnUiQueueThread(() -> mMapControl.goTo(floor));
                break;
            }
            case "MPBuilding": {
                MPBuilding building = gson.fromJson(entityString, MPBuilding.class);
                mCtx.runOnUiQueueThread(() -> mMapControl.goTo(building));
                break;
            }
            case "MPVenue":  {
                MPVenue venue = gson.fromJson(entityString, MPVenue.class);
                mCtx.runOnUiQueueThread(()-> mMapControl.goTo(venue));
                break;
            }
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void getCurrentVenue(final Promise promise) {
        final MPVenue venue = mMapControl.getCurrentVenue();
        if (venue != null) {
            promise.resolve(gson.toJson(venue));
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void getCurrentBuilding(final Promise promise) {
        final MPBuilding building = mMapControl.getCurrentBuilding();
        if (building != null) {
            promise.resolve(gson.toJson(building));
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void selectVenue(String venueString, boolean moveCamera, final Promise promise) {
        MPVenue venue = gson.fromJson(venueString, MPVenue.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.selectVenue(venue, moveCamera);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void selectBuilding(String buildingString, boolean moveCamera, final Promise promise) {
        MPBuilding building = gson.fromJson(buildingString, MPBuilding.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.selectBuilding(building, moveCamera);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void selectLocation(String locationString, String behaviorString, final Promise promise) {
        MPLocation location = gson.fromJson(locationString, Location.class).toMPLocation();
        MPSelectionBehavior behavior = gson.fromJson(behaviorString, MPSelectionBehavior.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.selectLocation(location, behavior);
            promise.resolve(null);
        });

    }

    @ReactMethod
    public void selectLocationWithId(String locationId, String behaviorString, final Promise promise) {
        MPSelectionBehavior behavior = gson.fromJson(behaviorString, MPSelectionBehavior.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.selectLocation(locationId, behavior);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void setMapPadding(int start, int top, int end, int bottom, final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.setMapPadding(start, top, end, bottom);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getMapViewPaddingStart(final Promise promise) {
        promise.resolve(mMapControl.getMapViewPaddingStart());
    }

    @ReactMethod
    public void getMapViewPaddingTop(final Promise promise) {
        promise.resolve(mMapControl.getMapViewPaddingTop());
    }

    @ReactMethod
    public void getMapViewPaddingEnd(final Promise promise) {
        promise.resolve(mMapControl.getMapViewPaddingEnd());
    }

    @ReactMethod
    public void getMapViewPaddingBottom(final Promise promise) {
        promise.resolve(mMapControl.getMapViewPaddingBottom());
    }

    @ReactMethod
    public void setMapStyle(String mapStyleString, final Promise promise) {
        mMapControl.setMapStyle(gson.fromJson(mapStyleString, MPMapStyle.class));
        promise.resolve(null);
    }

    @ReactMethod
    public void getMapStyle(final Promise promise) {
        MPMapStyle mapStyle = mMapControl.getMapStyle();
        String mapStyleString = mapStyle != null ? gson.toJson(mapStyle) : null;
        promise.resolve(mapStyleString);
    }

    @ReactMethod
    public void showInfoWindowOnClickedLocation(boolean show, final Promise promise) {
        mMapControl.showInfoWindowOnClickedLocation(show);
        promise.resolve(null);
    }

    @ReactMethod
    public void deSelectLocation(final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.deSelectLocation();
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getCurrentBuildingFloor(final Promise promise) {
        MPFloor floor = mMapControl.getCurrentBuildingFloor();
        String floorString = floor != null ? gson.toJson(floor) : null;
        promise.resolve(floorString);
    }

    @ReactMethod
    public void getCurrentFloorIndex(final Promise promise) {
        promise.resolve(mMapControl.getCurrentFloorIndex());
    }

    @ReactMethod
    public void getCurrentMapsIndoorsZoom(final Promise promise) {
        promise.resolve(mMapControl.getCurrentMapsIndoorsZoom());
    }

    @ReactMethod
    public void selectFloor(int floorIndex, final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            mMapControl.selectFloor(floorIndex);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void isFloorSelectorHidden(final Promise promise) {
        promise.resolve(mMapControl.isFloorSelectorHidden());
    }

    @ReactMethod
    public void hideFloorSelector(boolean hide, final Promise promise) {
        mMapControl.hideFloorSelector(hide);
        promise.resolve(null);
    }

    @ReactMethod
    public void isUserPositionShown(final Promise promise) {
        promise.resolve(mMapControl.isUserPositionShown());
    }

    private final Map<String, OnLiveLocationUpdateListener> liveLocationUpdateListeners = new HashMap<>();
    @ReactMethod
    public void enableLiveData(String domainType, boolean hasListener, final Promise promise) {
        if (hasListener) {
            if (liveLocationUpdateListeners.containsKey(domainType)) {
                liveLocationUpdateListeners.remove(domainType);
                mMapControl.disableLiveData(domainType);
            }

            OnLiveLocationUpdateListener listener = location -> {
                WritableMap params = Arguments.createMap();
                params.putString("location", gson.toJson(location));
                emit(Listener.ON_LIVE_LOCATION_UPDATE, params);
            };

            liveLocationUpdateListeners.put(domainType, listener);

            mMapControl.enableLiveData(domainType, listener);
        } else {
            mMapControl.enableLiveData(domainType);
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void disableLiveData(String domainType, final Promise promise) {
        liveLocationUpdateListeners.remove(domainType);
        mMapControl.disableLiveData(domainType);
        promise.resolve(null);
    }

    @ReactMethod
    public void animateCamera(String updateString, @Nullable Integer duration, final Promise promise) {
        MPCameraUpdate update = gson.fromJson(updateString, MPCameraUpdate.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapView.animateCamera(update, duration, error -> {
                promise.resolve(null);
            });
        });
    }

    @ReactMethod
    public void moveCamera(String updateString, final Promise promise) {
        MPCameraUpdate update = gson.fromJson(updateString, MPCameraUpdate.class);
        mCtx.runOnUiQueueThread(() -> {
            mMapView.moveCamera(update);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getCurrentCameraPosition(final Promise promise) {
        mCtx.runOnUiQueueThread(() -> promise.resolve(gson.toJson(mMapView.getCurrentCameraPosition())));
    }

    @ReactMethod
    public void setFloorSelector(boolean setup, Boolean isAutoFloorChangeEnabled, final Promise promise) {
        if (mMapControl != null) {
            if (setup) {
                autoFloorChange = isAutoFloorChangeEnabled;
                mCtx.runOnUiQueueThread(() -> mMapControl.setFloorSelector(this));
            } else {
                autoFloorChange = true;
                mMapControl.setFloorSelector(null);
            }
        }
        promise.resolve(null);
    }

    //region listeners

    @ReactMethod
    public void setOnMapClickListener(boolean setup, @Nullable final Boolean consumeEvent) {
        if (setup) {
            mMapControl.setOnMapClickListener((latLng, locations) -> {
                WritableMap params = Arguments.createMap();
                params.putString("point", gson.toJson(new MPPoint(latLng)));
                params.putString("locations", gson.toJson(locations));
                emit(Listener.ON_MAP_CLICK, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mMapControl.setOnMapClickListener(null);
        }
    }

    @ReactMethod
    public void setOnLocationSelectedListener(boolean setup, @Nullable final Boolean consumeEvent) {
        if (setup) {
            mMapControl.setOnLocationSelectedListener(location -> {
                WritableMap params = Arguments.createMap();
                params.putString("location", gson.toJson(location));
                emit(Listener.ON_LOCATION_SELECTED, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mMapControl.setOnLocationSelectedListener(null);
        }
    }

    @ReactMethod
    public void setOnCurrentVenueChangedListener(boolean setup) {
        if (setup) {
            mMapControl.setOnCurrentVenueChangedListener(venue -> {
                WritableMap params = Arguments.createMap();
                params.putString("venue", gson.toJson(venue));
                emit(Listener.ON_VENUE_FOUND_AT_CAMERA_TARGET, params);
            });
        } else {
            mMapControl.setOnCurrentVenueChangedListener(null);
        }
    }

    @ReactMethod
    public void setOnCurrentBuildingChangedListener(boolean setup) {
        if (setup) {
            mMapControl.setOnCurrentBuildingChangedListener(building -> {
                WritableMap params = Arguments.createMap();
                params.putString("building", gson.toJson(building));
                emit(Listener.ON_BUILDING_FOUND_AT_CAMERA_TARGET, params);
            });
        } else {
            mMapControl.setOnCurrentBuildingChangedListener(null);
        }
    }

    @ReactMethod
    public void setOnMarkerClickListener(boolean setup, @Nullable final Boolean consumeEvent) {
        if (setup) {
            mMapControl.setOnMarkerClickListener(marker -> {
                WritableMap params = Arguments.createMap();
                params.putString("locationId", marker.getId());
                emit(Listener.ON_MARKER_CLICK, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mMapControl.setOnMarkerClickListener(null);
        }
    }

    @ReactMethod
    public void setOnMarkerInfoWindowClickListener(boolean setup) {
        if (setup) {
            mMapControl.setOnMarkerInfoWindowClickListener(marker -> {
                WritableMap params = Arguments.createMap();
                params.putString("locationId", marker.getId());
                emit(Listener.ON_INFO_WINDOW_CLICK, params);
            });
        } else {
            mMapControl.setOnMarkerInfoWindowClickListener(null);
        }
    }

    @ReactMethod
    public void setMPCameraEventListener(boolean setup) {
        if (setup) {
            mMapControl.addOnCameraEventListener(this);
        } else {
            mMapControl.removeOnCameraEventListener(this);
        }
    }

    @ReactMethod
    public void setOnFloorUpdateListener(boolean setup) {
        if (setup) {
            mMapControl.addOnFloorUpdateListener(this);
        } else {
            mMapControl.removeOnFloorUpdateListener(this);
        }
    }

    @Override
    public void onFloorUpdate(@Nullable MPBuilding building, int floorIndex) {
        WritableMap params = Arguments.createMap();
        params.putString("floorIndex", gson.toJson(floorIndex));
        params.putString("building", gson.toJson(building));
        emit(Listener.ON_FLOOR_UPDATE, params);
    }

    @Override
    public void onCameraEvent(MPCameraEvent event) {
        WritableMap params = Arguments.createMap();
        params.putInt("event", event.ordinal());
        emit(Listener.CAMERA_EVENT, params);
    }

    @ReactMethod
    public void addListener(String eventName) {
        // we are handling listeners ourselves
    }
    @ReactMethod
    public void removeListeners(Integer count) {
        // we are handling listeners ourselves
    }

    //endregion

    //region FloorSelectorInterface
    private OnFloorSelectionChangedListener onFloorSelectionChangedListener = null;
    private boolean autoFloorChange = true;

    @Nullable
    @Override
    public View getView() {
        return null;
    }

    @Override
    public void setOnFloorSelectionChangedListener(@Nullable OnFloorSelectionChangedListener onFloorSelectionChangedListener) {
        this.onFloorSelectionChangedListener = onFloorSelectionChangedListener;
    }

    @ReactMethod
    public void onFloorSelectionChanged(String floorString) {
        if (onFloorSelectionChangedListener != null) {
            onFloorSelectionChangedListener.onFloorSelectionChanged(gson.fromJson(floorString, MPFloor.class));
        }
    }

    @Override
    public void setList(@Nullable List<MPFloor> list) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "setList");
        if (list != null) {
            params.putString("list", gson.toJson(list));
        }

        emit(Listener.FLOOR_SELECTOR, params);
    }

    @Override
    public void show(boolean show, boolean animated) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "show");
        params.putBoolean("show", show);
        params.putBoolean("animated", animated);
        emit(Listener.FLOOR_SELECTOR, params);
    }

    @Override
    public void setSelectedFloor(@NonNull MPFloor floor) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "setSelectedFloor");
        params.putString("floor", gson.toJson(floor));
        emit(Listener.FLOOR_SELECTOR, params);
    }

    @Override
    public void setSelectedFloorByZIndex(int floorIndex) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "setSelectedFloorByFloorIndex");
        params.putInt("floorIndex", floorIndex);
        emit(Listener.FLOOR_SELECTOR, params);
    }

    @Override
    public void zoomLevelChanged(float newZoomLevel) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "zoomLevelChanged");
        params.putDouble("zoom", newZoomLevel);
        emit(Listener.FLOOR_SELECTOR, params);
    }

    @Override
    public boolean isAutoFloorChangeEnabled() {
        return autoFloorChange;
    }

    @Override
    public void setUserPositionFloor(int floor) {
        WritableMap params = Arguments.createMap();
        params.putString("method", "setUserPositionFloor");
        params.putInt("floor", floor);
        emit(Listener.FLOOR_SELECTOR, params);
    }
    // endregion

    enum Listener {
        ON_FLOOR_UPDATE("onFloorUpdate"),
        FLOOR_SELECTOR("floorSelector"),
        CAMERA_EVENT("cameraEvent"),
        ON_LOCATION_SELECTED("onLocationSelected"),
        ON_MAP_CLICK("onMapClick"),
        ON_VENUE_FOUND_AT_CAMERA_TARGET("onVenueFoundAtCameraTarget"),
        ON_BUILDING_FOUND_AT_CAMERA_TARGET("onBuildingFoundAtCameraTarget"),
        ON_MARKER_CLICK("onMarkerClick"),
        ON_INFO_WINDOW_CLICK("onInfoWindowClick"),
        ON_LIVE_LOCATION_UPDATE("onLiveLocationUpdate");
        private final String method;
        Listener(String method) {
            this.method = method;
        }
    }

}

