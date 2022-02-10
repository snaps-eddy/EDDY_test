package com.snaps.common.deeplink;

import android.content.Intent;
import android.net.Uri;

import com.snaps.common.utils.ui.StringUtil;

import java.util.HashMap;
import java.util.Set;

import static com.snaps.common.utils.constant.ISnapsConfigConstants.SNAPS_EXEC_GOTO_HOST;

public class DeeplinkService {

    /**
     * Ex. depp link snapsmobilekr://exec_goto?snapsapp://gotoPage?gototarget=P0014&prmchnlcode=KOR0031&url=https://kr.snaps.com/member/my
     *
     * @param intent
     */

    public void putExtraDataIfHostIsExecGoto(Intent intent) {
        if (intent == null) {
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            return;
        }

        String host = uri.getHost();
        if (host == null || !host.equalsIgnoreCase(SNAPS_EXEC_GOTO_HOST)) {
            return;
        }

        String dataString = uri.getQuery();
        if (dataString != null && !dataString.startsWith("snapsapp://gotoPage?")) {
            dataString = "snapsapp://gotoPage?" + dataString;
        }

        HashMap<String, String> parsed = StringUtil.parseUrl(dataString);

        if (parsed != null && !parsed.isEmpty()) {
            Set<String> keySet = parsed.keySet();
            intent.putExtra("fullurl", dataString);
            for (String key : keySet) {
                intent.putExtra(key, parsed.get(key));
            }
        }
    }

    public boolean isFitLanguageTarget(String prmChannelCode, String deviceChannelCode) {
        return deviceChannelCode.equalsIgnoreCase(prmChannelCode);
    }
}
