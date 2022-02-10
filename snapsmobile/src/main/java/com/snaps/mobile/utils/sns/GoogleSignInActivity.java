package com.snaps.mobile.utils.sns;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.sns.googlephoto.GoogleAPITokenInfo;
import com.snaps.mobile.utils.sns.googlephoto.GoogleApiConstants;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIResult;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.eGooglePhotoLibResult;

import java.io.IOException;

import errorhandle.SnapsAssert;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.REQCODE_GOOGLE_SCOPE;
import static com.snaps.mobile.utils.sns.googlephoto.GoogleApiConstants.GOOGLE_PHOTO_LIBRARY_SCOPE;

/**
 * @author caesar
 */
public class GoogleSignInActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = GoogleSignInActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_photo_login);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    private void initialize() {
        initSignInButton();

        ImageView backBtn = (ImageView) findViewById(R.id.google_login_back_iv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initSignInButton() {
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_WIDE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            try {
                signIn();
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsAssert.assertException(this, e);
                MessageUtil.toast(this, R.string.failed_google_sign_in);
            }
        }
    }

    private void signIn() throws Exception {
        showProgressDialog();
        GoogleApiClient googleApiClient = GooglePhotoUtil.getGoogleApiClient();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GoogleApiConstants.REQUEST_CODE_FOR_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        hideProgressDialog();

        if (requestCode == GoogleApiConstants.REQUEST_CODE_FOR_GOOGLE_SIGN_IN) {
            try {
                handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
            } catch (Exception e) {
                Dlog.e(TAG, e);
                SnapsAssert.assertException(this, e);
                MessageUtil.toast(this, R.string.failed_google_sign_in);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) throws Exception {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct == null) throw new NullPointerException();

            GooglePhotoUtil.setGoogleSignInAccount(acct);
            PrefUtil.setGooglePhotoName(GoogleSignInActivity.this, acct.getDisplayName());
            refreshAccessTokenWithAuthCode(acct.getServerAuthCode());
            getGooglePhotoLibraryToken(new SnapsCommonResultListener<eGooglePhotoLibResult>() {
                @Override
                public void onResult(eGooglePhotoLibResult eGooglePhotoLibResult) {
                    try {
                        switch (eGooglePhotoLibResult) {
                            case SUCCESS:
                                sendForSuccessResultAndFinishActivity();
                                break;
                            default:
                                //FIXME... 실패했을 경우의 UI 처리 필요.
                                break;
                        }

                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        SnapsAssert.assertException(GoogleSignInActivity.this, e);
                        MessageUtil.toast(GoogleSignInActivity.this, R.string.failed_google_sign_in);
                    }

                }
            });
        } else {
            MessageUtil.toast(this, R.string.failed_google_sign_in);
        }
    }

    private void getGooglePhotoLibraryToken(SnapsCommonResultListener<eGooglePhotoLibResult> listener) {
        ATask.executeVoid(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    Account account;
                    GoogleSignInAccount apiClient = GooglePhotoUtil.getGoogleSignInAccount();
                    if (apiClient == null) {
                        if (listener != null) {
                            listener.onResult(eGooglePhotoLibResult.NOT_VALID_ACCOUNT);
                        }
                        return;
                    }

                    account = apiClient.getAccount();

                    String googleAuthToken = GoogleAuthUtil.getToken(GoogleSignInActivity.this,
                            account, GOOGLE_PHOTO_LIBRARY_SCOPE);

                    PrefUtil.setGooglePhotoAcccessToken(GoogleSignInActivity.this, googleAuthToken);
                    if (listener != null) {
                        listener.onResult(eGooglePhotoLibResult.SUCCESS);
                    }
                    return;
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                } catch (UserRecoverableAuthException e) {
                    if (listener != null) {
                        listener.onResult(eGooglePhotoLibResult.REQUEST_SCOPE);
                    }
                    startActivityForResult(e.getIntent(), REQCODE_GOOGLE_SCOPE); //FIXME... 요청 후 완료 되었을때,  ActivityForResult에서 처리해야 한다.
                    return;
                } catch (GoogleAuthException e) {
                    Dlog.e(TAG, e);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                if (listener != null) {
                    listener.onResult(eGooglePhotoLibResult.FAIL);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

    private void refreshAccessTokenWithAuthCode(String authCode) throws Exception {
        GoogleAPITokenInfo.refreshAccessTokenWithAuthCodeAsync(authCode, new GooglePhotoAPIListener() {
            @Override
            public void onPrepare() {
                showProgressDialog();
            }

            @Override
            public void onGooglePhotoAPIResult(boolean isSuccess, GooglePhotoAPIResult resultObj) {
                hideProgressDialog();
                if (isSuccess && resultObj != null) {
                    PrefUtil.setGooglePhotoAuthCode(GoogleSignInActivity.this, resultObj.getAuthCode());
                    PrefUtil.setGooglePhotoAcccessToken(GoogleSignInActivity.this, resultObj.getAuthAccessToken());


                } else {
                    MessageUtil.toast(GoogleSignInActivity.this, R.string.failed_google_sign_in);
                }
            }
        });
    }

    private void sendForSuccessResultAndFinishActivity() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.connecting_google_sign_in));
            mProgressDialog.setIndeterminate(true);
        }

        if (!isFinishing())
            mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
