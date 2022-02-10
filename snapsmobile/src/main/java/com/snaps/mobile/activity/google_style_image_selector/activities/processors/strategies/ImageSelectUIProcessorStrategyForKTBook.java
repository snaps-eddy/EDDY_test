package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.performs.ImageSelectPerformForKTBook;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayKTBookAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTraySmartSnapsSelectAdapter;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;

public class ImageSelectUIProcessorStrategyForKTBook extends ImageSelectUIProcessorStrategyForTemplateBase implements IImageSelectUIProcessorStrategy {

    private static final String TAG = ImageSelectUIProcessorStrategyForSmartSnapsSelect.class.getSimpleName();

    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();

        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_SNAPS);
        pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());

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
        return new ImageSelectTrayKTBookAdapter(uiProcessor.getActivity());
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {
        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (uiProcessor == null || template == null || template.getPages() == null || template.getPages().isEmpty()) return;

        //선택 가능한 최대 사진 갯수
        uiProcessor.setMaxImageCount(ImageSelectPerformForKTBook.MAX_KT_BOOK_IMAGE_COUNT);
        uiProcessor.setCurrentMaxImageCount(ImageSelectPerformForKTBook.MAX_KT_BOOK_IMAGE_COUNT);

        try {
            ImageSelectIntentData intentData = uiProcessor.getIntentData();
            if (intentData != null && intentData.isComebackFromEditActivity())
                uiProcessor.recoveryPrevSelectedImageList();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
