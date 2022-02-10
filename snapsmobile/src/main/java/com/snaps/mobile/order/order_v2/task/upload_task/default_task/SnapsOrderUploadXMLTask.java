package com.snaps.mobile.order.order_v2.task.upload_task.default_task;

import com.snaps.common.structure.SnapsMakeXML;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.xml.GetMultiPartMethod;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.SnapsOrderBaseTask;

import java.io.File;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderUploadXMLTask extends SnapsOrderBaseTask {
    private static final String TAG = SnapsOrderUploadXMLTask.class.getSimpleName();

    private static final int LIMIT_TRY_UPLOAD_XML_COUNT = 3;

    private File saveXML;
    private File orderXML;
    private File optionXML;

    private String exceptionWhere = null;

    private SnapsOrderUploadXMLTask(SnapsOrderAttribute attribute) {
        super(attribute);
    }

    public static SnapsOrderUploadXMLTask createInstanceWithAttribute(SnapsOrderAttribute attribute) {
        return new SnapsOrderUploadXMLTask(attribute);
    }

    public void performMakeXML(final SnapsOrderResultListener listener) throws Exception {

        setTemplateInfo();

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                try {
                    SnapsLogger.appendOrderLog("start Make Xml.", true);

                    SnapsMakeXML makeXML = new SnapsMakeXML(getTemplate(), SystemUtil.getAppVersion(getActivity()));

                    processXMLByProduct(makeXML);

                    makeSaveXML(makeXML);

                    makeAuraXML(makeXML);

                    makeOptionXML(makeXML);

                    boolean isValidAuraOrderFile = orderXML != null && orderXML.length() > 0;

                    if (!isValidAuraOrderFile) {
                        isValidAuraOrderFile = orderXML != null && orderXML.length() > 0;
                        if (!isValidAuraOrderFile) {
                            SnapsLogger.appendOrderLog(orderXML != null  ? ("orderXML exist : " + orderXML.exists() + ", orderXML length : " + orderXML.length()) : "orderXML File is null.");
                            SnapsLogger.appendOrderLog(saveXML != null  ? "saveXML length : " + saveXML.length() : "saveXML File is null.");
                            SnapsLogger.appendOrderLog(optionXML != null  ? "optionXML length : " + optionXML.length() : "optionXML File is null.");
                            exceptionWhere = "orderXML is empty.";
                        }
                    }
                    return isValidAuraOrderFile;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    SnapsLogger.appendOrderLog("make xml exception : " + exceptionWhere + ", " + e.toString());
                }
                return false;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    listener.onSnapsOrderResultSucceed(null);
                } else {
                    listener.onSnapsOrderResultFailed(exceptionWhere, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_MAKE_XML);
                }
            }
        });
    }

    public void performUploadXML(final SnapsOrderResultListener listener) throws Exception {
        if (!CNetStatus.getInstance().isAliveNetwork(ContextUtil.getContext())) {
            listener.onSnapsOrderResultFailed(SnapsOrderConstants.eSnapsOrderUploadResultMsg.UPLOAD_FAILED_CAUSE_NOT_SUPPORT_NETWORK_STATE, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
            return;
        }

        ATask.executeVoidWithThreadPoolBoolean(new ATask.OnTaskResult() {

            String uploadResultMsg = "";
            @Override
            public void onPre() {}

            @Override
            public boolean onBG() {
                try {
                    return requestCompleteUploadXMLFiles();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }

            private boolean requestCompleteUploadXMLFiles() throws Exception {
                int tryCount = 0;
                boolean uploadResult = requestUploadXMLFilesAndGetResult();
                while (!uploadResult
                        && ++tryCount < LIMIT_TRY_UPLOAD_XML_COUNT) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                    uploadResult = requestUploadXMLFilesAndGetResult();
                }
                return uploadResult;
            }

            private boolean requestUploadXMLFilesAndGetResult() throws Exception {
                uploadResultMsg = requestUploadXMLFiles();
                return isUploadSuccessByResultStr(uploadResultMsg);
            }

            private boolean isUploadSuccessByResultStr(String resultStr) throws Exception {
                if (StringUtil.isEmpty(resultStr)) return false;
                String[] returnValue = resultStr.replace("||", "|").split("\\|");
                return returnValue[0].indexOf("SUCCESS") >= 0;
            }

            @Override
            public void onPost(boolean result) {
                if (result) {
                    //업로드 성공 후 자동 저장 파일 갱신
                    AutoSaveManager saveMan = AutoSaveManager.getInstance();
                    if (saveMan != null) {
                        saveMan.exportProjectInfo();
                    }

                    // @Marko
                    // 아크릴 키링 과 같은 accessories 를 전달 해야하는 상품의 경우, 최초 prjoption xml 에 저장 한 뒤, 그 뒤에는 쓰지 말아야 한다 (백엔트 팀과 협의된 내용)
                    // 따라서 업로드가 완료된 현 시점에 template > product option 에 저장된 accessories 의 정보를 초기화 한다.
                    getTemplate().removeAccessoriesInfo();

                    listener.onSnapsOrderResultSucceed(null);
                } else {
                    //업로드 실패시 확인을 위한 로그
                    if (uploadResultMsg != null && uploadResultMsg.equalsIgnoreCase(SnapsOrderConstants.EXCEPTION_MSG_UNKNOWN_HOST_ERROR)) { //Unkown Host exception이 발생하면 Wifi연결이 안 되어 있을 수도 있기 때문에 별도로 오류 처리를 한다
                        listener.onSnapsOrderResultFailed(uploadResultMsg, SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    } else {
                        listener.onSnapsOrderResultFailed((exceptionWhere + "\n" + uploadResultMsg), SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    }
                }
            }
        });
    }

    /**
     * Save XML 생성.
     */
    private File makeSaveXMLString(SnapsMakeXML xml) {
        return xml.saveXmlFile();
    }

    /**
     * AuraOrder XML 생성.
     */
    private File makeAuraOrderXMLString(SnapsMakeXML xml) throws Exception {
        return xml.auraOrderXmlFile(SnapsOrderConstants.ORDER_STAT_CODE);
    }

    /**
     * Option XML 생성.
     */
    private File makeOptionXMLString(SnapsMakeXML xml) {
        return xml.optionXmlFile();
    }

    private String requestUploadXMLFiles() {
        exceptionWhere = "upload xml files err";
        return GetMultiPartMethod.getProejctUpload(SnapsLoginManager.getUUserNo(getActivity()), SnapsOrderConstants.ORDER_STAT_CODE, saveXML, orderXML, optionXML, null, false, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
    }

    private void setTemplateInfo() throws Exception {
        SnapsTemplate snapsTemplate = getTemplate();
        if (snapsTemplate == null || snapsTemplate.info == null) return;

        if (snapsTemplate.info.getCoverType() == SnapsTemplateInfo.COVER_TYPE.HARD_COVER) {
            float mmEditW = Float.parseFloat(snapsTemplate.info.F_COVER_MM_WIDTH) + snapsTemplate.getHardCoverSpineWidth();
            float mmEditH = Float.parseFloat(snapsTemplate.info.F_COVER_MM_HEIGHT);

            snapsTemplate.info.F_COVER_VIRTUAL_WIDTH = String.valueOf((mmEditW * snapsTemplate.info.getPXMM()));
            snapsTemplate.info.F_COVER_VIRTUAL_HEIGHT = String.valueOf((mmEditH * snapsTemplate.info.getPXMM()));
        }

        snapsTemplate.info.F_PROD_CODE = Config.getPROD_CODE();
    }

    private void processXMLByProduct(SnapsMakeXML xml) {
        if (xml == null) return;

        if (Const_PRODUCT.isCardProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            xml.setHiddenPageList(getHiddenPageList());
        } else if (isExistBackPageAndHiddenPageProduct()) {
            xml.setBackPageList(getBackPageList());
            xml.setHiddenPageList(getHiddenPageList());
        }
    }

    private boolean isExistBackPageAndHiddenPageProduct() {
        return Const_PRODUCT.isPackageProduct()
                || Const_PRODUCT.isPhotoCardProduct()
                || Const_PRODUCT.isNewWalletProduct()
                || Const_PRODUCT.isNewYearsCardProduct()
                || Config.isWoodBlockCalendar();
    }

    private void makeSaveXML(SnapsMakeXML makeXML) throws Exception {
        SnapsLogger.appendOrderLog("start Make Save Xml.");
        exceptionWhere = "make saveXML err";
        saveXML = makeSaveXMLString(makeXML);
    }

    private void makeAuraXML(SnapsMakeXML makeXML) throws Exception {
        SnapsLogger.appendOrderLog("start Make Aura Xml.");
        exceptionWhere = "make auraXML err";
        orderXML = makeAuraOrderXMLString(makeXML);// 여기서 에러나면 실패로~~
    }

    private void makeOptionXML(SnapsMakeXML makeXML) throws Exception {
        SnapsLogger.appendOrderLog("start Make Option Xml.");
        exceptionWhere = "make optionXML err";
        optionXML = makeOptionXMLString(makeXML);
    }
}
