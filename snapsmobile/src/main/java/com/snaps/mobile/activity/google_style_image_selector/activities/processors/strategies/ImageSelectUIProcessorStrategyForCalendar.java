package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.utils.net.xml.GetParsedXml;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2016. 11. 24..
 * 사진 선택 화면 - 상단에 트레이(포토북 형태)가 존재하는 형태의 상품군
 */
public class ImageSelectUIProcessorStrategyForCalendar extends ImageSelectUIProcessorStrategyForTemplateProducts {
    @Override
    protected void handleGetTemplateBeforeTask() {
        GetParsedXml.initTitleInfo(SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }
}
