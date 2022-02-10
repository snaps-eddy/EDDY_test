package com.snaps.mobile.activity.diary.recoder;

/**
 * Created by ysjeong on 16. 3. 21..
 */
public class SnapsDiaryPageInfo {
    private boolean isUsePaging = false; //페이징 사용 여부
    private int pagingNo = 0;
    private int pagingSize = 0;
    private String startDate = ""; //yyyyMMdd ex)20160311
    private String endDate = ""; //yyyyMMdd

    public boolean isUsePaging() {
        return isUsePaging;
    }

    public void setIsUsePaging(boolean isUsePaging) {
        this.isUsePaging = isUsePaging;
    }

    public String getPagingNoStr() {
        return String.valueOf(pagingNo);
    }

    public int getPagingNo() {
        return pagingNo;
    }

    public void setPagingNo(int pagingNo) {
        this.pagingNo = pagingNo;
    }

    public String getPagingSizeStr() {
        return String.valueOf(pagingSize);
    }

    public int getPagingSize() {
        return pagingSize;
    }

    public void setPagingSize(int pagingSize) {
        this.pagingSize = pagingSize;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
