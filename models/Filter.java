package com.reactlibrary.core.models;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPFilter;
import com.mapsindoors.core.MPMapExtend;
import com.mapsindoors.core.models.MPLatLngBounds;

import java.util.List;

public class Filter {
    @SerializedName("take")
    private Integer mTake;
    @SerializedName("skip")
    private Integer mSkip;
    @SerializedName("depth")
    private Integer mDepth;
    @SerializedName("floorIndex")
    private Integer mFloorIndex;
    @SerializedName("categories")
    private List<String> mCategories;
    @SerializedName("locations")
    private List<String> mLocations;
    @SerializedName("types")
    private List<String> mTypes;
    @SerializedName("parents")
    private List<String> mParents;
    @SerializedName("mapExtend")
    private Bounds mMapExtend;
    @SerializedName("geometry")
    private Bounds mGeometry;
    @SerializedName("ignoreSearchableStatus")
    private Boolean mIgnoreSearchableStatus;
    @SerializedName("ignoreActiveStatus")
    private Boolean mIgnoreActiveStatus;

    public MPFilter toMPFilter() {
        MPFilter.Builder builder = new MPFilter.Builder();
        if (mTake != null) {
            builder.setTake(mTake);
        }
        if (mSkip != null) {
            builder.setSkip(mSkip);
        }
        if (mDepth != null) {
            builder.setDepth(mDepth);
        }
        if (mFloorIndex != null) {
            builder.setFloorIndex(mFloorIndex);
        }
        if (mCategories != null) {
            builder.setCategories(mCategories);
        }
        if (mLocations != null) {
            builder.setLocations(mLocations);
        }
        if (mTypes != null) {
            builder.setTypes(mTypes);
        }
        if (mParents != null) {
            builder.setParents(mParents);
        }
        if (mMapExtend != null) {
            builder.setMapExtend(new MPMapExtend(mMapExtend.getSouthWest().getLatLng(), mMapExtend.getNorthEast().getLatLng()));
        }
        if (mGeometry != null) {
            MPLatLngBounds bounds = new MPLatLngBounds(mGeometry.getSouthWest().getLatLng(), mGeometry.getNorthEast().getLatLng());
            builder.setGeometry(new MPFilter.Geometry(bounds.getCenter().getLat(), bounds.getCenter().getLng(), new MPMapExtend(bounds)));
        }
        if (mIgnoreSearchableStatus != null) {
            builder.setIgnoreLocationSearchableStatus(mIgnoreSearchableStatus);
        }
        if (mIgnoreActiveStatus != null) {
            builder.setIgnoreLocationActiveStatus(mIgnoreActiveStatus);
        }
        return builder.build();
    }
}
