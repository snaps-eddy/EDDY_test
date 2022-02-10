package errorhandle.logger;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.mobile.utils.network.ip.SnapsIPManager;

/**
 * Created by ysjeong on 2017. 11. 20..
 */

public class SnapsInterfaceLogDefaultHandler implements SnapsInterfaceLogListener {
    private static final String TAG = SnapsInterfaceLogDefaultHandler.class.getSimpleName();

    public static SnapsInterfaceLogDefaultHandler createDefaultHandler() {
        return new SnapsInterfaceLogDefaultHandler();
    }

    private SnapsInterfaceLogDefaultHandler() {
    }

    @Override
    public void onSnapsInterfacePreRequest(String url) {
        Dlog.d("onSnapsInterfacePreRequest() url:" + url);
        SnapsLogger.appendInterfaceUrlLog(url + getIPInfo());
    }

    @Override
    public void onSnapsInterfaceResult(int httpResponseStatusCode, String responseText) {
        try {
            Dlog.d("onSnapsInterfaceResult() StatusCode:" + httpResponseStatusCode + ", responseText:" + responseText);
            if (httpResponseStatusCode != 200)
                SnapsLogger.sendInterfaceResultLog("interface result is fail.", responseText + getIPInfo());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.sendInterfaceResultLog("interface exception.", e.toString() + getIPInfo());
        }
    }

    @Override
    public void onSnapsInterfaceException(Exception e) {
        try {
            if (e != null)
                SnapsLogger.sendInterfaceResultLog("interface exception...", e.toString() + getIPInfo());
        } catch (Exception e2) {
            Dlog.e(TAG, e2);
        }
    }

    private String getIPInfo() {
        return " * IP Info : " + SnapsIPManager.getInstance().getDetailIPAddress();
    }
}
