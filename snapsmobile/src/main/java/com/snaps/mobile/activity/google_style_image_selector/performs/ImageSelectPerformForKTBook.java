package com.snaps.mobile.activity.google_style_image_selector.performs;

import android.content.Intent;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyForKTBook;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectTrayPageCountInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants.EXTRA_NAME_ADD_PAGE_INDEX_LIST;

public class ImageSelectPerformForKTBook extends BaseImageSelectPerformer implements IImageSelectProductPerform {

    public static final int MAX_KT_BOOK_IMAGE_COUNT = 22;

    private static final String TAG = ImageSelectPerformForKTBook.class.getSimpleName();

    public ImageSelectPerformForKTBook(ImageSelectActivityV2 activity) {
        super(activity);
    }

    @Override
    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType() {
        return ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.PHONE_DETAIL;
    }

    /**
     * ?????? ????????? ????????? ?????? ??????
     */
    @Override
    public void onClickedNextBtn() {
        if (imageSelectActivity == null) return;

        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return;

        boolean isNotAllSelected = false; //???????????? ?????????, ????????? ?????? ???????????? ?????? ??????

        ImageSelectTrayPageCountInfo pageCountInfo = manager.getPageCountInfo();
        if (pageCountInfo != null) {
//            if (pageCountInfo.getCurrentSelectedImageCount() < MAX_KT_BOOK_IMAGE_COUNT) {
//                isNotAllSelected = true;
//            }
            isNotAllSelected = pageCountInfo.hasEmptyImageContainerByTotalCount(MAX_KT_BOOK_IMAGE_COUNT);
        }

        List<MyPhotoSelectImageData> datas = manager.getImageSelectDataHolder().getNormalData();
        SnapsTemplateManager.getInstance().getSnapsTemplate().makeDynamicTemplate(datas);


        if (isNotAllSelected) {
            //????????? ??? ???????????????
            MessageUtil.toast(imageSelectActivity, imageSelectActivity.getString(R.string.select_some_photos, MAX_KT_BOOK_IMAGE_COUNT));
        } else {
            moveNextActivity();
        }
    }

    /**
     * SnapsEditActivity ?????? ????????? ????????? ??? ???????????? Intent
     */
    @Override
    public void moveNextActivity() {
        if (isSuccessSetSimpleDatas()) {
            Intent saveIntent = new Intent(imageSelectActivity, SnapsEditActivity.class);
            saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.KT_Book.ordinal());
            saveIntent.putExtra("templete", TEMPLATE_PATH);

            ArrayList<Integer> addPageIndexList = ImageSelectUtils.getAddPageIdxs();
            if (addPageIndexList != null && !addPageIndexList.isEmpty()) {
                saveIntent.putExtra(EXTRA_NAME_ADD_PAGE_INDEX_LIST, addPageIndexList);
            }

            imageSelectActivity.startActivity(saveIntent);
            imageSelectActivity.finish();
        }
    }
}
