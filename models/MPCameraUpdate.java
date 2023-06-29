package com.reactlibrary.core.models;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPPoint;

public class MPCameraUpdate {
    @SerializedName("mode")
    String modeString;
    @SerializedName("point")
    public
    MPPoint point;
    @SerializedName("bounds")
    public
    Bounds bounds;
    @SerializedName("padding")
    public
    Integer padding;
    @SerializedName("width")
    public
    Integer width;
    @SerializedName("height")
    public
    Integer height;
    @SerializedName("zoom")
    public
    Float zoom;
    @SerializedName("position")
    public
    MPCameraPosition position;


    public CameraUpdateMode getMode() {
        return CameraUpdateMode.fromString(modeString);
    }

    /*
    */

    public enum CameraUpdateMode {
        FROM_POINT("fromPoint"),
        FROM_BOUNDS("fromBounds"),
        ZOOM_BY("zoomBy"),
        ZOOM_TO("zoomTo"),
        FROM_CAMERA_POSITION("fromCameraPosition");
        final String mode;
        CameraUpdateMode(String mode) {
            this.mode = mode;
        }

        public static CameraUpdateMode fromString(String value) {
            switch (value) {
                case "fromPoint":
                    return FROM_POINT;
                case "fromBounds":
                    return FROM_BOUNDS;
                case "zoomBy":
                    return ZOOM_BY;
                case "zoomTo":
                    return ZOOM_TO;
                case "fromCameraPosition":
                    return FROM_CAMERA_POSITION;
                default:
                    return null;
            }
        }

    }
}
