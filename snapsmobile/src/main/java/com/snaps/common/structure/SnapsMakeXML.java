package com.snaps.common.structure;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.control.LineText;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap.EffectType;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

/**
 * com.snaps.kakao.structure SnapsMakeXML.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 27.
 * @Version :
 */
public class SnapsMakeXML {
    private static final String TAG = SnapsMakeXML.class.getSimpleName();
    private String year;
    private String month;
    private SnapsTemplate template;
    private ArrayList<SnapsPage> backPageList;
    private ArrayList<SnapsPage> hiddenPageList;
    private String version;

    public SnapsMakeXML(SnapsTemplate temp, String ver) {
        Date date = new Date();

        this.year = String.valueOf(date.getYear() + 1900);
        this.month = String.valueOf(date.getMonth() + 1);
        this.template = temp;
        this.version = ver;
    }

    /***
     * 사진인화 인경우만 사용을 한다.
     *
     * @param ver
     */
    public SnapsMakeXML(String ver) {
        Calendar cal = Calendar.getInstance();
        this.year = String.valueOf(cal.get(Calendar.YEAR));
        this.month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        this.version = ver;
        this.template = null;
    }

    public void setBackPageList(ArrayList<SnapsPage> list) {
        backPageList = list;
    }

    public void setHiddenPageList(ArrayList<SnapsPage> list) {
        hiddenPageList = list;
    }

    /**
     * @return
     */
    public File saveXmlFile() {
        File saveFile = null;

        try {
            saveFile = Config.getPROJECT_FILE(Config.SAVE_XML_FILE_NAME);
            if (saveFile == null) throw new SnapsIOException("failed make saveXML");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("xml save xml exeption0 : " + e.toString());
            return null;
        }

        SnapsLogger.appendOrderLog("save XML make point1");
        if (!saveFile.exists()) {
            try {
                if (!saveFile.createNewFile()) {
                    SnapsLogger.appendOrderLog("failed Make save XML file");
                    return null;
                }
            } catch (IOException e) {
                SnapsLogger.appendOrderLog("xml save xml exeption1 : " + e.toString());
                Dlog.e(TAG, e);
            }
        }

        FileOutputStream fileStream = null;
        SnapsLogger.appendOrderLog("save XML make point2");
        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            SnapsLogger.appendOrderLog("xml save xml exeption2 : " + e.toString());
            Dlog.e(TAG, e);
        }

        try {
            SnapsLogger.appendOrderLog("save XML make point3");
            SnapsSaveXML save = new SnapsSaveXML(fileStream);
            makeSaveXML(save);

            SnapsLogger.appendOrderLog("succeed make save xml.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("xml save xml exeption3 : " + e.toString());
        } finally {
            try {
                if (fileStream != null)
                    fileStream.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        return saveFile;
    }

    /**
     * @return
     */
    public String saveXmlString() {
        // 신규 상품 저장
        String saveXml = "";
        StringWriter writer = new StringWriter();

        try {
            SnapsSaveXML save = new SnapsSaveXML(writer);
            makeSaveXML(save);

            saveXml = writer.toString().replace("&lt;", "<").replace("&gt;", ">");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return saveXml;
    }

    /**
     * @param orderCode
     * @return
     * @throws IOException
     */
    public File auraOrderXmlFile(String orderCode) {
        File saveFile = null;
        try {
            saveFile = Config.getPROJECT_FILE(Config.AURA_ORDER_XML_FILE_NAME);
            if (saveFile == null) throw new SnapsIOException("failed make aura file");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("failed Make aura XML file pt1 :" + e.toString());
            return null;
        }

        if (!saveFile.exists()) {
            try {
                boolean isCreatedFile = saveFile.createNewFile();
                if (!isCreatedFile) {
                    SnapsLogger.appendOrderLog("failed Make aura XML file pt2");
                }
            } catch (IOException e) {
                Dlog.e(TAG, e);
                SnapsLogger.appendOrderLog("Make Aura Xml exception1  : " + e.toString());
            }
        }

        FileOutputStream fileStream = null;

        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("Make Aura Xml exception2  : " + e.toString());
        }

        try {
            SnapsAuraOrderXML order = new SnapsAuraOrderXML(fileStream);
            makeAuraOrderXml(order, orderCode);
            fileStream.close();

            SnapsLogger.appendOrderLog("succeed make aura xml.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("Make Aura Xml exception3  : " + e.toString());
        }

        return saveFile;
    }

    /**
     * @param orderCode
     * @return
     */
    public String auraOrderXmlString(String orderCode) {
        StringWriter writer = new StringWriter();

        try {
            SnapsAuraOrderXML auraOrder = new SnapsAuraOrderXML(writer);
            makeAuraOrderXml(auraOrder, orderCode);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return writer.toString();
    }

    /**
     * @return
     */
    public File optionXmlFile() {

        File saveFile = null;
        try {
            saveFile = Config.getPROJECT_FILE(Config.OPTION_XML_FILE_NAME);
            if (saveFile == null) throw new SnapsIOException("failed make saveFile");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }

        if (!saveFile.exists()) {
            try {
                if (!saveFile.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        FileOutputStream fileStream = null;

        try {
            fileStream = new FileOutputStream(saveFile);
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
        }

        try {
            SnapsOptionXML option = new SnapsOptionXML(fileStream);
            makeOptionXml(option);
            fileStream.close();

            SnapsLogger.appendOrderLog("succeed make option xml.");

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return saveFile;
    }

    /**
     * @return
     */
    public String optionXmlString() {
        StringWriter writer = new StringWriter();

        try {
            SnapsOptionXML option = new SnapsOptionXML(writer);
            makeOptionXml(option);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return writer.toString();
    }

    public void makePrintSaveXML(SnapsSaveXML save) {
        /*
         * "<?xml version="1.0" encoding="utf-8" ?> <basket version="2.5.0.0">" <item
         * id=\"photo\" maker=\"snaps\" editdate=\"2013-12-10 15:23:40\" type=\"photo\" check=\"false\" regDate=\"2013.12.10 15:23\">" <photoOption
         * paperMatch=\"true\" glossy=\"true\" adjustBright=\"true\" orderCount=\"1\" DateApplyAll=\"false\"/>" + "<scene id=\"4X6\">" + "<photo orgPath=\"%s\" " orgSize=\"551 720\" " +
         * "glossy=\"true\" paperMatch=\"true\" autoBright=\"true\" " recommend=\
         * "false\" orderCount=\"1\" orientation=\"0\" trimPos=\"-1.0000\" ucloudImgPath=\"%s\" imgYear=\"2013\" imgSeq=\"%s\" uploadPath=\"%s\"/>" + "</scene>" + "</item>" </basket>";
         */
        try {
            save.startTag(null, "basket");
            save.attribute(null, "version", "2.5.0.0");
            save.startTag(null, "item");
            save.attribute(null, "id", "photo");
            save.attribute(null, "maker", "snaps");
            save.attribute(null, "editdate", "2013-12-10-10 15:15:15");
            save.attribute(null, "type", "photo");
            save.attribute(null, "check", "false");
            save.attribute(null, "regDate", "2013-12-10-10 15:15:15");

            save.startTag(null, "photoOption");
            save.attribute(null, "paperMatch", "true"); // paper full(기본)/image
            // full
            save.attribute(null, "glossy", "true"); // 효과(기본.)
            save.attribute(null, "adjustBright", "true");// 밝게 보정 여부(기본 밝게 보)
            save.attribute(null, "orderCount", "1"); // 주문건수 장당..(기본 1장)
            save.attribute(null, "DateApplyAll", "false");// 촬영일 추가 여부 (기본 없음)
            save.endTag(null, "photoOption");

            save.startTag(null, "scene");
            save.attribute(null, "id", "4x6");

            save.startTag(null, "photo");
            save.attribute(null, "orgPath", "");
            save.attribute(null, "orgSize", "551 720");
            save.attribute(null, "glossy", "true");
            save.attribute(null, "paperMatch", "true");
            save.attribute(null, "autoBright", "true");
            save.attribute(null, "recommend", "false");
            save.attribute(null, "orderCount", "1");
            save.attribute(null, "orientation", "0");
            save.attribute(null, "trimPos", "-1.0000");
            save.attribute(null, "ucloudImgPath", "1426071630938083.jpg");
            save.attribute(null, "imgYear", "2013");
            save.attribute(null, "imgSeq", "0271670940");
            save.attribute(null, "uploadPath", "2013121015234036284.jpg");

            save.endTag(null, "photo");

            save.endTag(null, "scene");

            save.endTag(null, "item");
            save.endTag(null, "basket");
            save.endDocument();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void makePrintAuraOrderXml(SnapsAuraOrderXML auraOrder, String orderCode) throws Exception {
        /*
         * "<?xml version=\"1.0\" encoding=\"utf-8\" ?><Order code=\"%s\">" + "<Application name=\"SmartPhoto\" version=\"2.5.4.58\"/>" <item file=\
         * "%s\" prod_code=\"00800100010003\" prod_name=\"4 X 6\" prod_real_name=\"4 X 6\" prod_nick_name=\"4 X 6\" glss_type=\"glossy\" edge_type=\"noedge\" pool_type=\"paper\" " brht_type=\
         * "yes\" show_date=\"no\" rcmm_yorn=\"no\" prnt_cnt=\"1\" sell_price=\"139\" imgYear=\"2013\" imgSeq=\"%s\" x=\"36\" y=\"0\" width=\"479\" height=\"720\">" <editinfo
         * orientation=\"0\" scale=\"0.7639\" editWidth=\"550\" editHeight=\"550\"/>" + "</item>" + "</Order>"
         */
        auraOrder.startTag(null, "Order");
        auraOrder.attribute(null, "code", orderCode);

        // TODO : Application 정보 변경.
        auraOrder.startTag(null, "Application");
        auraOrder.attribute(null, "name", Config.APPLICATION_NAME);
        auraOrder.attribute(null, "version", version);
        auraOrder.endTag(null, "Application");
        auraOrder.startTag(null, "item");

        auraOrder.attribute(null, "file", "");

        auraOrder.attribute(null, "prod_code", "00800100010003");
        auraOrder.attribute(null, "prod_name", "4 X 6");
        auraOrder.attribute(null, "prod_real_name", "4 X 6");
        auraOrder.attribute(null, "prod_nick_name", "");
        auraOrder.attribute(null, "glss_type", "glossy");
        auraOrder.attribute(null, "edge_type", "noedge");
        auraOrder.attribute(null, "pool_type", "paper");
        auraOrder.attribute(null, "brht_type", "yes");
        auraOrder.attribute(null, "show_date", "no");
        auraOrder.attribute(null, "rcmm_yorn", "no");
        auraOrder.attribute(null, "prnt_cnt", "1");

        auraOrder.attribute(null, "sell_price", "139");
        auraOrder.attribute(null, "imgYear", "2013");
        auraOrder.attribute(null, "imgSeq", "0271670940");
        auraOrder.attribute(null, "x", "36");
        auraOrder.attribute(null, "y", "0");
        auraOrder.attribute(null, "width", "479");
        auraOrder.attribute(null, "height", "720");

        auraOrder.startTag(null, "editInfo");

        auraOrder.attribute(null, "orientation", "0");
        auraOrder.attribute(null, "scale", "0.7639");
        auraOrder.attribute(null, "editWidth", "550");
        auraOrder.attribute(null, "editHeight", "550");

        auraOrder.endTag(null, "editInfo");

        auraOrder.endTag(null, "item");
        auraOrder.endTag(null, "Order");

        auraOrder.endDocument();
    }

    public void makePrintOptionXml(SnapsOptionXML option) {

        try {
            option.startTag(null, "string");
            option.startTag(null, "ImageOrderInfo");

            option.addTag(null, "F_ORDR_CODE", "DDC1003587");
            option.addTag(null, "F_ALBM_ID", "20131210005339");
            option.addTag(null, "F_PROD_CODE", "00800100010003");
            option.addTag(null, "F_IMGX_YEAR", "2013");
            option.addTag(null, "F_IMGX_SQNC", "0271670940");
            option.addTag(null, "F_PRNT_CNT", "1");

            option.addTag(null, "F_GLSS_TYPE", "G");
            option.addTag(null, "F_EDGE_TYPE", "X");
            option.addTag(null, "F_POOL_TYPE", "P");
            option.addTag(null, "F_BRHT_TYPE", "Y");
            option.addTag(null, "F_SHOW_DATE", "N");
            option.addTag(null, "F_RCMM_YORN", "N");
            option.addTag(null, "F_UNIT_COST", "139");
            option.addTag(null, "F_SELL_PRICE", "139");
            option.addTag(null, "F_TRIM_CORD", null);

            option.endTag(null, "ImageOrderInfo");
            option.endTag(null, "string");

            option.endDocument();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean isExistHiddenPageProduct() { //그냥 히든 페이지를 뒤지면 되는거 아닌가....;
        return Const_PRODUCT.isPackageProduct()
                || Const_PRODUCT.isPhotoCardProduct()
                || Const_PRODUCT.isNewWalletProduct()
                || Const_PRODUCT.isNewYearsCardProduct()
                || Const_PRODUCT.isTransparencyPhotoCardProduct()
                || Config.isWoodBlockCalendar();
    }

    /**
     * @param save
     */
    public void makeSaveXML(SnapsSaveXML save) {
        try {
            SnapsLogger.appendOrderLog("makeSaveXML point1");

            save.startTag(null, "basket");
            save.attribute(null, "version", version);

            // TODO : Version.
            save.startTag(null, "item");

            save.getAlbumInfoXml_save_item(template, Config.getPROJ_NAME(), year, month);
            save.getAlbumInfoXml_save_info(template.info, Config.getYEAR_KEY(), Config.getSQNC_KEY());
            save.getAlbumInfoXml_save_price(template.priceList.get(0));

            SnapsLogger.appendOrderLog("makeSaveXML point2");

            ArrayList<SnapsPage> snapsPageList = null;
            if (Const_PRODUCT.isBothSidePrintProduct()) {
                snapsPageList = mergeBackAndFrontPageList(true);
            } else {
                snapsPageList = template.getPages();
            }

            //패키지 킷 상품(히든 된 커버를 다시 끼워 넣는 작업)
            if (isExistHiddenPageProduct())
                snapsPageList = getInsertedHiddenPages(snapsPageList);

            if (Const_PRODUCT.isCardProduct()) {
                if (Const_PRODUCT.isCardShapeFolder()) { //FIXME 만약, 단면에도 히든이 들어간다면 조건 변경이 필요 함.
                    snapsPageList = getInsertedHiddenPages(snapsPageList);
                }
            }

            SnapsLogger.appendOrderLog("makeSaveXML point3");

            for (SnapsPage page : snapsPageList) {
                page.getSavePageXML(save);
            }

            makeSummeryXml(save);

            save.endTag(null, "item");

            save.startTag(null, "del_item");

            SnapsLogger.appendOrderLog("makeSaveXML point4");

            for (SnapsDelImage img : template.delimgList) {
                img.getSaveXML(save);
            }
            save.endTag(null, "del_item");
            save.getAlbumInfoXml_save_clientInfo(template.clientInfo);

            SnapsLogger.appendOrderLog("makeSaveXML point5");

            // 템플릿 정보 저장..
            template.info.getSaveXML(save);
            // 가격정보 저장...
            for (SnapsTemplatePrice p : template.priceList) {
                p.getSaveXML(save);
            }

            template.getProductOption().getSaveXML(save);

            SnapsLogger.appendOrderLog("makeSaveXML point6");

            // 인테이어 액자인경우 frameInfo를 저장한다.
            // 휴대폰 케이스도 추가.
            // 이제 새로운 폰케이스는 frame info 를 사용하지 않는다.
            if (Const_PRODUCT.isInteiorFrame() || Const_PRODUCT.isLegacyPhoneCaseProduct()) {
                template.info.frameInfo.getSaveXML(save);
            }

            save.endTag(null, "basket");
            save.endDocument();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void copyLayoutControlAttributes(SnapsLayoutControl controlOrigin, SnapsLayoutControl controlTarget) {
        if (controlOrigin == null || controlTarget == null) return;
        controlTarget.x = controlOrigin.x;
        controlTarget.y = controlOrigin.y;
        controlTarget.imageLoadType = controlOrigin.imageLoadType;
        controlTarget.angle = controlOrigin.angle;
        controlTarget.freeAngle = controlOrigin.freeAngle;
        controlTarget.width = controlOrigin.width;
        controlTarget.height = controlOrigin.height;
        controlTarget.img_x = controlOrigin.img_x;
        controlTarget.img_y = controlOrigin.img_y;
        controlTarget.img_width = controlOrigin.img_width;
        controlTarget.img_height = controlOrigin.img_height;
        controlTarget.h = controlOrigin.h;
        controlTarget.w = controlOrigin.w;
        controlTarget.imagePath = controlOrigin.imagePath;
        controlTarget.imgSeq = controlOrigin.imgSeq;
        controlTarget.imgYear = controlOrigin.imgYear;
        controlTarget.isNoPrintImage = controlOrigin.isNoPrintImage;
        controlTarget.oriPath = controlOrigin.oriPath;
    }

    /**
     * 패키지 킷 상품은 앞, 뒤 구분이 있어서 aura를 쓸 때,
     * 앞, 뒤를 합쳐 준다.
     */
    private ArrayList<SnapsPage> mergeBackAndFrontPageList(boolean isGrayScale) {
        if (template == null || template.getPages() == null || backPageList == null) return null;
        ArrayList<SnapsPage> mergedList = new ArrayList<SnapsPage>();
        for (int ii = 0; ii < template.getPages().size(); ii++) {
            SnapsPage frontPage = template.getPages().get(ii);
            SnapsPage backPage = backPageList.get(ii);

            if (frontPage == null || backPage == null) continue;

            //포스트 카드는 뒷면에 사진이 안들어 간다..
            if (!Const_PRODUCT.isPostCardProduct()) {
                //Copy ImageControl
                ArrayList<SnapsControl> frontPageControls = frontPage.getLayoutList();
                ArrayList<SnapsControl> backPageControls = backPage.getLayoutList();
                if (frontPageControls == null || backPageControls == null) continue;

                if (frontPageControls.size() == backPageControls.size()) {
                    for (int jj = 0; jj < frontPageControls.size(); jj++) {
                        SnapsControl frontControl = frontPageControls.get(jj);
                        if (frontControl != null && frontControl instanceof SnapsLayoutControl) {
                            SnapsLayoutControl frontLayoutControl = (SnapsLayoutControl) frontControl;
                            MyPhotoSelectImageData frontImageData = frontLayoutControl.imgData;
                            if (frontImageData == null || frontImageData.PATH == null || frontImageData.PATH.length() < 1)
                                continue;

                            SnapsLayoutControl backLayoutControl = (SnapsLayoutControl) backPageControls.get(jj);
                            MyPhotoSelectImageData backImageData = new MyPhotoSelectImageData();
                            backImageData.set(frontImageData);
                            backLayoutControl.imgData = backImageData;

                            if (isGrayScale) {
                                backImageData.isApplyEffect = true;
                                backImageData.EFFECT_TYPE = EffectType.GRAY_SCALE.toString();
                            }

                            copyLayoutControlAttributes(frontLayoutControl, backLayoutControl);
                        }
                    }
                } else { //이럴일이 있으려나...
                    int findIdx = 0;
                    for (int jj = 0; jj < frontPageControls.size(); jj++) {
                        SnapsControl frontControl = frontPageControls.get(jj);
                        if (frontControl != null && frontControl instanceof SnapsLayoutControl) {
                            SnapsLayoutControl frontLayoutControl = (SnapsLayoutControl) frontControl;
                            MyPhotoSelectImageData frontImageData = frontLayoutControl.imgData;
                            if (frontImageData == null || frontImageData.PATH == null || frontImageData.PATH.length() < 1)
                                continue;

                            for (int kk = findIdx; kk < backPageControls.size(); kk++) {
                                SnapsControl backControl = backPageControls.get(kk);
                                if (backControl == null || !(backControl instanceof SnapsLayoutControl))
                                    continue;
                                SnapsLayoutControl backLayoutControl = (SnapsLayoutControl) backPageControls.get(kk);
                                MyPhotoSelectImageData backImageData = new MyPhotoSelectImageData();
                                backImageData.set(frontImageData);
                                backLayoutControl.imgData = backImageData;

                                if (isGrayScale) {
                                    backImageData.isApplyEffect = true;
                                    backImageData.EFFECT_TYPE = EffectType.GRAY_SCALE.toString();
                                }

                                copyLayoutControlAttributes(frontLayoutControl, backLayoutControl);
                                findIdx++;
                                break;
                            }
                        }
                    }
                }
            }

            mergedList.add(frontPage);
            mergedList.add(backPage);
        }

        return mergedList;
    }

    private void insertPolaroidBackText(ArrayList<SnapsPage> snapsPages) {
        if (snapsPages == null) return;

        for (int ii = 0; ii < snapsPages.size(); ii++) {
            SnapsPage page = snapsPages.get(ii);
            if (page == null || page.side == null || !page.side.equalsIgnoreCase("back")) continue;

            ArrayList<SnapsControl> textControls = page.getTextControlList();
            if (textControls == null || textControls.size() < 2) continue;

            SnapsPage frontPage = snapsPages.get(ii - 1);
            if (frontPage == null) continue;

            ArrayList<SnapsControl> layers = frontPage.getLayerLayouts();
            if (layers == null || layers.isEmpty()) continue;

            String photoTakenTime = "";
            SnapsLayoutControl imageControl = (SnapsLayoutControl) layers.get(0);
            MyPhotoSelectImageData imageData = imageControl.imgData;
            if (imageData != null) {
                //혹시라도 이상한 날짜가 들어간다면 오늘 날짜를 박아버린다.
                imageData.photoTakenDateTime = StringUtil.fixValidTakenTime(imageData.photoTakenDateTime);
                if (imageData.photoTakenDateTime > 0)
                    photoTakenTime = StringUtil.convertTimeLongToStr(imageData.photoTakenDateTime, "yyyyMMdd");
            }

            //0번째는 날짜, 1번째는 snaps
            SnapsTextControl dateControl = (SnapsTextControl) textControls.get(0);
            if (dateControl.textList == null) dateControl.textList = new ArrayList<>();
//            dateControl.format.align = "center";  //Ben 아놔 하드코딩
//            dateControl.format.fontSize = "12";     //Ben 아놔 하드코딩
//            dateControl.format.fontFace = "Aileron300";     //Ben 아놔 하드코딩
//            dateControl.format.fontColor = "a6a6a6";        //Ben 아놔 하드코딩
            dateControl.textList.clear();
            LineText dateLineText = new LineText();
            dateLineText.x = dateControl.x;
            dateLineText.y = dateControl.y;
            dateLineText.width = dateControl.width;
            dateLineText.height = dateControl.height;
            dateLineText.text = photoTakenTime;
            dateControl.text = photoTakenTime;
            dateControl.textList.add(dateLineText);

            SnapsTextControl snapsControl = (SnapsTextControl) textControls.get(1);
            if (snapsControl.textList == null) snapsControl.textList = new ArrayList<>();
//            snapsControl.format.align = "center";       //Ben 아놔 하드코딩
//            snapsControl.format.fontSize = "12";        //Ben 아놔 하드코딩
//            snapsControl.format.fontFace = "Aileron300";        //Ben 아놔 하드코딩
//            snapsControl.format.fontColor = "a6a6a6";       //Ben 아놔 하드코딩
            snapsControl.textList.clear();
            LineText snapsLineText = new LineText();
            snapsLineText.x = snapsControl.x;
            snapsLineText.y = snapsControl.y;
            snapsLineText.width = snapsControl.width;
            snapsLineText.height = snapsControl.height;
            snapsLineText.text = "snaps";
            snapsControl.textList.add(snapsLineText);
        }
    }

    /**
     *
     */
    public void makeAuraOrderXml(SnapsAuraOrderXML auraOrder, String orderCode) throws Exception {

        // try {
        // Order Tag
        SnapsLogger.appendOrderLog("Make Aura XML Point1");
        if (auraOrder == null)
            Dlog.d("makeAuraOrderXml() auraOrder == null");
        auraOrder.startTag(null, "Order");
        auraOrder.attribute(null, "code", orderCode);

        // TODO : Application 정보 변경.
        auraOrder.startTag(null, "Application");
        auraOrder.attribute(null, "name", Config.APPLICATION_NAME);
        auraOrder.attribute(null, "version", version);
        auraOrder.endTag(null, "Application");

        auraOrder.startTag(null, "item");
        auraOrder.setAlbumInfoXml_auraorder_item(template);

        //FIXME 제품에 따라, 뒷면이 없는 템플릿이 있을 수도 있다.
        ArrayList<SnapsPage> snapsPageList = null;
        if (isExistHiddenPageProduct()) {
            if (Const_PRODUCT.isBothSidePrintProduct()) {
                snapsPageList = mergeBackAndFrontPageList(true);
            } else {
                snapsPageList = template.getPages();
            }
        } else {
            snapsPageList = template.getPages();
        }

        SnapsLogger.appendOrderLog("Make Aura XML Point2");

        if (snapsPageList == null) throw new Exception("upload fail, no snapsPageList");

        //패키지 킷, 포토 카드 상품(히든 된 커버와 인덱스를 다시 끼워 넣는 작업)
        if (isExistHiddenPageProduct())
            snapsPageList = getInsertedHiddenPages(snapsPageList);

        if (Const_PRODUCT.isCardProduct()) {
            if (Const_PRODUCT.isCardShapeFolder()) {
                snapsPageList = switchHalfPageToHiddenPage(snapsPageList);
            }
        } else if (Const_PRODUCT.isNewPolaroidPackProduct()) {
            insertPolaroidBackText(snapsPageList);
        }

        SnapsLogger.appendOrderLog("Make Aura XML Point3");

        int pageIndex = 0;
        for (SnapsPage page : snapsPageList) {
            SnapsLogger.appendOrderLog("write aura xml page " + pageIndex);
            if (Config.isSnapsSticker()) {
                if (!page.type.equalsIgnoreCase("cover")) {
                    SnapsXML snapsXml = page.getAuraOrderPageXML(auraOrder, template.info, pageIndex);
                    if (snapsXml == null) {
                        SnapsLogger.appendOrderLog("write aura xml page Exception1 page : " + pageIndex);
                        throw new Exception("upload fail, getAuraOrderPageXml failed");
                    }
                    pageIndex++;
                }
            } else {// 콜라주,페북북,카스북
                if (template.info.maxPageInfo != null && page.type.equalsIgnoreCase("cover")) {// 테마북 심플포토북 라인만 적용..
                    // 맥스페이지 정보를 만든다 coverEdgeType, 하드커버인경우 spine넓이 설정..
                    template.info.F_COVEREDGE_TYPE = template.info.maxPageInfo.getCoverEdgeType(template.info.F_PAPER_CODE, snapsPageList.size() - 2);
                }

                SnapsXML snapsXml = page.getAuraOrderPageXML(auraOrder, template.info, pageIndex);
                if (snapsXml == null) {
                    SnapsLogger.appendOrderLog("write aura xml page Exception2 page : " + pageIndex);
                    throw new Exception("upload fail, getAuraOrderPageXml failed2");
                }

                pageIndex++;
            }
        }

        SnapsLogger.appendOrderLog("Make Aura XML Point4");

        if (Config.isThemeBook() || Config.isSimplePhotoBook() ||
                Const_PRODUCT.isSNSBook() || Config.isSimpleMakingBook()) {
            // 콜라주, 카카오북, 페북북 일경우에만 서비스 페이지 구성.

            // extra page tag
            auraOrder.startTag(null, "page");
            auraOrder.attribute(null, "type", "coverExtra");
            auraOrder.attribute(null, "effectivePage", "split_right");
            auraOrder.attribute(null, "mmWidth", template.info.F_TITLE_MM_WIDTH);
            auraOrder.attribute(null, "mmHeight", template.info.F_TITLE_MM_HEIGHT);

            // EditInfo Tag
            auraOrder.startTag(null, "editinfo");
            auraOrder.attribute(null, "editWidth", template.info.F_PAGE_PIXEL_WIDTH);
            auraOrder.attribute(null, "editHeight", template.info.F_PAGE_PIXEL_HEIGHT);
            auraOrder.endTag(null, "editinfo");

            auraOrder.endTag(null, "page");
        } else if (Config.isCalendarNormal(Config.getPROD_CODE()) || Config.PRODUCT_CALENDAR_MINI.equalsIgnoreCase(Config.getPROD_CODE()) || Config.isCalendarLarge(Config.getPROD_CODE())
                || Config.isCalendarNormalVert(Config.getPROD_CODE()) || Config.isCalenderWall(Config.getPROD_CODE()) || Config.isCalenderSchedule(Config.getPROD_CODE())) {

            auraOrder.startTag(null, "page");

            auraOrder.attribute(null, "type", "hidden");

            if (Config.isWoodBlockCalendar()) {
                auraOrder.attribute(null, "effectivePage", "single");
            } else {
                auraOrder.attribute(null, "effectivePage", "split_both");
            }

            auraOrder.attribute(null, "mmWidth", template.info.F_PAGE_MM_WIDTH);
            auraOrder.attribute(null, "mmHeight", template.info.F_PAGE_MM_HEIGHT);

            // EditInfo Tag
            auraOrder.startTag(null, "editinfo");
            auraOrder.attribute(null, "editWidth", template.info.F_PAGE_PIXEL_WIDTH);
            auraOrder.attribute(null, "editHeight", template.info.F_PAGE_PIXEL_HEIGHT);
            auraOrder.endTag(null, "editinfo");

            int len = 0;
            ArrayList<SnapsLayoutControl> summarys;
            summarys = GetTemplateXMLHandler.getSummaryLayer();// .size();
            len = summarys.size();

            Dlog.d("makeAuraOrderXml() Aura Size:" + len);
            auraOrder.startTag(null, "object");
            auraOrder.attribute(null, "type", "design");
            auraOrder.attribute(null, "rscType", "design");
            auraOrder.attribute(null, "imgYear", "");
            auraOrder.attribute(null, "imgSeq", "");
            auraOrder.attribute(null, "id", GetTemplateXMLHandler.getSummaryTarget());
            auraOrder.attribute(null, "angle", "0");
            auraOrder.attribute(null, "x", "0");
            auraOrder.attribute(null, "y", "0");
            auraOrder.attribute(null, "width", GetTemplateXMLHandler.getSummaryWidth());
            auraOrder.attribute(null, "height", GetTemplateXMLHandler.getSummaryHeight());
            auraOrder.attribute(null, "clipX", "0");
            auraOrder.attribute(null, "clipY", "0");
            auraOrder.attribute(null, "clipWidth", GetTemplateXMLHandler.getSummaryWidth());
            auraOrder.attribute(null, "clipHeight", GetTemplateXMLHandler.getSummaryHeight());
            auraOrder.endTag(null, "object");

            Dlog.d("makeAuraOrderXml() target:" + GetTemplateXMLHandler.getSummaryTarget()
                    + ", w:" + GetTemplateXMLHandler.getSummaryWidth() + ", h:" + GetTemplateXMLHandler.getSummaryHeight());

            int startYear = GetTemplateXMLHandler.getStartYear();
            int startMonth = GetTemplateXMLHandler.getStartMonth();

            for (int i = 0; i < len; i++) {
                String monthLabel = "";
                if (startMonth >= 1 && startMonth < 10)
                    monthLabel = String.format("0%d", startMonth);
                else
                    monthLabel = String.format("%d", startMonth);

                String id = String.format("%s%d%s", "cal_mini_", startYear, monthLabel);
                SnapsLayoutControl layout = summarys.get(i);

                Dlog.d("makeAuraOrderXml() id:" + id + ", x:" + layout.x + ", y:" + layout.y
                        + ", w:" + layout.width + ", y:" + layout.height);

                auraOrder.startTag(null, "object");
                auraOrder.attribute(null, "type", "design");
                auraOrder.attribute(null, "rscType", "design");
                auraOrder.attribute(null, "imgYear", "");
                auraOrder.attribute(null, "imgSeq", "");
                auraOrder.attribute(null, "id", id);
                auraOrder.attribute(null, "angle", "0");
                auraOrder.attribute(null, "x", layout.x);
                auraOrder.attribute(null, "y", layout.y);
                auraOrder.attribute(null, "width", layout.width);
                auraOrder.attribute(null, "height", layout.height);
                auraOrder.attribute(null, "clipX", layout.x);
                auraOrder.attribute(null, "clipY", layout.y);
                auraOrder.attribute(null, "clipWidth", layout.width);
                auraOrder.attribute(null, "clipHeight", layout.height);
                auraOrder.endTag(null, "object");
                startMonth++;
                if (startMonth > 12) {
                    startMonth = 1;
                    startYear++;
                }
            }

            auraOrder.endTag(null, "page");
        }

        auraOrder.endTag(null, "item");
        auraOrder.endTag(null, "Order");

        auraOrder.endDocument();
    }

    /**
     *
     */
    public void makeOptionXml(SnapsOptionXML option) {

        try {
            option.startTag(null, "string");
            option.startTag(null, "projOrderInfo");

            option.startTag(null, "ORDR");
            option.getOptionORDR(template);
            option.endTag(null, "ORDR");
            option.startTag(null, "MST");
            option.getOptionMST(template, Config.getPROJ_NAME());
            option.endTag(null, "MST");
            option.startTag(null, "DTL");
            option.getOptionDTL(template, Config.getYEAR_KEY(), Config.getSQNC_KEY());
            option.endTag(null, "DTL");

            Dlog.d("Template Has Accessories ? " + template.hasAccessories());
            if (template.hasAccessories()) {
                /**
                 * @Marko
                 * 아크릴 키링 제작 시 전달받은 accessory 가 있다면 해당 내용을 써줘야 한다.
                 * 1. 최초 optionXML 제작 시 accessory 정보 추가.
                 * 2. 그 이후 작성 시, 아무 정보 넣지 않는다.
                 * 3. 없을 경우, 동일하게 아무 정보를 넣지 않는다.
                 *
                 * @Marko 2020.11.23
                 * 폰케이스 투명 스트랩 케이스의 경우도 같은 로직 적용한다.
                 */
                option.startTag(null, "ACCESSORIES");
                option.writeAccessories(template);
                option.endTag(null, "ACCESSORIES");
            }

            for (SnapsDelImage img : template.delimgList) {
                if (!img.imgYear.equalsIgnoreCase("")) {
                    option.startTag(null, "IMGS");
                    option.addTag(null, "F_PROJ_CODE", Config.getPROJ_CODE());
                    option.addTag(null, "F_PROD_CODE", Config.getPROD_CODE());
                    option.addTag(null, "F_TMPL_CODE", Config.getTMPL_CODE());
                    option.addTag(null, "F_TMPL_SUB", template.info.F_TMPL_SUB);
                    option.addTag(null, "F_IMGX_YEAR", img.imgYear);
                    option.addTag(null, "F_IMGX_SQNC", img.imgSeq);
                    option.endTag(null, "IMGS");
                }
            }

            option.endTag(null, "projOrderInfo");
            option.endTag(null, "string");

            option.endDocument();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /***
     * summeryLayout xml를 만든다.
     *
     * @param save
     */
    void makeSummeryXml(SnapsSaveXML save) {
        try {

            if (Config.isCalendar()) {
                // 달력인 경우 summeryLayout을 넣어줘야 한다. 일반, 라지만 sumary가 존재..
                ArrayList<SnapsLayoutControl> summaryLayer = GetTemplateXMLHandler.getSummaryLayer();

                if (summaryLayer == null || summaryLayer.size() == 0)
                    return;

                save.startTag(null, "scene");
                save.attribute(null, "name", "root");
                save.attribute(null, "width", GetTemplateXMLHandler.getSummaryWidth());
                save.attribute(null, "height", GetTemplateXMLHandler.getSummaryHeight());
                save.attribute(null, "type", "hidden");

                int idx = 0;
                // Array에 있는 순서대로 cal_idx를 먹인다.
                for (SnapsLayoutControl c : summaryLayer) {

                    c.getControlSummaryXML(save, idx);
                    idx++;
                }

                save.startTag(null, "layer");
                save.attribute(null, "name", "background_layer");
                save.startTag(null, "source");
                save.attribute(null, "type", "webitem");
                save.attribute(null, "target", GetTemplateXMLHandler.getSummaryTarget());
                save.attribute(null, "target_type", "design");
                save.attribute(null, "fit", "fill_in");
                save.attribute(null, "maximize", "on");
                save.endTag(null, "source");

                save.startTag(null, "regist");
                save.attribute(null, "name", "background");
                save.attribute(null, "value", "1");
                save.endTag(null, "regist");

                save.endTag(null, "layer");

                save.endTag(null, "scene");
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private ArrayList<SnapsPage> getInsertedHiddenPages(ArrayList<SnapsPage> list) {
        if (list == null || hiddenPageList == null || hiddenPageList.isEmpty()) return list;

        ArrayList<SnapsPage> insertedPage = new ArrayList<SnapsPage>();
        insertedPage.addAll(list);

        for (int ii = hiddenPageList.size() - 1; ii >= 0; ii--) {
            SnapsPage page = hiddenPageList.get(ii);
            if (Const_PRODUCT.isCardProduct())
                page.type = "hidden";

            insertedPage.add(0, page);
        }

        return insertedPage;
    }

    private ArrayList<SnapsPage> getInsertedHiddenPage(ArrayList<SnapsPage> list, int hiddenPageIdx) {
        if (list == null || hiddenPageList == null || hiddenPageList.isEmpty() || hiddenPageList.size() <= hiddenPageIdx)
            return list;

        ArrayList<SnapsPage> insertedPage = new ArrayList<SnapsPage>();
        insertedPage.addAll(list);

        SnapsPage page = hiddenPageList.get(hiddenPageIdx);
        if (Const_PRODUCT.isCardProduct())
            page.type = "index";

        insertedPage.add(0, page);

        return insertedPage;
    }

    private void removeLayoutControlOnHiddenPage(SnapsPage hiddenPage) { //왜 이런 현상이 일어나는 지 모르겠는데, CS를 통해 HiddenPage에 Layer가 남아 있는 버그가 발견 된다
        if (hiddenPage == null) return;

        ArrayList<SnapsControl> layers = hiddenPage.getLayerLayouts();
        if (layers != null && !layers.isEmpty()) layers.clear();
    }

    //halPage에 있는 컨트롤들을 hiddenPage로 옮긴다.
    private SnapsPage getCoverPageFromHalfPage(SnapsPage hiddenPage, SnapsPage halfPage) {
        if (hiddenPage == null || halfPage == null) return null;

        removeLayoutControlOnHiddenPage(hiddenPage);

        float halfPageWidth = Float.parseFloat(halfPage.width);
        float halfPageHeight = Float.parseFloat(halfPage.height);
        float hiddenPageWidth = Float.parseFloat(hiddenPage.width);
        float hiddenPageHeight = Float.parseFloat(hiddenPage.height);

        ArrayList<SnapsControl> arrLayoutContorls = halfPage.getLayoutList();
        ArrayList<SnapsControl> arrClipContorls = halfPage.getControlList();
        int diffX = (int) (hiddenPageWidth - halfPageWidth);
        int diffY = (int) (hiddenPageHeight - halfPageHeight);

        if (arrLayoutContorls != null) {
            for (SnapsControl control : arrLayoutContorls) {
                if (control instanceof SnapsLayoutControl) {
                    SnapsLayoutControl layoutControl = (SnapsLayoutControl) control;
                    layoutControl.cardFolderFixValueX = diffX;
                    layoutControl.cardFolderFixValueY = diffY;
                    hiddenPage.deleteLayout(layoutControl);
                    hiddenPage.addLayout(layoutControl);
                }
            }
        }

        if (arrClipContorls != null) {
            for (SnapsControl control : arrClipContorls) {
                if (control instanceof SnapsClipartControl) {
                    SnapsClipartControl clipControl = (SnapsClipartControl) control;
                    clipControl.cardFolderFixValueX = diffX;
                    clipControl.cardFolderFixValueY = diffY;
                    hiddenPage.deleteControl(clipControl);
                    hiddenPage.addControl(clipControl);
                }
            }
        }

        //배경아이디 변경하기
        //half의 배경아이디를 hidden의 배경아디로 교체를 해준다.
        SnapsBgControl bgControl_hidden = hiddenPage.getBgControl();
        SnapsBgControl bgControl_half = halfPage.getBgControl();
        if (bgControl_hidden != null && bgControl_half != null) {
            bgControl_hidden.srcTarget = bgControl_half.srcTarget;
        }

        hiddenPage.setQuantity(halfPage.getQuantity());
        hiddenPage.type = "cover";
        return hiddenPage;
    }

    private ArrayList<SnapsPage> switchHalfPageToHiddenPage(ArrayList<SnapsPage> list) {
        if (list == null || hiddenPageList == null || hiddenPageList.isEmpty()) return list;

        ArrayList<SnapsPage> insertedPage = new ArrayList<SnapsPage>();
        int position = 0;
        for (int i = 0; i < hiddenPageList.size(); i++) {
            SnapsPage hiddenPage = hiddenPageList.get(i); //TODO  첫번째로 인덱스가 들어가고 2번째로 커버가 들어간다.
            SnapsPage halfPage = list.get(position); //half - text - half - text...
            SnapsPage coverPage = getCoverPageFromHalfPage(hiddenPage, halfPage);
            insertedPage.add(coverPage);
            insertedPage.add(list.get(position + 1));
            position += 2;
        }


        return insertedPage;
    }
}
