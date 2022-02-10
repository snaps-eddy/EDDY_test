package com.snaps.mobile.utils.network.retrofit2.data.response.common;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.snaps.common.utils.ui.StringUtil;

public class SnapsNetworkAPIBaseResponse {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;

    private String resultString = null;

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String result) {
        this.resultString = result;
    }

    public SnapsNetworkAPIBaseResponse() {

    }

    protected SnapsNetworkAPIBaseResponse(Parcel in) {
        status = in.readString();
        message = in.readString();
        errorMessage = in.readString();
        errorCode = in.readString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return isEmptyErrorCode() || isSuccessStateCode();
    }

    private boolean isSuccessStateCode() {
        return getStatus() != null && getStatus().equalsIgnoreCase("200");
    }

    private boolean isEmptyErrorCode() {
        return StringUtil.isEmpty(getErrorCode());
    }
}
