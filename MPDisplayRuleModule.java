package com.mapsindoorsrn.core;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.mapsindoors.core.MPBadgePosition;
import com.mapsindoors.core.MPDisplayRule;
import com.mapsindoors.core.MPIconPlacement;
import com.mapsindoors.core.MPIconSize;
import com.mapsindoors.core.MPLabelType;
import com.mapsindoors.core.MPSolutionDisplayRule;
import com.mapsindoors.core.MapsIndoors;
import com.mapsindoors.core.errors.MIError;
import com.mapsindoors.core.errors.MIErrorEnum;
import com.mapsindoorsrn.core.models.MPError;

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
    public void setVisible(String displayRuleId, String isVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setVisible(Boolean.valueOf(isVisible));
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
    public void setIconVisible(String displayRuleId, String iconVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setIconVisible(Boolean.valueOf(iconVisible));
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
    public void setPolygonVisible(String displayRuleId, String polygonVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setPolygonVisible(Boolean.valueOf(polygonVisible));
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
    public void setLabelVisible(String displayRuleId, String labelVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelVisible(Boolean.valueOf(labelVisible));
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
    public void setModel2DVisible(String displayRuleId, String model2DVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setModel2DVisible(Boolean.valueOf(model2DVisible));
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
    public void setWallVisible(String displayRuleId, String wallVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setWallVisible(Boolean.valueOf(wallVisible));
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
    public void setExtrusionVisible(String displayRuleId, String extrusionVisible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setExtrusionVisible(Boolean.valueOf(extrusionVisible));
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
            if (zoomFrom == -1) {
                displayRule.setZoomFrom(null);
            }else {
                displayRule.setZoomFrom(zoomFrom.floatValue());
            }
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
            if (zoomTo == -1) {
                displayRule.setZoomTo(null);
            }else {
                displayRule.setZoomTo(zoomTo.floatValue());
            }
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
            if (zoomFrom == -1) {
                displayRule.setLabelZoomFrom(null);
            }else {
                displayRule.setLabelZoomFrom(zoomFrom.floatValue());
            }
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
            if (zoomTo == -1) {
                displayRule.setLabelZoomTo(null);
            }else {
                displayRule.setLabelZoomTo(zoomTo.floatValue());
            }
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
            if (labelMaxWidth == -1) {
                displayRule.setLabelMaxWidth(null);
            }else {
                displayRule.setLabelMaxWidth(labelMaxWidth.intValue());
            }
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
            if (zoomFrom == -1) {
                displayRule.setPolygonZoomFrom(null);
            }else {
                displayRule.setPolygonZoomFrom(zoomFrom.floatValue());
            }
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
            if (polygonZoomTo == -1) {
                displayRule.setPolygonZoomTo(null);
            }else {
                displayRule.setPolygonZoomTo(polygonZoomTo.floatValue());
            }
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
            if (strokeWidth == -1) {
                displayRule.setPolygonStrokeWidth(null);
            }else {
                displayRule.setPolygonStrokeWidth(strokeWidth.floatValue());
            }
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
            if (polygonStrokeOpacity == -1) {
                displayRule.setPolygonStrokeOpacity(null);
            }else {
                displayRule.setPolygonStrokeOpacity(polygonStrokeOpacity.floatValue());
            }
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
            if (polygonFillOpacity == -1) {
                displayRule.setPolygonFillOpacity(null);
            }else {
                displayRule.setPolygonFillOpacity(polygonFillOpacity.floatValue());
            }
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
            if (wallHeight == -1) {
                displayRule.setWallHeight(null);
            }else {
                displayRule.setWallHeight(wallHeight.floatValue());
            }
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
            if (zoomFrom == -1) {
                displayRule.setWallZoomFrom(null);
            }else {
                displayRule.setWallZoomFrom(zoomFrom.floatValue());
            }
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
            if (zoomTo == -1) {
                displayRule.setWallZoomTo(null);
            }else {
                displayRule.setWallZoomTo(zoomTo.floatValue());
            }
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
            if (extrusionHeight == -1) {
                displayRule.setExtrusionHeight(null);
            }else {
                displayRule.setExtrusionHeight(extrusionHeight.floatValue());
            }
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
            if (zoomFrom == -1) {
                displayRule.setExtrusionZoomFrom(null);
            }else {
                displayRule.setExtrusionZoomFrom(zoomFrom.floatValue());
            }
            promise.resolve(null);
        } else {
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
            if (zoomTo == -1) {
                displayRule.setWallZoomTo(null);
            }else {
                displayRule.setExtrusionZoomTo(zoomTo.floatValue());
            }
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
            if (zoomFrom == -1) {
                displayRule.setModel2DZoomFrom(null);
            }else {
                displayRule.setModel2DZoomFrom(zoomFrom.floatValue());
            }
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
            if (zoomTo == -1) {
                displayRule.setModel2DZoomTo(null);
            }else {
                displayRule.setModel2DZoomTo(zoomTo.floatValue());
            }
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
            if (width == -1) {
                displayRule.setModel2DWidthMeters(null);
            }else {
                displayRule.setModel2DWidthMeters(width);
            }
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
            if (height == -1) {
                displayRule.setModel2DHeightMeters(null);
            }else {
                displayRule.setModel2DHeightMeters(height);
            }
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
            if (bearing == -1) {
                displayRule.setModel2DBearing(null);
            }else {
                displayRule.setModel2DBearing(bearing);
            }
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
    public void getIconScale(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getIconScale());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setIconScale(String displayRuleId, Double scale, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (scale == -1) {
                displayRule.setIconScale(null);
            }else {
                displayRule.setIconScale(scale.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getIconPlacement(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getIconPlacement() != null ? displayRule.getIconPlacement().ordinal() : null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setIconPlacement(String displayRuleId, Integer placement, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (placement == -1) {
                displayRule.setIconPlacement(null);
            }else {
                MPIconPlacement iconPlacement;
                switch (placement) {
                    case 1:
                        iconPlacement = MPIconPlacement.ABOVE;
                        break;
                    case 2:
                        iconPlacement = MPIconPlacement.BELOW;
                        break;
                    case 3:
                        iconPlacement = MPIconPlacement.LEFT;
                        break;
                    case 4:
                        iconPlacement = MPIconPlacement.RIGHT;
                        break;
                    default:
                        iconPlacement = MPIconPlacement.CENTER;
                        break;
                }
                displayRule.setIconPlacement(iconPlacement);
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelType(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelType() != null ? displayRule.getLabelType().ordinal() : null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelType(String displayRuleId, Integer type, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (type == -1) {
                displayRule.setLabelType(null);
            }else {
                MPLabelType labelType;
                switch (type) {
                    case 1:
                        labelType = MPLabelType.FLAT;
                        break;
                    default:
                        labelType = MPLabelType.FLOATING;
                        break;
                }
                displayRule.setLabelType(labelType);
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleTextSize(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleTextSize());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleTextSize(String displayRuleId, Double textSize, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (textSize == -1) {
                displayRule.setLabelStyleTextSize(null);
            }else {
                displayRule.setLabelStyleTextSize(textSize.intValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleTextColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleTextColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleTextColor(String displayRuleId, String color, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelStyleTextColor(color);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleTextOpacity(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleTextOpacity());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleTextOpacity(String displayRuleId, Double opacity, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (opacity == -1) {
                displayRule.setLabelStyleTextOpacity(null);
            }else {
                displayRule.setLabelStyleTextOpacity(opacity.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleHaloColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleHaloColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleHaloColor(String displayRuleId, String color, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setLabelStyleHaloColor(color);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleHaloWidth(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleHaloWidth());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleHaloWidth(String displayRuleId, Double width, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (width == -1) {
                displayRule.setLabelStyleHaloWidth(null);
            }else {
                displayRule.setLabelStyleHaloWidth(width.intValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleHaloBlur(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleHaloBlur());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleHaloBlur(String displayRuleId, Double blur, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (blur == -1) {
                displayRule.setLabelStyleHaloBlur(null);
            }else {
                displayRule.setLabelStyleHaloBlur(blur.intValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getLabelStyleBearing(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getLabelStyleBearing());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setLabelStyleBearing(String displayRuleId, Double bearing, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (bearing == -1) {
                displayRule.setLabelStyleBearing(null);
            }else {
                displayRule.setLabelStyleBearing(bearing.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getPolygonLightnessFactor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getPolygonLightnessFactor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setPolygonLightnessFactor(String displayRuleId, Double factor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (factor == -1) {
                displayRule.setPolygonLightnessFactor(null);
            }else {
                displayRule.setPolygonLightnessFactor(factor.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getWallLightnessFactor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getWallLightnessFactor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setWallLightnessFactor(String displayRuleId, Double factor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (factor == -1) {
                displayRule.setWallLightnessFactor(null);
            }else {
                displayRule.setWallLightnessFactor(factor.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getExtrusionLightnessFactor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getExtrusionLightnessFactor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setExtrusionLightnessFactor(String displayRuleId, Double factor, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (factor == -1) {
                displayRule.setExtrusionLightnessFactor(null);
            }else {
                displayRule.setExtrusionLightnessFactor(factor.floatValue());
            }
            displayRule.setExtrusionLightnessFactor(factor.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void isBadgeVisible(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.isBadgeVisible());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeVisible(String displayRuleId, String visible, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setBadgeVisible(Boolean.valueOf(visible));
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeZoomFrom(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeZoomFrom());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeZoomFrom(String displayRuleId, Double zoomFrom, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (zoomFrom == -1) {
                displayRule.setBadgeZoomFrom(null);
            }else {
                displayRule.setBadgeZoomFrom(zoomFrom.floatValue());
            }
            displayRule.setBadgeZoomFrom(zoomFrom.floatValue());
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeZoomTo(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeZoomTo());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeZoomTo(String displayRuleId, Double zoomTo, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (zoomTo == -1) {
                displayRule.setBadgeZoomTo(null);
            }else {
                displayRule.setBadgeZoomTo(zoomTo.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeRadius(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeRadius());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeRadius(String displayRuleId, Double radius, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (radius == -1) {
                displayRule.setBadgeRadius(null);
            }else {
                displayRule.setBadgeRadius(radius.intValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeStrokeWidth(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeStrokeWidth());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeStrokeWidth(String displayRuleId, Double strokeWidth, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (strokeWidth == -1) {
                displayRule.setBadgeStrokeWidth(null);
            }else {
                displayRule.setBadgeStrokeWidth(strokeWidth.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeStrokeColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeStrokeColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeStrokeColor(String displayRuleId, String color, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setBadgeStrokeColor(color);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeFillColor(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeFillColor());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeFillColor(String displayRuleId, String color, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            displayRule.setBadgeFillColor(color);
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgeScale(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            promise.resolve(displayRule.getBadgeScale());
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgeScale(String displayRuleId, Double scale, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            if (scale == -1) {
                displayRule.setBadgeScale(null);
            }else {
                displayRule.setBadgeScale(scale.floatValue());
            }
            promise.resolve(null);
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void getBadgePosition(String displayRuleId, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            MPBadgePosition badgePosition = displayRule.getBadgePosition();
            if (badgePosition != null) {
                if (badgePosition.equals(MPBadgePosition.bottomLeft)) {
                    promise.resolve(1);
                }else if (badgePosition.equals(MPBadgePosition.bottomRight)) {
                    promise.resolve(0);
                }else if (badgePosition.equals(MPBadgePosition.topLeft)) {
                    promise.resolve(2);
                }else {
                    promise.resolve(3);
                }
            }else {
                promise.resolve(null);
            }
        } else {
            reject(promise, displayRuleId);
        }
    }

    @ReactMethod
    public void setBadgePosition(String displayRuleId, Integer position, final Promise promise) {
        MPDisplayRule displayRule = getRule(displayRuleId);
        if (displayRule != null) {
            MPBadgePosition badgePosition;
            switch (position) {
                case 1:
                    badgePosition = MPBadgePosition.bottomLeft;
                    break;
                case 2:
                    badgePosition = MPBadgePosition.topLeft;
                    break;
                case 3:
                    badgePosition = MPBadgePosition.topRight;
                    break;
                default:
                    badgePosition = MPBadgePosition.bottomRight;
                    break;
            }
            displayRule.setBadgePosition(badgePosition);
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
