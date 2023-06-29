package com.reactlibrary.core;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.mapsindoors.core.MPDisplayRule;
import com.mapsindoors.core.MPIconSize;
import com.mapsindoors.core.MPSolutionDisplayRule;
import com.mapsindoors.core.MapsIndoors;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.errors.MIErrorEnum;
import com.reactlibrary.core.models.MPError;

public class MPDisplayRuleModule extends ReactContextBaseJavaModule {
    private final Gson gson = new Gson();

    public MPDisplayRuleModule(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "DisplayRule";
    }

    private void reject(Promise promise, String id) {
        promise.reject("DisplayRuleError",
                gson.toJson(MPError.fromMIError(
                        new MIError(MIErrorEnum.UNKNOWN_ERROR,
                                "The DisplayRule (id: "+ id +") cannot be found"))));
    }

    private MPDisplayRule getRule(String name) {
        switch (name) {
            case "buildingOutline" : return MapsIndoors.getDisplayRule(MPSolutionDisplayRule.BUILDING_OUTLINE);
            case "selectionHighlight": return MapsIndoors.getDisplayRule(MPSolutionDisplayRule.SELECTION_HIGHLIGHT);
            case "positionIndicator": return MapsIndoors.getDisplayRule(MPSolutionDisplayRule.POSITION_INDICATOR);
            default: return MapsIndoors.getDisplayRule(name);
        }
    }

    @ReactMethod
    public void isVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setVisible(String displayRuleId, boolean isVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setVisible(isVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isIconVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isIconVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setIconVisible(String displayRuleId, boolean iconVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setIconVisible(iconVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isPolygonVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isPolygonVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonVisible(String displayRuleId, boolean polygonVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonVisible(polygonVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isLabelVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isLabelVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelVisible(String displayRuleId, boolean labelVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelVisible(labelVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isModel2DVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isModel2DVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DVisible(String displayRuleId, boolean model2DVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DVisible(model2DVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isWallVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isWallVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallVisible(String displayRuleId, boolean wallVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallVisible(wallVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isExtrusionVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isExtrusionVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionVisible(String displayRuleId, boolean extrusionVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionVisible(extrusionVisible);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setZoomTo(zoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getIconUrl(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getIconUrl());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setIcon(String displayRuleId, String url, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setIcon(url);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getIconSize(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            String iconSizeString = gson.toJson(displayRule.getIconSize());
            promise.resolve(iconSizeString);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setIconSize(String displayRuleId, String iconSizeString, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            MPIconSize iconSize = gson.fromJson(iconSizeString, MPIconSize.class);
            displayRule.setIconSize(iconSize.getWidth(), iconSize.getHeight());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabel(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabel());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabel(String displayRuleId, String label, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabel(label);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelZoomTo(zoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelMaxWidth(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelMaxWidth());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelMaxWidth(String displayRuleId, Double labelMaxWidth, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelMaxWidth(labelMaxWidth.intValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonZoomTo(String displayRuleId, Double polygonZoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonZoomTo(polygonZoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonStrokeWidth(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonStrokeWidth());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonStrokeWidth(String displayRuleId, Double strokeWidth, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonStrokeWidth(strokeWidth.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonStrokeColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonStrokeColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonStrokeColor(String displayRuleId, String polygonStrokeColor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonStrokeColor(polygonStrokeColor);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonStrokeOpacity(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonStrokeOpacity());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonStrokeOpacity(String displayRuleId, Double polygonStrokeOpacity, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonStrokeOpacity(polygonStrokeOpacity.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonFillOpacity(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonFillOpacity());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonFillOpacity(String displayRuleId, Double polygonFillOpacity, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonFillOpacity(polygonFillOpacity.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonFillColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonFillColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonFillColor(String displayRuleId, String polygonFillColor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonFillColor(polygonFillColor);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getWallColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getWallColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallColor(String displayRuleId, String wallColor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallColor(wallColor);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getWallHeight(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getWallHeight());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallHeight(String displayRuleId, Double wallHeight, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallHeight(wallHeight.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getWallZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getWallZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getWallZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getWallZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallZoomTo(zoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getExtrusionColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getExtrusionColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionColor(String displayRuleId, String extrusionColor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionColor(extrusionColor);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getExtrusionHeight(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getExtrusionHeight());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionHeight(String displayRuleId, Double extrusionHeight, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionHeight(extrusionHeight.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getExtrusionZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getExtrusionZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionZoomFrom(zoomFrom.floatValue());
        } else {
            promise.resolve(null);
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getExtrusionZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getExtrusionZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionZoomTo(zoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DZoomTo(zoomTo.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DWidthMeters(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DWidthMeters());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DWidthMeters(String displayRuleId, Double width, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DWidthMeters(width);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DHeightMeters(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DHeightMeters());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DHeightMeters(String displayRuleId, Double height, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DHeightMeters(height);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DBearing(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DBearing());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DBearing(String displayRuleId, Double bearing, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DBearing(bearing);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getModel2DModel(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getModel2DModel());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setModel2DModel(String displayRuleId, String model, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DModel(model);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void reset(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.reset();
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

}
