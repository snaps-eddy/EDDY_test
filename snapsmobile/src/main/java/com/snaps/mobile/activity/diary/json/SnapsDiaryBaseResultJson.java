package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryBaseResultJson extends BaseResponse {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("message")
    private String message;

    @SerializedName("server_err_message")
    private String errMsg;

    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return getStatus() != null && getStatus().equals("00");
    }

    /**
     * saveFirstWriteDate 인터페이스를 호출 했을 때, 아래의 결과값(실패)이 나온다면, 일기 저장 시 isFail에 true 값을 넣어줘야 한다.
     */
    public boolean isDiaryReStartCode() {
        return getStatus() != null
                && (getStatus().equals(SnapsDiaryConstants.ERR_CODE_FAIL_SHORT_INK) || getStatus().equals(SnapsDiaryConstants.ERR_CODE_PASSED_EXPIRATION));
    }
}
