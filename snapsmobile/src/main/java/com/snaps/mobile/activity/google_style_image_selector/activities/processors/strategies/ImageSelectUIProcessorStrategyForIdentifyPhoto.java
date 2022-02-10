package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import android.graphics.Rect;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayIdentifyPhotoAdapter;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForIdentifyPhoto extends ImageSelectUIProcessorStrategyForTemplateBase {

    /**
     * 증명 사진 인화는 템플릿이 있는데 한장만 선택할 수 있는 특이한 케이스이다.
     */
    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();
        this.pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());

        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.IDENTIFY_PHOTO);

        loadTemplate();
    }

    @Override
    public void postInitialized() {}

    @Override
    public boolean isExistTrayView() {
        return true;
    }

    @Override
    public boolean isExistOnlyTrayAllViewLayout() {
        return false;
    }

    @Override
    public ImageSelectTrayBaseAdapter createTrayAdapter() {
        if (uiProcessor == null) return null;
        return new ImageSelectTrayIdentifyPhotoAdapter(uiProcessor.getActivity());
    }

    private Rect getIdentifyPhotoEnableResolutionRect(SnapsTemplate template) {
        for (int index = 0; index < template.getPages().size(); index++) {
            SnapsPage page = template.getPages().get(index);
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                Rect result = null;

                if (layout != null && "browse_file".equals(layout.type)) {
                    result = ResolutionUtil.getEnableResolution(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_PIXEL_WIDTH, layout);
                }
                if (result != null) return result;
            }
        }
        return null;
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {
        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (uiProcessor == null || template == null || template.getPages() == null || template.getPages().isEmpty()) return;
        Rect enableResolutionRect = getIdentifyPhotoEnableResolutionRect(template);
        if (enableResolutionRect != null) {
            ImageSelectIntentData intentData = activity.getIntentData();
            intentData.setRecommendWidth(enableResolutionRect.width());
            intentData.setRecommendHeight(enableResolutionRect.height());
        }
    }
}
