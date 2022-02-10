package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTraySmartRecommendBookAdapter;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForSmartRecommendBook extends ImageSelectUIProcessorStrategyForTemplateBase implements IImageSelectUIProcessorStrategy {
    private static final String TAG = ImageSelectUIProcessorStrategyForSmartRecommendBook.class.getSimpleName();

    @Override
    public void initialize(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return;

        this.uiProcessor = uiProcessor;
        this.activity = uiProcessor.getActivity();

        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS);
        pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());

        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT);

        uiProcessor.setMaxImageCount(SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT);
    }

    @Override
    public void postInitialized() {
        try {
            ImageSelectIntentData intentData = uiProcessor.getIntentData();
            if (intentData != null && intentData.isComebackFromEditActivity())
                uiProcessor.recoveryPrevSelectedImageList();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public boolean isExistTrayView() {
        return false;
    }

    @Override
    public boolean isExistOnlyTrayAllViewLayout() {
        return true;
    }

    @Override
    public ImageSelectTrayBaseAdapter createTrayAdapter() {
        if (uiProcessor == null) return null;
        return new ImageSelectTraySmartRecommendBookAdapter(uiProcessor.getActivity());
    }

    @Override
    protected void handleGetTemplateBeforeTask() {}

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {}

    @Override
    protected void onTemplateLoaded() {}
}
