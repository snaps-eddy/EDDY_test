package com.snaps.common.data.parser;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.SmartSnapsImgInfo;
import com.snaps.common.data.smart_snaps.SmartSnapsSaveXmlImageInfo;
import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsFormControl;
import com.snaps.common.structure.control.SnapsHelperControl;
import com.snaps.common.structure.control.SnapsHoleControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsSceneCutControl;
import com.snaps.common.structure.control.SnapsSceneMaskControl;
import com.snaps.common.structure.control.SnapsStickControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.control.TextFormat;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.BRect;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GetSaveXMLHandler extends GetTemplateXMLHandler {
    private static final String TAG = GetSaveXMLHandler.class.getSimpleName();

    protected final String TAG_DEL_IMAGE = "del_image";
    protected final String TAG_MYIMAGEDATA = "MyImageData";
    protected final String TAG_ITEM = "item";

    String preStr = "";
    //    String attrName = "";
    String sceneName = "";

    SmartSnapsSaveXmlImageInfo smartSnapsSaveXmlImageInfo = null;

    @Override
    public void startDocument() throws SAXException {
        template = new SnapsTemplate();
        FontUtil fontUtil = FontUtil.getInstance();
        fontUtil.initTextListFont();

        GetTemplateXMLHandler.summaryLayer = new ArrayList<SnapsLayoutControl>();
    }


    private void processCalendar(String regName) {
        String name = "";

        FontUtil fontUtil = FontUtil.getInstance();
        if (fontUtil != null) {
            name = fontUtil.findFontFile(ContextUtil.getContext(), Const_VALUE.SNAPS_TYPEFACE_NAME);
        }

        if (name != null && name.length() > 0) {
            if (regName.compareTo("day") == 0) {
                textControl.controType = "day";
                FontUtil.putUITextListFont(textControl, "day", textControl.format.fontFace);
            } else if (regName.compareTo("day_title") == 0) {
                textControl.controType = "day_title";
                FontUtil.putUITextListFont(textControl, "day_title", textControl.format.fontFace);
            } else if (regName.compareTo("month") == 0) {
                textControl.controType = "month";
                FontUtil.putUITextListFont(textControl, "month", textControl.format.fontFace);
            } else if (regName.compareTo("month_title") == 0) {
                textControl.controType = "month_title";
                FontUtil.putUITextListFont(textControl, "month_title", textControl.format.fontFace);
            } else if (regName.compareTo("year") == 0) {
                textControl.controType = "year";
                FontUtil.putUITextListFont(textControl, "year", textControl.format.fontFace);
            } else if (regName.compareTo("textlist") == 0) {
                textControl.controType = "textlist";
                FontUtil.putUITextListFont(textControl, "textlist", textControl.format.fontFace);
            } else if (regName.compareTo("day_front") == 0) {
                textControl.controType = "day_front";
                FontUtil.putUITextListFont(textControl, "day_front", textControl.format.fontFace);
            } else if (regName.compareTo("month_front") == 0) {
                textControl.controType = "month_front";
                FontUtil.putUITextListFont(textControl, "month_front", textControl.format.fontFace);
            } else if (regName.compareTo("monthtitle_front") == 0) {
                textControl.controType = "monthtitle_front";
                FontUtil.putUITextListFont(textControl, "monthtitle_front", textControl.format.fontFace);
            } else if (regName.compareTo("year_front") == 0) {
                textControl.controType = "year_front";
                FontUtil.putUITextListFont(textControl, "year_front", textControl.format.fontFace);
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        element = localName;
        characterText = new StringBuffer();

        if (localName.equalsIgnoreCase(TAG_ITEM)) {
            // 프로젝트 네임이르 설정한다.
            Config.setPROJ_NAME(getValue(attributes, "projectName"));
            Config.setTMPL_COVER(getValue(attributes, "coverColorCode"));
            Config.setTMPL_CODE(getValue(attributes, "tmplID"));
            Config.setBACK_TYPE(getValue(attributes, "backType"));
            template.saveInfo.orderCount = getValue(attributes, "orderCount");
        } else if (localName.equalsIgnoreCase(TAG_BASKET)) {
            template.version = getValue(attributes, "version");
        } else if (localName.equalsIgnoreCase(TAG_TEMPLATE)) {
            template.type = getValue(attributes, "type");
        } else if (localName.equalsIgnoreCase(TAG_SCENE)) {
            page = new SnapsPage(template.getPages().size(), template.info);
            page.setPageLayoutIDX(page.getPageID());
            page.type = getValue(attributes, "type");
            page.setWidth(getValue(attributes, "width"));
            page.height = getValue(attributes, "height");
            page.border = getValue(attributes, "border");
            page.layout = getValue(attributes, "layout");
            page.side = getValue(attributes, "side");
            page.orgMultiformId = getValue(attributes, "orgMultiformId");
            page.multiformId = getValue(attributes, "multiform");
            page.templateCode = getValue(attributes, "templateCode");
            page.orgBgId = getValue(attributes, "orgBgId");
            page.background = getValue(attributes, "background");
            String year = getValue(attributes, "year");
            String month = getValue(attributes, "month");
            if (Config.isCalendar()) {
                preStr = getValue(attributes, "type");
                if (preStr.compareTo("hidden") != 0) {
                    GetTemplateXMLHandler.setStartYear(Integer.parseInt(year));
                    GetTemplateXMLHandler.setStartMonth(Integer.parseInt(month));
                }

                summaryWidth = getValue(attributes, "width");
                summaryHeight = getValue(attributes, "height");
            }

            page.setQuantity(getValueInt(attributes, "prnt_cnt"));

            if (page.type.equalsIgnoreCase("cover")) {
                cover_width = page.getWidth();
                cover_height = Integer.parseInt(page.height);
            }

        } else if (localName.equalsIgnoreCase(TAG_TEXT)) {
            layoutState = TAG_TEXT;

            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

            textControl.x = getValue(attributes, "x");
            textControl.y = getValue(attributes, "y");
            textControl.width = getValue(attributes, "width");
            textControl.height = getValue(attributes, "height");
            textControl.textDrawableWidth = getValue(attributes, "textDrawableWidth");
            textControl.textDrawableHeight = getValue(attributes, "textDrawableHeight");

            textControl.isClick = getValue(attributes, "isEditable");

            textControl.setOffsetX(getValueInt(attributes, "offsetX"));
            textControl.setOffsetY(getValueInt(attributes, "offsetY"));
            textControl.angle = getValue(attributes, "angle", "0");

            if (Config.isCalendar()) {
                textControl.type = "calendar";
            }
        } else if (localName.equalsIgnoreCase(TAG_IMAGE)) {
            layoutState = TAG_IMAGE;

            if (Config.isCalendar()) {
                if (Config.isWoodBlockCalendar()) {
                    ;
                } else {
                    if (preStr.compareTo("hidden") == 0) {
                        layout = new SnapsLayoutControl();
                        layout.x = getValue(attributes, "x");
                        layout.y = getValue(attributes, "y");
                        layout.width = getValue(attributes, "w");
                        layout.height = getValue(attributes, "h");
                        layout.angle = getValue(attributes, "angle", "0");

                        String indexStr = getValue(attributes, "cal_idx");

                        if (!indexStr.isEmpty()) {
                            layout.angleClip = getValue(attributes, "angleClip", "0");
                            GetTemplateXMLHandler.summaryLayer.add(layout);
                        }

                    }
                }
            }

            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // BackGround 이미지

            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
                layout = new SnapsLayoutControl();
                layout.setX(getValue(attributes, "x"));
                layout.y = getValue(attributes, "y");
                layout.width = getValue(attributes, "width");
                layout.height = getValue(attributes, "height");
                layout.angle = getValue(attributes, "angle", "0");
                layout.border = getValue(attributes, "border");
                layout.isClick = getValue(attributes, "isEditable");
                layout.angleClip = getValue(attributes, "angleClip", "0");
                layout.name = getValue(attributes, "name");

                String szKakaoCover = getValue(attributes, "isNewKakaoCover");
                layout.isSnsBookCover = szKakaoCover != null && szKakaoCover.equalsIgnoreCase("true");

                // 사진틀..
                layout.bordersinglecolortype = getValue(attributes, "bordersinglecolortype");
                layout.bordersinglealpha = getValue(attributes, "bordersinglealpha");
                layout.bordersinglethick = getValue(attributes, "bordersinglethick");
                layout.bordersinglecolor = getValue(attributes, "bordersinglecolor");
                layout.qrCodeUrl = getValue(attributes, "qrCodeUrl");
                String imagefull = getValue(attributes, "imagefull");
                layout.isImageFull = imagefull.equals("true");

                if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                    String orientation = getValue(attributes, "orientation");
                    String imgAnalysis = getValue(attributes, "imgAnalysis");
                    if (!StringUtil.isEmpty(orientation) && !StringUtil.isEmpty(imgAnalysis)) {
                        smartSnapsSaveXmlImageInfo = new SmartSnapsSaveXmlImageInfo.Builder().setPageIdx(page.getPageID()).setOrientation(orientation).setImgAnalysis(imgAnalysis).create();
                    }
                }
            } else {
                // browse 이미지.
                layout = new SnapsLayoutControl();
                layout.setX(getValue(attributes, "x"));
                layout.y = getValue(attributes, "y");
                layout.width = getValue(attributes, "width");
                layout.height = getValue(attributes, "height");
                layout.angle = getValue(attributes, "angle", "0");
                layout.border = getValue(attributes, "border");
                layout.isClick = getValue(attributes, "isEditable");
            }
        } else if (localName.equalsIgnoreCase(TAG_LAYER)) {
            state = getValue(attributes, "name");

            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {

                bg = new SnapsBgControl();
                bg.layerName = getValue(attributes, "name");
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form = new SnapsFormControl();
                form.layerName = getValue(attributes, "name");

            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
                if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                    String strX = getValue(attributes, "x");
                    String strY = getValue(attributes, "y");
                    String strWidth = getValue(attributes, "width");
                    String strHeight = getValue(attributes, "height");
                    try {
                        if (strX != null && strY != null && strWidth != null && strHeight != null) {
                            int x = Integer.parseInt(strX);
                            int y = Integer.parseInt(strY);
                            int width = Integer.parseInt(strWidth);
                            int height = Integer.parseInt(strHeight);
                            BRect rect = new BRect();
                            rect.set(x, y, x + width, y + height);
                            page.setImageLayerRect(rect);
                        }
                    } catch (NumberFormatException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        } else if (localName.equalsIgnoreCase(TAG_STYLE)) {
            textControl.format.fontFace = getValue(attributes, "font_face");
            Const_VALUE.SNAPS_TYPEFACE_NAME = textControl.format.fontFace;

            textControl.format.alterFontFace = getValue(attributes, "alter_font_face");
            textControl.format.fontSize = getValue(attributes, "font_size");

            textControl.format.fontColor = getValue(attributes, "font_color");
            //Ben 땜방
            //"font_color"값이 6자리가 아니고 4자리인 경우가 있다. 이 경우 앞에 00을 붙인다.
            if (textControl.format.fontColor != null &&
                    textControl.format.fontColor.startsWith("#") == false && textControl.format.fontColor.length() == 4) {
                Dlog.w(TAG, "startElement() fontcolor:" + textControl.format.fontColor + " -> " + "00" + textControl.format.fontColor);
                textControl.format.fontColor = "00" + textControl.format.fontColor;
            }

            textControl.format.baseFontColor = getValue(attributes, "baseFontColor");
            if (StringUtil.isEmpty(textControl.format.baseFontColor))
                textControl.format.baseFontColor = textControl.format.fontColor;

            textControl.format.align = getValue(attributes, "align");
            textControl.format.bold = getValue(attributes, "bold");
            textControl.format.italic = getValue(attributes, "italic");
            textControl.format.underline = getValue(attributes, "underline");
            textControl.format.verticalView = getValue(attributes, "vertical_view");
            textControl.format.auraOrderFontSize = getValue(attributes, "auraOrder_FontSize");
            textControl.albumMode = getValue(attributes, "album_mode");
            textControl.lineSpcing = getValueFloat(attributes, "lineSpacing");
            textControl.format.setOverPrint(getValue(attributes, "overPrint"));
            String orientation = getValue(attributes, "orientation");
            if (orientation.equals("1"))
                textControl.format.orientation = TextFormat.TEXT_ORIENTAION_VERTICAL;

        } else if (localName.equalsIgnoreCase(TAG_REGIST)) {
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                bg.regName = getValue(attributes, "name");
                bg.regValue = getValue(attributes, "value");
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form.regName = getValue(attributes, "name");
                form.layerName = getValue(attributes, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_TEXT)) {
                textControl.regName = getValue(attributes, "name");
                textControl.regValue = getValue(attributes, "value");
                textControl.text = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "value"));

                textControl.textForDiaryPublish = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "value_for_diary_publish"));

                if (Config.isCalendar()) {
                    //TODO  기념일 예외 처리
                    if (textControl.text != null && (textControl.text.equals("말복") || textControl.text.equals("7.1"))) {
//                        boolean fixRegTextErrText = false;
//                        String basketVersion = template.version;
//                        basketVersion = basketVersion.replace(".", "");
//                        try {
//                            if (Integer.parseInt(basketVersion) <= 269)
//                                fixRegTextErrText = true;
//                        } catch (NumberFormatException e) {
//                            Dlog.e(TAG, e);
//                        }
//                        if (fixRegTextErrText) {
//                            textControl.text = "";
//                        }
                        textControl.text = "";
                    }

                    processCalendar(textControl.regName);
                }

            } else if (layoutState.equalsIgnoreCase(TAG_IMAGE)) {
                layout.regName = getValue(attributes, "name");
                layout.regValue = getValue(attributes, "value");
            }

        } else if (localName.equalsIgnoreCase(TAG_SOURCE)) {
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // BackGround 이미지
                bg.type = getValue(attributes, "type");
                bg.fit = getValue(attributes, "fit");
                bg.bgColor = getValue(attributes, "bgcolor");
                bg.coverColor = getValue(attributes, "covercolor");
                bg.srcTargetType = getValue(attributes, "target_type");
                bg.srcTarget = getValue(attributes, "target");
                bg.resourceURL = getValue(attributes, "resourceURL");
                bg.getVersion = getValue(attributes, "getVersion");
                if (Config.isCalendar()) {
                    if (Config.isWoodBlockCalendar()) {
                        ;
                    } else {
                        Dlog.d("startElement() summaryTaget:" + GetTemplateXMLHandler.summaryTaget);
                        if (preStr.compareTo("hidden") == 0) {
                            GetTemplateXMLHandler.summaryTaget = getValue(attributes, "target");
                            Dlog.d("startElement() summaryTaget:" + GetTemplateXMLHandler.summaryTaget + " [hidden]");
                            preStr = "";
                        }
                    }
                }
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form.type = getValue(attributes, "type");
                form.fit = getValue(attributes, "fit");
                form.bgColor = getValue(attributes, "bgcolor");
                form.srcTargetType = getValue(attributes, "target_type");
                form.srcTarget = getValue(attributes, "target");
                form.resourceURL = getValue(attributes, "resourceURL");
                form.getVersion = getValue(attributes, "getVersion");
            } else {
                layout.type = getValue(attributes, "type");
                layout.fit = getValue(attributes, "fit");
                layout.bgColor = getValue(attributes, "bgcolor");
                layout.srcTargetType = getValue(attributes, "target_type");
                layout.srcTarget = getValue(attributes, "target");
                layout.resourceURL = getValue(attributes, "resourceURL");
                layout.mask = getValue(attributes, "mask");
                layout.maskURL = getValue(attributes, "maskURL");
                layout.maskType = getValue(attributes, "mask_type");
                layout.maskRadius = getValue(attributes, "mask_radius");
            }
        } else if (localName.equalsIgnoreCase(TAG_TMPLINFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_TMPLPRICE)) {
            state = localName;
            price = new SnapsTemplatePrice();
        } else if (localName.equalsIgnoreCase(TAG_PRODUCT_OPTION)) {
            state = localName;
            productOption = new SnapsProductOption();
        } else if (localName.equalsIgnoreCase(TAG_DEL_IMAGE)) {

            SnapsDelImage delImg = new SnapsDelImage();

            delImg.imgYear = getValue(attributes, "imgYear");
            delImg.imgSeq = getValue(attributes, "imgSeq");
            delImg.uploadPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "uploadPath"));
            delImg.tinyPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "tinyPath"));
            delImg.oriPath = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "oriPath"));
            delImg.sizeOrgImg = getValue(attributes, "sizeOrgImg");
            delImg.realFileName = StringUtil.convertEmojiAliasToUniCode(getValue(attributes, "realFileName"));
            delImg.shootDate = getValue(attributes, "shootDate");
            delImg.usedImgCnt = getValue(attributes, "usedImgCnt");

            template.delimgList.add(delImg);
        }// MyImageData
        else if (localName.equalsIgnoreCase(TAG_MYIMAGEDATA)) {
            MyPhotoSelectImageData d = MyPhotoSelectImageData.xmlToMyPhotoSeletImageData(attributes);
            // 커버이미
            if (template.getPages().size() == 0)
                d.IMG_IDX = 1;
            else
                d.IMG_IDX = template.getPages().size() * 2 + Integer.parseInt(layout.regValue);
            // 사진데이터를 따로 arrayList에 담아 놓은다...
            d.cropRatio = Float.parseFloat(layout.width) / Float.parseFloat(layout.height);

            if (SmartSnapsManager.isSupportSmartSnapsProduct()) {
                if (smartSnapsSaveXmlImageInfo != null) {
                    d.setSmartSnapsImgInfo(SmartSnapsImgInfo.createImgInfoWithSmartSnapsSaveXmlImageInfo(smartSnapsSaveXmlImageInfo));
                    smartSnapsSaveXmlImageInfo = null;
                }
            }

            template.myphotoImageList.add(d);
            layout.imgData = d;

        }// 스티커...
        else if (localName.equalsIgnoreCase(TAG_CLIPART)) {
            clipart = new SnapsClipartControl();
            clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;

            String[] temp = getValue(attributes, "rc").replace(" ", "|").split("\\|");
            int left = (int) Float.parseFloat(temp[0]);
            int top = (int) Float.parseFloat(temp[1]);
            int right = (int) Float.parseFloat(temp[2]);
            int bottom = (int) Float.parseFloat(temp[3]);

            clipart.setX(String.valueOf(left));
            clipart.y = String.valueOf(top);
            clipart.width = String.valueOf((int) (right - left));
            clipart.height = String.valueOf((int) (bottom - top));
            clipart.resourceURL = getValue(attributes, "resourceURL");
            clipart.clipart_id = getValue(attributes, "id");

            clipart.angle = getValue(attributes, "angle", "0");
            clipart.alpha = getValue(attributes, "alpha");

            clipart.setOffsetX(getValueInt(attributes, "offsetX"));
            clipart.setOffsetY(getValueInt(attributes, "offsetY"));
            clipart.setOverPrint(getValue(attributes, "overPrint"));

            page.addControl(clipart);
        } else if (localName.equalsIgnoreCase(TAG_FRAMEINFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_HOLE)) {
            hole = new SnapsHoleControl();
            hole._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            hole.setSnsproperty(TAG_HOLE);   //SNS 아닌데...

            hole.x = getValue(attributes, "x", "0");
            hole.y = getValue(attributes, "y", "0");
            hole.width = getValue(attributes, "width", "0");
            hole.height = getValue(attributes, "height", "0");
            hole.angle = getValue(attributes, "angle", "0");
            hole.angleClip = getValue(attributes, "angle", "0");

            page.addControl(hole);
        } else if (localName.equalsIgnoreCase(TAG_HELPER)) {
            helper = new SnapsHelperControl();
            helper._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            helper.setSnsproperty(TAG_HELPER);   //SNS 아닌데...

            helper.x = getValue(attributes, "x", "0");
            helper.y = getValue(attributes, "y", "0");
            helper.width = getValue(attributes, "width", "0");
            helper.height = getValue(attributes, "height", "0");
            helper.angle = getValue(attributes, "angle", "0");
            helper.angleClip = getValue(attributes, "angle", "0");

            page.addControl(helper);
        } else if (localName.equalsIgnoreCase(TAG_STICK)) {
            stick = new SnapsStickControl();
            stick._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            stick.setSnsproperty(TAG_STICK);   //SNS 아닌데...

            stick.x = getValue(attributes, "x", "0");
            stick.y = getValue(attributes, "y", "0");
            stick.width = getValue(attributes, "width", "0");
            stick.height = getValue(attributes, "height", "0");
            stick.angle = getValue(attributes, "angle", "0");
            stick.angleClip = getValue(attributes, "angle", "0");

            page.addControl(stick);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_MASK)) {
            sceneMask = new SnapsSceneMaskControl();
            sceneMask._controlType = SnapsControl.CONTROLTYPE_LOCKED;
            sceneMask.setSnsproperty(TAG_SCENE_MASK);   //SNS 아닌데...

            sceneMask.x = getValue(attributes, "x", "0");
            sceneMask.y = getValue(attributes, "y", "0");
            sceneMask.width = getValue(attributes, "width", "0");
            sceneMask.height = getValue(attributes, "height", "0");
            sceneMask.angle = getValue(attributes, "angle", "0");
            sceneMask.angleClip = getValue(attributes, "angle", "0");
            sceneMask.resourceURL = getValue(attributes, "resourceURL", "");
            sceneMask.setId(getValue(attributes, "id", ""));
            page.addControl(sceneMask);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_CUT)) {
            sceneCut = new SnapsSceneCutControl();
            sceneCut._controlType = SnapsControl.CONTROLTYPE_LOCKED;
            sceneCut.setSnsproperty(TAG_SCENE_CUT);   //SNS 아닌데...

            sceneCut.x = getValue(attributes, "x", "0");
            sceneCut.y = getValue(attributes, "y", "0");
            sceneCut.width = getValue(attributes, "width", "0");
            sceneCut.height = getValue(attributes, "height", "0");
            sceneCut.resourceURL = getValue(attributes, "resourceURL", "");
            sceneCut.setId(getValue(attributes, "id", ""));
            page.addControl(sceneCut);
        }
    }
}
