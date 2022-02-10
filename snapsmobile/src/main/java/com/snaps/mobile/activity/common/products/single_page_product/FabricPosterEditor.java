package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;

public class FabricPosterEditor extends SnapsSinglePageEditor {
    private static final String TAG = FabricPosterEditor.class.getSimpleName();

    public FabricPosterEditor(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void initControlVisibleStateOnActivityCreate() {
        setNotExistThumbnailLayout();
    }

    @Override
    public void initEditInfoBeforeLoadTemplate() {
        Config.setPROJ_NAME("");
    }

    @Override
    public void onCompleteLoadTemplateHook() {

        SnapsOrderManager.startSenseBackgroundImageUploadNetworkState();

        SnapsOrderManager.uploadThumbImgListOnBackground();
    }

    @Override
    public void onFinishedFirstSmartSnapsAnimation() {
    }

    @Override
    public boolean shouldSmartSnapsAnimateOnActivityStart() {
        return true;
    }

    @Override
    public void handleScreenRotatedHook() {
        initControlVisibleStateOnActivityCreate();
    }
}
