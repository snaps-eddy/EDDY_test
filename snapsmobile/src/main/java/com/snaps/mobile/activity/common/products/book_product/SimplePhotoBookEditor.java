package com.snaps.mobile.activity.common.products.book_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class SimplePhotoBookEditor extends SnapsBookShapeEditor {
    private static final String TAG = SimplePhotoBookEditor.class.getSimpleName();

    public SimplePhotoBookEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_SIMPLE_BOOKS;
    }

    @Override
    public void onClickedTextControl(SnapsProductEditReceiveData editReceiveData) {}

    @Override
    public void onClickedBgControl() {
        if (getSnapsTemplateInfo() == null) return;
        if (getSnapsTemplateInfo().getCoverType() != SnapsTemplateInfo.COVER_TYPE.NONE_COVER)
            showCoverChangeActcity();
    }

    @Override
    public void handleCenterPagerSelected() {
        setTitleIconStateOnChangedPage();
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
    public void initImageRangeInfoOnLoadedTemplate(SnapsTemplate template) {
        ArrayList<MyPhotoSelectImageData> sortedGalleryList = null;
        if (SmartSnapsManager.isSupportSmartSnapsProduct() && SmartSnapsManager.isSmartImageSelectType()) {
            sortedGalleryList = getSortByPhotoFileModifiedDateIfSmartChoice();
        } else {
            sortedGalleryList = getEditInfo().getGalleryList();
        }

        PhotobookCommonUtils.imageRange(template, sortedGalleryList);
    }

    private ArrayList<MyPhotoSelectImageData> getSortByPhotoFileModifiedDateIfSmartChoice() {
        ArrayList<MyPhotoSelectImageData> result = getEditInfo().getGalleryList();
        if (result != null && result.size() > 1) {
            Collections.sort(result, new Comparator<MyPhotoSelectImageData>() {
                @Override
                public int compare(MyPhotoSelectImageData currentImgData, MyPhotoSelectImageData nextImgData) {
                    if (currentImgData != null && nextImgData != null) {
                        return currentImgData.photoTakenDateTime < nextImgData.photoTakenDateTime ? -1 : (currentImgData.photoTakenDateTime > nextImgData.photoTakenDateTime ? 1 : 0);
                    }
                    return 0;
                }
            });
        }

        return result;
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
                MessageUtil.alertnoTitleOneBtn(getActivity(), msg, new ICustomDialogListener() {
                    @Override
                    public void onClick(byte clickedOk) {
                        showEditActivityTutorial();
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }
}
