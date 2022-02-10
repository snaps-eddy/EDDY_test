package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2016. 11. 24..
 * 사진 선택 화면 - 상단에 트레이(포토북 형태)가 존재하는 형태의 상품군
 */
public class ImageSelectUIProcessorStrategyForDiaryServiceAlive extends ImageSelectUIProcessorStrategyForTemplateProducts {
    private static final String TAG = ImageSelectUIProcessorStrategyForDiaryServiceAlive.class.getSimpleName();

    @Override
    protected SnapsTemplate loadTemplate(final ImageSelectIntentData intentData) {
        String templatePrePath = Const_VALUE.PATH_PACKAGE(activity, false);
        try {
            Config.checkServiceThumbnailSimpleFileDir();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        //템플릿을 파싱을 한다
        SnapsTemplateManager.getInstance().cleanInstance();
        final String DIARY_TEMPLATE_PATH = "/cache/template/diary_template.xml";
        String filePath = templatePrePath + DIARY_TEMPLATE_PATH;

        String url = "";
        if (intentData != null) {
            url = intentData.getDiaryXMLPath();
        }

        if (!HttpUtil.makeDiaryTemplateFile(activity, SnapsTemplate.getDiaryTemplateUrl(url), filePath))
            return null;

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.setTemplateFilePath(filePath);
        return GetTemplateLoad.getFileTemplate(filePath, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }
}
