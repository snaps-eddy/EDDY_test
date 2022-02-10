package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class DIYStickerProductEditor extends SnapsSinglePageEditor {
    private static final String TAG = DIYStickerProductEditor.class.getSimpleName();

    public DIYStickerProductEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_DIY_STICKER;
    }

	@Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {
        startSmartSearchOnEditorFirstLoad();

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    @Override
    public void setTemplateBaseInfo() {
        getTemplate().saveInfo.orderCount = Config.getCARD_QUANTITY();
        super.setTemplateBaseInfo();
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
        showEditActivityTutorial();
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    protected boolean initLoadedTemplateInfo(SnapsTemplate template) {
        super.initLoadedTemplateInfo(template);
        if (!getEditInfo().IS_EDIT_MODE()) {
            template.info.F_FRAME_TYPE = Config.getFRAME_TYPE();
        } else {
            Config.setFRAME_TYPE(template.info.F_FRAME_TYPE);
        }
        return true;
    }

    @Override
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
        if(Const_PRODUCT.isTumblerProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isHangingFrameProduct()) {
            if(getEditInfo().IS_EDIT_MODE()) {
                Config.setPAPER_CODE(template.info.F_PAPER_CODE);
                Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
            } else {
                template.info.F_PAPER_CODE = Config.getPAPER_CODE();
                template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
            }

            PhotobookCommonUtils.checkPaperInfoFromTemplate(template);
        } else {
            super.initPaperInfoOnLoadedTemplate(template);
        }
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }
}
