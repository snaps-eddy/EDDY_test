package com.snaps.kakao.utils.kakao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryImageInfo;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.exception.KakaoException;
import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.CustomFragment;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.kakao.R;
import com.snaps.kakao.utils.kakao.custom.SnapsSnsInviteParams;
import com.snaps.kakao.utils.share.ISNSShareConstants;
import com.snaps.kakao.utils.share.SNSShareContentsStruct;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContentsKakaoStory;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContentsKakaoStory.eSnsShareKakaoStoryType;
import com.snaps.kakao.utils.share.SNSShareUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint("HandlerLeak")
public class KaKaoUtil extends IKakao {
    private static final String TAG = KaKaoUtil.class.getSimpleName();
    public static Context mContext;
    public static String klastId = "";

    int loadimgCount = 28;

    //	private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private KakaoLink kakaoLink;
    CustomFragment customFragment;

    public KaKaoUtil(String str) {
        //@marko ?????? ???????????? ??? ???????????? ???????????? ????????? ????????? ??????.
        this();
    }

    public KaKaoUtil() {
        super();
    }

    public boolean isKakaoLogin() {
        return isLogin();
    }

    public void onKakaoClickLogout() {
        onClickLogout();
    }

    public void startKakaoLoginActivity(Context context) {
        Intent intent = new Intent(context, KakaoLoginActivity.class);
        context.startActivity(intent);
    }

    public void initKakoLoginButton() {

    }

    public static void initSDK(Context context) {
        mContext = context;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config??? ???????????? default????????? ????????????.
         * ????????? ??????????????? override?????? ???????????? ???.
         *
         * @return Session??? ?????????.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return mContext;
                }
            };
        }
    }

    private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

        @Override
        public void onNotKakaoStoryUser() {
            Toast.makeText(mContext.getApplicationContext(), "not a KakaoStory user", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Toast.makeText(mContext.getApplicationContext(), "failed : " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
        }

        @Override
        public void onNotSignedUp() {
        }
    }

//    public void getRequestKakao(String lastid, final int photoCount, final ArrayAdapter<MyKakaoStoryImageData> adapter, final CustomFragment _customFragment) {
//        ArrayList<MyKakaoStoryImageData> dataList = new ArrayList<MyKakaoStoryImageData>();
//        getRequestKakao(lastid, photoCount, adapter, _customFragment, dataList);
//    }

    @Override
    public void getRequestKakao(String lastid, final int photoCount, final IKakao.ISNSPhotoHttpHandler handler, final ArrayList<ImageSelectSNSImageData> dataList) {

        KakaoStoryService.getInstance().requestGetMyStories(new KakaoStoryResponseCallback<List<MyStoryInfo>>() {
            @Override
            public void onSuccess(List<MyStoryInfo> myStories) {
                int imageCount = 0;
                String compareMediaID = "";

                for (MyStoryInfo info : myStories) {

                    compareMediaID = info.getId();

                    if (info.getImageInfoList() == null)
                        continue;

                    int i = 0;
                    for (MyStoryImageInfo imgInfo : info.getImageInfoList()) {

                        ImageSelectSNSImageData image = new ImageSelectSNSImageData();

                        String context = info.getContent();

                        if (context == null)
                            context = "";

                        if (!context.equals("Not Supported Media Type.")) {

                            image.setId(info.getId() + i);

                            image.setContent(context);

                            image.setOrgImageUrl(imgInfo.getOriginal());
                            image.setThumbnailImageUrl(imgInfo.getMedium());

                            image.setStrCreateAt(info.getCreatedAt());

                            image.setlCreateAt(convertStrDateToLongDate(info.getCreatedAt()));

                            if (image.getOrgImageUrl().contains("width")) {
                                image.setOrgImageWidth(StringUtil.getTitleAtUrl(imgInfo.getOriginal(), "width", true));
                                image.setOrgImageHeight(StringUtil.getTitleAtUrl(imgInfo.getOriginal(), "height", true));
                            }

                            i++;
                            imageCount++;

                            dataList.add(image);
                        }

                    }
                }

                klastId = compareMediaID;

                if (photoCount + imageCount >= loadimgCount || myStories.size() == 0) {
                    final int imgCnt = imageCount;
                    final int stories = myStories.size();
                    final String compareID = compareMediaID;

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPreExecute() {

                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                ArrayList<ImageSelectSNSImageData> errorList = new ArrayList<ImageSelectSNSImageData>();
                                for (ImageSelectSNSImageData d : dataList) {
                                    //?????? ???????????? ?????????
                                    if (!d.getOrgImageHeight().equals("")) {
                                        Rect rect = HttpUtil.getNetworkImageRect(d.getOrgImageUrl());

                                        //rect null?????? ???????????? ???????????? ????????????
                                        if (rect == null) {
                                            errorList.add(d);
                                            continue;
                                        }

                                        d.setOrgImageWidth(rect.width() + "");
                                        d.setOrgImageHeight(rect.height() + "");
                                        //add width, height
                                        //???????????? ???????????? url??? ????????? ??????
                                        d.setOrgImageUrl(StringUtil.addUrlParameter(d.getOrgImageUrl(), "width=" + rect.width()));
                                        d.setOrgImageUrl(StringUtil.addUrlParameter(d.getOrgImageUrl(), "height=" + rect.height()));
                                    }
                                }

                                //???????????? ???????????? ?????? ???????????? ?????? ????????? ????????? ??????
                                for (ImageSelectSNSImageData d : errorList) {
                                    dataList.remove(d);
                                }

                                errorList.clear();


                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            try {
                                if (handler != null) {
                                    handler.onSNSPhotoHttpResult(dataList);
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }

                        ;
                    }.execute();
                } else {
                    getRequestKakao(compareMediaID, (imageCount + photoCount), handler, dataList);
                }
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                if (handler != null) {
                    handler.onSNSPhotoError(errorResult.getErrorCode(), errorResult.getErrorMessage());
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if (handler != null) {
                    handler.onSNSPhotoError(errorResult.getErrorCode(), errorResult.getErrorMessage());
                }
            }

            @Override
            public void onNotKakaoStoryUser() {
                if (handler != null) {
                    handler.onSNSPhotoError(-999, "Not kakao Story User");
                }
            }

            @Override
            public void onNotSignedUp() {
                if (handler != null) {
                    handler.onSNSPhotoError(-990, "Not singed up");
                }
            }

        }, lastid);
    }

    private long convertStrDateToLongDate(String createAt) {
        if (StringUtil.isEmpty(createAt)) return 0;
        //kakao
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());
            Date date = dateFormat.parse(createAt);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

//    public void getRequestKakao(String lastid, final int photoCount, final ArrayAdapter<MyKakaoStoryImageData> adapter, final CustomFragment _customFragment, final ArrayList<MyKakaoStoryImageData> dataList) {
//
//        KakaoStoryService.getInstance().requestGetMyStories(new KakaoStoryResponseCallback<List<MyStoryInfo>>() {
//            @Override
//            public void onSuccess(List<MyStoryInfo> myStories) {
//                int imageCount = 0;
//                String compareMediaID = "";
//
//                for (MyStoryInfo info : myStories) {
//
//                    compareMediaID = info.getId();
//
//                    if (info.getImageInfoList() == null)
//                        continue;
//
//                    int i = 0;
//                    for (MyStoryImageInfo imgInfo : info.getImageInfoList()) {
//
//                        MyKakaoStoryImageData image = new MyKakaoStoryImageData();
//
//                        String context = info.getContent();
//
//                        if (context == null)
//                            context = "";
//
//                        if (!context.equals("Not Supported Media Type.")) {
//
//                            image.ID = info.getId() + i;
//
//                            image.CONTENT = context;
//
//                            image.ORIGIN_IMAGE_DATA = imgInfo.getOriginal();
//                            image.THUMBNAIL_IMAGE_DATA = imgInfo.getMedium();
//
//                            image.setCreateAt(info.getCreatedAt());
//
//                            if (image.ORIGIN_IMAGE_DATA.contains("width")) {
//                                // ?????????????????? ???????????? ?????? ??????
//                                // image.ORIGIN_IMAGE_WIDTH = imgInfo.getOriginWidth();
//                                // image.ORIGIN_IMAGE_HEIGHT = imgInfo.getOriginHeight();
//                                image.ORIGIN_IMAGE_WIDTH = StringUtil.getTitleAtUrl(imgInfo.getOriginal(), "width", true);
//                                image.ORIGIN_IMAGE_HEIGHT = StringUtil.getTitleAtUrl(imgInfo.getOriginal(), "height", true);
//                            } else {
//                                image.ORIGIN_IMAGE_WIDTH = "640";
//                                image.ORIGIN_IMAGE_HEIGHT = "640";
//                            }
//
//                            i++;
//                            imageCount++;
//
////							adapter.add(image);
//                            dataList.add(image);
//
//                        }
//
//                    }
//                }
//
//                klastId = compareMediaID;
//
//                // _customFragment.updateUI(imageCount, myStories.length, compareMediaID);
//
//                // mContext.updateUI();
//                // updateUI();
//                // lastID = compareMediaID;
//
//                if (imageCount + photoCount >= loadimgCount || myStories.size() == 0) {
//                    //????????? ????????? ?????? ????????? ????????? ??????!
//                    //arrayAdapter??? ????????? ?????? ????????? notifyDataSetChanged() ????????????.
//                    adapter.addAll(dataList);
//
//
//                    _customFragment.updateUI(imageCount, myStories.size(), compareMediaID);
//
//                } else {
//                    getRequestKakao(compareMediaID, imageCount + photoCount, adapter, _customFragment, dataList);
//                }
//
//                // _size = imageCount;
//
//            }
//        }, lastid);
//    }

    public String createKakaoInstance(Context context) {
        return null;
    }

    public void sendKakaoData(Context context) {
//		try {
//
////			assignMessageBuilder();
////			// kakaoTalkLinkMessageBuilder.addText("[?????????] ?????????????????? ?????????????????? ???????????? ?????? ????????? ?????????!").addAppButton(
////			kakaoTalkLinkMessageBuilder.addText("[?????????]\n????????????/?????????/?????????\n??????????????? 1??? ????????????!\n\n" + "???????????? ???????????? ??????????????? ?????? ?????????, ??????, ??????, ?????????????????? ??? " + "????????? ??????????????? ????????? ????????? ???????????? ???????????????!").addAppButton(
////					"????????? ??????",
////					new AppActionBuilder().addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam("execparamkey1=1111").setMarketParam("referrer=kakaotalklink").build())
////							.addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder().setExecuteParam("execparamkey1=1111").build()).build());
////
////			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, context);
//
//		} catch (KakaoParameterException e) {
//			Dlog.e(TAG, e);
//
//		}

    }

    public void assignMessageBuilder() {
    }


    public void initializeSession(Context context) {

    }

    public static boolean isLogin() {
        Session session = Session.getCurrentSession();
        return session != null && session.isOpened();
    }

    public static void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
            }
        });
    }

    @Override
    public String getLastID() {
        return klastId;
    }

    //????????? ???????????? ?????? ??????
    public boolean sendPostingLink(Activity activity, SNSShareContents contents, IPostingResult listener) {

        if (activity == null || contents == null || !(contents instanceof SNSShareContentsKakaoStory))
            return false;

        if (SNSShareUtil.isInstalledApp(activity, ISNSShareConstants.PACKAGE_NAME_KAKAO_STORY)) {
            SNSShareContentsKakaoStory shareContents = (SNSShareContentsKakaoStory) contents;

            if (!isLogin()) {
                startKakaoLoginActivity(activity);
                return false;
            }

            if (shareContents.getPostType() == eSnsShareKakaoStoryType.BTYPE) {
                sendPostingLink((Activity) shareContents.getContext(),
                        shareContents.getLink(), shareContents.getSubject(), shareContents.getDescription(), shareContents.getImgUrl(), "article");
                return true;
            }

            requestPostLink(shareContents.getLink(), shareContents.getDescription(), listener);

        } else {
            SNSShareUtil.gotoGooglePlay(activity, ISNSShareConstants.PACKAGE_NAME_KAKAO_STORY);
            return false;
        }

        return true;
    }

    //??????????????? ?????? ??????
    public void sendProjectShareLink(Context context, SNSShareContents contents, IPostingResult listener) {

        if (context == null || contents == null || !(contents instanceof SNSShareContentsStruct.SNSShareContentsKakaoTalk))
            return;

        if (SNSShareUtil.isInstalledApp(context, ISNSShareConstants.PACKAGE_NAME_KAKAO_TALK)) {
            SNSShareContentsStruct.SNSShareContentsKakaoTalk shareContents = (SNSShareContentsStruct.SNSShareContentsKakaoTalk) contents;
            try {

                boolean result = true;
                String linkUrl = shareContents.getLink();
                if (TextUtils.isEmpty(linkUrl)) {
                    String[] link = shareContents.getSubject().split("\n");
                    linkUrl = link[1];
                    if (!linkUrl.startsWith("http")) {
                        linkUrl = "m.snaps.kr";
                    }
                }
                TextTemplate textTemplate = TextTemplate.newBuilder(shareContents.getSubject(), LinkObject.newBuilder().setWebUrl(linkUrl).setMobileWebUrl(linkUrl).build()).build();
                if (listener != null)
                    listener.OnPostingComplate(result, null);
                sendDefault(context, textTemplate);
            } catch (NullPointerException e) {
                Dlog.e(TAG, e);
                if (listener != null)
                    listener.OnPostingComplate(false, e.toString());
            }
        } else {
            SNSShareUtil.gotoGooglePlay(context, ISNSShareConstants.PACKAGE_NAME_KAKAO_TALK);
        }
    }

    public void sendInviteMessage(Context context, String text, String url, String urlText, String imagUrl, String imgWidth, String imgHeight, String isRunApp, String executeParams) {

        try {
            SnapsSnsInviteParams params = new SnapsSnsInviteParams.Builder()
                    .setText(text)
                    .setImgUrl(imagUrl)
                    .setUrlText(urlText)
                    .setImgWidth(imgWidth)
                    .setImgHeight(imgHeight)
                    .setIsRunapp(isRunApp)
                    .setExcuteParam(executeParams)
                    .create();
            feedTemplateWithUrlData(context, params);
        } catch (KakaoException e) {
            Dlog.e(TAG, e);
        }
    }

    public void sendInviteMessage(Context context, String text, String url, String urlText, String imagUrl, String imgWidth, String imgHeight, String isRunApp) {
        try {

            SnapsSnsInviteParams params = new SnapsSnsInviteParams.Builder()
                    .setText(text)
                    .setImgUrl(imagUrl)
                    .setUrlText(urlText)
                    .setImgWidth(imgWidth)
                    .setImgHeight(imgHeight)
                    .setIsRunapp(isRunApp)
                    .create();
            feedTemplateWithUrlData(context, params);
        } catch (KakaoException e) {
            Dlog.e(TAG, e);
        }
    }

    public void sendInviteFriend(Context context, String content, String buttonTitle, String imagUrl, String imgWidth, String imgHeight, String parameter) {
        try {
            SnapsSnsInviteParams params = new SnapsSnsInviteParams.Builder()
                    .setText(content)
                    .setImgUrl(imagUrl)
                    .setImgWidth(imgWidth)
                    .setImgHeight(imgHeight)
                    .setUrlText(buttonTitle)
                    .setExcuteParam(parameter)
                    .create();
            feedTemplateWithUrlData(context, params);
        } catch (KakaoException e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean sendPostingLink(Activity activity, String url, String title, String desc, String imgUrl, String type) {

        Map<String, Object> urlInfoAndroid = new Hashtable<String, Object>(1);
        if (title == null) title = "";
        if (desc == null) desc = "";
        if (imgUrl == null) imgUrl = "";

        urlInfoAndroid.put("title", title);
        urlInfoAndroid.put("desc", desc);
        urlInfoAndroid.put("imageurl", new String[]{imgUrl});
        urlInfoAndroid.put("type", type);

        // Recommended: Use application context for parameter.
        StoryLink storyLink = StoryLink.getLink(activity.getApplicationContext());

        // check, intent is available.
        if (!storyLink.isAvailableIntent()) {
            return false;
        }

        /**
         * @param activity
         * @param post
         *            (message or url)
         * @param appId
         * @param appVer
         * @param appName
         * @param encoding
         * @param urlInfoArray
         */
        try {
            storyLink.openKakaoLink(activity, url, activity.getPackageName(), activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName, "snaps", "UTF-8", urlInfoAndroid);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return false;
        }

        return true;
    }

    public void sendPostingLink(String url, String content, IPostingResult listener) {
        requestPostLink(url, content, listener);
    }

    private void requestPostLink(final String scrapUrl, final String content, final IPostingResult listener) {

        try {
            KakaoStoryService.getInstance().requestPostLink(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo myStoryInfo) {
                    if (myStoryInfo.getId() != null) {
                        // ??????
                        if (listener != null)
                            listener.OnPostingComplate(true, null);
                    } else {
                        // ??????
                        if (listener != null)
                            listener.OnPostingComplate(false, null);
                    }
                }
            }, scrapUrl, content, PostRequest.StoryPermission.PUBLIC, true, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>());
        } catch (KakaoParameterException e) {
            Dlog.e(TAG, e);
            // ??????????????? ?????? ??????
            if (listener != null)
                listener.OnPostingComplate(false, e.toString());
        }
    }

    public void sendPostingPhoto(final List<File> files, final String content, IPostingResult listener) {
        requestPostPhoto(files, content, listener);
    }

    void requestPostPhoto(final List<File> files, final String content, final IPostingResult listener) {
        try {
            KakaoStoryService.getInstance().requestPostPhoto(new KakaoStoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSuccess(MyStoryInfo myStoryInfo) {
                    if (myStoryInfo.getId() != null) {
                        // ??????
                        if (listener != null)
                            listener.OnPostingComplate(true, null);
                    } else {
                        // ??????
                        if (listener != null)
                            listener.OnPostingComplate(false, null);
                    }
                }
            }, files, content);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            // ????????? ????????? ??????
        }
    }

    public boolean feedTemplateWithUrlData(Context context, SnapsSnsInviteParams snapsSnsInviteParams) {
        if (context == null || snapsSnsInviteParams == null) return false;

        if (snapsSnsInviteParams.getExcuteParam() != null) {
            return feedTemplateWithExecuteParam(context, snapsSnsInviteParams);
        }

        return feedTemplateSelectiveWithExecute(context, snapsSnsInviteParams);
    }

    private boolean feedTemplateWithExecuteParam(Context context, SnapsSnsInviteParams snapsSnsInviteParams) {
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("SNAPS", //???????????? ????????? ????????? ??????.
                        snapsSnsInviteParams.getImgUrl(),
                        LinkObject.newBuilder().setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam())
                                .setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
                                .setWebUrl(snapsSnsInviteParams.getOpenurl())
                                .setMobileWebUrl(snapsSnsInviteParams.getOpenurl()).build())
                        .setDescrption(snapsSnsInviteParams.getText())
                        .setImageWidth(Integer.parseInt(snapsSnsInviteParams.getImgWidth()))
                        .setImageHeight(Integer.parseInt(snapsSnsInviteParams.getImgHeight()))
                        .build())
                .addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam())
                        .setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
                        .build()))
                .build();
        return sendDefault(context, params);
    }

    private boolean sendDefault(Context context, TemplateParams templateParams) {
        try {

            KakaoLinkService.getInstance().sendDefault(context, templateParams, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    ErrorResult errorResult1 = errorResult;
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    KakaoLinkResponse response = result;
                }
            });
            return true;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    private boolean feedTemplateSelectiveWithExecute(Context context, SnapsSnsInviteParams snapsSnsInviteParams) {
        ContentObject contentObject = ContentObject.newBuilder("SNAPS", //???????????? ????????? ????????? ??????.
                snapsSnsInviteParams.getImgUrl(),
                LinkObject.newBuilder().setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam()).setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl()).build())
                .setDescrption(snapsSnsInviteParams.getText())
                .setImageWidth(Integer.parseInt(snapsSnsInviteParams.getImgWidth()))
                .setImageHeight(Integer.parseInt(snapsSnsInviteParams.getImgHeight()))
                .build();

        FeedTemplate.Builder feedBuilder = new FeedTemplate.Builder(contentObject);
        if (!StringUtil.isEmpty(snapsSnsInviteParams.getUrlText())) {
            if (snapsSnsInviteParams.getIsRunapp() != null && snapsSnsInviteParams.getIsRunapp().equalsIgnoreCase("true")) {
                feedBuilder.addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setAndroidExecutionParams(snapsSnsInviteParams.getExcuteParam())
                        .setIosExecutionParams(snapsSnsInviteParams.getExcuteParam())
                        .build()));
            } else {
                feedBuilder.addButton(new ButtonObject(snapsSnsInviteParams.getUrlText(), LinkObject.newBuilder()
                        .setWebUrl(snapsSnsInviteParams.getOpenurl())
                        .setMobileWebUrl(snapsSnsInviteParams.getOpenurl())
                        .build()));
            }
        }
        return sendDefault(context, feedBuilder.build());
    }

    public void kakaoTalkPost(Activity act, String projCode) {
        try {
            // Recommended: Use application context for parameter.
            KakaoLink kakaoLink = KakaoLink.getLink(act.getApplicationContext());

            // check, intent is available.
            if (!kakaoLink.isAvailableIntent()) {
                alert(act, act.getResources().getString(R.string.kakaotalk_install_fail));
                return;
            }

            ArrayList<Map<String, String>> metaInfoArray = null;

            if (!Config.getPROJ_CODE().equalsIgnoreCase(""))
                metaInfoArray = getKakaoPostMyProject(act);
            else
                metaInfoArray = getKakaoPostApps(act);

            String shareString = "";
            String shareMsg = Setting.getString(act, Const_VALUE.KEY_KAKAOTALK_SHARE_MSG);
            String myName = Setting.getString(act, Const_VALUE.KEY_KAKAOTALK_MYNAME);
            if ("".equals(shareMsg)) {// ??????????????? ?????? ??????
                shareString = String.format(act.getString(R.string.kakaotalk_share_message), myName);
            } else {// ?????? ??????
                shareString = shareMsg.replaceAll("%@", myName);
            }

            kakaoLink.openKakaoAppLink(act, SnapsAPI.DOMAIN(), // link url
                    shareString, act.getPackageName(), act.getPackageManager().getPackageInfo(act.getPackageName(), 0).versionName, act.getResources().getString(R.string.snaps), // [title]
                    "UTF-8", metaInfoArray);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * KaKao Post Apps
     *
     * @param context
     * @return
     */
    public ArrayList<Map<String, String>> getKakaoPostApps(Context context) {

        ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

        // excute url??? kakao + client_id + :// ??? ?????? ???????????? ??????????????????.
        // ?????????????????? ??? ?????? ??????????????? ????????? AndroidManifest.xml??? custom scheme??? ??????????????????.
        // ???????????? ????????? ?????? exe?key1=value1&key2=value2 ??? ???????????? excuteurl??? ????????????????????????.

        Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
        metaInfoAndroid.put("os", "android");
        metaInfoAndroid.put("devicetype", "phone");
        metaInfoAndroid.put("installurl", SnapsAPI.PLAY_STORE_UPDATE_URL);
        metaInfoAndroid.put("executeurl", KakaoConst.CLIENT_REDIRECT_URI);

        Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
        metaInfoIOS.put("os", "ios");
        metaInfoIOS.put("devicetype", "phone");
        metaInfoIOS.put("installurl", SnapsAPI.APP_STORE_UPDATE_URL);
        metaInfoIOS.put("executeurl", KakaoConst.CLIENT_REDIRECT_URI);

        // add to array
        metaInfoArray.add(metaInfoAndroid);
        metaInfoArray.add(metaInfoIOS);

        return metaInfoArray;
    }


    /**
     * Kakao Post Project
     *
     * @param context
     * @return
     */
    public ArrayList<Map<String, String>> getKakaoPostMyProject(Context context) {
        ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

        // If application is support Android platform.
        Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
        metaInfoAndroid.put("os", "android");
        metaInfoAndroid.put("devicetype", "phone");
        metaInfoAndroid.put("installurl", SnapsAPI.PLAY_STORE_UPDATE_URL);
        metaInfoAndroid.put("executeurl", "snapsmobilekr" + "://sharekakaotalk?prdcode=" + Config.getPROD_CODE() + "&prjcode=" + Config.getPROJ_CODE());

        // If application is support ios platform.
        Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
        metaInfoIOS.put("os", "ios");
        metaInfoIOS.put("devicetype", "phone");
        metaInfoIOS.put("installurl", SnapsAPI.APP_STORE_UPDATE_URL);
        metaInfoIOS.put("executeurl", "snapsmobilekr" + "://sharekakaotalk?prdcode=" + Config.getPROD_CODE() + "&prjcode=" + Config.getPROJ_CODE());

        // add to array
        metaInfoArray.add(metaInfoAndroid);
        metaInfoArray.add(metaInfoIOS);

        return metaInfoArray;
    }

    void alert(Context context, String message) {
//		MessageUtil.alert(context, context.getString(R.string.app_name), message);

    }
}
