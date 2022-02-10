package errorhandle.logger.web.executor;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONObject;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.WebLogUtil;
import errorhandle.logger.web.request.interfacies.WebLogRequestInfo;
import errorhandle.logger.web.request.payload.WebLogPayload;

public class WebLogSender extends Thread {
    private static final String TAG = WebLogSender.class.getSimpleName();
    public static WebLogSender createWebLogSenderWithRequestInfo(WebLogRequestInfo requestInfo) {
        return new WebLogSender(requestInfo);
    }

    private WebLogRequestInfo requestInfo;

    private WebLogSender(WebLogRequestInfo requestInfo) {
        this.setDaemon(true);
        this.requestInfo = requestInfo;
    }

    @Override
    public void run() {
        super.run();
        try {
            writePreLogcat();

            JSONObject requestParams = WebLogUtil.createWebLogParamJsonObj(requestInfo);
            int responseCode = WebLogUtil.sendSnapsWebLogWithJsonParams(requestParams, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            Dlog.d("run() send web Log result : " + responseCode);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    private void writePreLogcat() {
        Dlog.d("writePreLogcat() try send web Log : " + requestInfo.getLogName() +  " (" + requestInfo.getLogDescription() + ", " + requestInfo.getURI() + ", " + requestInfo.getInterfaceType() + ")");

        WebLogPayload payload = requestInfo.getPayload();
        if (payload != null) {
            try {
                Dlog.d("writePreLogcat() payload (" + getRequirePayloads(payload) +  ") : " + payload.getPayloadJsonStr());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private String getRequirePayloads(WebLogPayload payload) {
        StringBuilder payloads = new StringBuilder();
        WebLogConstants.eWebLogPayloadType[] arRequireParams = payload.getRequireParams();
        if (arRequireParams != null) {
            boolean isFirst = true;
            for (WebLogConstants.eWebLogPayloadType payloadType : arRequireParams) {
                if (!isFirst) {
                    payloads.append(", ");
                }
                payloads.append(payloadType.toString());
                isFirst = false;
            }
        }
        return payloads.toString();
    }
}
