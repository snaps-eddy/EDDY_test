package com.snaps.mobile.utils.sns.googlephoto;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIResult;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public class GoogleSignInHandler {
    private static final String TAG = GoogleSignInHandler.class.getSimpleName();
    private GoogleApiClient.OnConnectionFailedListener googleApiConnectFailedListener;
    private FragmentActivity fragmentActivity;

    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount googleSignInAccount;

    private GoogleSignInHandler() {}

    public static GoogleSignInHandler initWithGoogleConnectActivity(FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        GoogleSignInHandler signInHandler = new GoogleSignInHandler();
        signInHandler.fragmentActivity = fragmentActivity;
        signInHandler.googleApiConnectFailedListener = onConnectionFailedListener;
        return signInHandler;
    }

    public void finalizeInstance() throws Exception {
        googleApiClient = null;
        googleSignInAccount = null;
        fragmentActivity = null;
        googleApiConnectFailedListener = null;
    }

    /**
     * GoogleAPIClient를 초기화 해 줘야 어떤 작업이든 가능하다
     */
    public void initGoogleApiClient() throws Exception {
        initGoogleApiClientWithSignInOption(getDefaultGoogleSignInOption());
    }

    public void silentSignIn(@NonNull final GooglePhotoAPIListener googlePhotoAPIListener) throws Exception {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            OptionalPendingResult<GoogleSignInResult> optionalPendingResult;
            @Override
            public void onPre() {
                googlePhotoAPIListener.onPrepare();
            }

            @Override
            public void onBG() {
                optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

                //GoogleAPI 버그 인거 같은데 바로 호출하면 결과가 제대로 안 나와서 좀 쉬었다가 체크한다
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
                boolean result = false;
                GooglePhotoAPIResult googlePhotoAPIResult = null;
                if (optionalPendingResult != null && optionalPendingResult.isDone()) {
                    GoogleSignInResult signInResult = optionalPendingResult.get();
                    if (signInResult.isSuccess()) {
                        result = true;
                        setGoogleSignInAccount(signInResult.getSignInAccount());
                        googlePhotoAPIResult = GooglePhotoAPIResult.initWithAuthInfo(getGoogleSignInAccount().getServerAuthCode(), null);
                    }
                }

                googlePhotoAPIListener.onGooglePhotoAPIResult(result, googlePhotoAPIResult);
            }
        });
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private void initGoogleApiClientWithSignInOption(GoogleSignInOptions gso) throws Exception {
        googleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity , googleApiConnectFailedListener )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private GoogleSignInOptions getDefaultGoogleSignInOption() throws Exception {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(GoogleApiConstants.GOOGLE_PHOTO_WEB_CLIENT_ID)
                .requestScopes(new Scope(GoogleApiConstants.GOOGLE_PHOTO_SCOPE))
                .build();
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }

    public void signOut(final GooglePhotoAPIListener googlePhotoAPIListener) throws Exception {
        GoogleApiClient googleApiClient = GooglePhotoUtil.getGoogleApiClient();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        googlePhotoAPIListener.onGooglePhotoAPIResult(true, null);
                    }
                });
    }

    public void revokeAccess(final GooglePhotoAPIListener googlePhotoAPIListener) throws Exception {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        googlePhotoAPIListener.onGooglePhotoAPIResult(true, null);
                    }
                });
    }
}
