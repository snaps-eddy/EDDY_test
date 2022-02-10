package com.snaps.mobile.activity.common;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.data.img.BRect;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsProductInfoManager;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.OrientationSensorManager;
import com.snaps.common.utils.ui.SnapsViewVisibilityByScrollHandler;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SmartRecommendBookHandlerInstance;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.handler.SmartRecommendBookAssistantControlHandler;
import com.snaps.mobile.activity.common.handler.SmartRecommendBookCaptureHandler;
import com.snaps.mobile.activity.common.handler.SmartRecommendBookTemplateHandler;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SnapsCustomLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.SmartRecommendBookPageEditActivity;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.activity.themebook.adapter.SmartRecommendBookMainListAdapter;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom.SmartRecommendBookMainListItemPinchZoomDrawer;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.custom.SmartRecommendBookMainListItemPinchZoomLayout;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies.ISmartRecommendBookCoverChangeListener;
import com.snaps.mobile.edit_activity_tools.EditActivityPreviewActivity;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.ISnapsOrderStateListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultHandleData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.GifTutorialView;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;

import java.util.ArrayList;
import java.util.List;

import errorhandle.CatchFragmentActivity;
import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.REQUEST_CODE_PAGE_EDIT;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.RESULT_CODE_EDITED;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_ACTIVE_ROTATION_SENSOR;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_PAGE_INDEX;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.EXTRAS_KEY_SCREEN_ORIENTATION_ACT_INFO;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_CHANGE_TITLE_TEXT;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_SHOW_TUTORIAL_FOR_SMART_RECOMMEND_BOOK_COVER;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.HANDLER_MSG_UNLOCK_ORIENTATION_SENSOR;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.MIN_PAGE_COUNT_OF_PHOTO_BOOK;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_PREVIEW;
import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.RESULT_CODE_EDIT;

public class SmartRecommendBookMainActivity extends CatchFragmentActivity
        implements View.OnClickListener, SnapsEditActExternalConnectionBridge,
        SnapsOrderActivityBridge, ISnapsOrderStateListener, SnapsImageUploadStateListener, ISnapsHandler, GoHomeOpserver.OnGoHomeOpserver,
        SmartRecommendBookMainListItemPinchZoomLayout.IPinchZoomLayoutBridge, OrientationSensorManager.OrientationChangeListener
{
    private static final String TAG = SmartRecommendBookMainActivity.class.getSimpleName();

    private SmartRecommendBookTemplateHandler templateHandler = null;
    private SmartRecommendBookCaptureHandler captureHandler = null;
    private SmartRecommendBookAssistantControlHandler assistantControlHandler = null;
    private SnapsViewVisibilityByScrollHandler bottomViewHandler = null;

    private SmartRecommendBookMainListItemPinchZoomLayout pinchZoomLayout;
    private SmartRecommendBookMainListAdapter listAdapter = null;
    private SnapsRecyclerView recyclerView = null;

    private SnapsProductEditInfo snapsProductEditInfo;

    private SnapsPageEditRequestInfo editRequestInfo;

    private SnapsHandler snapsHandler = null;

    private SnapsPage currentCoverPage = null;
    private boolean shouldCheckCoverTutorial = false;
    private boolean shouldBeComeBackToPreviewActivity = false; //미리보기 화면에서 편집 화면에 진입 했다면, 편집 종료 후 다시 미리 보기 화면으로 돌아간다.
    private boolean performPreviewByOrientationSensor = false; // 미리보기로 이동할때, 화면 방향 센서를 사용할 것인지.

    private String mFaceDetectionInfo = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        if (Config.getAI_IS_RECOMMENDAI())
            mFaceDetectionInfo = getIntent().getStringExtra("faceInfo");

        setContentView(R.layout.smart_snaps_analysis_product_edit_activity);

        initialize();

        if (checkPermissionGranted()) {
            loadTemplate();
        }
    }

    public String getFaceDetectionInfo() {
        return mFaceDetectionInfo;
    }

    private void loadTemplate() {
        SnapsAssert.assertNotNull(templateHandler);
        templateHandler.loadTemplate(new SmartRecommendBookTemplateHandler.SmartRecommendBookTemplateHandleResultListener() {
            @Override
            public void onLoadedTemplate() {
                loadListOnLoadTemplate();
            }

            @Override
            public void onFailedGetTemplate() {
                handleOnFailedGetTemplate();
            }
        });
    }

    private void loadListOnLoadTemplate() {
        SnapsAssert.assertNotNull(captureHandler);
        captureHandler.createCanvasFragment();

        SnapsAssert.assertNotNull(listAdapter);
        listAdapter.setListWithTemplate(getTemplate());

        startImageUploadOnBackground();

        showPinchZoomTutorial();
    }

    public void handleOnFailedGetTemplate() {
        Activity activity = getActivity();
        if (activity == null) return;
        MessageUtil.alertnoTitleOneBtn(activity, activity.getString(R.string.kakao_book_make_err_template_download), new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finishAnalysisProductActivity();
            }
        });
    }

    private void initialize() {
        initBaseConfig();

        initOrientationSensor();

        initProjectBaseInfo();

        initHandlers();

        initControls();

        applyTitleProjectName();

        initListView();
    }

    private void initOrientationSensor() {
        OrientationSensorManager orientationSensorManager = OrientationSensorManager.getInstance();
        orientationSensorManager.init(this, this);
    }

    @Override
    public void onOrientationChanged(int orientation) {
//		if (!hasWindowFocus() || Settings.System.getInt(getActivity().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
//			return;
//		}
        if (!hasWindowFocus() || OrientationSensorManager.isActiveAutoRotation(getActivity()) == false) {
            return;
        }

        OrientationSensorManager sensorManager = OrientationSensorManager.getInstance();
        if (!sensorManager.isAllowOrientationChangeTime()) return;

        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_widthPreview)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

                performPreviewByOrientationSensor = true;
                performPreview(getCurrentVisibleItemPosition(), orientation);

                sensorManager.updateLastOrientationChangeTime();
                break;
        }


        sensorManager.setLastScreenOrientation(orientation);
    }

    private void initListView() {
        recyclerView = (SnapsRecyclerView) findViewById(R.id.smart_snaps_analysis_product_edit_activity_recycler_view);

        final SnapsCustomLinearLayoutManager linearLayoutManager = new SnapsCustomLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        listAdapter = new SmartRecommendBookMainListAdapter(this);

        listAdapter.setCoverChangeListener(new ISmartRecommendBookCoverChangeListener() {
            @Override
            public void onCoverChanged(SnapsPage coverPage) {
                dismissToolTip();

                changeCoverPage(coverPage);
            }
        });
        listAdapter.setItemClickListener(new SnapsCommonResultListener<SnapsPageEditRequestInfo>() {
            @Override
            public void onResult(SnapsPageEditRequestInfo editRequestInfo) {
                onListItemClicked(editRequestInfo);
            }
        });
        listAdapter.setItemLongClickListener(new SnapsCommonResultListener<SnapsPageEditRequestInfo>() {
            @Override
            public void onResult(SnapsPageEditRequestInfo editRequestInfo) {
                onListItemLongClicked(editRequestInfo);
            }
        });

        recyclerView.setAdapter(listAdapter);

        View bottomView = findViewById(R.id.smart_snaps_analysis_product_edit_activity_bottom_layout);

        //생명주기..
        bottomViewHandler = SnapsViewVisibilityByScrollHandler.createHandler(this, recyclerView, bottomView);
        bottomViewHandler.start();
    }

    private boolean isListItemZoomMode() {
        return pinchZoomLayout != null && pinchZoomLayout.isZoomMode();
    }

    private void stopListItemZoomMode() {
        if (pinchZoomLayout == null) return;
        pinchZoomLayout.stopListItemZoomMode();
    }

    @Override
    public void forceListItemClick(int position) {
        if (position < 0 || getPageList() == null || position >= getPageList().size() || listAdapter == null)
            return;
        listAdapter.forceListItemClick(position);
    }

    @Override
    public void onTouchDown() {
        if (shouldCheckCoverTutorial) {
            dismissToolTip();
        }
    }

    private void onListItemClicked(SnapsPageEditRequestInfo editRequestInfo) {
        if (editRequestInfo == null || isListItemZoomMode()) return;
        //취소 했을 때를 대비 해서 이 시점에 데이터를 저장 해 놓는다.
        dismissToolTip();

        SmartSnapsUtil.saveSmartRecommendBookCurrentEditInfo(getGalleryList(), getPageList());

        startPageEditActivityForResult(editRequestInfo);
    }

    private void onListItemLongClicked(SnapsPageEditRequestInfo editRequestInfo) {
        if (isListItemZoomMode()) return;

        this.editRequestInfo = editRequestInfo;
        if (editRequestInfo == null) return;

        dismissToolTip();

        smoothScrollToPosition(editRequestInfo.getPageIndex());

        if (recyclerView != null) {
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLongClickedListItemTooltip();
                }
            }, 100);
        }
    }

    private void showLongClickedListItemTooltip() {
        if (editRequestInfo == null) return;
        int pageIndex = editRequestInfo.getPageIndex();
        SnapsLayoutControl coverLayoutControl = PhotobookCommonUtils.findLayoutControlWithPageList(getPageList(), pageIndex);
        if (coverLayoutControl != null) {
            View targetView = findViewById(coverLayoutControl.getControlId());
            if (targetView != null) {
                handleShowPopMenuPhotoTooltip((View) targetView.getParent().getParent());
            }
        }
    }

    void handleShowPopMenuPhotoTooltip(View popupView) {
        if (isListItemZoomMode()) return;

        if (getPopupMenuView() != null && getPopupMenuView().isShown()) return;

        Rect rect = new Rect();
        ViewGroup rootView = findViewById(R.id.smart_snaps_analysis_product_edit_activity_root_layout);
        if (rootView == null) return;

        if (popupView != null)
            popupView.getGlobalVisibleRect(rect);

        int popWidth = UIUtil.convertDPtoPX(getActivity(), 100);
        int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);

        int tooltipLayoutResId = R.layout.popmenu_photo_exclude_change_photo;
        if (editRequestInfo.getPageIndex() < 2) {
            tooltipLayoutResId = R.layout.popmenu_photo_only_edit;
        }

        assistantControlHandler.setPopupMenuView(new PopoverView(getActivity(), tooltipLayoutResId));

        getPopupMenuView().setContentSizeForViewInPopover(new Point(popWidth, popHeight));
        DataTransManager transMan = DataTransManager.getInstance();
        if (transMan != null) {
            ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
            if (coordInfo != null) {
                coordInfo.convertPopupOverRect(rect, popupView, rootView, false);

                rect.top += UIUtil.convertDPtoPX(getActivity(), 108); //페이지 아랫쪽에 위치하게 하기 위해...
                rect.bottom += UIUtil.convertDPtoPX(getActivity(), 108);

                getPopupMenuView().setArrowPosition(rect, coordInfo.getTranslateX(), coordInfo.getScaleFactor(), coordInfo.getDefualtScaleFactor(), false);
            }
        } else {
            DataTransManager.notifyAppFinish(getActivity());
            return;
        }

        getPopupMenuView().showPopoverFromRectInViewGroup(rootView, rect, PopoverView.PopoverArrowDirectionUp, true);
    }

    private void showDeletePageConfirm(final SnapsPageEditRequestInfo editRequestInfo) {
        if (editRequestInfo == null) return;

        dismissToolTip();

        if (isLackMinPageCount()) {
            MessageUtil.toast(getActivity(), getString(R.string.failed_delete_page_cause_min_page_msg));
        } else {
            if (getConfirmDialog() == null || !getConfirmDialog().isShowing()) {
                assistantControlHandler.setConfirmDialog(new CustomizeDialog(getActivity(), getString(R.string.page_delete_with_warning_msg), new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        if (clickedOk == ICustomDialogListener.OK) {
                            deletePage(editRequestInfo.getPageIndex());
                        }
                    }
                }, null));

                getConfirmDialog().show();
            }
        }
    }

    private void deletePage(final int index) {
        if (getPageList() == null || getPageList().size() <= index)
            return;

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editpopup_updateDelete)
                .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_CNT, String.valueOf(PhotobookCommonUtils.getImgCntInPage((editRequestInfo != null ? editRequestInfo.getPageIndex() : -1), getPageList())))
                .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf((editRequestInfo != null ? editRequestInfo.getPageIndex() : -1)))
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

        // 삭제전에 이미지 데이터가 있으면 삭제하기..
        SnapsPage page = getPageList().get(index);

        SnapsOrderManager.removeBackgroundUploadOrgImagesInPage(page);

        // 페이지 삭제
        getPageList().remove(page);

        PhotobookCommonUtils.sortPagesIndex(getPageList(), index);

        // maxpage 설정...
        getTemplate().setApplyMaxPage();

        refreshList();

        MessageUtil.toast(this, getString(R.string.delete_complete));
    }

    private boolean isLackMinPageCount() {
        return PhotobookCommonUtils.convertPageSizeToPageCount(getPageList().size()) <= MIN_PAGE_COUNT_OF_PHOTO_BOOK;
    }

    @Override
    public SnapsPageCanvas findSnapsPageCanvasWithTouchOffsetRect(BRect bRect) {
        return listAdapter != null ? listAdapter.findSnapsPageCanvasWithTouchOffsetRect(bRect) : null;
    }

    @Override
    public void onGoHome() {
        completeFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoHomeOpserver.removeGoHomeListenrer(this);

        Config.setAliveRecommendBookActivity(false);

        OrientationSensorManager.finalizeInstance();

        if (bottomViewHandler != null) bottomViewHandler.stop();
    }

    private void dismissTutorialTooltip() {
        if (!shouldCheckCoverTutorial) return;
        shouldCheckCoverTutorial = false;

        SnapsTutorialUtil.clearTooltip();
    }

    private void dismissToolTip() {
        if (getPopupMenuView() != null)
            getPopupMenuView().dissmissPopover(false);

        dismissTutorialTooltip();
    }

    private void initBaseConfig() {
        FileUtil.initProjectFileSaveStorage();
        Config.setIS_MAKE_RUNNING(true);

        Config.setAliveRecommendBookActivity(true);

        GoHomeOpserver.addGoHomeListener(this);
    }

    private void initControls() {
        snapsHandler = new SnapsHandler(this);

        assistantControlHandler.setDefaultProgress(new DialogDefaultProgress(this));

        pinchZoomLayout = findViewById(R.id.smart_snaps_analysis_product_edit_activity_main_list_item_pinch_zoom_layout);

        View fullScreenBtn = findViewById(R.id.smart_snaps_analysis_product_edit_activity_full_screen_btn);
        if (fullScreenBtn != null) fullScreenBtn.setOnClickListener(this);

        View scrollTopBtn = findViewById(R.id.smart_snaps_analysis_product_edit_activity_scroll_top_btn);
        if (scrollTopBtn != null) scrollTopBtn.setOnClickListener(this);

        SmartRecommendBookMainListItemPinchZoomDrawer.PinchZoomDrawerAttribute pinchZoomDrawerAttribute = new SmartRecommendBookMainListItemPinchZoomDrawer.PinchZoomDrawerAttribute.Builder()
                .setBottomLayout(findViewById(R.id.smart_snaps_analysis_product_edit_activity_bottom_layout))
                .setFullScreenBtn(fullScreenBtn)
                .setScrollToTopBtn(scrollTopBtn).create();

        pinchZoomLayout.setPinchZoomDrawer((SmartRecommendBookMainListItemPinchZoomDrawer) findViewById(R.id.smart_snaps_analysis_product_edit_activity_pinch_zoom_drawer),
                pinchZoomDrawerAttribute);

        pinchZoomLayout.setPinchZoomLayoutBridge(this);

        View orderBtn = findViewById(R.id.smart_snaps_analysis_product_edit_activity_order_tv);
        if (orderBtn != null) orderBtn.setOnClickListener(this);
    }

    private void initHandlers() {
        SmartRecommendBookHandlerInstance handlerInstance = new SmartRecommendBookHandlerInstance.Builder()
                .setExternalConnectionBridge(this).setSnapsProductEditInfo(snapsProductEditInfo).create();

        templateHandler = SmartRecommendBookTemplateHandler.createHandlerWithInstance(handlerInstance);
        captureHandler = SmartRecommendBookCaptureHandler.createHandlerWithInstance(handlerInstance);
        assistantControlHandler = SmartRecommendBookAssistantControlHandler.createHandlerWithInstance();
    }

    final void initProjectBaseInfo() {
        try {
            shouldBeComeBackToPreviewActivity = false;

            snapsProductEditInfo = SnapsProductEditInfo.createInstance();

            String prjCode = getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
            if (!StringUtil.isEmpty(prjCode)) {
                snapsProductEditInfo.setIS_EDIT_MODE(true);
                Config.setPROJ_CODE(prjCode);
            }

            String prodCode = getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE);
            if (!StringUtil.isEmpty(prodCode)) {
                Config.setPROD_CODE(prodCode);
            }

            Config.setFromCart(snapsProductEditInfo.IS_EDIT_MODE());

            snapsProductEditInfo.setCanvasList(new ArrayList<Fragment>());
            snapsProductEditInfo.setIS_EDIT_MODE(Config.isFromCart());
            snapsProductEditInfo.initTemplateUrl();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    private void applyTitleProjectName() {
        TextView themeTitle = (TextView) findViewById(R.id.ThemeTitleText);
        if (themeTitle != null) {
            String title = null;
            if (StringUtil.isEmptyAfterTrim(Config.getPROJ_NAME())) {
                title = getString(R.string.photo_book);
            } else {
                title = Config.getPROJ_NAME();
            }
            themeTitle.setText(title);
        }
    }

    private void changeCoverPage(SnapsPage coverPage) {
        if (coverPage == null || this.currentCoverPage == coverPage || getTemplate() == null)
            return;
        this.currentCoverPage = coverPage;
        try {
            updateTemplateCodeOnCoverPage(coverPage);

            PhotobookCommonUtils.changeCoverPage(coverPage, getTemplate(), getPageList());

            getTemplate().addQRcode(PhotobookCommonUtils.getPhotoBookQRCodeRect(getTemplate()));

            getTemplate().setApplyMaxPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void updateTemplateCodeOnCoverPage(SnapsPage coverPage) {
        if (coverPage == null) return;

        if (!StringUtil.isEmpty(coverPage.templateCode)) {
            Config.setTMPL_CODE(coverPage.templateCode);
        }
    }

    private void startPageEditActivityForResult(SnapsPageEditRequestInfo editRequestInfo) {
        if (editRequestInfo == null) return;

        SnapsOrderManager.cancelCurrentImageUploadExecutor(); //편집 화면에서 빠르게 얼굴 맞춤을 시도 하게 하기 위해서 업로드를 중단 시킨다..

        Intent intent = new Intent(this, SmartRecommendBookPageEditActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRAS_KEY_PAGE_EDIT_REQUEST_DATA, editRequestInfo);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_CODE_PAGE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PAGE_EDIT:
                SnapsOrderManager.setImageUploadStateListener(SmartRecommendBookMainActivity.this);
                SnapsOrderManager.uploadThumbImgListOnBackground();

                int itemPosition = scrollByLastEditIndexWithIntent(data);
                if (resultCode == RESULT_CODE_EDITED) {
                    refreshList();

                    SmartSnapsUtil.deleteSmartRecommendBookTempEditInfo();
                } else {
                    recoveryPrevEditInfo();
                }

                if (shouldBeComeBackToPreviewActivity) {
                    shouldBeComeBackToPreviewActivity = false;

                    OrientationSensorManager sensorManager = OrientationSensorManager.getInstance();
                    int lastScreenOrientation = sensorManager.getLastScreenOrientation();
                    performPreview(itemPosition, (lastScreenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : lastScreenOrientation));
                }
                break;
            case SnapsOrderConstants.LOGIN_REQUSTCODE:
//                refreshCoverTitleInfo();
                break;
            case REQ_PREVIEW:
                int pageIndex = scrollByLastEditIndexWithIntent(data);

                if (resultCode == RESULT_OK) {
                    performSaveToBasket();
                } else if (resultCode == RESULT_CODE_EDIT) {
                    shouldBeComeBackToPreviewActivity = true;
                    forceListItemClick(pageIndex);
                }
                break;
        }
    }

    private int scrollByLastEditIndexWithIntent(Intent data) {
        if (data == null) return -1;

        int targetIndex = data.getIntExtra(EXTRAS_KEY_LAST_EDITED_PAGE_REQUEST_DATA, 0);
        if (targetIndex < 0) return targetIndex;

        smoothScrollToPosition(targetIndex);
        return targetIndex;
    }

    private void smoothScrollToPosition(int index) {
        if (index < 0 || recyclerView == null || getPageList() == null || getPageList().size() == 0) return;
        int targetIndex = Math.min(getPageList().size() - 1, index);
        SnapsCustomLinearLayoutManager layoutManager = (SnapsCustomLinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.scrollToCenterPosition(recyclerView, targetIndex);
    }

    private void refreshList() {
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }

        applyTitleProjectName();
    }

    private void recoveryPrevEditInfo() {
        recoveryPhotoImageList();

        recoverySnapsPageList();

        recoveryCoverPageList();

        recoveryCoverTitle();

        PhotobookCommonUtils.imageResolutionCheck(getTemplate());

        refreshList();

        SmartSnapsUtil.deleteSmartRecommendBookTempEditInfo();
    }

    private void recoveryCoverTitle() {
        try {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            String prevCoverTitle = StringUtil.getSafeStrIfNotValidReturnSubStr(smartSnapsManager.getTempCoverTitle(), "");
            Config.setPROJ_NAME(prevCoverTitle);

            ArrayList<SnapsPage> coverList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
            if (coverList == null) return;

            for (SnapsPage coverPage : coverList) {
                if (coverPage == null) continue;
                PhotobookCommonUtils.handleNotifyCoverTextFromText(prevCoverTitle, this, coverPage);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void recoveryPhotoImageList() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        ArrayList<MyPhotoSelectImageData> tempPhotoImageList = smartSnapsManager.getTempPhotoImageDataList();
        smartSnapsManager.recoveryPrevImageList(tempPhotoImageList);
    }

    private void recoverySnapsPageList() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        ArrayList<SnapsPage> tempSnapsPageList = smartSnapsManager.getTempPageList();
        if (getTemplate() != null && getTemplate().getPages() != null) {
            getTemplate().getPages().clear();

            if (tempSnapsPageList != null) {
                getTemplate().getPages().addAll(tempSnapsPageList);
            }
        }
    }

    private void recoveryCoverPageList() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        ArrayList<SnapsPage> tempCoverPageList = smartSnapsManager.getTempCoverPageList();

        ArrayList<SnapsPage> coverPageList = smartSnapsManager.getCoverPageListOfAnalysisPhotoBook();
        if (coverPageList != null) {
            coverPageList.clear();

            if (tempCoverPageList != null) {
                coverPageList.addAll(tempCoverPageList);
            }
        }

        if (listAdapter != null) {
            int lastSelectedCoverItemPosition = listAdapter.getLastSelectedCoverItemPosition();
            if (coverPageList != null && coverPageList.size() > lastSelectedCoverItemPosition && lastSelectedCoverItemPosition >= 0) {
                SnapsPage currentCoverPage = coverPageList.get(lastSelectedCoverItemPosition);
                if (currentCoverPage != null)
                    changeCoverPage(currentCoverPage);
            }
        }
    }

    private boolean checkPermissionGranted() {
        boolean permissionGranted = true;
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION); // 설명을 보면 한번 사용자가 거부하고, 다시 묻지 않기를 체크하지 않았을때 여기를 탄다고 한다. 이때 설명을 넣고 싶으면 이걸 지우고 넣자.
                else
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Const_VALUE.REQ_CODE_PERMISSION);
                permissionGranted = false;
            }
        }

        return permissionGranted;
    }

    private void showPinchZoomTutorial() {
        if (Setting.getBoolean(this, Const_VALUE.KEY_USER_HAD_PINCH_ZOOM_ON_MAIN_LIST, false)) {
            showCoverDescTutorial();

            requestInitOrientationSensorListener();
            return;
        }

        SnapsTutorialUtil.showGifView(this, new SnapsTutorialAttribute.Builder().setGifType(SnapsTutorialAttribute.GIF_TYPE.RECOMMEND_BOOK_MAIN_LIST_PINCH_ZOOM).create(), new GifTutorialView.CloseListener() {
            @Override
            public void close() {
                showCoverDescTutorial();

                requestInitOrientationSensorListener();
            }
        });
    }

    private void requestInitOrientationSensorListener() {
        if (snapsHandler != null) {
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_UNLOCK_ORIENTATION_SENSOR, 2000);
        }
    }

    private void unlockOrientationSensorListener() {
        OrientationSensorManager orientationSensorManager = OrientationSensorManager.getInstance();
        orientationSensorManager.setBlockSensorEvent(false);
    }

    private void startImageUploadOnBackground() {
        try {
            SnapsOrderManager.initialize(SmartRecommendBookMainActivity.this);
            SnapsOrderManager.setImageUploadStateListener(SmartRecommendBookMainActivity.this);

            SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showCoverDescTutorial() {
        if (listAdapter == null || shouldCheckCoverTutorial) return;
        View targetView = findViewById(R.id.smart_snaps_analysis_product_edit_activity_cover_dummy_view);
        showCoverTutorialWithTargetView(targetView);
    }

    private void showCoverTutorialWithTargetView(View targetView) {
        if (targetView == null) return;
        SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER)
                .setText(getString(R.string.smart_snaps_recommend_book_cover_tutorial))
                .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_PHOTOBOOK_FIND_COVER)
                .setForceSetTargetView(true)
                .setTargetView(targetView)
                .create());

        shouldCheckCoverTutorial = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Config.setIS_MAKE_RUNNING(true);

        if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_UPLOAD_COMPLETE)) {
            SnapsOrderManager.showCompleteUploadPopup();
        }

        SnapsOrderManager.setSnapsOrderStatePauseCode("");

        SnapsOrderManager.registerNetworkChangeReceiverOnResume();

        OrientationSensorManager.resume(this);

        fixScreenOrientationState();
    }

    private void fixScreenOrientationState() { //만약, 화면이 누워 있다면 다시 세운다.
        int screenOrientation = UIUtil.getScreenOrientation(this);
        if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            UIUtil.fixOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Config.setIS_MAKE_RUNNING(false);

        if (SnapsOrderManager.isUploadingProject())
            SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_APPLICATION);

        OrientationSensorManager.pause();
    }

    @Override
    public void onClick(View v) {
        if (isListItemZoomMode()) return;

        int viewId = v.getId();

        if (viewId == R.id.smart_snaps_analysis_product_edit_activity_order_tv) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickCart)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            performSaveToBasket();
        } else if (v.getId() == R.id.ThemeTitleLeft || v.getId() == R.id.ThemeTitleLeftLy) {// 뒤로
            showCancelConfirm();
        } else if (v.getId() == R.id.popup_menu_photo_modify) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editpopup_clickEdit)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf((editRequestInfo != null ? editRequestInfo.getPageIndex() : -1)))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            onListItemClicked(editRequestInfo);
        } else if (v.getId() == R.id.popup_menu_photo_delete) {
            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editpopup_clickDelete)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_CNT, String.valueOf(PhotobookCommonUtils.getImgCntInPage((editRequestInfo != null ? editRequestInfo.getPageIndex() : -1), getPageList())))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf((editRequestInfo != null ? editRequestInfo.getPageIndex() : -1)))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));

            showDeletePageConfirm(editRequestInfo);
        } else if (v.getId() == R.id.smart_snaps_analysis_product_edit_activity_full_screen_btn) {
            performPreviewByOrientationSensor = false;
            performPreview(getCurrentVisibleItemPosition(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickPreview)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        } else if (v.getId() == R.id.smart_snaps_analysis_product_edit_activity_scroll_top_btn) {
            requestListScrollToTop();
        }
    }

    private void performPreview(int listItemPosition, int orientation) {
        OrientationManager.fixCurrentOrientation(this);
        SnapsTimerProgressView.destroyProgressView();

        Intent intent = new Intent(this, EditActivityPreviewActivity.class);
        intent.putExtra(EXTRAS_KEY_PAGE_INDEX, listItemPosition);
        intent.putExtra(EXTRAS_KEY_SCREEN_ORIENTATION_ACT_INFO, orientation);
        intent.putExtra(EXTRAS_KEY_ACTIVE_ROTATION_SENSOR, performPreviewByOrientationSensor);
        startActivityForResult(intent, REQ_PREVIEW);
    }

    private void requestListScrollToTop() {
        smoothScrollToPosition(0);

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickUp)
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
    }

    private int getCurrentVisibleItemPosition() {
        if (recyclerView == null) return 0;
        try {
            SnapsCustomLinearLayoutManager linearLayoutManager = (SnapsCustomLinearLayoutManager) recyclerView.getLayoutManager();
            return linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    private void performSaveToBasket() {
        OrientationSensorManager.getInstance().setBlockSensorEvent(true);

        SnapsOrderManager.performSaveToBasket(this);
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getGalleryList() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        return smartSnapsManager.getAllAddedImageList();
    }

    @Override
    public ArrayList<SnapsPage> getPageList() {
        if (getTemplate() == null || getTemplate().getPages() == null) {
            SnapsAssert.assertTrue(false);

            MessageUtil.alertnoTitleOneBtn(this, getString(R.string.smart_recommend_book_making_exception_retry_msg), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    handleActivityFinish();
                }
            });
            return null;
        }

        return getTemplate().getPages();
    }

    @Override
    public SnapsTemplate getTemplate() {
        return templateHandler != null ? templateHandler.getMainTemplate() : null;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void pageProgressUnload() {
        try {
            if (getDefaultProgress() != null)
                getDefaultProgress().dismiss();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void showPageProgress() {
        try {
            if (getDefaultProgress() != null && !getDefaultProgress().isShowing())
                getDefaultProgress().show();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onBackPressed() {
        if (isListItemZoomMode()) {
            stopListItemZoomMode();
        } else {
            showCancelConfirm();
        }
    }

    public void showCancelConfirm() {
        String msg = getString(Config.isFromCart() ? R.string.moveto_cartpage_msg : R.string.do_not_save_then_move_to_prev_page_at_main_act);
        MessageUtil.alertnoTitle(this, msg, new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                if (clickedOk == ICustomDialogListener.OK) {
                    handleActivityFinish();

                    SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completeback_clickConfirm)
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                } else {
                    SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_completeback_clickCancel)
                            .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
                }
            }
        });

        SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_complete_clickBack)
                .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
    }

    private void handleActivityFinish() {
        if (Config.isFromCart()) {
            completeFinish();
        } else {
            finishAnalysisProductActivity();
        }
    }

    private void completeFinish() {
        DataTransManager.releaseInstance();

        SmartSnapsManager.finalizeInstance();

        finalizeInstance();

        SnapsProductInfoManager productInfoManager = SnapsProductInfoManager.getInstance();
        if (productInfoManager != null)
            productInfoManager.setPROD_CODE("");

        SmartRecommendBookMainActivity.this.finish();
    }

    private void finishAnalysisProductActivity() {
        SnapsOrderManager.cancelCurrentImageUploadExecutor();

        if (!Config.isFromCart() & !Config.getAI_IS_RECOMMENDAI()) {
            Intent intent = new Intent(SmartRecommendBookMainActivity.this, ImageSelectActivityV2.class);
            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setComebackFromEditActivity(true)
                    .setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT)
                    .setHomeSelectProduct(Config.SELECT_SMART_ANALYSIS_PHOTO_BOOK)
                    .setHomeSelectProductCode(Config.getPROD_CODE())
                    .setHomeSelectKind("").create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            intent.putExtras(bundle);

            if (Config.getAI_IS_SELFAI()) Config.setAI_SELFAI_EDITTING(false);
            startActivity(intent);
        }

        finalizeInstance();

        SmartRecommendBookMainActivity.this.finish();
    }

    private void finalizeInstance() {
        try {
            Config.setIS_MAKE_RUNNING(false);
            Config.setFromCart(false);
            Config.setPROJ_NAME("");
            Config.setPROJ_CODE("");
            Config.setTMPL_COVER(null);

            SnapsTimerProgressView.destroyProgressView();

            pageProgressUnload();

            SnapsOrderManager.finalizeInstance();

            SnapsUploadFailedImageDataCollector.clearHistory(Config.getPROJ_CODE());

            ViewUnbindHelper.unbindReferences(getActivity().getWindow().getDecorView());

            SnapsTemplateManager templateManager = SnapsTemplateManager.getInstance();
            if (templateManager != null) {
                templateManager.cleanInstance();
            }
            SnapsTemplateManager.notifyEditActivityFinishingSyncLocker();

            SnapsTemplateManager.finalizeInstance();

            clearPrevInfo();

            SmartSnapsManager.clearAllInstanceInfo();

            ImageSelectUtils.initPhotoLastSelectedHistory();

            OrientationManager.finalizeInstance();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void clearPrevInfo() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.clearTemplateInfo();
    }

    @Override
    public void setPageThumbnailFail(int index) {
        SnapsAssert.assertNotNull(captureHandler);
        captureHandler.setPageThumbnailFail(index);
    }

    @Override
    public void setPageThumbnail(int pageIdx, String filePath) {
        SnapsAssert.assertNotNull(captureHandler);
        captureHandler.setPageThumbnail(pageIdx, filePath);
    }

    @Override
    public void setPageFileOutput(int index) {
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return PhotobookCommonUtils.getMyPhotoSelectImageDataWithTemplate(getTemplate(), IPhotobookCommonConstants.eImageDataRequestType.ALL);
    }

    @Override
    public SnapsOrderAttribute getSnapsOrderAttribute() {
        return new SnapsOrderAttribute.Builder()
                .setActivity(getActivity())
                .setEditMode(snapsProductEditInfo.IS_EDIT_MODE())
                .setImageList(PhotobookCommonUtils.getImageListFromTemplate(getTemplate()))
                .setPageList(snapsProductEditInfo.getPageList())
                .setSnapsTemplate(getTemplate())
                .setBackPageList(snapsProductEditInfo.getBackPageList())
                .setCanvasList(snapsProductEditInfo.getCanvasList())
                .setTextOptions(snapsProductEditInfo.getTextOptions())
                .create();
    }

    @Override
    public void onOrderStateChanged(int state) {
        try {
            switch (state) {
                case ORDER_STATE_UPLOADING:
                    break;
                case ORDER_STATE_STOP:
                    OrientationSensorManager.getInstance().setBlockSensorEvent(false);
                    break;
                case ORDER_STATE_CANCEL:
                    OrientationSensorManager.getInstance().setBlockSensorEvent(false);
                    findLowResolutionImage();
                    break;

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void findLowResolutionImage() {
        if (getPageList() == null) return;

        ArrayList<SnapsPage> pageList = getPageList();
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            if (snapsPage != null && snapsPage.isExistLowResolutionImageData()) {
                smoothScrollToPosition(ii);
                break;
            }
        }
    }

    @Override
    public void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener) {
        SnapsAssert.assertNotNull(captureHandler);
        captureHandler.setSnapsPageCaptureListener(captureListener);

        PhotobookCommonUtils.changePageThumbnailState(getPageList(), 0, false); //커버는 매번 따야 하니까..

        setPageThumbnail(0, "");
    }

    @Override
    public void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener) {
    }

    @Override
    public void onUploadFailedOrgImgWhenSaveToBasket() throws Exception {
        SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createUploadFailedImagePopupAttribute(getActivity(), Config.getPROJ_CODE(), false);

        SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
            @Override
            public void onShowUploadFailedImagePopup() {
                OrientationManager.fixCurrentOrientation(getActivity());
            }

            @Override
            public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
                PhotobookCommonUtils.setUploadFailedIconVisibleStateToShow(getTemplate());

                refreshList();

                selectViewPagerToErrorImagePosition(uploadFailedImageList);
            }
        });
    }

    private void selectViewPagerToErrorImagePosition(List<MyPhotoSelectImageData> uploadFailedImageList) {
        try {
            MyPhotoSelectImageData uploadErrImgData = PhotobookCommonUtils.findFirstIndexOfUploadFailedOrgImageOnList(uploadFailedImageList);
            int pageIndex = PhotobookCommonUtils.findImageDataIndexOnPageList(getPageList(), uploadErrImgData);
            smoothScrollToPosition(pageIndex);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onDisconnectNetwork() {
        if (SmartSnapsManager.isSmartAreaSearching()) {
            try {
                suspendSmartSnapsFaceSearching();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    void suspendSmartSnapsFaceSearching() throws Exception {
        SmartSnapsManager.suspendSmartSnapsFaceSearching();

        refreshList();
    }

    @Override
    public void increaseCanvasLoadCompleteCount() {
    }

    @Override
    public void decreaseCanvasLoadCompleteCount() {
    }

    @Override
    public int getCanvasLoadCompleteCount() {
        return 0;
    }

    @Override
    public SnapsProductEditorAPI getProductEditorAPI() {
        return null;
    }

    @Override
    public void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {
    }

    @Override
    public void onThumbImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData) {
        SnapsImageUploadResultHandleData resultHandleData = new SnapsImageUploadResultHandleData.Builder()
                .setActivity(getActivity()).setSnapsHandler(snapsHandler).setSnapsTemplate(getTemplate()).setState(state).setUploadResultData(resultData).create();
        PhotobookCommonUtils.handleThumbImgUploadStateChanged(resultHandleData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const_VALUE.REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadTemplate();
                } else {
                    MessageUtil.alert(this, getString(R.string.need_to_permission_accept_for_get_phone_pictures), "", R.string.cancel, R.string.confirm_move_to_setting, false, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                SystemIntentUtil.showSystemSetting(SmartRecommendBookMainActivity.this);
                            }
                            SmartRecommendBookMainActivity.this.finish();
                        }
                    });
                }
                break;
        }
    }

    private PopoverView getPopupMenuView() {
        return assistantControlHandler != null ? assistantControlHandler.getPopupMenuView() : null;
    }

    private CustomizeDialog getConfirmDialog() {
        return assistantControlHandler != null ? assistantControlHandler.getConfirmDialog() : null;
    }

    private DialogDefaultProgress getDefaultProgress() {
        return assistantControlHandler != null ? assistantControlHandler.getDefaultProgress() : null;
    }

    public void requestChangeTitleText() {
        if (snapsHandler != null) {
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_CHANGE_TITLE_TEXT, 0);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_MSG_SHOW_TUTORIAL_FOR_SMART_RECOMMEND_BOOK_COVER:
                showCoverDescTutorial();
                break;
            case HANDLER_MSG_UNLOCK_ORIENTATION_SENSOR:
                unlockOrientationSensorListener();
                break;
            case HANDLER_MSG_CHANGE_TITLE_TEXT:
                applyTitleProjectName();
                break;
        }
        SnapsOrderManager.handleUploadImageCommonMessage(msg);
    }
}