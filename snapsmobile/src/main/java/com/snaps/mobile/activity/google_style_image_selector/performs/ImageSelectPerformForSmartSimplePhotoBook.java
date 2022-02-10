package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.util.org_image_upload.SnapsImageUploadUtil;
import com.snaps.common.utils.system.DateUtil;

import java.util.ArrayList;

import errorhandle.logger.Logg;

import static com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants.EXTRA_NAME_ADD_PAGE_INDEX_LIST;
import static com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants.SETTING_VALUE_USE_CELLULAR_CONFIRM_DATE;

/**
 * Created by ysjeong on 2016. 12. 2..
 */
public class ImageSelectPerformForSmartSimplePhotoBook extends BaseImageSelectPerformer implements IImageSelectProductPerform {
    private static final String TAG = ImageSelectPerformForSmartSimplePhotoBook.class.getSimpleName();

    public ImageSelectPerformForSmartSimplePhotoBook(ImageSelectActivityV2 activity) {
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
            moveNextActivity();
    }

    private boolean checkValidSmartSnapsImageSelectConditions() {
        if (isNotSelectPhoto()) {
            MessageUtil.toast(imageSelectActivity, R.string.please_select_a_photo_for_photobook);
            return false;
        }

        return isValidNetworkState();
    }

    private boolean isNotSelectPhoto() {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        return holder == null || holder.getListSize() == 0;
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
                        moveNextActivity();
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
    @Override
    public void moveNextActivity() {
        ArrayList<Integer> addPageIndexList = getAddPageIndexList();

        if(isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            saveIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SIMPLE_PHOTO_BOOK.ordinal());
            saveIntent.putExtra("templete", TEMPLATE_PATH);

            if (addPageIndexList != null && !addPageIndexList.isEmpty()) {
                saveIntent.putExtra(EXTRA_NAME_ADD_PAGE_INDEX_LIST, addPageIndexList);
            }

            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();

            cloneCurrentSelectedImageList();
        }
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
        //FIXME 시간순 정렬 처리 해서 넣자..

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
            while(currentSelectedImageCount > currentImageControlCnt && PhotobookCommonUtils.calculateTotalPageCount(totalPageCount) <= maxPage) {
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
