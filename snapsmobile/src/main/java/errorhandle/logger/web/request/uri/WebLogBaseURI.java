package errorhandle.logger.web.request.uri;

import com.snaps.common.utils.ui.StringUtil;

import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.interfacies.WebLogRequestInfo;
import errorhandle.logger.web.request.payload.WebLogPayload;

import static errorhandle.logger.web.WebLogConstants.WEB_LOG_ANNIE_PREFIX;

public abstract class WebLogBaseURI implements WebLogRequestInfo {

    private WebLogPayload payload = null;

    private WebLogConstants.eWebLogName logName;

    private WebLogConstants.eWebLogInterfaceType interfaceType;

    private WebLogConstants.eWebLogPayloadType[] arRequirePayloads = null;

    private String uri;

    private String logDescription;

    private boolean shouldSendLogMessage = false;

    protected abstract void initHook();

    public WebLogBaseURI(WebLogConstants.eWebLogName logName) {
        init(logName);
    }

    private void init(WebLogConstants.eWebLogName logName) {
        this.logName = logName;

        createURIPathByLogName();

        initHook();
    }

//    public WebLogBaseURI addURI(String uri) {
//        this.uri = uri;
//        return this;
//    }

    public WebLogBaseURI forceSendLogMessage() {
        this.shouldSendLogMessage = true;
        return this;
    }

    public void setShouldSendLogMessage(boolean shouldSendLogMessage) {
        this.shouldSendLogMessage = shouldSendLogMessage;
    }

    public WebLogBaseURI addRequirePayloads(WebLogConstants.eWebLogPayloadType... payloadType) {
        this.arRequirePayloads = payloadType;
        createPayload();
        return this;
    }

    public WebLogBaseURI addLogDescription(String logDescription) {
        this.logDescription = logDescription;
        return this;
    }

    public WebLogBaseURI addInterfaceType(WebLogConstants.eWebLogInterfaceType interfaceType) {
        this.interfaceType = interfaceType;
        return this;
    }

    @Override
    public WebLogConstants.eWebLogName getLogName() {
        return logName;
    }

    @Override
    public String getURI() {
        return this.uri != null ? this.uri : "";
    }

    @Override
    public WebLogPayload getPayload() {
        return payload;
    }

    private void createURIPathByLogName() {
        if (logName == null) return;

        String logStr = logName.toString();
        if (StringUtil.isEmpty(logStr)) return;

        if (logStr.contains("_RES") || logStr.contains("_REQ")) {
            logStr = logStr.substring(0, logStr.lastIndexOf("_"));
        }

        uri = WEB_LOG_ANNIE_PREFIX + logStr.replace("_", "/");
    }

    public void createPayload() {
        WebLogConstants.eWebLogPayloadType[] arPayloads = getRequirePayloads();
        if (arPayloads == null || arPayloads.length < 1) return;
        this.payload = WebLogPayload.createPayloadWithRequireParams(arPayloads);
    }

    @Override
    public WebLogConstants.eWebLogPayloadType[] getRequirePayloads() {
        return arRequirePayloads != null ? arRequirePayloads : new WebLogConstants.eWebLogPayloadType[] {};
    }

    @Override
    public WebLogConstants.eWebLogInterfaceType getInterfaceType() {
        return interfaceType != null ? interfaceType : WebLogConstants.eWebLogInterfaceType.REQ;
    }

    @Override
    public WebLogConstants.eWebLogMethodType getMethodType() {
        return WebLogConstants.eWebLogMethodType.POST;
    }

    @Override
    public void putPayload(WebLogConstants.eWebLogPayloadType key, String value) {
        if (payload == null) return;
        payload.putPayload(key, value);
    }

    @Override
    public String getLogDescription() {
        return logDescription != null ? logDescription : "";
    }

    @Override
    public boolean shouldSendLogMessage() {
        return shouldSendLogMessage;
    }
}
