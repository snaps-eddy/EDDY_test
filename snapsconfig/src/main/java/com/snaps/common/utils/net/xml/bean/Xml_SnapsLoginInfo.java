package com.snaps.common.utils.net.xml.bean;

public class Xml_SnapsLoginInfo {
    public String F_USER_NO;
    public String F_USER_ID;
    public String F_USER_NAME;
    public String F_USER_MAIL;
    public String F_USER_LVL;
    public String F_EVENT_TERM;// 이벤트 여부...
    public String F_COUPON; // 로그인 쿠폰 발급 참여 여부...
    public String F_REVIEW;// 리뷰 이벤트 참여 여부
    public String F_DEVICE;// 이벤트에 참여한 경우 FALSE
    public String F_FILE_PATH;// 이벤트에 이미지 경로
    public String F_USER_AUTH; // 추가인증 여부
    public String F_USER_PHONENUMBER;//추가인증시 전화번호
    public String F_RETURN_CODE = null;
    public String F_RETURN_MSG = null;
    public boolean F_USER_AI_SYNC = false;
    public boolean F_USER_AI_TOS_AGREE = false;
    public boolean isServerError = false;

    public Xml_SnapsLoginInfo(String f_USER_NO,
                              String f_USER_ID,
                              String f_USER_NAME,
                              String f_USER_MAIL,
                              String f_USER_LVL,
                              String F_EVENT_TERM,
                              String F_COUPON,
                              String F_REVIEW,
                              String F_DEVICE,
                              String F_FILE_PATH,
                              String F_USER_AUTH,
                              String F_USER_PHONENUMBER,
                              String f_RETURN_CODE,
                              String F_USER_AI_SYNC,
                              String F_USER_AI_TOS_AGREE) {
        F_USER_NO = f_USER_NO;
        F_USER_ID = f_USER_ID;
        F_USER_NAME = f_USER_NAME;
        F_USER_MAIL = f_USER_MAIL;
        F_USER_LVL = f_USER_LVL;

        // 이벤트 정보.. 추가...
        this.F_EVENT_TERM = F_EVENT_TERM;
        this.F_COUPON = F_COUPON;
        this.F_REVIEW = F_REVIEW;
        this.F_DEVICE = F_DEVICE;
        this.F_FILE_PATH = F_FILE_PATH;
        this.F_USER_AUTH = F_USER_AUTH;
        this.F_USER_PHONENUMBER = F_USER_PHONENUMBER;
        this.F_USER_AI_SYNC = "y".equalsIgnoreCase(F_USER_AI_SYNC);
        this.F_USER_AI_TOS_AGREE = "y".equalsIgnoreCase(F_USER_AI_TOS_AGREE);
        this.F_RETURN_CODE = f_RETURN_CODE;
    }

    public Xml_SnapsLoginInfo(String f_RETURN_CODE, String f_RETURN_MSG) {
        this.F_RETURN_CODE = f_RETURN_CODE;
        this.F_RETURN_MSG = f_RETURN_MSG;
    }

    public Xml_SnapsLoginInfo(boolean isServerError) {
        this.isServerError = isServerError;
    }

    /**/
}
