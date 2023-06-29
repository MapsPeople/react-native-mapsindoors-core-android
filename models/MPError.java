package com.reactlibrary.core.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.mapsindoors.core.errors.MIError;

public class MPError {
    @NonNull
    @SerializedName("code")
    private final Integer mCode;
    @NonNull
    @SerializedName("message")
    private final String mMessage;
    @Nullable
    @SerializedName("status")
    private final Integer mStatus;
    @Nullable
    @SerializedName("tag")
    private final Object mTag;

    MPError(int code, @NonNull String message, @Nullable Integer status, @Nullable Object tag) {
        mCode = code;
        mMessage = message;
        mStatus = status;
        mTag = tag;
    }

    @Nullable
    public static MPError fromMIError(@Nullable MIError miError) {
        if (miError != null) {
            return new MPError(miError.code, miError.message, miError.status, miError.tag);
        } else {
            return null;
        }
    }
}
