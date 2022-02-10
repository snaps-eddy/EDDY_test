package com.snaps.mobile.activity.common.data;

import android.app.Dialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.mobile.activity.edit.PagerContainer;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.pager.SnapsPagerController2;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.edit.view.DialogSmartSnapsProgress;
import com.snaps.mobile.activity.edit.view.SnapsClippingDimLayout;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.customview.EditActivityThumbnailRecyclerView;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.text_animation.HTextView;

import font.FTextView;

/**
 * Created by ysjeong on 2017. 10. 12..
 * @Marko
 * 공통 사용되는 뷰들을 핸들링 하는 것으로 이해하면 되려나 ..
 */

public class SnapsProductEditControls {

    private SnapsCanvasFragment canvasFragment = null;
    private InterceptTouchableViewPager centerPager = null;
    private SnapsPagerController2 loadPager = null;

    private PagerContainer container = null;

    //Controls
    private PopoverView popupMenuView = null;
    private PopoverView popupMenuGalleryView = null;
    private RelativeLayout rootView = null;
    private RelativeLayout addPageLy = null;
    private ImageView themeTextModify = null;
    private ImageView themeCoverModify = null;
    private ImageView calendarPeriodModify = null;
    private ImageView themeInfo = null;
    private CustomizeDialog confirmDialog = null;
    private ImageView themePreviewBtn = null;
    private TextView cartTxt = null;
    private FrameLayout tooltipTutorialLayout = null;
    private Dialog numperPickerDialog = null;

    //하단 썸네일 관련
    private EditActivityThumbnailRecyclerView thumbnailVerticalRecyclerView = null;
    private EditActivityThumbnailRecyclerView thumbnailHorizontalRecyclerView = null;

    private BaseEditActivityThumbnailAdapter thumbnailAdapter = null;
    private ThemeBookClipBoard pageClipBoard = null;
    private ItemTouchHelper verticalItemTouchHelper = null;
    private ItemTouchHelper horizontalItemTouchHelper = null;

    private EditActivityThumbnailUtils thumbnailUtil;

    private DialogDefaultProgress pageProgress;
    private DialogSmartSnapsProgress smartSnapsProgress;

    private ProgressBar smartSnapsSearchProgressBar = null;
    private SnapsClippingDimLayout smartSnapsSearchProgressDimLayout = null;
    private HTextView smartSnapsSearchProgressTitleText = null;
    private FTextView smartSnapsSearchProgressValueText = null;
    private FTextView smartSnapsSearchCancelText = null;

    //상단 백그라운드이미지 툴 (씰 스티커 참조)
    private View toggleBtnBackgroundToolbox = null;
    private View viewBackgroundToolbox = null;
    private View viewOutsideBackgroundToolbox = null;
    private View btnBackgroundToolChangeSource = null;
    private View btnBackgroundToolEdit = null;
    private View btnBackgroundToolDelete = null;

    public EditActivityThumbnailUtils getThumbnailUtil() {
        return thumbnailUtil;
    }

    public void setThumbnailUtil(EditActivityThumbnailUtils thumbnailUtil) {
        this.thumbnailUtil = thumbnailUtil;
    }

    private SnapsProductEditControls() {
        init();
    }

    public static SnapsProductEditControls createInstance() {
        return new SnapsProductEditControls();
    }

    private void init() {
        setThumbnailUtil(new EditActivityThumbnailUtils());
    }

    public SnapsCanvasFragment getCanvasFragment() {
        return canvasFragment;
    }

    public void setCanvasFragment(SnapsCanvasFragment canvasFragment) {
        this.canvasFragment = canvasFragment;
    }

    public InterceptTouchableViewPager getCenterPager() {
        return centerPager;
    }

    public void setCenterPager(InterceptTouchableViewPager centerPager) {
        this.centerPager = centerPager;
    }

    public SnapsPagerController2 getLoadPager() {
        return loadPager;
    }

    public void setLoadPager(SnapsPagerController2 loadPager) {
        this.loadPager = loadPager;
    }

    public PagerContainer getContainer() {
        return container;
    }

    public void setContainer(PagerContainer container) {
        this.container = container;
    }

    public EditActivityThumbnailRecyclerView getThumbnailVerticalRecyclerView() {
        return thumbnailVerticalRecyclerView;
    }

    public void setThumbnailVerticalRecyclerView(EditActivityThumbnailRecyclerView thumbnailVerticalRecyclerView) {
        this.thumbnailVerticalRecyclerView = thumbnailVerticalRecyclerView;
    }

    public EditActivityThumbnailRecyclerView getThumbnailHorizontalRecyclerView() {
        return thumbnailHorizontalRecyclerView;
    }

    public void setThumbnailHorizontalRecyclerView(EditActivityThumbnailRecyclerView thumbnailHorizontalRecyclerView) {
        this.thumbnailHorizontalRecyclerView = thumbnailHorizontalRecyclerView;
    }

    public BaseEditActivityThumbnailAdapter getThumbnailAdapter() {
        return thumbnailAdapter;
    }

    public void setThumbnailAdapter(BaseEditActivityThumbnailAdapter thumbnailAdapter) {
        this.thumbnailAdapter = thumbnailAdapter;
    }

    public ThemeBookClipBoard getPageClipBoard() {
        return pageClipBoard;
    }

    public void setPageClipBoard(ThemeBookClipBoard pageClipBoard) {
        this.pageClipBoard = pageClipBoard;
    }

    public ItemTouchHelper getVerticalItemTouchHelper() {
        return verticalItemTouchHelper;
    }

    public void setVerticalItemTouchHelper(ItemTouchHelper verticalItemTouchHelper) {
        this.verticalItemTouchHelper = verticalItemTouchHelper;
    }

    public ItemTouchHelper getHorizontalItemTouchHelper() {
        return horizontalItemTouchHelper;
    }

    public void setHorizontalItemTouchHelper(ItemTouchHelper horizontalItemTouchHelper) {
        this.horizontalItemTouchHelper = horizontalItemTouchHelper;
    }

    public PopoverView getPopupMenuView() {
        return popupMenuView;
    }

    public void setPopupMenuView(PopoverView popupMenuView) {
        this.popupMenuView = popupMenuView;
    }

    public PopoverView getPopupMenuGalleryView() {
        return popupMenuGalleryView;
    }

    public void setPopupMenuGalleryView(PopoverView popupMenuGalleryView) {
        this.popupMenuGalleryView = popupMenuGalleryView;
    }

    public RelativeLayout getRootView() {
        return rootView;
    }

    public void setRootView(RelativeLayout rootView) {
        this.rootView = rootView;
    }

    public RelativeLayout getAddPageLy() {
        return addPageLy;
    }

    public void setAddPageLy(RelativeLayout addPageLy) {
        this.addPageLy = addPageLy;
    }

    public ImageView getThemeTextModify() {
        return themeTextModify;
    }

    public void setThemeTextModify(ImageView themeTextModify) {
        this.themeTextModify = themeTextModify;
    }

    public ImageView getThemeCoverModify() {
        return themeCoverModify;
    }

    public void setThemeCoverModify(ImageView themeCoverModify) {
        this.themeCoverModify = themeCoverModify;
    }

    public ImageView getCalendarPeriodModify() {
        return calendarPeriodModify;
    }

    public void setCalendarPeriodModify(ImageView calendarPeriodModify) {
        this.calendarPeriodModify = calendarPeriodModify;
    }

    public ImageView getThemeInfo() {
        return themeInfo;
    }

    public void setThemeInfo(ImageView themeInfo) {
        this.themeInfo = themeInfo;
    }

    public CustomizeDialog getConfirmDialog() {
        return confirmDialog;
    }

    public void setConfirmDialog(CustomizeDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    public ImageView getThemePreviewBtn() {
        return themePreviewBtn;
    }

    public void setThemePreviewBtn(ImageView themePreviewBtn) {
        this.themePreviewBtn = themePreviewBtn;
    }

    public TextView getCartTxt() {
        return cartTxt;
    }

    public void setCartTxt(TextView cartTxt) {
        this.cartTxt = cartTxt;
    }

    public FrameLayout getTooltipTutorialLayout() {
        return tooltipTutorialLayout;
    }

    public void setTooltipTutorialLayout(FrameLayout tooltipTutorialLayout) {
        this.tooltipTutorialLayout = tooltipTutorialLayout;
    }

    public Dialog getNumperPickerDialog() {
        return numperPickerDialog;
    }

    public void setNumperPickerDialog(Dialog numperPickerDialog) {
        this.numperPickerDialog = numperPickerDialog;
    }

    public DialogDefaultProgress getPageProgress() {
        return pageProgress;
    }

    public void setPageProgress(DialogDefaultProgress pageProgress) {
        this.pageProgress = pageProgress;
    }

    public ProgressBar getSmartSnapsSearchProgressBar() {
        return smartSnapsSearchProgressBar;
    }

    public void setSmartSnapsSearchProgressBar(ProgressBar smartSnapsSearchProgressBar) {
        this.smartSnapsSearchProgressBar = smartSnapsSearchProgressBar;
    }

    public HTextView getSmartSnapsSearchProgressTitleText() {
        return smartSnapsSearchProgressTitleText;
    }

    public void setSmartSnapsSearchProgressTitleText(HTextView smartSnapsSearchProgressTitleText) {
        this.smartSnapsSearchProgressTitleText = smartSnapsSearchProgressTitleText;
    }

    public FTextView getSmartSnapsSearchProgressValueText() {
        return smartSnapsSearchProgressValueText;
    }

    public void setSmartSnapsSearchProgressValueText(FTextView smartSnapsSearchProgressValueText) {
        this.smartSnapsSearchProgressValueText = smartSnapsSearchProgressValueText;
    }

    public FTextView getSmartSnapsSearchCancelText() {
        return smartSnapsSearchCancelText;
    }

    public void setSmartSnapsSearchCancelText(FTextView smartSnapsSearchCancelText) {
        this.smartSnapsSearchCancelText = smartSnapsSearchCancelText;
    }

    public DialogSmartSnapsProgress getSmartSnapsProgress() {
        return smartSnapsProgress;
    }

    public void setSmartSnapsProgress(DialogSmartSnapsProgress smartSnapsProgress) {
        this.smartSnapsProgress = smartSnapsProgress;
    }

    public SnapsClippingDimLayout getSmartSnapsSearchProgressDimLayout() {
        return smartSnapsSearchProgressDimLayout;
    }

    public void setSmartSnapsSearchProgressDimLayout(SnapsClippingDimLayout smartSnapsSearchProgressDimLayout) {
        this.smartSnapsSearchProgressDimLayout = smartSnapsSearchProgressDimLayout;
    }

    /**
     * 씰 스티커 상단 툴 박스 뷰 Getter/Setter
     */
    public View getToggleBtnBackgroundToolbox() {
        return toggleBtnBackgroundToolbox;
    }

    public void setToggleBtnBackgroundToolbox(View view) {
        this.toggleBtnBackgroundToolbox = view;
    }

    public View getViewBackgroundToolbox() {
        return viewBackgroundToolbox;
    }

    public void setViewBackgroundToolbox(@Nullable View view) {
        this.viewBackgroundToolbox = view;
    }

    public View getViewOutsideBackgroundToolbox() {
        return viewOutsideBackgroundToolbox;
    }

    public void setViewOutsideBackgroundToolbox(@Nullable View view) {
        this.viewOutsideBackgroundToolbox = view;
    }

    public View getBtnBackgroundToolChangeSource() {
        return btnBackgroundToolChangeSource;
    }

    public void setBtnBackgroundToolChangeSource(@Nullable View view) {
        this.btnBackgroundToolChangeSource = view;
    }

    public View getBtnBackgroundToolEdit() {
        return btnBackgroundToolEdit;
    }

    public void setBtnBackgroundToolEdit(@Nullable View view) {
        this.btnBackgroundToolEdit = view;
    }

    public View getBtnBackgroundToolDelete() {
        return btnBackgroundToolDelete;
    }

    public void setBtnBackgroundToolDelete(@Nullable View view) {
        this.btnBackgroundToolDelete = view;
    }
    /**
     * end 씰 스티커 상단 툴 박스 뷰 Getter/Setter
     */
}
