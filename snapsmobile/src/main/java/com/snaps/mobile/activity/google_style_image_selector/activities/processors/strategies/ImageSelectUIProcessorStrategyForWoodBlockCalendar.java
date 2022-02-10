package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;

import java.util.ArrayList;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessorStrategyForWoodBlockCalendar extends ImageSelectUIProcessorStrategyForTemplateProducts implements IImageSelectUIProcessorStrategy {
    @Override
    protected void handleGetTemplateBeforeTask() {
        GetParsedXml.initTitleInfo(SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    @Override
    protected void handleGetTemplateAfterTask(SnapsTemplate template) {
        if(template != null && template.getPages() != null) {
            template._hiddenPageList = new ArrayList<SnapsPage>();

            for(int ii = 1; ii >= 0; ii--) { //첫장은 커버, 2번째는 인덱스
                SnapsPage page = template.getPages().get(ii);
                if(page != null && page.type != null && page.type.equalsIgnoreCase("hidden")) {
                    template._hiddenPageList.add(0, page);
                    template.getPages().remove(page);
                }
            }
        }
    }
}
