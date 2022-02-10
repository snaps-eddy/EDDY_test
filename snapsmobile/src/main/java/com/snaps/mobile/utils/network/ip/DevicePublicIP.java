package com.snaps.mobile.utils.network.ip;

import com.snaps.common.http.ResponseDeviceIP;

class DevicePublicIP {

    private String ip;

    private String location;

    private String hostName;

    private String ISP;

    private boolean usingTOR;

    private String countryCode;

    void mapFromNetwork(ResponseDeviceIP dto) {
        this.ip = dto.getPublicIP();
        this.location = dto.getLocation();
        this.hostName = dto.getHostName();
        this.ISP = dto.getISP();
        this.usingTOR = dto.isUsingTOR();
        this.countryCode = dto.getCountryCode();
    }

    String getPublicIP() {
        return ip == null ? "unknown" : ip;
    }

    String getISP() { return ISP == null ? "unknown" : ISP;}

    String getDetailInfo() {
        return "<< IP : [" + ip + "], ISP : [" + ISP + "], Location : [" + location + "], Using TOR : [" + usingTOR + "] >>";
    }
}
