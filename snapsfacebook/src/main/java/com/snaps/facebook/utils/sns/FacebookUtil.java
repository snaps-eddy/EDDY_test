package com.snaps.facebook.utils.sns;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.snaps.common.data.img.MyFacebookData;
import com.snaps.common.data.img.MyFacebookData.MyFBReply;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.facebook.R;
import com.snaps.facebook.model.sns.facebook.AlbumData;
import com.snaps.facebook.model.sns.facebook.AttachmentData;
import com.snaps.facebook.model.sns.facebook.ChapterData;
import com.snaps.facebook.model.sns.facebook.CommentData;
import com.snaps.facebook.model.sns.facebook.FeedData;
import com.snaps.facebook.model.sns.facebook.FriendData;
import com.snaps.facebook.model.sns.facebook.LikeData;
import com.snaps.facebook.model.sns.facebook.PostData;
import com.snaps.facebook.model.sns.facebook.StoryTag;
import com.snaps.facebook.model.sns.facebook.TimelineData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;


@SuppressLint("SimpleDateFormat")
public class FacebookUtil extends IFacebook {
    private static final String TAG = FacebookUtil.class.getSimpleName();

    Activity current;

    CallbackManager callbackManager;

    public FacebookUtil(String str) {
        this();
    }

    public FacebookUtil() {
        super();
    }

    public void facebookLogout() {
        logout();
    }

    public void init(Activity act) {
        current = act;
        Init(act);
    }

    public void activeApp(Context context, String str) {
        //에러로그가 계속 올라와서 추가한 코드
        try {
            if (FacebookSdk.isInitialized())
                AppEventsLogger.activateApp(context, str);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean isFacebookLogin() {
        return isLogin();
    }

    public void facebookLoginChk(Activity act, final OnFBComplete onComp) {
        Init(act);

        onFBComplete = onComp;

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult arg0) {
                if (onComp != null) {
                    Log.e("TAG", "login result " + arg0.toString());
                    onComp.onFBComplete(arg0.toString());
                }
            }

            @Override
            public void onError(FacebookException e) {
                Dlog.e(TAG, e);
            }

            @Override
            public void onCancel() {
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(act, PERMISSIONS_READ);
    }

    public void saveInstance(Bundle bundle) {
    }

    public void onActivityResult(Activity act, int requestCode, int resultCode, Intent data) {
        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean facebookGetPhotos(Activity act, String nextKey, int paging, final OnPaging onPaging) {
        return getPhotos(act, nextKey, String.valueOf(paging), onPaging);
    }

    public void facebookGetPostInfosForBatch(ArrayList<MyPhotoSelectImageData> returnList) {
        getPostInfosForBatch(returnList);
    }

    public void setContext(Context context) {
        mContext = context;
    }

    // permission
    public static final List<String> PERMISSIONS_READ = Arrays.asList("public_profile", "email", "user_friends", "user_photos", "user_posts");

    // pending action
    enum PendingAction {
        NONE, POST_PHOTO, POST_STATUS_UPDATE, READ_PHOTOS
    }

    static PendingAction pendingAction = PendingAction.NONE;

    /*
     * // interface public interface OnFBComplete { public void onFBComplete(String result); }
     */
    static OnFBComplete onFBComplete;

    public static Context mContext;

    public static void Init(Activity act) {
        FacebookSdk.sdkInitialize(act.getApplicationContext());
    }

    public static boolean isLogin() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    public void logout() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null)
            LoginManager.getInstance().logOut();

        Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_NAME, "");
    }

    @SuppressWarnings("incomplete-switch")
    static void handlePendingAction(String result) {
        PendingAction previouslyPendingAction = pendingAction;
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case POST_PHOTO:
                break;
            case POST_STATUS_UPDATE:
                break;
            case READ_PHOTOS:
                if (onFBComplete != null) {
                    onFBComplete.onFBComplete(result);
                    onFBComplete = null;
                }
                break;
        }
    }

    static void getUserInfo(Activity act) {
        if (isLogin())
            getProfileData(act);
    }

    public static void getCoverData() {
        try {
            String fql = "SELECT src, src_big, src_big_height, src_big_width, object_id FROM photo WHERE pid IN "
                    + "(SELECT cover_pid from album where aid IN (SELECT aid, name FROM album WHERE owner=me() AND name=\"Cover Photos\"))";

            if (isLogin()) {
                Bundle params = new Bundle();
                params.putString("q", fql);

                GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "/fql", params, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse arg0) {
                        JSONObject mainObj = null;
                        try {
                            if (arg0.getError() == null) {
                                mainObj = arg0.getJSONObject();
                                JSONArray coverObj = mainObj.getJSONArray("data");
                                if (coverObj != null) {
                                    String coverImgUrl = "";

                                    for (int i = 0; i < coverObj.length(); i++) {
                                        try {
                                            coverImgUrl = coverObj.getJSONObject(i).getString("src_big");
                                        } catch (JSONException json) {
                                            Dlog.e(TAG, json);
                                        }
                                    }

                                    Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_COVER_URL, coverImgUrl);
                                } else
                                    Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_COVER_URL, "");
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        } finally {
                            if (onFBComplete != null) {
                                onFBComplete.onFBComplete(null);
                                onFBComplete = null;
                            }
                        }
                    }
                });
                GraphRequest.executeBatchAsync(request);
            }

            return;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return;
    }

    public static void getProfileData(Activity act) {
        getNewProfile(act);
    }

    public static void getProfile() {
        try {
            Bundle params = new Bundle();
            params.putString("fields", "cover,picture.type(large),id,link,name");
            GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        if (response.getError() == null) {
                            try {
                                JSONObject mainObj = response.getJSONObject();
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    } finally {
                        if (onFBComplete != null) {
                            onFBComplete.onFBComplete(null);
                            onFBComplete = null;
                        }
                    }
                }
            });
            request.executeAsync();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void getPostInfosForBatch(ArrayList<MyPhotoSelectImageData> returnList) {
        try {
            GraphRequestBatch requestBatch = new GraphRequestBatch();
            for (final MyPhotoSelectImageData imageData : returnList) {
                final MyFacebookData fbData = imageData.FB_DATA;
                final Bundle params = new Bundle();
                params.putString("fields", "name,comments.limit(4).fields(message,from,created_time,like_count),created_time,likes.limit(1000).fields(id)");
                GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), imageData.FB_OBJECT_ID + "/", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getError() == null) {
                                try {
                                    JSONObject mainObj = response.getJSONObject();

                                    fbData.postMsg = mainObj.optString("name", "");// message
                                    fbData.postDate = StringUtil.getFBDatetoFormat(mContext, mainObj.optString("created_time", ""));// created_time
                                    // likes
                                    if (mainObj.optJSONObject("likes") != null && mainObj.optJSONObject("likes").optJSONArray("data") != null) {
                                        int likes = mainObj.optJSONObject("likes").optJSONArray("data").length();
                                        fbData.postLikes = likes == 0 ? "" : String.valueOf(likes);
                                    }

                                    // comments
                                    if (mainObj.optJSONObject("comments") != null && mainObj.optJSONObject("comments").optJSONArray("data") != null) {
                                        JSONArray jArray = mainObj.optJSONObject("comments").optJSONArray("data");
                                        for (int i = 0; i < jArray.length(); i++) {
                                            JSONObject jObj = jArray.optJSONObject(i);
                                            MyFBReply fbReply = new MyFBReply();
                                            fbReply.replyMsg = jObj.optString("message", "");
                                            fbReply.replyDate = StringUtil.getFBDatetoFormatExtraWeek(mContext, jObj.optString("created_time", ""));
                                            String like = jObj.optString("like_count", "");
                                            fbReply.replyLikes = "0".equals(like) ? "" : like;
                                            fbReply.replyFBName = jObj.optJSONObject("from").optString("name", "");
                                            fbReply.replyProfileImg = "http://graph.facebook.com/" + jObj.optJSONObject("from").optString("id", "") + "/picture?type=square";
                                            fbData.replyData.add(fbReply);
                                        }
                                    }
                                } catch (Exception e) {
                                    Dlog.e(TAG, e);
                                }
                            }
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                    }
                });
                requestBatch.add(request);
            }
            requestBatch.executeAndWait();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset)
            if (!superset.contains(string))
                return false;
        return true;
    }

    public static boolean getPhotos(final Activity context, String nextPageKey, String limitCount, final OnPaging paging) {
        if (isLogin()) {
            AccessToken token = AccessToken.getCurrentAccessToken();
            Object[] temp = token.getPermissions().toArray();
            ArrayList<String> permissions = new ArrayList<String>();
            for (int i = 0; i < temp.length; ++i)
                permissions.add(temp[i].toString());

            if (!isSubsetOf(PERMISSIONS_READ, permissions))
                LoginManager.getInstance().logInWithReadPermissions(context, PERMISSIONS_READ);

            String request = "/me/photos";
            Bundle postParams = new Bundle();
            postParams.putString("type", "uploaded");
            postParams.putString("fields", "images, created_time,width,height");
            postParams.putString("limit", limitCount);
            if (nextPageKey != null && nextPageKey.length() > 0)
                postParams.putString("after", nextPageKey);

            GraphRequest.Callback callback = new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    JSONObject graphResponse = null;
                    try {
                        if (response != null)
                            graphResponse = response.getJSONObject();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    } finally {
                        if (paging != null)
                            paging.onPagingComplete(graphResponse);
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        // FIXME 에러 처리 필요.
                    }
                }
            };

            GraphRequest getPhotoReq = new GraphRequest(AccessToken.getCurrentAccessToken(), request, postParams, HttpMethod.GET, callback);
            GraphRequestAsyncTask task = new GraphRequestAsyncTask(getPhotoReq);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();
        }
        return true;
    }

    public static boolean getNewProfile(final Activity context) {
        if (isLogin()) {
            String request = "/me";
            Bundle postParams = new Bundle();
            postParams.putString("fields", "id,name,picture.width(256),link,cover");

            GraphRequest.Callback callback = new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    JSONObject graphResponse = null;
                    try {
                        if (response != null) {
                            graphResponse = response.getJSONObject();
                            String name = graphResponse.getString("name");
                            String id = graphResponse.getString("id");
                            JSONObject pictures = (JSONObject) graphResponse.get("picture");
                            JSONObject data = (JSONObject) pictures.get("data");
                            String profileUrl = data.getString("url");

                            Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_ID, id);
                            Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_NAME, name);
                            Setting.set(mContext, Const_VALUE.KEY_FACEBOOK_PROFILE_URL, profileUrl);

                            Intent faceBookProfile = new Intent(Const_VALUE.FACEBOOK_CHANGE_NAME_ACTION);
                            mContext.sendBroadcast(faceBookProfile);
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    } finally {
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        // FIXME 에러 처리 필요.
                    }
                }
            };

            GraphRequest getPhotoReq = new GraphRequest(AccessToken.getCurrentAccessToken(), request, postParams, HttpMethod.GET, callback);
            GraphRequestAsyncTask task = new GraphRequestAsyncTask(getPhotoReq);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();
        }
        return true;
    }

    @Override
    public void addCallback() {
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public void removeCallback() {
    }

    public interface ProcessListener {
        public void onComplete(Object result);

        public void onFail(Object result);

        public void onError(Object result);
    }

    public interface ProgressListener {
        public void onUpdate(float per);
    }

    public static class BookMaker {
        public static final String GRAPH_API_URL = "https://graph.facebook.com/v2.5/";

        public static final String[][] TEMPLATE_ID_TYPE = {
                {"045021006852", "045021006866", "045021007091", "045021007092", "045021007093", "045021007094", "045021007095", "045021007096"},                    // A type
                {"045021006892", "045021006895", "045021007097", "045021007098", "045021007099", "045021007100", "045021007101", "045021007102"},                    // B type
                {"045021007142", "045021007143", "045021007189", "045021007190", "045021007191", "045021007192", "045021007193", "045021007194"},                    // C type
                {"045021007144", "045021007145", "045021007195", "045021007196", "045021007197", "045021007198", "045021007199", "045021007200"}};                // D type																// C type

        public static final int TYPE_A = 0;
        public static final int TYPE_B = 1;
        public static final int TYPE_C = 2;
        public static final int TYPE_D = 3;
        public static final int TYPE_E = 4;
        public static final int TYPE_F = 5;
        public static final int TYPE_G = 6;

        public static final int LINE_SPACINNG = 0;

        public static final int LIKE_COUNT_INDEX = 0;
        public static final int COMMENT_COUNT_INDEX = 1;
        public static final int SHARE_COUNT_INDEX = 2;

        private static BookMaker instance;

        private BookMaker() {

        }

        private LinkedBlockingQueue<GraphRequest> linkedQueue;
        private ArrayList<GraphRequest> workingList;
        @SuppressWarnings("rawtypes")
        private ArrayList<AsyncTask> imageCalculateWorkingList;
        public ArrayList<TimelineData> timelines;
        public ArrayList<ChapterData> chapterList;
        public ArrayList<AlbumData> albums;
        public ArrayList<PostData> posts;
        public ArrayList<FeedData> feeds;
        public HashMap<Long, FriendData> friendsMap;
        private ArrayList<FriendData> friends;

        public String id, name, email, birthday, profileOriginUrl, profileThumbUrl, coverId, coverUrl;
        public int[] profileOriginSize, profileThumbSize, coverSize;

        // format : yyyy-mm-dd
        public String startTime, endTime, realEndTime;

        public String coverTitle = "나만의 페이스북 포토북";
        public int commentLimit = 30;
        public int replyLimit = 30;

        public static final int SHOW_PHOTO_UNLIMIT = 100;
        public static final int SHOW_PHOTO_LIMIT_5 = 5;
        public static final int SHOW_PHOTO_LIMIT_1 = 1;
        public int showPhotoType = SHOW_PHOTO_UNLIMIT;

        public boolean showMyPostOnly = false;

        public int templateType = TYPE_A;

        private String templateId, productCode;
        private String paperCode;

        private ProcessListener processListener;
        private ProgressListener progressListener;

        private boolean[] processFlags;

        public static final int BASE_FLAG_INDEX = 0;
        public static final int ALBUM_FLAG_INDEX = 1;
        public static final int POST_FLAG_INDEX = 2;
        public static final int FEED_FLAG_INDEX = 3;
        public int[] response;

        public static final int REQUEST_PROGRESS_COMPLETE = 100;

        public static final int POST_WRITE_COUNT_INDEX = 0;
        public static final int POST_PHOTO_COUNT_INDEX = 1;
        public static final int POST_VIDEO_COUNT_INDEX = 2;
        public static final int POST_SHARED_COUNT_INDEX = 3;
        public int[] postCount;

        public static final int SHARED_WRITE_COUNT_INDEX = 0;
        public static final int SHARED_PHOTO_COUNT_INDEX = 1;
        public static final int SHARED_VIDEO_COUNT_INDEX = 2;
        public int[] sharedCount;

        public int messageCount;
        Handler mCompleteHandler = null;

        public synchronized static BookMaker getInstance() {
            if (instance == null)
                instance = new BookMaker();
            return instance;
        }

        public static String getFormattedDateString(Calendar cal, String format) {
            return getFormattedDateString(cal, format, Locale.US);
        }

        public static String getFormattedDateString(Calendar cal, String format, Locale locale) {
            SimpleDateFormat sdf = new SimpleDateFormat(format, locale);
            return sdf.format(cal.getTime());
        }

        public String getPaperCode() {
            return paperCode;
        }

        public void setTemplateId(String id) {
            this.templateId = id;
            checkTemplateType();
        }

        public String getTemplateId() {
            return this.templateId;
        }

        public int getTemplateType() {
            return this.templateType;
        }

        public void setProductCode(String code) {
            this.productCode = code;
        }

        public String getProductCode() {
            return this.productCode;
        }


        public void setPaperCode(String paperCode) {
            this.paperCode = paperCode;
        }

        private void checkTemplateType() {
            templateType = TYPE_A;
            for (int i = 0; i < TEMPLATE_ID_TYPE.length; ++i) {
                for (int j = 0; j < TEMPLATE_ID_TYPE[i].length; ++j) {
                    if (templateId.equalsIgnoreCase(TEMPLATE_ID_TYPE[i][j])) {
                        templateType = i;
                        return;
                    }
                }
            }
        }

        public String getPeriodString() {
            if (chapterList == null || chapterList.size() < 1 || chapterList.get(0).getTimeLines() == null || chapterList.get(0).getTimeLines().size() < 1)
                return "";
            return getStartTimeStr() + " ~ " + getEndTimeStr();
        }

        public Calendar getStartCal() {
            return chapterList.get(0).getTimeLines().get(0).createDate;
        }

        public Calendar getEndCal() {
            return chapterList.get(chapterList.size() - 1).getTimeLines().get(chapterList.get(chapterList.size() - 1).getTimeLines().size() - 1).createDate;
        }

        public String getStartTimeStr() {
            Calendar s = getStartCal();
            return getFormattedDateString(s, "yyyy.MM.dd");
        }

        public String getEndTimeStr() {
            Calendar e = getEndCal();
            return getFormattedDateString(e, "yyyy.MM.dd");
        }


        public static String getBirthdayString(String facebookStringFormat) {
            if (facebookStringFormat == null || facebookStringFormat.length() < 1)
                return "";
            String[] ary = facebookStringFormat.split("/");
            if (ary.length < 3)
                return "";

            if (ary[0].startsWith("0") && ary[0].length() > 1)
                ary[0] = ary[0].substring(1);
            if (ary[1].startsWith("0") && ary[1].length() > 1)
                ary[1] = ary[1].substring(1);

            return ary[2] + ". " + ary[0] + ". " + ary[1];
        }

        public void setThumbData(JSONObject jobj) {
            try {
                profileThumbUrl = jobj.has("url") ? jobj.getString("url") : "";

                if (jobj.has("width") && jobj.has("height")) {
                    profileThumbSize = new int[2];
                    profileThumbSize[0] = jobj.getInt("width");
                    profileThumbSize[1] = jobj.getInt("height");
                }
            } catch (JSONException e) {
                Dlog.e(TAG, e);
            }
        }

        public void setOriginData(JSONObject jobj) {
            try {
                profileOriginUrl = jobj.has("url") ? jobj.getString("url") : "";

                if (jobj.has("width") && jobj.has("height")) {
                    profileOriginSize = new int[2];
                    profileOriginSize[0] = jobj.getInt("width");
                    profileOriginSize[1] = jobj.getInt("height");
                }
            } catch (JSONException e) {
                Dlog.e(TAG, e);
            }
        }

        public void setCoverData(String url, int[] size) {
            coverUrl = url;
            coverSize = size;
        }

        public void setFriendDataFromLike(HashMap<Long, FriendData> map, ArrayList<LikeData> list) {
            if (map == null)
                map = new HashMap<Long, FriendData>();
            else if (list == null)
                return;

            LikeData like;
            FriendData friend;
            for (int i = 0; i < list.size(); ++i) {
                like = list.get(i);
                if (like.id != null && like.id.length() > 0) {
                    Long id = Long.parseLong(like.id);
                    if (!map.containsKey(id))
                        map.put(id, new FriendData(like.id, like.name));
                    else {
                        friend = map.get(id);
                        map.remove(id);
                        friend.likeCount++;
                        map.put(id, friend);
                    }
                }
            }
        }

        // post의 story_tag 기준으로 친구데이터를 가져오기 위해 추가.
        public void setFriendDataFromPost(HashMap<Long, FriendData> map, ArrayList<StoryTag> list) {
            if (map == null)
                map = new HashMap<Long, FriendData>();
            else if (list == null)
                return;

            StoryTag tag;
            for (int i = 0; i < list.size(); ++i) {
                tag = list.get(i);
                if (tag.id != null && tag.id.length() > 0 && "user".equals(tag.rawType)) {
                    Long id = Long.parseLong(tag.id);
                    if (!map.containsKey(id))
                        map.put(id, new FriendData(tag.id, tag.name));
                }
            }
        }

        // feed의 작성자 기준으로 친구데이터를 가져오기 위해 추가.
        public void setFriendDataFromFeed(HashMap<Long, FriendData> map, ArrayList<FeedData> feeds) {
            if (map == null)
                map = new HashMap<Long, FriendData>();
            else if (feeds == null)
                return;

            FeedData feed;
            for (int i = 0; i < feeds.size(); ++i) {
                feed = feeds.get(i);
                if (feed.fromId != null && feed.fromId.length() > 0 && feed.fromName != null && feed.fromName.length() > 0 && feed.fromSomeone) {
                    Long id = Long.parseLong(feed.fromId);
                    if (!map.containsKey(id))
                        map.put(id, new FriendData(feed.fromId, feed.fromName));
                }
            }
        }

        public void setFriendDataFromComment(HashMap<Long, FriendData> map, ArrayList<CommentData> list) {
            if (map == null)
                map = new HashMap<Long, FriendData>();
            else if (list == null)
                return;

            CommentData comment;
            FriendData friend;
            for (int i = 0; i < list.size(); ++i) {
                comment = list.get(i);
                if (comment.fromId != null && comment.fromId.length() > 0) {
                    Long id = Long.parseLong(comment.fromId);
                    if (!map.containsKey(id))
                        map.put(id, new FriendData(comment.fromId, comment.fromName));
                    else {
                        friend = map.get(id);
                        map.remove(id);
                        friend.commentCount++;
                        map.put(id, friend);
                    }
                }

                if (comment.subCommentList != null && comment.subCommentList.size() > 0) {
                    CommentData subComment = null;
                    for (int j = 0; j < comment.subCommentList.size(); ++j) {
                        subComment = comment.subCommentList.get(j);
                        if (subComment.fromId != null && subComment.fromId.length() > 0) {
                            Long id = Long.parseLong(subComment.fromId);
                            if (!map.containsKey(subComment.fromId))
                                map.put(id, new FriendData(subComment.fromId, subComment.fromName));
                            else {
                                friend = map.get(subComment.fromId);
                                map.remove(subComment.fromId);
                                friend.commentCount++;
                                map.put(id, friend);
                            }
                        }
                    }
                }
            }
        }

        public void updateProgress(float percent) {
            if (progressListener != null) progressListener.onUpdate(percent);
        }

        public void setProgressListener(ProgressListener listener) {
            this.progressListener = listener;
        }

        public void init() {
            timelines = new ArrayList<TimelineData>();
            albums = new ArrayList<AlbumData>();
            posts = new ArrayList<PostData>();
            feeds = new ArrayList<FeedData>();

            processListener = null;
            progressListener = null;

            id = "";
            name = "";
            email = "";
            birthday = "";
            profileOriginUrl = "";
            profileThumbUrl = "";
            coverUrl = "";
            startTime = "";
            endTime = "";

            processFlags = new boolean[4];

            friendsMap = new HashMap<Long, FriendData>();
            mCompleteHandler = new Handler(new Callback() {

                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == REQUEST_PROGRESS_COMPLETE) doAfterRefresh();
                    else notiProcessDone(msg.what, true);
                    return false;
                }
            });
        }

        public void setCoverTitle(String title) {
            this.coverTitle = title;
        }

        public void setDataLimit(int commentLimit, int replyLimit, int showPhotoType, boolean showMyPostOnly) {
            this.commentLimit = commentLimit;
            this.replyLimit = replyLimit;
            this.showPhotoType = showPhotoType;
            this.showMyPostOnly = showMyPostOnly;
        }

        public void makeTimelineList(boolean firstFlag) {
            timelines = new ArrayList<TimelineData>();
            boolean added = false; // timelines 만든 다음에도, post와 feed에서 정보를 사용하므로, timelines에 추가하지 않는 데이터는 지워버린다.

            if (posts != null)
                for (int i = 0; i < posts.size(); ++i) {
                    added = false;
                    if (posts.get(i).type != TimelineData.TYPE_LINK) {
                        //youtube 링크가 유효하지 않는 경우에는 아래와 같은 Url로 온다고 가정한다.
                        if (!posts.get(i).fullPicture.equals("https://external.xx.fbcdn.net/safe_image.php?d=AQDtHKwrK8aUCpIg&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FQkCpf557ZGw%2Fhqdefault.jpg&cfs=1")) {
                            timelines.add(new TimelineData(posts.get(i)));
                            added = true;
                        }
                    }

                    if (!added) {
                        posts.remove(i);
                        i--;
                    }
                }

            FeedData feed = null;
            if (feeds != null && !showMyPostOnly)
                for (int i = 0; i < feeds.size(); ++i) {
                    added = false;
                    feed = feeds.get(i);
                    feed.fromSomeone = !id.equalsIgnoreCase(feed.fromId);
                    if (feed.type != TimelineData.TYPE_LINK && feed.fromSomeone) {
                        if (!feed.fullPicture.equals("https://external.xx.fbcdn.net/safe_image.php?d=AQDtHKwrK8aUCpIg&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2FQkCpf557ZGw%2Fhqdefault.jpg&cfs=1") &&
                                ((feed.message != null && feed.message.length() > 0) || (feed.attachmentList != null && feed.attachmentList.size() > 0))) {
                            timelines.add(new TimelineData(feed));
                            added = true;
                        }
                    }

                    if (!added) {
                        feeds.remove(i);
                        i--;
                    }
                }

//			// TODO test code
//			int index = 0;
//			while( timelines.size() < 800 ) {
//				if( index > timelines.size() - 1 ) index = 0;
//				timelines.add( timelines.get(index) );
//			}
//			// TODO test code end

            Collections.sort(timelines, new NumberAscCompare());

            if (firstFlag) refreshCommentAndLikeData();
        }

        private void addRequest(GraphRequest request) {
            synchronized (linkedQueue) {
                linkedQueue.add(request);
            }
        }

        @SuppressWarnings("rawtypes")
        private void refreshCommentAndLikeData() {
            linkedQueue = new LinkedBlockingQueue<GraphRequest>();
            workingList = new ArrayList<GraphRequest>();
            imageCalculateWorkingList = new ArrayList<AsyncTask>();

            synchronized (linkedQueue) {
                TimelineData data = null;
                for (int i = 0; i < timelines.size(); ++i) {
                    data = timelines.get(i);
                    if (data != null) {
                        GraphRequest request = null, request2 = null;
                        if (data.type == TimelineData.TYPE_MESSAGE) {
                            request = data.feed.refreshCommentAndLike(this);
                            data.feed.calculateImageSize(this, imageCalculateWorkingList);
                        } else {
                            request = data.post.refreshCommentAndLike(this);
                            request2 = data.post.getSharedWriteCreatedTime(this);
                        }

                        if (request != null) addRequest(request);
                        if (request2 != null) addRequest(request2);
                    }
                }

                if (linkedQueue.size() < 1) doAfterRefresh();
                else {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (linkedQueue.size() > 0) {
                                GraphRequest req = linkedQueue.poll();
                                workingList.add(req);
                                req.executeAndWait();
                            }
                        }
                    });
                }
            }
        }

        public void checkRefreshProcessDone(GraphRequest req) {
            synchronized (linkedQueue) {
                workingList.remove(req);
                if (linkedQueue.size() < 1 && workingList.size() < 1 && imageCalculateWorkingList.size() < 1)
                    mCompleteHandler.sendEmptyMessage(REQUEST_PROGRESS_COMPLETE);
            }
        }

        public void checkRefreshProcessDone() {
            synchronized (linkedQueue) {
                if (linkedQueue.size() < 1 && workingList.size() < 1 && imageCalculateWorkingList.size() < 1)
                    mCompleteHandler.sendEmptyMessage(REQUEST_PROGRESS_COMPLETE);
            }
        }

        private void doAfterRefresh() {
            getResponseCount();
            getPostCount();
            getMessageCount();
            makeFriendList();

            if (processListener != null)
                processListener.onComplete(instance);
        }

        private void makeFriendList() {
            int index = 0;

            if (feeds != null && feeds.size() > 0) setFriendDataFromFeed(friendsMap, feeds);

            boolean checkContinue = true;
            while (checkContinue) {
                checkContinue = false;
                if (index < albums.size()) {
                    checkContinue = true;
                    setFriendDataFromComment(friendsMap, albums.get(index).commentList);
                    setFriendDataFromLike(friendsMap, albums.get(index).likeList);
                }
                if (index < posts.size()) {
                    checkContinue = true;
                    setFriendDataFromComment(friendsMap, posts.get(index).commentList);
                    setFriendDataFromLike(friendsMap, posts.get(index).likeList);
                    setFriendDataFromPost(friendsMap, posts.get(index).storyTagList);
                }
                if (index < feeds.size()) {
                    checkContinue = true;
                    setFriendDataFromComment(friendsMap, feeds.get(index).commentList);
                    setFriendDataFromLike(friendsMap, feeds.get(index).likeList);
                }
                index++;
            }

            if (!StringUtil.isEmpty(id)) friendsMap.remove(Long.parseLong(id)); // 본인 데이터 제외.
            setFriendList(friendsMap);
        }

        public void setFriendList(HashMap<Long, FriendData> map) {
            friends = new ArrayList<FriendData>(friendsMap.values());
            Collections.sort(friends, new FriendPointDescCompare());
        }

        private void getMessageCount() {
            messageCount = 0;

            for (int index = 0; index < feeds.size(); ++index)
                if (feeds.get(index).fromSomeone) {
                    // 다시 다른사람이 내 이름으로 태그한 글은 메세지로 해달라고 하여 원복.
					/*if( feeds.get(index).storyTagList != null && feeds.get(index).storyTagList.size() > 0 ) { // 다른사람이 내이름으로 태그한 글은 메세지가 아니라 공유글로 포함해달라고 디자인팀에서 요청하여 수정.
						switch (feeds.get(index).type) {
							case TimelineData.TYPE_LINK:
							case TimelineData.TYPE_STATUS:
								sharedCount[BookMaker.SHARED_WRITE_COUNT_INDEX]++;
								break;
							case TimelineData.TYPE_PHOTO:
								sharedCount[BookMaker.SHARED_PHOTO_COUNT_INDEX]++;
								break;
							case TimelineData.TYPE_VIDEO:
								sharedCount[BookMaker.SHARED_VIDEO_COUNT_INDEX]++;
								break;
						}
					}
					else */
                    messageCount++;
                }
        }

        private void getPostCount() {
            postCount = new int[4];
            sharedCount = new int[3];
            boolean isShared;

            for (int index = 0; index < posts.size(); ++index) {
                if (!posts.get(index).sharedFromSomeone && posts.get(index).summary[BookMaker.SHARE_COUNT_INDEX] > 0)
                    postCount[BookMaker.POST_SHARED_COUNT_INDEX]++;
                else {
                    isShared = posts.get(index).isSharedPost() || posts.get(index).isSharedPostWithoutWriter() || posts.get(index).hasSharedAttachment();

                    switch (posts.get(index).type) {
                        case TimelineData.TYPE_LINK:
                        case TimelineData.TYPE_STATUS:
                            if (isShared) sharedCount[BookMaker.SHARED_WRITE_COUNT_INDEX]++;
                            else postCount[BookMaker.POST_WRITE_COUNT_INDEX]++;
                            break;
                        case TimelineData.TYPE_PHOTO:
                            if (isShared) sharedCount[BookMaker.SHARED_PHOTO_COUNT_INDEX]++;
                            else postCount[BookMaker.POST_PHOTO_COUNT_INDEX]++;
                            break;
                        case TimelineData.TYPE_VIDEO:
                            if (isShared) sharedCount[BookMaker.SHARED_VIDEO_COUNT_INDEX]++;
                            else postCount[BookMaker.POST_VIDEO_COUNT_INDEX]++;
                            break;
                    }
                }
            }
        }

        public int getStartPageIndex(ChapterData chapter) {
            if (chapterList == null || chapterList.size() < 1) return -1;
            int index = chapterList.indexOf(chapter);
            if (index < 0) return -1;

            // 1: 인덱스.
            // 2: 사진들.
            // 3: 프로필 사진.
            // 4: post summary
            // 5: firend summary
            int pageIndex = 6;
            for (int i = 0; i < index; ++i) {
                pageIndex += (chapterList.get(i).pageCounts + 1) * 2; // (챕터페이지1 + 내용) * 2;
            }
            return pageIndex += 2; // chapter pages 추가.
        }

        public int getTotalPost() {
            int total = 0;
            int index = 0;
            boolean checkContinue = true;
            while (checkContinue) {
                checkContinue = false;
                if (index < postCount.length) {
                    checkContinue = true;
                    total += postCount[index];
                }
                if (index < sharedCount.length) {
                    checkContinue = true;
                    total += sharedCount[index];
                }

                index++;
            }
            total += messageCount;
            return total;
        }

        private void getResponseCount() {
            response = new int[3];

            int index = 0;
            boolean checkContinue = true;
            while (checkContinue) {
                checkContinue = false;
//				if (index < albums.size()) {
//					checkContinue = true;
//					response = FacebookUtil.combineAry(response, albums.get(index).summary);
//				}
                if (index < posts.size()) {
                    checkContinue = true;
                    response = FacebookUtil.combineAry(response, posts.get(index).summary);
                }
                if (index < feeds.size()) {
                    checkContinue = true;
                    response = FacebookUtil.combineAry(response, feeds.get(index).summary);
                }

                index++;
            }
        }

        public TimelineData getBestTimeline() {
            if (timelines == null)
                return null;
            return getBestTimeline(0, timelines.size() - 1);
        }

        public TimelineData getBestTimeline(ChapterData chapter) {
            int maxPoint = -1;
            int tempPoint = -1;
            int index = -1;

            for (int i = 0; i < chapter.timelines.size(); ++i) {
                tempPoint = chapter.timelines.get(i).getPoint();
                if (tempPoint > maxPoint && chapter.timelines.get(i).getAttachments().size() > 0) {
                    index = i;
                    maxPoint = tempPoint;
                }
            }

            if (index < 0) {
                for (int i = 0; i < chapter.timelines.size(); ++i) {
                    tempPoint = chapter.timelines.get(i).getPoint();
                    if (tempPoint > maxPoint) {
                        index = i;
                        maxPoint = tempPoint;
                    }
                }
            }

            TimelineData data = null;
            if (index > -1)
                data = chapter.timelines.get(index);
            return data;
        }

        public TimelineData getBestTimeline(int startIndex, int endIndex) {
            int index = -1;
            int maxPoint = -1;
            int tempPoint = -1;

            for (int i = startIndex; i < endIndex + 1; ++i) {
                tempPoint = timelines.get(i).getPoint();
                if (tempPoint >= maxPoint && timelines.get(i).getAttachments().size() > 0) {
                    index = i;
                    maxPoint = tempPoint;

                }
            }

            if (index < 0) {
                for (int i = startIndex; i < endIndex + 1; ++i) {
                    tempPoint = timelines.get(i).getPoint();
                    if (tempPoint >= maxPoint) {
                        index = i;
                        maxPoint = tempPoint;
                    }
                }
            }

            TimelineData data = null;
            if (index > -1)
                data = timelines.get(index);
            return data;
        }

        public ArrayList<AlbumData> getBestAlbumList() {
            HashMap<String, AlbumData> temp = new HashMap<String, AlbumData>();
            if (albums != null && albums.size() > 0)
                for (int i = 0; i < albums.size(); ++i) temp.put(albums.get(i).id, albums.get(i));
            addAlbumDataFromPosts(temp);
            addAlbumDataFromFeeds(temp);

            ArrayList<AlbumData> list = new ArrayList<AlbumData>(temp.values());
            Collections.sort(list, new AlbumCommentDescCompare());
            return list;
        }

        private void addAlbumDataFromPosts(HashMap<String, AlbumData> map) {
            PostData data;
            AttachmentData att;
            AlbumData album;
            if (posts != null && posts.size() > 0) {
                for (int i = 0; i < posts.size(); ++i) {
                    data = posts.get(i);
                    if (data.attachmentList != null && data.attachmentList.size() > 0) {
                        for (int j = 0; j < data.attachmentList.size(); ++j) {
                            att = data.attachmentList.get(j);
                            if (!map.containsKey(att.targetId)) {
                                album = new AlbumData(AlbumData.TYPE_NORMAL);
                                album.id = att.targetId;
                                album.thumb = att.imageUrl;
                                album.fullPicture = att.imageUrl;

                                if (att.width == AttachmentData.INVALID_IMAGE_DIMENSION
                                        || att.height == AttachmentData.INVALID_IMAGE_DIMENSION) {
                                    Rect imageRect = HttpUtil.getNetworkImageRect(att.imageUrl);
                                    att.width = imageRect.width();
                                    att.height = imageRect.height();
                                }

                                album.width = att.width;
                                album.height = att.height;

                                album.createDate = data.createDate;
                                album.createTime = data.createTime;
                                map.put(album.id, album);
                            }
                        }
                    }
                }
            }
        }

        private void addAlbumDataFromFeeds(HashMap<String, AlbumData> map) {
            FeedData data;
            AttachmentData att;
            AlbumData album;
            if (feeds != null && feeds.size() > 0) {
                for (int i = 0; i < feeds.size(); ++i) {
                    data = feeds.get(i);
                    if (data.type == TimelineData.TYPE_MESSAGE && data.attachmentList != null && data.attachmentList.size() > 0) {
                        for (int j = 0; j < data.attachmentList.size(); ++j) {
                            att = data.attachmentList.get(j);
                            if (!map.containsKey(att.targetId)) {
                                album = new AlbumData(AlbumData.TYPE_NORMAL);
                                album.id = att.targetId;
                                album.thumb = att.imageUrl;
                                album.fullPicture = att.imageUrl;

                                if (att.width == AttachmentData.INVALID_IMAGE_DIMENSION
                                        || att.height == AttachmentData.INVALID_IMAGE_DIMENSION) {
                                    Rect imageRect = HttpUtil.getNetworkImageRect(att.imageUrl);
                                    att.width = imageRect.width();
                                    att.height = imageRect.height();
                                }

                                album.width = att.width;
                                album.height = att.height;

                                album.createDate = data.createDate;
                                album.createTime = data.createTime;
                                map.put(album.id, album);
                            }
                        }
                    }
                }
            }
        }

        public ArrayList<FriendData> getFrieldList() {
            return this.friends;
        }

        public ArrayList<AlbumData> getProfileAlbumList() {
            ArrayList<AlbumData> temp = new ArrayList<AlbumData>();
            ArrayList<AlbumData> temp2 = new ArrayList<AlbumData>();
            for (int i = 0; i < albums.size(); ++i)
                if (albums.get(i).type == AlbumData.TYPE_PROFILE)
                    temp.add(albums.get(i));

            boolean isExist = false; // 중복되는게 있는 케이스가 있다 하니 한번 정리해줌.
            for (int i = 0; i < temp.size(); ++i) {
                isExist = false;
                for (int j = 0; j < temp2.size(); ++j) {
                    if (temp2.get(j).id.equalsIgnoreCase(temp.get(i).id)) isExist = true;
                }
                if (!isExist) temp2.add(temp.get(i));
            }

            if (temp2.size() > 1) Collections.sort(temp2, new ProfileDateDescCompare());
            return temp2;
        }

        public void clearSelectedPosts(ArrayList<String> selected) {
            if (timelines == null || timelines.size() < 1)
                return;

            int index;
            TimelineData temp;
            for (int i = 0; i < selected.size(); ++i) {
                index = Integer.parseInt(selected.get(i));
                if (index > -1 && index < timelines.size()) {
                    temp = timelines.get(index);
                    if (temp.type != TimelineData.TYPE_MESSAGE && temp.post != null)
                        removePost(temp.post);
                    else if (temp.feed != null)
                        removeFeed(temp.feed);
                }
            }
            makeTimelineList(false);
        }

        private void removePost(PostData post) {
            if (post.attachmentList != null && post.attachmentList.size() > 0) {
                ArrayList<String> albumIdAry = new ArrayList<String>();

                for (int i = 0; i < post.attachmentList.size(); ++i)
                    albumIdAry.add(post.attachmentList.get(i).targetId);

                albumIdAry = getUniqueIdList(albumIdAry, post.id);
                clearAlbums(albumIdAry);
            }

            posts.remove(post);
        }

        private ArrayList<String> getUniqueIdList(ArrayList<String> origin, String postId) {
            ArrayList<String> list = new ArrayList<String>();
            ArrayList<String> temp;
            TimelineData timeline;
            for (int i = 0; i < timelines.size(); ++i) {
                timeline = timelines.get(i);
                if ((timeline.type == TimelineData.TYPE_MESSAGE && timeline.feed.id.equals(postId)) || (timeline.type != TimelineData.TYPE_MESSAGE && timeline.post.id.equals(postId)))
                    continue;
                temp = timeline.getAttachmentIdList();
                if (temp != null && temp.size() > 0) list.addAll(temp);
            }

            return list;
        }

        private void clearAlbums(ArrayList<String> idList) {
            while (idList.size() > 0) {
                for (int i = 0; i < albums.size(); ++i) {
                    if (albums.get(i).id.equals(idList.get(0))) {
                        albums.remove(i);
                        i--;
                    }
                }
                idList.remove(0);
            }
        }

        private void removeFeed(FeedData feed) {
            if (feed.attachmentList != null && feed.attachmentList.size() > 0) {
                ArrayList<String> albumIdAry = new ArrayList<String>();

                for (int i = 0; i < feed.attachmentList.size(); ++i)
                    albumIdAry.add(feed.attachmentList.get(i).targetId);

                albumIdAry = getUniqueIdList(albumIdAry, feed.id);
                clearAlbums(albumIdAry);
            }

            feeds.remove(feed);
        }

        public void execute(Activity context, ProcessListener processListener) {
            execute(context, startTime, endTime, processListener);
        }

        public void execute(Activity context, String startTime, String endTime) {
            execute(context, startTime, endTime, null);
        }

        private String convertDateString(String origin) {
            String newStr = origin;
            if (!origin.contains("-") && origin.trim().length() == 8) {
                try {
                    newStr = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(origin));
                } catch (ParseException e) {
                    Dlog.e(TAG, e);
                }
            }
            return newStr;
        }

        public void execute(Activity context, String startTime, String endTime, ProcessListener processListener) {
            init();

            this.processListener = processListener;

            this.startTime = convertDateString(startTime);
            this.endTime = convertDateString(endTime);
            this.realEndTime = getPlusOneDayDateString(this.endTime);

            // 프로필을 앨범에서 가져오지 않기 위하여 나눔.
            // getBaseInfo()에서 기본 데이터와 썸네일프로필, 커버, 친구정보 등을 가져오고, getAlbumData()에서 앨범정보와 프로필 큰 이미지를 가져온다.
            getBaseInfo();
            getAlbumData();
            getPostList(null);
            if (showMyPostOnly)
                processFlags[FEED_FLAG_INDEX] = true;
            else
                getFeedList(null);
        }

        private String getPlusOneDayDateString(String dateString) {
//			String returnStr = "";
//
//			Calendar cal = Calendar.getInstance();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			try {
//				cal.setTime( sdf.parse(dateString) );
//				cal.add( Calendar.DAY_OF_MONTH, 1);
//				returnStr = cal.get( Calendar.YEAR ) + "-" + ( cal.get(Calendar.MONTH) + 1 ) + "-" + cal.get( Calendar.DAY_OF_MONTH );
//			} catch (Exception e) {
//				returnStr = dateString;
//				Dlog.e(TAG, e);
//			}
//
//			return returnStr;

            try {
                String[] splittedString = dateString.split("-");
                int year, month, day, lastDay;
                year = Integer.parseInt(splittedString[0]);
                month = Integer.parseInt(splittedString[1]);
                day = Integer.parseInt(splittedString[2]);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                cal.setTime(sdf.parse(year + "-" + month));
                lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (day > lastDay - 1) {
                    day = 1;
                    month = month > 11 ? 1 : month + 1;
                    if (month == 1) year++;
                } else day++;

                return year + "-" + (month > 9 ? month : "0" + month) + "-" + (day > 9 ? day : "0" + day);
            } catch (Exception e) {
                Dlog.e(TAG, e);
                return dateString;
            }
        }


        private void notiProcessDone(int index, boolean flag) {
            processFlags[index] = flag;
            checkAllProcessDone();
        }

        private synchronized void checkAllProcessDone() {
            for (int i = 0; i < processFlags.length; ++i)
                if (!processFlags[i])
                    return;

            makeTimelineList(true);
        }

        public ArrayList<AlbumData> getAlbumListByType(int type) {
            ArrayList<AlbumData> list = new ArrayList<AlbumData>();

            for (int i = 0; i < albums.size(); ++i)
                if (albums.get(i).type == type)
                    list.add(albums.get(i));

            return list;
        }

        public AlbumData getAlbumDataById(String id) {
            if (id == null || albums == null)
                return null;

            for (int i = 0; i < albums.size(); ++i)
                if (id.equals(albums.get(i).id)) {
                    return albums.get(i);
                }
            return null;
        }

        private void getBaseInfo() {
            GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONObject jobj = response.getJSONObject();
                        if (jobj == null) {
                            notiProcessDone(BASE_FLAG_INDEX, true);
                            return;
                        }

                        id = jobj.getString("id");
                        name = jobj.getString("name");
                        birthday = jobj.has("birthday") ? jobj.getString("birthday") : "";
                        email = jobj.has("email") ? jobj.getString("email") : "";

                        if (jobj.has("cover")) {
                            JSONObject cover = jobj.getJSONObject("cover");
                            if (cover.has("source")) {
                                coverUrl = cover.getString("source");
                            } else {
                                coverUrl = "";
                            }
                        }

                        if (jobj.has("friends")) {
                            JSONArray tempAry = jobj.getJSONObject("friends").getJSONArray("data");
                            JSONObject tempObj;
                            for (int i = 0; i < tempAry.length(); ++i) {
                                tempObj = tempAry.getJSONObject(i);
                                if (!friendsMap.containsKey(tempObj.getString("id")))
                                    friendsMap.put(tempObj.getLong("id"), new FriendData(tempObj.getString("id"), tempObj.getString("name")));
                            }
                        }

                        GraphRequest nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                        if (nextRequest != null) {
                            nextRequest.setCallback(this);
                            nextRequest.executeAsync();
                        } else
                            notiProcessDone(BASE_FLAG_INDEX, true);
                    } catch (JSONException e) {
                        Dlog.e(TAG, e);
                        if (processListener != null)
                            processListener.onError(e.getMessage());
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters
                    .putString(
                            "fields",
                            "id,name,friends,email,birthday,picture,cover");
            parameters.putString("date_format", "U");
            request.setParameters(parameters);
            request.executeAsync();
        }

        private class NextUrl {
            public String type, url;

            public NextUrl(String type, String url) {
                this.type = type;
                this.url = url;
            }
        }

        private ArrayList<NextUrl> nextUrlList;

        private void getAlbumData() {
            albums = new ArrayList<AlbumData>();
            nextUrlList = new ArrayList<NextUrl>();

            GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me", new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONObject jobj = response.getJSONObject();
                        if (jobj == null) {
                            notiProcessDone(ALBUM_FLAG_INDEX, true);
                            return;
                        }

                        JSONArray jary = jobj.has("albums") ? jobj.getJSONObject("albums").getJSONArray("data") : new JSONArray();
                        JSONArray tempAry;
                        JSONObject tempObj;
                        AlbumData temp;
                        int type;
                        String typeStr;
                        for (int i = 0; i < jary.length(); ++i) {
                            tempObj = jary.getJSONObject(i);
                            if (!tempObj.has("type"))
                                continue;

                            typeStr = tempObj.getString("type");
                            if ("cover".equalsIgnoreCase(typeStr))
                                type = AlbumData.TYPE_COVER;
                            else if ("profile".equalsIgnoreCase(typeStr))
                                type = AlbumData.TYPE_PROFILE;
                            else if ("wall".equalsIgnoreCase(typeStr))
                                type = AlbumData.TYPE_WALL;
                            else if ("mobile".equalsIgnoreCase(typeStr) || "app".equalsIgnoreCase(typeStr))
                                type = AlbumData.TYPE_MOBILE;
                            else
                                type = AlbumData.TYPE_NORMAL;

                            if (!tempObj.has("photos")) continue;
                            tempObj = tempObj.getJSONObject("photos");

                            if (tempObj.has("paging") && tempObj.getJSONObject("paging").has("next")) {
                                nextUrlList.add(new NextUrl(typeStr, tempObj.getJSONObject("paging").getString("next")));
                            }

                            tempAry = tempObj.getJSONArray("data");
                            for (int j = 0; j < tempAry.length(); ++j) {
                                tempObj = tempAry.getJSONObject(j);
                                temp = new AlbumData(tempObj, type);
                                if (FacebookUtil.checkDateInPeriod(temp.createDate, startTime, endTime)) {
                                    albums.add(temp);
                                }
                            }
                        }

                        if (nextUrlList.size() < 1)
                            notiProcessDone(ALBUM_FLAG_INDEX, true);
                        else requestNextAlbumData();
                    } catch (JSONException e) {
                        Dlog.e(TAG, e);
                        if (processListener != null)
                            processListener.onError(e.getMessage());
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "albums{type,photos{images,picture,sharedposts,likes,comments{comments{created_time,id,from,message},message,created_time,from,id},from,created_time}}");
            parameters.putString("date_format", "U");
            request.setParameters(parameters);
            request.executeAsync();
        }

        private void requestNextAlbumData() {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (nextUrlList == null || nextUrlList.size() < 1) {
                        notiProcessDone(ALBUM_FLAG_INDEX, true);
                        return;
                    }

                    String typeStr = nextUrlList.get(0).type;
                    int type;
                    if ("cover".equalsIgnoreCase(typeStr))
                        type = AlbumData.TYPE_COVER;
                    else if ("profile".equalsIgnoreCase(typeStr))
                        type = AlbumData.TYPE_PROFILE;
                    else if ("wall".equalsIgnoreCase(typeStr))
                        type = AlbumData.TYPE_WALL;
                    else if ("mobile".equalsIgnoreCase(typeStr) || "app".equalsIgnoreCase(typeStr))
                        type = AlbumData.TYPE_MOBILE;
                    else
                        type = AlbumData.TYPE_NORMAL;

                    String response = HttpUtil.connectGet(nextUrlList.get(0).url, null);
                    nextUrlList.remove(0);
                    try {
                        JSONObject jobj;
                        if (StringUtil.isEmpty(response) || (jobj = new JSONObject(response)) == null) {
                            notiProcessDone(ALBUM_FLAG_INDEX, true);
                            return;
                        }

                        if (jobj.has("paging") && jobj.getJSONObject("paging").has("next")) {
                            nextUrlList.add(new NextUrl(typeStr, jobj.getJSONObject("paging").getString("next")));
                        }

                        JSONArray jary = jobj.has("data") ? jobj.getJSONArray("data") : new JSONArray();
                        JSONObject tempObj;
                        AlbumData temp;
                        for (int i = 0; i < jary.length(); ++i) {
                            tempObj = jary.getJSONObject(i);
                            temp = new AlbumData(tempObj, type);
                            if (FacebookUtil.checkDateInPeriod(temp.createDate, startTime, endTime)) {
                                albums.add(temp);
                            }
                        }

                        if (nextUrlList.size() < 1)
                            notiProcessDone(ALBUM_FLAG_INDEX, true);
                        else requestNextAlbumData();
                    } catch (JSONException e) {
                        Dlog.e(TAG, e);
                        if (processListener != null)
                            processListener.onError(e.getMessage());
                    }
                }
            });
        }

        final GraphRequest.Callback feedCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null) {
                        mCompleteHandler.sendEmptyMessage(FEED_FLAG_INDEX);
                        return;
                    }
                    JSONArray jary = jobj == null ? new JSONArray() : jobj.has("data") ? jobj.getJSONArray("data") : new JSONArray();

                    if (feeds == null) feeds = new ArrayList<FeedData>();
                    for (int i = 0; i < jary.length(); ++i) {
                        JSONObject j = jary.getJSONObject(i);
                        FeedData temp = new FeedData(j);
                        if (((!StringUtil.isEmpty(temp.message) || (temp.attachmentList != null && temp.attachmentList.size() > 0)) // 메세지나 attachment가 있고
                                && !temp.isPostedByQuestionApplication() // questions application으로 올린 포스트가 아니며
                                && !temp.isVideoPostWithoutThumbnail() // thumbnail이 없는 video 포스트가 아니며
                                && !(j.has("attachments") && j.getJSONObject("attachments").toString().contains("\"type\":\"map\"")))) { // attachment 중 type:map이 없을때만 리스트에 추가.
                            feeds.add(temp);
                        }
                    }

                    String nextRequest = jobj == null ? "" : jobj.has("paging") ? jobj.getJSONObject("paging").getString("next") : "";
                    if (nextRequest != null && nextRequest.length() > 0) getFeedList(nextRequest);
                    else mCompleteHandler.sendEmptyMessage(FEED_FLAG_INDEX);
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    if (processListener != null)
                        processListener.onError(e.getMessage());
                }
            }
        };

        final GraphRequest.Callback postCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null) {
                        mCompleteHandler.sendEmptyMessage(POST_FLAG_INDEX);
                        return;
                    }
                    JSONArray jary = jobj == null ? new JSONArray() : jobj.has("data") ? jobj.getJSONArray("data") : new JSONArray();

                    if (posts == null) posts = new ArrayList<PostData>();
                    for (int i = 0; i < jary.length(); ++i) {
                        JSONObject j = jary.getJSONObject(i);
                        PostData temp = new PostData(j);
                        if (((!StringUtil.isEmpty(temp.message) || (temp.attachmentList != null && temp.attachmentList.size() > 0)) // 메세지나 attachment가 있고
                                && !temp.isPostedByQuestionApplication() // questions application으로 올린 포스트가 아니며
                                && !temp.isVideoPostWithoutThumbnail() // thumbnail이 없는 video 포스트가 아니며
                                && !temp.isMemorySharePost() // 추억 공유 포스트가 아니며.
                                && !(j.has("attachments") && j.getJSONObject("attachments").toString().contains("\"type\":\"map\"")))) { // attachment 중 type:map이 없을때만 리스트에 추가.
                            posts.add(temp);
                        }
                    }

                    String nextRequest = jobj == null ? "" : jobj.has("paging") ? jobj.getJSONObject("paging").getString("next") : "";
                    if (nextRequest != null && nextRequest.length() > 0) getPostList(nextRequest);
                    else mCompleteHandler.sendEmptyMessage(POST_FLAG_INDEX);
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    if (processListener != null)
                        processListener.onError(e.getMessage());
                }
            }
        };

        private HashMap<String, String> parseNextUrl(String nextUrl) {
            if (StringUtil.isEmpty(nextUrl)) return null;

            try {
                HashMap<String, String> params = new HashMap<String, String>();
                String result = URLDecoder.decode(nextUrl, "UTF-8");
                if (StringUtil.isEmpty(result) || !result.contains("&") || !result.contains("=") || !result.contains("?"))
                    return null;
                result = result.substring(result.indexOf("?") + 1);
                String[] splited = result.split("&");
                String[] temp;
                for (int i = 0; i < splited.length; ++i) {
                    if (!splited[i].contains("=")) continue;

                    temp = splited[i].split("=");
                    if (temp != null && temp.length > 1)
                        params.put(temp[0], temp[1]);
                }
                return params;
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
                return null;
            }
        }

        private void getPostList(final String nextUrl) {
            Bundle parameters = new Bundle();

            if (StringUtil.isEmpty(nextUrl)) {
                parameters.putString("fields", "name,from,story_tags,shares,full_picture,created_time,attachments.limit(" + showPhotoType + "){media,description,target,title,type,url,subattachments.limit(" + showPhotoType + ")},story,description,comments.limit(100){id,from,likes{id,name},created_time,message,comments.limit(100){id,from,likes.limit(100){id,name},created_time,message}},likes.limit(100){username,picture{url},name},message,type,picture");
                parameters.putString("since", startTime);
                parameters.putString("until", realEndTime);
            } else {
                HashMap<String, String> p = parseNextUrl(nextUrl);
                if (p.containsKey("fields")) parameters.putString("fields", p.get("fields"));
                if (p.containsKey("__paging_token"))
                    parameters.putString("__paging_token", p.get("__paging_token"));
                if (p.containsKey("since")) parameters.putString(" since", p.get("since"));
                if (p.containsKey("until")) parameters.putString(" until", p.get("until"));
            }

            parameters.putString("date_format", "U");
            parameters.putString("locale", "ko_KR");
            parameters.putString("limit", "100");

            GraphRequest newRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/posts", postCallback);
            ;
            newRequest.setParameters(parameters);
            newRequest.executeAsync();
        }

        private void getFeedList(final String nextUrl) {
            Bundle parameters = new Bundle();

            if (StringUtil.isEmpty(nextUrl)) {
                parameters.putString("fields", "created_time,name,id,picture,message,type,event,story,story_tags,description,from,shares,attachments.limit(" + showPhotoType + "){media,description,target,title,url,type,subattachments.limit(" + showPhotoType + ")},full_picture,comments.limit(100){id,from,created_time,message,comments.limit(100){id,from,created_time,message},likes.limit(100){id,name,picture{url}}},likes.limit(100){id,name,picture{url}}");
                parameters.putString("since", startTime);
                parameters.putString("until", realEndTime);
            } else {
                HashMap<String, String> p = parseNextUrl(nextUrl);
                if (p.containsKey("fields")) parameters.putString("fields", p.get("fields"));
                if (p.containsKey("__paging_token"))
                    parameters.putString("__paging_token", p.get("__paging_token"));
                if (p.containsKey("since")) parameters.putString(" since", p.get("since"));
                if (p.containsKey("until")) parameters.putString(" until", p.get("until"));
            }

            parameters.putString("date_format", "U");
            parameters.putString("locale", "ko_KR");
            parameters.putString("limit", "100");

            GraphRequest newRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/feed", feedCallback);
            ;
            newRequest.setParameters(parameters);
            newRequest.executeAsync();
        }
    }

    /**
     * 타임라인 생성순
     */
    public static class NumberAscCompare implements Comparator<TimelineData> {
        @Override
        public int compare(TimelineData arg0, TimelineData arg1) {
            return arg0.createTime < arg1.createTime ? -1 : arg0.createTime > arg1.createTime ? 1 : 0;
        }
    }

    /**
     * 앨범 좋아요, 커멘트 합산 포인트 내림차순
     */
    public static class AlbumCommentDescCompare implements Comparator<AlbumData> {
        @Override
        public int compare(AlbumData arg0, AlbumData arg1) {
            return arg0.getPoint() < arg1.getPoint() ? 1 : arg0.getPoint() > arg1.getPoint() ? -1 : 0;
        }
    }

    /**
     * 프로필 최신순.
     */
    public static class ProfileDateDescCompare implements Comparator<AlbumData> {
        @Override
        public int compare(AlbumData arg0, AlbumData arg1) {
            return arg0.createTime < arg1.createTime ? 1 : arg0.createTime > arg1.createTime ? -1 : 0;
        }
    }

    /**
     * 친구 가까운순.
     */
    public static class FriendPointDescCompare implements Comparator<FriendData> {
        @Override
        public int compare(FriendData arg0, FriendData arg1) {
            return arg0.commentCount + arg0.likeCount < arg1.commentCount + arg1.likeCount ? 1 : arg0.commentCount + arg0.likeCount > arg1.commentCount + arg1.likeCount ? -1 : 0;
        }
    }

    /**
     * int배열 두개 합.
     */
    public static int[] combineAry(int[] ary1, int[] ary2) {
        if (ary1 == null || ary1.length < 0)
            return ary2;
        else if (ary2 == null || ary2.length < 0)
            return ary1;

        int size1 = ary1.length, size2 = ary2.length;
        int[] result = new int[Math.max(size1, size2)];
        for (int i = 0; i < result.length; ++i)
            result[i] = (i < size1 ? ary1[i] : 0) + (i < size2 ? ary2[i] : 0);
        return result;
    }

    private static boolean checkDateInPeriod(Calendar date, String startStr, String endStr) {
        String[] startAry = startStr.split("-");
        String[] endAry = endStr.split("-");
        if (startAry == null || startAry.length < 3 || endAry == null || endAry.length < 3)
            return false;

        int start = Integer.parseInt(startAry[0]) * 10000 + Integer.parseInt(startAry[1]) * 100 + Integer.parseInt(startAry[2]);
        int end = Integer.parseInt(endAry[0]) * 10000 + Integer.parseInt(endAry[1]) * 100 + Integer.parseInt(endAry[2]);
        int current = date.get(Calendar.YEAR) * 10000 + (date.get(Calendar.MONTH) + 1) * 100 + date.get(Calendar.DAY_OF_MONTH);

        return start - 1 < current && end + 1 > current;
    }

    public static String getDateDiffString(long createdTime) {
        String result = "";
        long diff = Calendar.getInstance().getTimeInMillis() / 1000 - createdTime;
        Calendar created = Calendar.getInstance();
        created.setTimeInMillis(createdTime * 1000);

        if (diff < 60) result = ContextUtil.getString(R.string.just_a_moment, "방금");
        else if (diff < 3600) result = (diff / 60) + ContextUtil.getString(R.string.min_ago, "분전");
        else if (diff < 86400)
            result = (diff / 3600) + ContextUtil.getString(R.string.hour_ago, "시간전");
        else if (diff < 2592000)
            result = (diff / 86400) + ContextUtil.getString(R.string.day_ago, "일전");
        else {
            int monthDiff = (Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance().get(Calendar.MONTH) + 1)
                    - (created.get(Calendar.YEAR) * 12 + created.get(Calendar.MONTH) + 1);

            if (monthDiff < 12)
                result = monthDiff + ContextUtil.getString(R.string.month_ago, "개월전");
            else result = (monthDiff / 12) + ContextUtil.getString(R.string.year_ago, "년전");
        }
        return result;
    }
}