package com.snaps.common.http;

import com.google.gson.annotations.SerializedName;

public class ResponseDeviceIP {

    @SerializedName("YourFuckingIPAddress")
    private String publicIP;

    @SerializedName("YourFuckingLocation")
    private String location;

    @SerializedName("YourFuckingHostname")
    private String hostName;

    @SerializedName("YourFuckingISP")
    private String ISP;

    @SerializedName("YourFuckingTorExit")
    private boolean usingTOR;

    @SerializedName("YourFuckingCountryCode")
    private String countryCode;

    public String getPublicIP() {
        return publicIP;
    }

    public String getLocation() {
        return location;
    }

    public String getHostName() {
        return hostName;
    }

    public String getISP() {
        return ISP;
    }

    public boolean isUsingTOR() {
        return usingTOR;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
