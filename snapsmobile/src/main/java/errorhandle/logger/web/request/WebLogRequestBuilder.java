package errorhandle.logger.web.request;

import java.util.HashMap;
import java.util.Map;

import errorhandle.logger.web.WebLogConstants;

public class WebLogRequestBuilder {
    public static WebLogRequestBuilder createBuilderWithLogName(WebLogConstants.eWebLogName logName) {
        return new WebLogRequestBuilder(logName);
    }

    private Map<WebLogConstants.eWebLogPayloadType, String> payloadMap = null;
    private WebLogConstants.eWebLogName logURI;

    public Map<WebLogConstants.eWebLogPayloadType, String> getPayloadMap() {
        return payloadMap;
    }

    public WebLogConstants.eWebLogName getLogURI() {
        return logURI;
    }

    private WebLogRequestBuilder(WebLogConstants.eWebLogName logName) {
        this.logURI = logName;
    }

    public WebLogRequestBuilder appendPayload(WebLogConstants.eWebLogPayloadType payloadType, String value) {
        if (payloadType == null) return this;
        if (this.payloadMap == null) this.payloadMap = new HashMap<>();
        this.payloadMap.put(payloadType, value);
        return this;
    }
}
