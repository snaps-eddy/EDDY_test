package com.snaps.mobile.activity.common.products.single_page_product;

import android.graphics.Rect;
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

public class FrameProductEditor extends SnapsSinglePageEditor {
    private static final String TAG = FrameProductEditor.class.getSimpleName();

    public FrameProductEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();
    }

    @Override
    public int getAutoSaveProductCode() {
        return IAutoSaveConstants.PRODUCT_TYPE_FRAME;
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

    /***
     * 디자인 노트에 QR 코드를 넣기 위해...
     */
    @Override
    public Rect getQRCodeRect() {
        // cover 사이즈를 구한다.
        float cover_width = getTemplate().getPages().get(0).getWidth();
        float cover_height = Float.parseFloat(getTemplate().getPages().get(0).height);

        int width = 60;
        int height = 20;

        Rect qrRect = new Rect();
        qrRect.left = (int) (cover_width / 2 - width - 20);
        qrRect.right = qrRect.left + width;
        qrRect.top = (int) (cover_height - height - 20);
        qrRect.bottom = qrRect.top + height;

        return qrRect;
    }

    @Override
    protected boolean initLoadedTemplateInfo(SnapsTemplate template) {
        super.initLoadedTemplateInfo(template);

        // 프레임 타입을 저장한다.
        if (Const_PRODUCT.isFrameProduct() &&!getEditInfo().IS_EDIT_MODE()) {
            template.info.F_FRAME_TYPE = Config.getFRAME_TYPE();
            template.info.F_FRAME_ID = Config.getFRAME_ID();

            template.changeWidthHeight();
        }

        // 디자인 노트인경우
        if (Const_PRODUCT.isDesignNoteProduct() && !getEditInfo().IS_EDIT_MODE()) {
            template.info.F_PAPER_CODE = Config.getNOTE_PAPER_CODE();
        }

//        // 핸드폰 케이스..
//        if (Const_PRODUCT.isPhoneCaseProduct() && !getEditInfo().IS_EDIT_MODE()) {
//            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
//        }
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
