package com.mapsindoorsrn.core;

import android.graphics.Typeface;
import android.util.Log;
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
import com.mapsindoors.core.MPFeatureType;
import com.mapsindoors.core.MPFilter;
import com.mapsindoors.core.MPFilterBehavior;
import com.mapsindoors.core.MPFloor;
import com.mapsindoors.core.MPFloorSelectorInterface;
import com.mapsindoors.core.MPHighlightBehavior;
import com.mapsindoors.core.MPIMapConfig;
import com.mapsindoors.core.MPLocation;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPSelectionBehavior;
import com.mapsindoors.core.MPSelectionMode;
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
import com.mapsindoorsrn.core.models.Filter;
import com.mapsindoorsrn.core.models.Location;
import com.mapsindoorsrn.core.models.MPCameraUpdate;
import com.facebook.react.module.annotations.ReactModule;
import com.mapsindoorsrn.core.models.MPError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ReactModule(name = MapControlModule.NAME)
public class MapControlModule extends ReactContextBaseJavaModule implements MPCameraEventListener, OnFloorUpdateListener, MPFloorSelectorInterface {
    public static final String NAME = "MapControlModule";

    // Volatile: written on the UI thread, read from React Native's module thread too.
    private volatile RCMapView mMapView;
    private final Gson gson = RnGson.create();
    private volatile MapControl mMapControl;
    private OnMapControlReadyListener mcListener;
    @Nullable private Runnable mOnMapControlDestroyed;
    private final ReactApplicationContext mCtx;

    public MapControlModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
        mCtx = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Rejects with the SDK's error, serialised.
     *
     * Most callers are SDK callbacks, off React Native's stack, where an unhandled throw takes the
     * host app down rather than failing the call. {@code MPError} carries {@code MIError.tag},
     * which is a bare {@code Object}, so this is the same reflection hazard the rest of the module
     * guards - and the one path where a throw would land when the call has already failed
     * (MS-3983). A failure to serialise the error still has to reach the caller, so it falls back
     * to the Gson-free payload rather than being dropped.
     */
    private void reject(Promise promise, MIError error) {
        final String errorString;
        try {
            errorString = gson.toJson(MPError.fromMIError(error));
        } catch (Exception | StackOverflowError t) {
            RnGson.rejectSerialisationFailure(promise, "MapControlError",
                    "Could not serialise error " + (error != null ? error.code : "null") + ": " + t);
            return;
        }
        promise.reject("MapControlError", errorString);
    }

    public void setOnMapControlReadyListener(OnMapControlReadyListener listener) {
       mcListener = listener;
    }

    private void emit(Listener listener, Object data) {
        mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(listener.method, data);
    }

    public void setOnMapControlDestroyedListener(@Nullable Runnable listener) {
        mOnMapControlDestroyed = listener;
    }

    public void setView(RCMapView view) {
        mMapView = view;
    }

    /**
     * Releases {@code view} and its MapControl once React Native unmounts it. Call on the UI thread,
     * before the map itself is destroyed.
     *
     * Left alive, the MapControl stays registered with the SDK, which keeps it and its map reachable
     * and has it react to the next map's display rule changes (SPEX-2885). A no-op for any other view,
     * as a new map may mount before the old one is dropped.
     */
    public void onViewDropped(RCMapView view) {
        if (mMapView != view) {
            return;
        }
        destroyMapControl();
        mMapView = null;
    }

    private void destroyMapControl() {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            return;
        }
        mMapControl = null;
        liveLocationUpdateListeners.clear();
        // Set by the destroyed MapControl, which it would keep reachable.
        onFloorSelectionChangedListener = null;
        mapControl.onDestroy();
        if (mOnMapControlDestroyed != null) {
            mOnMapControlDestroyed.run();
        }
    }

    /**
     * The MapControl, or {@code null} once {@code promise} is rejected: there is none before
     * {@code initMapControl}, nor after its view is dropped.
     */
    @Nullable
    private MapControl requireMapControl(@Nullable Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl == null && promise != null) {
            promise.reject("MapControlError", "mapControl not available");
        }
        return mapControl;
    }

    /**
     * Logs a call that has no promise to reject: without a MapControl, JS would otherwise believe the
     * listener was set.
     */
    private void warnNoMapControl(String method) {
        Log.w(NAME, method + ": mapControl not available, ignored");
    }

    private interface MapControlAction {
        void run(@NonNull MapControl mapControl);
    }

    /**
     * Runs {@code action} on the UI thread with the MapControl. Read there, where it is destroyed, so
     * it cannot go between the check and the call. Without one, rejects {@code promise} if given.
     */
    private void runOnUiQueueThread(@Nullable Promise promise, MapControlAction action) {
        mCtx.runOnUiQueueThread(() -> {
            MapControl mapControl = requireMapControl(promise);
            if (mapControl != null) {
                action.run(mapControl);
            }
        });
    }

    @ReactMethod
    public void initMapControl(ReadableMap config, final Promise promise) {
        getCurrentActivity().runOnUiThread(()-> {
            RCMapView mapView = mMapView;
            if (mapView == null) {
                promise.reject("MapControlError", "MapView not available");
                return;
            }
            // Replaced below: destroyed first so it unregisters from the SDK.
            destroyMapControl();
            MPIMapConfig mpMapConfig = mapView.parseConfig(config, getCurrentActivity());
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
        MapControl mapControl = mMapControl;
        if (mapControl != null) {
            mapControl.setFilter(locations, filterBehavior);
            promise.resolve(true);
        }else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void setFilter(String filterString, String filterBehaviorString, final Promise promise) {
        MPFilter filter = gson.fromJson(filterString, Filter.class).toMPFilter();
        MPFilterBehavior filterBehavior = gson.fromJson(filterBehaviorString, MPFilterBehavior.class);
        MapControl mapControl = mMapControl;
        if (mapControl != null && filter != null) {
            mapControl.setFilter(filter, filterBehavior, null);
            promise.resolve(true);
        }else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void clearFilter(final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            MapControl mapControl = mMapControl;
            if (mapControl != null) {
                mapControl.clearFilter();
            }
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void setHighlight(String locationsString, String highlightBehaviorString, final Promise promise) {
        ArrayList<String> locationIds = gson.fromJson(locationsString, new TypeToken<List<String>>() {}.getType());
        List<MPLocation> locations = new ArrayList<>();
        for (String locationId : locationIds) {
            locations.add(MapsIndoors.getLocationById(locationId));
        }
        MPHighlightBehavior highlightBehavior = gson.fromJson(highlightBehaviorString, MPHighlightBehavior.class);
        MapControl mapControl = mMapControl;
        if (mapControl != null) {
            mapControl.setHighlight(locations, highlightBehavior);
            promise.resolve(true);
        }else {
            promise.resolve(false);
        }
    }

    @ReactMethod
    public void clearHighlight(final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            MapControl mapControl = mMapControl;
            if (mapControl != null) {
                mapControl.clearHighlight();
            }
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void showUserPosition(boolean show, final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl != null) {
            mapControl.showUserPosition(show);
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void goTo(String entityString, String entityType, Double maxZoom, final Promise promise) {
        switch (entityType) {
            case "MPLocation": {
                MPLocation location = gson.fromJson(entityString, Location.class).toMPLocation();
                if (maxZoom != -999) {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(location, maxZoom));
                }else {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(location));
                }
                break;
            }
            case "MPFloor": {
                MPFloor floor = gson.fromJson(entityString, MPFloor.class);
                if (maxZoom != -999) {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(floor, maxZoom));
                }else {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(floor));
                }
                break;
            }
            case "MPBuilding": {
                MPBuilding building = gson.fromJson(entityString, MPBuilding.class);
                if (maxZoom != -999) {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(building, maxZoom));
                }else {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(building));
                }
                break;
            }
            case "MPVenue":  {
                MPVenue venue = gson.fromJson(entityString, MPVenue.class);
                if (maxZoom != -999) {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(venue, maxZoom));
                }else {
                    runOnUiQueueThread(null, mapControl -> mapControl.goTo(venue));
                }
                break;
            }
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void getCurrentVenue(final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            promise.resolve(null);
            return;
        }
        final MPVenue venue = mapControl.getCurrentVenue();
        if (venue != null) {
            promise.resolve(gson.toJson(venue));
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void getCurrentBuilding(final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            promise.resolve(null);
            return;
        }
        final MPBuilding building = mapControl.getCurrentBuilding();
        if (building != null) {
            promise.resolve(gson.toJson(building));
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void selectVenue(String venueString, boolean moveCamera, final Promise promise) {
        MPVenue venue = gson.fromJson(venueString, MPVenue.class);
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.selectVenue(venue, moveCamera);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void selectBuilding(String buildingString, boolean moveCamera, final Promise promise) {
        MPBuilding building = gson.fromJson(buildingString, MPBuilding.class);
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.selectBuilding(building, moveCamera);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void selectLocation(String locationString, String behaviorString, final Promise promise) {
        MPLocation location = gson.fromJson(locationString, Location.class).toMPLocation();
        MPSelectionBehavior behavior = gson.fromJson(behaviorString, MPSelectionBehavior.class);
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.selectLocation(location, behavior);
            promise.resolve(null);
        });

    }

    @ReactMethod
    public void selectLocationWithId(String locationId, String behaviorString, final Promise promise) {
        MPSelectionBehavior behavior = gson.fromJson(behaviorString, MPSelectionBehavior.class);
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.selectLocation(locationId, behavior);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void setMapPadding(int start, int top, int end, int bottom, final Promise promise) {
        double density =  mCtx.getResources().getDisplayMetrics().density;
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.setMapPadding((int)Math.round(start * density), (int)Math.round(top * density), (int)Math.round(end * density), (int)   Math.round(bottom * density));
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getMapViewPaddingStart(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        double density =  mCtx.getResources().getDisplayMetrics().density;
        promise.resolve((int)Math.round(mapControl.getMapViewPaddingStart()/density));
    }

    @ReactMethod
    public void getMapViewPaddingTop(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        double density =  mCtx.getResources().getDisplayMetrics().density;
        promise.resolve((int)Math.round(mapControl.getMapViewPaddingTop()/density));
    }

    @ReactMethod
    public void getMapViewPaddingEnd(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        double density =  mCtx.getResources().getDisplayMetrics().density;
        promise.resolve((int)Math.round(mapControl.getMapViewPaddingEnd()/density));
    }

    @ReactMethod
    public void getMapViewPaddingBottom(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        double density =  mCtx.getResources().getDisplayMetrics().density;
        promise.resolve((int)Math.round(mapControl.getMapViewPaddingBottom()/density));
    }

    @ReactMethod
    public void setMapStyle(String mapStyleString, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        mapControl.setMapStyle(gson.fromJson(mapStyleString, MPMapStyle.class));
        promise.resolve(null);
    }

    @ReactMethod
    public void getMapStyle(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        MPMapStyle mapStyle = mapControl.getMapStyle();
        String mapStyleString = mapStyle != null ? gson.toJson(mapStyle) : null;
        promise.resolve(mapStyleString);
    }

    @ReactMethod
    public void showInfoWindowOnClickedLocation(boolean show, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        mapControl.showInfoWindowOnClickedLocation(show);
        promise.resolve(null);
    }

    @ReactMethod
    public void deSelectLocation(final Promise promise) {
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.deSelectLocation();
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getCurrentBuildingFloor(final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            promise.resolve(null);
            return;
        }
        MPFloor floor = mapControl.getCurrentBuildingFloor();
        String floorString = floor != null ? gson.toJson(floor) : null;
        promise.resolve(floorString);
    }

    @ReactMethod
    public void getCurrentFloorIndex(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.getCurrentFloorIndex());
    }

    @ReactMethod
    public void getCurrentMapsIndoorsZoom(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.getCurrentMapsIndoorsZoom());
    }

    @ReactMethod
    public void selectFloor(int floorIndex, final Promise promise) {
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.selectFloor(floorIndex);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void isFloorSelectorHidden(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.isFloorSelectorHidden());
    }

    @ReactMethod
    public void hideFloorSelector(boolean hide, final Promise promise) {
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.hideFloorSelector(hide);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void isUserPositionShown(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.isUserPositionShown());
    }

    private final Map<String, OnLiveLocationUpdateListener> liveLocationUpdateListeners = new HashMap<>();
    @ReactMethod
    public void enableLiveData(String domainType, boolean hasListener, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        if (hasListener) {
            if (liveLocationUpdateListeners.containsKey(domainType)) {
                liveLocationUpdateListeners.remove(domainType);
                mapControl.disableLiveData(domainType);
            }

            OnLiveLocationUpdateListener listener = location -> {
                // Live updates arrive on the SDK's thread and there is no promise to fail, so a
                // throw here would take the host app down. Drop the update and log it (MS-3983).
                // StackOverflowError is named because it is the failure being guarded and it is an
                // Error, not an Exception; OutOfMemoryError and the linkage Errors are left to
                // propagate, since nothing here can recover from them.
                final String locationString;
                try {
                    locationString = gson.toJson(location);
                } catch (Exception | StackOverflowError t) {
                    Log.e(NAME, "Dropping a live location update that could not be serialised", t);
                    return;
                }

                WritableMap params = Arguments.createMap();
                params.putString("location", locationString);
                emit(Listener.ON_LIVE_LOCATION_UPDATE, params);
            };

            liveLocationUpdateListeners.put(domainType, listener);

            mapControl.enableLiveData(domainType, listener);
        } else {
            mapControl.enableLiveData(domainType);
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void disableLiveData(String domainType, final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            promise.resolve(null);
            return;
        }
        liveLocationUpdateListeners.remove(domainType);
        mapControl.disableLiveData(domainType);
        promise.resolve(null);
    }

    @ReactMethod
    public void animateCamera(String updateString, @Nullable Integer duration, final Promise promise) {
        MPCameraUpdate update = gson.fromJson(updateString, MPCameraUpdate.class);
        mCtx.runOnUiQueueThread(() -> {
            RCMapView mapView = mMapView;
            if (mapView == null) {
                promise.reject("MapControlError", "MapView not available");
                return;
            }
            // we have to parse null as -1 because iOS cannot parse null
            mapView.animateCamera(update, (duration == - 1 ? null : duration), error -> {
                promise.resolve(null);
            });
        });
    }

    @ReactMethod
    public void moveCamera(String updateString, final Promise promise) {
        MPCameraUpdate update = gson.fromJson(updateString, MPCameraUpdate.class);
        mCtx.runOnUiQueueThread(() -> {
            RCMapView mapView = mMapView;
            if (mapView == null) {
                promise.reject("MapControlError", "MapView not available");
                return;
            }
            mapView.moveCamera(update);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getCurrentCameraPosition(final Promise promise) {
        mCtx.runOnUiQueueThread(() -> {
            // Runs later on the UI queue, off React Native's stack (MS-3983).
            RCMapView mapView = mMapView;
            if (mapView == null) {
                promise.reject("MapControlError", "MapView not available");
                return;
            }
            final String cameraPositionString;
            try {
                cameraPositionString = gson.toJson(mapView.getCurrentCameraPosition());
            } catch (Exception | StackOverflowError t) {
                RnGson.rejectSerialisationFailure(promise, "MapControlError",
                        "Could not serialise the camera position: " + t);
                return;
            }
            promise.resolve(cameraPositionString);
        });
    }

    @ReactMethod
    public void setFloorSelector(boolean setup, Boolean isAutoFloorChangeEnabled, final Promise promise) {
        MapControl mapControl = mMapControl;
        if (mapControl != null) {
            if (setup) {
                autoFloorChange = isAutoFloorChangeEnabled;
                runOnUiQueueThread(null, current -> current.setFloorSelector(this));
            } else {
                autoFloorChange = true;
                mapControl.setFloorSelector(null);
            }
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setLabelOptions(Integer textSize, String color, boolean showHalo, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        mapControl.setMapLabelFont(Typeface.DEFAULT, color, showHalo);
        if (textSize != -1) {
            mapControl.setMapLabelTextSize(textSize);
        }
        promise.resolve(null);
    }

    //region listeners

    @ReactMethod
    public void setOnMapClickListener(boolean setup, @Nullable final Boolean consumeEvent) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnMapClickListener");
            return;
        }
        if (setup) {
            mapControl.setOnMapClickListener((latLng, locations) -> {
                // Emitted from an SDK callback, off React Native's stack: a throw here takes the
                // host app down and there is no promise to fail. Drop the event and log it (MS-3983).
                // The locations list is the payload that crashed in MS-3983, and this fires on every
                // tap rather than once per search. Consumption is unaffected by a dropped event:
                // the host app configured it, so report it either way.
                final WritableMap params = Arguments.createMap();
                try {
                    params.putString("point", gson.toJson(new MPPoint(latLng)));
                    params.putString("locations", gson.toJson(locations));
                } catch (Exception | StackOverflowError t) {
                    Log.e(NAME, "Dropping a map click that could not be serialised", t);
                    return Boolean.TRUE.equals(consumeEvent);
                }
                emit(Listener.ON_MAP_CLICK, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mapControl.setOnMapClickListener(null);
        }
    }

    @ReactMethod
    public void setOnLocationSelectedListener(boolean setup, @Nullable final Boolean consumeEvent) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnLocationSelectedListener");
            return;
        }
        if (setup) {
            mapControl.setOnLocationSelectedListener(location -> {
                // Emitted from an SDK callback, off React Native's stack: a throw here takes the
                // host app down and there is no promise to fail. Drop the event and log it (MS-3983).
                final WritableMap params = Arguments.createMap();
                try {
                    params.putString("location", gson.toJson(location));
                } catch (Exception | StackOverflowError t) {
                    Log.e(NAME, "Dropping a location selection that could not be serialised", t);
                    return Boolean.TRUE.equals(consumeEvent);
                }
                emit(Listener.ON_LOCATION_SELECTED, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mapControl.setOnLocationSelectedListener(null);
        }
    }

    @ReactMethod
    public void setOnCurrentVenueChangedListener(boolean setup) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnCurrentVenueChangedListener");
            return;
        }
        if (setup) {
            mapControl.setOnCurrentVenueChangedListener(venue -> {
                // Emitted from an SDK callback, off React Native's stack: a throw here takes the
                // host app down and there is no promise to fail. Drop the event and log it (MS-3983).
                final WritableMap params = Arguments.createMap();
                try {
                    params.putString("venue", gson.toJson(venue));
                } catch (Exception | StackOverflowError t) {
                    Log.e(NAME, "Dropping a venue change that could not be serialised", t);
                    return;
                }
                emit(Listener.ON_VENUE_FOUND_AT_CAMERA_TARGET, params);
            });
        } else {
            mapControl.setOnCurrentVenueChangedListener(null);
        }
    }

    @ReactMethod
    public void setOnCurrentBuildingChangedListener(boolean setup) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnCurrentBuildingChangedListener");
            return;
        }
        if (setup) {
            mapControl.setOnCurrentBuildingChangedListener(building -> {
                // Emitted from an SDK callback, off React Native's stack: a throw here takes the
                // host app down and there is no promise to fail. Drop the event and log it (MS-3983).
                final WritableMap params = Arguments.createMap();
                try {
                    params.putString("building", gson.toJson(building));
                } catch (Exception | StackOverflowError t) {
                    Log.e(NAME, "Dropping a building change that could not be serialised", t);
                    return;
                }
                emit(Listener.ON_BUILDING_FOUND_AT_CAMERA_TARGET, params);
            });
        } else {
            mapControl.setOnCurrentBuildingChangedListener(null);
        }
    }

    @ReactMethod
    public void setOnMarkerClickListener(boolean setup, @Nullable final Boolean consumeEvent) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnMarkerClickListener");
            return;
        }
        if (setup) {
            mapControl.setOnMarkerClickListener(marker -> {
                WritableMap params = Arguments.createMap();
                params.putString("locationId", marker.getId());
                emit(Listener.ON_MARKER_CLICK, params);
                return Boolean.TRUE.equals(consumeEvent);
            });
        } else {
            mapControl.setOnMarkerClickListener(null);
        }
    }

    @ReactMethod
    public void setOnMarkerInfoWindowClickListener(boolean setup) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnMarkerInfoWindowClickListener");
            return;
        }
        if (setup) {
            mapControl.setOnMarkerInfoWindowClickListener(marker -> {
                WritableMap params = Arguments.createMap();
                params.putString("locationId", marker.getId());
                emit(Listener.ON_INFO_WINDOW_CLICK, params);
            });
        } else {
            mapControl.setOnMarkerInfoWindowClickListener(null);
        }
    }

    @ReactMethod
    public void setMPCameraEventListener(boolean setup) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setMPCameraEventListener");
            return;
        }
        if (setup) {
            mapControl.addOnCameraEventListener(this);
        } else {
            mapControl.removeOnCameraEventListener(this);
        }
    }

    @ReactMethod
    public void setOnFloorUpdateListener(boolean setup) {
        MapControl mapControl = mMapControl;
        if (mapControl == null) {
            warnNoMapControl("setOnFloorUpdateListener");
            return;
        }
        if (setup) {
            mapControl.addOnFloorUpdateListener(this);
        } else {
            mapControl.removeOnFloorUpdateListener(this);
        }
    }

    @ReactMethod
    public void setBuildingSelectionMode(Double selectionMode, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        switch (selectionMode.intValue()) {
            case 0:
                mapControl.setBuildingSelectionMode(MPSelectionMode.MANUAL);
                break;
            case 1:
                mapControl.setBuildingSelectionMode(MPSelectionMode.AUTOMATIC);
                break;
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void getBuildingSelectionMode(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.getBuildingSelectionMode().ordinal());
    }

    @ReactMethod
    public void setFloorSelectionMode(Double selectionMode, final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        switch (selectionMode.intValue()) {
            case 0:
                mapControl.setFloorSelectionMode(MPSelectionMode.MANUAL);
                break;
            case 1:
                mapControl.setFloorSelectionMode(MPSelectionMode.AUTOMATIC);
                break;
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void getFloorSelectionMode(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        promise.resolve(mapControl.getFloorSelectionMode().ordinal());
    }

    @ReactMethod
    public void setHiddenFeatures(String featuresString, final Promise promise) {
        List<Integer> featureStrings = gson.fromJson(featuresString, new TypeToken<List<Integer>>() {}.getType());
        List<MPFeatureType> features = new ArrayList<>();
        for (Integer feature : featureStrings) {
            switch (feature) {
                case 1:
                    features.add(MPFeatureType.WALLS_2D);
                    break;
                case 3:
                    features.add(MPFeatureType.WALLS_3D);
                    break;
                case 0:
                    features.add(MPFeatureType.MODEL_2D);
                    break;
                case 2:
                    features.add(MPFeatureType.MODEL_3D);
                    break;
                case 4:
                    features.add(MPFeatureType.EXTRUSION_3D);
                    break;
                case 5:
                    features.add(MPFeatureType.EXTRUDED_BUILDINGS);
                    break;
            }
        }
        runOnUiQueueThread(promise, mapControl -> {
            mapControl.setHiddenFeatures(features);
            promise.resolve(null);
        });
    }

    @ReactMethod
    public void getHiddenFeatures(final Promise promise) {
        MapControl mapControl = requireMapControl(promise);
        if (mapControl == null) {
            return;
        }
        List<MPFeatureType> features = mapControl.getHiddenFeatures();
        List<Integer> featureStrings = new ArrayList<>();
        for (MPFeatureType feature : features) {
            featureStrings.add(feature.ordinal());
        }
        promise.resolve(gson.toJson(featureStrings));
    }

    @Override
    public void onFloorUpdate(@Nullable MPBuilding building, int floorIndex) {
        // Called by the SDK, off React Native's stack: a throw here takes the host app down and
        // there is no promise to fail. Drop the event and log it (MS-3983).
        final WritableMap params = Arguments.createMap();
        try {
            params.putString("floorIndex", gson.toJson(floorIndex));
            params.putString("building", gson.toJson(building));
        } catch (Exception | StackOverflowError t) {
            Log.e(NAME, "Dropping a floor update that could not be serialised", t);
            return;
        }
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
        // Called by the SDK, off React Native's stack: a throw here takes the host app down and
        // there is no promise to fail. Drop the event and log it (MS-3983).
        final WritableMap params = Arguments.createMap();
        params.putString("method", "setList");
        if (list != null) {
            try {
                params.putString("list", gson.toJson(list));
            } catch (Exception | StackOverflowError t) {
                Log.e(NAME, "Dropping a floor list that could not be serialised", t);
                return;
            }
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
        // Called by the SDK, off React Native's stack: a throw here takes the host app down and
        // there is no promise to fail. Drop the event and log it (MS-3983).
        final WritableMap params = Arguments.createMap();
        params.putString("method", "setSelectedFloor");
        try {
            params.putString("floor", gson.toJson(floor));
        } catch (Exception | StackOverflowError t) {
            Log.e(NAME, "Dropping a floor selection that could not be serialised", t);
            return;
        }
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

