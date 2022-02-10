package com.snaps.mobile.activity.google_style_image_selector.ui.fragments;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsConfigManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.instagram.utils.instagram.Const;
import com.snaps.instagram.utils.instagram.InstagramApp;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.utils.pref.PrefUtil;
import com.snaps.mobile.utils.sns.GoogleSignInActivity;
import com.snaps.mobile.utils.sns.googlephoto.GoogleAPITokenInfo;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;
import com.snaps.mobile.utils.sns.googlephoto.interfacies.eGooglePhotoLibResult;

import java.io.IOException;
import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import font.FProgressDialog;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.REQCODE_GOOGLE_SCOPE;
import static com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC;
import static com.snaps.mobile.utils.sns.googlephoto.GoogleApiConstants.GOOGLE_PHOTO_LIBRARY_SCOPE;

public class SelectImageSrcFragment extends ImageSelectBaseFragment {
    private static final String TAG = SelectImageSrcFragment.class.getSimpleName();

    private FProgressDialog pd;
    private ImageSelectSNSData snsData = null;
    private LinearLayout layoutInstagram;
    private boolean isFirstLoad = true;

    public SelectImageSrcFragment() {
    }

    @Override
    public boolean isExistAlbumList() {
        return false;
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
    }

    @Override
    public IAlbumData getCurrentAlbumCursor() {
        return null;
    }

    @Override
    public void onUpdatedPhotoList(String imageKey) {
    }

    @Override
    public void onChangedAlbumCursor(IAlbumData cursor) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imageSelectActivityV2 = (ImageSelectActivityV2) getActivity();
        imageSelectActivityV2.setMaxImageCount();

        snsData = imageSelectActivityV2.getSNSData();

        View v = inflater.inflate(R.layout.fragment_select_imgsrc_, container, false);

        LinearLayout layoutPhone = v.findViewById(R.id.layoutPhone);
        LinearLayout layoutSnaps = v.findViewById(R.id.layoutSnaps);
        LinearLayout layoutKakao = v.findViewById(R.id.layoutKakao);
        LinearLayout layoutFacebook = v.findViewById(R.id.layoutFacebook);
        LinearLayout layoutSDKCustomer = v.findViewById(R.id.layout_sdk_customer);
        LinearLayout layoutBetweenCustomer = v.findViewById(R.id.layout_between_customer);
        LinearLayout layoutGooglePhoto = v.findViewById(R.id.layout_googlephoto);
        layoutInstagram = v.findViewById(R.id.layout_instagram);

        layoutFacebook.setOnClickListener(onClick);
        layoutPhone.setOnClickListener(onClick);
        layoutSDKCustomer.setOnClickListener(onClick);
        layoutKakao.setOnClickListener(onClick);
        layoutInstagram.setOnClickListener(onClick);
        layoutGooglePhoto.setOnClickListener(onClick);
        layoutSnaps.setOnClickListener(onClick);
        layoutBetweenCustomer.setOnClickListener(onClick);

        if (!imageSelectActivityV2.isSingleChooseType() && !imageSelectActivityV2.isMultiChooseType()) {
            View grayAreaView = v.findViewById(R.id.top_gray_layout);
            if (grayAreaView != null) grayAreaView.setVisibility(View.VISIBLE);
        }

        ImageView instaIcon = (ImageView) layoutInstagram.findViewById(R.id.instagram_icon);
        instaIcon.setVisibility(View.VISIBLE);
        instaIcon.setImageResource(R.drawable.pic_select_insta);

        if (Config.isSnapsSticker() && Config.getTMPL_CODE() == Config.TEMPLATE_STICKER_6) {
            layoutSnaps.setVisibility(View.VISIBLE);
        } else {
            layoutSnaps.setVisibility(View.GONE);
        }

        layoutSDKCustomer.setVisibility(View.GONE);
        layoutBetweenCustomer.setVisibility(View.GONE);

        if (PrefUtil.getGooglePhotoEnable(getActivity())) {
            layoutGooglePhoto.setVisibility(View.VISIBLE);
        }

        if (SnapsDiaryDataManager.isAliveSnapsDiaryService() || Config.isSnapsDiary()) {
            layoutFacebook.setVisibility(View.GONE);
            layoutKakao.setVisibility(View.GONE);
            layoutInstagram.setVisibility(View.GONE);
            layoutGooglePhoto.setVisibility(View.GONE);
        }

        if (!Config.useKorean()) layoutKakao.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 2020/04/08 ben
        // 사진 소스 선택 화면에서 폰 사진 리스트 로딩 속도 향상을 위해서 아래와 같이 처리한 듯 한데, 오버헤드만 커지고 그닥 효율은 없어 보임
        // 그래서 주석 처리 해 버림
        /*
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            imageSelectManager.createPhonePhotoDatas(imageSelectActivityV2, null);
        }
        */

        switchLastSelectedPhotoSourceFragment();
    }

    private void switchLastSelectedPhotoSourceFragment() {
        if (!isFirstLoad)
            return;

        if (SnapsConfigManager.isAutoLaunchProductMakingMode()) {
            if (!imageSelectActivityV2.isSingleChooseType() && !imageSelectActivityV2.isMultiChooseType()) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> changeFragmentToPhonePhoto(), 5000);//FIXME...템플릿 로딩하는 시점에 해야되는데...귀찮다...
                return;
            }
        }

        isFirstLoad = false;

        ISnapsImageSelectConstants.ePhotoSourceType sourceType = ImageSelectUtils.loadLastSelectedPhotoSourceOrdinal();
        if (sourceType == ISnapsImageSelectConstants.ePhotoSourceType.NONE) return;
        switch (sourceType) {
            case PHONE:
                changeFragmentToPhonePhoto();
                break;
            case FACEBOOK:
                changeFragmentToFacebookPhoto();
                break;
            case GOOGLE_PHOTO:
                changeToGooglePhotoFragment();
                break;
            case KAKAO_STORY:
                changeFragmentToKakaoStoryPhoto();
                break;
            case INSTAGRAM:
                changeFragmentToInstagramPhoto();
                break;
            case SNAPS_STICKER:
                changeFragmentToSnapsSticker();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageSelectActivityV2 != null) {
            imageSelectActivityV2.updateTitle(R.string.choose_photo);
        }
    }

    @Override
    public void onDestroy() {

        imageSelectActivityV2 = null;

        if (snsData != null) {
            snsData.clear();
        }

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            imageSelectManager.suspendCreatingPhonePhotoData();
        }

        super.onDestroy();
    }

    private void changeFragmentToFacebookPhoto() {
        imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_FACEBOOK);
        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.FACEBOOK);

        if (imageSelectActivityV2.isMultiChooseType()) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickAlbum)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(imageSelectActivityV2.getPageIndex()))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM, WebLogConstants.eWebLogPhotoAlbumType.FACEBOOK.getValue())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private void changeFragmentToGooglePhoto() {
        imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_GOOGLEPHOTO);
        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.GOOGLE_PHOTO);

        if (imageSelectActivityV2.isMultiChooseType()) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickAlbum)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(imageSelectActivityV2.getPageIndex()))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM, WebLogConstants.eWebLogPhotoAlbumType.GOOGLE_PHOTO.getValue())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private void changeFragmentToPhonePhoto() {
        imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_PHONE);
        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.PHONE);

        if (imageSelectActivityV2.isMultiChooseType()) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickAlbum)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(imageSelectActivityV2.getPageIndex()))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM, WebLogConstants.eWebLogPhotoAlbumType.PHONE.getValue())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private void changeFragmentToSnapsSticker() {
        imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_SNAPS);
        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.SNAPS_STICKER);
    }

    private void changeFragmentToInstagramPhoto() {
        InstagramApp insta = null;
        if (snsData != null) {
            if (snsData.getInstagram() != null) {
                insta = snsData.getInstagram();
            } else {
                insta = new InstagramApp(getActivity(), Const.CLIENT_ID, Const.CLIENT_SECRET, Const.REDIRECT_URI);
                insta.setListener(new InstagramApp.OAuthAuthenticationListener() {
                    @Override
                    public void onSuccess() {
                        onClick.onClick(layoutInstagram);
                    }

                    @Override
                    public void onFail(String error) {
                        MessageUtil.toast(getActivity(), getString(R.string.instagram_login_fail_msg));
                    }
                });
                snsData.setInstagram(insta);
            }
        }

        if (insta != null && insta.getId() != null && insta.getId().length() > 0) {

            ImageSelectSNSData commonData = imageSelectActivityV2.getSNSData();
            if (commonData != null) {
                commonData.setInstagram(insta);
            }
            imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_INSTAGRAM);
        } else if (insta != null) {
            insta.authorize();
        }

        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.INSTAGRAM);

        if (imageSelectActivityV2.isMultiChooseType()) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickAlbum)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(imageSelectActivityV2.getPageIndex()))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM, WebLogConstants.eWebLogPhotoAlbumType.INSTARGRAM.getValue())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private void changeFragmentToKakaoStoryPhoto() {
        imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_KAKAO);
        ImageSelectUtils.saveLastSelectedPhotoSourceOrdinal(ISnapsImageSelectConstants.ePhotoSourceType.KAKAO_STORY);

        if (imageSelectActivityV2.isMultiChooseType()) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_addphoto_clickAlbum)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(imageSelectActivityV2.getPageIndex()))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PHOTO_ALBUM, WebLogConstants.eWebLogPhotoAlbumType.KAKAO_STORY.getValue())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

            //현재 페이스북 계정 만료로 사진 가져오기 안됨
            if (v.getId() == R.id.layoutFacebook) {
                if (Config.IS_SUPPORT_FACEBOOK == false) {
                    MessageUtil.alert(getActivity(), R.string.facebook_not_support_msg);
                    return;
                }
            }

            //현재 인스타그램 계정 만료로 사진 가져오기 안됨
            if (v.getId() == R.id.layout_instagram) {
                if (Config.IS_SUPPORT_INSTAGRAM == false) {
                    MessageUtil.alert(getActivity(), R.string.instagram_not_support_msg);
                    return;
                }
            }

            if ((v.getId() == R.id.layoutFacebook || v.getId() == R.id.layoutKakao || v.getId() == R.id.layout_instagram || v.getId() == R.id.layout_googlephoto) && PrefUtil.showSnsBookAlert(getActivity())) {
                MessageUtil.showSnsAlert(getActivity());
                return;
            }
            try {
                if (v.getId() == R.id.layoutFacebook) {
                    if (Config.isFacebookService() == false) {
                        return;
                    }

                    //굳이 매번 init하지 않도록 수정 함.
                    IFacebook facebook = null;
                    if (snsData != null) {
                        if (snsData.getFacebook() != null) {
                            facebook = snsData.getFacebook();
                        } else {
                            facebook = SnsFactory.getInstance().queryInteface();
                            facebook.init(getActivity());
                            snsData.setFacebook(facebook);
                        }
                    }

                    if (facebook == null) {
                        return;
                    }

                    if (facebook.isFacebookLogin()) {
                        changeFragmentToFacebookPhoto();

                    } else {
//                        WebView webview = new WebView(imageSelectActivityV2);
//                        webview.resumeTimers();

                        facebook.facebookLoginChk(getActivity(), result -> {
                            changeFragmentToFacebookPhoto();
                            FacebookUtil.getProfileData(imageSelectActivityV2);
                        });
                    }

                } else if (v.getId() == R.id.layoutPhone) {
                    changeFragmentToPhonePhoto();
                } else if (v.getId() == R.id.layoutSnaps) {
                    changeFragmentToSnapsSticker();
                } else if (v.getId() == R.id.layout_sdk_customer) {
                    imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_SDK_CUSTOMER);
                } else if (v.getId() == R.id.layoutKakao) {
                    IKakao kakao = null;
                    if (snsData != null) {
                        if (snsData.getKakao() != null) {
                            kakao = snsData.getKakao();
                        } else {
                            kakao = SnsFactory.getInstance().queryIntefaceKakao();
                            snsData.setKakao(kakao);
                        }
                    }

                    if (kakao != null) {
                        if (kakao.isKakaoLogin()) {
                            changeFragmentToKakaoStoryPhoto();
                        } else {
                            kakao.startKakaoLoginActivity(imageSelectActivityV2);
                        }
                    }
                } else if (v.getId() == R.id.layout_between_customer) {
                    imageSelectActivityV2.onRequestedFragmentChange(SELECT_IMAGE_SRC, Const_VALUES.SELECT_BETWEEN);
                } else if (v.getId() == R.id.layout_instagram) {
                    changeFragmentToInstagramPhoto();
                } else if (v.getId() == R.id.layout_googlephoto) {
                    changeToGooglePhotoFragment();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    };

    private void changeToGooglePhotoFragment() {
        if (GoogleAPITokenInfo.isValidAccessToken()) {
            changeFragmentToGooglePhoto();

        } else {
            Intent itt = new Intent(imageSelectActivityV2, GoogleSignInActivity.class);
            imageSelectActivityV2.startActivityForResult(itt, ISnapsImageSelectConstants.REQCODE_GOOGLE_SIGN_IN);
        }
    }

    private void getGooglePhotoLibraryToken(SnapsCommonResultListener<eGooglePhotoLibResult> listener) {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
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

                    String googleAuthToken = GoogleAuthUtil.getToken(getContext(),
                            account, GOOGLE_PHOTO_LIBRARY_SCOPE);

                    PrefUtil.setGooglePhotoAcccessToken(getContext(), googleAuthToken);
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

    private void showPopupSuspendedGooglePhotoService() {
        MessageUtil.alertnoTitleOneBtn(imageSelectActivityV2, getString(R.string.googlephoto_block_popup_msg), null);
    }
}