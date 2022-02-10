package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.common.products.base.PhoneCaseEditorHandler;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorHandler;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

public class PhoneCaseEditor extends SnapsSinglePageEditor {

    public PhoneCaseEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();

        ImageView coverModify = getEditControls().getThemeCoverModify();
        if (coverModify != null) {
            coverModify.setVisibility(View.VISIBLE);
        }
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
            template.info.F_GLOSSY_TYPE = Config.getGLOSSY_TYPE();
        }
        return true;
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }

    @Override
    public void onClickedChangeDesign() {
        showChangePageActcity(false);
    }

    /**
     * Phonecase 용 EditorHanlder 리턴. 만약 다른 상품 Editor 에서 BaseEditorHandler 관련 기능을 수정할 때 이와같이 새로운 핸들러 만들기를 추천.
     *
     * @return
     */
    @Override
    protected SnapsProductBaseEditorHandler getBaseEditorHandler() {
        return Const_PRODUCT.isLegacyPhoneCaseProduct() ? PhoneCaseEditorHandler.createHandlerWithBridge(this) : super.getBaseEditorHandler();
    }
}
