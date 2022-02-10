package com.snaps.mobile.activity.diary.json;

import com.google.gson.Gson;
import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryGsonUtil {
    private static final String TAG = SnapsDiaryGsonUtil.class.getSimpleName();
    public static SnapsDiaryBaseResultJson getParsedGsonData(String jsonStr, Class<?> clazz) {
        if(jsonStr == null) return null;

        Gson gson = new Gson();
        SnapsDiaryBaseResultJson resultInfo = null;

        try {
            SnapsDiaryBaseResultJson baseInfo = gson.fromJson(jsonStr, SnapsDiaryBaseResultJson.class);
            if(baseInfo == null) {
                return null;
            }

            if(!baseInfo.isSuccess()) {
                Dlog.w(TAG, "getParsedGsonData() error:" + baseInfo.getErrMsg());
            }

            JSONObject jObj = new JSONObject(jsonStr);
            JSONObject resultObj = (JSONObject) jObj.get("result");
            if(resultObj != null) {
                resultInfo = (SnapsDiaryBaseResultJson) gson.fromJson(resultObj.toString(), clazz);
                if(resultInfo != null) {
                    resultInfo.setErrMsg(baseInfo.getErrMsg());
                    resultInfo.setMessage(baseInfo.getMessage());
                    resultInfo.setStatus(baseInfo.getStatus());
                }
            }
        } catch (JSONException e) {
            Dlog.e(TAG, e);
        }

        return resultInfo;
    }

}
