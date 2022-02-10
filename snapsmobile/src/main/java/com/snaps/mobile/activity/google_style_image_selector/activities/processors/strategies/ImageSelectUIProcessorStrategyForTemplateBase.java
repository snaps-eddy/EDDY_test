package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;

import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.thread.ATask;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

/**
 * Created by ysjeong on 2017. 7. 7..
 */

public abstract class ImageSelectUIProcessorStrategyForTemplateBase implements IImageSelectUIProcessorStrategy {
    private static final String TAG = ImageSelectUIProcessorStrategyForTemplateBase.class.getSimpleName();
    protected DialogDefaultProgress pageProgress;
    protected ImageSelectUIProcessor uiProcessor;
    protected ImageSelectActivityV2 activity;

    protected void loadTemplate() {
        if (uiProcessor == null) return;

        //Network Disabled
        CNetStatus netStatus = CNetStatus.getInstance();
        if (!netStatus.isAliveNetwork(activity)) {
            //템플릿 로딩 실패
            activity.onTemplateDownloadErrorOccur(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.TEMPLATE_DOWNLOAD_ERROR);
            return;
        }

        requestGetTemplate(uiProcessor.getIntentData());
    }

    protected abstract void handleGetTemplateBeforeTask(); //템플릿을 본격적으로 받기 전에 작업
    protected abstract void handleGetTemplateAfterTask(SnapsTemplate template); //템플릿을 받은 직후 작업

    //서버로 부터 템플릿 XML 파일을 받아서 SnapsTempleteManager에 set해 놓는다.
    protected void requestGetTemplate(final ImageSelectIntentData intentData) {
        // 템플릿을 파일로 저장을 한다음 파일 패스를 넘긴다.(편집화면에서 사용하기 위함...)
        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            @Override
            public void onPre() {
                try {
                    if (pageProgress != null)
                        pageProgress.show();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public boolean onBG() {
                handleGetTemplateBeforeTask();

                SnapsTemplate template = loadTemplate(intentData);

                handleGetTemplateAfterTask(template);

                SnapsTemplateManager.getInstance().setSnapsTemplate(template);
                return template != null;
            }

            @Override
            public void onPost(boolean result) {
                try {
                    if (pageProgress != null)
                        pageProgress.dismiss();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                if (result) {
                    try {
                        Config.checkServiceThumbnailSimpleFileDir();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }

                    onTemplateLoaded();
                } else {
                    //템플릿 로딩 실패
                    activity.onTemplateDownloadErrorOccur(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.TEMPLATE_DOWNLOAD_ERROR);
                }
            }
        });
    }

    protected SnapsTemplate loadTemplate(final ImageSelectIntentData intentData) {
        try {
            Config.checkServiceThumbnailSimpleFileDir();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        //템플릿을 파싱을 한다
        SnapsTemplateManager.getInstance().cleanInstance();
        return GetTemplateLoad.getTemplate(SnapsTemplate.getTemplateUrl(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    protected abstract void onTemplateLoaded();

    protected void setTemplateThumbnails() {
        SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
        if (uiProcessor == null || template == null || template.getPages() == null || template.getPages().isEmpty()) return;

        ImageSelectTrayBaseAdapter trayAdapter = uiProcessor.getTrayAdapter();
        if (trayAdapter == null) return;
        trayAdapter.setData(template.getPages());
    }
}
