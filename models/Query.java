package com.reactlibrary.core.models;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.MPPoint;
import com.mapsindoors.core.MPQuery;

import java.util.List;

public class Query {
    @SerializedName("query")
    private String mQuery;
    @SerializedName("near")
    private MPPoint mNear;
    @SerializedName("queryProperties")
    private List<String> mQueryProperties;

    public MPQuery toMPQuery() {
        MPQuery.Builder builder = new MPQuery.Builder();
        if (mQuery != null) {
            builder.setQuery(mQuery);
        }
        if (mNear != null) {
            builder.setNear(mNear);
        }
        if (mQueryProperties != null) {
            builder.setQueryProperties(mQueryProperties);
        }
        return builder.build();
    }
}
