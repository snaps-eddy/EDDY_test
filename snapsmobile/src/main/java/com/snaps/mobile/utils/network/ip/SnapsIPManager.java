package com.snaps.mobile.utils.network.ip;

import com.snaps.common.http.APIConnection;
import com.snaps.common.http.ResponseDeviceIP;
import com.snaps.common.utils.log.Dlog;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SnapsIPManager {

    private static final String TAG = SnapsIPManager.class.getSimpleName();

    private SnapsIPManager() {
    }

    private static class LazyHolder {
        static final SnapsIPManager INSTANCE = new SnapsIPManager();
    }

    public static SnapsIPManager getInstance() {
        return SnapsIPManager.LazyHolder.INSTANCE;
    }

    private DevicePublicIP lastDeviceIP;

    public void setIPAddress() {
        Dlog.d("Request IP Address, Last ip is " + getIPAddress());
        APIConnection.getInstance().getIpCheckingAPI().getIPInfo()
                .subscribeOn(Schedulers.io())
                .singleOrError()
                .subscribe(new DisposableSingleObserver<ResponseDeviceIP>() {
                    @Override
                    public void onSuccess(ResponseDeviceIP deviceIPDTO) {
                        DevicePublicIP model = new DevicePublicIP();
                        model.mapFromNetwork(deviceIPDTO);
                        Dlog.d("New IP Address : " + model.getPublicIP());
                        lastDeviceIP = model;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Dlog.e(TAG, e);
                    }
                });
    }

    public String getIPAddress() {
        if (lastDeviceIP == null) {
            return "Not found Public ip address";
        }
        return lastDeviceIP.getPublicIP();
    }

    public String getISP() {
        if (lastDeviceIP == null) {
            return "Not found ISP";
        }
        return lastDeviceIP.getISP();
    }

    public String getDetailIPAddress() {
        if (lastDeviceIP == null) {
            return "Not found Public ip address";
        }
        return lastDeviceIP.getDetailInfo();
    }
}
