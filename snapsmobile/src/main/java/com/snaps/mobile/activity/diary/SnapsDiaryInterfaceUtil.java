package com.snaps.mobile.activity.diary;

import android.content.Context;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.net.CustomMultiPartEntity;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.activity.diary.json.SnapsDiaryBaseResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryCountJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryGsonUtil;
import com.snaps.mobile.activity.diary.json.SnapsDiaryImgUploadResultJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListItemJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryListJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryMissionStateJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryProfileThumbnailJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryUserMissionInfoJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPageInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryPublishItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 16. 3. 16..
 */
public class SnapsDiaryInterfaceUtil {
    private static final String TAG = SnapsDiaryInterfaceUtil.class.getSimpleName();
    public static final String INTERFACE_CODE_MISSION_STATE_START = "362001";
    public static final String INTERFACE_CODE_MISSION_STATE_SUCCESS = "362002";
    public static final String INTERFACE_CODE_MISSION_STATE_FAILED = "362003";
    public static final String INTERFACE_CODE_MISSION_STATE_RETRY = "362004";

    /**
     * 아래는 공통 코드 조회를 위한 URL로 실제 서비스에서 사용은 하지 않음. (개발 참고용)
     */

    public interface ISnapsDiaryInterfaceCallback {
        void onPreperation();
        void onResult(boolean result, Object resultObj);
    }

    public interface ISnapsDiaryListProcessListener {
        void onStartGetDiaryList();
        void onUpdateDiaryList(int totalCount, int complete);
        void onResultGetDiaryList(List<SnapsDiaryPublishItem> resultList, int failedDataIdx);
    }

    public static void requestUpdateUserProfileThumbnail(final Context context, final String filePath, final boolean isDelete, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryBaseResultJson resultObj;
            @Override
            public void onPre() {
                if(callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String message = GetMultiPartMethod.getDiaryUserProfileImageUplad(SnapsLoginManager.getUUserNo(context), filePath, isDelete, new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num, long total) {
                    }
                }, SnapsInterfaceLogDefaultHandler.createDefaultHandler());

                if (message != null) {
                    resultObj = SnapsDiaryGsonUtil.getParsedGsonData(message, SnapsDiaryImgUploadResultJson.class);
                    if (resultObj != null) {
                        return resultObj.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if(callback != null)
                    callback.onResult(result, resultObj);
            }
        });
    }

    public static void requestUserProfileThumbnail(final Context context, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryProfileThumbnailJson profileThumbnailJson;
            @Override
            public void onPre() {
                if(callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestGetProfileThumbnail(context);
                Dlog.d("requestUserProfileThumbnail() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryProfileThumbnailJson.class);
                    if (result != null) {
                        profileThumbnailJson = (SnapsDiaryProfileThumbnailJson) result;
                        return profileThumbnailJson.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if(callback != null)
                    callback.onResult(result, profileThumbnailJson);
            }
        });
    }

    public static void requestGetDiaryCountSameDate(final Context context, final String diaryNo, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryCountJson countResult = null;
            @Override
            public void onPre() {
                if(callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestGetDiaryCountSameDate(context, diaryNo);
                Dlog.d("requestGetDiaryCountSameDate() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryCountJson.class);
                    if (result != null) {
                        countResult = (SnapsDiaryCountJson) result;
                        return countResult.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if(callback != null)
                    callback.onResult(result, countResult);
            }
        });
    }

    public static void requestDiaryDelete(final Context context, final String diaryNo, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryBaseResultJson resultObj;

            @Override
            public void onPre() {
                if (callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestDiaryDelete(context, diaryNo);
                Dlog.d("requestDiaryDelete() result:" + resultJson);

                if (resultJson != null) {
                    resultObj = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryMissionStateJson.class);
                    if (resultObj != null) {
                        return resultObj.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result, resultObj);
            }
        });
    }

    /**
     *
     * @param context
     * @param missionState :SnapsDiaryConstants
     * @param missionNo
     * @param callback
     */
    public static void requestChangeMissionState(final Context context, final String missionState, final String missionNo, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryMissionStateJson missionResult;

            @Override
            public void onPre() {
            }

            @Override
            public boolean onBG() {
                String resultJson = requestSnapsDiaryChangeMissionState(context, missionState, missionNo);
                Dlog.d("requestChangeMissionState() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryMissionStateJson.class);
                    if (result != null) {
                        missionResult = (SnapsDiaryMissionStateJson) result;
                        return missionResult.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result, missionResult);
            }
        });
    }

    /**
     * 일기 리스트를 뿌여 주기 위한 용도로 사용.
     * @param context
     * @param pageInfo
     * @param callback
     */
    public static void getDiaryList(final Context context, final SnapsDiaryPageInfo pageInfo, final ISnapsDiaryInterfaceCallback callback) {
        getDiaryList(context, false, pageInfo, callback);
    }

    public static void getDiaryList(final Context context, final boolean isPublishList, final SnapsDiaryPageInfo pageInfo, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryListJson listResult;

            @Override
            public void onPre() {
                if (callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestDiaryList(context, pageInfo);
                Dlog.d("getDiaryList() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryListJson.class);
                    if (result != null) {
                        listResult = (SnapsDiaryListJson) result;

                        return listResult.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result, listResult);
            }
        });
    }

    private static void getCompletionDiaryList(final Context context, final List<SnapsDiaryPublishItem> arrResultList, final SnapsDiaryPageInfo pageInfo, final Set<String> setExclude, final ISnapsDiaryListProcessListener listener) {
        if(arrResultList == null) return;

        getDiaryList(context, true, pageInfo, new ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                if (resultObj == null) {
                    SnapsLogger.appendTextLog("SnapsDiaryInterfaceUtil/getCompletionDiaryList getDiaryList resultObj is null.");
                    if (listener != null) {
                        listener.onResultGetDiaryList(null, 0);
                    }
                    return;
                }

                SnapsDiaryListJson listResult = (SnapsDiaryListJson) resultObj;

                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                SnapsDiaryListInfo listInfo = dataManager.getPublishListInfo();

                listInfo.setCurrentPageNo(listResult.getPageNo());
                listInfo.setTotalCount(listResult.getTotalCount());
                listInfo.setPageSize(listResult.getPageSize());
                listInfo.setIosCount(listResult.getIosCount());
                listInfo.setAndroidCount(listResult.getAndroidCount());

                List<SnapsDiaryListItemJson> list = listResult.getDiaryList();
                if (list != null && !list.isEmpty()) {
                    listInfo.clearDiaryList();
                    listInfo.addDiaryList(list, setExclude);
                }

                if (result) {
                    setPublishDiaryList(context, arrResultList, pageInfo, setExclude, listener);
                } else {
                    SnapsLogger.appendTextLog("SnapsDiaryInterfaceUtil/getCompletionDiaryList getDiaryList result was failed.");
                    if (listener != null) {
                        listener.onResultGetDiaryList(null, listInfo.getArrDiaryList() != null ? listInfo.getArrDiaryList().size() : 0);
                    }
                }
            }
        });
    }

    private static void setPublishDiaryList(final Context context, final List<SnapsDiaryPublishItem> arrResultList, final SnapsDiaryPageInfo pageInfo, final Set<String> setExclude, final ISnapsDiaryListProcessListener listener) {
        if(arrResultList == null) {
            SnapsLogger.appendTextLog("setPublishDiaryList/arrResultList is null.");
            listener.onResultGetDiaryList(null, 0);
            return;
        }

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        final SnapsDiaryListInfo listInfo = dataManager.getPublishListInfo();
        final ArrayList<SnapsDiaryListItem> diaryList = listInfo.getArrDiaryList();

        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    SnapsDiaryPublishItem publishItem;

                    for (SnapsDiaryListItem item : diaryList) {
                        if (item == null) continue;

                        if (SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION) {
                            //FIXME
                        } else {
                            //IOS 일기가 호환되지 않으니 출판 제외.....
                            if (!SnapsDiaryConstants.isOSTypeEqualsAndroid(item.getOsType())) continue;
                        }

                        String url = item.getFilePath();
                        if (!url.startsWith(SnapsAPI.DOMAIN(false)))
                            url = SnapsAPI.DOMAIN(false) + url;

                        SnapsTemplate tempTemplate = GetTemplateLoad.getThemeBookTemplate(url, true, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                        ArrayList<MyPhotoSelectImageData> _galleryList = PhotobookCommonUtils.getImageListFromTemplate( tempTemplate );

                        //TODO  getTextListFromTemplate로 하면, 사용자가 작성한 들여쓰기가 적용되고 getDiaryPublishTextListFromTemplate는 출판 영역을 계산한 들여쓰기가 적용 된다.
                        String contents = PhotobookCommonUtils.getDiaryPublishTextListFromTemplate( tempTemplate );

                        publishItem = new SnapsDiaryPublishItem();
                        publishItem.setArrPhotoImageDatas(_galleryList);
                        publishItem.setContents(contents);
                        publishItem.setDate(item.getDate());
                        publishItem.setDiaryNo(item.getDiaryNo());
                        publishItem.setFeels(item.getFeels());
                        publishItem.setFilePath(item.getFilePath());
                        publishItem.setThumbnailUrl(item.getThumbnail());
                        publishItem.setWeather(item.getWeather());
                        publishItem.setOsType(item.getOsType());
                        publishItem.setTemplate( tempTemplate );
                        arrResultList.add(publishItem);

                        if (listener != null) {
                            listener.onUpdateDiaryList(listInfo.getTotalCount(), arrResultList.size());
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    SnapsLogger.sendLogException("SnapsDiaryInterfaceUtil/setPublishDiaryList", e);
                } finally {
                    if (!listInfo.isMoreNextPage()) {
                        if (listener != null) {
                            int resultCode = arrResultList.size() == listInfo.getTotalCount() ? -1 : arrResultList.size();

                            SnapsLogger.appendTextLog("SnapsDiaryInterfaceUtil/setPublishDiaryList resultCode : " + resultCode + ", arrResultList size : " + arrResultList.size());

                            listener.onResultGetDiaryList(arrResultList, resultCode);
                        }
                    }
                }
            }

            @Override
            public void onPost() {
                if (listInfo.isMoreNextPage()) {
                    pageInfo.setPagingNo(listInfo.getCurrentPageNo() + 1);
                    getCompletionDiaryList(context, arrResultList, pageInfo, setExclude, listener);
                }
            }
        });
    }

    /**
     * 상세 보기까지 완성된 리스트를 요청한다. (출판하기에서 사용)
     */
    public static void getCompletionDiaryList(final Context context, final SnapsDiaryPageInfo pageInfo, final Set<String> setExclude, final ISnapsDiaryListProcessListener listener) {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.setPublishListInfo(null);

        if(pageInfo != null) {
            pageInfo.setIsUsePaging(true);
            pageInfo.setPagingNo(1);
            pageInfo.setPagingSize(10); //한번에 다 로딩하면 부하가 있을 것으로 예상되어 10개씩 끊어서 요청 한다.
        }

        if(listener != null)
            listener.onStartGetDiaryList();

        List<SnapsDiaryPublishItem> arrResultList = new ArrayList<>();
        getCompletionDiaryList(context, arrResultList, pageInfo, setExclude, listener);
    }

    public static void getSingleDiaryItem(final Context context, final String diaryNo, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryListJson listResult;

            @Override
            public void onPre() {
                if (callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestDiaryItem(context, diaryNo);
                Dlog.d("getSingleDiaryItem() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryListJson.class);
                    if (result != null) {
                        listResult = (SnapsDiaryListJson) result;
                        return listResult.isSuccess();
                    }
                }

                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result, listResult);
            }
        });
    }

    public static void getUserMissionInfo(final Context context, final ISnapsDiaryInterfaceCallback callback) {
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
            SnapsDiaryUserMissionInfoJson missionResult;

            @Override
            public void onPre() {
                if (callback != null)
                    callback.onPreperation();
            }

            @Override
            public boolean onBG() {
                String resultJson = requestSnapsDiaryReadMissionInfo(context);
                Dlog.d("getUserMissionInfo() result:" + resultJson);

                if (resultJson != null) {
                    SnapsDiaryBaseResultJson result = SnapsDiaryGsonUtil.getParsedGsonData(resultJson, SnapsDiaryUserMissionInfoJson.class);
                    if (result != null) {
                        missionResult = (SnapsDiaryUserMissionInfoJson) result;
                        return missionResult.isSuccess();
                    }
                }
                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (callback != null)
                    callback.onResult(result, missionResult);
            }
        });
    }

    public static String getDiarySequence(Context context) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_SEQUENCE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestSnapsDiaryChangeMissionState(Context context, String stateCode, String missionNo) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("missionStat", stateCode));
        params.add(new BasicNameValuePair("missionNo", missionNo));
        if (SnapsDiaryConstants.IS_QA_VERSION) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            params.add(new BasicNameValuePair("firstSaveDate", writeInfo.getDateNumber()));
        }
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_CHANGE_MISSION_STATE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestSnapsDiaryReadMissionInfo(Context context) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_READ_MISSION_STATE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestDiaryList(Context context, SnapsDiaryPageInfo pageInfo) {
        if(pageInfo == null) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("isPaging", pageInfo.isUsePaging() ? "true" : ""));
        params.add(new BasicNameValuePair("pageNo", pageInfo.getPagingNoStr()));
        params.add(new BasicNameValuePair("pageSize", pageInfo.getPagingSizeStr()));
        params.add(new BasicNameValuePair("startDate", pageInfo.getStartDate()));
        params.add(new BasicNameValuePair("endDate", pageInfo.getEndDate()));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_LIST(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestDiaryItem(Context context, String diaryNo) {
        if(diaryNo == null) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("isPaging", ""));
        params.add(new BasicNameValuePair("pageNo", ""));
        params.add(new BasicNameValuePair("pageSize", ""));
        params.add(new BasicNameValuePair("startDate", ""));
        params.add(new BasicNameValuePair("endDate", ""));
        params.add(new BasicNameValuePair("diaryNo", diaryNo));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_LIST(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestDiaryDelete(Context context, String diaryNo) {
        if(diaryNo == null || diaryNo.length() < 1) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("diaryNo", diaryNo));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_DELETE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestGetProfileThumbnail(Context context) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_READ_USER_PROFILE_THUMBNAIL(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    public static String requestCheckMissionValid(Context context, String diaryNo) {
        if(diaryNo == null || diaryNo.length() < 1) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("diaryNo", diaryNo));
        params.add(new BasicNameValuePair("osType", SnapsDiaryConstants.CODE_OS_TYPE_ANDROID));
        if (SnapsDiaryConstants.IS_QA_VERSION) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            params.add(new BasicNameValuePair("firstSaveDate", writeInfo.getDateNumber()));
        }
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_CHECK_MISSION_VALID(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private static String requestGetDiaryCountSameDate(Context context, String diaryNo) {
        if(diaryNo == null || diaryNo.length() < 1) return null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userNo", SnapsLoginManager.getUUserNo(context)));
        params.add(new BasicNameValuePair("diaryNo", diaryNo));
        return HttpUtil.connectGet(SnapsAPI.GET_API_DIARY_COUNT_SAME_DATE(), params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }
}
