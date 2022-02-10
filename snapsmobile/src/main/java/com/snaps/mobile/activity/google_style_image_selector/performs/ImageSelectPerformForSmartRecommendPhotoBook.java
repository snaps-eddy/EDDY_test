package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.DateUtil;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.SmartRecommendBookMakingActivity;
import com.snaps.mobile.activity.themebook.ThemeTitleActivity;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;

import errorhandle.logger.Logg;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.REQCODE_INPUT_TITLE;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE;

/**
 * Created by ysjeong on 2016. 12. 2..
 */

public class ImageSelectPerformForSmartRecommendPhotoBook extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    private static final String TAG = ImageSelectPerformForSmartRecommendPhotoBook.class.getSimpleName();

    public ImageSelectPerformForSmartRecommendPhotoBook(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.PHONE_DETAIL;
    }

    /**
     * 완료 버튼을 눌렀을 때의 처리
     */
    @Override
    public void onClickedNextBtn() {
        if (checkValidSmartSnapsImageSelectConditions())
            performNextStep();
    }

    private boolean checkValidSmartSnapsImageSelectConditions() {
        if (isNotSelectedCoverPhoto()) {
            MessageUtil.toast(imageSelectActivity, imageSelectActivity.getString(R.string.auto_recommand_cover_select_msg));
            return false;
        }

        if (isNotEnoughImage()) {
            MessageUtil.toast(imageSelectActivity, String.format(imageSelectActivity.getString(R.string.smart_analysis_product_is_not_enough_photo_msg),
                    SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT, SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT));
            return false;
        }

        return isValidNetworkState();
    }

    private boolean isNotSelectedCoverPhoto() {
        if (Config.isActiveImageAutoSelectFunction()) return false;
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (smartSnapsManager.isExistCoverPhotoMapKey()) {
            DataTransManager dataTransManager = DataTransManager.getInstance();
            if (dataTransManager != null) {
                ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
                if (holder != null) {
                    ArrayList<MyPhotoSelectImageData> imageList = holder.getNormalData();
                    if (imageList != null) {
                        for (MyPhotoSelectImageData imageData : imageList) {
                            if (imageData == null) continue;
                            if (SmartSnapsManager.getInstance().isContainCoverPhotoMapKey(imageData.getImageSelectMapKey())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isNotEnoughImage() {
        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager != null) {
            ImageSelectTrayPageCountInfo pageCountInfo = manager.getPageCountInfo();
            if (pageCountInfo != null) {
//                if (pageCountInfo.getCurrentSelectedImageCount() < SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT) {
//                    return true;
//                }
                return pageCountInfo.hasEmptyImageContainerByTotalCount(SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT);
            }
        }
        return false;
    }

    private boolean isValidNetworkState() {
        switch (SnapsImageUploadUtil.getBackgroundImgUploadNetworkCheckResult(imageSelectActivity)) {
            case SUCCESS:
                return true;
            case FAILED_CAUSE_IS_NOT_ALLOW_CELLULAR:
            case FAILED_CAUSE_DENIED:
                showConfirmBackgroundUploadByCellularData();
                return false;
            default:
                MessageUtil.toast(imageSelectActivity, R.string.smart_snaps_search_network_disconnect_alert);
                return false;
        }
    }

    private void showConfirmBackgroundUploadByCellularData() {
        try {
            SnapsImageUploadUtil.showConfirmBackgroundUploadByCellularData(imageSelectActivity, getCellularDataConfirmDialog(), new ICustomDialogListener() {
                @Override
                public void onClick(byte clickedOk) {
                    if (clickedOk == ICustomDialogListener.OK) {
                        Setting.set(imageSelectActivity, SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE, DateUtil.getTodayDate());
                        performNextStep();
                    } else {
                        MessageUtil.toast(imageSelectActivity, R.string.smart_snaps_search_network_disconnect_alert);
                    }
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 액티비티 이동
     */
    protected void performNextStep() {
        if (isSuccessSetSimpleDatas()) {
            Intent intent = new Intent(imageSelectActivity, ThemeTitleActivity.class);
            intent.putExtra("whereis", "recommend_photo_book");
            imageSelectActivity.startActivityForResult(intent, REQCODE_INPUT_TITLE);
        }
    }

    @Override
    public void moveNextActivity() {
        Intent saveIntent = new Intent(imageSelectActivity, SmartRecommendBookMakingActivity.class);
        saveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        imageSelectActivity.startActivity(saveIntent);
        imageSelectActivity.finish();
        cloneCurrentSelectedImageList();
    }

    @Override
    protected boolean isSuccessSetSimpleDatas() {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> postImageList = holder.getNormalData();
            DataTransManager dataTransManager = DataTransManager.getInstance();
            if (dataTransManager != null) {
                dataTransManager.releaseAllData();
                dataTransManager.setPhotoImageDataList(postImageList);
            } else {
                DataTransManager.notifyAppFinish(imageSelectActivity);
                return false;
            }
        }

        return true;
    }

    //뒤로가기로 돌아왔을 때 데이터를 복구하기 위해..
    private void cloneCurrentSelectedImageList() {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) dataTransManager.cloneCurrentSelectedImageList();
    }

    private ArrayList<Integer> getAddPageIndexList() {
        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return null;

        int currentSelectedImageCount = 0;
        ImageSelectTrayPageCountInfo pageCountInfo = manager.getPageCountInfo();
        if (pageCountInfo != null) currentSelectedImageCount = pageCountInfo.getCurrentSelectedImageCount();

        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (template == null || template.getPages() == null || template.getPages().isEmpty()) return null;

        ArrayList<SnapsPage> pageList = template.getPages();

        int currentImageControlCnt = 0;

        for (int ii = 0; ii < pageList.size(); ii++) {
            SnapsPage snapsPage = pageList.get(ii);
            currentImageControlCnt += snapsPage.getImageLayoutControlCountOnPage();
        }

        if (currentImageControlCnt > 0) {
            int totalPageCount = pageList.size();
            int addPageIndex = 2;
            ArrayList<Integer> addIdxList = ImageSelectUtils.getAddPageIdxs();
            int maxPage = (2 * Integer.parseInt(ImageSelectUtils.getCurrentPaperCodeMaxPage())) + 1;
            while (currentSelectedImageCount > currentImageControlCnt && PhotobookCommonUtils.calculateTotalPageCount(totalPageCount) <= maxPage) {
                if (addPageIndex >= pageList.size())
                    addPageIndex = 2;

                SnapsPage snapsPage = pageList.get(addPageIndex);
                currentImageControlCnt += snapsPage.getImageLayoutControlCountOnPage();

                if (addIdxList != null) {
                    addIdxList.add(addPageIndex++);
                }

                totalPageCount++;
            }

            Dlog.d("addPageCount:" + totalPageCount + ", currentSelectedImageCount:" + currentSelectedImageCount
                    + ", currentImageControlCnt:" + currentImageControlCnt);
            return addIdxList;
        }

        return null;
    }
}
