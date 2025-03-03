package com.mapsindoorsrn.core;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.mapsindoors.core.MPCollisionHandling;
import com.mapsindoors.core.MPGeometry;
import com.mapsindoors.core.MPLocation;
import com.mapsindoors.core.MPLocationSettings;
import com.mapsindoors.core.MPMultiPolygonGeometry;
import com.mapsindoors.core.MPPOIType;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPPolygonGeometry;
import com.mapsindoors.core.MPSolution;
import com.mapsindoors.core.MPVenue;
import com.mapsindoors.core.MapsIndoors;
import com.mapsindoors.core.MPJsonParser;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.errors.MIErrorEnum;
import com.mapsindoorsrn.core.models.MPError;

import java.util.Locale;

public class UtilsModule extends ReactContextBaseJavaModule {

    private final Gson gson = new Gson();

    public UtilsModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "UtilsModule";
    }

    private void reject(Promise promise, MIError error) {
        promise.reject("UtilError", gson.toJson(MPError.fromMIError(error)));
    }

    @ReactMethod
    public void venueHasGraph(String venueId, final Promise promise) {
        if (MapsIndoors.getVenues() != null) {
            MPVenue venue = MapsIndoors.getVenues().getVenueById(venueId);
            if (venue != null) {
                promise.resolve(venue.hasGraph());
            } else {
                reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Cannot find venue with ID: " + venueId));
            }
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Venues are not available"));
        }

    }

    @ReactMethod
    public void pointAngleBetween(String point1, String point2, final Promise promise) {
        MPPoint it = MPJsonParser.parse(point1, MPPoint.class);
        MPPoint other = MPJsonParser.parse(point2, MPPoint.class);

        if (it != null && other != null) {
            promise.resolve(it.angleBetween(other));
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "One or more points are null: " + point1 + ", " + point2));
        }
    }

    @ReactMethod
    public void pointDistanceTo(String point1, String point2, final Promise promise) {
        MPPoint it = MPJsonParser.parse(point1, MPPoint.class);
        MPPoint other = MPJsonParser.parse(point2, MPPoint.class);

        if (it != null && other != null) {
            promise.resolve(it.distanceTo(other));
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "One or more points are null: " + point1 + ", " + point2));
        }
    }

    @ReactMethod
    public void geometryIsInside(String pointString, String geometryString, final Promise promise) {
        MPPoint point = MPJsonParser.parse(pointString, MPPoint.class);
        MPGeometry geometry = MPJsonParser.parse(geometryString, MPGeometry.class);
        if (point != null && geometry != null) {
            promise.resolve(geometry.isInside(point.getLatLng()));
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Point (" + point + ") and/or Geometry (" + geometry + ") is null"));
        }
    }

    @ReactMethod
    public void geometryArea(String geometryString, final Promise promise) {
        MPGeometry geometry = MPJsonParser.parse(geometryString, MPGeometry.class);
        if (geometry != null) {
            promise.resolve(geometry.getArea());
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Geometry is null"));
        }
    }

    @ReactMethod
    public void polygonDistanceToClosestEdge(String pointString, String geometryString, final Promise promise) {
        MPPoint point = MPJsonParser.parse(pointString, MPPoint.class);
        MPGeometry geometry = MPJsonParser.parse(geometryString, MPGeometry.class);
        if (geometry != null && point != null) {
            if (MPGeometry.POLYGON.equals(geometry.getType()) ) {
                MPPolygonGeometry polygonGeometry = (MPPolygonGeometry) geometry;
                promise.resolve(polygonGeometry.getSquaredDistanceToClosestEdge(point));
            } else if (MPGeometry.MULTIPOLYGON.equals(geometry.getType())) {
                MPMultiPolygonGeometry multiPolygonGeometry = (MPMultiPolygonGeometry) geometry;
                promise.resolve(multiPolygonGeometry.getSquaredDistanceToClosestEdge(point));
            } else {
                reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Geometry of incompatible type: " + geometry.getType() +
                        " (should be " + MPGeometry.POLYGON + " or "+ MPGeometry.MULTIPOLYGON + ")"));
            }
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Point (" + point + ") or Geometry (" + geometry + ") is null"));
        }
    }

    @ReactMethod
    public void parseMapClientUrl(String venueId, String locationId, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            promise.resolve(solution.parseMapClientUrl(venueId, locationId));
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Solution is null, MapsIndoors is probably not ready yet"));
        }
    }

    @ReactMethod
    public void setCollisionHandling(Double collisionHandling, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            solution.getConfig().setCollisionHandling(MPCollisionHandling.fromValue(collisionHandling.intValue()));
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void enableClustering(boolean enable, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            solution.getConfig().setEnableClustering(enable);
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setNewSelection(boolean enable, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            solution.getConfig().setNewSelection(enable);
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setExtrusionOpacity(Double extrusionOpacity, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            solution.getConfig().getSettings3D().setExtrusionOpacity(extrusionOpacity.floatValue());
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setWallOpacity(Double wallOpacity, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            solution.getConfig().getSettings3D().setWallOpacity(wallOpacity.floatValue());
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void setSelectable(String id, boolean selectable, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            if (id.equals("solution")) {
                solution.getConfig().getLocationSettings().setSelectable(selectable);
            } else {
                MPLocation location = MapsIndoors.getLocationById(id);
                if (location != null) {
                    if (location.getLocationSettings() == null) {
                        location.setLocationSettings(new MPLocationSettings(selectable));
                    }else {
                        location.getLocationSettings().setSelectable(selectable);
                    }
                }else {
                    for (MPPOIType type : solution.getTypes()) {
                        if (type.getName().equals(id.toLowerCase(Locale.ROOT))) {
                            if (type.getLocationSettings() == null) {
                                type.setLocationSettings(new MPLocationSettings(selectable));
                            }else {
                                type.getLocationSettings().setSelectable(selectable);
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            reject(promise, new MIError(MIErrorEnum.UNKNOWN_ERROR, "Solution is null, MapsIndoors needs to be loaded before setting selectable"));
        }

        promise.resolve(null);
    }

    @ReactMethod
    public void setAutomatedZoomLimit(Double zoomLimit, final Promise promise) {
        MPSolution solution = MapsIndoors.getSolution();
        if (solution != null) {
            if (zoomLimit == -1) {
                solution.getConfig().setAutomatedZoomLimit(null);
            }else {
                solution.getConfig().setAutomatedZoomLimit(zoomLimit);
            }
        }
        promise.resolve(null);
    }
}
