package com.snaps.mobile.activity.common.products.multi_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.R;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

import static com.snaps.common.utils.constant.Const_PRODUCT.BIG_RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.EXAM_STICKER_2020;
import static com.snaps.common.utils.constant.Const_PRODUCT.LONG_PHOTO_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.NAME_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.RECTANGLE_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.ROUND_STICKER;
import static com.snaps.common.utils.constant.Const_PRODUCT.SQUARE_STICKER;

/**
 * Created by kimduckwon on 2018. 1. 15..
 */

public class StickerEditor extends SnapsCounterPageEditor {
    private static final String TAG = StickerEditor.class.getSimpleName();

    public final static int MAX_ROUND_QUANTITY = 5;
    public final static int MAX_SQUARE_QUANTITY = 7;
    public final static int MAX_RECTANGLE_QUANTITY = 17;
    public final static int MAX_EXAM_QUANTITY = 5;
    public final static int MAX_NAME_QUANTITY = 10;
    public final static int MAX_BIG_RECTANGLE_QUANTITY = 5;
    public final static int MAX_LONG_PHOTO_QUANTITY = 3;

    public StickerEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        if(Config.getPROD_CODE().equals(EXAM_STICKER_2020)) {
            setNotExistThumbnailLayout();
        } else{
            super.initControlVisibleStateOnActivityCreate();
        }
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();
        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();
        SnapsOrderManager.uploadThumbImgListOnBackground();
        super.onCompleteLoadTemplateHook();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_STICKER;
    }

    @Override
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    public int setMaxQuantity() {
        return getMaxQuantity();
    }

    private int getMaxQuantity() {
        switch (Config.getPROD_CODE()) {
            case ROUND_STICKER:
                return MAX_ROUND_QUANTITY;
            case SQUARE_STICKER:
                return MAX_SQUARE_QUANTITY;
            case RECTANGLE_STICKER:
                return MAX_RECTANGLE_QUANTITY;
            case EXAM_STICKER:
                return MAX_EXAM_QUANTITY;
            case NAME_STICKER:
                return MAX_NAME_QUANTITY;
            case BIG_RECTANGLE_STICKER:
                return MAX_BIG_RECTANGLE_QUANTITY;
            case LONG_PHOTO_STICKER:
                return MAX_LONG_PHOTO_QUANTITY;
            default:
                return 0;

        }
    }

    @Override
    public String setTitle() {
        return getTitle();
    }

    private String getTitle() {
        switch (Config.getPROD_CODE()) {
            case ROUND_STICKER:
                return getString(R.string.round_sticker);
            case SQUARE_STICKER:
                return getString(R.string.square_sticker);
            case RECTANGLE_STICKER:
                return getString(R.string.rectangle_sticker);
            case EXAM_STICKER:
                return getString(R.string.exam_sticker);
            case NAME_STICKER:
                return getString(R.string.name_sticker);
            case BIG_RECTANGLE_STICKER:
                return getString(R.string.big_rectangle_sticker_2);
            case LONG_PHOTO_STICKER:
                return getString(R.string.long_photo_sticker);
            default:
                return "";

        }
    }

    @Override
    public boolean addTemplatePage(SnapsTemplate snapsTemplate) {
        return false;
    }

    @Override
    public String getDeletePageMessage() {
        return getString(R.string.sticker_delete);
    }

    @Override
    public void changeTemplatePage(SnapsTemplate snapsTemplate) {

    }

    @Override
    public void appendAddPageOnLoadedTemplate(SnapsTemplate template) {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        int count =dataTransManager.getPhotoImageDataList().size();
        try {
            int currentPageCount = template.getPages().size();
            int maxImageCount = (template.getPages().get(0).getLayerLayouts().size());
            int addCount = (int) Math.ceil((double)count / maxImageCount);


            // 페이지 추가
            for(int i=0; i<addCount -1  ; i++) {
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
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
        showEditActivityTutorial();
    }
}

