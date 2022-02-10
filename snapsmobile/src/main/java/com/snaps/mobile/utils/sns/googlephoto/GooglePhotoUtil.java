package com.snaps.mobile.utils.sns.googlephoto;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.GooglePhotoAPIListener;
import com.snaps.mobile.utils.sns.googlephoto.model.GooglePhotoImageListModel;

import java.util.ArrayList;

import static com.snaps.mobile.utils.network.retrofit2.interfacies.SnapsNetworkConstants.BEARER_PREFIX;

/**
 * Created by ysjeong on 2017. 5. 19..
 */

public class GooglePhotoUtil {
    private static final String TAG = GooglePhotoUtil.class.getSimpleName();
    private static volatile GooglePhotoUtil gInstance = null;

    private GoogleSignInHandler googleSignInHandler = null;

    private GooglePhotoImageRequester googlePhotoImageRequester = null;

    private GooglePhotoUtil() {
        setGooglePhotoImageRequester(new GooglePhotoImageRequester());
    }

    public static GooglePhotoUtil getInstance() {
        if(gInstance ==  null)
            createInstance();

        return gInstance;
    }

    public static void createInstance() {
        if (gInstance ==  null) {
            synchronized (SnapsOrderManager.class) {
                if (gInstance ==  null) {
                    gInstance = new GooglePhotoUtil();
                }
            }
        }
    }

    /**
     * 액비비티가 종료될 때 꼭 호출해 줄 것.
     */
    public static void finalizeInstance() {
        try {
            releaseAllInstances();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private static void releaseAllInstances() throws Exception {
        if (gInstance ==  null) return;

        if (gInstance.googleSignInHandler != null) {
            gInstance.googleSignInHandler.finalizeInstance();
            gInstance.googleSignInHandler = null;
        }

        if (gInstance.googlePhotoImageRequester != null) {
            gInstance.googlePhotoImageRequester.finalizeInstance();
            gInstance.googlePhotoImageRequester = null;
        }

        gInstance = null;
    }

    /**
     * 해당 유틸을 사용할 Activity의 onCreate에서 초기화 해 주도록 한다.
     */
    public static void initGoogleSign(FragmentActivity fragmentActivity, GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) throws Exception {
        setGoogleSignInHandler(GoogleSignInHandler.initWithGoogleConnectActivity(fragmentActivity, onConnectionFailedListener));

        getGoogleSignInHandler().initGoogleApiClient();
    }

    /**
     * 한번 SignIn 성공 한 이후에는 silentSignIn으로 로그인을 할 수 있다
     */
    public static void silentSignIn(GooglePhotoAPIListener googlePhotoAPIListener) throws Exception {
        getGoogleSignInHandler().silentSignIn(googlePhotoAPIListener);
    }

    public static void getAlbumList(Context context, boolean isFirst, SnapsCommonResultListener<ArrayList<IAlbumData>> listener) throws Exception {
        getGooglePhotoImageRequester().getAlbumList(context, isFirst, listener);
    }

    public static void getImageList(Context context, String albumurl,String nextKey, boolean isFirst, SnapsCommonResultListener<GooglePhotoImageListModel> listener) throws Exception {
        getGooglePhotoImageRequester().getImageList(context, albumurl,nextKey, isFirst, listener);
    }

    public static void signOut(GooglePhotoAPIListener listener) throws Exception {
        getGoogleSignInHandler().signOut(listener);
    }

    public static void setGoogleSignInAccount(GoogleSignInAccount account) {
        getGoogleSignInHandler().setGoogleSignInAccount(account);
    }

    public static GoogleApiClient getGoogleApiClient() throws Exception {
        return getGoogleSignInHandler().getGoogleApiClient();
    }

    public static GoogleSignInAccount getGoogleSignInAccount() throws Exception {
        return getGoogleSignInHandler().getGoogleSignInAccount();
    }

    private static GoogleSignInHandler getGoogleSignInHandler() {
        GooglePhotoUtil googlePhotoUtil = getInstance();
        return googlePhotoUtil.googleSignInHandler;
    }

    private static GooglePhotoImageRequester getGooglePhotoImageRequester() {
        GooglePhotoUtil googlePhotoUtil = getInstance();
        return googlePhotoUtil.googlePhotoImageRequester;
    }

    private static void setGoogleSignInHandler(GoogleSignInHandler signInHandler) {
        GooglePhotoUtil googlePhotoUtil = getInstance();
        googlePhotoUtil.googleSignInHandler = signInHandler;
    }

    public void setGooglePhotoImageRequester(GooglePhotoImageRequester googlePhotoImageRequester) {
        this.googlePhotoImageRequester = googlePhotoImageRequester;
    }

    public static String getBearerToken() {
        return BEARER_PREFIX + GoogleAPITokenInfo.getCurrentGoogleAuthAccessToken();
    }
}
