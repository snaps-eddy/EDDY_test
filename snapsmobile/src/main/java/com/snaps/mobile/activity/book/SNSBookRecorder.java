package com.snaps.mobile.activity.book;

import android.content.Context;
import com.snaps.common.utils.ui.UIUtil;

/**
 * Created by songhw on 2016. 3. 28..
 */

public abstract class SNSBookRecorder {

    // control 복사
    // 영역 설정
    // value 설정..
    // 텍스트 사이즈 계산하기.
    public static class ControlFixInfo {
        Context context;
        boolean isTextHalfPositionUp;
        int fixPosX;
        int fixPosY;
        int fixTextSize;

        public ControlFixInfo(Context con) {
            context = con;
        }

        public void set(boolean half, int fixX, int fixY, int fontSize) {
            isTextHalfPositionUp = half;
            fixPosX = (int) UIUtil.convertPXtoDP(context, fixX);
            fixPosY = (int) UIUtil.convertPXtoDP(context, fixY);
            fixTextSize = fontSize;
        }
    }

    public static class SNSBookTemplateBgRes {
        private String leftResPath;
        private String centerResPath;
        private String rightResPath;

        private String leftResId;
        private String centerResId;
        private String rightResId;

        public String getLeftResPath() {
            return leftResPath;
        }
        public void setLeftResPath(String leftResPath) {
            this.leftResPath = leftResPath;
        }
        public String getCenterResPath() {
            return centerResPath;
        }
        public void setCenterResPath(String centerResPath) {
            this.centerResPath = centerResPath;
        }
        public String getRightResPath() {
            return rightResPath;
        }
        public void setRightResPath(String rightResPath) {
            this.rightResPath = rightResPath;
        }
        public String getLeftResId() {
            return leftResId;
        }
        public void setLeftResId(String leftResId) {
            this.leftResId = leftResId;
        }
        public String getCenterResId() {
            return centerResId;
        }
        public void setCenterResId(String centerResId) {
            this.centerResId = centerResId;
        }
        public String getRightResId() {
            return rightResId;
        }
        public void setRightResId(String rightResId) {
            this.rightResId = rightResId;
        }
    }

    public static class SNSBookInfo {

        private String thumbUrl;
        private String userName;
        private String period;
        private String coverType;
        private String paperType;
        private String pageCount;
        private String priceOrigin;
        private String priceSale;
        private boolean isMaxPageEdited = false; //최대페이지(401)초과여부

        public static int getPageCount( int pagesSize ) { return ( pagesSize - 2 ) * 2 + 1; }

        public String getThumbUrl() {
            return thumbUrl;
        }
        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }
        public String getUserName() {
            return userName;
        }
        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getPeriod() {
            return period;
        }
        public void setPeriod(String period) {
            this.period = period;
        }
        public String getCoverType() {
            return coverType;
        }
        public void setCoverType(String coverType) {
            this.coverType = coverType;
        }
        public String getPageCount() {
            return pageCount;
        }
        public void setPageCount(String pageCount) {
            this.pageCount = (Integer.parseInt(pageCount) - 2) * 2 + 1 + "";
        }
        public String getPriceOrigin() {
            return priceOrigin;
        }
        public void setPriceOrigin(String priceOrigin) {
            this.priceOrigin = priceOrigin;
        }
        public String getPriceSale() {
            return priceSale;
        }
        public void setPriceSale(String priceSale) {
            this.priceSale = priceSale;
        }

        public boolean getMaxPageEdited() { return isMaxPageEdited; }
        public void setMaxPageEdited( boolean flag ) { this.isMaxPageEdited = flag; }
        public void setPaperType(String paperType) { this.paperType = paperType;}
        public String getPaperType() { return paperType;}
    }
}
