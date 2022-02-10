package com.snaps.mobile.activity.common.products.card_shape_product;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressViewFactory;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.component.SnapsNumberPicker;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.NewYearsCardActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import font.FTextView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kimduckwon on 2017. 11. 6..
 */

public class NewYearsCardEditor extends SnapsCardShapeProductEditor {
    private static final String TAG = NewYearsCardEditor.class.getSimpleName();

    private boolean isShownTutorialForChangeQuantity = false;
    private boolean isShownTutorialForLongClickDelete = false;

    public NewYearsCardEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setCardShapeLayout();
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
        Config.setIS_OVER_LENTH_CARD_MSG(false);
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_NEWYEARS_CARD;
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    protected void initTemplateUrl() {
        try {
            getEditInfo().initTemplateUrl(getNewYearsCardTemplateUrl());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private ArrayList<String> getNewYearsCardTemplateUrl() {
        ArrayList<String> templateCode = new ArrayList<String>();
        if(getActivity().getIntent().hasExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE)) {
            templateCode = getActivity().getIntent().getStringArrayListExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE);
        }
        return templateCode;
    }

    @Override
    public void handleOnFirstResume() {
        if (getEditInfo() != null) {
            if (!getEditInfo().IS_EDIT_MODE()) {
                getEditInfo().setTemplateUrl(SnapsTemplate.getTemplateNewYearsCardUrl(getNewYearsCardTemplateUrl()));
            }
        }

        String templateUrl = getEditInfo() != null ? getEditInfo().getTemplateUrl() : "";
        getTemplateHandler(templateUrl);
    }

    @Override
    public void setCardShapeLayout() {
        View line = findViewById(R.id.horizontal_line);
        if (line != null)
            line.setVisibility(View.VISIBLE);

        RelativeLayout addPageLy = getEditControls().getAddPageLy();
        if (addPageLy != null)
            addPageLy.setVisibility(View.VISIBLE);

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.VISIBLE);

        ImageView textModify = getEditControls().getThemeTextModify();
        if (textModify != null)
            textModify.setVisibility(View.GONE);

        View galleryView = findViewById(isLandScapeScreen() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v);
        if (galleryView != null)
            galleryView.setVisibility(View.VISIBLE);

        if (getThumbnailRecyclerView() != null)
            getThumbnailRecyclerView().setVisibility(View.VISIBLE);

        FrameLayout dragLayer = (FrameLayout) findViewById(isLandScapeScreen() ? R.id.drag_layer_h : R.id.drag_layer_v);
        if (dragLayer != null) {
            photoCardCurrentCount = (TextView) dragLayer.findViewById(R.id.photo_count_current_index_tv);
            photoCardMaxCount = (TextView) dragLayer.findViewById(R.id.photo_count_current_total_count_tv);
            TextView themeTitleText = (TextView) dragLayer.findViewById(R.id.ThemeTitleText);
//            themeTitleText.setText(getString(R.string.new_years_card));
            themeTitleText.setText(getString(R.string.late_new_years_card));
            View titleTextView = dragLayer.findViewById(R.id.topView);
            if (titleTextView != null)
                titleTextView.setVisibility(View.VISIBLE);
        }

        refreshPhotoCardQuantityFloatingView(MAX_NEWYEARS_CARD_QUANTITY);
    }

    @Override
    public void onThumbnailCountViewClick(View view, int position) {
        try {
            showCountPickerDialog(view, position);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }

    }

    private void showCountPickerDialog(final View countView, final int position) throws Exception {
        int selectedPageCurrentQuantity = getPageQuantity(position);
        final Dialog numperPickerDialog = createPhotoCardCountPickerDialog(selectedPageCurrentQuantity, position);
        getEditControls().setNumperPickerDialog(numperPickerDialog);

        font.FTextView confirmBtn = (FTextView) numperPickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countView == null || !(countView instanceof FTextView)) return;
                SnapsNumberPicker numberPicker = (SnapsNumberPicker) numperPickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_number_picker);
                int selectedQuantity = numberPicker.getValue();

                if (isExceedQuantity(position, selectedQuantity)) {
                    MessageUtil.alertnoTitle(getActivity(), getString(R.string.any_more_card_msg), null);
                } else {
                    FTextView countTextView = (FTextView)countView;
                    countTextView.setText(String.valueOf(selectedQuantity));

                    setPhotoCardPageQuantity(position, selectedQuantity);

                    refreshPhotoCardQuantityFloatingView(MAX_NEWYEARS_CARD_QUANTITY);
                }

                if (numperPickerDialog != null)
                    numperPickerDialog.dismiss();
            }
        });
        numperPickerDialog.show();
    }

    private void setPhotoCardPageQuantity(int pagePosition, int selectedQuantity) {
        try {
            ArrayList<SnapsPage> pageList = getTemplate().getPages();
            SnapsPage selectPage = pageList.get(pagePosition);
            selectPage.setQuantity(selectedQuantity);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    private int getCurrentTotalCardQuantity() throws Exception {
        if (getTemplate() == null || getTemplate().getPages() == null) return 0;
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return totalQuantity;
    }

    private boolean isExceedQuantity(int pagePosition, int selectedQuantity) {
        int currentTotalCardQuantity = 0;
        try {
            currentTotalCardQuantity = getTotalCardQuantityExcludeSelectedPage(pagePosition);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            return true;
        }
        return currentTotalCardQuantity + selectedQuantity > MAX_NEWYEARS_CARD_QUANTITY;
    }

    private int getPageQuantity(int pagePosition) throws Exception {
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        return pageList.get(pagePosition).getQuantity();
    }

    private Dialog createPhotoCardCountPickerDialog(int defaultValue, int selectedPosition) throws Exception {
        Dialog pickerDialog = new Dialog(getActivity());
        pickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickerDialog.setContentView(R.layout.edit_activity_thumbnail_view_counter_picker_dialog);
        pickerDialog.setCanceledOnTouchOutside(false);

        SnapsNumberPicker numberPicker = (SnapsNumberPicker) pickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(getAddableMaxCount(selectedPosition));
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value);
            }
        });
        numberPicker.setValue(defaultValue);

        numberPicker.changeDividerColor(Color.parseColor("#eeeeee"));

        return pickerDialog;
    }

    private int getAddableMaxCount(int selectedPosition) {
        try {
            return Math.max(1, (MAX_NEWYEARS_CARD_QUANTITY - getTotalCardQuantityExcludeSelectedPage(selectedPosition)));
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return 1;
        }
    }

    private int getTotalCardQuantityExcludeSelectedPage(int pagePosition) throws Exception {
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            if (pagePosition == ii ) continue;
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return totalQuantity;
    }

    @Override
    public void onThumbnailViewClick(View view, int position) {
        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null && position != centerPager.getCurrentItem()) {
            centerPager.setCurrentItem(position);
        }
    }

    @Override
    public void onThumbnailViewLongClick(View view, int position) {
        if (getTemplate() != null && getTemplate().getPages() != null && getTemplate().getPages().size() > 1)
            showGalleryPopOverView(view, position);
    }

    @Override
    public String getDeletePageMessage() {
        return getString(R.string.delete_card_alert_msg);
    }

    @Override
    public void showBottomThumbnailPopOverView(View offsetView, int position) {
        PopoverView menuGalleryView = getEditControls().getPopupMenuGalleryView();
        if (menuGalleryView != null && menuGalleryView.isShown())
            return;

        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        pageClipBoard.setSelectedPageIndex(position);

        final Rect rect = new Rect();

        try {
            if (isLandScapeScreen()) {
                int[] newInt = new int[2];
                offsetView.getLocationInWindow(newInt);

                int offsetW = UIUtil.convertDPtoPX(getActivity(), 80);
                int offsetH = UIUtil.convertDPtoPX(getActivity(), 24);
                int padding = UIUtil.convertDPtoPX(getActivity(), 18);

                int imgTop = newInt[1] - padding;

                offsetView.getGlobalVisibleRect(rect);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 70);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);
                rect.top = imgTop;
                rect.bottom = imgTop + popHeight;

                menuGalleryView = new PopoverView(getActivity(), R.layout.popmenu_photo_left_gallery_only_delete);
                getEditControls().setPopupMenuGalleryView(menuGalleryView);

                View convertView = menuGalleryView.getConvertView();

                rect.offset(offsetW, offsetH);

                menuGalleryView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));
                //
                menuGalleryView.showPopoverFromRectInViewGroup(getEditControls().getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);
            } else {
                offsetView.getGlobalVisibleRect(rect);
                int imgW = UIUtil.convertDPtoPX(getActivity(), Const_VALUE.NEW_YEARS_CARD_GALLERY_VIEW_WDITH);
                int imgH = UIUtil.convertDPtoPX(getActivity(), Const_VALUE.NEW_YEARS_CARD_GALLERY_VIEW_HEIGHT);
                rect.offset(imgW, -imgH);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 50);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 37);

                int layoutId = R.layout.popmenu_photo_bottom_gallery_only_delete;

                menuGalleryView = new PopoverView(getActivity(), layoutId);
                getEditControls().setPopupMenuGalleryView(menuGalleryView);
                menuGalleryView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));

                View convertView = menuGalleryView.getConvertView();
                ImageView ivArrow = (ImageView) convertView.findViewById(R.id.pop_menu_uparrow);
                int arrowWdithHalf = UIUtil.convertDPtoPX(getActivity(),4);
                int xCoordinate = (int) (popWidth * .5f) - arrowWdithHalf;
                ivArrow.setX(xCoordinate);

                menuGalleryView.showPopoverFromRectInViewGroup(getEditControls().getRootView(), rect, PopoverView.PopoverArrowDirectionUp, true);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean isLackMinPageCount() {
        return getTemplate().getPages().size() <= 1;
    }

    @Override
    public void showCannotDeletePageToast(int minCount) {
        String basePageMsg = getString(R.string.make_disable_page_msg);
        MessageUtil.toast(getActivity(), String.format(basePageMsg, 1));
    }

    @Override
    public void deletePage() {
        ThemeBookClipBoard pageClipBoard = getEditControls().getPageClipBoard();
        deletePage(pageClipBoard.getSelectedPageIndex());
        refreshPhotoCardQuantityFloatingView(MAX_NEWYEARS_CARD_QUANTITY);
    }

    @Override
    public void deletePage(final int index) {
        if (getPageList() == null || getPageList().size() <= index)
            return;

        // 삭제전에 이미지 데이터가 있으면 삭제하기..
        SnapsPage selectPage = getPageList().get(index);


        SnapsOrderManager.removeBackgroundUploadOrgImagesInPage(selectPage);

        // 페이지 삭제
        getPageList().remove(selectPage);


        EditActivityThumbnailUtils thumbnailUtil = getEditControls().getThumbnailUtil();
        if (thumbnailUtil != null) {
            thumbnailUtil.sortPagesIndex(getActivity(), getCurrentPageIndex());
        }

        BaseEditActivityThumbnailAdapter thumbnailAdapter = getEditControls().getThumbnailAdapter();
        if (thumbnailAdapter != null) {
            try {
                thumbnailAdapter.notifyItemRemoved(index/2);
            } catch (Exception e) { Dlog.e(TAG, e); }
        }

        // maxpage 설정...
        getTemplate().setApplyMaxPage();
        exportAutoSaveTemplate();

        refreshList(index, getPageList().size() - 1);
    }

    @Override
    public BaseEditActivityThumbnailAdapter createThumbnailAdapter() {
        return new NewYearsCardActivityThumbnailAdapter(getActivity(), getEditInfo());
    }

    @Override
    public void showDesignChangeTutorial() {

        int topMargin = -24;
        ImageView coverModify = getEditControls().getThemeCoverModify();
        SnapsTutorialUtil.showTooltipAlways(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                .setText(getActivity().getString(R.string.guide_change_design_msg))
                .setTargetView(coverModify)
                .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                .create());
    }

    private boolean isMultiPage() {
        return getPageList() != null && getPageList().size() > 1;
    }

    private void showThumbnailLongClickDeleteTutorial() {
        if ( isShownTutorialForLongClickDelete ) return;

        if (!isMultiPage()) return; //2장 이상 있을때만 안내 한다

        try {

            int topMargin = 0;
            if ( isLandScapeScreen() ) {
                topMargin = 20;
            } else {
                topMargin = -24;
            }

            BaseEditActivityThumbnailAdapter activityThumbnailAdapter =getEditControls().getThumbnailAdapter();
            View targetView =((NewYearsCardActivityThumbnailAdapter)activityThumbnailAdapter).getFirstLayout();
            SnapsTutorialUtil.showTooltipAlways(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                    .setText(getActivity().getString(R.string.guide_delete_card_msg))
                    .setTargetView(targetView)
                    .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                    .create());

            isShownTutorialForLongClickDelete = true;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean isSuccessInitializeTemplate(SnapsTemplate template) {
            if(!divisionPageListFrontAndBack(template))
                return false;
        return super.checkBaseSuccessInitializeTemplate(template);
    }

    @Override
    public boolean isOverPageCount() {
        int maxCount = 0;
        try {
            maxCount = getMaxSelectCount();
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }
        if (maxCount == 0) {
            return true;
        }else {
            return false;
        }
    }



    @Override
    public void addPage() {
        //TODO  페이지 추가 디자인 선택 리스트로 가야한다
        int maxCount = 0;
        try {
            maxCount = getMaxSelectCount();
        }catch (Exception e) {
            Dlog.e(TAG, e);
        }
//        if (maxCount == 0) {
//            MessageUtil.toast(getActivity(),getString(R.string.any_more_card_msg));
//        } else {
            showAddProductPageActivity(maxCount);
//        }

    }

    @Override
    public void showPageOverCountToastMessage() {
        MessageUtil.toast(getActivity(), R.string.any_more_card_msg);
    }
    @Override

    public void onCompleteLoadTemplateHook() {
        refreshPhotoCardQuantityFloatingView(MAX_NEWYEARS_CARD_QUANTITY);

        showDesignChangeTutorial();
        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        showThumbnailLongClickDeleteTutorial();
    }

    @Override
    public Point getNoPrintToastOffsetForScreenLandscape() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX_LANDSCAPE_PHOTOCARD, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_LANDSCAPE_PHOTOCARD);
    }

    @Override
    public Point getNoPrintToastOffsetForScreenPortrait() {
        return new Point(ResolutionConstants.NO_PRINT_TOAST_OFFSETX_PHOTOCARD, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_PHOTOCARD);
    }

    @Override
    public void setPreviewBtnVisibleState() {
        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null) {
            previewBtn.setVisibility(View.GONE);
        }
    }

    public void handleScreenRotatedHook() {
        setCardShapeLayout();
    }

    @Override
    public int getLastEditPageIndex() {
        return 0;
    }

    @Override
    public void onClickedChangeDesign() {
        showChangeProductPageActivity();
    }


    private int getMaxSelectCount() throws Exception{

        return MAX_NEWYEARS_CARD_QUANTITY - getCurrentTotalCardQuantity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_PRODUCT_ADD_PAGE && resultCode == RESULT_OK) {
            ArrayList<String> templateCode = new ArrayList<String>();
            if (data.hasExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE)) {
                templateCode = data.getStringArrayListExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE);
            } else {}
            getAddTemplate(templateCode,true);
        } else if(requestCode == REQ_PRODUCT_CHANGE_PAGE && resultCode == RESULT_OK ) {
            ArrayList<String> templateCode = new ArrayList<String>();
            if (data.hasExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE)) {
                templateCode = data.getStringArrayListExtra(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE);
            } else {}

            getAddTemplate(templateCode,false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void getAddTemplate(final ArrayList<String> templateCode, final boolean add) {
        ATask.executeBoolean(new ATask.OnTaskResult() {
            SnapsTemplate downloadTemplate = null;
            @Override
            public void onPre() {
                SnapsTimerProgressView.showProgress(getActivity(),
                        SnapsTimerProgressViewFactory.eTimerProgressType.PROGRESS_TYPE_LOADING,
                        getString(R.string.templete_data_downloaing));
            }

            @Override
            public boolean onBG() {
                downloadTemplate = GetTemplateLoad.getNewYearsCardTemplate(SnapsTemplate.getTemplateNewYearsCardUrl(templateCode), false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                PhotobookCommonUtils.saveMaskData(downloadTemplate);
                return downloadTemplate != null;

            }

            @Override
            public void onPost(boolean result) {
                SnapsTimerProgressView.destroyProgressView();

                if (result) {
                    if (add) {
                        boolean shouldShowTutorial = addTemplatePage(downloadTemplate);
                        if (shouldShowTutorial) {
                            showThumbnailLongClickDeleteTutorial();
                        }
                        refreshPhotoCardQuantityFloatingView(MAX_NEWYEARS_CARD_QUANTITY);
                    } else {
                        changeTemplatePage(downloadTemplate);
                    }
                }
            }
        });
    }

    @Override
    public void handleAfterRefreshList(int startPageIDX, int endPageIdx) {
        int startIdx = Math.min(Math.max(0, startPageIDX), getPageList().size() - 1);
        int endIdx = Math.min(getPageList().size() - 1, endPageIdx);

        offerQueue(startIdx, endIdx);
        refreshPageThumbnail();

        InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
        if (centerPager != null)
            centerPager.setCurrentItem(startIdx, true);

        setThumbnailSelectionDragView(EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE, startIdx);
    }

    @Override
    public void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData) {
        if (imageData == null) return;
        try {
            if (isAllPhotoInsertedOnFirstLayout())
                showThumbnailChangeQuantityTutorial();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isAllPhotoInsertedOnFirstLayout() throws Exception {
        if (getPageList() == null || getPageList().isEmpty()) return false;
        SnapsPage firstPage = getPageList().get(0);
        ArrayList<SnapsControl> layers = firstPage.getLayerLayouts();
        for (SnapsControl control : layers) {
            if (!(control instanceof SnapsLayoutControl)) continue;
            SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
            if( layoutControl.type != null && layoutControl.type.equalsIgnoreCase("browse_file")) {
                if (layoutControl.imgData == null ) {
                    return false;
                }
            }
        }
        return true;
    }

    //FIXME 포토카드와 동일한 로직
    private void showThumbnailChangeQuantityTutorial() {
        if (getEditInfo().IS_EDIT_MODE() || isShownTutorialForChangeQuantity) return;

        try {
            int topMargin = 0;
            if (isLandScapeScreen()) {
                topMargin = 0;
            } else {
                topMargin = -24;
            }
            BaseEditActivityThumbnailAdapter activityThumbnailAdapter =getEditControls().getThumbnailAdapter();
            View targetView =((NewYearsCardActivityThumbnailAdapter)activityThumbnailAdapter).getFirstCountLayout();
            SnapsTutorialUtil.showTooltipAlways(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                    .setText(getActivity().getString(R.string.guide_change_count_msg))
                    .setTargetView(targetView)
                    .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                    .create());

            isShownTutorialForChangeQuantity = true;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean addTemplatePage(SnapsTemplate snapsTemplate) {
        ArrayList<SnapsPage> arrayListSnapsPage = new ArrayList<>();
        for (SnapsPage page : snapsTemplate.getPages()) {
            if (!page.type.equals("hidden")) {
                if (page != null && page.side != null && page.side.equals("back")){
                    SnapsPage snapsPage = new SnapsPage();
                    snapsPage = page;
                    getEditInfo().getBackPageList().add(snapsPage);
                } else {
                    SnapsPage snapsPage = new SnapsPage();
                    snapsPage = page;
                    arrayListSnapsPage.add(snapsPage) ;
                }
            }
        }
        SnapsPage [] snapsPages = new SnapsPage[arrayListSnapsPage.size()];
        for(int i = 0 ; i < arrayListSnapsPage.size() ; i++) {
            snapsPages[i] = arrayListSnapsPage.get(i);
        }

        return addPage(getEditInfo().getPageList().size(), snapsPages);
    }
    SnapsPage changeSnapsPage = null;
    private void changeTemplatePage(SnapsTemplate snapsTemplate) {
        changeSnapsPage = new SnapsPage();
        final int currentPosition = getEditControls().getCenterPager().getCurrentItem();
        for (SnapsPage page : snapsTemplate.getPages()) {
            if (!page.type.equals("hidden")) {
                if (page != null && page.side != null && page.side.equals("back")){
                    SnapsPage snapsPage = new SnapsPage();
                    snapsPage = page;
                    getEditInfo().getBackPageList().add(currentPosition,snapsPage);
                } else {
                    changeSnapsPage = page;
                }
            }
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                changePage(currentPosition, changeSnapsPage);
            }
        });

    }

    protected void refreshPhotoCardQuantityFloatingView(final int maxQuantity) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int currentTotalCardQuantity = getCurrentTotalCardQuantity();
                        photoCardCurrentCount.setText(String.valueOf(currentTotalCardQuantity));
                        photoCardMaxCount.setText(String.valueOf(maxQuantity));
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });


    }

}
