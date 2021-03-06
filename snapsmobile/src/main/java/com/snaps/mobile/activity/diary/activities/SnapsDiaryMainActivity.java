package com.snaps.mobile.activity.diary.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.SnapsDiaryInterfaceUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryListProcessor;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryDialog;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryTutorialView;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryLoadListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryTutorialListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.json.SnapsDiaryMissionStateJson;
import com.snaps.mobile.activity.diary.json.SnapsDiaryUserMissionInfoJson;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListInfo;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.list.ListActivity;
import com.snaps.mobile.activity.themebook.ImageEditActivity;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.utils.pref.PrefUtil;

import java.util.Calendar;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

import static com.snaps.mobile.activity.diary.SnapsDiaryConstants.DIARY_BOOK_F_CLSS_CODE;


/**
 * Created by ysjeong on 16. 3. 4..
 */
public class SnapsDiaryMainActivity extends CatchFragmentActivity implements GoHomeOpserver.OnGoHomeOpserver, ISnapsDiaryTutorialListener, ISnapsDiaryUploadOpserver, ISnapsDiaryLoadListener, View.OnClickListener {
    private static final String TAG = SnapsDiaryMainActivity.class.getSimpleName();

    public static final boolean IS_END_OF_SERVICE = true;  //?????? ????????? ?????? ?????? ?????????

    private final int HANDLER_MSG_LIST_CLEAR_AND_RELOAD = 1;
    private final int HANDLER_MSG_READ_MISSION_INFO = 2;

    private final int HANDLER_VALUE_TRUE = 100;

    private SnapsDiaryTutorialView mTutorialView = null;

    private SnapsDiaryListProcessor mListProcessor = null;

    private DialogDefaultProgress pageProgress;

    private SnapsShouldOverrideUrlLoader shouldOverrideUrlLoader = null;

    //?????? ????????? ?????? ??????
    private volatile boolean isEnableStartNewMission = true;
    private volatile boolean isWriteNewDiary = false;   //?????? flag

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.snaps_diary_main_activity_layout);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.registDiaryUploadObserver(this);

        GoHomeOpserver.addGoHomeListener(this);

        /** ????????? ???????????? ?????? ??? ???. ( ??????, ????????? ??????????????? ?????? ??????. )**/
        if (!Config.useKorean()) {
            finish();
            return;
        }

        init();

        //?????? ????????? ?????? ??????
        if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
            //NTPClient.checkEnableDiaryNewOrEdit();
            if (isShowNoticeDialog()) {
                NoticeDialog noticeDialog = new NoticeDialog(this);
                noticeDialog.show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isWriteNewDiary) {
            isWriteNewDiary = false;
            requestReadUserMissionInfo(false);
        }
    }

    /**
     * ????????? ?????? ??? ?????? ???.
     */
    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
        if (isNewWrite) {
            mHandler.sendEmptyMessageDelayed(HANDLER_MSG_LIST_CLEAR_AND_RELOAD, 50); //??? ?????? ?????????, ???????????? ????????? ?????? ?????? ?????? ??????.(??????????????????, onActivityResult?????? ????????????.)
        }
    }

    /**
     * ????????? ?????? ?????? ??? ?????? ???. (????????? ?????? ??? ????????? ???????????? ?????? ?????? ?????? ????????? ??? ???????????? UI??? ????????????.)
     */
    @Override
    public void onFinishDiaryListLoad() {
        //?????? OS(iOS)?????? ?????? ?????? ?????????, ????????? ????????? ??????.
        //checkOtherOSNotice(); <- ?????? ????????? ?????? ??????

        setUIByUserMissionState();
    }

    /**
     * ???????????? ??? ????????? ???
     */
    @Override
    public void onClosedTutorialView(int btnType) {
        switch (btnType) {
            case ISnapsDiaryTutorialListener.SNAPS_DIARY_TUTORIAL_BTN_01:
//                startDiaryTutorialWebPage();
                break;
            case ISnapsDiaryTutorialListener.SNAPS_DIARY_TUTORIAL_BTN_02:
                startWriteDiary();
                closeTutorial();
                break;
            case ISnapsDiaryTutorialListener.SNAPS_DIARY_CLOSE:
                closeTutorial();
                finish();
                break;
        }
    }

    public void getUserMissionInfo(boolean isFirstLoad) {
        //???????????? ?????? ????????? ?????? ???.
        Message msg = new Message();
        msg.what = HANDLER_MSG_READ_MISSION_INFO;
        msg.arg1 = isFirstLoad ? HANDLER_VALUE_TRUE : 0;
        mHandler.sendMessageDelayed(msg, 50);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ThemeTitleLeftLy || v.getId() == R.id.ThemeTitleLeft) {
            if (mListProcessor != null && mListProcessor.isShownPopMenu()) {
                mListProcessor.closePopMenu();
            } else
                onBackPressed();
        }
//        else if (v.getId() == R.id.ThemecartBtnLy || v.getId() == R.id.ThemecartBtn) {
//            startDiaryTutorialWebPage();
//        }
        else if (v.getId() == R.id.snaps_diary_write_diary_btn || v.getId() == R.id.snaps_diary_empty_btn_ly) {
            //?????? ????????? ?????? ??????
            if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
                /*
                if (!isEnableStartNewMission || !NTPClient.isEnableDiaryNewOrEdit()) {
                    MessageUtil.alertnoTitleOneBtn(this, "?????? ???????????? ????????????\n?????? ????????? ????????? ??? ????????????.", null);
                    return;
                }
                */
                if (!isEnableStartNewMission) {
                    MessageUtil.alertnoTitleOneBtn(this, "?????? ???????????? ????????????\n?????? ????????? ????????? ??? ????????????.", null);
                    return;
                }
            }
            startWriteDiary();
        } else if (v.getId() == R.id.snaps_diary_publish_diary_btn) {
            startPublishDiary();
        } else if (v.getId() == R.id.snaps_diary_main_act_hamburger_menu_btn) {
            SnapsMenuManager.showHamburgerMenu(this, SnapsMenuManager.eHAMBURGER_ACTIVITY.DIARY);
        }

    }

    @Override
    public void onGoHome() {
        finish();
    }

    private void startDiaryTutorialWebPage() {
        if (shouldOverrideUrlLoader != null)
            shouldOverrideUrlLoader.shouldOverrideUrlLoading(SnapsAPI.DIARY_HELP_PAGE_URL());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Config.cleanProductInfo();

        SnapsTimerProgressView.destroyProgressView();
        try {
            if (pageProgress != null)
                pageProgress.dismiss();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        SnapsDiaryDataManager.finalizeInstance();

        DataTransManager.releaseInstance();

        ImageSelectManager.finalizeInstance();

        if (mListProcessor != null)
            mListProcessor.destroyView();

        GoHomeOpserver.removeGoHomeListenrer(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.setIsWritingDiary(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void showProgress() {
        if (!isFinishing() && pageProgress != null)
            pageProgress.show();
    }

    public void hideProgress() {
        if (!isFinishing() && pageProgress != null && pageProgress.isShowing())
            pageProgress.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case SnapsDiaryConstants.REQUEST_CODE_DIARY_UPDATE: //?????? ???????????????, ???????????? ???
                if (resultCode == SnapsDiaryConstants.RESULT_CODE_DIARY_DELETED) {
                    getUserMissionInfo(false); //????????? ?????????, ?????? ????????? ?????????, ?????? ?????? ????????? ????????????. ?????? ?????? ????????? ?????????, ????????? ????????? ????????? ??????.
                } else if (resultCode == SnapsDiaryConstants.RESULT_CODE_DIARY_UPDATED) {
                    boolean isDiaryDateUpdated = data != null && data.getBooleanExtra(SnapsDiaryConstants.EXTRAS_BOOLEAN_EDITED_DATE, false);
                    if (isDiaryDateUpdated) { //????????? ???????????????, ???????????? ?????? ????????????.
                        mHandler.sendEmptyMessageDelayed(HANDLER_MSG_LIST_CLEAR_AND_RELOAD, 50);
                    } else {
                        if (mListProcessor != null) {
                            mListProcessor.requestDiaryListRefresh(); //?????? ?????? ?????? ??????, ?????? ?????? ????????? ?????? ????????? ????????? ??????.
                        }
                    }
                }
                break;

            case SnapsDiaryConstants.REQUEST_CODE_SELECT_ONE_PHOTO: //???????????? ???????????? ???????????? ?????? ??????????????? ????????? ????????? ??????
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    b.setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
                    MyPhotoSelectImageData d = (MyPhotoSelectImageData) b.getSerializable("imgData");
                    //?????? ???????????? ??????.
                    requestEditThumbnail(d);
                }
                break;
            case SnapsDiaryConstants.REQUEST_CODE_EDIT_PHOTO: //???????????? ????????? ????????? ????????? ??? ????????? ?????? ???.
                if (resultCode == RESULT_OK) {
                    data.getExtras().setClassLoader(MyPhotoSelectImageData.class.getClassLoader());
                    final MyPhotoSelectImageData cropData = (MyPhotoSelectImageData) data.getSerializableExtra("single_img_data");

                    if (cropData != null) {
                        SnapsDiaryInterfaceUtil.requestUpdateUserProfileThumbnail(this, cropData.PATH, false, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
                            @Override
                            public void onPreperation() {
                                SnapsTimerProgressView.showProgress(SnapsDiaryMainActivity.this,
                                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
                                        SnapsDiaryMainActivity.this.getString(R.string.diary_profile_updating_msg));
                            }

                            @Override
                            public void onResult(boolean result, Object resultObj) {
                                SnapsTimerProgressView.destroyProgressView();

                                if (result) {
                                    if (mListProcessor != null)
                                        mListProcessor.requestGetUserProfileThumbnail(); //????????? ??????
                                } else {
                                    MessageUtil.alertnoTitleOneBtn(SnapsDiaryMainActivity.this, getString(R.string.network_error_message_please_wait), null);
                                }
                            }
                        });
                    }
                }
                break;
        }
    }

    private void init() {
        if (findViewById(R.id.ThemeTitleLeftLy) != null)
            findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(this);
        if (findViewById(R.id.ThemeTitleLeft) != null)
            findViewById(R.id.ThemeTitleLeft).setOnClickListener(this);

        TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
        themeTitle.setText(R.string.snaps_diary);

        findViewById(R.id.snaps_diary_write_diary_btn).setOnClickListener(this);
        findViewById(R.id.snaps_diary_publish_diary_btn).setOnClickListener(this);

//        findViewById(R.id.ThemecartBtnLy).setOnClickListener(this);
//        findViewById(R.id.ThemecartBtn).setOnClickListener(this);
        findViewById(R.id.snaps_diary_main_act_hamburger_menu_btn).setOnClickListener(this);

        mListProcessor = new SnapsDiaryListProcessor(SnapsDiaryMainActivity.this);
        pageProgress = new DialogDefaultProgress(this);

        shouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader(this, SnapsShouldOverrideUrlLoader.NATIVE);

        getUserMissionInfo(true);
    }

    /**
     * ????????? ????????? ????????? ?????? ???, ???????????? ?????? ??????
     */
    private void checkListEmptyUI() {
        RelativeLayout lyEmpty = (RelativeLayout) findViewById(R.id.snaps_diary_empty_list_ly);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        if (listInfo.isEmptyDiaryList()) {
            lyEmpty.setVisibility(View.VISIBLE);
            findViewById(R.id.snaps_diary_empty_btn_ly).setOnClickListener(this);
        } else {
            lyEmpty.setVisibility(View.GONE);
        }
    }

    private void showTutorial() {
        if (Config.useKorean()) {
            mTutorialView = new SnapsDiaryTutorialView(this, this);
            addContentView(mTutorialView, new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    private void closeTutorial() {
        try {
            if (mTutorialView != null) {
                ((ViewGroup) mTutorialView.getParent()).removeView(mTutorialView);
                mTutorialView = null;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void requestEditThumbnail(MyPhotoSelectImageData imgData) {
        if (imgData == null) return;

        Intent intent = new Intent(getApplicationContext(), ImageEditActivity.class);

        imgData.cropRatio = 1;
        Bundle bundle = new Bundle();
        bundle.putSerializable("single_img_data", imgData);
        bundle.putBoolean("single_img_edit", true);
        bundle.putBoolean("diary_profile", true);
        intent.putExtras(bundle);

        startActivityForResult(intent, SnapsDiaryConstants.REQUEST_CODE_EDIT_PHOTO);
    }

    private void startWriteDiary() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.setWriteInfo(null);

        isWriteNewDiary = true;
        Intent ittWrite = new Intent(this, SnapsDiarySelectDateWeatherFeelActivity.class);
        startActivity(ittWrite);
    }

    private void startPublishDiary() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryListInfo listInfo = dataManager.getListInfo();
        dataManager.setIsWritingDiary(false);

        if (listInfo.getAndroidCount() >= SnapsDiaryConstants.MIN_DIARY_PAGE_COUNT_FOR_PUBLISH) {

            MenuDataManager menuDataManager = MenuDataManager.getInstance();
            SubCategory subCategory = null;
            if (menuDataManager != null) {
                subCategory = menuDataManager.getSubCategoryByF_CLSS_CODE(DIARY_BOOK_F_CLSS_CODE);

                SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
                if (menuMan != null) {
                    menuMan.setSubCategory(subCategory);
                }
            }

            String titleStr = getString(R.string.select_design);

            SnapsProductListParams listParams = new SnapsProductListParams();
            listParams.setClssCode(DIARY_BOOK_F_CLSS_CODE);

            Intent intent = ListActivity.getIntent(this, titleStr, false, listParams);
            intent.putExtra("fromHomeActivity", false);
            startActivity(intent);

        } else {
            String msg = null;
            if (listInfo.getTotalCount() >= SnapsDiaryConstants.MIN_DIARY_PAGE_COUNT_FOR_PUBLISH) {
                msg = String.format(getString(R.string.diary_not_enough_page_for_publish_ios), listInfo.getAndroidCount(), getString(R.string.ios_eng));
            } else {
                msg = String.format(getString(R.string.diary_not_enough_page_for_publish), listInfo.getAndroidCount());
            }
            SnapsDiaryDialog.showDialogOneBtn(SnapsDiaryMainActivity.this, msg, "", null);
        }
    }

    //???????????? ?????? ????????? ?????? ???.
    private void requestReadUserMissionInfo(final boolean IS_FIRST_LOAD) {
        SnapsDiaryInterfaceUtil.getUserMissionInfo(this, new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {

            @Override
            public void onPreperation() {
                showProgress();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                hideProgress();
                if (result && resultObj != null) {
                    SnapsDiaryUserMissionInfoJson missionResult = (SnapsDiaryUserMissionInfoJson) resultObj;

                    SnapsDiaryUserInfo userInfo = new SnapsDiaryUserInfo();
                    userInfo.set(missionResult);

                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryUserInfo prevUserInfo = dataManager.getSnapsDiaryUserInfo();

                    //???????????? ?????? ?????? ??????.
                    if (prevUserInfo != null) {
                        userInfo.setThumbnailCache(prevUserInfo.getThumbnailCache());
                        userInfo.setThumbnailPath(prevUserInfo.getThumbnailPath());
                    }

                    dataManager.setSnapsDiaryUserInfo(userInfo);

                    //////////////////////////////////////////
                    //?????? ????????? ?????? ??????
                    if (SnapsDiaryMainActivity.IS_END_OF_SERVICE) {
                        if (userInfo.getMissionStat() == null || !userInfo.getMissionStat().equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_ING)) {
                            //?????? ???????????? ?????? ??????
                            isEnableStartNewMission = false;
                        }
                        if (userInfo.checkPassedMissionPeriod() || userInfo.checkAlreadyMissionCompleted()) {
                            //?????? ?????? ????????? ????????????..(?????? ????????? ??????) ?????? ?????? ???????????? ?????? ??????
                            isEnableStartNewMission = false;
                        }
                    }
                    //////////////////////////////////////////

                    //?????? ????????? ????????? ??? ??????
                    if (userInfo.checkPassedMissionPeriod()) {
                        requestChangeMissionState(userInfo, SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_FAILED);
                    } else if (userInfo.checkAlreadyMissionCompleted()) { //????????? ??????????????? ?????????, ???????????? ?????? ????????? ??? ??? ??????
                        requestChangeMissionState(userInfo, SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_SUCCESS);
                    } else {
                        if (IS_FIRST_LOAD) {
                            initializeDiaryList();
                        } else {
                            if (mListProcessor != null)
                                mListProcessor.requestDiaryListRefresh();

                            setUIByUserMissionState();
                        }
                    }
                } else {
                    MessageUtil.alert(SnapsDiaryMainActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                requestReadUserMissionInfo(IS_FIRST_LOAD);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void requestChangeMissionState(SnapsDiaryUserInfo userInfo, final String missionStateCode) {
        SnapsDiaryInterfaceUtil.requestChangeMissionState(this, missionStateCode, userInfo.getMissionNo(), new SnapsDiaryInterfaceUtil.ISnapsDiaryInterfaceCallback() {
            @Override
            public void onPreperation() {
                showProgress();
            }

            @Override
            public void onResult(boolean result, Object resultObj) {
                hideProgress();
                if (result) {
                    SnapsDiaryMissionStateJson missionResult = (SnapsDiaryMissionStateJson) resultObj;
                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
                    if (userInfo != null) {
                        String responseMissionNo = missionResult.getMissionNo();
                        if (responseMissionNo != null && responseMissionNo.length() > 0)
                            userInfo.setMissionNo(responseMissionNo);
                        userInfo.setMissionStat(missionStateCode);

                        //?????? ????????? ?????? ??????
                        if (missionStateCode.equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_FAILED) ||
                                missionStateCode.equals(SnapsDiaryConstants.INTERFACE_CODE_MISSION_STATE_SUCCESS))
                        {
                            isEnableStartNewMission = false;
                        }

                    }
                }

                initializeDiaryList(); //?????? ??? ????????? ???????????? ????????? ??? ?????????, ???????????? ??????????????? ?????? ???????????? ???????????? ?????? ??????.
            }
        });
    }

    private void initializeDiaryList() {
        if (mListProcessor == null)
            mListProcessor = new SnapsDiaryListProcessor(SnapsDiaryMainActivity.this);

        mListProcessor.initDiaryList(); //????????? ?????????

        mListProcessor.requestGetUserProfileThumbnail(); //????????? ??????
    }

    public void setUIByUserMissionState() {
        switch (getCurrentUserMissionState()) {
            case PREV:
                //showTutorial();   //2019??? 6??? 27??? ?????? - renewal ?????? ?????? Tutorial ??????????????? ?????? ?????? ??????
                break;
            case ING:
                break;
            case SUCCESS:
                break;
            case FAILED:
                break;
            case UNKNOWN: //ERROR UI ????????? ?????? ??????~?
                MessageUtil.alert(SnapsDiaryMainActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        if (clickedOk == ICustomDialogListener.OK) {
                            requestReadUserMissionInfo(false);
                        } else {
                            finish();
                        }
                    }
                });
                return;
        }

        if (mListProcessor != null)
            mListProcessor.requestAdapterRefresh();

        //????????? ????????? ????????? ?????????..
        checkListEmptyUI();
    }

    private SnapsDiaryConstants.eMissionState getCurrentUserMissionState() {
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        SnapsDiaryConstants.eMissionState state = SnapsDiaryConstants.eMissionState.UNKNOWN;
        if (userInfo != null)
            state = userInfo.getMissionStateEnum();
        return state;
    }

    /**
     * iOS ?????? ?????? ?????? ?????????, ?????? ????????? ?????? ???.
     */
    private void checkOtherOSNotice() {
        if (!PrefUtil.isNeedCheckDiaryOtherOsNoticeAlert(this)) return;

        switch (getCurrentUserMissionState()) {
            case ING:
            case SUCCESS:
            case FAILED:
                SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                SnapsDiaryListInfo listInfo = dataManager.getListInfo();
                if (listInfo != null && listInfo.isExistOtherOsContents()) {
                    showOtherOSNotice();
                }
                break;
        }
    }

    /**
     * IOS?????? ?????? ??? ?????? ?????? ?????? ?????? ??????..
     */
    private void showOtherOSNotice() {
        //1?????? ?????????.
        PrefUtil.showDiaryOtherOsNoticeAlert(this);

        SnapsDiaryDialog.showDialogIosContentsNotice(this);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_LIST_CLEAR_AND_RELOAD:
                    if (mListProcessor != null)
                        mListProcessor.clearAndReloadDiaryList();
                    break;
                case HANDLER_MSG_READ_MISSION_INFO:
                    boolean isFirstLoad = msg.arg1 == HANDLER_VALUE_TRUE;
                    requestReadUserMissionInfo(isFirstLoad);
                    break;
            }
            return false;
        }
    });

    private boolean isShowNoticeDialog() {
        String saveDate = Setting.getString(this, NoticeDialog.KEY_NOT_AGAIN_TODAY, "");
        if (saveDate.length() == 0) return true;
        if (saveDate.equals(getTodayText())) return false;
        return true;
    }

    private static String getTodayText() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return "" + year + mon + day;
    }

    //?????? ????????? ?????? ??????
    public static class NoticeDialog extends Dialog {
        //public static final String KEY_NOT_AGAIN = "DIARY_END_OF_SERVICE_181818";
        public static final String KEY_NOT_AGAIN_TODAY = "DIARY_END_OF_SERVICE_TODAY_181818";
        public static volatile Bitmap sBitmap = null;

        public NoticeDialog(Context context) {
            super(context, R.style.TransparentProgressDialog);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.diary_end_of_service_dialog);

            if (sBitmap != null) {
                ImageView imageView = findViewById(R.id.imageView_popup_img);
                imageView.setImageBitmap(sBitmap);
            }

            LinearLayout btn_notAgain = findViewById(R.id.btn_not_again);
            ImageView checkBox_notAgin = findViewById(R.id.checkBox_not_again);

            btn_notAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox_notAgin.setSelected(!checkBox_notAgin.isSelected());
                    if(checkBox_notAgin.isSelected()) {
                        Setting.set(getContext(), KEY_NOT_AGAIN_TODAY, getTodayText());
                    } else {
                        Setting.set(getContext(), KEY_NOT_AGAIN_TODAY, "");
                    }
                }
            });

            LinearLayout linearLayoutConfirm = findViewById(R.id.btn_confim);
            linearLayoutConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });

            setCanceledOnTouchOutside(false);
        }
    }

    //?????? ????????? ?????? ??????
    /*
    public static void downloadNoticeDialogImg(Context context) {
        if (Setting.getBoolean(context, NoticeDialog.KEY_NOT_AGAIN, false)) return;

        String fileName = "diary_end_of_service_popup_img";
        float density = context.getResources().getDisplayMetrics().density;
        // https://stackoverflow.com/questions/5099550/how-to-check-an-android-device-is-hdpi-screen-or-mdpi-screen
        if (density < 1.5) {
            fileName += "_M.png";
        } else if (density < 2.0) {
            fileName += "_H.png";
        } else if (density < 3.0) {
            fileName += "_XH.png";
        } else if (density < 4.0) {
            fileName += "_XXH.png";
        } else {
            fileName += "_XXXH.png";
        }

        String url = "https://www.snaps.com/Upload/Data1/mobile/cs/diary_android/" + fileName;

        try {
            RequestOptions options = new RequestOptions().skipMemoryCache(true);
            Glide.with(context).asBitmap().load(url).apply(options).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    NoticeDialog.sBitmap = resource;
                }
            });
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
     */
}
