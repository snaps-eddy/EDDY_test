package errorhandle.logger.web.request.payload;

import com.snaps.common.utils.log.Dlog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import errorhandle.logger.Logg;
import errorhandle.logger.web.WebLogConstants;

public class WebLogPayload {
    private static final String TAG = WebLogPayload.class.getSimpleName();
    public static WebLogPayload createPayloadWithRequireParams(WebLogConstants.eWebLogPayloadType... requireDesc) {
        return new WebLogPayload(requireDesc);
    }

    private WebLogPayload() {}

    private WebLogConstants.eWebLogPayloadType[] requireParams;
    private Map<String, String> payloadJsonMap = null;

    private WebLogPayload(WebLogConstants.eWebLogPayloadType... requireDesc) {
        this.requireParams = requireDesc;
    }

    public void putPayload(WebLogConstants.eWebLogPayloadType key, String value) {
        if (payloadJsonMap == null) payloadJsonMap = new HashMap<>();
        payloadJsonMap.put(key.getKeyName(), value);
    }

    public String getPayloadJsonStr() throws Exception {
        if (payloadJsonMap == null) return "";

        if (!checkAllRequireInfoInserted()) {
//            SnapsAssert.assertTrue(false);
        }

        try {
            JSONObject jsonObject = new JSONObject(payloadJsonMap);
            return jsonObject.toString();
        } catch (Exception e) { Dlog.e(TAG, e); }
        return "";
    }

    private boolean checkAllRequireInfoInserted() throws Exception {
        if (payloadJsonMap == null || requireParams == null) return false;

        Set<String> payloadKeySet = payloadJsonMap.keySet();
        for (WebLogConstants.eWebLogPayloadType payloadType : requireParams) {
            if (payloadType == null) continue;

            boolean isExistPayload = false;
            for (String payloadKey : payloadKeySet) {
                String keyName = payloadType.getKeyName();
                if (keyName != null && keyName.equalsIgnoreCase(payloadKey)) {
                    isExistPayload = true;
                    break;
                }
            }

            if (!isExistPayload) return false;
        }

        return true;
    }

    public WebLogConstants.eWebLogPayloadType[] getRequireParams() {
        return requireParams;
    }
}
