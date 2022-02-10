package com.snaps.mobile.activity.diary.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ysjeong on 16. 3. 15..
 */
public class SnapsDiaryListJson extends SnapsDiaryBaseResultJson {
    private static final long serialVersionUID = 3833645865598906672L;

    @SerializedName("PAGE_NO")
    private String pageNo;

    @SerializedName("PAGE_SIZE")
    private String pageSize;

    @SerializedName("A_COUNT")
    private String androidCount;

    @SerializedName("I_COUNT")
    private String iosCount;

    @SerializedName("TOTAL_COUNT")
    private String totalCount;

    @SerializedName("DIARY_LIST")
    private List<SnapsDiaryListItemJson> diaryList;

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public List<SnapsDiaryListItemJson> getDiaryList() {
        return diaryList;
    }

    public void setDiaryList(List<SnapsDiaryListItemJson> diaryList) {
        this.diaryList = diaryList;
    }

    public String getAndroidCount() {
        return androidCount;
    }

    public void setAndroidCount(String androidCount) {
        this.androidCount = androidCount;
    }

    public String getIosCount() {
        return iosCount;
    }

    public void setIosCount(String iosCount) {
        this.iosCount = iosCount;
    }
}
