package com.snaps.mobile.utils.sns.googlephoto.interfacies;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public class GooglePhotoAPIResult {

    private String authCode;
    private String authAccessToken;

    public static GooglePhotoAPIResult initWithAuthInfo(String authCode, String authAccessToken) {
        GooglePhotoAPIResult apiResult = new GooglePhotoAPIResult();
        apiResult.setAuthCode(authCode);
        apiResult.setAuthAccessToken(authAccessToken);
        return apiResult;
    }

    public String getAuthAccessToken() {
        return authAccessToken;
    }

    public void setAuthAccessToken(String authAccessToken) {
        this.authAccessToken = authAccessToken;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
