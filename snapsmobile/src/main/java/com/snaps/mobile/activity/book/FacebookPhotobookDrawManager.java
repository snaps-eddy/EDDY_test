package com.snaps.mobile.activity.book;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_KAKAKAO;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;

import errorhandle.logger.Logg;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.facebook.model.sns.facebook.AlbumData;
import com.snaps.facebook.model.sns.facebook.AttachmentData;
import com.snaps.facebook.model.sns.facebook.ChapterData;
import com.snaps.facebook.model.sns.facebook.CommentData;
import com.snaps.facebook.model.sns.facebook.FriendData;
import com.snaps.facebook.model.sns.facebook.TimelineData;
import com.snaps.facebook.utils.FBCommonUtil;
import com.snaps.facebook.utils.sns.FacebookUtil;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;
import com.snaps.facebook.utils.sns.FacebookUtil.ProcessListener;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.utils.ui.CalcViewRectUtil;
import com.snaps.mobile.utils.ui.MultiLineTextData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

public class FacebookPhotobookDrawManager {
    private static final String TAG = FacebookPhotobookDrawManager.class.getSimpleName();

    private final String DEFAULT_PROFILE_IMG_URL = SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/sticker/edit/Est208_gg.png";
    private final String DEFAULT_QR_CODE_THUMBNAIL_IMG_URL = SnapsAPI.DOMAIN() + "/Upload/Data1/Resource/sticker/Medit/Est287_gg.png";

    private final int MARGIN_VALUE_FRIEND_GRID = 3;
    private final int ROW_COUNT_FRIEND_GRID = 8;
    private final int COLUMN_COUNT_FRIEND_GRID = 8;

    private final int STICK_FIX_MARGIN_VALUE = 8;

    private final static int BASE_AREA_COUNT_PER_CHAPTER = 125;
    private final static int SPLIT_AREA_COUNT_PER_CHAPTER = 200;
    private final static int MIN_COMBINE_AREA_COUNT_PER_CHAPTER = 100;
    private final static int MAX_COMBINE_AREA_COUNT_PER_CHAPTER = 150;

    public static final int MULTI_TEMPLATE_COVER_IDX = 0;
    public static final int MULTI_TEMPLATE_INDEX_IDX = 1;
    public static final int MULTI_TEMPLATE_TITLE_IDX = 2;
    public static final int MULTI_TEMPLATE_PAGE_IDX = 3;

    protected BookMaker maker;

    protected Context context;

    protected SnapsTemplate pageTemplate;

    private SnapsPage chapter;
    private SnapsPage pageChapter;
    private SnapsPage pageInner;
    private SnapsPage pageLast;

    // 페이지 그리는데 필요한 데이터.
    private SnapsControl stCamera, stWrite, stVideo, stShare, stMessage, st2Like, st2Comment, st2Share, divideLine;
    private SnapsControl txDate, txTime, txWrite, txCommentName, txComment, txLikeCount, txCommentCount, txShareCount, txPageMonth;
    private SnapsControl userImage, postImage;
    private int[] screenSize;
    private final int[] baseX = new int[]{15, 164, 363, 512};
    private final int baseChapterY = 107, baseInnerY = 39;
    private final int[] postPicX = new int[]{baseX[0] + 1, baseX[1] + 1, baseX[2] + 1, baseX[3] + 1};
    private final int[] sharedX = new int[]{postPicX[0] + 1, postPicX[1] + 1, postPicX[2] + 1, postPicX[3] + 1};
    private final int[] contentsX = new int[]{postPicX[0] + 5, postPicX[1] + 5, postPicX[2] + 5, postPicX[3] + 5};
    private final int[] replyX = new int[]{postPicX[0] + 30, postPicX[1] + 30, postPicX[2] + 30, postPicX[3] + 30};
    private final int topLineW = 137, areaW = topLineW - 2;
    private final int tMarinChapter = 107, tMarginInner = 39, lMargin = 16, rMargin = 16, bMargin = 21, pageW = 137;
    private final int lAddtionalMargin = 5;
    private final int commentAdditionalMargin = 2;
    private int baseY, currentY, areaNumber;

    private boolean isChapterPage;
    private boolean isMaxPageEdited = false;

    protected int totalPage = 0;
    private int areaCount = 0;

    public final int COMMENT_BG_MARGIN = 5;
    public final int FROM_BG_MARGIN = 3;

    private HashMap<Long, FriendData> friendsMap;

    private LinkedBlockingQueue<GraphRequest> linkedQueue;
    private ArrayList<GraphRequest> workingList;
    private ProcessListener getProfileProcessListener;

    public FacebookPhotobookDrawManager(Context context) {
        this.context = context;
        maker = BookMaker.getInstance();
    }

    public BookMaker getMaker() {
        return this.maker;
    }


    public void makeProfileData(ProcessListener listener) {
        this.getProfileProcessListener = listener;

        maker.updateProgress(1);
        linkedQueue = new LinkedBlockingQueue<GraphRequest>();
        workingList = new ArrayList<GraphRequest>();

        setCoverData();
        setProfileData(true);
        setProfileData(false);

        // 친구 이미지 가져올때 호출 빈도를 줄이기 위한 수정.
        setFriendProfileData();

        if (listener != null && linkedQueue.size() < 1) listener.onComplete(null);
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


    private void checkProccessDone(GraphRequest request) {
        synchronized (linkedQueue) {
            workingList.remove(request);
            maker.updateProgress(20 - (int) ((float) linkedQueue.size() / (float) (((friendsMap.size() / 10) + 1) * 2 + 4) * 19f));
            ;
            if (linkedQueue.size() < 1 && workingList.size() < 1 && getProfileProcessListener != null) {
                maker.setFriendList(friendsMap);
                getProfileProcessListener.onComplete(null);
            }
        }
    }

    private void addRequest(GraphRequest request) {
        synchronized (linkedQueue) {
            linkedQueue.add(request);
        }
    }

    public void setCoverData() {
        if (maker.coverId == null || maker.coverId.length() < 1) {
            return;
        }

        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), maker.coverId, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getRawResponse() == null) {
                    checkProccessDone(response.getRequest());
                    setCoverData();
                    return;
                }
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null || !jobj.has("images")) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    JSONArray jary = jobj.getJSONArray("images");
                    int[] size = new int[2];
                    int temp = 0;
                    String url = "";
                    for (int i = 0; i < jary.length(); ++i) {
                        jobj = jary.getJSONObject(i);
                        temp = jobj.has("width") ? jobj.getInt("width") : -1;
                        if (temp > size[0]) {
                            size[0] = temp;
                            size[1] = jobj.getInt("height");
                            url = jobj.getString("source");
                        }
                    }

                    maker.setCoverData(url, size);
                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        request.setParameters(parameters);
        addRequest(request);
    }

    public void setProfileData(final boolean origin) {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/picture", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getRawResponse() == null) {
                    setProfileData(origin);
                    checkProccessDone(response.getRequest());
                }

                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null || !jobj.has("data")) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    if (origin) maker.setOriginData(jobj.getJSONObject("data"));
                    else maker.setThumbData(jobj.getJSONObject("data"));
                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("redirect", "false");
        parameters.putString("width", origin ? "300" : "200");
        request.setParameters(parameters);
        addRequest(request);
    }

    public void getFriendProfileDatas(final ArrayList<String> ids) {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "picture", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    for (int i = 0; i < ids.size(); ++i)
                        if (jobj.has(ids.get(i)) && jobj.getJSONObject(ids.get(i)).has("data") && friendsMap.get(Long.parseLong(ids.get(i))) != null) {
                            Long id = Long.parseLong(ids.get(i));
                            FriendData data = friendsMap.get(id);
                            friendsMap.remove(id);
                            data.setThumbProfile(jobj.getJSONObject(ids.get(i)).getJSONObject("data"));
                            friendsMap.put(id, data);
                        }

                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("ids", getFriendIdString(ids));
        parameters.putString("redirect", "false");
        parameters.putString("width", "200");
        request.setParameters(parameters);
        addRequest(request);

        request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "picture", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    for (int i = 0; i < ids.size(); ++i)
                        if (jobj.has(ids.get(i)) && jobj.getJSONObject(ids.get(i)).has("data") && friendsMap.get(Long.parseLong(ids.get(i))) != null) {
                            Long id = Long.parseLong(ids.get(i));
                            FriendData data = friendsMap.get(id);
                            friendsMap.remove(id);
                            data.setOriginProfile(jobj.getJSONObject(ids.get(i)).getJSONObject("data"));
                            friendsMap.put(id, data);
                        }

                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        parameters = new Bundle();
        parameters.putString("ids", getFriendIdString(ids));
        parameters.putString("redirect", "false");
        parameters.putString("width", "300");
        request.setParameters(parameters);
        addRequest(request);
    }

    public String getFriendIdString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(",");
        }
        return sb.toString();
    }


    public void setFriendProfileData() {
        friendsMap = new HashMap<Long, FriendData>();
        for (FriendData a : maker.getFrieldList()) friendsMap.put(Long.parseLong(a.id), a);

        ArrayList<String> idList = new ArrayList<String>();
        for (FriendData a : maker.getFrieldList()) {
            if (idList.size() < 10) idList.add(a.id);

            if (idList.size() > 9) {
                getFriendProfileDatas(idList);
                idList = new ArrayList<String>();
            }
        }
        if (idList.size() > 0) getFriendProfileDatas(idList);
    }

    public void setProfileData(final int index) {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), maker.getFrieldList().get(index).id + "/picture", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null || !jobj.has("data")) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    maker.getFrieldList().get(index).setThumbProfile(jobj.getJSONObject("data"));
                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("redirect", "false");
        parameters.putString("width", "200");
        request.setParameters(parameters);
        addRequest(request);

        request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), maker.getFrieldList().get(index).id + "/picture", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                try {
                    JSONObject jobj = response.getJSONObject();
                    if (jobj == null || !jobj.has("data")) {
                        checkProccessDone(response.getRequest());
                        return;
                    }

                    maker.getFrieldList().get(index).setOriginProfile(jobj.getJSONObject("data"));
                    checkProccessDone(response.getRequest());
                } catch (JSONException e) {
                    Dlog.e(TAG, e);
                    checkProccessDone(response.getRequest());
                }
            }
        });

        parameters = new Bundle();
        parameters.putString("redirect", "false");
        parameters.putString("width", "300");
        request.setParameters(parameters);
        addRequest(request);
    }

    public void makePages(SnapsTemplate template) {
        drawFriends(template);
        maker.updateProgress(20);
        drawPage(template);
        drawCover(template);
        maker.updateProgress(95);
        drawSummary(template);
        maker.updateProgress(96);
        drawPicture(template);
        maker.updateProgress(97);
        drawIndex(template);
        maker.updateProgress(98);
    }

    protected void drawPage(SnapsTemplate template) {
        // 샘플페이지 추출.
        chapter = getPage(template, FacebookPhotobookPageType.CHAPTER);
        pageChapter = getPage(template, FacebookPhotobookPageType.PAGE_CHAPTER);
        pageInner = getPage(template, FacebookPhotobookPageType.PAGE_INNER);
        pageLast = getPage(template, FacebookPhotobookPageType.PAGE_LAST);
        template.getPages().remove(FacebookPhotobookPageType.CHAPTER.getIndex());
        template.getPages().remove(FacebookPhotobookPageType.CHAPTER.getIndex());
        template.getPages().remove(FacebookPhotobookPageType.CHAPTER.getIndex());
        template.getPages().remove(FacebookPhotobookPageType.CHAPTER.getIndex());

        setBaseControls();
        makeSamplePageSet();
        setChapters(maker.chapterList);

        // 새로운 페이지 제작하여, 템플릿에 삽입.
        template.getPages().addAll(FacebookPhotobookPageType.CHAPTER.getIndex(), drawFullChapterData(template.getPages().size()));
    }

    private void setBaseControls() {
        screenSize = new int[]{Integer.parseInt(pageChapter.width), Integer.parseInt(pageChapter.height)};

        // 기본형 복사 후 지우기, 클립아트, 이미지, 텍스트 순.
        ArrayList<SnapsPage> pages = new ArrayList<SnapsPage>();
        ArrayList<SnapsControl> controls = pageChapter.getControlList();
        SnapsLayoutControl layoutTemp;
        SnapsTextControl textTemp;
        SnapsClipartControl clipTemp;

        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsClipartControl) {
                clipTemp = (SnapsClipartControl) controls.get(i);
                if ("sticker_camera".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    stCamera = makeStickerControl(clipTemp);
                else if ("sticker_text".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    stWrite = makeStickerControl(clipTemp);
                else if ("sticker_message".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    stMessage = makeStickerControl(clipTemp);
                else if ("sticker_video".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    stVideo = makeStickerControl(clipTemp);
                else if ("sticker_sharing".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    stShare = makeStickerControl(clipTemp);
                    // snsproperty 없어서 하드코딩.
                else if ("sticker_line".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    divideLine = makeStickerControl(clipTemp);
                else if ("sticker_like".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    st2Like = makeStickerControl(clipTemp);
                else if ("sticker_reply".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    st2Comment = makeStickerControl(clipTemp);
                else if ("sticker_sharing_2".equalsIgnoreCase(clipTemp.getSnsproperty()))
                    st2Share = makeStickerControl(clipTemp);
                else clipTemp = null;

                if (clipTemp != null) {
                    controls.remove(i);
                    i--;
                }
            } else if (controls.get(i) instanceof SnapsTextControl) {
                textTemp = (SnapsTextControl) controls.get(i);
                if ("story_date".equalsIgnoreCase(textTemp.getSnsproperty()))
                    txDate = textTemp.copyControl();
                else if ("story_time".equalsIgnoreCase(textTemp.getSnsproperty()))
                    txTime = textTemp.copyControl();
                else if ("inner_month".equalsIgnoreCase(textTemp.getSnsproperty())) {
                    txPageMonth = textTemp.copyControl();
                    textTemp = null;
                } else if ("text_body".equalsIgnoreCase(textTemp.getSnsproperty())) {
                    txWrite = textTemp.copyControl();
                    ((SnapsTextControl) txWrite).format.fontSize = "5";
                } else if ("text_like".equalsIgnoreCase(textTemp.getSnsproperty()))
                    txLikeCount = textTemp.copyControl();
                else if ("text_reply".equalsIgnoreCase(textTemp.getSnsproperty()))
                    txCommentCount = textTemp.copyControl();
                else if ("text_sharing".equalsIgnoreCase(textTemp.getSnsproperty()))
                    txShareCount = textTemp.copyControl();
                else if ("text_friendname".equalsIgnoreCase(textTemp.getSnsproperty())) {
                    txCommentName = textTemp.copyControl();
                    ((SnapsTextControl) txCommentName).format.fontSize = "3";
                } else if ("text_friendreply".equalsIgnoreCase(textTemp.getSnsproperty())) {
                    txComment = textTemp.copyControl();
                    ((SnapsTextControl) txComment).format.fontSize = "3";
                } else textTemp = null;

                if (textTemp != null) {
                    controls.remove(i);
                    i--;
                }
            }
        }

        controls = pageChapter.getLayoutList();
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsLayoutControl && "user_image".equals(controls.get(i).regName)) {
                layoutTemp = (SnapsLayoutControl) controls.get(i);
                if ("0".equals(layoutTemp.regValue)) userImage = layoutTemp.copyImageControl();
                else if ("1".equals(layoutTemp.regValue)) postImage = layoutTemp.copyImageControl();
                else layoutTemp = null;

                if (layoutTemp != null) {
                    controls.remove(i);
                    i--;
                }
            }
        }
    }

    private SnapsClipartControl makeStickerControl(SnapsClipartControl control) {
        SnapsClipartControl clipart = new SnapsClipartControl();

        clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;

        clipart.angle = control.angle;
        clipart.alpha = control.alpha;

        clipart.setX(control.x);
        clipart.y = control.y;
        clipart.width = control.width;
        clipart.height = control.height;
        clipart.resourceURL = control.resourceURL;
        clipart.clipart_id = control.clipart_id;

        return clipart;
    }

    // 재정렬한 챕터로 다시 그림.
    @SuppressWarnings("unchecked")
    private ArrayList<SnapsPage> drawFullChapterData(int currentPageSize) {
        ArrayList<SnapsPage> pages = new ArrayList<SnapsPage>();
        ArrayList<SnapsPage> tempPageList = new ArrayList<SnapsPage>();
        ; // 중간중간 제작하여 전달받을 페이지들.
        ArrayList<TimelineData> timelines;
        SnapsPage tempPage = null, savedTempPage; // 현재 작업중인 페이지.
        ChapterData chapter = null;
        int pageCount;
        int timelineCount = 0;
        int currentTimeline = 0;
        for (int i = 0; i < maker.chapterList.size(); ++i)
            timelineCount += maker.chapterList.get(i).timelines.size();
        for (int i = 0; i < maker.chapterList.size(); ++i) {
            if (SNSBookInfo.getPageCount(pages.size() + tempPageList.size() + currentPageSize) > FacebookPhotobookFragmentActivity.LIMIT_MAX_PAGE_COUNT - 3) { // 챕터 페이지만 들어가면 안되니 기본적으로 2페이지 더 추가하여 체크.
                isMaxPageEdited = true;
                maker.chapterList.remove(i);
                i--;
                continue;
            }

            chapter = maker.chapterList.get(i);
            isChapterPage = true;
            timelines = (ArrayList<TimelineData>) chapter.timelines.clone();
            TimelineData temp;
            pageCount = 0;
            pages.add(drawChapter(chapter));

            currentY = baseChapterY; // 초기화.
            areaNumber = maker.templateType == BookMaker.TYPE_C ? 1 : 0;

            while (timelines.size() > 0 && pages.size() * 2 < FacebookPhotobookFragmentActivity.LIMIT_MAX_PAGE_COUNT - 10) {
                temp = timelines.get(0);

                if (tempPage == null) {
                    if (isChapterPage) {
                        tempPage = pageChapter.copyPage(FacebookPhotobookPageType.PAGE_CHAPTER.getIndex());
                        setPageChapterBaseData(tempPage, chapter);
                    } else
                        tempPage = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }

                savedTempPage = tempPage.copyPage(tempPage.getPageID());

                areaCount = 1;
                tempPageList = makePage(tempPage, temp);

                if (SNSBookInfo.getPageCount(pages.size() + tempPageList.size() + currentPageSize) > FacebookPhotobookFragmentActivity.LIMIT_MAX_PAGE_COUNT - 1) {
                    isMaxPageEdited = true;
                    tempPage = savedTempPage.copyPage(savedTempPage.getPageID());
                    break;
                }
                for (int j = 0; j < tempPageList.size(); ++j) {
                    if (j == tempPageList.size() - 1)
                        tempPage = tempPageList.get(j); // 마지막껀 다시 tempPage에 넣어 이어 그림.
                    else {
                        pages.add(tempPageList.get(j)); // 그 앞에 페이지들은 리턴할 페이지에 추가.
                        pageCount++;
                    }
                }
                temp.areaCount = areaCount;
                chapter.timelines.add(temp);
                timelines.remove(temp);

                currentTimeline++;
                maker.updateProgress(57 + (int) (currentTimeline / (float) timelineCount * 38f));
            }

            if (tempPage != null) {
                // 챕터 끝의 한바닥이 비면 바꿔줌.
                setLastPageProcess(tempPage);
                pages.add(tempPage);
                tempPage = null;
                pageCount++;
            }
            chapter.pageCounts = pageCount;
        }

        return pages;
    }

    private void setLastPageProcess(SnapsPage page) {
        if (areaNumber > 1) return;

        //inner_month가 2개가 있는 경우도 있음. 한번더 호출...
        page.removeText("inner_month");
        page.removeText("inner_month");

        SnapsBgControl bg = pageLast.getBgControl();
        if (bg != null) page.changeBg(bg);
    }

    private void setPageChapterBaseData(SnapsPage page, ChapterData chapter) {
        SnapsTextControl temp;

        for (SnapsControl textContrl : page.getTextControlList()) {
            temp = (SnapsTextControl) textContrl;

            if (maker.getTemplateType() == BookMaker.TYPE_C) {
                if (temp.getSnsproperty().equals("endmonth"))
                    temp.text = chapter.getEndMonthStr(true) + " " + chapter.getEndYear();
                else if (temp.getSnsproperty().equals("startmonth"))
                    temp.text = chapter.getStartMonthStr(true) + " " + chapter.getStartYear();
                else if ("feeling".equals(temp.getSnsproperty()))
                    temp.text = "" + chapter.getSummary()[BookMaker.LIKE_COUNT_INDEX];
                else if ("replies".equals(temp.getSnsproperty()))
                    temp.text = "" + chapter.getSummary()[BookMaker.COMMENT_COUNT_INDEX];
                else if ("sharing".equals(temp.getSnsproperty()))
                    temp.text = "" + chapter.getSummary()[BookMaker.SHARE_COUNT_INDEX];
                continue;
            }

            if (temp.getSnsproperty().equals("period")) {
                if (temp.getFormat().equals("MONTH YYYY - MONTH YYYY")) {
                    if (maker.templateType != BookMaker.TYPE_D) temp.text = "• ";
                    temp.text += chapter.getStartMonthStr(true) + " " + chapter.getStartYear() + " - " + chapter.getEndMonthStr(true) + " " + chapter.getEndYear();
                    if (maker.templateType != BookMaker.TYPE_D) temp.text += " •";
                } else if (temp.getFormat().equals("MONTH YYYY -")) {
                    temp.text = chapter.getStartMonthStr(true) + " " + chapter.getStartYear() + " -";
                } else if (temp.getFormat().equals("MONTH YYYY")) {
                    temp.text = chapter.getEndMonthStr(true) + " " + chapter.getEndYear();
                }

            }
        }

        SnapsControl temp2 = page.getControlByProperty("chapter");
        if (temp2 != null && temp2 instanceof SnapsTextControl)
            ((SnapsTextControl) temp2).text = chapter.getChapterIndexStr();

        page.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

    }

    private void setPageChapterMonthData(SnapsPage page, TimelineData data) {
        ArrayList<SnapsControl> controls = page.getControlListByProperty("inner_month");
        SnapsTextControl full = null, month = null, year = null, temp;
        for (int i = 0; i < controls.size(); ++i) {
            if (!(controls.get(i) instanceof SnapsTextControl)) continue;
            temp = (SnapsTextControl) controls.get(i);
            SnapsPage.setTextControlFont(temp, Const_PRODUCT.AURATEXT_RATION);
            if ("MM".equalsIgnoreCase(temp.getFormat())) month = temp;
            else if ("YYYY".equalsIgnoreCase(temp.getFormat())) year = temp;
            else full = temp;
        }

        if (full != null) {
            if ("".equalsIgnoreCase(full.text)) {
                full.text = data.year + (data.month < 10 ? ".0" : ".") + data.month;
            }
        } else if (month != null && year != null) {
            if ("".equalsIgnoreCase(month.text) && "".equalsIgnoreCase(year.text)) {
                month.text = (data.month < 10 ? "0" : "") + data.month;
                year.text = "" + data.year;
            }
        }
    }

    private SnapsPage drawChapter(ChapterData chapter) {
        SnapsPage tempPage = this.chapter.copyPage(FacebookPhotobookPageType.CHAPTER.getIndex());
        setChapterData(tempPage, chapter);
        tempPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
        return tempPage;
    }

    // 계산용 샘플 챕터와 페이지 제작. temp 변수의 남발.
    private void makeSamplePageSet() {
        ArrayList<SnapsPage> pages = new ArrayList<SnapsPage>();
        ArrayList<SnapsPage> tempPageList; // 중간중간 제작하여 전달받을 페이지들.
        SnapsPage tempPage = null;    // 현재 작업중인 페이지.
        @SuppressWarnings("unchecked")
        ArrayList<TimelineData> timelines = (ArrayList<TimelineData>) maker.timelines.clone();

        maker.chapterList = new ArrayList<ChapterData>();
        ChapterData chapter = null;
        TimelineData temp;
        final int timelineCount = timelines.size();
        while (timelines.size() > 0) {
            maker.updateProgress(20 + (int) ((timelineCount - timelines.size()) / (float) timelineCount * 37f));

            temp = timelines.get(0);
            if (chapter == null) {
                chapter = new ChapterData();
                // 위치값 초기화.
                currentY = baseInnerY;
                areaNumber = 0;
            }

            // 제작 도중 월이 바뀌면 끝.
            if (chapter.timelines.size() > 0 && (chapter.getStartYear() != temp.year || chapter.getStartMonth() != temp.month)) {
                if (tempPage != null) {
                    pages.add(tempPage); // 작업중이던 페이지를 저장해주고,
                    tempPage = null;    // 비움.
                }

                chapter.pageCounts = pages.size();
                chapter.refreshCount(false);
                pages = new ArrayList<SnapsPage>();
                maker.chapterList.add(chapter);
                chapter = null;    // 챕터도 다시 만들수 있게 비움.
                continue;
            }

            if (tempPage == null)
                tempPage = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());

            areaCount = 1;
            tempPageList = makePage(tempPage, temp);
            for (int j = 0; j < tempPageList.size(); ++j) {
                if (j == tempPageList.size() - 1)
                    tempPage = tempPageList.get(j); // 마지막껀 다시 tempPage에 넣어 이어 그림.
                else pages.add(tempPageList.get(j)); // 그 앞에 페이지들은 리턴할 페이지에 추가.
            }
            temp.areaCount = areaCount;
            chapter.timelines.add(temp);
            timelines.remove(temp);
        }

        if (tempPage != null) pages.add(tempPage);
        chapter.pageCounts = pages.size();
        chapter.refreshCount(false);
        maker.chapterList.add(chapter);
    }

    private void setChapters(ArrayList<ChapterData> list) {
        int index = 0;
        int areaCount = 0;

        ArrayList<ChapterData> tempList;
        ChapterData temp, temp2;
        while (index < list.size()) {
            temp = list.get(index);
            if (temp.areaCount > SPLIT_AREA_COUNT_PER_CHAPTER) {
                tempList = splitChapter(temp);
                list.remove(index);
                list.addAll(index, tempList);
            } else if (temp.areaCount < MIN_COMBINE_AREA_COUNT_PER_CHAPTER && index < list.size() - 1) {
                temp = list.get(index);
                areaCount = temp.areaCount;
                for (int i = index + 1; i < list.size(); ++i) {
                    temp2 = list.get(i);
                    areaCount += temp2.areaCount;
                    if (areaCount < MAX_COMBINE_AREA_COUNT_PER_CHAPTER) {
                        temp = combineChapter(temp, temp2);
                        list.remove(i);
                        i--;
                        if (areaCount > BASE_AREA_COUNT_PER_CHAPTER) break;
                    } else break;
                }
                list.remove(index);
                list.add(index, temp);
            }

            // 마지막 챕터가 너무 짧게 나오는 경우 보정.
            if (list.get(list.size() - 1).areaCount < 10 && list.size() > 1) {
                temp2 = list.get(list.size() - 1);
                temp = combineChapter(list.get(list.size() - 2), temp2);
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                list.add(temp);
            }
            index++;
        }

        for (int i = 0; i < list.size(); ++i) {
            list.get(i).chapterIndex = i + 1;
        }

    }

    private ArrayList<ChapterData> splitChapter(ChapterData data) {
        ArrayList<ChapterData> list = new ArrayList<ChapterData>();
        ChapterData temp = null;
        int totalArea = 0;
        int currentArea = 0;
        for (int i = 0; i < data.timelines.size(); ++i)
            totalArea += data.timelines.get(i).areaCount;

        int SPLIT_AMOUNT = getSplitAmount(totalArea);

        while (data.timelines.size() > 0) {
            if (temp == null) {
                temp = new ChapterData();
                currentArea = 0;
            }

            currentArea += data.timelines.get(0).areaCount;
            temp.timelines.add(data.timelines.get(0));
            data.timelines.remove(0);

            if (currentArea > SPLIT_AMOUNT) {
                temp.refreshCount();
                list.add(temp);
                temp = null;
            }
        }
        if (temp != null && temp.timelines != null && temp.timelines.size() > 0) list.add(temp);

        return list;
    }

    private int getSplitAmount(int total) {
        if (total < BASE_AREA_COUNT_PER_CHAPTER) return total;

        int num[] = new int[5];
        int quo[] = new int[5];
        int bestCount = 0, bestAmount = 0;

        quo[0] = total / BASE_AREA_COUNT_PER_CHAPTER;
        quo[1] = quo[0] + 1;
        quo[2] = quo[0] - 1;
        quo[3] = quo[0] + 2;
        quo[4] = quo[0] - 2;

        for (int i = 0; i < quo.length; ++i) {
            if (quo[i] < 1) continue;

            num[i] = total / quo[i];
            if (bestAmount < 1 || Math.abs(BASE_AREA_COUNT_PER_CHAPTER - bestAmount) > Math.abs(BASE_AREA_COUNT_PER_CHAPTER - num[i])) {
                bestAmount = num[i];
                bestCount = quo[i];
            }
        }
        return bestAmount;
    }

    private ChapterData combineChapter(ChapterData chapter1, ChapterData chapter2) {
        ChapterData data = new ChapterData();
        data.timelines = new ArrayList<TimelineData>();
        data.timelines.addAll(chapter1.timelines);
        data.timelines.addAll(chapter2.timelines);
        data.areaCount = chapter1.areaCount + chapter2.areaCount;
        data.pageCounts = chapter1.pageCounts + chapter2.pageCounts;
        Collections.sort(data.timelines, new FacebookUtil.NumberAscCompare());
        return data;
    }


    // 현재 적용되 있는 페이지에 추가로 그려 페이지(들)을 리턴함.
    @SuppressWarnings("static-access")
    private ArrayList<SnapsPage> makePage(SnapsPage page, TimelineData data) {
        ArrayList<SnapsPage> pages = new ArrayList<SnapsPage>();

        if (page == null) {
            page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
            isChapterPage = false;
            currentY = baseInnerY; // 초기화.
            areaNumber = maker.templateType == BookMaker.TYPE_C ? 1 : 0;
        }

        setPageChapterMonthData(page, data);

        // 초기 라인, 날짜, 메세지, 사진, 좋아요 셋트가 들어갈수 있는지 체크.
        baseY = isChapterPage ? baseChapterY : baseInnerY;
        boolean fromMessage = data.type == TimelineData.TYPE_MESSAGE && data.feed != null && data.feed.fromSomeone;
        boolean drawable = false;
        SnapsTextControl write = null;
        SnapsTextControl description = null;
        MultiLineTextData writeData = null;
        MultiLineTextData descriptionData = null;
        SnapsLayoutControl image = null;
        ImageInfo imgInfo = null;
        Rect r = null;
        ArrayList<SnapsLayoutControl> pictures = new ArrayList<SnapsLayoutControl>();

        @SuppressWarnings("unchecked")
        ArrayList<AttachmentData> attList = (ArrayList<AttachmentData>) data.getAttachments();
        if (attList != null && attList.size() > 0)
            for (int i = 0; i < attList.size(); ++i) {
                imgInfo = getImageInfoFromAttachment(attList.get(i), data.isMemorySharePost());
                image = ((SnapsLayoutControl) postImage).copyControl();
                image.facebookImageID = imgInfo != null ? imgInfo.targetId : "";
                image.qrCodeUrl = imgInfo != null ? imgInfo.targetUrl : "";

                try {
                    int w = Integer.parseInt(imgInfo.getOriginWidth());
                    int h = Integer.parseInt(imgInfo.getOriginHeight());
                    int maxW = 137;
                    int maxH = screenSize[1] - baseY - bMargin;

                    if (imgInfo != null) {
                        setImageInfoToControl(image, imgInfo);

                        if ((float) maxW / (float) maxH > (float) w / (float) h) {
                            w = (int) (((float) maxH / h) * w);
                            h = maxH;
                        } else {
                            h = (int) (((float) maxW / w) * h);
                            w = maxW;
                        }
                        image.height = "" + h;
                        image.width = "" + w;
                    }
                    pictures.add(image);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        else if (data.getThumbnailUrl() != null && data.getThumbnailUrl().length() > 0) {
            pictures.add(((SnapsLayoutControl) postImage).copyControl());
            try {
                imgInfo = getImageInfo(data.getThumbnailUrl(), (data.getFullPictureUrl() != null && data.getFullPictureUrl().length() > 0) ? data.getFullPictureUrl() : data.getThumbnailUrl(), 640, 640);
                pictures.get(pictures.size() - 1).facebookImageID = imgInfo != null ? imgInfo.targetId : "";
                pictures.get(pictures.size() - 1).qrCodeUrl = imgInfo != null ? imgInfo.targetUrl : "";
                int w = Integer.parseInt(imgInfo.getOriginWidth());
                int h = Integer.parseInt(imgInfo.getOriginHeight());
                int maxW = 137;
                int maxH = screenSize[1] - baseY - bMargin;

                if (imgInfo != null) {
                    setImageInfoToControl(pictures.get(0), imgInfo);

                    if ((float) maxW / (float) maxH > (float) w / (float) h) {
                        w = (int) (((float) maxH / h) * w);
                        h = maxH;
                    } else {
                        h = (int) (((float) maxW / w) * h);
                        w = maxW;
                    }
                    pictures.get(pictures.size() - 1).height = "" + h;
                    pictures.get(pictures.size() - 1).width = "" + w;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        } else if (data.isMemorySharePost() && data.post.attachmentList != null && data.post.attachmentList.size() > 0 && !StringUtil.isEmpty(data.post.attachmentList.get(0).targetUrl)) {
            imgInfo = new ImageInfo();
            imgInfo.large = DEFAULT_QR_CODE_THUMBNAIL_IMG_URL;
            imgInfo.medium = DEFAULT_QR_CODE_THUMBNAIL_IMG_URL;
            imgInfo.small = DEFAULT_QR_CODE_THUMBNAIL_IMG_URL;
            imgInfo.original = DEFAULT_QR_CODE_THUMBNAIL_IMG_URL;
            imgInfo.setOriginWidth(32 + "");
            imgInfo.setOriginHeight(32 + "");
            imgInfo.targetUrl = data.post.attachmentList.get(0).targetUrl;

            SnapsLayoutControl temp = ((SnapsLayoutControl) postImage).copyControl();
            temp.qrCodeUrl = imgInfo != null ? imgInfo.targetUrl : "";
            setImageInfoToControl(temp, imgInfo);
            pictures.add(temp);
        }

        String contentStr = "";
        if (!StringUtil.isEmpty(data.content)) contentStr = data.content;
        else if (data.isMemorySharePost() && attList != null && attList.size() > 0 && !StringUtil.isEmpty(attList.get(0).description))
            contentStr = attList.get(0).description;

        if (!StringUtil.isEmpty(contentStr)) {
            write = ((SnapsTextControl) txWrite).copyControl();
            writeData = CalcViewRectUtil.getTextControlRect(context, write.format.fontFace, write.format.fontSize, write.getIntWidth(), contentStr, 1.0f, fromMessage ? FontUtil.TEXT_TYPE_CONTENTS2 : FontUtil.TEXT_TYPE_CONTENTS);
            write.lineSpcing = BookMaker.LINE_SPACINNG;
        }

        boolean isShared = data.isSharedPost() || data.isSharedPostWithoutWriter() || data.hasSharedAttachment();

        if (isShared) {
            boolean hasDescription = false;
            String descriptionText = "";
            if (data.post.attachmentList != null && data.post.attachmentList.size() > 0 && !StringUtil.isEmpty(data.post.attachmentList.get(0).description)) {
                hasDescription = true;
                descriptionText = data.post.attachmentList.get(0).description;
            } else if (!StringUtil.isEmpty(data.post.description)) {
                hasDescription = true;
                descriptionText = data.post.description;
            }

            if (hasDescription) {
                description = ((SnapsTextControl) txWrite).copyControl();
                description.format.fontSize = "" + (Integer.parseInt(description.format.fontSize) - 1); // 가이드가 없는것 같아서 기본 글의 사이즈보다 하나 줄여서 넣자.
                descriptionData = CalcViewRectUtil.getTextControlRect(context, description.format.fontFace, description.format.fontSize, description.getIntWidth() - 5, descriptionText, 1.0f, FontUtil.TEXT_TYPE_CONTENTS2);
                description.lineSpcing = BookMaker.LINE_SPACINNG;
            }
        }

        if (currentY < baseY + 1) ;
        else {
            int size = Integer.parseInt(stCamera.height);
            if (data.content != null && data.content.length() > 0) {
                size += 7;
                size += writeData.getTextTotalHeight(write);
            }


            // 공유한 글의 내용들은 줄바꿈을 체크하는 묶음에서 제외.
            if (!isShared) {
                if (pictures.size() > 0) {
                    size += 7;
                    size += pictures.get(0).getIntHeight();
                }

                boolean hasCount = false;
                if (pictures != null && pictures.size() > 0) {
                    AlbumData album = maker.getAlbumDataById(pictures.get(0).facebookImageID);
                    hasCount = album != null && album.likeList.size() + album.commentList.size() + album.sharedList.size() > 0;
                }

                if (hasCount || (pictures.size() < 2 && data.hasSummaryData())) size += 15;
            }

            if (!isPossibleToDrawCurrentArea(size) && size < screenSize[1] - bMargin - baseY) { // 위치 확인
                if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                    isChapterPage = false;
                    pages.add(page);
                    setPageChapterMonthData(page, data);
                    page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }
            }
        }

        SnapsClipartControl line = makeStickerControl((SnapsClipartControl) divideLine);
        if (!(isChapterPage && currentY == baseChapterY) && !(!isChapterPage && currentY == baseInnerY))
            currentY += 7;
        line.x = "" + baseX[areaNumber];
        line.y = "" + currentY;
        line.width = "" + 137;
        line.height = "" + 6;
        page.addControl(line);

        SnapsClipartControl icon = null;
        if (isShared /*|| (data.feed != null && data.feed.isTaggedWrite())*/)
            icon = makeStickerControl((SnapsClipartControl) stShare);
        else if (data.type == TimelineData.TYPE_MESSAGE || data.post.sharedFromSomeone)
            icon = makeStickerControl((SnapsClipartControl) stMessage);
        else {
            switch (data.type) {
                case TimelineData.TYPE_STATUS:
                    icon = makeStickerControl((SnapsClipartControl) stWrite);
                    break;
                case TimelineData.TYPE_PHOTO:
                    icon = makeStickerControl((SnapsClipartControl) stCamera);
                    break;
                case TimelineData.TYPE_VIDEO:
                    icon = makeStickerControl((SnapsClipartControl) stVideo);
                    break;
                case TimelineData.TYPE_LINK:
                    icon = makeStickerControl((SnapsClipartControl) stShare);
                    break;
            }
        }

        if (icon != null) {
            icon.x = "" + baseX[areaNumber];
            icon.y = "" + currentY;
            page.addControl(icon);

            currentY += icon.getIntHeight();
        }

        currentY += 1;// 1px 넗게.

        SnapsTextControl date = ((SnapsTextControl) txDate).copyControl();

        date.text = data.year + "." + (data.month < 10 ? "0" + data.month : data.month) + "." + (data.day < 10 ? "0" + data.day : data.day);
        r = CalcViewRectUtil.getTextControlRect2(context, date.text, date.format.fontSize, date.getIntWidth(), date.format.fontFace, 1f);
        date.text = data.year + "." + (data.month < 10 ? "0" + data.month : data.month) + "." + (data.day < 10 ? "0" + data.day : data.day);
        date.x = (Integer.parseInt(icon.x) + Integer.parseInt(icon.width) + 3) + "";
        date.y = "" + (Integer.parseInt(line.y) + 5);
        date.height = "8";
        date.format.auraOrderFontSize = "12";
        date.format.bold = "bold";
        page.addControl(date);

        SnapsTextControl day = ((SnapsTextControl) txTime).copyControl();
        day.format.auraOrderFontSize = "7";
        day.text = String.format("%s %s %02d:%02d", data.dayOfWeek, data.hourStr, data.hour, data.min);
        String applicationName = data.getSharedApplicationName();
        r = CalcViewRectUtil.getTextControlRect2(context, day.text, day.format.fontSize, day.getIntWidth(), day.format.fontFace, 1f);
        day.x = (Integer.parseInt(date.x) + Integer.parseInt(date.width) - 1) + "";
        day.y = "" + (Integer.parseInt(date.y) + 1);
        //위치 보정.
        day.setOffsetX(-8 - 2);
        day.setOffsetY(2);
        day.height = "7";
        page.addControl(day);


        // tag.
        SnapsTextControl story = null;
        if (data.getStory() != null && data.getStory().length() > 0 && isWriteStoryTag(data.getStory())) {
            currentY += 2; //5;//-2 //7;
            story = ((SnapsTextControl) txWrite).copyControl();
            story.text = data.getStory();
            story.format.fontSize = "5";
            story.format.auraOrderFontSize = "5";
            story.x = "" + contentsX[areaNumber]; // 위치 보정.
            story.y = "" + currentY;
            page.addControl(story);
            currentY += 6;//4;//2;

            // 스티커 추가.
            SnapsClipartControl clip = SnapsClipartControl.setFacebookStoryLineSticker();
            clip.x = "" + (contentsX[areaNumber] - 2);
            clip.y = "" + currentY;
            page.addControl(clip);

            currentY += 3;
        }

        if (fromMessage) {
            currentY += 5;
            SnapsLayoutControl messageProfile = ((SnapsLayoutControl) userImage).copyControl();
            SnapsTextControl name, timeText;

            imgInfo = null;
            try {
                imgInfo = getImageInfo(data.feed.fromId);
                if (imgInfo == null) {
                    imgInfo = new ImageInfo();
                    imgInfo.large = DEFAULT_PROFILE_IMG_URL;
                    imgInfo.medium = DEFAULT_PROFILE_IMG_URL;
                    imgInfo.small = DEFAULT_PROFILE_IMG_URL;
                    imgInfo.original = DEFAULT_PROFILE_IMG_URL;

                    imgInfo.setOriginWidth("400");
                    imgInfo.setOriginHeight("400");
                }
                setImageInfoToControl(messageProfile, imgInfo);
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            if (!isPossibleToDrawCurrentArea(15)) {
                if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                    isChapterPage = false;
                    pages.add(page);
                    setPageChapterMonthData(page, data);
                    page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }
            }
            startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);
            currentY += 5;

            messageProfile.x = "" + (contentsX[areaNumber] + 5);
            messageProfile.y = "" + currentY;

            name = ((SnapsTextControl) txCommentName).copyControl();
            name.text = data.feed.fromName + " ► " + maker.name;
            name.x = "" + (contentsX[areaNumber] + 10 + messageProfile.getIntWidth());
            name.y = "" + currentY;
            name.format.fontSize = "5";
            name.format.auraOrderFontSize = "6";
            name.format.bold = "bold";


            timeText = ((SnapsTextControl) txWrite).copyControl();
            timeText.text = FacebookUtil.getDateDiffString(data.createTime);
            timeText.format.fontSize = "5";
            timeText.format.auraOrderFontSize = "5";
            timeText.x = "" + (contentsX[areaNumber] + 10 + messageProfile.getIntWidth());
            timeText.y = "" + (currentY + 5);

            page.addLayout(messageProfile);
            page.addControl(name);
            page.addControl(timeText);

            currentY += 12;
        }

        if (writeData != null) {
            currentY += 3;
            if (!isPossibleToDrawCurrentArea(writeData.getTextHeight())) { // 위치 확인
                if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                    isChapterPage = false;
                    pages.add(page);
                    setPageChapterMonthData(page, data);
                    page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }
            }

            if (fromMessage)
                startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);

            if (isPossibleToDrawCurrentArea(writeData.getTextTotalHeight(write))) { // 위치 확인
                String sText = "";
                for (int i = 0; i < writeData.getLineTexts().size(); i++) {
                    if (!sText.equals(""))
                        sText += "\n";
                    sText += writeData.getLineTexts().get(i);
                }
                write = ((SnapsTextControl) txWrite).copyControl();
                write.lineSpcing = BookMaker.LINE_SPACINNG;
                write.text = sText;
                write.x = "" + (contentsX[areaNumber] - 2);
                if (fromMessage) write.x = "" + (contentsX[areaNumber] + 5);
                write.y = "" + currentY;
                write.format.auraOrderFontSize = "8";
                write.height = "" + writeData.getTextTotalHeight(write);

                CalcViewRectUtil.makeLineText(write, writeData.getLineTexts(), writeData.getTextHeight());
                page.addControl(write);
                currentY += writeData.getTextTotalHeight(write);
            } else {
                int startLine = 0;
                while (true) {
                    if (startLine >= writeData.getLineTexts().size())
                        break;

                    if (!isPossibleToDrawCurrentArea(writeData.getTextHeight())) {
                        if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                            isChapterPage = false;
                            pages.add(page);
                            setPageChapterMonthData(page, data);
                            page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                        }
                    }

                    write = ((SnapsTextControl) txWrite).copyControl();
                    write.lineSpcing = BookMaker.LINE_SPACINNG;
                    startLine = writeData.getExtractTextByHeight(startLine, getAvailableHeight(), write);
                    write.x = "" + contentsX[areaNumber];
                    write.y = "" + currentY;
                    write.format.auraOrderFontSize = "8";

                    page.addControl(write);
                    currentY += write.getIntHeight();
                }
            }

            if (endPostCommentBackgroud(currentY))
                currentY += 5;
        } else {
            endPostCommentBackgroud(currentY);
        }

        if (isShared) {
            currentY += 3;

            FriendData originWriter = data.getSharedPostOriginWriter();
            if (originWriter != null && data.isSharedFromUser()) {
                SnapsLayoutControl messageProfile = ((SnapsLayoutControl) userImage).copyControl();
                SnapsTextControl name, timeText;

                imgInfo = null;
                try {
                    imgInfo = getImageInfo(originWriter.id);
                    if (imgInfo == null) {
                        imgInfo = new ImageInfo();
                        imgInfo.large = DEFAULT_PROFILE_IMG_URL;
                        imgInfo.medium = DEFAULT_PROFILE_IMG_URL;
                        imgInfo.small = DEFAULT_PROFILE_IMG_URL;
                        imgInfo.original = DEFAULT_PROFILE_IMG_URL;

                        imgInfo.setOriginWidth("400");
                        imgInfo.setOriginHeight("400");
                    }
                    setImageInfoToControl(messageProfile, imgInfo);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                if (!isPossibleToDrawCurrentArea(15)) {
                    if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                        isChapterPage = false;
                        pages.add(page);
                        setPageChapterMonthData(page, data);
                        page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                    }
                }

                startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);
                currentY += 5;

                messageProfile.x = "" + (contentsX[areaNumber]);
                messageProfile.y = "" + currentY;

                name = ((SnapsTextControl) txCommentName).copyControl();
                name.text = originWriter.name;
                name.x = "" + (contentsX[areaNumber] + 10 + messageProfile.getIntWidth());
                name.y = "" + currentY;
                name.format.fontSize = "5";
                name.format.auraOrderFontSize = "6";
                name.format.bold = "bold";

                Calendar originWriteCreatedTime = data.getSharedPostCreatedTime();
                if (originWriteCreatedTime != null) {
                    timeText = ((SnapsTextControl) txWrite).copyControl();
                    timeText.text = FBCommonUtil.getSharedOriginWriteDate(originWriteCreatedTime);
                    timeText.format.fontSize = "5";
                    timeText.format.auraOrderFontSize = "5";
                    timeText.x = "" + (contentsX[areaNumber] + 10 + messageProfile.getIntWidth());
                    timeText.y = "" + (currentY + 5);
                    page.addControl(timeText);
                }

                page.addLayout(messageProfile);
                page.addControl(name);

                currentY += 5;
            }


            if (descriptionData != null) {
                currentY += 5;

                if (!isPossibleToDrawCurrentArea(descriptionData.getTextHeight() + 3)) { // 위치 확인
                    endPostCommentBackgroud(currentY - 3); // 안들어가면 border 마무리.

                    if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                        isChapterPage = false;
                        pages.add(page);
                        setPageChapterMonthData(page, data);
                        page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                    }

                    startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);
                } else
                    startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);

                currentY += 5;

                if (isPossibleToDrawCurrentArea(descriptionData.getTextTotalHeight(description))) { // 위치 확인
                    String sText = "";
                    for (int i = 0; i < descriptionData.getLineTexts().size(); i++) {
                        if (!sText.equals(""))
                            sText += "\n";
                        sText += descriptionData.getLineTexts().get(i);
                    }

                    description = ((SnapsTextControl) txWrite).copyControl();
                    description.lineSpcing = BookMaker.LINE_SPACINNG;
                    description.text = sText;
                    description.x = "" + (contentsX[areaNumber] + 5);
                    description.y = "" + currentY;
                    description.format.fontSize = "7";
                    description.format.auraOrderFontSize = "7";
                    description.height = "" + descriptionData.getTextTotalHeight(description);

                    CalcViewRectUtil.makeLineText(description, descriptionData.getLineTexts(), descriptionData.getTextHeight());
                    page.addControl(description);
                    currentY += descriptionData.getTextTotalHeight(description);
                } else {
                    int startLine = 0;
                    while (true) {
                        if (startLine >= descriptionData.getLineTexts().size())
                            break;

                        if (!isPossibleToDrawCurrentArea(descriptionData.getTextHeight())) {
                            endPostCommentBackgroud(currentY - 3); // 안들어가면 border 마무리.

                            if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                                isChapterPage = false;
                                pages.add(page);
                                setPageChapterMonthData(page, data);
                                page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                            }

                            startPostCommentBackgroud(sharedX[areaNumber] + "", currentY + "", page, false, -4);
                        }

                        description = ((SnapsTextControl) txWrite).copyControl();
                        description.lineSpcing = BookMaker.LINE_SPACINNG;
                        startLine = descriptionData.getExtractTextByHeight(startLine, getAvailableHeight(), description);
                        description.x = "" + (contentsX[areaNumber] + 5);
                        description.y = "" + currentY;
                        description.format.fontSize = "7";
                        description.format.auraOrderFontSize = "7";

                        page.addControl(description);
                        currentY += description.getIntHeight();
                    }
                }
            }
        }

        int photoCount = 0;
        if (maker.showPhotoType == BookMaker.SHOW_PHOTO_UNLIMIT) photoCount = pictures.size();
        else if (maker.showPhotoType == BookMaker.SHOW_PHOTO_LIMIT_1)
            photoCount = pictures.size() > 1 ? 1 : pictures.size();
        else if (maker.showPhotoType == BookMaker.SHOW_PHOTO_LIMIT_5)
            photoCount = pictures.size() > 5 ? 5 : pictures.size();
        boolean hasCount;
        AlbumData album;
        boolean isDrawRect = false;// 좋아요 이후에 바로 좋아요가 나오는 경우 라인을 추가한다.
        int tempY = 0;

        // 사진 추가 밑 그에 딸린 좋아요, 댓글 등도 추가. 여기서부턴 위치 체크하여 추가.
        for (int i = 0; i < photoCount; ++i) {
            if (i == 0) currentY += 7;

            album = maker.getAlbumDataById(pictures.get(i).facebookImageID);
            hasCount = album != null && album.likeList.size() + album.commentList.size() + album.sharedList.size() > 0 && photoCount > 1; // 사진이 1개일때 사진의 좋아요와 포스트의 좋아요가 중복되서 예외처리. 사진이 1개일땐 사진 == 포스트라고 생각해도 될듯 하며 좋아요와 댓글 처리를 포스트 댓글과 좋아요에서 표시.

            if (!isPossibleToDrawCurrentArea(pictures.get(i).getIntHeight() + (hasCount ? 15 : 0))) { // 위치 확인
                if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                    isChapterPage = false;
                    pages.add(page);
                    setPageChapterMonthData(page, data);
                    page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }
            }

            pictures.get(i).x = "" + (isShared ? sharedX[areaNumber] : baseX[areaNumber]);
            pictures.get(i).y = "" + currentY;
            if (isShared)
                pictures.get(i).width = "" + (Integer.parseInt(pictures.get(i).width) - 4);

            isDrawRect = false;
            tempY = currentY + 2;
            page.addLayout(pictures.get(i));

            currentY += pictures.get(i).getIntHeight();

            if (i < photoCount - 1 && !hasCount) currentY++;

            // 포스트 내의 사진의 좋아요 등.
            if (hasCount) {
                isDrawRect = true;
                ArrayList<SnapsClipartControl> ctList = new ArrayList<SnapsClipartControl>();
                ArrayList<SnapsTextControl> txList = new ArrayList<SnapsTextControl>();

                if (album.likeList.size() > 0) {
                    ctList.add(makeStickerControl((SnapsClipartControl) st2Like));
                    txList.add(((SnapsTextControl) txLikeCount).copyControl()); //
                    txList.get(txList.size() - 1).text = "" + album.likeList.size();
                }
                if (album.commentList.size() > 0) {
                    ctList.add(makeStickerControl((SnapsClipartControl) st2Comment));
                    txList.add(((SnapsTextControl) txLikeCount).copyControl());
                    txList.get(txList.size() - 1).text = "" + album.commentList.size();
                }
                if (album.sharedList.size() > 0) {
                    ctList.add(makeStickerControl((SnapsClipartControl) st2Share));
                    txList.add(((SnapsTextControl) txLikeCount).copyControl());
                    txList.get(txList.size() - 1).text = "" + album.sharedList.size();
                }

                for (int j = 0; j < ctList.size(); ++j) {
                    ctList.get(j).x = j == 0 ? "" + contentsX[areaNumber] : j == 1 ? "" + (contentsX[areaNumber] + 20) : "" + (contentsX[areaNumber] + 40);
                    ctList.get(j).y = "" + (currentY + 4);
                    txList.get(j).x = j == 0 ? "" + (contentsX[areaNumber] + 11) : j == 1 ? "" + (contentsX[areaNumber] + 31) : "" + (contentsX[areaNumber] + 51);
                    txList.get(j).y = "" + (currentY + 6);
                    txList.get(j).height = "" + (Integer.parseInt(txList.get(j).height) + 2);
                    txList.get(j).format.auraOrderFontSize = "8";

                    page.addControl(ctList.get(j));
                    page.addControl(txList.get(j));
                }

                currentY += 15;
            }

            // 사진 댓글과 포스트 댓글이 중복되는 경우가 있어 필터링.
            if (album != null && album.commentList != null && album.commentList.size() > 0 && data.getCommentList() != null && data.getCommentList().size() > 0) {
                for (int j = 0; j < album.commentList.size(); ++j) {
                    if (isSameComment(album.commentList.get(j), data.getCommentList())) {
                        album.commentList.remove(j);
                        j--;
                    }
                }
            }


            if (album != null && album.commentList != null && album.commentList.size() > 0) {
                isDrawRect = false;
                ArrayList<SnapsPage> resultPages = drawComment(page, data, album.commentList);
                if (resultPages != null && resultPages.size() > 0) {
                    page = resultPages.get(resultPages.size() - 1);
                    resultPages.remove(resultPages.size() - 1);
                    if (resultPages.size() > 0) pages.addAll(resultPages);
                }

                currentY += 5;
            }

            if (isShared) {
                endPostCommentBackgroud(currentY);
                currentY += 5;
            }
        }

        // 전체 포스트의 좋아요 등.
        if (data.hasSummaryData()) {
            if (!isPossibleToDrawCurrentArea(15)) { // 위치 확인
                if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                    isChapterPage = false;
                    pages.add(page);
                    setPageChapterMonthData(page, data);
                    page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                }

                isDrawRect = false;
            }

            //구분 rect를 넣는다.
            if (isDrawRect) {
                startPostCommentBackgroud(baseX[areaNumber] + "", tempY + "", page, false, true);
                endPostCommentBackgroud(currentY - 5);
            }

            ArrayList<SnapsClipartControl> ctList = new ArrayList<SnapsClipartControl>();
            ArrayList<SnapsTextControl> txList = new ArrayList<SnapsTextControl>();

            if (data.getLikeList().size() > 0) {
                ctList.add(makeStickerControl((SnapsClipartControl) st2Like));
                txList.add(((SnapsTextControl) txLikeCount).copyControl()); //
                txList.get(txList.size() - 1).text = "" + data.getLikeList().size();
            }
            if (data.getCommentList().size() > 0) {
                ctList.add(makeStickerControl((SnapsClipartControl) st2Comment));
                txList.add(((SnapsTextControl) txLikeCount).copyControl());
                txList.get(txList.size() - 1).text = "" + data.getCommentList().size();
            }
            if (data.getShareCount() > 0) {
                ctList.add(makeStickerControl((SnapsClipartControl) st2Share));
                txList.add(((SnapsTextControl) txLikeCount).copyControl());
                txList.get(txList.size() - 1).text = "" + data.getShareCount();
            }

            for (int j = 0; j < ctList.size(); ++j) {
                ctList.get(j).x = j == 0 ? "" + contentsX[areaNumber] : j == 1 ? "" + (contentsX[areaNumber] + 20) : "" + (contentsX[areaNumber] + 40);
                ctList.get(j).y = "" + (currentY + 4);
                txList.get(j).x = j == 0 ? "" + (contentsX[areaNumber] + 11) : j == 1 ? "" + (contentsX[areaNumber] + 31) : "" + (contentsX[areaNumber] + 51);
                txList.get(j).y = "" + (currentY + 6);
                txList.get(j).height = "" + (Integer.parseInt(txList.get(j).height) + 2);
                txList.get(j).format.auraOrderFontSize = "8";
                page.addControl(ctList.get(j));
                page.addControl(txList.get(j));
            }
            currentY += 15;
        }

        // 포스트의 댓글은 배경을 넣어줘야 한다.
        ArrayList<CommentData> commentList = data.type == TimelineData.TYPE_MESSAGE ? data.feed.commentList : data.post.commentList;
        if (commentList != null && commentList.size() > 0) {
            ArrayList<SnapsPage> resultPages = drawComment(page, data, commentList, true);
            if (resultPages != null && resultPages.size() > 0) {
                page = resultPages.get(resultPages.size() - 1);
                resultPages.remove(resultPages.size() - 1);
                if (resultPages.size() > 0) pages.addAll(resultPages);
            }
        }

        pages.add(page);
        setPageChapterMonthData(page, data);

        return pages;
    }

    private ArrayList<SnapsPage> drawComment(SnapsPage page, TimelineData data, ArrayList<CommentData> list) {
        return drawComment(page, data, list, false);
    }

    private ArrayList<SnapsPage> drawComment(SnapsPage page, TimelineData data, ArrayList<CommentData> list, boolean isPostComment) {
        ArrayList<SnapsPage> pages = new ArrayList<SnapsPage>();

        // 포스트 내의 댓글.
        SnapsLayoutControl commentProfile;
        SnapsTextControl commentName, commentContent;
        ImageInfo imgInfo;
        MultiLineTextData writeData;
        currentY += commentAdditionalMargin; // 배경그리기 위해 댓글 전후에 띄어준다. 간격은 임의로 넣었으므로 조정.
        if (list != null && list.size() > 0) {
            CommentData comment, reply;
            for (int i = 0; i < Math.min(list.size(), maker.commentLimit); ++i) {
                comment = list.get(i);
                commentProfile = ((SnapsLayoutControl) userImage).copyControl();
                imgInfo = null;
                try {
                    imgInfo = getImageInfo(comment.fromId);
                    if (imgInfo != null) setImageInfoToControl(commentProfile, imgInfo);
                } catch (Exception e) {
                    continue;
                }

                commentName = ((SnapsTextControl) txCommentName).copyControl();
                commentName.text = comment.fromName;
                commentContent = ((SnapsTextControl) txComment).copyControl();
                commentContent.text = comment.message;

                writeData = CalcViewRectUtil.getTextControlRect(context, commentContent.format.fontFace, commentContent.format.fontSize, 130, commentContent.text, 1.0f, FontUtil.TEXT_TYPE_COMMENT, 4);

                currentY += 5;
                if (!isPossibleToDrawCurrentArea(writeData.getTextHeight() + COMMENT_BG_MARGIN) || !isPossibleToDrawCurrentArea(commentProfile.getIntHeight() + COMMENT_BG_MARGIN)) { // 위치 확인
                    if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                        isChapterPage = false;
                        pages.add(page);
                        setPageChapterMonthData(page, data);
                        page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                    }

                    currentY += COMMENT_BG_MARGIN;
                }


                startPostCommentBackgroud(baseX[areaNumber] + "", currentY - COMMENT_BG_MARGIN + "", page, isPostComment);


                commentProfile.x = "" + contentsX[areaNumber];
                commentProfile.y = "" + currentY;
                commentName.x = "" + (contentsX[areaNumber] + 5 + (Integer.parseInt(commentProfile.width)));
                commentName.y = "" + currentY;
                commentName.format.bold = "true";
                commentName.format.auraOrderFontSize = "6";
                commentName.format.bold = "bold";

                page.addLayout(commentProfile);
                page.addControl(commentName);
                currentY += 5;

                if (isPossibleToDrawCurrentArea(writeData.getTextTotalHeight(commentContent) + COMMENT_BG_MARGIN)) { // 위치 확인
                    String sText = "";
                    for (int k = 0; k < writeData.getLineTexts().size(); k++) {
                        if (!sText.equals(""))
                            sText += "\n";
                        sText += writeData.getLineTexts().get(k);
                    }

                    commentContent.x = "" + (contentsX[areaNumber] + 5 + (Integer.parseInt(commentProfile.width)));
                    commentContent.y = "" + currentY;
                    commentContent.height = "" + writeData.getTextTotalHeight(commentContent);
                    commentContent.format.auraOrderFontSize = "6";

                    CalcViewRectUtil.makeLineText(commentContent, writeData.getLineTexts(), writeData.getTextHeight());
                    page.addControl(commentContent);

                    if (writeData.getTextTotalHeight(commentContent) < commentProfile.getIntHeight() - 7)
                        currentY += commentProfile.getIntHeight() - 7;
                    else currentY += writeData.getTextTotalHeight(commentContent);

                } else {
                    int startLine = 0;
                    int beforeLine = 0;
                    int height;
                    while (true) {
                        StringBuilder sb = new StringBuilder();
                        for (int l = 0; l < writeData.getLineTexts().size(); ++l)
                            sb.append(writeData.getLineTexts().get(l));
                        if (startLine >= writeData.getLineTexts().size())
                            break;

                        if (!isPossibleToDrawCurrentArea(writeData.getTextHeight() + COMMENT_BG_MARGIN)) {
                            if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                                isChapterPage = false;
                                pages.add(page);
                                setPageChapterMonthData(page, data);
                                page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                            }

                            currentY += COMMENT_BG_MARGIN;
                        }
                        startPostCommentBackgroud(baseX[areaNumber] + "", currentY - COMMENT_BG_MARGIN + "", page, isPostComment);

                        commentContent = ((SnapsTextControl) txComment).copyControl();
                        commentContent.lineSpcing = BookMaker.LINE_SPACINNG;
                        beforeLine = startLine;
                        startLine = writeData.getExtractTextByHeight(startLine, getAvailableHeight(), commentContent);
                        height = writeData.getTextLineHeight(commentContent, startLine - beforeLine);
                        commentContent.x = "" + (contentsX[areaNumber] + 5 + (Integer.parseInt(commentProfile.width)));
                        commentContent.y = "" + currentY;
                        commentContent.format.auraOrderFontSize = "6";

                        page.addControl(commentContent);

                        currentY += commentContent.getIntHeight();
                    }
                }

                // 댓글의 댓글.
                if (comment.subCommentList != null && comment.subCommentList.size() > 0) {
                    int spaceW = CalcViewRectUtil.getTextControlRect3(context, " ", commentName.format.fontSize, 1000, commentName.format.fontFace, 1f).width();
                    int nameW = 0;
                    for (int k = 0; k < Math.min(comment.subCommentList.size(), maker.replyLimit); ++k) {
                        if (k == 0) {
                            SnapsClipartControl commentSticker = SnapsClipartControl.setFacebookCommentSticker();
                            commentSticker.x = (replyX[areaNumber] - commentSticker.getIntWidth() - 2) + "";
                            commentSticker.y = "" + currentY;
                            page.addControl(commentSticker);
                        }

                        reply = comment.subCommentList.get(k);

                        commentName = ((SnapsTextControl) txCommentName).copyControl();
                        commentName.text = reply.fromName;
                        commentContent = ((SnapsTextControl) txComment).copyControl();

                        nameW = CalcViewRectUtil.getTextControlRect3(context, commentName.text, commentName.format.fontSize, 1000, commentName.format.fontFace, 1.3f).width();
                        StringBuilder sb = new StringBuilder();
                        int count = nameW / spaceW + 2;
                        if (nameW % spaceW > spaceW / 3) count++;
                        for (int l = 0; l < count; ++l) sb.append(" ");
                        commentContent.text = sb.toString() + reply.message;
                        writeData = CalcViewRectUtil.getTextControlRect(context, commentContent.format.fontFace, commentContent.format.fontSize, 120, commentContent.text, 1.0f, FontUtil.TEXT_TYPE_COMMENT2, 4);
                        if (!isPossibleToDrawCurrentArea(writeData.getTextHeight() + COMMENT_BG_MARGIN)) { // 위치 확인
                            if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                                isChapterPage = false;
                                pages.add(page);
                                setPageChapterMonthData(page, data);
                                page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                            }
                            currentY += COMMENT_BG_MARGIN;
                        }

                        startPostCommentBackgroud(baseX[areaNumber] + "", currentY - COMMENT_BG_MARGIN + "", page, isPostComment);

                        commentName.x = "" + replyX[areaNumber];
                        commentName.y = "" + currentY;

                        commentName.format.auraOrderFontSize = "6";
                        commentName.format.bold = "bold";
                        page.addControl(commentName);

                        if (isPossibleToDrawCurrentArea(writeData.getTextTotalHeight(commentContent) + COMMENT_BG_MARGIN)) { // 위치 확인
                            String sText = "";
                            for (int j = 0; j < writeData.getLineTexts().size(); j++) {
                                if (!sText.equals(""))
                                    sText += "\n";
                                sText += writeData.getLineTexts().get(j);
                            }

                            commentContent.x = "" + replyX[areaNumber];
                            commentContent.y = "" + currentY;
                            commentContent.height = "" + writeData.getTextTotalHeight(commentContent);
                            commentContent.width = "" + (Integer.parseInt(commentContent.width) - Integer.parseInt(commentName.x) + Integer.parseInt(commentContent.x));
                            commentContent.format.auraOrderFontSize = "6";

                            CalcViewRectUtil.makeLineText(commentContent, writeData.getLineTexts(), writeData.getTextHeight());
                            page.addControl(commentContent);

                            currentY += writeData.getTextTotalHeight(commentContent);
                        } else {
                            int startLine = 0;

                            sb = new StringBuilder();
                            for (int l = 0; l < writeData.getLineTexts().size(); ++l)
                                sb.append(writeData.getLineTexts().get(l));

                            while (true) {
                                if (startLine >= writeData.getLineTexts().size())
                                    break;

                                if (!isPossibleToDrawCurrentArea(writeData.getTextHeight() + COMMENT_BG_MARGIN)) {
                                    if (!moveArea()) { // 안들어가면 위치 이동 후 페이지 새로 만들어야 하면 생성.
                                        isChapterPage = false;
                                        pages.add(page);
                                        setPageChapterMonthData(page, data);
                                        page = pageInner.copyPage(FacebookPhotobookPageType.PAGE_INNER.getIndex());
                                    }
                                    currentY += COMMENT_BG_MARGIN;
                                }
                                startPostCommentBackgroud(baseX[areaNumber] + "", currentY - COMMENT_BG_MARGIN + "", page, isPostComment);
                                commentContent = ((SnapsTextControl) txComment).copyControl();
                                startLine = writeData.getExtractTextByHeight(startLine, getAvailableHeight(), commentContent);
                                commentContent.x = "" + replyX[areaNumber];
                                commentContent.y = "" + currentY;
                                commentContent.format.auraOrderFontSize = "6";

                                page.addControl(commentContent);
                                currentY += commentContent.getIntHeight();
                            }
                        }
                    }
                }
            }
        }

        currentY += commentAdditionalMargin;
        endPostCommentBackgroud(currentY);
        currentY += 4;//commentAdditionalMargin;


        pages.add(page);
        return pages;
    }

    private boolean moveArea() {

        endPostCommentBackgroud(currentY);

        areaCount++;
        areaNumber++;
        currentY = baseY;

        if (areaNumber > 1) {
            isChapterPage = false;
            currentY = baseInnerY;
            baseY = baseInnerY;
        }

        if (areaNumber > 3) {
            areaNumber = 0;
            return false;
        }
        return true;
    }

    private int getAvailableHeight() {
        return screenSize[1] - currentY - bMargin;
    }

    private boolean isPossibleToDrawCurrentArea(float height) {
        return screenSize[1] - currentY - bMargin > height;
    }

    protected void drawCover(SnapsTemplate template) {
        SnapsPage page = getPage(template, FacebookPhotobookPageType.COVER);
        setDataControls(page);
        //커버 이미지는 편집이 가능하다.
        setCoverEditable(page);
        // 책등에 타이틀 넣기. 소프트 커버인 경우 다르게 처리를 해야 한다.
        template.setSNSBookStick(setCoverSpineText(page));

        page.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

    }

    protected void drawIndex(SnapsTemplate template) {
        SnapsPage indexPage = getPage(template, FacebookPhotobookPageType.INDEX);
        setDataControls(indexPage);
        FacebookPhotobookIndexMaker indexMaker = new FacebookPhotobookIndexMaker(indexPage, maker);
        indexMaker.makeIndex();

        indexPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

    }

    protected void drawPicture(SnapsTemplate template) {
        SnapsPage page = getPage(template, FacebookPhotobookPageType.PICTURE);
        setBestImages(page);
        setProfilePage(page);

        if (page.info.F_TMPL_NAME.contains("모던_")) {
            //이메일 생일 위치 조정.
            SnapsControl birth = page.getControlByProperty("birth");
            SnapsControl email = page.getControlByProperty("e-mail");
            birth.y = (birth.getIntY() + 5) + "";
            email.y = (email.getIntY() + 5) + "";
        }

        page.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

    }

    protected void drawSummary(SnapsTemplate template) {
        SnapsPage page = getPage(template, FacebookPhotobookPageType.SUMMARY);
        setData(page, FacebookPhotobookPageType.SUMMARY);

        page.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

    }

    protected void drawFriends(SnapsTemplate template) {
        ArrayList<FriendData> friendList = (ArrayList<FriendData>) maker.getFrieldList().clone(), tempFriends = null;
        SnapsPage friendPage = null, tempPage = null;

        for (SnapsPage page : template.getPages()) {
            if (page.getSnsproperty().equals("friend")) {
                friendPage = page.copyPage(page.getPageID());
                template.getPages().remove(page);
                break;
            }
        }

        if (friendPage == null) return;

        while (friendList.size() > 0) {
            if (tempPage == null) tempPage = friendPage.copyPage(friendPage.getPageID());
            if (tempFriends == null) tempFriends = new ArrayList<FriendData>();

            for (int i = 0; i < 128; ++i) {
                if (friendList.size() < 1) break;
                tempFriends.add(friendList.get(0));
                friendList.remove(0);
            }

            setFriendListPage(tempPage, tempFriends);
            tempPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
            template.getPages().add(tempPage);

            tempFriends = null;
            tempPage = null;
        }
    }

    void setFriendListPage(SnapsPage friendPage, ArrayList<FriendData> friendList) {
        ArrayList<SnapsControl> layoutList = friendPage.getLayoutList();
        if (layoutList == null || layoutList.isEmpty()) return;

        int columns = 1, rows = 1;
        int x = 0, y = 0;

        boolean isOverOnePage = false;

        SnapsLayoutControl imgControl = null;
        SnapsClipartControl clipControl = null;
        SnapsTextControl txtControl = null;

        imgControl = (SnapsLayoutControl) friendPage.getLayoutList().get(0);
        clipControl = (SnapsClipartControl) friendPage.getClipartControlList().get(0);
        txtControl = (SnapsTextControl) friendPage.getTextControlList().get(0);

        friendPage.getLayoutList().remove(imgControl);
        friendPage.deleteControl(clipControl);
        friendPage.deleteControl(txtControl);

        x = imgControl.getIntX();
        y = imgControl.getIntY();

        final int CLIP_OFFSET_Y = clipControl.getIntY() - imgControl.getIntY();
        final int TXT_OFFSET_Y = txtControl.getIntY() - clipControl.getIntY();

        int addMargin = !(maker.templateType == BookMaker.TYPE_A) ? 2 : 0;


        for (FriendData friend : friendList) {
            SnapsLayoutControl copyImgControl = imgControl.copyImageControl();
            SnapsClipartControl copyClipControl = clipControl.copyClipartControl();
            SnapsTextControl copyTxtControl = txtControl.copyControl();

            MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
            imgData.KIND = Const_VALUES.SELECT_FACEBOOK;
            imgData.PATH = friend.originUrl;
            imgData.THUMBNAIL_PATH = friend.thumbUrl;
            imgData.F_IMG_WIDTH = "" + friend.originSize[0];
            imgData.F_IMG_HEIGHT = "" + friend.originSize[1];
            copyImgControl.imgData = imgData;
            copyImgControl.angle = "0";
            copyImgControl.imagePath = imgData.PATH;
            copyImgControl.imageLoadType = imgData.KIND;
            copyImgControl.isImageFull = false;

            if (friend.name != null && friend.name.length() > 0) {
                copyTxtControl.text = friend.name;

                byte fontType = FontUtil.TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A;
                switch (maker.templateType) {
                    case BookMaker.TYPE_B:
                        fontType = FontUtil.TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B;
                        break;
                    case BookMaker.TYPE_C:
                        fontType = FontUtil.TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C;
                        break;
                    case BookMaker.TYPE_D:
                        fontType = FontUtil.TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D;
                        break;
                    default:
                        fontType = FontUtil.TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A;
                        break;
                }
                MultiLineTextData writeData = CalcViewRectUtil.getTextControlRect(context, copyTxtControl.format.fontFace, copyTxtControl.format.fontSize, copyTxtControl.getIntWidth(), copyTxtControl.text,
                        1.0f, fontType, 4);
                if (writeData.getLineTexts().size() > 1) {
                    copyTxtControl.text = writeData.getLineTexts().get(0);
                    if (copyTxtControl.text.length() > 3) copyTxtControl.text += "...";
                }
            }

            if (isOverOnePage) {//좌우페이지의 왼쪽 마진이 달라서 +8
                copyImgControl.x = String.valueOf(x + friendPage.getWidth() / 2 + 8);
                copyClipControl.x = String.valueOf(x + friendPage.getWidth() / 2 + 8);
                copyTxtControl.x = String.valueOf(x + friendPage.getWidth() / 2 + 8 + addMargin);
            } else {
                copyImgControl.x = String.valueOf(x);
                copyClipControl.x = String.valueOf(x);
                copyTxtControl.x = String.valueOf(x + addMargin);
            }

            if (maker.templateType == BookMaker.TYPE_C || maker.templateType == BookMaker.TYPE_D)
                copyTxtControl.width = "" + (Integer.parseInt(copyTxtControl.width) - 2);

            copyImgControl.y = String.valueOf(y);
            copyClipControl.y = String.valueOf(y + CLIP_OFFSET_Y);
            copyTxtControl.y = String.valueOf(copyClipControl.getIntY() + TXT_OFFSET_Y);

            friendPage.getLayoutList().add(copyImgControl);
            friendPage.addControl(copyClipControl);
            friendPage.addControl(copyTxtControl);

            x += (imgControl.getIntWidth() + MARGIN_VALUE_FRIEND_GRID);
            if (++columns > COLUMN_COUNT_FRIEND_GRID) {
                columns = 1;
                x = imgControl.getIntX();
                y += imgControl.getIntHeight() + clipControl.getIntHeight();
                if (maker.templateType != BookMaker.TYPE_A)
                    y += MARGIN_VALUE_FRIEND_GRID;

                if (++rows > ROW_COUNT_FRIEND_GRID) {
                    rows = 1;
                    if (isOverOnePage)
                        break;
                    isOverOnePage = true;
                    y = imgControl.getIntY();
                }
            }
        }
    }

    private void setData(SnapsPage page, FacebookPhotobookPageType pageIndex) {
        String snsProperty = "";

        ArrayList<SnapsControl> controls = page.getLayerControls();
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsTextControl) {
                snsProperty = ((SnapsTextControl) controls.get(i)).getSnsproperty();
                // summary
                if ("totalresponse".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + (maker.response[BookMaker.LIKE_COUNT_INDEX] + maker.response[BookMaker.COMMENT_COUNT_INDEX] + maker.response[BookMaker.SHARE_COUNT_INDEX]);
                else if ("totalfeeling".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.response[BookMaker.LIKE_COUNT_INDEX];
                else if ("totalreplies".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.response[BookMaker.COMMENT_COUNT_INDEX];
                else if ("totalsharing".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.response[BookMaker.SHARE_COUNT_INDEX];
                else if ("feeling".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline().getSummary()[BookMaker.LIKE_COUNT_INDEX];
                else if ("replies".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline().getSummary()[BookMaker.COMMENT_COUNT_INDEX];
                else if ("sharing".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline().getSummary()[BookMaker.SHARE_COUNT_INDEX];
                else if ("totalfriends".equalsIgnoreCase(snsProperty)) {
                    String totalfriends = "" + maker.getFrieldList().size();
                    ((SnapsTextControl) controls.get(i)).text = totalfriends;
                    if (((SnapsTextControl) controls.get(i)).id.equals("")) {
                    } else {
                        if (totalfriends.length() > 2) {
                            SnapsControl c = page.getControlByStickerTarget(((SnapsTextControl) controls.get(i)).id);
                            if (c != null) {
                                c.x = (c.getIntX() - (totalfriends.length() - 2) * 15) + "";
                            }
                        }
                    }
                } else if ("totalpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getTotalPost();
                else if ("totalwritingpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.postCount[BookMaker.POST_WRITE_COUNT_INDEX];
                else if ("totalcomplexpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.postCount[BookMaker.POST_PHOTO_COUNT_INDEX];
                else if ("totalvideopost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.postCount[BookMaker.POST_VIDEO_COUNT_INDEX];
                else if ("totalsharingpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.postCount[BookMaker.POST_SHARED_COUNT_INDEX];
                else if ("totalmessage".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.messageCount;
                    // friends
                else if ("closefriendfeeling".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).likeCount;
                } else if ("closefriendreplies".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).commentCount;
                } else if ("closefriendnickname".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).name;
                } else if ("totalsharedwritingpost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.sharedCount[BookMaker.POST_WRITE_COUNT_INDEX];
                } else if ("totalsharedcomplexpost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.sharedCount[BookMaker.SHARED_PHOTO_COUNT_INDEX];
                } else if ("totalsharedvideopost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.sharedCount[BookMaker.SHARED_VIDEO_COUNT_INDEX];
                } else if ("postdate".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + FBCommonUtil.convertDateString(((SnapsTextControl) controls.get(i)).getFormat(), maker.getBestTimeline().createDate, null);
                else if ("text".equalsIgnoreCase(snsProperty)) {
                    String text = "" + (maker.getBestTimeline().content != null ? maker.getBestTimeline().content : "");
                    if (text != null) {
                        ((SnapsTextControl) controls.get(i)).text = "" + (maker.getBestTimeline().content != null ? maker.getBestTimeline().content : "");
                        setChapterPageAttr(page);
                    }
                }
            }
        }

        controls = page.getLayerLayouts();
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsLayoutControl) {
                snsProperty = ((SnapsLayoutControl) controls.get(i)).getSnsproperty();
                ImageInfo imgInfo = null;
                try {
                    // best timeline
                    if ("profile".equalsIgnoreCase(snsProperty))
                        imgInfo = getTimelineImage(maker.getBestTimeline());
                        // close friends
                    else if ("closefriendprofile".equalsIgnoreCase(snsProperty)) {
                        int value = Integer.parseInt(((SnapsLayoutControl) controls.get(i)).regValue);
                        if (value < maker.getFrieldList().size() + 1)
                            imgInfo = getImageInfo(maker.getFrieldList().get(value - 1));
                    } else if ("best".equalsIgnoreCase(snsProperty)) {
                        imgInfo = getTimelineImage(maker.getBestTimeline());
                    }

                    if (imgInfo != null)
                        setImageInfoToControl((SnapsLayoutControl) controls.get(i), imgInfo);
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    //FIXME 챕터 제작 용
    private void setChapterData(SnapsPage page, ChapterData chapter) {
        String snsProperty = "";
        String format = "";

        ArrayList<SnapsControl> controls = page.getLayerControls();
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsTextControl) {
                snsProperty = controls.get(i).getSnsproperty();
                format = controls.get(i).getFormat();

                // summary
                int[] summary = chapter.getSummary();
                int[][] postCount = chapter.getPostCounts();
                int totalCount = 0;
                for (int k = 0; k < postCount.length; ++k) {
                    for (int j = 0; j < postCount[k].length; ++j) totalCount += postCount[k][j];
                }
                if ("totalresponse".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + (summary[BookMaker.LIKE_COUNT_INDEX] + summary[BookMaker.COMMENT_COUNT_INDEX] + summary[BookMaker.SHARE_COUNT_INDEX]);
                else if ("totalfeeling".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + summary[BookMaker.LIKE_COUNT_INDEX];
                else if ("totalreplies".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + summary[BookMaker.COMMENT_COUNT_INDEX];
                else if ("totalsharing".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + summary[BookMaker.SHARE_COUNT_INDEX];
                else if ("feeling".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline(chapter).getSummary()[BookMaker.LIKE_COUNT_INDEX];
                else if ("replies".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline(chapter).getSummary()[BookMaker.COMMENT_COUNT_INDEX];
                else if ("sharing".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getBestTimeline(chapter).getSummary()[BookMaker.SHARE_COUNT_INDEX];
                else if ("totalfriends".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().size();
                    ((SnapsTextControl) controls.get(i)).y = "" + (Integer.parseInt(((SnapsTextControl) controls.get(i)).y) - 10);
                } else if ("totalpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + totalCount;
                else if ("totalwritingpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[0][BookMaker.POST_WRITE_COUNT_INDEX];
                else if ("totalcomplexpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[0][BookMaker.POST_PHOTO_COUNT_INDEX];
                else if ("totalvideopost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[0][BookMaker.POST_VIDEO_COUNT_INDEX];
                else if ("totalsharingpost".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[0][BookMaker.POST_SHARED_COUNT_INDEX];
                else if ("totalmessage".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[2][0];
                    // friends
                else if ("closefriendfeeling".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).likeCount;
                } else if ("closefriendreplies".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).commentCount;
                } else if ("closefriendnickname".equalsIgnoreCase(snsProperty)) {
                    int value = Integer.parseInt(((SnapsTextControl) controls.get(i)).regValue);
                    if (value < maker.getFrieldList().size() + 1)
                        ((SnapsTextControl) controls.get(i)).text = "" + maker.getFrieldList().get(value - 1).name;
                } else if ("postdate".equalsIgnoreCase(snsProperty) && !"XDAY".equalsIgnoreCase(format)) {
                    ((SnapsTextControl) controls.get(i)).text =
                            FBCommonUtil.convertDateString(controls.get(i).getFormat(), maker.getBestTimeline(chapter).createDate, null);
                } else if ("postdate".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text =
                            FBCommonUtil.convertDateString(controls.get(i).getFormat(), maker.getBestTimeline(chapter).createDate, null);
                } else if ("text".equalsIgnoreCase(snsProperty)) {
                    String text = maker.getBestTimeline(chapter).content;
                    if (text != null) {
                        ((SnapsTextControl) controls.get(i)).text = maker.getBestTimeline(chapter).content;
                        setChapterPageAttr(page);
                    }
                } else if ("chapter".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text =
                            String.format(Locale.getDefault(), "%02d", chapter.chapterIndex);  //FIXME 현재 챕터 번호를 써야 한다..
                } else if ("totalsharedwritingpost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[1][BookMaker.SHARED_WRITE_COUNT_INDEX];
                } else if ("totalsharedcomplexpost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[1][BookMaker.SHARED_PHOTO_COUNT_INDEX];
                } else if ("totalsharedvideopost".equalsIgnoreCase(snsProperty)) {
                    ((SnapsTextControl) controls.get(i)).text = "" + postCount[1][BookMaker.SHARED_VIDEO_COUNT_INDEX];
                } else if ("endmonth".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + chapter.getEndMonthStr(true) + " " + chapter.getEndYear();
                else if ("startmonth".equalsIgnoreCase(snsProperty))
                    ((SnapsTextControl) controls.get(i)).text = "" + chapter.getStartMonthStr(true) + " " + chapter.getStartYear();
                else {
                }
            }
        }

        controls = page.getLayerLayouts();
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsLayoutControl) {
                snsProperty = ((SnapsLayoutControl) controls.get(i)).getSnsproperty();
                ImageInfo imgInfo = null;
                try {
                    // best timeline
                    if ("profile".equalsIgnoreCase(snsProperty))
                        imgInfo = getTimelineImage(maker.getBestTimeline(chapter));
                        // close friends
                    else if ("closefriendprofile".equalsIgnoreCase(snsProperty)) {
                        int value = Integer.parseInt(((SnapsLayoutControl) controls.get(i)).regValue);
                        if (value < maker.getFrieldList().size() + 1)
                            imgInfo = getImageInfo(maker.getFrieldList().get(value - 1));
                    } else if ("best".equalsIgnoreCase(snsProperty)) {
                        //FIXME
                        imgInfo = getImageInfo(maker.getBestTimeline(chapter));
                    } else {
                        Dlog.w(TAG, "setChapterData() snsProperty:" + snsProperty);
                    }

                    if (imgInfo != null)
                        setImageInfoToControl((SnapsLayoutControl) controls.get(i), imgInfo);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    continue;
                }
            }
        }
    }

    private void setImageInfoToControl(SnapsLayoutControl control, ImageInfo imgInfo) {
        MyPhotoSelectImageData myPhotoImageData = new MyPhotoSelectImageData();
        myPhotoImageData.KIND = Const_VALUES.SELECT_FACEBOOK;
        myPhotoImageData.PATH = imgInfo.original;
        myPhotoImageData.THUMBNAIL_PATH = imgInfo.medium;
        // 원본 이미지 w, h 추가
        myPhotoImageData.F_IMG_WIDTH = imgInfo.getOriginWidth();
        myPhotoImageData.F_IMG_HEIGHT = imgInfo.getOriginHeight();
        myPhotoImageData.cropRatio = control.getRatio();

        control.imgData = myPhotoImageData;
        control.imagePath = myPhotoImageData.PATH;
        control.thumPath = myPhotoImageData.THUMBNAIL_PATH;
        control.imageLoadType = myPhotoImageData.KIND;
        control.qrCodeUrl = imgInfo.targetUrl;
        control.angle = "0";
    }

    private void setBestImages(SnapsPage page) {
        ArrayList<AlbumData> albums = maker.getBestAlbumList();

        ArrayList<SnapsControl> bestLayouts = page.getLayoutListByProperty("best");
        Collections.sort(bestLayouts, new ValueAscCompare());
        int index = 0;
        for (SnapsControl c : bestLayouts) {
            if (albums.size() < index + 1) break;

            SnapsLayoutControl control = (SnapsLayoutControl) c;
            ImageInfo imgInfo = null;

            try {
                imgInfo = getImageInfoFromAlbum(albums.get(index));
            } catch (Exception e) {
                Dlog.e(TAG, e);
                continue;
            }

            setImageInfoToControl(control, imgInfo);
            index++;
        }
    }

    private void setProfilePage(SnapsPage page) {
        // text
        ArrayList<String> strAry = new ArrayList<String>();
        if (maker.email != null && maker.email.length() > 0)
            strAry.add("Facebook _ " + maker.email);
        if (maker.birthday != null && maker.birthday.length() > 0)
            strAry.add("Birthday _ " + BookMaker.getBirthdayString(maker.birthday));

        String email = "", phone = "", birth = "";
        for (int i = 0; i < strAry.size(); ++i) {
            if (email == null || email.length() < 1) email = strAry.get(i);
                //else if( phone == null || phone.length() < 1 ) phone = strAry.get(i);
            else if (birth == null || birth.length() < 1) birth = strAry.get(i);
        }

        final int MAX_NAME_LENGTH = 13;

        ArrayList<SnapsControl> controls = page.getLayerControls();
        int x, w;
        String property;
        boolean isNicknameMultiLine = false;
        for (int i = 0; i < controls.size(); ++i) {
            if (controls.get(i) instanceof SnapsTextControl) {
                property = ((SnapsTextControl) controls.get(i)).getSnsproperty();
                if ("nickname".equalsIgnoreCase(property) || "e-mail".equalsIgnoreCase(property)
                        || "birth".equalsIgnoreCase(property) || "phone".equalsIgnoreCase(property) || "period".equalsIgnoreCase(property)) {
                    x = Integer.parseInt(((SnapsTextControl) controls.get(i)).x);
                    w = Integer.parseInt(((SnapsTextControl) controls.get(i)).width);

                    if ("nickname".equalsIgnoreCase(property)) {
                        String name = maker.name;
                        if (name != null && name.length() > MAX_NAME_LENGTH) {
                            try {
                                int spaceIdx = name.contains(" ") ? Math.min(MAX_NAME_LENGTH, name.lastIndexOf(" ")) : MAX_NAME_LENGTH;
                                String firstName = name.substring(0, spaceIdx).trim();
                                String lastName = name.substring(spaceIdx).trim();

                                SnapsTextControl copyControl = ((SnapsTextControl) controls.get(i)).copyControl();

                                int y = (int) Float.parseFloat(((SnapsTextControl) controls.get(i)).y);
                                int fontSize = (int) Float.parseFloat(((SnapsTextControl) controls.get(i)).format.fontSize);

                                if (lastName != null && lastName.length() > MAX_NAME_LENGTH) {
                                    String subName = lastName.substring(MAX_NAME_LENGTH);
                                    lastName = lastName.substring(0, MAX_NAME_LENGTH);

                                    SnapsTextControl subControl = ((SnapsTextControl) controls.get(i)).copyControl();

                                    ((SnapsTextControl) controls.get(i)).y = String.valueOf(y - (fontSize * 3));
                                    copyControl.y = String.valueOf(y - (fontSize * 1.5));
                                    subControl.y = String.valueOf(y);
                                    ((SnapsTextControl) controls.get(i)).text = firstName;
                                    copyControl.text = lastName;
                                    subControl.text = subName;

                                    controls.add(copyControl);
                                    controls.add(subControl);
                                } else {
                                    ((SnapsTextControl) controls.get(i)).y = String.valueOf(y - (fontSize * 1.5));
                                    copyControl.y = String.valueOf(y);

                                    ((SnapsTextControl) controls.get(i)).text = firstName;
                                    copyControl.text = lastName;
                                    controls.add(copyControl);
                                }

                                isNicknameMultiLine = true;
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                ((SnapsTextControl) controls.get(i)).text = name;
                            }

                        } else
                            ((SnapsTextControl) controls.get(i)).text = name;
                    } else if ("e-mail".equalsIgnoreCase(property))
                        ((SnapsTextControl) controls.get(i)).text = email;
                    else if ("birth".equalsIgnoreCase(property))
                        ((SnapsTextControl) controls.get(i)).text = birth;
                    else if ("phone".equalsIgnoreCase(property))
                        ((SnapsTextControl) controls.get(i)).text = phone;
                    else if ("period".equalsIgnoreCase(property)) {
                        ((SnapsTextControl) controls.get(i)).text = FBCommonUtil.convertDateString(((SnapsTextControl) controls.get(i)).getFormat(),
                                StringUtil.convertDateStrToCalendar(maker.getStartTimeStr()), StringUtil.convertDateStrToCalendar(maker.getEndTimeStr()));
                        if (isNicknameMultiLine) {
                            try {
                                int y = (int) Float.parseFloat(((SnapsTextControl) controls.get(i)).y);
                                int fontSize = (int) Float.parseFloat(((SnapsTextControl) controls.get(i)).format.fontSize);
                                ((SnapsTextControl) controls.get(i)).y = String.valueOf(y + fontSize);
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    }

                    String align = ((SnapsTextControl) controls.get(i)).format.align;
                    if (align.equalsIgnoreCase("right")) {
                        ((SnapsTextControl) controls.get(i)).x = "0";
                        ((SnapsTextControl) controls.get(i)).width = x + w + "";
                    } else if (align.equalsIgnoreCase("center")) {
                        ((SnapsTextControl) controls.get(i)).x = ((SnapsTextControl) controls.get(i)).getIntX() + "";
                        ((SnapsTextControl) controls.get(i)).width = ((SnapsTextControl) controls.get(i)).getIntWidth() + "";
                    }
                }
            }
        }

        // images
        ArrayList<AlbumData> profiles = maker.getProfileAlbumList();

        if (profiles.size() < 1) {
            AlbumData data = new AlbumData(AlbumData.TYPE_PROFILE);
            data.fullPicture = maker.profileOriginUrl;
            data.thumb = maker.profileOriginUrl;
            profiles.add(data);
        }

        ArrayList<SnapsControl> profileLayers = page.getLayoutListByProperty("profile"); //FIXME 가장 최신 사진으로 셋팅 해 달라 합니다.
        Collections.sort(profileLayers, new ValueAscCompare());

        if (profileLayers.size() < 1) return;

        SnapsLayoutControl control = null;
        ImageInfo imgInfo = null;

        if (profileLayers.size() > 1) {
            int profileSize = profiles.size() > 4 ? 4 : profiles.size();
            int[][] indexAry = {{}, {0}, {1, 2}, {1, 3, 4}, {1, 3, 4, 2}};
            int[] temp = indexAry[profileSize];
            for (int i = 0; i < temp.length; ++i) {
                if (profileLayers.size() <= temp[i]) break;

                control = (SnapsLayoutControl) profileLayers.get(temp[i]);
                imgInfo = null;

                try {
                    imgInfo = getImageInfoFromAlbum(profiles.get(0));
                    profiles.remove(0);
                    setImageInfoToControl(control, imgInfo);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    continue;
                }
            }
        } else {
            control = (SnapsLayoutControl) profileLayers.get(0);
            try {
                imgInfo = getImageInfo(maker.profileThumbUrl, maker.profileOriginUrl, maker.profileOriginSize[0], maker.profileOriginSize[1]);
                profiles.remove(0);
                setImageInfoToControl(control, imgInfo);
            } catch (Exception e) {
                Dlog.e(TAG, e);
                return;
            }
        }
    }

    private void setCoverEditable(SnapsPage page) {
        // 이미지..
        for (SnapsControl c : page.getLayerLayouts()) {
            if (c instanceof SnapsLayoutControl && !c.getSnsproperty().equals("profile")) {
                SnapsLayoutControl control = (SnapsLayoutControl) c;
                control.isSnsBookCover = true;
            }
        }
    }

    /***
     * 책등 텍스트를 설정하는 함수..
     * @param coverPage
     */
    private SnapsTextControl setCoverSpineText(SnapsPage coverPage) {
        // 텍스트..
        for (SnapsControl t : coverPage.getTextControlList()) {
            SnapsTextControl tControl = (SnapsTextControl) t;
            if (tControl != null && tControl.format.verticalView.equals("true")) {
                tControl.text = maker.getPeriodString().replace("-", ".").replace(" ~ ", " - ") + " " + maker.coverTitle;
                tControl.format.auraOrderFontSize = "14";
                return tControl;
            }
        }
        return null;
    }

    public ImageInfo getProfileImage() {
        if (maker == null) return null;
        if (maker.profileOriginUrl == null || maker.profileOriginUrl.length() < 1 || maker.profileThumbUrl == null || maker.profileThumbUrl.length() < 1)
            return null;

        String imgUrl = maker.profileOriginUrl;
        String thumbUrl = maker.profileThumbUrl;

        if (imgUrl == null || imgUrl.trim().length() < 1) {
            imgUrl = DEFAULT_PROFILE_IMG_URL;
            thumbUrl = DEFAULT_PROFILE_IMG_URL;
        }

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = thumbUrl;
        imgInfo.medium = thumbUrl;
        imgInfo.small = thumbUrl;
        imgInfo.original = imgUrl;

        imgInfo.setOriginWidth("640");
        imgInfo.setOriginHeight("640");

        if (imgInfo.original != null) {
            if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
                imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgInfo.original, "width"));
                imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgInfo.original, "height"));
            }
        }
        return imgInfo;
    }

    public ImageInfo getImageInfoFromAlbum(AlbumData data) {
        if (data == null) return null;

        String imgUrl = data.fullPicture;
        String thumbUrl = data.thumb;

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = thumbUrl;
        imgInfo.medium = thumbUrl;
        imgInfo.small = thumbUrl;
        imgInfo.original = imgUrl;

        imgInfo.setOriginWidth("640");
        imgInfo.setOriginHeight("640");

        if (imgInfo.original != null) {
            if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
                imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgUrl, "width"));
                imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgUrl, "height"));
            }
        }

        return imgInfo;
    }

    public ImageInfo getImageInfo(String thumb, String origin, int width, int height) {
        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = thumb;
        imgInfo.medium = thumb;
        imgInfo.small = thumb;
        imgInfo.original = origin;

        imgInfo.setOriginWidth(width + "");
        imgInfo.setOriginHeight(height + "");

        if (imgInfo.original != null) {
            if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
                imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(origin, "width"));
                imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(origin, "height"));
            }
        }
        return imgInfo;
    }

    public ImageInfo getImageInfo(TimelineData data) {
        String thumb, origin, width = "640", height = "640";

        thumb = data.getThumbnailUrl();
        if (data.getAttachments() != null && data.getAttachments().size() > 0) {
            AttachmentData attach = data.getAttachments().get(0);
            origin = attach.imageUrl;

            if (attach.width == AttachmentData.INVALID_IMAGE_DIMENSION
                    || attach.height == AttachmentData.INVALID_IMAGE_DIMENSION) {
                Rect imageRect = HttpUtil.getNetworkImageRect(attach.imageUrl);
                attach.width = imageRect.width();
                attach.height = imageRect.height();
            }

            width = "" + attach.width;
            height = "" + attach.height;
        } else origin = data.getFullPictureUrl();

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = thumb;
        imgInfo.medium = thumb;
        imgInfo.small = thumb;
        imgInfo.original = origin;

        imgInfo.setOriginWidth(width);
        imgInfo.setOriginHeight(height);

        if (imgInfo.original != null) {
            if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
                imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(origin, "width"));
                imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(origin, "height"));
            }
        }
        return imgInfo;
    }

    public ImageInfo getImageInfo(FriendData friend) {
        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = friend.thumbUrl;
        imgInfo.medium = friend.thumbUrl;
        imgInfo.small = friend.thumbUrl;
        imgInfo.original = friend.originUrl;

        imgInfo.setOriginWidth("" + friend.originSize[0]);
        imgInfo.setOriginHeight("" + friend.originSize[1]);

        return imgInfo;
    }

    public ImageInfo getImageInfo(String friendId) {
        FriendData friend = null;
        for (int i = 0; i < maker.getFrieldList().size(); ++i) {
            if (friendId.equals(maker.getFrieldList().get(i).id)) {
                friend = maker.getFrieldList().get(i);
                break;
            }
        }

        if (friend == null) {
            if (maker.id.equals(friendId)) {
                friend = new FriendData(maker.id, maker.name);
                friend.thumbUrl = maker.profileThumbUrl;
                friend.originUrl = maker.profileOriginUrl;
                friend.originSize = maker.profileOriginSize;
            } else return null;
        }

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = friend.thumbUrl;
        imgInfo.medium = friend.thumbUrl;
        imgInfo.small = friend.thumbUrl;
        imgInfo.original = friend.originUrl;

        imgInfo.setOriginWidth("" + friend.originSize[0]);
        imgInfo.setOriginHeight("" + friend.originSize[1]);

        return imgInfo;
    }

    public ImageInfo getCoverImage() {
        if (maker == null) return null;
        if (maker.coverUrl == null || maker.coverUrl.length() < 1) return null;

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = maker.coverUrl;
        imgInfo.medium = maker.coverUrl;
        imgInfo.small = maker.coverUrl;
        imgInfo.original = maker.coverUrl;

        imgInfo.setOriginWidth("640");
        imgInfo.setOriginHeight("640");

        if (imgInfo.original != null) {
            if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
                imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgInfo.original, "width"));
                imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgInfo.original, "height"));
            } else {
                //UI Thread면 죽기 때문에..체크를 해 줌.
                try {
                    if (Looper.myLooper() != Looper.getMainLooper()) {
                        Rect rect = HttpUtil.getNetworkImageRect(imgInfo.original);
                        if (rect != null) {
                            String width = String.valueOf(rect.width());
                            String height = String.valueOf(rect.height());
                            if (width != null && width.length() > 0 && height != null && height.length() > 0) {
                                imgInfo.setOriginWidth(width);
                                imgInfo.setOriginHeight(height);
                            }
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }
        return imgInfo;
    }

    public ImageInfo getTimelineImage(TimelineData timeline) {
        return getTimelineImage(timeline, 0);
    }

    public ImageInfo getTimelineImage(TimelineData timeline, int index) {
        if (timeline == null) return null;
        ArrayList<AttachmentData> list = timeline.type == TimelineData.TYPE_MESSAGE ? timeline.feed.attachmentList : timeline.post.attachmentList;

        if (list == null || list.size() < index + 1) return null;

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = timeline.getThumbnailUrl();
        imgInfo.medium = timeline.getThumbnailUrl();
        imgInfo.small = timeline.getThumbnailUrl();
        imgInfo.original = timeline.getFullPictureUrl();

        imgInfo.setOriginWidth("640");
        imgInfo.setOriginHeight("640");

        if (imgInfo.original != null) {

            if (list.get(0).width == AttachmentData.INVALID_IMAGE_DIMENSION
                    || list.get(0).height == AttachmentData.INVALID_IMAGE_DIMENSION) {
                Rect imageRect = HttpUtil.getNetworkImageRect(list.get(0).imageUrl);
                list.get(0).width = imageRect.width();
                list.get(0).height = imageRect.height();
            }

            if (list.get(0).width > 0 && list.get(0).height > 0) {
                imgInfo.setOriginWidth("" + list.get(0).width);
                imgInfo.setOriginHeight("" + list.get(0).height);
            }
        }

        return imgInfo;
    }

    public ImageInfo getImageInfoFromAttachment(AttachmentData data, boolean isMemorySharePost) {
        if (data.imageUrl == null || data.imageUrl.length() < 1) return null;

        ImageInfo imgInfo = new ImageInfo();
        imgInfo.large = data.imageUrl;
        imgInfo.medium = data.imageUrl;
        imgInfo.small = data.thumbUrl;
        imgInfo.original = data.imageUrl;

        imgInfo.setOriginWidth("640");
        imgInfo.setOriginHeight("640");

        if (data.width == AttachmentData.INVALID_IMAGE_DIMENSION
                || data.height == AttachmentData.INVALID_IMAGE_DIMENSION) {
            Rect imageRect = HttpUtil.getNetworkImageRect(data.imageUrl);
            data.width = imageRect.width();
            data.height = imageRect.height();
        }

        if (data.width > 0 && data.height > 0) {
            imgInfo.setOriginWidth("" + data.width);
            imgInfo.setOriginHeight("" + data.height);
        }

        imgInfo.targetId = data.targetId != null ? data.targetId : "";
        imgInfo.targetUrl = data.getQrCodeUrl() != null ? data.getQrCodeUrl() : "";
        if (isMemorySharePost) imgInfo.targetUrl = data.targetUrl;
        return imgInfo;
    }

    /***
     * 페이지 타입에 따라 페이지를 가져오는 함수..
     *
     * @param template
     * @param type
     * @return
     */
    protected SnapsPage getPage(SnapsTemplate template, FacebookPhotobookPageType type) {
        return template.getPages().get(type.getIndex());
    }

    protected SnapsClipartControl getClipart(SnapsPage page, String snsproperty) {

        for (SnapsControl c : page.getClipartControlList()) {
            SnapsClipartControl cc = (SnapsClipartControl) c;
            if (cc.getSnsproperty().equals(snsproperty))
                return cc;
        }

        return null;
    }

    protected SnapsTextControl getTextControl(SnapsPage page, String snsproperty) {
        for (SnapsControl c : page.getTextControlList()) {
            SnapsTextControl cc = (SnapsTextControl) c;
            if (cc.getSnsproperty().equals(snsproperty))
                return cc;
        }

        return null;
    }

    public SNSBookInfo getInfo() {
        BookMaker maker = BookMaker.getInstance();
        SNSBookInfo info = new SNSBookInfo();
        info.setThumbUrl(maker.profileThumbUrl);
        info.setPeriod(maker.getPeriodString());
        info.setPageCount(totalPage + "");

        String name = maker.name;
        if (name.length() > 4) name = name.substring(0, 4) + "...";
        info.setUserName(name);
        info.setMaxPageEdited(isMaxPageEdited);

        return info;
    }

    protected ImageInfo getImageData(String snsProperty) {
        if (snsProperty.equals("main")) return getCoverImage();
        else if (snsProperty.equals("profile")) return getProfileImage();
        else return null;
    }

    protected String getTextData(String snsproperty, String format) {
        if ("period".equalsIgnoreCase(snsproperty)) {
            if (format.startsWith("-"))
                return "- " + maker.getEndTimeStr().replace("-", ".");
            else {
                return FBCommonUtil.convertDateString(format,
                        maker.getStartCal(),
                        maker.getEndCal());
            }
        } else if ("by nickname".equals(snsproperty)) return "by " + maker.name;
        else if ("By. nickname".equals(snsproperty)) return "By. " + maker.name;
        else if ("title".equals(snsproperty)) return maker.coverTitle;
        else if ("nickname".equals(snsproperty)) return maker.name;

        return "";
    }

    /***
     * 페이지에 데이터를 채우는 함수.
     *napsScheme
     * @param page
     */
    protected void setDataControls(SnapsPage page) {
        // 이미지..
        for (SnapsControl c : page.getLayerLayouts()) {
            if (c instanceof SnapsLayoutControl) {
                SnapsLayoutControl control = (SnapsLayoutControl) c;

                ImageInfo imgInfo = getImageData(control.getSnsproperty());
                if (imgInfo != null) {
                    MyPhotoSelectImageData myPhotoImageData = new MyPhotoSelectImageData();
                    myPhotoImageData.KIND = Const_VALUES.SELECT_FACEBOOK;
                    myPhotoImageData.PATH = imgInfo.original;
                    myPhotoImageData.THUMBNAIL_PATH = imgInfo.original;
                    // 원본 이미지 w, h 추가
                    myPhotoImageData.F_IMG_WIDTH = imgInfo.getOriginWidth();
                    myPhotoImageData.F_IMG_HEIGHT = imgInfo.getOriginHeight();
                    myPhotoImageData.cropRatio = control.getRatio();

                    control.imgData = myPhotoImageData;
                    control.imagePath = myPhotoImageData.PATH;
                    control.thumPath = myPhotoImageData.THUMBNAIL_PATH;
                    control.imageLoadType = myPhotoImageData.KIND;
                    control.angle = "0";
                }
            }
        }

        // 텍스트..
        for (SnapsControl t : page.getLayerControls()) {
            if (t instanceof SnapsTextControl) {
                String data = null;
                if (t.getSnsproperty().length() != 0)
                    data = getTextData(t.getSnsproperty(), t.getFormat());

                if (data != null && !data.equals("")) {
                    ((SnapsTextControl) t).text = data;
                } else if (!((SnapsTextControl) t).emptyText.equals(""))
                    ((SnapsTextControl) t).text = ((SnapsTextControl) t).emptyText;
            }
        }

        // 위치 보정, 나중에 다시 쓰든가 함.
        for (SnapsControl t : page.getLayerControls()) {
            if (t instanceof SnapsClipartControl || t instanceof SnapsTextControl) {
                String coordX = getCoordinateByStick(page, t);
                if (coordX != null) {
                    t.x = coordX;
                }
            }
        }
    }

    protected String getCoordinateByStick(SnapsPage page, SnapsControl control) {
        // offsetControl 기준 컨트롤
        // control 따라 다니는 컨트롤..

        if (control == null)
            return null;

        String targetName = control.stick_target;
        String direction = control.stick_dirction;
        String margin = control.stick_margin;

        if (targetName == null || direction == null || margin == null || targetName.length() < 1 || direction.length() < 1 || margin.length() < 1)
            return null;

        // 템플릿별 예외처리..
        if (targetName.contains("11-p4"))
            return null;

        SnapsControl offsetControl = findStickOffsetTargetControl(page, targetName);

        if (offsetControl == null)
            return null;

        // 강제 보정치.
        int adjust = 0;

        if (targetName.contains("12-p4")) {
            // ordinary 디자인 챕터에 텍스트 겹침 해소용으로 추가.
            // stories
            if (targetName.equals("12-p4-3") || targetName.equals("12-p4-6")) {
                control.x = control.getIntX() - (offsetControl.getIntWidth() / 5) * (4 - ((SnapsTextControl) offsetControl).text.length()) + "";
                return null;
            } else if (control.id.equals("12-p4-3") || control.id.equals("12-p4-6") || control.id.equals("12-p4-5"))
                return null;

            else if (targetName.equals("12-p4-2") || targetName.equals("12-p4-1")) {
                // offset의 길이에 따라.. 조절을 해야 함..
                int length = ((SnapsTextControl) offsetControl).text.length();
                adjust = (7 - length) * -5;
            }

        }

        // 2번째 디자인..
        else if (targetName.contains("10-p4-1")) {
            offsetControl.width = offsetControl.getIntWidth() + control.getIntWidth() + "";
            ((SnapsTextControl) offsetControl).text = ((SnapsTextControl) offsetControl).text + "  " + ((SnapsTextControl) control).text;
            ((SnapsTextControl) control).text = "";
            return null;
        }

        int coord = 0;
        boolean isLeftOfOffset = direction.trim().equalsIgnoreCase("left");
        try {
            coord = getCoordOffsetControl(offsetControl, control, isLeftOfOffset);
            if (isLeftOfOffset) {
                coord -= UIUtil.convertDPtoPX(context, (int) Float.parseFloat(margin) + STICK_FIX_MARGIN_VALUE);
            } else {
                coord += UIUtil.convertDPtoPX(context, (int) Float.parseFloat(margin) + STICK_FIX_MARGIN_VALUE);
            }
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        return String.valueOf(coord + adjust);
    }

    protected int getCoordOffsetControl(SnapsControl offsetControl, SnapsControl control, boolean isLeft) throws NumberFormatException {
        if (offsetControl == null)
            return 0;

        int offset = Integer.parseInt(control.x);

        float coordControlRight = Float.parseFloat(offsetControl.x) + Float.parseFloat(offsetControl.width);
        float offsetControlSize = 0.f;

        if (isLeft) {
            if (offsetControl instanceof SnapsTextControl) {
                SnapsTextControl textControl = (SnapsTextControl) offsetControl;
                offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3정도
                // 보정을 해
                // 줘야
                // 맞음..
                int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
                offset = (int) (coordControlRight - fontTotalSize);
            } else {
                offset = (int) (Float.parseFloat(offsetControl.x));
            }

            offset -= Float.parseFloat(control.width);
        } else {
            if (offsetControl instanceof SnapsTextControl) {
                SnapsTextControl textControl = (SnapsTextControl) offsetControl;
                offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3정도
                // 보정을 해
                // 줘야
                // 맞음..
                int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
                offset = (int) (Float.parseFloat(offsetControl.x) + fontTotalSize);
            } else {
                offset = (int) coordControlRight;
            }
        }

        return offset;
    }

    protected SnapsControl findStickOffsetTargetControl(SnapsPage page, String name) {
        if (page == null || name == null)
            return null;
        for (SnapsControl t : page.getLayerControls()) {
            if (t != null && t.id != null && t.id.trim().equals(name))
                return t;
        }
        return null;
    }

    /**
     * value 오름차순
     */
    static class ValueAscCompare implements Comparator<SnapsControl> {
        @Override
        public int compare(SnapsControl arg0, SnapsControl arg1) {
            // TODO Auto-generated method stub
            return Integer.parseInt(arg0.regValue) > Integer.parseInt(arg1.regValue) ? 1 : Integer.parseInt(arg0.regValue) < Integer.parseInt(arg1.regValue) ? -1 : 0;
        }
    }


    SnapsLayoutControl commentBG = null;

    //댓글에 배경을 그려줄거다
    void startPostCommentBackgroud(String x, String y, SnapsPage page, boolean isPostComment) {
        startPostCommentBackgroud(x, y, page, isPostComment, false, 0);
    }

    void startPostCommentBackgroud(String x, String y, SnapsPage page, boolean isPostComment, int widthFix) {
        startPostCommentBackgroud(x, y, page, isPostComment, false, widthFix);
    }

    void startPostCommentBackgroud(String x, String y, SnapsPage page, boolean isPostComment, boolean isAddPre) {
        startPostCommentBackgroud(x, y, page, isPostComment, isAddPre, 0);
    }

    void startPostCommentBackgroud(String x, String y, SnapsPage page, boolean isPostComment, boolean isAddPre, int widthFix) {

        if (commentBG != null)
            return;

        SnapsLayoutControl l = new SnapsLayoutControl();
        l.x = x;
        l.y = y;
        l.width = "" + (137 + widthFix);
        l.type = isPostComment ? "webitem" : "border";
        l.angle = "0";
        l.tempImageColor = "FFF7F8F8";
        l.bgColor = "FFF7F8F8";
        l.regName = "background";
        l.imagePath = "";
        commentBG = l;
        if (isAddPre) {
            ArrayList<SnapsControl> aa = page.getLayoutList();
            aa.add(aa.size() - 1, commentBG);
        } else {
            page.addLayout(commentBG);
        }
    }

    boolean endPostCommentBackgroud(int height) {
        boolean isApply = false;
        if (commentBG != null && (height != commentBG.getIntY())) {
            int h = (height - commentBG.getIntY() + COMMENT_BG_MARGIN);
            commentBG.height = h + "";
            isApply = true;
        }

        commentBG = null;

        return isApply;
    }

    final float NOTE_FONT_RATIO = 0.75f;// 댓글인경우 괜찮다. 렌더 텍스트 크기를 맞추기 위해 설정..

    void setChapterPageAttr(SnapsPage page) {
        if (page == null)
            return;

        // 라인텍스트 정보를 가져온다.
        int limitSize = 0;
        float fontRatio = 0.f;
        fontRatio = 1.0f;

        for (SnapsControl t : page.getLayerControls()) {
            if (t instanceof SnapsTextControl && t.getSnsproperty().equalsIgnoreCase("text")) {
                SnapsTextControl note = ((SnapsTextControl) t);
                note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
                note._controlType = SnapsControl.CONTROLTYPE_TEXT;
                limitSize = note.getIntWidth();

                MultiLineTextData textData = CalcViewRectUtil.getTextControlRect(context, note.format.fontFace, note.format.fontSize, limitSize, note.text, fontRatio, FontUtil.TEXT_TYPE_CHAPTER);

                if (textData == null)
                    return;

                if (note != null) {
                    String sText = "";
                    int curHeight = 0;
                    int PADDING_BOTTOM = textData.getTextLineHeight(note, 3);
                    int maxHeight = note.getIntHeight() - PADDING_BOTTOM;

                    for (int i = 0; i < textData.getLineTexts().size(); i++) {
                        if (!sText.equals(""))
                            sText += "\n";

                        String curLineText = textData.getLineTexts().get(i);

                        curHeight = textData.getTextLineHeight(note, i + 1);

                        if (curHeight > maxHeight) {
                            if (t.getTextType().equals("1")) {

                                if (curLineText.length() > 27) // 가로로 대충 28자 정도
                                    // 들어감 ...을 넣으면
                                    // 넘어가니까 잘라준다.
                                    curLineText = curLineText.substring(0, 27);

                                curLineText += "...";
                            }

                            sText += curLineText;
                            break;
                        }

                        sText += curLineText;
                    }

                    note.text = sText;
                }
                break;
            }
        }
    }

    private boolean isSameComment(CommentData comment, ArrayList<CommentData> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (comment.id.equals(list.get(i).id)) return true;
        }
        return false;
    }


    boolean isWriteStoryTag(String storyTag) {

        if (storyTag.contains("명과 함께"))
            return true;

        if (storyTag.contains("에서") || storyTag.contains(" at "))
            return true;

        if (storyTag.contains("에 있습니다"))
            return true;

        if (storyTag.contains("추억을 공유했습니다"))
            return true;

        if (storyTag.contains("님과 함께"))
            return true;

        if (storyTag.contains("와 함께"))
            return true;

        return false;
    }
}