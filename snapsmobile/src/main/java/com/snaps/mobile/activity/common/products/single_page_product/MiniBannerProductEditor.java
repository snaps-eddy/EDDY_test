package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.products.multi_page_product.SnapsMultiPageEditor;
import com.snaps.mobile.autosave.IAutoSaveConstants;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public class MiniBannerProductEditor extends SnapsMultiPageEditor {
    private static final String TAG = MiniBannerProductEditor.class.getSimpleName();

    public MiniBannerProductEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null)
            coverModify.setVisibility(View.VISIBLE);

        ImageView previewBtn = getEditControls().getThemePreviewBtn();
        if (previewBtn != null)
            previewBtn.setVisibility(View.GONE);
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_MINI_BANNER;
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
    public void setActivityContentView() {
        getActivity().setContentView(R.layout.activity_edit_new_years_card);
    }

    @Override
    public void initPaperInfoOnLoadedTemplate(SnapsTemplate template) {
//        if(Const_PRODUCT.isTumblerProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isHangingFrameProduct()) {
//            if(getEditInfo().IS_EDIT_MODE()) {
//                Config.setPAPER_CODE(template.info.F_PAPER_CODE);
//                Config.setGLOSSY_TYPE(template.info.F_GLOSSY_TYPE);
//            } else {
//                template.info.F_PAPER_CODE = Config.getPAPER_CODE();
//                template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
//            }
//
//            PhotobookCommonUtils.checkPaperInfoFromTemplate(template);
//        } else {
            super.initPaperInfoOnLoadedTemplate(template);
//        }
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }

    @Override
    public void onClickedChangeDesign() {
        showChangePageActcity(false);
    }
}
