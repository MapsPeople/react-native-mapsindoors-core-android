package com.reactlibrary.core;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.mapsindoors.core.MPDirectionsService;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.errors.MapsIndoorsException;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DirectionsServiceModule extends ReactContextBaseJavaModule {

    private HashMap<String, MPDirectionsService> serviceMap = new HashMap<>();
    private static final String NO_DS = "This DirectionsService is not available";

    private final Gson gson = new Gson();
    public DirectionsServiceModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void create(final Promise promise) {
        MPDirectionsService service = new MPDirectionsService();
        String uuid = UUID.randomUUID().toString();
        serviceMap.put(uuid, service);
        promise.resolve(uuid);
    }

    @ReactMethod
    public void addAvoidWayType(String wayType, final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            service.addAvoidWayType(wayType);
            promise.resolve(null);
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }
    }

    @ReactMethod
    public void clearWayType(final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            service.clearWayType();
            promise.resolve(null);
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }
    }

    @ReactMethod
    public void setIsDeparture(boolean isDeparture, final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            service.setIsDeparture(isDeparture);
            promise.resolve(null);
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }
    }

    @ReactMethod
    public void getRoute(String originString, String destinationString, final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            service.setRouteResultListener((route, error) -> {
                WritableMap map = Arguments.createMap();
                if (route != null) {
                    map.putString("route", gson.toJson(route));
                }
                if (error != null) {
                    map.putString("error", gson.toJson(error));
                }
                promise.resolve(map);
            });
            MPPoint origin = gson.fromJson(originString, MPPoint.class);
            MPPoint destination = gson.fromJson(destinationString, MPPoint.class);

            if (origin != null && destination != null) {
                service.query(origin, destination);
            } else {
                promise.reject(new MapsIndoorsException("Origin:" + originString +
                        ", or Destination:" + destinationString + ", are not parsable"));
            }
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }

    }

    @ReactMethod
    public void setTravelMode(String travelMode, final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            service.setTravelMode(travelMode);
            promise.resolve(null);
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }
    }

    @ReactMethod
    public void setTime(int time, final String id, final Promise promise) {
        MPDirectionsService service = serviceMap.get(id);
        if (service != null) {
            Date date = new Date();
            date.setTime(time);
            service.setTime(date);
            promise.resolve(null);
        } else {
            promise.reject(NO_DS,new MapsIndoorsException((NO_DS)));
        }

    }

    @NonNull
    @Override
    public String getName() {
        return "DirectionsService";
    }
}
