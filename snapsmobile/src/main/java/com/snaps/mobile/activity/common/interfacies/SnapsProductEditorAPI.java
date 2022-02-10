package com.snaps.mobile.activity.common.interfacies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsUploadResultListener;
import com.snaps.common.imp.ISnapsPageItemInterface;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.order.ISnapsCaptureListener;
import com.snaps.mobile.order.ISnapsOrderStateListener;
import com.snaps.mobile.order.order_v2.datas.SnapsImageUploadResultData;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsImageUploadStateListener;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 10. 26..
 */

public abstract class SnapsProductEditorAPI implements SnapsProductEditActivityDelegate,
        ISnapsPageItemInterface, ISnapsOrderStateListener, IPhotobookCommonConstants, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver,
        View.OnClickListener, ISnapsHandler, SnapsOrderActivityBridge, SmartSnapsUploadResultListener, SnapsImageUploadStateListener
{
    /**
     * User의 Action에 대한 Handler들
     */
    public abstract void onClickedChangePeriod();

    public abstract void onClickedInfo();

    public abstract void onClickedChangeDesign();

    public abstract void onClickedAddPage();

    public abstract void onClickedPreview();

    public abstract void onClickedChangeTitle();

    public abstract void onClickedImageEdit();

    public abstract void onClickedImageChange();

    public abstract void onClickedImageRemove();

    public abstract void onClickedSaveBasket();

    public abstract void onClickedBgControl();

    public abstract void onClickedTextControl(SnapsProductEditReceiveData editReceiveData);

    public abstract void onClickedLayoutControl(Intent intent);

    public abstract void handleCenterPagerSelected();

    public abstract void onThumbnailViewClick(View view, int position);
    public abstract void onThumbnailCountViewClick(View view, int position);
    public abstract void onThumbnailViewLongClick(View view, int position);

    public abstract void handleOnFirstResume();

    public abstract void onCenterPagerSelected(int page);

    public abstract void onRearrange(final View view, final int oldIndex, final int newIndex);

    /**
     * 편집 정보에 대한 각 상품 별 예외 처리
     */
    public abstract int getAutoSaveProductCode();

    public abstract Rect getQRCodeRect();

    public abstract int getLastEditPageIndex();

    public abstract boolean isLackMinPageCount();

    public abstract String getDeletePageMessage();

    public abstract void exportAutoSaveTemplate();

    public abstract SnapsTemplate recoveryTemplateFromAutoSavedFile();

    public abstract ArrayList<String> getPageThumbnailPaths();

    public abstract int getPopMenuPhotoTooltipLayoutResId(Intent intent);

    public abstract void refreshSelectedNewImageData(MyPhotoSelectImageData newImageData, SnapsLayoutControl control);

    public abstract void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData);

    public abstract void notifyTextControlFromIntentDataHook(SnapsTextControl control);

    public abstract void handleAfterRefreshList(int startPageIDX, int endPageIdx);

    public abstract void addPage();

    public abstract void addPage(int index);

    public abstract boolean addPage(int pageIDX, SnapsPage... pages);

    public abstract void changePage(int pageIDX, SnapsPage pages);

    public abstract void deletePage();

    public abstract void deletePage(final int index);

    public abstract void refreshList(final int startPageIDX, final int endPageIdx);

    public abstract void refreshPageThumbnail();

    public abstract void refreshPageThumbnail(int page, long delay);

    public abstract boolean isOverPageCount();

    public abstract SnapsProductEditInfo getEditInfo();

    public abstract void selectCenterPager(int position, boolean isSmoothScroll);

    @Override
    public abstract void setPageThumbnail(int pageIdx, String filePath);

    @Override
    public abstract void setPageThumbnailFail(int pageIdx);

    @Override
    public abstract void requestMakeMainPageThumbnailFile(ISnapsCaptureListener captureListener);

    @Override
    public abstract void requestMakePagesThumbnailFile(ISnapsCaptureListener captureListener);

    public abstract boolean isAddedPage();

    /**
     * 각 상품 별 UI 에외 처리를 위한.
     */
    public abstract void initControlVisibleStateOnActivityCreate();

    public abstract void showAddStickToastMsg();

    public abstract void showCoverSpineDeletedToastMsg();

    public abstract void showAddPageActivity();

    public abstract Point getNoPrintToastOffsetForScreenLandscape();

    public abstract Point getNoPrintToastOffsetForScreenPortrait();

    public abstract void setPreviewBtnVisibleState();

    public abstract BaseEditActivityThumbnailAdapter createThumbnailAdapter();

    public abstract void setThumbnailSelectionDragView(int pageChangeType, int page);

    public abstract void showDesignChangeTutorial();

    public abstract void handleScreenRotatedHook();

    public abstract void setActivityContentView();

    public abstract void setCardShapeLayout();

    public abstract void showBottomThumbnailPopOverView(View offsetView, int position);

    public abstract void showCannotDeletePageToast(int minCount);

    public abstract void showPageOverCountToastMessage();

    public abstract void showGalleryPopOverView(final View view, final int position);

    public abstract void setPhotoResolutionEnableWithLayoutControl(SnapsLayoutControl newControl);

    public abstract void refreshChangedPhoto();

    public abstract void refreshUI();

    public abstract void setPageFileOutput(int index);

    public abstract void pageProgressUnload();

    public abstract void showPageProgress();

    @Override
    public abstract void onPageLoadComplete(int page);

    @Override
    public abstract void handleMessage(Message msg);

    @Override
    public abstract void onOrderStateChanged(int state);

    public abstract boolean isTwinShapeBottomThumbnail();

    /**
     *  각 상품별 템플릿 정보 처리를 위한
     */
    public abstract void initEditInfoBeforeLoadTemplate();

    public abstract void initHiddenPageOnLoadedTemplate(SnapsTemplate template);

    public abstract void preHandleLoadedTemplateInfo(SnapsTemplate template);

    public abstract void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template);

    public abstract void onCompleteLoadTemplateHook();

    public abstract void setTemplateBaseInfo();

    public abstract void appendAddPageOnLoadedTemplate(SnapsTemplate template);

    public abstract boolean isSuccessInitializeTemplate(SnapsTemplate template);

    public abstract SnapsTemplate getTemplate(String _url);

    public abstract void initPaperInfoOnLoadedTemplate(SnapsTemplate template);

    public abstract void initFrameIdOnLoadedTemplate(SnapsTemplate template);

    public abstract void getTemplateHandler(final String url);

    public abstract SnapsTemplate loadTemplate(String url);

    @Override
    public abstract SnapsTemplate getTemplate();

    /**
     * 기타...
     */
    @Override
    public abstract void onConfigurationChanged(Configuration newConfig);
    @Override
    public abstract void onBackPressed();
    @Override
    public abstract void onCreate();
    @Override
    public abstract void onResume();
    @Override
    public abstract void onPause();
    @Override
    public abstract void onStop();
    @Override
    public abstract void onDestroy();
    @Override
    public abstract void dispatchTouchEvent(MotionEvent ev);
    @Override
    public abstract void onClick(View v);
    @Override
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
    @Override
    public abstract ArrayList<MyPhotoSelectImageData> getUploadImageList();
    @Override
    public abstract SnapsOrderAttribute getSnapsOrderAttribute();
    @Override
    public abstract void onReceiveData(Context context, Intent intent);
    @Override
    public abstract void onOrgImgUploadStateChanged(SnapsImageUploadListener.eImageUploadState state, SnapsImageUploadResultData resultData);
    @Override
    public abstract void onUploadFailedOrgImgWhenSaveToBasket() throws Exception;

    //스마트 스냅스 관련
    @Override
    public abstract void onSmartSnapsImgUploadSuccess(MyPhotoSelectImageData uploadedImageData);
    @Override
    public abstract void onSmartSnapsImgUploadFailed(MyPhotoSelectImageData uploadedImageData);

    public abstract void suspendSmartSnapsFaceSearching();

    public abstract void onFinishedFirstSmartSnapsAnimation();

    public abstract void startSmartSearchOnEditorFirstLoad();

    public abstract boolean shouldSmartSnapsAnimateOnActivityStart();
}
