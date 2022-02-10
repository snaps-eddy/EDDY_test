package com.snaps.mobile.activity.common.products.multi_page_product;

import android.app.Dialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICommonConfirmListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorCommonImplement;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.ThemeBookClipBoard;
import com.snaps.mobile.activity.themebook.adapter.PopoverView;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.component.SnapsNumberPicker;
import com.snaps.mobile.edit_activity_tools.adapter.BaseEditActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.NewYearsCardActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.adapter.SloganActivityThumbnailAdapter;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

import errorhandle.SnapsAssert;
import font.FTextView;

import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER_2020;

/**
 * Created by kimduckwon on 2018. 1. 15..
 */

public abstract class SnapsCounterPageEditor extends SnapsProductBaseEditorCommonImplement {
    private static final String TAG = SnapsCounterPageEditor.class.getSimpleName();

    private boolean isShownTutorialForChangeQuantity = false;
    private boolean isShownTutorialForLongClickDelete = false;
    private Dialog numberPickerDialog = null;
    TextView currentCount;
    TextView maxCount;
    public SnapsCounterPageEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setShapeLayout();
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
        Config.setIS_OVER_LENTH_CARD_MSG(false);
    }


    @Override
    public abstract int getAutoSaveProductCode();
    @Override
    public abstract void setActivityContentView();

    public abstract int setMaxQuantity();

    public abstract String setTitle();

    public abstract boolean addTemplatePage(SnapsTemplate snapsTemplate);
    public abstract void changeTemplatePage(SnapsTemplate snapsTemplate);


    public void setShapeLayout() {
        View line = findViewById(R.id.horizontal_line);
        if (line != null)
            line.setVisibility(View.VISIBLE);

        RelativeLayout addPageLy = getEditControls().getAddPageLy();
        if (addPageLy != null)
            addPageLy.setVisibility(View.VISIBLE);

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null) {
            if(Const_PRODUCT.isExamStikerProduct()) {
                coverModify.setVisibility(View.GONE);
            } else {
                coverModify.setVisibility(View.VISIBLE);
            }

        }

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
            currentCount = (TextView) dragLayer.findViewById(R.id.photo_count_current_index_tv);
            maxCount = (TextView) dragLayer.findViewById(R.id.photo_count_current_total_count_tv);
            TextView themeTitleText = (TextView) dragLayer.findViewById(R.id.ThemeTitleText);
            themeTitleText.setText(setTitle());
            View titleTextView = dragLayer.findViewById(R.id.topView);
            if (titleTextView != null)
                titleTextView.setVisibility(View.VISIBLE);
        }

        refreshQuantityFloatingView(setMaxQuantity());
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

    @Override
    public boolean isOverPageCount() {
        int quantityLeftToChoose = getMaxSelectCount();
        return (quantityLeftToChoose == 0);
    }

    private void showCountPickerDialog(final View countView, final int position) throws Exception {
        if (numberPickerDialog != null && numberPickerDialog.isShowing()) {
            return;
        }

        int selectedPageCurrentQuantity = getPageQuantity(position);
        numberPickerDialog = PhotobookCommonUtils.createCountPickerDialog(getActivity(), selectedPageCurrentQuantity, getAddableMaxCount(position), new ICommonConfirmListener() {
            @Override
            public void onConfirmed() {
                if (countView == null || !(countView instanceof FTextView) || numberPickerDialog == null) return;
                SnapsNumberPicker numberPicker = (SnapsNumberPicker) numberPickerDialog.findViewById(R.id.edit_activity_thumbnail_view_counter_picker_dialog_number_picker);
                int selectedQuantity = numberPicker.getValue();

                if (isExceedQuantity(position, selectedQuantity)) {
                    MessageUtil.alertnoTitle(getActivity(), getString(R.string.any_more_card_msg), null);
                } else {
                    FTextView countTextView = (FTextView)countView;
                    countTextView.setText(String.valueOf(selectedQuantity));

                    setPageQuantity(position, selectedQuantity);

                    refreshQuantityFloatingView(setMaxQuantity());
                }

                if (numberPickerDialog != null)
                    numberPickerDialog.dismiss();
            }
        });
        getEditControls().setNumperPickerDialog(numberPickerDialog);
        numberPickerDialog.show();
    }

    private void setPageQuantity(int pagePosition, int selectedQuantity) {
        try {
            ArrayList<SnapsPage> pageList = getTemplate().getPages();
            SnapsPage selectPage = pageList.get(pagePosition);
            selectPage.setQuantity(selectedQuantity);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
        }
    }

    protected int getCurrentTotalQuantity() throws Exception {
        if (getTemplate() == null || getTemplate().getPages() == null) return 0;
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        int totalQuantity = 0;
        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            totalQuantity += snapsPage.getQuantity();
        }
        return totalQuantity;
    }

    //2020.02.15 Ben
    //사용하지 않는 코드 주석 처리
//    private void setCurrentTotalCardQuantityOnSaveInfo() throws Exception {
//        int totalQuantity = getCurrentTotalQuantity();
//        getTemplate().saveInfo.orderCount = totalQuantity+"";
//    }

    private boolean isExceedQuantity(int pagePosition, int selectedQuantity) {
        int currentTotalQuantity = 0;
        try {
            currentTotalQuantity = getTotalQuantityExcludeSelectedPage(pagePosition);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(getActivity(), e);
            return true;
        }
        return currentTotalQuantity + selectedQuantity > setMaxQuantity();
    }

    private int getPageQuantity(int pagePosition) throws Exception {
        ArrayList<SnapsPage> pageList = getTemplate().getPages();
        return pageList.get(pagePosition).getQuantity();
    }

    private int getAddableMaxCount(int selectedPosition) {
        try {
            return Math.max(1, (setMaxQuantity() - getTotalQuantityExcludeSelectedPage(selectedPosition)));
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return 1;
        }
    }

    protected int getTotalQuantityExcludeSelectedPage(int pagePosition) throws Exception {
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
    public abstract String getDeletePageMessage();

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
        refreshQuantityFloatingView(setMaxQuantity());
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
       // loadTemplate().setApplyMaxPage();
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
            View targetView = null;
            if(Const_PRODUCT.isSloganProduct()) {
                targetView = ((SloganActivityThumbnailAdapter) activityThumbnailAdapter).getFirstLayout();
            } else {
                targetView = ((NewYearsCardActivityThumbnailAdapter) activityThumbnailAdapter).getFirstLayout();
            }
                SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
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
    public void addPage() {
        //TODO  페이지 추가 디자인 선택 리스트로 가야한다
//        int maxCount = 0;
//        try {
//            maxCount = getMaxSelectCount();
//        }catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//        if (maxCount == 0) {
//            MessageUtil.toast(getActivity(),getString(R.string.any_more_card_msg));
//        } else {
            if(Const_PRODUCT.isExamStikerProduct()) {
                addCopiedPageFromLastPage();
            } else if(Const_PRODUCT.isSloganProduct()) {
                addCopiedPageFromLastTwoPage();
            } else{
                showAddPageActivity();
            }

//        }

    }
    private void addCopiedPageFromLastPage() {
        try {
            int currentPageCount = getTemplate().getPages().size();

            int lastPageIndex = currentPageCount-1;
            int addLeftPageIndex = currentPageCount;

            SnapsPage copiedLastPage = getTemplate().getPages().get(lastPageIndex).copyPage(addLeftPageIndex);
            if (copiedLastPage == null) {
                MessageUtil.toast(getActivity(), getString(R.string.page_add_error_msg));//"페이지 추가 중 오류가 발생했습니다.");
                return;
            }
            // 페이지 추가
            SnapsPage[] pages = { copiedLastPage};
            if (addPage(addLeftPageIndex, pages)) {
                refreshQuantityFloatingView(setMaxQuantity());
                showThumbnailLongClickDeleteTutorial();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void addCopiedPageFromLastTwoPage() {
        try {
            int currentPageCount = getTemplate().getPages().size();

            int lastLeftPageIndex = currentPageCount-2;
            int lastRightPageIndex = currentPageCount-1;
            int addLeftPageIndex = currentPageCount;
            int addRightPageIndex = currentPageCount + 1;

            SnapsPage copiedLastLeftPage = getTemplate().getPages().get(lastLeftPageIndex).copyPage(addLeftPageIndex);
            SnapsPage copiedLastRightPage = getTemplate().getPages().get(lastRightPageIndex).copyPage(addRightPageIndex);

            if (copiedLastLeftPage == null || copiedLastRightPage == null) {
                MessageUtil.toast(getActivity(), getString(R.string.page_add_error_msg));//"페이지 추가 중 오류가 발생했습니다.");
                return;
            }
            // 페이지 추가
            SnapsPage[] pages = { copiedLastLeftPage, copiedLastRightPage };
            if (addPage(addLeftPageIndex, pages)) {
                refreshQuantityFloatingView(setMaxQuantity());
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
    public void onCompleteLoadTemplateHook() {
        refreshQuantityFloatingView(setMaxQuantity());
        if(!Const_PRODUCT.isExamStikerProduct() && !Config.getPROD_CODE().equals(EXAM_STICKER_2020)) {
            showDesignChangeTutorial();
        }

        showTextEditTutorial();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

//        showThumbnailLongClickDeleteTutorial();
        showThumbnailChangeQuantityTutorial();
    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        if(!AutoSaveManager.isAutoSaveRecoveryMode()) {
            // add page가 있으면 추가를 한다.
            ArrayList<Integer> addIndex = ImageSelectUtils.getAddPageIdxs();

            if (addIndex != null) {
                for (int idx : addIndex) {
                    SnapsPage page = template.getPages().get(idx);

                    SnapsPage addPage = page.copyPage(template.getPages().size());
                    if (!page.type.equalsIgnoreCase("hidden"))
                        template.getPages().add(addPage);

                    Dlog.d("appendAddPageOnLoadedTemplate() SnapsPage idx:" + idx);
                    getEditInfo().setPageAddIndex(idx);
                }
            }
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

    public void handleScreenRotatedHook() {
        setCardShapeLayout();
    }

    @Override
    public int getLastEditPageIndex() {
        return 0;
    }

    @Override
    public void onClickedChangeDesign() {
        showChangePageActcity(false);
    }


    //2020.02.15 Ben
    //스티커 수량 계산 버그 수정
    //*생산에서 수정 요청
    //스냅스 스티커 중 set 개수 안맞는 문제가 가끔 발생하는데요.
    //하루치를 모아 한번에 생산하다보니 1건 set 개수가 안맞아도 그날 전체 주문 순서가 섞여 오류 주문 찾고 정상 주문들 짝맞추는데 어려움이 많습니다.
    //1월에 1번, 2월에 1번 발생하였는데 모두 안드로이드 주문이 였습니다.
    //발생하지 않도록 확인 요청드립니다.
    //
    //메소드 이름 지랄같은데
    //최대 개수에서 현재 개수를 뺀 나머지 즉, 사용자가 추가 할 수 있는 수량을 리턴해주면 된다.
    private int getMaxSelectCount() {
        int maxQuantity = 0;
        int currentTotalQuantity = 0;
        try {
            maxQuantity = setMaxQuantity();  //메소드 이름 지랄같네. setMaxQuantity()안에서 getMaxQuantity()를 호출하고 있음
            currentTotalQuantity = getCurrentTotalQuantity();
        }catch(Exception e) {
            Dlog.e(TAG, e);
        }
        //1이상이어야 한다. 0이면 뭔가 박살난 상태라고 봐야한다.
        if (maxQuantity < 1 || currentTotalQuantity < 1) {
            //더 이상 추가 불가
            return 0;
        }

        //기존 버그 때문에 max보다 크게 추가된 경우가 있다. 이 문제 땜방
        int reuslt = Math.max(0, maxQuantity - currentTotalQuantity);
        return reuslt;
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
        refreshQuantityFloatingView(setMaxQuantity());
        setThumbnailSelectionDragView(EditActivityThumbnailUtils.PAGE_MOVE_TYPE_NONE, startIdx);
        showThumbnailLongClickDeleteTutorial();
    }

    @Override
    public void refreshSelectedNewImageDataHook(MyPhotoSelectImageData imageData) {
        if (imageData == null) return;
//        try {
//            if (isAllPhotoInsertedOnFirstLayout())
//                showThumbnailChangeQuantityTutorial();
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
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


    private void showThumbnailChangeQuantityTutorial() {
        if (getEditInfo().IS_EDIT_MODE() || isShownTutorialForChangeQuantity) return;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int topMargin = 0;
                    if (isLandScapeScreen()) {
                        topMargin = 0;
                    } else {
                        topMargin = -24;
                    }
                    BaseEditActivityThumbnailAdapter activityThumbnailAdapter =getEditControls().getThumbnailAdapter();
                    View targetView = null;
                    if(Const_PRODUCT.isSloganProduct()) {
                        targetView = ((SloganActivityThumbnailAdapter) activityThumbnailAdapter).getFirstCountLayout();
                   } else {
                        targetView = ((NewYearsCardActivityThumbnailAdapter) activityThumbnailAdapter).getFirstCountLayout();
                   }
                    SnapsTutorialUtil.showTooltip(getActivity(), new SnapsTutorialAttribute.Builder().setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM)
                            .setText(getActivity().getString(R.string.guide_change_count_msg))
                            .setTargetView(targetView)
                            .setTopMargin(UIUtil.convertDPtoPX(getActivity(), topMargin))
                            .create());

                    isShownTutorialForChangeQuantity = true;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        },500);

    }


    protected void refreshQuantityFloatingView(final int maxQuantity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int currentTotalCardQuantity = getCurrentTotalQuantity();
                    currentCount.setText(String.valueOf(currentTotalCardQuantity));
                    maxCount.setText(String.valueOf(maxQuantity));
//                    setCurrentTotalCardQuantityOnSaveInfo();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }
}
