package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiarySeqResultJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("F_DIARY_NO")
    private String diaryNo;

    public String getDiaryNo() {
        return diaryNo;
    }

    public void setDiaryNo(String diaryNo) {
        this.diaryNo = diaryNo;
    }
}
