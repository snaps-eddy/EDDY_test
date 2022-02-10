package com.snaps.mobile.activity.common.products.card_shape_product;

import android.app.Dialog;
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
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.component.SnapsNumberPicker;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.TransparencyPhotoCardActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsEditActivityTutorialUtil;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import font.FTextView;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class TransparencyPhotoCardEditor extends SnapsCardShapeProductEditor {
    private static final String TAG = TransparencyPhotoCardEditor.class.getSimpleName();

    public TransparencyPhotoCardEditor(FragmentActivity fragmentActivity) {
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
        return IAutoSaveConstants.PRODUCT_TYPE_TRANSPARENCY_PHOTO_CARD;
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_photo_card);
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
            themeTitleText.setText(getString(R.string.clear_photo_card));
            View titleTextView = dragLayer.findViewById(R.id.topView);
            if (titleTextView != null)
                titleTextView.setVisibility(View.VISIBLE);
        }

		refreshPhotoCardQuantityFloatingView(MAX_PHOTO_CARD_QUANTITY);
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

        FTextView confirmBtn = (FTextView) numperPickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_confirm_btn);
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

                    refreshPhotoCardQuantityFloatingView(MAX_PHOTO_CARD_QUANTITY);
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
        return currentTotalCardQuantity + selectedQuantity > MAX_PHOTO_CARD_QUANTITY;
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
            return Math.max(1, (MAX_PHOTO_CARD_QUANTITY - getTotalCardQuantityExcludeSelectedPage(selectedPosition)));
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return 1;
        }
    }

    private int getTotalCardQuantityExcludeSelectedPage(int pagePosition) throws Exception {
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            if (pagePosition == ii) continue;
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

                int offsetW = UIUtil.convertDPtoPX(getActivity(), 108);
                int offsetH = UIUtil.convertDPtoPX(getActivity(), 24);
                int padding = UIUtil.convertDPtoPX(getActivity(), 18);

                int imgTop = newInt[1] - padding;

                offsetView.getGlobalVisibleRect(rect);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 80);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 43);
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

                int imgH = UIUtil.convertDPtoPX(getActivity(), Const_VALUE.PHOTO_CARD_GALLERY_VIEW_HEIGHT);
                rect.offset(0, -imgH);

                int popWidth = UIUtil.convertDPtoPX(getActivity(), 68);
                int popHeight = UIUtil.convertDPtoPX(getActivity(), 52);

                int layoutId = R.layout.popmenu_photo_bottom_gallery_only_delete;

                menuGalleryView = new PopoverView(getActivity(), layoutId);
                getEditControls().setPopupMenuGalleryView(menuGalleryView);
                menuGalleryView.setContentSizeForViewInPopover(new Point(popWidth, popHeight));

                View convertView = menuGalleryView.getConvertView();
                ImageView ivArrow = (ImageView) convertView.findViewById(R.id.pop_menu_uparrow);

                int xCoordinate = (int) (popWidth * .5f);
                ivArrow.setX(xCoordinate - UIUtil.convertDPtoPX(getActivity(), 15));

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
        refreshPhotoCardQuantityFloatingView(MAX_PHOTO_CARD_QUANTITY);
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
        return new TransparencyPhotoCardActivityThumbnailAdapter(getActivity(), getEditInfo());
    }

    @Override
    public void showDesignChangeTutorial() {
        if (getEditInfo().IS_EDIT_MODE()) return;

        FrameLayout tooltipTutorialLayout = getEditControls().getTooltipTutorialLayout();
        ImageView coverModify = getEditControls().getThemeCoverModify();

        SnapsTutorialAttribute attribute = new SnapsTutorialAttribute.Builder()
                .setTooltipTutorialLayout(tooltipTutorialLayout)
                .setLandscapeMode(isLandScapeScreen())
                .setTargetView(coverModify)
                .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN)
                .create();

        SnapsEditActivityTutorialUtil.showTooltipTutorial(getActivity(), attribute);
    }

    private void showThumbnailLongClickDeleteTutorial() {
        if (getEditInfo().IS_EDIT_MODE()) return;

        SnapsTutorialAttribute attribute = null;
        try {
            FrameLayout tooltipTutorialLayout = getEditControls().getTooltipTutorialLayout();
            attribute = new SnapsTutorialAttribute.Builder()
                    .setTooltipTutorialLayout(tooltipTutorialLayout)
                    .setLandscapeMode(isLandScapeScreen())
                    .setTargetView(getThumbnailArea())
                    .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE)
                    .create();

            SnapsEditActivityTutorialUtil.showTooltipTutorial(getActivity(), attribute);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showThumbnailChangeQuantityTutorial() {
        if (getEditInfo().IS_EDIT_MODE()) return;

        SnapsTutorialAttribute attribute = null;
        try {
            FrameLayout tooltipTutorialLayout = getEditControls().getTooltipTutorialLayout();
            attribute = new SnapsTutorialAttribute.Builder()
                    .setTooltipTutorialLayout(tooltipTutorialLayout)
                    .setLandscapeMode(isLandScapeScreen())
                    .setTargetView(getThumbnailArea())
                    .setTutorialId(SnapsTutorialConstants.eTUTORIAL_ID.TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_QUANTITY)
                    .create();

            SnapsEditActivityTutorialUtil.showTooltipTutorial(getActivity(), attribute);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private View getThumbnailArea() throws Exception {
        return findViewById(isLandScapeScreen() ? R.id.activity_edit_themebook_gallery_ly_h : R.id.activity_edit_themebook_gallery_ly_v);
    }

    @Override
    public boolean isOverPageCount() {
        try {
            return getCurrentTotalCardQuantity() >= MAX_PHOTO_CARD_QUANTITY;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return true;
        }
    }

    @Override
    public void addPage() {
        addCopiedPageFromLastPage();
    }

    private void addCopiedPageFromLastPage() {
        try {
            int currentPageCount = getTemplate().getPages().size();


            int lastPageIndex = currentPageCount-1;
            int addPageIndex = currentPageCount;


            SnapsPage copiedLastPage = getTemplate().getPages().get(lastPageIndex).copyPage(addPageIndex);
            for(int i = 0; i < copiedLastPage.getLayerLayouts().size() ; i++) {
                SnapsLayoutControl  snapsControl = (SnapsLayoutControl)copiedLastPage.getLayerLayouts().get(i);
                snapsControl.isNoPrintImage = false;
            }

            if (copiedLastPage == null) {
                MessageUtil.toast(getActivity(), getString(R.string.page_add_error_msg));//"페이지 추가 중 오류가 발생했습니다.");
                return;
            }

            // 페이지 추가
            SnapsPage[] pages = { copiedLastPage};
            if (addPage(addPageIndex, pages)) {
                refreshPhotoCardQuantityFloatingView(MAX_PHOTO_CARD_QUANTITY);
                showThumbnailLongClickDeleteTutorial();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void showPageOverCountToastMessage() {
        MessageUtil.toast(getActivity(), R.string.any_more_card_msg);
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
        if (imageData != null) {
           // checkPhotoCardChangeDesignTutorial(imageData.pageIDX);
        }
    }

    @Override
    public void notifyTextControlFromIntentDataHook(SnapsTextControl control) {
        //checkPhotoCardChangeDesignTutorial(control.getPageIndex());
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();

        refreshPhotoCardQuantityFloatingView(MAX_PHOTO_CARD_QUANTITY);

        showThumbnailChangeQuantityTutorial();
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
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        if (!Config.getPROJ_CODE().equalsIgnoreCase("")) {
            Config.setPAPER_CODE(template.info.F_PAPER_CODE);
            Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
        } else {
            template.info.F_PAPER_CODE = Config.getPAPER_CODE();
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        /** 페이지 추가 개념이 없다 **/
        DataTransManager dataTransManager = DataTransManager.getInstance();
        int count =dataTransManager.getPhotoImageDataList().size();
            try {
                int currentPageCount = template.getPages().size();




                // 페이지 추가
                for(int i=0; i<count-1;i++) {
                    int lastPageIndex = currentPageCount-1;
                    int addPageIndex = currentPageCount;


                    SnapsPage copiedLastPage = template.getPages().get(lastPageIndex).copyPage(addPageIndex);
                    template.getPages().add(copiedLastPage);
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
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

    @Override
    public void handleScreenRotatedHook() {
        setCardShapeLayout();
    }

    @Override
    public int getLastEditPageIndex() {
        return 0;
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }
}
