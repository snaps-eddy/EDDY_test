package com.snaps.mobile.activity.common.products.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.data.smart_snaps.SmartSnapsAnimationListener;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.FragmentUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditControls;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.DialogSmartSnapsProgress;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.themebook.OrientationChecker;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.EditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultHandleData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

import static android.app.Activity.RESULT_OK;
import static com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants.EXTRA_NAME_ADD_PAGE_INDEX_LIST;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public abstract class SnapsProductBaseEditor extends SnapsProductEditorAPI {
    private static final String TAG = SnapsProductBaseEditor.class.getSimpleName();

    private SnapsProductBaseEditorHandler snapsProductEditBaseHandler;

    private SnapsProductEditInfo snapsProductEditInfo;

    private SnapsProductEditControls snapsProductEditControls;

    private SnapsHandler snapsHandler = null;

    private SnapsBroadcastReceiver receiver = null;

    private ISnapsCaptureListener snapsPageCaptureListener = null;

    private FragmentActivity activity = null;

    private boolean isInitializedTemplate = false;

    SnapsProductBaseEditor(FragmentActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    @Override
    public void onCreate() {
        initialize();

        startAutoSave();

        //템플릿 로딩은 handleOnFirstResume 에서 시작된다.
    }

    private void initialize() {
        addClassNameLog();

        setActivityDefaultConfig();

        setActivityContentView();

        initLayout();

        initControls();

        initEditData();

        registerListeners();

        initControlVisibleStateOnActivityCreate();
    }

    private void startAutoSave() {
        if (!AutoSaveManager.isSupportProductAutoSave() || AutoSaveManager.isAutoSaveRecoveryMode())
            return;

        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null) return;

        if (getAutoSaveProductCode() > 0) saveMan.startAutoSave(getAutoSaveProductCode());
    }

    private void addClassNameLog() {
        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
    }

    private void setActivityDefaultConfig() {
        snapsHandler = new SnapsHandler(this);

        snapsProductEditInfo = SnapsProductEditInfo.createInstance();

        snapsProductEditControls = SnapsProductEditControls.createInstance();

        snapsProductEditBaseHandler = getBaseEditorHandler();

        getOrientationManager().init(getActivity());

        Config.setIS_MAKE_RUNNING(true);

        // 폴더 생성.
        FileUtil.initProjectFileSaveStorage();
    }

    protected SnapsProductBaseEditorHandler getBaseEditorHandler() {
        return SnapsProductBaseEditorHandler.createHandlerWithBridge(this);
    }

    OrientationManager getOrientationManager() {
        return OrientationManager.getInstance(getActivity());
    }

    protected final boolean isLandScapeScreen() {
        return getOrientationManager().isLandScapeMode();
    }

    private void initEditData() {
        initBaseInstances();

        initTemplateUrl();

        initGalleryData();

        initEditInfoBeforeLoadTemplate();
    }

    @Override
    public void onClickedAddPage() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleOnClickedAddPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedPreview() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleClickedPreview();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedChangeTitle() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleClickedChangeTitle();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedImageEdit() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleOnClickedImageEdit();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedLayoutControl(Intent intent) {
        try {
            snapsProductEditBaseHandler.handleOnClickedLayoutControl(intent);
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedImageChange() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleOnClickedChangePhoto();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedImageRemove() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleOnClickedImageRemove();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClickedSaveBasket() {
        if (isBlockAllOnClickEvent()) return;

        SnapsOrderManager.performSaveToBasket(this);
    }

    private void initBaseInstances() {
        snapsProductEditInfo.setCanvasList(new ArrayList<Fragment>());

        initProjectCodeAndProdCode();
        SnapsAssert.assertNotEmptyStr(Config.getPROD_CODE());

        SnapsCanvasFragment snapsCanvasFragment = createCanvasFragmentWithProductCode(Config.getPROD_CODE());
        if (snapsCanvasFragment == null) {
            DataTransManager.notifyAppFinish(getActivity());
            return;
        }

        snapsCanvasFragment.setRetainInstance(true);

        getEditControls().setCanvasFragment(snapsCanvasFragment);

        getEditControls().setPageClipBoard(new ThemeBookClipBoard(getActivity()));

        setPageProgress(new DialogDefaultProgress(getActivity()));

        if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
            try {
                setSmartSnapsPageProgress(snapsProductEditBaseHandler.createBaseSmartSnapsProgressDialog());
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    final void initProjectCodeAndProdCode() {
        if (AutoSaveManager.isAutoSaveRecoveryMode()) {
            snapsProductEditInfo.setIS_EDIT_MODE(Config.isFromCart());
            return;
        } else {
            Config.setPROJ_CODE("");
        }

        String prjCode = getActivity().getIntent().getStringExtra(Const_EKEY.MYART_PROJCODE);
        if (!StringUtil.isEmpty(prjCode)) {
            snapsProductEditInfo.setIS_EDIT_MODE(true);
            Config.setPROJ_CODE(prjCode);
        }

        String prodCode = getActivity().getIntent().getStringExtra(Const_EKEY.MYART_PRODCODE);
        if (!StringUtil.isEmpty(prodCode)) {
            Config.setPROD_CODE(prodCode);
        }

        Config.setFromCart(snapsProductEditInfo.IS_EDIT_MODE());
    }

    protected void initTemplateUrl() {
        try {
            snapsProductEditInfo.initTemplateUrl();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private void initGalleryData() {
        try {
            snapsProductEditInfo.initGalleryListFromDataTransManager(getActivity());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private SnapsCanvasFragment createCanvasFragmentWithProductCode(String prodCode) {
        if (!StringUtil.isEmpty(Config.getPROD_CODE())) {
            return new SnapsCanvasFragmentFactory().createCanvasFragment(prodCode);
        }
        return null;
    }

    private void registerListeners() {
        registerClickLayoutActionReceiver();
    }

    final void unRegisterReceivers() {
        getActivity().unregisterReceiver(receiver);
    }

    private void registerClickLayoutActionReceiver() {
        IntentFilter filter = new IntentFilter(Const_VALUE.CLICK_LAYOUT_ACTION);
        filter.addAction(Const_VALUE.TEXT_TO_IMAGE_ACTION);
        receiver = new SnapsBroadcastReceiver();
        receiver.setImpRecevice(this);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        UIUtil.applyLanguage(getActivity());
        handleOnScreenRotated(newConfig);
    }

    private void handleOnScreenRotated(Configuration newConfig) {
        try {
            snapsProductEditBaseHandler.handleOnScreenRotated(newConfig);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void setNotExistTitleActLayout() {
        try {
            snapsProductEditBaseHandler.handleNotExistTitleActLayout();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void setPreviewBtnVisibleState();

    final void handleBasePreviewBtnVisibleState() {
        try {
            snapsProductEditBaseHandler.handleBasePreviewBtnVisibleState();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 페이지 로드
     */
    public void loadPager() {
        try {
            snapsProductEditBaseHandler.loadPager();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initDragView() {
        try {
            snapsProductEditBaseHandler.handleInitDragView();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void dismissPopOvers() {
        try {
            snapsProductEditBaseHandler.handleDismissPopOvers();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void setActivityContentView();

    final void handleBaseActivityContentView() {
        SnapsAssert.assertNotNull(getActivity());
        getActivity().setContentView(R.layout.activity_edit_themebook);
    }

    private void initLayout() {
        try {
            snapsProductEditBaseHandler.handleInitLayout();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    private void initControls() {
        snapsProductEditBaseHandler.handleInitControls();
    }

    @Override
    public abstract void onCenterPagerSelected(int page);

    final void handleOnCenterPagerSelected(int page) {
        snapsProductEditBaseHandler.handleOnCenterPagerSelected(page);

        handleCenterPagerSelected();
    }

    void initCanvasMatrix() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_INIT_CANVAS_MATRIX, 500);
    }

    @Override
    public abstract void onClickedChangeDesign();

    final void handleBaseChangePageDesign() {
        if (isBlockAllOnClickEvent()) return;
        try {
            snapsProductEditBaseHandler.handleBaseChangePageDesign();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void onClickedChangePeriod();

    final void handleBaseChangePeriod() {
    }

    @Override
    public abstract void onClickedInfo();

    final void handleBaseInfo() {
    }

    @Override
    public void addPage() {
        showAddPageActivity();
    }

    @Override
    public abstract BaseEditActivityThumbnailAdapter createThumbnailAdapter();

    final BaseEditActivityThumbnailAdapter handleCreateThumbnailAdapter() {
        return new EditActivityThumbnailAdapter(getActivity(), getEditInfo());
    }

    protected final void showCoverChangeActcity() {
        try {
            snapsProductEditBaseHandler.handleShowCoverChangeActcity();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void removeDragView() {
        try {
            snapsProductEditBaseHandler.handleRemoveDragView();
        } catch (Exception e) {
            SnapsAssert.assertException(getActivity(), e);
            Dlog.e(TAG, e);
        }
    }

    protected final void showChangePageActcity(boolean prmSideT) {
        try {
            snapsProductEditBaseHandler.handleShowChangePageActcity(prmSideT);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void showChangeProductPageActivity() {
        try {
            snapsProductEditBaseHandler.handleShowAddProductChangePageActcity();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void showAddProductPageActivity(int maxCount) {
        try {
            snapsProductEditBaseHandler.handleShowAddProductPageActcity(maxCount);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void showAddPageActivity();

    final void handleShowAddPageActivity() {
        try {
            snapsProductEditBaseHandler.handleShowAddPageActcity();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void showPageOverCountToastMessage();

    final void handleShowPageOverCountToastMessage() {
        MessageUtil.toast(getActivity(), R.string.disable_add_page);
    }

    @Override
    public abstract void showAddStickToastMsg();

    final void handleShowAddStickToastMsg() {
        MessageUtil.toast(getActivity(), getActivity().getString(R.string.cover_add_stick_msg));//"커버에 책등이 추가되었습니다. 책등에는 입력하신 제목이 출력됩니다.");
    }

    /***
     * 페이지 추가하기..
     */
    public abstract void addPage(int index);

    final void handleAddPage(int index) {
        snapsProductEditBaseHandler.handleAddPage(index);
    }

    @Override
    public abstract boolean addPage(int pageIDX, SnapsPage... pages);

    final boolean handleAddPage(int pageIDX, SnapsPage... pages) {
        return snapsProductEditBaseHandler.handleAddPage(pageIDX, pages);
    }

    @Override
    public abstract void changePage(int pageIDX, SnapsPage pages);

    final void handlechangePage(int pageIDX, SnapsPage pages) {
        try {
            snapsProductEditBaseHandler.changePage(pageIDX, pages);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void deletePage(final int index);

    final void handleDeletePage(final int index) {
        snapsProductEditBaseHandler.handleDeletePage(index);
    }

    @Override
    public abstract void exportAutoSaveTemplate();

    final void handleExportAutoSaveTemplate() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null) saveMan.exportTemplate(getTemplate());
    }

    @Override
    public abstract ArrayList<String> getPageThumbnailPaths();

    final ArrayList<String> getBasePageThumbnailPaths() {
        return PhotobookCommonUtils.getBasePageThumbnailPathsFromPageList(getPageList());
    }

    @Override
    public abstract void refreshList(final int startPageIDX, final int endPageIdx);

    final void handleRefreshList(final int startPageIDX, final int endPageIdx) {
        try {
            snapsProductEditBaseHandler.handleRefreshList(startPageIDX, endPageIdx);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void refreshUI();

    final void handleRefreshUI() {
        try {
            snapsProductEditBaseHandler.refreshUI();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void handleAfterRefreshList(int startPageIDX, int endPageIdx);

    final void handleBaseAfterRefreshList(int startPageIDX, int endPageIdx) {
        try {
            snapsProductEditBaseHandler.handleBaseAfterRefreshList(startPageIDX, endPageIdx);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void refreshPageThumbnailsAfterDelay() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_REFRESH_THUMBNAIL, 500);
    }

    @Override
    public abstract void refreshPageThumbnail();

    final void handleBaseRefreshPageThumbnail() {
        try {
            snapsProductEditBaseHandler.handleBaseRefreshPageThumbnail();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void refreshPageThumbnail(int page, long delay);

    final void handleBaseRefreshPageThumbnail(int page, long delay) {
        if (snapsHandler != null) {
            Message msg = new Message();
            msg.what = HANDLER_MSG_REFRESH_THUMBNAIL_WITH_PAGE;
            msg.arg1 = page;
            snapsHandler.sendMessageDelayed(msg, delay);
        }
    }

    final void handleBaseRefreshPageThumbnail(int page) {
        try {
            snapsProductEditBaseHandler.handleBaseRefreshPageThumbnail(page);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void requestRefreshThumbnails() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_REFRESH_DRAG_VIEW, 1000);
    }

    public void offerQueue(int start, int end) {
        try {
            snapsProductEditInfo.offerQueue(start, end);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected boolean refreshPagesId(ArrayList<SnapsPage> pageList) {
        try {
            return PhotobookCommonUtils.refreshPagesId(pageList);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    protected final boolean divisionPageListFrontAndBack(SnapsTemplate snapsTemplate) {
        try {
            return snapsProductEditBaseHandler.divisionPageListFrontAndBack(snapsTemplate);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    @Override
    public abstract void setTemplateBaseInfo();

    protected final void handleBaseTemplateBaseInfo() {
        try {
            snapsProductEditBaseHandler.handleBaseTemplateBaseInfo();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract Rect getQRCodeRect();

    final Rect getBaseQRCodeRect() {
        return null;
    }

    @Override
    public abstract boolean isOverPageCount();

    final boolean checkBaseOverPageCount() {
        try {
            return snapsProductEditBaseHandler.checkBaseOverPageCount();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    @Override
    public abstract void setThumbnailSelectionDragView(int pageChangeType, int page);

    final void handleBaseThumbnailSelectionDragView(int pageChangeType, int page) {
        snapsProductEditBaseHandler.handleThumbnailSelectionDragView(pageChangeType, page);
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    public ArrayList<SnapsPage> getPageList() {
        SnapsAssert.assertNotNull(snapsProductEditInfo);
        return snapsProductEditInfo.getPageList();
    }

    private void initSnapsOrderManager() {
        try {
            SnapsOrderManager.initialize(this);
            SnapsOrderManager.setImageUploadStateListener(this);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            snapsProductEditBaseHandler.handleBaseBackPressed();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onPause() {
        try {
            snapsProductEditBaseHandler.handleBaseOnPause();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void restoreRotateState() {
        try {
            snapsProductEditBaseHandler.handleRestoreRotateState();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void notifyOrientationState() {
        try {
            snapsProductEditBaseHandler.handleNotifyOrientationState();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onResume() {
        try {
            snapsProductEditBaseHandler.handleBaseOnResume();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void onFirstResume() {
        if (!snapsProductEditInfo.isFirstLoad()) return;
        snapsProductEditInfo.setFirstLoad(false);
        handleOnFirstResume();
    }

    final boolean isFirstResume() {
        return snapsProductEditInfo != null && snapsProductEditInfo.isFirstLoad();
    }

    @Override
    public abstract void handleOnFirstResume();

    final void handleBaseOnFirstResume() {
        String templateUrl = getEditInfo() != null ? getEditInfo().getTemplateUrl() : "";
        getTemplateHandler(templateUrl);
    }

    @Override
    public void onOrderStateChanged(int state) {
        try {
            snapsProductEditBaseHandler.handleBaseOnOrderStateChanged(state);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onUploadFailedOrgImgWhenSaveToBasket() {
        try {
            snapsProductEditBaseHandler.handleBaseOnUploadFailedOrgImgWhenSaveToBasket();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void selectUploadErrorImgInPager(List<MyPhotoSelectImageData> uploadFailedImageList) {
        try {
            snapsProductEditBaseHandler.handleSelectUploadErrorImgInPager(uploadFailedImageList);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    final void notifyCenterPagerAdapter() {
        try {
            snapsProductEditBaseHandler.handleNotifyCenterPagerAdapter();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onStop() {
        SnapsOrderManager.unRegisterNetworkChangeReceiver();
    }

    @Override
    public void onDestroy() {
        try {
            snapsProductEditBaseHandler.handleBaseOnDestroy();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (isBlockAllOnClickEvent()) return;

            UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

            // 사진 편집(crop)
            if (v.getId() == R.id.popup_menu_photo_modify) {
                onClickedImageEdit();
            } else if (v.getId() == R.id.popup_menu_photo_delete) {
                onClickedImageRemove();
            } else if (v.getId() == R.id.popup_menu_photo_change) {
                onClickedImageChange();
            } else if (R.id.exist_pop_menu_bottom_photo == v.getId() || R.id.default_pop_menu_bottom_photo == v.getId() || R.id.exist_contents_pop_menu_bottom_photo == v.getId()) {
                performSelectPhoto();
            } else if (R.id.exist_pop_menu_bottom_contents == v.getId() || R.id.default_pop_menu_bottom_contents == v.getId() || R.id.exist_contents_pop_menu_bottom_contents == v.getId()) {
                performChangePageContents();
            } else if (R.id.ThemecartBtn == v.getId() || R.id.ThemecartTxt == v.getId()) {
                onClickedSaveBasket();
            } else if (v.getId() == R.id.btn_confim) {
                performGoToHomeAct();
            } else if (v.getId() == R.id.popup_menu_photo_bottom_copy) {
                performCopyPage();
            } else if (v.getId() == R.id.popup_menu_photo_bottom_paste) {
                performPastePage();
            } else if (v.getId() == R.id.popup_menu_photo_bottom_delete) {
                performShowDeletePageConfirm();
            } else if (v.getId() == R.id.popup_menu_card_text_delete) {
                performDeleteText();
            } else if (v.getId() == R.id.popup_menu_card_text_modify) {
                performEditText();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performSelectPhoto() {
        try {
            snapsProductEditBaseHandler.handlePerformSelectPhoto();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performChangePageContents() {
        try {
            snapsProductEditBaseHandler.handlePerformChangePageContents();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performEditText() {
        try {
            snapsProductEditBaseHandler.handlePerformEditText();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performGoToHomeAct() {
        try {
            snapsProductEditBaseHandler.handlePerformGoToHomeAct();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performCopyPage() {
        try {
            snapsProductEditBaseHandler.handlePerformCopyPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performPastePage() {
        try {
            snapsProductEditBaseHandler.handlePerformPastePage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performShowDeletePageConfirm() {
        try {
            snapsProductEditBaseHandler.handlepPerformShowDeletePageConfirm();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void performDeleteText() {
        try {
            snapsProductEditBaseHandler.handlePerformDeleteText();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void deletePage();

    final void handleDeletePage() {
        snapsProductEditBaseHandler.handleDeletePage();
    }

    final void findEmptyImage() {
        snapsProductEditBaseHandler.handleFindEmptyImage();
    }

    final void findLowResolutionImage() {
        snapsProductEditBaseHandler.handleFindLowResolutionImage();
    }

    private void showImageEmptyLayoutControlTooltip(View view) {
        snapsProductEditBaseHandler.handleShowImageEmptyLayoutControlTooltip(view);
    }

    @Override
    public abstract String getDeletePageMessage();

    final String getBaseDeletePageMessage() {
        return getString(R.string.page_delete_msg);
    }

    @Override
    public abstract void showCoverSpineDeletedToastMsg();

    final void handleShowCoverSpineDeletedToastMsg() {
        MessageUtil.toast(getActivity(), getString(R.string.cover_spine_deleted_msg));//"커버에 책등이 삭제되었습니다.");
    }

    @Override
    public abstract void showCannotDeletePageToast(int minCount);

    final void handleShowCannotDeletePageToast(int minCount) {
        String basePageMsg = getString(R.string.make_disable_page_msg);
        MessageUtil.toast(getActivity(), String.format(basePageMsg, minCount));
    }

    @Override
    public abstract boolean isLackMinPageCount();

    final boolean checkBaseLackMinPageCount() {
        EditActivityThumbnailUtils thumbnailUtil = snapsProductEditControls.getThumbnailUtil();
        return thumbnailUtil != null && thumbnailUtil.isLimitPageCount();
    }

    int getLimitPageCount() {
        EditActivityThumbnailUtils thumbnailUtil = snapsProductEditControls.getThumbnailUtil();
        if (thumbnailUtil != null) {
            return thumbnailUtil.getMinPage() * 2 + 1;
        }
        return 21;
    }

    protected final ArrayList<MyPhotoSelectImageData> getMyPhotoSelectImageData(boolean isOnlyCover) {
        IPhotobookCommonConstants.eImageDataRequestType requestType = isOnlyCover ? eImageDataRequestType.ONLY_COVER : eImageDataRequestType.ALL;
        return PhotobookCommonUtils.getMyPhotoSelectImageDataWithTemplate(getTemplate(), requestType);
    }

    @Override
    public void onPageLoadComplete(int page) {
        snapsProductEditBaseHandler.dismissPageProgress();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            //이미지 편집 화면과 화면 방향이 바뀌면 오류가 발생하기 때문에 onResume 에서 처리 한다.
            if (!getOrientationChecker().checkChangedOrientationAtImgEditor()) {
                if (!isLockPortraitOrientation()) {
                    notifyOrientationState();
                }
            }

            if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                SnapsOrderManager.uploadThumbImgListOnBackground();
            }

            if (requestCode == REQ_EDIT_TEXT) {
                requestRemoveBlurActivityWhenWrittenText();
            }

            return;
        }

        switch (requestCode) {
            case REQ_PHOTO:  // 사진변경, 사진추가
                notifyLayoutControlFromIntentData(data);
                break;
            case REQ_MODIFY:
                notifyImageDataOnModified();
                break;
            case REQ_COVER_PHOTO:
                notifyCoverLayoutControlFromIntentData(data);
                break;
            case REQ_COVER_TEXT:
                notifyCoverTextFromIntentData(data);
                break;
            case REQ_COVER_CHANGE:
                notifyChangedCoverFromIntentData(data);

                if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                    SnapsOrderManager.uploadThumbImgListOnBackground();
                }
                break;
            case REQ_ADD_PAGE:
                notifyAddedCoverFromIntentData(data);
                break;
            case REQ_CHANGE_PAGE:
                notifyChangedPageFromIntentData(data);

                if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                    SnapsOrderManager.uploadThumbImgListOnBackground();
                }
                break;
            case REQ_PREVIEW:
                requestDirectUpload();
                break;
            case REQ_EDIT_TEXT:
                requestRemoveBlurActivityWhenWrittenText();
                notifyTextControlFromIntentData(data);
                break;
            case REQ_NAME_STICKER_EDIT_TEXT:
                setNameStikerEdit(data);
                break;

        }
    }

    private void setNameStikerEdit(Intent data) {
        int position = data.getIntExtra("position", 0);
        refreshList(position, position);
    }

    private void notifyTextControlFromIntentData(Intent data) {
        View view = (View) findViewById(getEditInfo().getTempImageViewID());
        if (view != null) {
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
            if (snapsControl != null && snapsControl instanceof SnapsTextControl) {
                SnapsTextControl control = (SnapsTextControl) snapsControl;
                String str = data.getStringExtra("contentText");
                control.text = str;

                if (str != null) {
                    control.setText(str);
                    int textAlignOrdinal = data.getIntExtra("snapsTextAlign", 0);
                    SnapsTextAlign align = SnapsTextAlign.values()[textAlignOrdinal];
                    control.format.align = align.getStr();

                    String textColor = data.getStringExtra("fontColor");
                    if (!StringUtil.isEmpty(textColor))
                        control.format.fontColor = textColor;

                    control.isEditedText = true;
                }

                SnapsPagerController2 loadPager = getEditControls().getLoadPager();
                if (loadPager != null)
                    loadPager.pageAdapter.notifyDataSetChanged();

                offerQueue(control.getPageIndex(), control.getPageIndex());

                OrientationManager.fixCurrentOrientation(getActivity());
                refreshPageThumbnailsAfterDelay();

                notifyTextControlFromIntentDataHook(control);
            }
        }
    }

    private void notifyLayoutControlFromIntentData(Intent data) {
        snapsProductEditBaseHandler.handleNotifyLayoutControlFromIntentData(data);
    }

    private void notifyCoverLayoutControlFromIntentData(Intent data) {
        try {
            snapsProductEditBaseHandler.handleNotifyCoverLayoutControlFromIntentData(data);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void notifyCoverTextFromIntentData(Intent data) {
        try {
            snapsProductEditBaseHandler.handleNotifyCoverTextFromIntentData(data);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void notifyChangedCoverFromIntentData(Intent data) {
        String xmlPath = data.getStringExtra("coverXMLPATH");
        changeCover(xmlPath);
    }

    private void notifyAddedCoverFromIntentData(Intent data) {
        String xmlPath = data.getStringExtra("pageXMLPATH");
        addPage(xmlPath);
        if (Config.isPhotobooks()) {
            // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//            Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Edit", "Add"));
        }
    }

    private void notifyChangedPageFromIntentData(Intent data) {
        String xmlPath = data.getStringExtra("pageXMLPATH");
        changePage(xmlPath);
    }

    private void requestDirectUpload() {
        try {
            snapsProductEditBaseHandler.handleBaseRequestDirectUpload();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void refreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl control);

    final void handleRefreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl control) {
        try {
            snapsProductEditBaseHandler.handleRefreshSelectedNewImageData(newImageData, control);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void notifyImageDataOnModified() {
        try {
            snapsProductEditBaseHandler.handleNotifyImageDataOnModified();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected void setNotExistThumbnailLayout() {
        try {
            snapsProductEditBaseHandler.handleNotExistThumbnailLayout();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void onThumbnailViewClick(View view, int position);

    final void handleOnThumbnailViewClick(View view, int position) {
        try {
            snapsProductEditBaseHandler.handleOnThumbnailViewClick(view, position);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 썸네일 뷰를 롱 클릭해서 들어 올렸다가, 내려 놓는 순간 호출 된다.
     */
    @Override
    public void onRearrange(final View view, final int oldIndex, final int newIndex) {
        if (snapsHandler != null) {
            Message message = new Message();
            message.what = HANDLER_MSG_THUMBNAIL_AREA_REARRANGE;
            message.obj = view;
            message.arg1 = oldIndex;
            message.arg2 = newIndex;
            snapsHandler.sendMessageDelayed(message, 300);
        }
    }

    private void handleOnRearrange(Message msg) {
        try {
            snapsProductEditBaseHandler.handleBaseOnRearrange((View) msg.obj, msg.arg1, msg.arg2);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void handleTryShowTextEditTutorial() {
        try {
            snapsProductEditBaseHandler.handleShowTextControlTutorial();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static final int HANDLER_MSG_SHOW_POPOVER_VIEW = 1;
    static final int HANDLER_MSG_NOTIFY_ORIENTATION_STATE = 2;
    static final int HANDLER_MSG_REFRESH_THUMBNAIL = 3;
    static final int HANDLER_MSG_REFRESH_DRAG_VIEW = 4;
    public static final int HANDLER_MSG_UNLOCK_ROTATE_BLOCK = 6;
    static final int HANDLER_MSG_REFRESH_CHANGED_PHOTO = 7;
    static final int HANDLER_MSG_SHOW_IMG_EMPTY_LAYOUT_CONTROL_TOOLTIP = 8;
    static final int HANDLER_MSG_CLICKED_LAYOUT_CONTROL = 9;
    static final int HANDLER_MSG_THUMBNAIL_AREA_REARRANGE = 10;
    static final int HANDLER_MSG_REFRESH_THUMBNAIL_WITH_PAGE = 11;
    static final int HANDLER_MSG_TRY_SHOW_TEXT_EDIT_TUTORIAL = 14;

    @Override
    public void handleMessage(Message msg) {
        if (msg == null)
            return;

        try {
            switch (msg.what) {
                case HANDLER_MSG_TRY_SHOW_TEXT_EDIT_TUTORIAL:
                    handleTryShowTextEditTutorial();
                    break;

                case HANDLER_MSG_THUMBNAIL_AREA_REARRANGE:
                    handleOnRearrange(msg);
                    break;

                case HANDLER_MSG_REFRESH_CHANGED_PHOTO:
                    refreshChangedPhoto();
                    break;

                case HANDLER_MSG_UNLOCK_ROTATE_BLOCK:
                    handleUnlockRotateBlock();
                    break;

                case HANDLER_MSG_INIT_CANVAS_MATRIX:
                    handleInitCanvasMatrix();
                    break;

                case HANDLER_MSG_REFRESH_THUMBNAIL:
                    refreshPageThumbnail();
                    break;

                case HANDLER_MSG_NOTIFY_ORIENTATION_STATE:
                    notifyOrientationState();
                    break;

                case HANDLER_MSG_REFRESH_DRAG_VIEW:
                    handleRefreshDragView();
                    break;

                case HANDLER_MSG_SHOW_POPOVER_VIEW:
                    handleShowPopoverView(msg);
                    break;

                case HANDLER_MSG_SHOW_IMG_EMPTY_LAYOUT_CONTROL_TOOLTIP:
                    handleShowImgEmptyLayoutControlTooltip(msg);
                    break;

                case HANDLER_MSG_CLICKED_LAYOUT_CONTROL:
                    handleClickedLayoutControlAfterRotatedImageView(msg);
                    break;

                case HANDLER_MSG_REFRESH_THUMBNAIL_WITH_PAGE:
                    handleBaseRefreshPageThumbnail(msg.arg1);
                    break;

                case HANDLER_MSG_UPLOAD_THUMB_IMAGES: {
                    Object msgObj = msg.obj;
                    if (msgObj != null && msgObj instanceof MyPhotoSelectImageData)
                        SnapsOrderManager.uploadThumbImgOnBackground((MyPhotoSelectImageData) msgObj);
                    else
                        SnapsOrderManager.uploadThumbImgListOnBackground();
                    break;
                }

                case HANDLER_MSG_UPLOAD_ORG_IMAGES:
                    SnapsOrderManager.uploadOrgImgOnBackground();
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    //플러스 버튼이 회전하고 원상 복귀 된 상태에서 호출 된다
    private void handleClickedLayoutControlAfterRotatedImageView(Message msg) {
        if (msg == null || msg.obj == null || !(msg.obj instanceof SnapsLayoutControl)) return;
        try {
            SnapsOrderManager.cancelCurrentImageUploadExecutor();

            OrientationManager.fixCurrentOrientation(getActivity());
            dismissPopOvers();
            dismissBackgroundToolBox();

            Intent in = new Intent(getActivity(), ImageSelectActivityV2.class);

            int recommendWidth = 0, recommendHeight = 0;
            SnapsLayoutControl control = (SnapsLayoutControl) msg.obj;
            Rect rect = ResolutionUtil.getEnableResolution(getTemplate().info.F_PAGE_MM_WIDTH, getTemplate().info.F_PAGE_PIXEL_WIDTH, control);
            if (rect != null) {
                recommendWidth = rect.right;
                recommendHeight = rect.bottom;
            }

            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setHomeSelectProduct(Config.SELECT_SINGLE_CHOOSE_TYPE)
                    .setRecommendWidth(recommendWidth)
                    .setRecommendHeight(recommendHeight)
                    .setOrientationChanged(true).create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            in.putExtras(bundle);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            getActivity().startActivityForResult(in, REQ_PHOTO);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void handleUnlockRotateBlock() throws Exception {
        if (isLockPortraitOrientation()) {
            return;
        }
        getOrientationManager().setBlockRotate(false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
        getOrientationManager().setEnableOrientationSensor(true);
    }

    private void handleInitCanvasMatrix() throws Exception {
        if (snapsProductEditInfo.getInitedCanvasIdx() == getCurrentPageIndex()) return;
        snapsProductEditInfo.setInitedCanvasIdx(getCurrentPageIndex());

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null)
            centerPager.initCanvasMatrix();
    }

    private void handleRefreshDragView() throws Exception {
        BaseEditActivityThumbnailAdapter thumbnailAdapter = snapsProductEditControls.getThumbnailAdapter();
        if (thumbnailAdapter != null) {
            try {
                thumbnailAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private void handleShowPopoverView(Message msg) throws Exception {
        int position = msg.arg1;
        View view = (View) msg.obj;
        showBottomThumbnailPopOverView(view, position);
    }

    private void handleShowImgEmptyLayoutControlTooltip(Message msg) throws Exception {
        int controlId = msg.arg1;
        if (controlId >= 0) {
            RelativeLayout rootView = getEditControls().getRootView();
            if (rootView != null) {
                View targetView = (View) rootView.findViewById(controlId);
                showImageEmptyLayoutControlTooltip(targetView);
            }
        }
    }

    @Override
    public abstract void showBottomThumbnailPopOverView(View offsetView, int position);

    final void handleShowBottomThumbnailPopOverView(View offsetView, int position) {
        try {
            snapsProductEditBaseHandler.handleShowBottomThumbnailPopOverView(offsetView, position);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void showGalleryPopOverView(final View view, final int position);

    final void handleShowGalleryPopOverView(final View view, final int position) {
        try {
            snapsProductEditBaseHandler.handleShowGalleryPopOverView(view, position);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void changeCover(String xmlPath) {
        try {
            snapsProductEditBaseHandler.handleChangeCover(xmlPath);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void setPhotoResolutionEnableWithLayoutControl(SnapsLayoutControl newControl) {
        try {
            ResolutionUtil.isEnableResolution(Float.parseFloat(getTemplate().info.F_PAGE_MM_WIDTH), Integer.parseInt(getTemplate().info.F_PAGE_PIXEL_WIDTH), newControl);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void addPage(String xmlPath) {
        try {
            snapsProductEditBaseHandler.handleAddPage(xmlPath);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void changePage(String xmlPath) {
        try {
            snapsProductEditBaseHandler.handleChangePage(xmlPath);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void setPageThumbnailFail(final int pageIdx) {
        if (getSnapsPageCaptureListener() != null)
            getSnapsPageCaptureListener().onFinishPageCapture(false);

        // 안내문구 화면 구성 실패..
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
                offerQueue(pageIdx, pageIdx);
//				refreshPageThumbnail();
                refreshPageThumbnailsAfterDelay();
            }
        });
    }

    private boolean isCreatedThumbnailPageOnPageList(int idx) {
        try {
            PhotobookCommonUtils.isCreatedThumbnailPageOnPageList(getPageList(), idx);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    @Override
    synchronized public void setPageThumbnail(final int pageIdx, String filePath) {
        try {
            snapsProductEditBaseHandler.handleBasePageThumbnail(pageIdx, filePath);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    // 복구 완료.
    final protected void finishRecovery() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null || !saveMan.isRecoveryMode())
            return;

        // 지워진 사진이 있는 지 체크
        if (saveMan.isMissingImgFile()) {
            MessageUtil.alert(getActivity(), R.string.missing_saved_file);
            saveMan.setMissingImgFile(false);
        }

        // 마지막으로 편집하던 페이지에 포커스..
        requestPagerFocusLastEditedPageIdx();

        saveMan.setRecoveryMode(false);
    }

    private void requestPagerFocusLastEditedPageIdx() {
        snapsProductEditBaseHandler.handleRequestPagerFocusLastEditedPageIdx();
    }

    @Override
    public void refreshChangedPhoto() {
        handleRefreshChangePhoto();
    }

    private void handleRefreshChangePhoto() {
        try {
            snapsProductEditBaseHandler.handleRefreshChangePhoto();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected final void refreshImageWithPageIndexList(ArrayList<Integer> changeList) {
        try {
            snapsProductEditBaseHandler.handleRefreshImageWithPageIndexList(changeList);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public SnapsTemplate loadTemplate(String url) {
        String templetePath = getActivity().getIntent().getStringExtra("templete");

        if (templetePath != null && templetePath.length() > 0) {
            return SnapsTemplateManager.getInstance().getSnapsTemplate();
        } else {
            SnapsTemplate snapsTemplate = null;
            if (Const_PRODUCT.isNewYearsCardProduct()) {
                snapsTemplate = GetTemplateLoad.getNewYearsCardTemplate(url, snapsProductEditInfo.IS_EDIT_MODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            } else {
                snapsTemplate = GetTemplateLoad.getThemeBookTemplate(url, snapsProductEditInfo.IS_EDIT_MODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }
            SnapsTemplateManager.getInstance().setSnapsTemplate(snapsTemplate);

            return snapsTemplate;
        }
    }

    @Override
    public abstract void initPaperInfoOnLoadedTemplate(SnapsTemplate template);

    protected final void handleInitPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        try {
            PhotobookCommonUtils.initPaperInfoOnLoadedTemplate(template);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template);

    protected final void handleBaseImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        PhotobookCommonUtils.imageRange(template, snapsProductEditInfo.getGalleryList());
    }

    @Override
    public void initFrameIdOnLoadedTemplate(SnapsTemplate template) {
        if (template != null && template.info != null
                && "".equals(template.info.F_FRAME_ID) && Config.getFRAME_ID() != null && !"".equals(Config.getFRAME_ID()))
            template.info.F_FRAME_ID = Config.getFRAME_ID();
    }

    @Override
    public abstract void appendAddPageOnLoadedTemplate(SnapsTemplate template);

    protected final void handleBaseAppendAddPageOnLoadedTemplate(SnapsTemplate template) {
        if (getActivity() != null && getActivity().getIntent() != null) {
            // add page가 있으면 추가를 한다.
            ArrayList<Integer> addIndex = getActivity().getIntent().getIntegerArrayListExtra(EXTRA_NAME_ADD_PAGE_INDEX_LIST);
            if (addIndex != null) {
                for (int idx : addIndex) {
                    SnapsPage page = template.getPages().get(idx);

                    SnapsPage addPage = page.copyPage(template.getPages().size());
                    template.getPages().add(addPage);
                    Dlog.d("handleBaseAppendAddPageOnLoadedTemplate() page index:" + idx);
                    snapsProductEditInfo.setPageAddIndex(idx);
                }
            }
        }
    }

    protected void initSelectedImageListOnLoadedTemplate(SnapsTemplate template) {
        // 이미지 삽입..차례대
        if (snapsProductEditInfo.getGalleryList() == null || snapsProductEditInfo.getGalleryList().size() == 0)
            snapsProductEditInfo.setGalleryList(template.myphotoImageList);

        // save.xml를 로드한 경우 정렬을 하지 않는다. 자동 저장 복구 모드일 때도 정렬 안 함.
        if (snapsProductEditInfo.IS_EDIT_MODE() || AutoSaveManager.isAutoSaveRecoveryMode()) {
            PhotobookCommonUtils.imageRange2(template);
        } else {
            initImageRangeInfoOnLoadedTemplate(template);
        }
    }

    protected boolean initLoadedTemplateInfo(SnapsTemplate template) {
        preHandleLoadedTemplateInfo(template);

        initPaperInfoOnLoadedTemplate(template);

        initFrameIdOnLoadedTemplate(template);

        appendAddPageOnLoadedTemplate(template);

        initHiddenPageOnLoadedTemplate(template);

        initSelectedImageListOnLoadedTemplate(template);

        template.info.F_ACTIVITY = "Product Code (" + Config.getPROD_CODE() + ")";

        return isSuccessInitializeTemplate(template);
    }

    @Override
    public abstract boolean isSuccessInitializeTemplate(SnapsTemplate template);

    public boolean checkBaseSuccessInitializeTemplate(SnapsTemplate template) {
        return template != null;
    }

    protected void showEditActivityTutorial() {
        snapsProductEditBaseHandler.handleShowEditActivityTutorial();
    }

    protected final void handlePinchZoomTutorialOnClose() {
        snapsProductEditBaseHandler.handlePinchZoomTutorialOnClose();
    }

    protected void showTextEditTutorial() {
        try {
            snapsProductEditBaseHandler.handleShowTextControlTutorial();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    //FIXME 이거 리펙토링 좀 하자..
    @Override
    public void getTemplateHandler(final String _url) {
        ATask.executeBoolean(new ATask.OnTaskResult() {
            @Override
            public void onPre() {
                SnapsTimerProgressView.showProgress(getActivity(),
                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
                        getString(R.string.templete_data_downloaing));
            }

            @Override
            public boolean onBG() {
                //화면이 회전하는 시간을 벌어준다
                if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }

                snapsProductEditInfo.setSnapsTemplate(getTemplate(_url));

                if (getTemplate() == null) return false;

                // 템플릿을 로드한후 처리..
                setTemplateBaseInfo();

                //삭제 된 파일이 있는 지 검사하는 로직인데, 속도가 좀 느려서 테스트 시에는 그냥 건나 뛴다.
                if (Config.isRealServer()) {
                    AutoSaveManager saveMan = AutoSaveManager.getInstance();
                    if (saveMan != null && saveMan.isRecoveryMode())
                        saveMan.checkMissingImageFile(snapsProductEditInfo.IS_EDIT_MODE());
                }

                // 폰트를 다운 받는다.
                return PhotobookCommonUtils.processFontDownloading(getActivity());
            }

            @Override
            public void onPost(boolean result) {
                if (getTemplate() != null && result) {
                    try {
                        onCompleteLoadTemplate();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                        getActivity().finish();
                        SnapsAssert.assertException(getActivity(), e);
                        return;
                    }

                    loadPager();

                    initDragView();

                    SnapsTimerProgressView.destroyProgressView();
                } else {
                    // 복구에 실패하면 지워 버린다.
                    AutoSaveManager saveMan = AutoSaveManager.getInstance();
                    if (saveMan != null && saveMan.isRecoveryMode()) {
                        saveMan.finishAutoSaveMode();
                    }

                    PhotobookCommonUtils.progressUnload(getActivity());
                    getActivity().finish();
                }

                Dlog.d(Dlog.UI_MACRO, "LOAD_COMPLEATE_TEMPLEATE");
            }

            /**
             * 템플릿 로딩이 완료되었을 때 호출
             */
            private void onCompleteLoadTemplate() throws Exception {
                // 폰트 적용을 한다.
                PhotobookCommonUtils.updateUI(getActivity());

                SnapsTemplate template = getTemplate();

                // save.xml를 가져올때는 맥스페이지를 해야한다.
                snapsProductEditInfo.setPageList(getTemplate().getPages());

                //컨트롤에 페이지 아이디 부여
                refreshPagesId(getPageList());

                //레더 커버는 텍스트가 없다.
                if (getTemplate().info.F_COVER_TYPE.equals("leather")) {
                    ImageView textModifyBtn = getEditControls().getThemeTextModify();
                    if (textModifyBtn != null) {
                        textModifyBtn.setVisibility(View.GONE);
                    }
                }

                //템플릿에 클라이언트 단말기 정보 셋팅
                getTemplate().clientInfo.screendpi = String.valueOf(getActivity().getResources().getDisplayMetrics().densityDpi);
                getTemplate().clientInfo.screenresolution = SystemUtil.getScreenResolution(getActivity());
//
//                //썸네일 뷰 초기화
//                initDragView();

                //미리보기에서 사용할 목적으로 셋팅 해 놓음..
//                DataTransManager dataTransManager = DataTransManager.getInstance();
//                if (dataTransManager != null) {
//                    dataTransManager.setSnapsTemplate(getTemplate());
//                } else {
//                    DataTransManager.notifyAppFinish(getActivity());
//                    return;
//                }

                AutoSaveManager saveMan = AutoSaveManager.getInstance();
                if (saveMan == null || !saveMan.isRecoveryMode())
                    getPageList().get(0).isSelected = true;

                //자동 저장 복구 모드일 때, 자동 저장 프로젝트 파일 생성.
                if (saveMan != null) {
                    if (!saveMan.isRecoveryMode()) {
                        saveMan.exportProjectInfo();
                    }
                }

                //대표 썸네일 만드는 과정도 생략한다.
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                bundle.putBoolean("pageSave", false);
                bundle.putBoolean("pageLoad", false);
                bundle.putBoolean("preThumbnail", false);
                bundle.putBoolean("visibleButton", false);

                SnapsCanvasFragment canvasFragment = getEditControls().getCanvasFragment();
                if (canvasFragment == null) {
                    canvasFragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
                    getEditControls().setCanvasFragment(canvasFragment);
                }

                //생성이 안된다면 에러로 간주하고 다시 제작하도록 액티비티를 종료 시킨다.
                if (canvasFragment == null) {
                    Dlog.d("onCompleteLoadTemplate() canvasFragment -> null : " + Config.getPROD_CODE());
                    getActivity().finish();
                    return;
                }

                canvasFragment.setArguments(bundle);
                FragmentUtil.replce(getOrientationManager().isLandScapeMode() ? R.id.frameMain_h : R.id.frameMain_v, (FragmentActivity) getActivity(), canvasFragment);

                //사진 편집에 들어가서 Orientaion을 변경했다면, 화면 전환이 완료 되고 나서야 내용을 적용 시킨다..FIXME 이 내용은 검토가 필요 해 보인다..
                if (getOrientationChecker().isChangedPhoto()
                        && getOrientationChecker().isChangedOrientationAtImgEditor()) {
                    refreshChangedPhoto();
                    return;
                }

                initSnapsOrderManager();

                onCompleteLoadTemplateHook();

                if (!SmartSnapsManager.isSupportSmartSnapsProduct() || !SmartSnapsManager.isFirstSmartAreaSearching()) {
                    if (snapsHandler != null) {
                        snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_UNLOCK_ROTATE_BLOCK, 1000);
                    }
                }

                isInitializedTemplate = true;

                if (saveMan != null) {
                    if (saveMan.isRecoveryMode()) {
                        finishRecovery();
                    } else {
                        if (!SmartSnapsManager.isSupportSmartSnapsProduct())
                            exportAutoSaveTemplate();
                    }
                }
            }
        });
    }

    /**
     * 상품별로 화면을 고정시키고 싶을 때 true로 설정하면 화면이 회전하지 않는다.
     */
    protected boolean isLockPortraitOrientation() {
        return false;
    }

    @Override
    public abstract SnapsTemplate getTemplate(String _url);

    protected final SnapsTemplate handleGetBaseTemplate(String _url) {
        if (AutoSaveManager.isAutoSaveRecoveryMode()) {
            return recoveryTemplateFromAutoSavedFile();
        }

        SnapsTemplate snapsTemplate = loadTemplate(_url);
        return initLoadedTemplateInfo(snapsTemplate) ? snapsTemplate : null;
    }

    @Override
    public abstract SnapsTemplate recoveryTemplateFromAutoSavedFile();

    protected final SnapsTemplate handleRecoveryTemplateFromAutoSavedFile() {
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan == null || !saveMan.isRecoveryMode()) return null;

        SnapsTemplate template = saveMan.getSnapsTemplate();
        initLayoutControlsIdInTemplate(template);
        return template;
    }

    private void initLayoutControlsIdInTemplate(SnapsTemplate template) {
        try {
            if (template != null) template.initLayoutControlsIdInTemplate();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getUploadImageList() {
        return getMyPhotoSelectImageData(false);
    }

    @Override
    public SnapsOrderAttribute getSnapsOrderAttribute() {
        return new SnapsOrderAttribute.Builder()
                .setActivity(getActivity())
                .setEditMode(snapsProductEditInfo.IS_EDIT_MODE())
                .setHiddenPageList(snapsProductEditInfo.getHiddenPageList())
                .setImageList(PhotobookCommonUtils.getImageListFromTemplate(getTemplate()))
                .setPageList(snapsProductEditInfo.getPageList())
                .setPagerController(getEditControls().getLoadPager())
                .setSnapsTemplate(getTemplate())
                .setBackPageList(snapsProductEditInfo.getBackPageList())
                .setCanvasList(snapsProductEditInfo.getCanvasList())
                .setTextOptions(snapsProductEditInfo.getTextOptions())
                .create();
    }

    @Override
    public void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener) {
        setSnapsPageCaptureListener(captureListener);

        for (int ii = 0; ii < getPageList().size(); ii++) {
            offerQueue(ii, ii);
        }

        setPageThumbnail(-1, "");
    }

    @Override
    public void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener) {
        setSnapsPageCaptureListener(captureListener);

        if (!isCreatedThumbnailPageOnPageList(0)) {
            offerQueue(0, 0);
        }

        setPageThumbnail(-1, "");
    }

    @Override
    public SnapsTemplate getTemplate() {
        return snapsProductEditInfo.getSnapsTemplate();
    }

    protected SnapsTemplateInfo getSnapsTemplateInfo() {
        SnapsAssert.assertNotNull(getTemplate());
        SnapsAssert.assertNotNull(getTemplate().info);
        return getTemplate() != null ? getTemplate().info : null;
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
    public void dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            SnapsTutorialUtil.clearTooltip();
        }
    }

    public ISnapsCaptureListener getSnapsPageCaptureListener() {
        return snapsPageCaptureListener;
    }

    public void setSnapsPageCaptureListener(ISnapsCaptureListener snapsPageCaptureListener) {
        this.snapsPageCaptureListener = snapsPageCaptureListener;
    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        if (isBlockAllOnClickEvent()) return;

        if (PhotobookCommonUtils.isFromLayoutControlReceiveData(intent)) {
            boolean isLongClick = intent.getBooleanExtra("isLongClick", false);
            if (isLongClick) return;

            int control_id = intent.getIntExtra("control_id", -1);
            if (!Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                SnapsControl control = findSnapsControlWithTempImageViewId(control_id);
                if (control == null || control_id == -1) {
                    snapsProductEditInfo.setTempImageViewID(-1);
                    return;
                }

                snapsProductEditInfo.setTempImageViewID(control_id);

                boolean isOnClickBgControl = control instanceof SnapsBgControl;
                boolean isOnClickTextControl = control instanceof SnapsTextControl;

                if (isOnClickBgControl) {
                    onClickedBgControl();
                } else if (isOnClickTextControl && !Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                    onClickedTextControl(SnapsProductEditReceiveData.createReceiveData(intent, control));
                } else {
                    onClickedLayoutControl(intent);
                }
            } else {
                if (Config.isBabyNameStickerEditScreen()) {
                    onClickedBgControl();
                }
            }
        } else if (PhotobookCommonUtils.isFromTextToImageReceiveData(intent)) {
            int msgWhat = intent.getIntExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_MSG_WHAT, -1);
            int pageIndex = intent.getIntExtra(SNAPS_BROADCAST_BUNDLE_EXTRA_KEY_PAGE_INDEX, -1);
            if (pageIndex == getCurrentPageIndex()) {
                switch (msgWhat) {
                    case SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_OVER_AREA_MSG:
                        try {
                            MessageUtil.toast(getActivity(), R.string.text_server_over_area_caution_msg);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                        break;
                    case SNAPS_BROADCAST_BUNDLE_VALUE_TEXT_TO_IMAGE_SHOW_TEXT_SERVER_NETWORK_ERR_MSG:
                        try {
                            MessageUtil.toast(getActivity(), R.string.text_server_network_err_msg);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }
                        break;
                }
            }
        }
    }

    private SnapsControl findSnapsControlWithTempImageViewId(int tempImageViewID) {
        RelativeLayout rootView = getEditControls().getRootView();
        View v = null;
        if (rootView != null) {
            v = rootView.findViewById(tempImageViewID);
        }

        return PhotobookCommonUtils.getSnapsControlFromView(v);
    }

    //Todo : @Marko 리펙터링 대상
    @Override
    public void onClickedTextControl(SnapsProductEditReceiveData editReceiveData) {
        try {
            //KT 북
            if (Config.isKTBook()) {
                //실제 사용자가 클릭 했을때만 "dummy_control_id"가 존재한다.
                //사용자가 클릭 하지 않아도 기본 동작이 클릭된 상태로 동작한다.
                int dummyControlID = editReceiveData.getIntent().getIntExtra("dummy_control_id", Integer.MIN_VALUE);
                if (dummyControlID == Integer.MIN_VALUE) return;
                boolean viewInCover = editReceiveData.getIntent().getBooleanExtra("viewInCover", false);
                if (viewInCover) {
                    snapsProductEditBaseHandler.handleClickedChangeTitle();
                } else {
                    snapsProductEditBaseHandler.handleOnClickedTextControl(editReceiveData);
                }
                return;
            }

            snapsProductEditBaseHandler.handleOnClickedTextControl(editReceiveData);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

//    protected void performEditProductTitle() {
//       snapsProductEditBaseHandler.handlePerformEditOfProductTitle();
//    }

    @Override
    public abstract int getPopMenuPhotoTooltipLayoutResId(Intent intent);

    protected final int getBasePopMenuPhotoTooltipLayoutResId(Intent intent) {
        return R.layout.popmenu_photo;
    }

    public OrientationChecker getOrientationChecker() {
        return getOrientationManager().getOrientationChecker();
    }

    public int getCurrentPageIndex() {
        return getEditInfo().getCurrentPageIndex();
    }

    public View.OnClickListener getOnClickListener() {
        return this;
    }

    public void setPageProgress(DialogDefaultProgress defaultProgress) {
        snapsProductEditControls.setPageProgress(defaultProgress);
    }

    public DialogDefaultProgress getPageProgress() {
        return snapsProductEditControls.getPageProgress();
    }

    public void setSmartSnapsPageProgress(DialogSmartSnapsProgress smartSnapsPageProgress) {
        snapsProductEditControls.setSmartSnapsProgress(smartSnapsPageProgress);
    }

    public DialogSmartSnapsProgress getSmartSnapsPageProgress() {
        return snapsProductEditControls.getSmartSnapsProgress();
    }

    public SnapsProductEditInfo getEditInfo() {
        return snapsProductEditInfo;
    }

    public SnapsProductEditControls getEditControls() {
        return snapsProductEditControls;
    }

    public SnapsHandler getSnapsHandler() {
        return snapsHandler;
    }

    final void setActivityResumeFinished(boolean flag) {
        if (snapsProductEditBaseHandler != null)
            snapsProductEditBaseHandler.handleActivityResumeFinished(flag);
    }

    protected boolean isImageEditableOnlyCover() {
        return false;
    }

    @Nullable
    public View findViewById(@IdRes int id) {
        if (getActivity() == null) return null;
        return getActivity().findViewById(id);
    }

    @NonNull
    public final String getString(@StringRes int resId) {
        if (getActivity() == null) return "";
        return getActivity().getString(resId);
    }

    @Override
    public abstract Point getNoPrintToastOffsetForScreenLandscape();

    protected final Point handleGetBaseNoPrintToastOffsetForScreenLandscape() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX_LANDSCAPE, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_LANDSCAPE);
    }

    @Override
    public abstract Point getNoPrintToastOffsetForScreenPortrait();

    protected final Point handleGetBaseNoPrintToastOffsetForScreenPortrait() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX, ResolutionConstants.NO_PRINT_TOAST_OFFSETY);
    }

    @Override
    public abstract int getLastEditPageIndex();

    protected final int getBaseLastEditPageIndex() {
        if (getPageList() == null) return 0;
        for (int ii = 0; ii < getPageList().size(); ii++) {
            SnapsPage page = getPageList().get(ii);
            if (page != null && page.isSelected) {
                return ii;
            }
        }
        return 0;
    }

    public final EditActivityThumbnailRecyclerView getThumbnailRecyclerView() {
        OrientationManager orientationManager = getOrientationManager();
        return orientationManager.isLandScapeMode() ? getEditControls().getThumbnailVerticalRecyclerView() : getEditControls().getThumbnailHorizontalRecyclerView();
    }

    @Override
    public abstract void pageProgressUnload();

    protected final void handlePageProgressUnload() {
        if (snapsProductEditBaseHandler != null)
            snapsProductEditBaseHandler.dismissPageProgress();
    }

    public abstract void selectCenterPager(int position, boolean isSmoothScroll);

    protected final void handleBaseSelectCenterPager(int position, boolean isSmoothScroll) {
        if (snapsProductEditBaseHandler != null)
            snapsProductEditBaseHandler.setPageCurrentItem(position, isSmoothScroll);
    }

    @Override
    public void setPageFileOutput(int index) {
        if (getSnapsPageCaptureListener() != null) {
            getSnapsPageCaptureListener().onFinishPageCapture(true);
        }
    }

    @Override
    public void onSmartSnapsImgUploadSuccess(MyPhotoSelectImageData uploadedImageData) {
        PhotobookCommonUtils.handleOnSmartSnapsImgUploadSuccess(uploadedImageData, getActivity(), getTemplate());
    }

    @Override
    public void onSmartSnapsImgUploadFailed(MyPhotoSelectImageData uploadedImageData) {
        PhotobookCommonUtils.handleOnSmartSnapsImgUploadFailed(uploadedImageData, getActivity());
    }

    @Override
    public void onDisconnectNetwork() {
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && SmartSnapsManager.isSmartAreaSearching()) {
            try {
                suspendSmartSnapsFaceSearching();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            //LTE로 올릴 때는 사용자의 컨펌이 필요 하다
            MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.smart_snaps_search_network_disconnect_alert), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    try {
                        if (snapsProductEditBaseHandler != null)
                            snapsProductEditBaseHandler.handleFirstSmartSnapsAnimationSuspend();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });
        }
    }

    @Override
    public abstract void suspendSmartSnapsFaceSearching();

    final void handleBaseSuspendSmartSnapsFaceSearching() {
        if (!SmartSnapsManager.isSupportSmartSnapsProduct()) return;

        try {
            snapsProductEditBaseHandler.suspendSmartSnapsFaceSearching();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public abstract void onFinishedFirstSmartSnapsAnimation();

    public abstract void startSmartSearchOnEditorFirstLoad();

    void handleSmartSearchOnEditorFirstLoad() {
        if (!SmartSnapsManager.shouldSmartSnapsSearchingOnActivityCreate()) return;
        try {
            SmartSnapsManager.unlockAllSyncObjects();

            SmartSnapsManager.startSmartSnapsAutoFitImage(getDefaultSmartSnapsAnimationListener(), SmartSnapsConstants.eSmartSnapsProgressType.FIST_LOAD, 0);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    SmartSnapsAnimationListener getDefaultSmartSnapsAnimationListener() {
        return snapsProductEditBaseHandler != null ? snapsProductEditBaseHandler.getSnapsProductEditorSmartSnapsHandler() : null;
    }

    @Override
    public boolean isAddedPage() {
        try {
            return snapsProductEditBaseHandler.isAddedPage();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    boolean isBlockAllOnClickEvent() {
        return !isInitializedTemplate || SmartSnapsManager.isSmartAreaSearching();
    }

    protected void requestRemoveBlurActivityWhenWrittenText() {
        try {
            snapsProductEditBaseHandler.removeBlurActivityWhenWrittenText();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected void dismissBackgroundToolBox() {
        snapsProductEditBaseHandler.handleDismissBackgroundToolBox();
    }
}