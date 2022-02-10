package errorhandle.logger.web.request.interfacies;

import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.payload.WebLogPayload;

public interface WebLogRequestInfo {
    WebLogConstants.eWebLogName getLogName();
    WebLogConstants.eWebLogPayloadType[] getRequirePayloads();
    String getLogDescription();
    WebLogPayload getPayload();
    String getURI();
    WebLogConstants.eWebLogInterfaceType getInterfaceType();
    WebLogConstants.eWebLogMethodType getMethodType(); //모바일에서는 의미가 없어 보인다.
    boolean shouldSendLogMessage();
    void putPayload(WebLogConstants.eWebLogPayloadType key, String value);
}
