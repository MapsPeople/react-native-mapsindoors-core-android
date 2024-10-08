package com.mapsindoorsrn.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReadableArray;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapsindoors.core.MPCameraViewFitMode;
import com.mapsindoors.core.MPDirectionsRenderer;
import com.mapsindoors.core.MPRoute;
import com.mapsindoors.core.MPRouteStopIconProvider;
import com.mapsindoors.core.MapControl;
import com.mapsindoors.core.OnMapControlReadyListener;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.errors.MIErrorEnum;
import com.mapsindoors.core.errors.MapsIndoorsException;
import com.mapsindoorsrn.core.models.BitmapStopIconConfig;
import com.mapsindoorsrn.core.models.MPError;
import com.mapsindoorsrn.core.models.RouteStopIconConfigModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DirectionsRendererModule extends ReactContextBaseJavaModule implements OnMapControlReadyListener {
    private MapControl mMapControl;
    private MPDirectionsRenderer mRenderer;
    private final ReactApplicationContext mCtx;
    private final Gson gson = new Gson();


    public DirectionsRendererModule(@NonNull ReactApplicationContext reactContext, MapControlModule mapControl) {
        super(reactContext);
        mapControl.setOnMapControlReadyListener(this);
        mCtx = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "DirectionsRenderer";
    }

    private void rejectPromise(Promise promise) {
        promise.reject("Renderer not initialized", new MapsIndoorsException("Renderer not initialized"));
    }

    @ReactMethod
    public void clear(final Promise promise) {
        if (mRenderer != null) {
            mRenderer.clear();
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void getSelectedLegFloorIndex(final Promise promise) {
        if (mRenderer != null) {
            promise.resolve(mRenderer.getSelectedLegFloorIndex());
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void nextLeg(final Promise promise) {
        if (mRenderer != null) {
            mRenderer.nextLeg();
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void previousLeg(final Promise promise) {
        if (mRenderer != null) {
            mRenderer.previousLeg();
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void selectLegIndex(int legIndex, final Promise promise) {
        if (mRenderer != null) {
            try {
                mRenderer.selectLegIndex(legIndex);
            } catch (IllegalStateException e) {
                promise.reject(e.getMessage(), new Gson().toJson(MPError.fromMIError(new MIError(MIErrorEnum.ROUTING_UNKNOWN_ERROR, e.getMessage()))));
                return;
            }
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setAnimatedPolyline(boolean animated, boolean repeated, int duration, final Promise promise) {
        if (mRenderer != null) {
            mRenderer.setAnimatedPolyline(animated, repeated, duration);
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void showRouteLegButtons(boolean value, final Promise promise) {
        if (mRenderer != null) {
            mRenderer.showRouteLegButtons(value);
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setCameraAnimationDuration(int duration, final Promise promise) {
        if (mRenderer != null) {
            mRenderer.setCameraAnimationDuration(duration);
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setCameraViewFitMode(int cameraFitMode, final Promise promise) {
        if (mRenderer != null) {
            final MPCameraViewFitMode mode;
            switch (cameraFitMode) {
                case 0:
                    mode = MPCameraViewFitMode.NORTH_ALIGNED;
                    break;
                case 1:
                    mode = MPCameraViewFitMode.FIRST_STEP_ALIGNED;
                    break;
                case 2:
                    mode = MPCameraViewFitMode.START_TO_END_ALIGNED;
                    break;
                case 3:
                    mode = MPCameraViewFitMode.NONE;
                    break;
                default:
                    mode = null;
            }
            if (mode != null) {
                mRenderer.setCameraViewFitMode(mode);
            }
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setOnLegSelectedListener(final Promise promise) {
        if (mRenderer != null) {
            mRenderer.setOnLegSelectedListener(leg -> {
                WritableMap params = Arguments.createMap();
                params.putInt("leg", leg);
                mCtx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onLegSelected", params);
            });
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setPolyLineColors(String foregroundString, String backgroundString, final Promise promise) {
        if (mRenderer != null) {
            int foreground;
            int background;
            try {
                foreground = Color.parseColor(foregroundString);
                background = Color.parseColor(backgroundString);
            } catch (IllegalArgumentException e) {
                promise.reject(e);
                return;
            }
            mRenderer.setPolylineColors(foreground, background);
            promise.resolve(null);
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setRoute(String routeString, String icons, int legIndex, final Promise promise) {
        if (mRenderer != null) {
            Context ctx = mCtx.getApplicationContext();
            MPRoute route = new Gson().fromJson(routeString, MPRoute.class);
            if (icons != null) {
                new Handler().post(()-> {
                    HashMap<Integer, String> iconsMap = gson.fromJson(icons, new TypeToken<HashMap<Integer, String>>(){}.getType());
                    HashMap<Integer, MPRouteStopIconProvider> iconConfigs = new HashMap<>();
                    for (Map.Entry<Integer, String> mapEntry : iconsMap.entrySet()) {
                        String icon = mapEntry.getValue();
                        if (icon == null) {
                            iconConfigs.put(mapEntry.getKey(), null);
                        } else if (isUrl(icon)) {
                            FutureTarget<Bitmap> futureTarget = Glide.with(ctx).asBitmap().load(icon).submit();
                            try {
                                BitmapStopIconConfig iconConfig = new BitmapStopIconConfig(futureTarget.get());
                                iconConfigs.put(mapEntry.getKey(), iconConfig);
                            } catch (Exception e) {
                                promise.reject("something went wrong loading the image for " + icon, e);
                                return;
                            }
                        }else {
                            String json = icon.substring(0, icon.length() - 1);
                            RouteStopIconConfigModel iconConfig = gson.fromJson(json, RouteStopIconConfigModel.class);
                            iconConfigs.put(mapEntry.getKey(), iconConfig.toMPRouteStopIconConfig(ctx));
                        }
                    }

                    mRenderer.setRoute(route, iconConfigs);
                    if (legIndex != 0) {
                        try {
                            mRenderer.selectLegIndex(legIndex);
                        } catch (IllegalStateException e) {
                            promise.reject(e.getMessage(), new Gson().toJson(MPError.fromMIError(new MIError(MIErrorEnum.ROUTING_UNKNOWN_ERROR, e.getMessage()))));
                            return;
                        }   
                    }
                    
                    promise.resolve(null);
                });
            }else {
                mRenderer.setRoute(route);
                promise.resolve(null);
            }
        } else {
            rejectPromise(promise);
        }
    }

    @ReactMethod
    public void setDefaultRouteStopIcon(String iconString, final Promise promise) {
        if (mRenderer != null) {
            //Create icon from url
            if (iconString != null) {
                //Handle icon
                new Handler().post(()-> {
                    Context ctx = mCtx.getApplicationContext();
                    if (iconString == null || iconString.equals("null") || iconString.isEmpty()) {
                          mRenderer.setDefaultRouteStopIconConfig(null);
                          promise.resolve(null);
                     } else if (isUrl(iconString)) {
                          FutureTarget<Bitmap> futureTarget = Glide.with(ctx).asBitmap().load(iconString).submit();
                          try {
                            BitmapStopIconConfig iconConfig = new BitmapStopIconConfig(futureTarget.get());
                            mRenderer.setDefaultRouteStopIconConfig(iconConfig);
                            promise.resolve(null);
                          } catch (Exception e) {
                            promise.reject("something went wrong loading the image", e);
                          }
                     } else {
                        String json = iconString.substring(0, iconString.length() - 1);
                        RouteStopIconConfigModel iconConfig = gson.fromJson(json, RouteStopIconConfigModel.class);
                        mRenderer.setDefaultRouteStopIconConfig(iconConfig.toMPRouteStopIconConfig(ctx));
                        promise.resolve(null);
                    }
                });
            }else {
                mRenderer.setDefaultRouteStopIconConfig(null);
                promise.resolve(null);
            }
        } else {
            rejectPromise(promise);
        }
    }

    @Override
    public void onMapControlReady(@Nullable MapControl mapControl, @Nullable MIError miError) {
        if (mapControl != null) {
            mMapControl = mapControl;
            mRenderer = new MPDirectionsRenderer(mMapControl);
        }
    }

    @ReactMethod
    public void addListener(String eventName) {
        // we are handling listeners ourselves
    }
    @ReactMethod
    public void removeListeners(Integer count) {
        // we are handling listeners ourselves
    }

    private boolean isUrl(String url) {
        String regex = "^(http|https)://.*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
