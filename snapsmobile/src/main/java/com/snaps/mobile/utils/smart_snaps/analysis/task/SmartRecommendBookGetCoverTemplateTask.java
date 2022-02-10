package com.snaps.mobile.utils.smart_snaps.analysis.task;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemeCover;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsUtil;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;

import java.util.ArrayList;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static com.snaps.common.data.img.MyPhotoSelectImageData.INVALID_ROTATE_ANGLE;


/**
 * Created by ysjeong on 2018. 4. 24..
 */

public class SmartRecommendBookGetCoverTemplateTask extends SmartRecommendBookAnalysisBaseTask {
    private static final String TAG = SmartRecommendBookGetCoverTemplateTask.class.getSimpleName();
    public SmartRecommendBookGetCoverTemplateTask(Activity activity, SmartSnapsAnalysisListener analysisListener) {
        super(activity, analysisListener);
    }

    @Override
    public SmartSnapsConstants.eSmartSnapsAnalysisTaskType getTaskType() {
        return SmartSnapsConstants.eSmartSnapsAnalysisTaskType.GET_COVER_TEMPLATE;
    }

    @Override
    public void perform() {
        super.perform();

        try {
            requestGetCoverTemplate();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            sendException(e);
        }
    }

    private void requestGetCoverTemplate() throws Exception {
        final SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (smartSnapsManager.isExistCoverDataOfAnalysisPhotoBook()) {
            requestGetCoverLayoutList();
        } else {
            ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
                @Override
                public void onPre() {}

                @Override
                public boolean onBG() {
                    if (!Config.isValidProjCode())
                        return false;

                    StringBuilder urlBuilder = new StringBuilder(SnapsAPI.GET_API_SMART_SNAPS_GET_RECOMMEND_COVER_URL());
                    urlBuilder.append("&prmAppType=android").append("&prmChnlCode=").append(Config.getCHANNEL_CODE())
                            .append("&prmProjCode=").append(Config.getPROJ_CODE()).append("&prmTmplCode=").append(Config.getTMPL_CODE());

                    SnapsTemplate coverMultiTemplate = GetTemplateLoad.getTemplateByXmlPullParser(urlBuilder.toString(), false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    if (!isValidCoverMultiTemplate(coverMultiTemplate)) return false;

                    checkCartCoverExist(coverMultiTemplate);

                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.setCoverTemplateOfAnalysisPhotoBook(coverMultiTemplate);

                    insertImageOnCover(coverMultiTemplate);

                    String templateCode = geTemplateCodeOnCoverMultiTemplate(coverMultiTemplate);
                    if (!StringUtil.isEmpty(templateCode)) {
                        Config.setTMPL_CODE(templateCode);
                        return true;
                    } else {
                        checkOldProject(coverMultiTemplate); //예전에 만든 프로젝트는 없다...

                        templateCode = geTemplateCodeOnCoverMultiTemplate(coverMultiTemplate);

                        if (!StringUtil.isEmpty(templateCode)) {
                            Config.setTMPL_CODE(templateCode);
                            return true;
                        }
                    }

                    return false;
                }

                private void checkCartCoverExist(SnapsTemplate coverMultiTemplate) {
                    if (coverMultiTemplate == null || !Config.isFromCart()) return;

                    try {
                        SnapsTemplate cartTemplate = SnapsTemplateManager.getInstance().getSnapsTemplate();
                        if (cartTemplate == null || cartTemplate.getPages() == null || cartTemplate.getPages().isEmpty()) return;

                        SnapsPage cartCoverPage = cartTemplate.getPages().get(0);
                        if (cartCoverPage == null) return;

                        for (SnapsPage snapsPage : coverMultiTemplate.getPages()) {
                            if (snapsPage == null) continue;
                            if (snapsPage.multiformId != null && snapsPage.multiformId.equalsIgnoreCase(cartCoverPage.multiformId)) {
                                return;
                            }
                        }

                        coverMultiTemplate.getPages().remove(0);
                        coverMultiTemplate.getPages().add(0, cartCoverPage);
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }

                private void checkOldProject(SnapsTemplate coverMultiTemplate) {
                    if (StringUtil.isEmpty(Config.getTMPL_CODE())) return;
                    SnapsPage coverPage = getDefaultCoverPage(coverMultiTemplate);
                    coverPage.templateCode = Config.getTMPL_CODE();
                }

                @Override
                public void onPost(boolean result) {
                    if (result) {
                        requestGetCoverLayoutList();
                    } else {
                        sendFailed("failed get cover template.");
                    }
                }

                private boolean isValidCoverMultiTemplate(SnapsTemplate coverMultiTemplate ) {
                    if (coverMultiTemplate == null || coverMultiTemplate.getPages() == null || coverMultiTemplate.getPages().isEmpty()) return false;
                    for (SnapsPage coverPage : coverMultiTemplate.getPages()) {
                        if (coverPage == null || StringUtil.isEmpty(coverPage.templateCode))
                            return false;
                    }
                    return true;
                }

                private void insertImageOnCover(SnapsTemplate coverMultiTemplate) {
                    try {
                        insertImageListToTemplateByAnalysisKey(coverMultiTemplate, SmartSnapsUtil.getCoverImageData(getGalleryList()));
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }

                private void insertImageListToTemplateByAnalysisKey(SnapsTemplate coverMultiTemplate, MyPhotoSelectImageData coverImageData) throws Exception {
                    if (coverMultiTemplate == null || coverImageData == null) return;

                    MyPhotoSelectImageData copiedSubCoverImageData = createCopiedSubCoverImageData(coverImageData);
                    for (int index = 0; index < coverMultiTemplate.getPages().size(); index++) {
                        SnapsPage page = coverMultiTemplate.getPages().get(index);

                        boolean isSameLoadedTemplateCoverPage = compareLoadedTemplateWithPage(page);
                        if (isSameLoadedTemplateCoverPage) {
                            coverMultiTemplate.getPages().remove(index);

                            SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                            SnapsTemplate template = snapsTemplateManager.getSnapsTemplate();
                            if (template != null && template.getPages() != null && !template.getPages().isEmpty()) {
                                SnapsPage coverPage = template.getPages().get(0);
                                coverMultiTemplate.getPages().add(index, coverPage);
                            }

                            if (Config.isFromCart()) {
                                continue;
                            }
                        }

                        for (int i = 0; i < page.getLayoutList().size(); i++) {
                            SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                            if (layout.type.equalsIgnoreCase("browse_file")) {
                                if (isSameLoadedTemplateCoverPage) {
                                    layout.imgData = coverImageData;

                                    if (Config.isFromCart()) {
                                        ;
                                    } else {
                                        SmartSnapsUtil.initPageFullPositionBySmartImageAreaInfo(getActivity(), layout.imgData, layout);

                                        SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(getActivity(), layout);
                                    }
                                } else {
                                    MyPhotoSelectImageData copiedImageData = new MyPhotoSelectImageData();
                                    if (Config.isFromCart() && copiedSubCoverImageData.isApplyEffect) {
                                        copiedImageData.weakCopy(copiedSubCoverImageData);
                                        layout.imgData = copiedImageData;

                                        layout.imgData.isApplyEffect = true;
                                        layout.imgData.EFFECT_PATH = copiedSubCoverImageData.EFFECT_PATH;
                                        layout.imgData.EFFECT_THUMBNAIL_PATH = copiedSubCoverImageData.EFFECT_THUMBNAIL_PATH;
                                        layout.imgData.EFFECT_TYPE = copiedSubCoverImageData.EFFECT_TYPE;
                                        layout.imgData.ORIGINAL_ROTATE_ANGLE = copiedSubCoverImageData.ORIGINAL_ROTATE_ANGLE;
                                        layout.imgData.ORIGINAL_THUMB_ROTATE_ANGLE = copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE;
                                        layout.imgData.ROTATE_ANGLE = copiedSubCoverImageData.ROTATE_ANGLE;
                                        layout.imgData.ROTATE_ANGLE_THUMB = copiedSubCoverImageData.ROTATE_ANGLE_THUMB;
                                    } else {
                                        copiedImageData.weakCopy(coverImageData);
                                        layout.imgData = coverImageData;
                                    }

                                    SmartSnapsUtil.initPageFullPositionBySmartImageAreaInfo(getActivity(), layout.imgData, layout);

                                    SmartSnapsUtil.fixLayoutControlCropAreaBySmartSnapsAreaInfo(getActivity(), layout);
                                }
                                break;
                            }
                        }
                    }
                }

                private MyPhotoSelectImageData createCopiedSubCoverImageData(MyPhotoSelectImageData coverImageData) {
                    MyPhotoSelectImageData copiedSubCoverImageData = new MyPhotoSelectImageData();
                    copiedSubCoverImageData.weakCopy(coverImageData);
                    if (Config.isFromCart() && coverImageData.isApplyEffect) {
                        copiedSubCoverImageData.isApplyEffect = true;
                        copiedSubCoverImageData.EFFECT_PATH = coverImageData.EFFECT_PATH;
                        copiedSubCoverImageData.EFFECT_THUMBNAIL_PATH = coverImageData.EFFECT_THUMBNAIL_PATH;
                        copiedSubCoverImageData.EFFECT_TYPE = coverImageData.EFFECT_TYPE;
                        copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE = coverImageData.ORIGINAL_THUMB_ROTATE_ANGLE;
                        copiedSubCoverImageData.ORIGINAL_ROTATE_ANGLE = coverImageData.ORIGINAL_ROTATE_ANGLE;
                        copiedSubCoverImageData.ROTATE_ANGLE_THUMB = coverImageData.ROTATE_ANGLE_THUMB;

                        if (copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE != copiedSubCoverImageData.ROTATE_ANGLE_THUMB || copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE == INVALID_ROTATE_ANGLE) {
                            try {
                                if (!ImageFilters.updateEffectImageToOrgAngle(getActivity(), copiedSubCoverImageData)) {
                                    copiedSubCoverImageData.isApplyEffect = false;
                                }
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                                copiedSubCoverImageData.isApplyEffect = false;
                            }

                            if (copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE != INVALID_ROTATE_ANGLE) //만약 ratio 오류같은 게 발생한다면 이부분을 의심해보자
                                copiedSubCoverImageData.ROTATE_ANGLE_THUMB = copiedSubCoverImageData.ORIGINAL_THUMB_ROTATE_ANGLE;
                        }
                    }
                    return copiedSubCoverImageData;
                }

                private boolean compareLoadedTemplateWithPage(SnapsPage snapsPage) {
                    if (snapsPage == null) return false;
                    SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                    SnapsTemplate template = snapsTemplateManager.getSnapsTemplate();
                    if (template == null || template.getPages() == null || template.getPages().isEmpty()) return false;
                    SmartSnapsManager snapsManager = SmartSnapsManager.getInstance();
                    if (!snapsManager.isContainCoverPageOnPageList(template.getPages())) return false;

                    SnapsPage coverPage = template.getPages().get(0);
                    return coverPage != null && ( (coverPage.multiformId != null && coverPage.multiformId.equalsIgnoreCase(snapsPage.multiformId))
                                    || (coverPage.orgMultiformId != null && coverPage.orgMultiformId.equalsIgnoreCase(snapsPage.orgMultiformId)) );
                }

                private String geTemplateCodeOnCoverMultiTemplate(SnapsTemplate template) {
                    SnapsPage coverPage = getDefaultCoverPage(template);
                    return coverPage != null ? coverPage.templateCode : "";
                }

                private SnapsPage getDefaultCoverPage(SnapsTemplate template) {
                    if (template == null || template.getPages() == null || template.getPages().isEmpty()) return null;
                    return template.getPages().get(0);
                }
            });
        }
    }

    private void requestGetCoverLayoutList() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (smartSnapsManager.isExistCoverDesignListOfAnalysisPhotoBook()) {
            sendComplete();
        } else {
            ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {
                @Override
                public void onPre() {}

                @Override
                public boolean onBG() {
                    Xml_ThemeCover xmlThemeCover = GetParsedXml.getSmartSnapsAnalysisPhotoBookCoverLayoutList(Config.getPROD_CODE(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
                    SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                    smartSnapsManager.setCoverDesignListOfAnalysisPhotoBook(xmlThemeCover);
                    return xmlThemeCover != null;
                }

                @Override
                public void onPost(boolean result) {
                    if (result) {
                        sendComplete();
                    } else {
                        sendFailed("failed cover layout list.");
                    }
                }
            });
        }
    }

    public ArrayList<MyPhotoSelectImageData> getGalleryList() {
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager == null) {
            DataTransManager.notifyAppFinish(getActivity());
            return null;
        }

        return dataTransManager.getPhotoImageDataList();
    }
}
