package com.snaps.mobile.utils.sns.googlephoto;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public class GoogleAPITokenInfo {
    private static final String TAG = GoogleAPITokenInfo.class.getSimpleName();

    public static void refreshAccessTokenWithAuthCodeAsync(@NonNull final String serverAuthCode, @NonNull final GooglePhotoAPIListener googlePhotoAPIListener) {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {}

            @Override
            public void onBG() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(createGoogleAccessTokenRequest()).enqueue(googleAccessTokenCallBack);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {}

            private Callback googleAccessTokenCallBack = new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    googlePhotoAPIListener.onGooglePhotoAPIResult(false, null);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("access_token")) {
                            String accessToken = (String) jsonObject.get("access_token");
                            googlePhotoAPIListener.onGooglePhotoAPIResult(true, GooglePhotoAPIResult.initWithAuthInfo(serverAuthCode, accessToken));
                        } else {
                            googlePhotoAPIListener.onGooglePhotoAPIResult(false, null);
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        googlePhotoAPIListener.onGooglePhotoAPIResult(false, null);
                    }
                }
            };

            private RequestBody createGoogleAccessTokenRequestBody() {
                return  new FormEncodingBuilder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", GoogleApiConstants.GOOGLE_PHOTO_WEB_CLIENT_ID)
                        .add("client_secret", GoogleApiConstants.GOOGLE_PHOTO_CLIENT_SECRET)
                        .add("redirect_uri","")
                        .add("code", serverAuthCode)
                        .build();
            }

            private Request createGoogleAccessTokenRequest() {
                return new Request.Builder()
                        .url(GoogleApiConstants.GOOGLE_AUTH_TOKEN_REQUEST_URL)
                        .post(createGoogleAccessTokenRequestBody())
                        .build();
            }
        });
    }

    public static boolean isValidAccessToken() { //FIXME 토큰의 유효성을 체크할 수 있으면 좋을텐데..방법이 없는지
        return !StringUtil.isEmpty(PrefUtil.getGooglePhotoAcccessToken(ContextUtil.getContext()));
    }

    private static boolean isExistAccount() { //FIXME... 가능하면, 이것도 Preference에 저장해서 계속 호출 하지 않도록 처리 하자. name만 저장해서 날리수도 있다.
        GoogleSignInAccount apiClient = null;
        try {
            apiClient = GooglePhotoUtil.getGoogleSignInAccount();
            return apiClient != null && apiClient.getAccount() != null;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public static String getCurrentGoogleAuthAccessToken() {
        return PrefUtil.getGooglePhotoAcccessToken(ContextUtil.getContext());
    }

    public static void deleteGoogleAllAuthInfo(Context context) {
        PrefUtil.removeGooglePhotoAcccessToken(context);
        PrefUtil.removeGooglePhotoName(context);
        PrefUtil.removeGooglePhotoRefreshToken(context);
    }
}
