package com.snaps.mobile.activity.common.products.book_product;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

public class KTBookEditor extends SnapsBookShapeEditor {

    private static final String TAG = KTBookEditor.class.getSimpleName();

    public KTBookEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();

        getTemplate().setApplyMaxPage();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_KT_BOOK;
    }

    @Override
    public void handleCenterPagerSelected() {
        setTitleIconStateOnChangedPage();
    }


    @Override
    public void onClickedChangeDesign() {
        if (isCoverPage()) {
            super.onClickedChangeDesign();
        } else {
            InterceptTouchableViewPager centerPager = getEditControls().getCenterPager();
            if (centerPager != null) {
                showChangePageActcity(centerPager.getCurrentItem() == 1);
            }
        }
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setTitleIconStateOnChangedPage();
    }

    private void setTitleIconStateOnChangedPage() {
        ImageView coverModify = getEditControls().getThemeCoverModify();
        ImageView textModify = getEditControls().getThemeTextModify();

        if (getTemplate() != null && getTemplate().info != null) {
            SnapsTemplateInfo snapsTemplateInfo = getTemplate().info;
            if (isCoverPage()) {
                boolean isLeatherCover = snapsTemplateInfo.F_COVER_TYPE != null && snapsTemplateInfo.F_COVER_TYPE.equals("leather");
                if (!isLeatherCover)
                    textModify.setVisibility(View.VISIBLE);

                coverModify.setVisibility(View.VISIBLE);
            } else {
                if (textModify != null && textModify.isShown())
                    textModify.setVisibility(View.GONE);
            }
        }

        View addPageButton = getEditControls().getAddPageLy();
        if (addPageButton != null) {
            addPageButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        ArrayList<MyPhotoSelectImageData> sortedGalleryList = getEditInfo().getGalleryList();
        PhotobookCommonUtils.imageRange(template, sortedGalleryList);
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
        if (isAddedPage()) {
            try {
                showAddedPagePriceInfoPopup();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        } else {
            showEditActivityTutorial();
        }
    }

    private void showAddedPagePriceInfoPopup() throws Exception {
        try {
            int pageCount = PhotobookCommonUtils.getEachPageCount(getPageList().size());
            String sellPrice = PhotobookCommonUtils.calculateAddedPageTotalSellPrice(getActivity(), getTemplate(), pageCount);
            int addedPageCount = PhotobookCommonUtils.getAddedPageCount(getTemplate(), pageCount);

            if (addedPageCount > 0) {
                String msg = String.format(getActivity().getString(R.string.smart_snaps_added_page_alert_msg), addedPageCount, sellPrice);
                MessageUtil.alertnoTitleOneBtn(getActivity(), msg, clickedOk -> showEditActivityTutorial());
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public int getPopMenuPhotoTooltipLayoutResId(Intent intent) {
        return R.layout.popmenu_photo;
    }

    @Override
    public void showBottomThumbnailPopOverView(View offsetView, int position) {
        //트레이 팝업 메뉴 표시 안되게
    }

    @Override
    protected boolean isImageEditableOnlyCover() {
        return false;
    }

}
