package com.snaps.common.data.parser;

import android.graphics.Rect;

import com.snaps.common.spc.view.SnapsMovableImageView;
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
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.ui.RotateUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import errorhandle.logger.Logg;

public class SnapsXMLPullParser {
    private static final String TAG = SnapsXMLPullParser.class.getSimpleName();
    XmlPullParser parser = null;

    protected final String TAG_TEMPLATE = "template";
    protected final String TAG_SCENE = "scene";
    protected final String TAG_TEXT = "text";
    protected final String TAG_IMAGE = "image";
    protected final String TAG_LAYER = "layer";
    protected final String TAG_STYLE = "style";
    protected final String TAG_REGIST = "regist";
    protected final String TAG_SOURCE = "source";
    protected final String TAG_TMPLINFO = "TmplInfo";
    protected final String TAG_TMPLPRICE = "TmplPrice";

    protected final String TAG_FORMSTYLE = "formStyle";
    protected final String TAG_CALENDAR_GRID = "calendarGrid";

    protected final String TAG_TOBJECT = "tobject";
    protected final String TAG_TEXTLIST = "textlist";

    protected final String TAG_HTMLTEXT = "htmlText";
    protected final String TAG_TEXTFLOW = "TextFlow";

    protected final String TAG_P = "p";
    protected final String TAG_SPAN = "span";

    // 액자군...
    protected final String TAG_CLIPART = "clipart";

    protected final String TAG_BACKGROUND_LAYER = "background_layer";
    protected final String TAG_IMAGE_LAYER = "image_layer";
    protected final String TAG_FORM_LAYER = "form_layer";

    protected final String TAG_FORM_STYLE = "form_style";

    // 액자군..
    protected final String TAG_CONTROL_LAYER = "control_layer";

    protected final String TAG_HOLE = "hole";    //키링 상품의 키홀
    protected final String TAG_HELPER = "helper";    //아크릴 스탠드의 지지대
    protected final String TAG_STICK = "stick";    //아크릴 스탠드의 고정대
    protected final String TAG_SCENE_MASK = "sceneMask";
    protected final String TAG_SCENE_CUT = "sceneCut";

    protected SnapsTemplate template = null;
    protected SnapsTemplatePrice price = null;
    protected SnapsPage page = null;
    protected SnapsTextControl textControl = null;
    protected SnapsLayoutControl layout = null;
    protected SnapsBgControl bg = null;
    protected SnapsFormControl form = null;

    protected String state = null;
    protected String layoutState = null;   // image, text 등
    protected String element = null;

    protected SnapsClipartControl clipart = null;
    protected SnapsHoleControl hole = null;
    protected SnapsHelperControl helper = null;
    protected SnapsStickControl stick = null;
    protected SnapsSceneMaskControl sceneMaskControl = null;
    protected SnapsSceneCutControl sceneCutControl = null;

    int cover_width = 0;
    int cover_height = 0;
    protected boolean isHidden = false;
    boolean isGrid = false;

    // Calendar 관련 내부 변수 //////////////////////////////////
    private String calWidth = "0";
    private String calHeight = "0";

    private String calX = "0";
    private String calY = "0";

    private String rowCount = "0";

    private String fontFace = "";
    private String fontColor = "";
    private String fontSize = "";
    private String fontAlign = "";

    private String titleX = "";
    private String titleY = "";
    private String titleWidth = "";
    private String titleHeight = "";

    private String titleStyle = "";

    private String dayOffsetX = "";
    private String dayOffsetY = "";

    private String dayTitleOffsetX = "";
    private String dayTitleOffsetY = "";

    private String monthX = "";
    private String monthY = "";
    private String monthWidth = "";
    private String monthHeight = "";
    private String monthFontFace = "";
    private String monthFontColor = "";
    private String monthFontSize = "";
    private String monthFontAlign = "";

    private String monthTitleX = "";
    private String monthTitleY = "";
    private String monthTitleWidth = "";
    private String monthTitleHeight = "";
    private String monthTitleFontFace = "";
    private String monthTitleFontColor = "";
    private String monthTitleFontSize = "";
    private String monthTitleFontAlign = "";

    private String yearX = "";
    private String yearY = "";

    private String yearWidth = "";
    private String yearHeight = "";

    private String yearFontFace = "";
    private String yearFontColor = "";
    private String yearFontSize = "";
    private String yearFontAlign = "";

    private String dayFontFace = "";
    private String dayFontColor = "";
    private String dayFontSize = "";
    private String dayFontAlign = "left";

    private String dayTitleFontFace = "";
    private String dayTitleFontColor = "";
    private String dayTitleFontSize = "";
    private String dayTitleFontAlign = "";

    private String subType = "";
    private static int nStartYear = 2014;
    private static int nStartMonth = 9;

    private static int nOldYear;
    private static int nOldMonth;

    private String cellType = "";

    private String textX = "";
    private String textY = "";
    private String textWidth = "";
    private String textHeight = "";
    private String textAlign = "";

    private String htmlText = "";

    private int textList = 0;

    public static HashMap<String, String> _textListFont = null;

    public static ArrayList<SnapsLayoutControl> summaryLayer = null;

    public static String summaryTaget = "";

    public static String summaryWidth = "";
    public static String summaryHeight = "";
    private String attrName = "";

    public interface XMLHandler {
        public void loadComplete();
    }

    public static String getSummaryTarget() {
        return summaryTaget;
    }

    public static String getSummaryWidth() {
        return summaryWidth;
    }

    public static String getSummaryHeight() {
        return summaryHeight;
    }

    public static int getStartYear() {
        return nOldYear;
    }

    public static int getStartMonth() {
        return nOldMonth;
    }

    public static ArrayList<SnapsLayoutControl> getSummaryLayer() {
        return summaryLayer;
    }

    public static HashMap<String, String> getTextListFontName() {
        return _textListFont;
    }

    public static void setStartYear(int startYear) {
        nStartYear = startYear;
        nOldYear = startYear;
    }

    public static void setStartMonth(int startMonth) {
        nStartMonth = startMonth;
        nOldMonth = startMonth;
    }

    public SnapsXMLPullParser(XmlPullParser parser) {
        this.parser = parser;
        parser();
    }

    /**
     * XML Tempate data
     *
     * @return SnapsTemplate
     */
    public SnapsTemplate getTemplate() {
        return template;
    }

    void parser() {
        try {
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        template = new SnapsTemplate();
                        _textListFont = new HashMap<String, String>();

                        summaryLayer = new ArrayList<SnapsLayoutControl>();
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        if (template.info.F_COVER_TYPE == "soft") {
                            template.info.SOFT_COVER_PXFORMM = Double.parseDouble(template.info.F_COVER_XML_WIDTH) / Double.parseDouble(template.info.F_COVER_MM_WIDTH);
                        }
                        break;

                    case XmlPullParser.START_TAG:
//					Log.d("start = " + parser.getName());
                        startTag(parser);
                        break;

                    case XmlPullParser.END_TAG:
                        endTag(parser);
                        break;
                    case XmlPullParser.TEXT:
                        textTag(parser);
                        break;
                }

                eventType = parser.next();
            }

        } catch (Exception ex) {
            Dlog.e(TAG, ex);
        }
    }

    void startTag(XmlPullParser parser) {
        element = parser.getName();
        String localName = element;
        if (localName == null) localName = "";

        if (localName.equalsIgnoreCase(TAG_TEMPLATE)) {
            template.type = getValue(parser, "type");
        } else if (localName.equalsIgnoreCase(TAG_SCENE)) {
            attrName = getValue(parser, "name");
//			if (preStr.compareTo("hidden") == 0) {
            if (attrName.compareTo("root") == 0) {
                if (Config.isCalendar()) {
                    isHidden = true;
                    summaryWidth = getValue(parser, "width");
                    summaryHeight = getValue(parser, "height");
                }
            }

            page = new SnapsPage(template.getPages().size(), template.info);

            page.setPageLayoutIDX(page.getPageID());
            page.type = getValue(parser, "type");
            page.setWidth(getValue(parser, "width"));
            page.height = getValue(parser, "height");
            page.border = getValue(parser, "border");
            page.layout = getValue(parser, "layout");
            page.background = getValue(parser, "background");

            page.multiformId = getValue(parser, "multiform");
            page.orgMultiformId = page.multiformId;
            page.templateCode = getValue(parser, "templateCode");

            subType = getValue(parser, "sub_type");

            // sub_type="schedule_memo"
            if (page.type.equalsIgnoreCase("cover")) {
                cover_width = page.getWidth();
                cover_height = Integer.parseInt(page.height);
            }

            page.setSnsproperty(getValue(parser, "snsproperty"));
            page.setTextType(getValue(parser, "texttype"));
            page.setIndexRect(getValue(parser, "index_rc"));
            page.vAlign = getValue(parser, "valign");

        } else if (localName.equalsIgnoreCase(TAG_TEXT)) {
            layoutState = TAG_TEXT;
            state = TAG_TEXT;
            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

            textControl.setX(getValue(parser, "x"));
            textControl.y = getValue(parser, "y");
            textControl.width = getValue(parser, "width");
            textControl.height = getValue(parser, "height");
            textControl.textDrawableWidth = getValue(parser, "textDrawableWidth");
            textControl.textDrawableHeight = getValue(parser, "textDrawableHeight");

            textControl.isClick = getValue(parser, "isEditable");
        } else if (localName.equalsIgnoreCase(TAG_IMAGE)) {
            layoutState = TAG_IMAGE;

            if (Config.isCalendar()) {
//				if (preStr.compareTo("hidden") == 0) {
                if (attrName.compareTo("root") == 0) {
                    layout = new SnapsLayoutControl();
                    layout.setX(getValue(parser, "x"));
                    layout.y = getValue(parser, "y");
                    layout.width = getValue(parser, "w");
                    layout.height = getValue(parser, "h");

                    if (layout.width.isEmpty())
                        layout.width = getValue(parser, "width");
                    if (layout.height.isEmpty())
                        layout.height = getValue(parser, "height");

                    layout.angle = getValue(parser, "angle", "0");

                    String indexStr = getValue(parser, "cal_idx");

                    if (!indexStr.isEmpty()) {
                        int index = Integer.parseInt(indexStr);
                        // cal_idx
                        layout.angleClip = getValue(parser, "angleClip", "0");
                        summaryLayer.add(layout);
                    }
                }
            }
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // BackGround 이미지

            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
                layout = new SnapsLayoutControl();
                layout.setX(getValue(parser, "x"));
                layout.y = getValue(parser, "y");
                layout.width = getValue(parser, "width");
                layout.height = getValue(parser, "height");
                layout.angle = getValue(parser, "angle", "0");
                layout.angleClip = getValue(parser, "angleClip", "0");
                String szKakaoCover = getValue(parser, "isNewKakaoCover");
                layout.isSnsBookCover = szKakaoCover != null && szKakaoCover.equalsIgnoreCase("true");

                if (!layout.angleClip.equals("0") && !layout.angleClip.equals("")) {
                    convertClipRect(layout, layout.angleClip);
                }

                layout.border = getValue(parser, "border");
                layout.isClick = getValue(parser, "isEditable");
                // 사진틀..
                layout.bordersinglecolortype = getValue(parser, "bordersinglecolortype");
                layout.bordersinglealpha = getValue(parser, "bordersinglealpha");
                layout.bordersinglethick = getValue(parser, "bordersinglethick");
                layout.bordersinglecolor = getValue(parser, "bordersinglecolor");
            } else {
                // browse 이미지.
                layout = new SnapsLayoutControl();
                layout.setX(getValue(parser, "x"));
                layout.y = getValue(parser, "y");
                layout.width = getValue(parser, "width");
                layout.height = getValue(parser, "height");
                layout.angle = getValue(parser, "angle", "0");
                layout.border = getValue(parser, "border");
                layout.isClick = getValue(parser, "isEditable");
            }

        } else if (localName.equalsIgnoreCase(TAG_LAYER)) {
            state = getValue(parser, "name");

            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // BackGround 이미지
                bg = new SnapsBgControl();
                bg.layerName = getValue(parser, "name");
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form = new SnapsFormControl();
                form.layerName = getValue(parser, "name");
            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
            } else if (state.equalsIgnoreCase(TAG_FORM_STYLE)) {
            }
        } else if (localName.equalsIgnoreCase(TAG_STYLE)) {

            if (state.equalsIgnoreCase(TAG_FORM_STYLE)) {
                fontFace = getValue(parser, "font_face");
                template.fonts.add(fontFace);
                Const_VALUE.SNAPS_TYPEFACE_NAME = fontFace;
                fontSize = getValue(parser, "font_size");
                fontColor = getValue(parser, "font_color");
                fontAlign = getValue(parser, "align");
            } else {

                textControl.format.fontFace = FontUtil.getFontFaceByChannel(getValue(parser, "font_face"));
                template.fonts.add(textControl.format.fontFace);
                Const_VALUE.SNAPS_TYPEFACE_NAME = textControl.format.fontFace;
                textControl.format.alterFontFace = getValue(parser, "alter_font_face");
                textControl.format.fontSize = getValue(parser, "font_size");
                textControl.format.fontColor = getValue(parser, "font_color");
                textControl.format.baseFontColor = textControl.format.fontColor;
                textControl.format.align = getValue(parser, "align");
                textControl.format.bold = getValue(parser, "bold");
                textControl.format.italic = getValue(parser, "italic");
                textControl.format.underline = getValue(parser, "underline");
                textControl.format.verticalView = getValue(parser, "vertical_view");
                textControl.albumMode = getValue(parser, "album_mode");

                textControl.format.auraOrderFontSize = getValue(parser, "auraOrder_FontSize");

            }

        } else if (localName.equalsIgnoreCase(TAG_REGIST)) {
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                bg.regName = getValue(parser, "name");
                bg.regValue = getValue(parser, "value");
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form.regName = getValue(parser, "name");
                form.regValue = getValue(parser, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_TEXT)) {
                textControl.regName = getValue(parser, "name");
                textControl.regValue = getValue(parser, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_IMAGE)) {
                layout.regName = getValue(parser, "name");
                layout.regValue = getValue(parser, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_FORM_STYLE)) {

                String name = getValue(parser, "name");
                assignCalendarFont(name);
            }
        } else if (localName.equalsIgnoreCase(TAG_SOURCE)) {
            Dlog.d("startTag() layout.mask2" + getValue(parser, "mask"));

            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // BackGround 이미지
                bg.type = getValue(parser, "type");
                bg.fit = getValue(parser, "fit");
                bg.bgColor = getValue(parser, "bgcolor");
                bg.coverColor = getValue(parser, "covercolor");
                bg.srcTargetType = getValue(parser, "target_type");
                bg.srcTarget = getValue(parser, "target");
                page.orgBgId = bg.srcTarget;
                bg.resourceURL = getValue(parser, "resourceURL");
                bg.getVersion = getValue(parser, "getVersion");

                if (Config.isCalendar()) {
//					if (preStr.compareTo("hidden") == 0) {
                    if (attrName.compareTo("root") == 0) {
                        summaryTaget = getValue(parser, "target");
                        attrName = "";
                    }
                }
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form.type = getValue(parser, "type");
                form.fit = getValue(parser, "fit");
                form.bgColor = getValue(parser, "bgcolor");
                form.srcTargetType = getValue(parser, "target_type");
                form.srcTarget = getValue(parser, "target");
                form.resourceURL = getValue(parser, "resourceURL");
                form.getVersion = getValue(parser, "getVersion");
                Dlog.d("startTag() form.resourceURL:" + form.resourceURL);
            } else {
                layout.type = getValue(parser, "type");
                layout.fit = getValue(parser, "fit");
                layout.bgColor = getValue(parser, "bgcolor");
                layout.srcTargetType = getValue(parser, "target_type");
                layout.srcTarget = getValue(parser, "target");
                layout.resourceURL = getValue(parser, "resourceURL");
                layout.mask = getValue(parser, "mask");
                Dlog.d("startTag() layout.mask:" + layout.mask);
                layout.maskURL = getValue(parser, "maskURL");
                layout.maskType = getValue(parser, "mask_type");
                layout.maskRadius = getValue(parser, "mask_radius");
                layout.setSnsproperty(getValue(parser, "snsproperty"));
                layout.setFormat(getValue(parser, "format"));
                layout.setTextType(getValue(parser, "texttype"));
                String imagefull = getValue(parser, "imagefull");
                layout.isImageFull = imagefull.equals("true");

            }
        } else if (localName.equalsIgnoreCase(TAG_CALENDAR_GRID)) {
            if (page.type.equalsIgnoreCase("cover"))
                return;

            layoutState = TAG_FORM_STYLE;

            String dayOffset = getValue(parser, "day_offest");
            String dayOffsets[] = dayOffset.split(" ");
            dayOffsetX = dayOffsets[0];
            dayOffsetY = dayOffsets[1];

            String dayTitleOffset = getValue(parser, "dayTitle_offest");
            String dayTitleOffsets[] = dayTitleOffset.split(" ");
            dayTitleOffsetX = dayTitleOffsets[0];
            dayTitleOffsetY = dayTitleOffsets[1];

            cellType = getValue(parser, "cellType");
            calWidth = getValue(parser, "width");
            calHeight = getValue(parser, "height");
            rowCount = getValue(parser, "rowCount");

            calX = getValue(parser, "x");
            calY = getValue(parser, "y");

        } else if (localName.equalsIgnoreCase(TAG_FORMSTYLE)) {

            layoutState = TAG_FORM_STYLE;

            titleX = getValue(parser, "x");
            titleY = getValue(parser, "y");
            titleWidth = getValue(parser, "width");
            titleHeight = getValue(parser, "height");

        } else if (localName.equalsIgnoreCase(TAG_TMPLINFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_TMPLPRICE)) {
            state = localName;
            price = new SnapsTemplatePrice();
        } else if (localName.equalsIgnoreCase(TAG_TEXTLIST)) {
            state = TAG_TEXTLIST;
            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;
            String[] temp = getValue(parser, "rc").replace(" ", "|").split("\\|");

            textControl.format.verticalView = getValue(parser, "vertical");

            int xx = (int) Float.parseFloat(temp[0]);
            int yy = (int) Float.parseFloat(temp[1]);
            int ww = (int) Float.parseFloat(temp[2]);
            int hh = (int) Float.parseFloat(temp[3]);

            textX = String.format("%d", xx);
            textY = String.format("%d", yy);
            textWidth = String.format("%d", ww);
            textHeight = String.format("%d", hh);

            textControl.setX(String.valueOf(xx));
            textControl.y = String.valueOf(yy);

            textControl.regName = getValue(parser, "name");
            textControl.regValue = getValue(parser, "value");
            textControl.angle = getValue(parser, "angle", "0");

            // 책등인 경우
            if (textControl.format.verticalView.equals("true")) {
                textControl.width = String.valueOf((int) (ww - xx));
                textControl.height = String.valueOf((int) (hh - yy));
            } else {
                textControl.width = String.valueOf(ww);
                textControl.height = String.valueOf(hh);
            }

            textControl.format.fontFace = FontUtil.getFontFaceByChannel(getValue(parser, "fontFace"));
            template.fonts.add(textControl.format.fontFace);
            textControl.format.alterFontFace = "";
            textControl.format.fontSize = getValue(parser, "fontSize");
            if (textControl.format.fontSize.equals("0"))
                textControl.format.fontSize = "12";

            String color = getValue(parser, "color");

            if (color.equals("null") || color.equals(""))
                textControl.format.fontColor = "000000";
            else
                textControl.format.fontColor = color.replace("#", "");

            textControl.format.baseFontColor = textControl.format.fontColor;

            textAlign = getValue(parser, "align");
            textControl.format.align = textAlign;
            textControl.format.bold = getValue(parser, "bold");
            textControl.format.italic = getValue(parser, "italic");
            textControl.format.underline = getValue(parser, "underline");

            textControl.setSnsproperty(getValue(parser, "snsproperty"));
            textControl.setTextType(getValue(parser, "texttype"));
            textControl.setFormat(getValue(parser, "format"));

            textControl.emptyText = getValue(parser, "emptyText");
            // id
            textControl.id = getValue(parser, "name");

            textControl.stick_target = getValue(parser, "stick_target");

            textControl.stick_dirction = getValue(parser, "stick_direction");

            textControl.stick_margin = getValue(parser, "stick_margin");

            textControl.stick_margin = getValue(parser, "stick_margin");

            if (getValue(parser, "k_vertical").equals("true")) {
                textControl.format.orientation = TextFormat.TEXT_ORIENTAION_VERTICAL;
            }
            checkKVertical(textControl);

            textControl.spacing = getValue(parser, "letterspace");
            textControl.format.setOverPrint(getValue(parser, "overPrint"));

        } else if (localName.equalsIgnoreCase(TAG_HTMLTEXT)) {
            state = TAG_HTMLTEXT;

        } else if (localName.equalsIgnoreCase(TAG_TEXTFLOW)) {
            if (textControl != null) {
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(getValue(parser, "fontFamily"));
                textControl.format.fontSize = getValue(parser, "fontSize");
            }

        }// 스티커...
        else if (localName.equalsIgnoreCase(TAG_CLIPART)) {
            state = TAG_CLIPART;
            clipart = new SnapsClipartControl();
            clipart.parse(parser);
            page.addControl(clipart);

        } else if (localName.equalsIgnoreCase(TAG_P)) {

        } else if (localName.equalsIgnoreCase(TAG_SPAN)) {
            state = TAG_SPAN;
            String fSize = getValue(parser, "fontSize");
            String fFamily = getValue(parser, "fontFamily");
            String fColor = getValue(parser, "color");

            textControl.format.fontFace = FontUtil.getFontFaceByChannel(fFamily);
            textControl.format.fontSize = fSize;

            String color = fColor;

            if (color.equals("null") || color.equals(""))
                textControl.format.fontColor = "000000";
            else
                textControl.format.fontColor = color.replace("#", "");

            textControl.format.baseFontColor = textControl.format.fontColor;
        } else if (localName.equalsIgnoreCase(TAG_HOLE)) {
            state = TAG_HOLE;
            hole = new SnapsHoleControl();
            hole.parse(parser);
            page.addControl(hole);
        } else if (localName.equalsIgnoreCase(TAG_HELPER)) {
            state = TAG_HELPER;
            helper = new SnapsHelperControl();
            helper.parse(parser);
            page.addControl(helper);
        } else if (localName.equalsIgnoreCase(TAG_STICK)) {
            state = TAG_STICK;
            stick = new SnapsStickControl();
            stick.parse(parser);
            page.addControl(stick);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_MASK)) {
            state = TAG_SCENE_MASK;
            sceneMaskControl = new SnapsSceneMaskControl();
            sceneMaskControl.parse(parser);
            page.addControl(sceneMaskControl);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_CUT)) {
            state = TAG_SCENE_CUT;
            sceneCutControl = new SnapsSceneCutControl();
            sceneCutControl.parse(parser);
            page.addControl(sceneCutControl);
        }
    }

    void endTag(XmlPullParser parser) {
        String localName = parser.getName();
        if (localName.equalsIgnoreCase(TAG_SCENE)) {
            if (Config.isCalendar()) {
                if (!isHidden && subType.compareTo("schedule_memo") != 0) {
                    template.getPages().add(page);
                }
            } else
                template.getPages().add(page);

        } else if (localName.equalsIgnoreCase(TAG_TEXT)) {
            // 페이지 번호도 저장한다 커버는 0번이 되겠다.
            textControl.setPageIndex(template.getPages().size());
            page.addControl(textControl);
        } else if (localName.equalsIgnoreCase(TAG_TEXTLIST)) {
            extractTextList();
        } else if (localName.equalsIgnoreCase(TAG_IMAGE)) {
            if (isHidden)
                return;

            // 페이지 번호도 저장한다 커버는 0번이 되겠다.
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER) || state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                // BackGround 이미지 , // Form 이미지
            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
                // Image Layer
                layout.setPageIndex(template.getPages().size());
                alignSnapsLayout(page.getOriginWidth(), Integer.parseInt(page.height), layout);
                page.addLayout(layout);
            } else {
                // browse 이미지.
                layout.setPageIndex(template.getPages().size());
                alignSnapsLayout(page.getOriginWidth(), Integer.parseInt(page.height), layout);
                page.addLayout(layout);
            }
        } else if (localName.equalsIgnoreCase(TAG_LAYER)) {
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                // 해당컨트롤 넓이가 있는경우에만 추가를 한다.
                // 빈테그에 대한 처리..

                if (bg.type.length() > 0)
                    page.addBg(bg);
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                // 해당컨트롤 넓이가 있는경우에만 추가를 한다.
                // 빈테그에 대한 처리..
                if (form.type.length() > 0)
                    page.addForm(form);
            } else if (state.equalsIgnoreCase(TAG_IMAGE_LAYER)) {
                // Image Layer
            } else if (state.equalsIgnoreCase(TAG_FORM_STYLE)) {

                initCalendarControl();

            } else {
                // browse 이미지.
            }
        } else if (localName.equalsIgnoreCase(TAG_TMPLPRICE)) {
            template.priceList.add(price);
        } else if (localName.equalsIgnoreCase(TAG_TMPLINFO)) {

        }

    }

    void textTag(XmlPullParser parser) {

        String stringChars = parser.getText();

        if (state != null) {
            if (state.equalsIgnoreCase(TAG_TEXT)) {
                if ("initial_text".equalsIgnoreCase(element)) {
                    if (stringChars.length() > 0) {
                        textControl.initialText = stringChars;
                    }
                }
            } else if (state.equalsIgnoreCase(TAG_TEXTLIST)) {

            } else if (state.equalsIgnoreCase(TAG_HTMLTEXT)) {
                if (stringChars.length() > 0) {

                    htmlText += stringChars;
                }

            } else if (state.equalsIgnoreCase(TAG_TMPLINFO)) {
                if ("F_PROD_CODE".equalsIgnoreCase(element)) {
                    template.info.F_PROD_CODE = stringChars;
                } else if ("F_PROD_NAME".equalsIgnoreCase(element)) {
                    template.info.F_PROD_NAME = stringChars;
                } else if ("F_PROD_SIZE".equalsIgnoreCase(element)) {
                    template.info.F_PROD_SIZE = stringChars;
                } else if ("F_PROD_NICK_NAME".equalsIgnoreCase(element)) {
                    template.info.F_PROD_NICK_NAME = stringChars;
                } else if ("F_GLOSSY_TYPE".equalsIgnoreCase(element)) {
                    template.info.F_GLOSSY_TYPE = stringChars;
                } else if ("F_USE_ANALECTA".equalsIgnoreCase(element)) {
                    template.info.F_USE_ANALECTA = stringChars;
                } else if ("F_ENLARGE_IMG".equalsIgnoreCase(element)) {
                    template.info.F_ENLARGE_IMG = stringChars;
                } else if ("FEP".equalsIgnoreCase(element)) {
                    template.info.FEP = stringChars;
                } else if ("F_USE_WATERMARK".equalsIgnoreCase(element)) {
                    template.info.F_USE_WATERMARK = stringChars;
                } else if ("F_COVER_VIRTUAL_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER_VIRTUAL_WIDTH = stringChars;
                } else if ("F_COVER_VIRTUAL_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_COVER_VIRTUAL_HEIGHT = stringChars;
                } else if ("F_COVER_EDGE_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER_EDGE_WIDTH = stringChars;
                } else if ("F_COVER_EDGE_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_COVER_EDGE_HEIGHT = stringChars;
                } else if ("F_BASE_QUANTITY".equalsIgnoreCase(element)) {
                    template.info.F_BASE_QUANTITY = stringChars;
                } else if ("F_MAX_QUANTITY".equalsIgnoreCase(element)) {

                } else if ("F_PRNT_TYPE".equalsIgnoreCase(element)) {
                    template.info.F_PRNT_TYPE = stringChars;
                } else if ("F_EDIT_COVER".equalsIgnoreCase(element)) {
                    template.info.F_EDIT_COVER = stringChars;
                } else if ("F_COVER_TYPE".equalsIgnoreCase(element)) {
                    template.info.F_COVER_TYPE = stringChars;
                } else if ("F_COVER_MID_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER_MID_WIDTH = stringChars;
                } else if ("F_COVER2_MID_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER2_MID_WIDTH = stringChars;
                } else if ("F_COVER_MM_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER_MM_WIDTH = stringChars;
                } else if ("F_COVER_MM_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_COVER_MM_HEIGHT = stringChars;
                } else if ("F_COVER2_MM_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER2_MM_WIDTH = stringChars;
                } else if ("F_COVER2_MM_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_COVER2_MM_HEIGHT = stringChars;
                } else if ("F_COVER_CHANGE_QUANTITY".equalsIgnoreCase(element)) {
                    template.info.F_COVER_CHANGE_QUANTITY = stringChars;
                } else if ("F_TITLE_MM_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_TITLE_MM_WIDTH = stringChars;
                } else if ("F_TITLE_MM_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_TITLE_MM_HEIGHT = stringChars;
                } else if ("F_PAGE_MM_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_PAGE_MM_WIDTH = stringChars;
                } else if ("F_PAGE_MM_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_PAGE_MM_HEIGHT = stringChars;
                } else if ("F_PAGE_PIXEL_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_PAGE_PIXEL_WIDTH = stringChars;
                } else if ("F_PAGE_PIXEL_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_PAGE_PIXEL_HEIGHT = stringChars;
                } else if ("F_CP_CODE".equalsIgnoreCase(element)) {
                    template.info.F_CP_CODE = stringChars;
                } else if ("F_UI_COVER".equalsIgnoreCase(element)) {
                    template.info.F_UI_COVER = stringChars;
                } else if ("F_UI_BACKGROUND".equalsIgnoreCase(element)) {
                    template.info.F_UI_BACKGROUND = stringChars;
                } else if ("F_UI_LAYOUT".equalsIgnoreCase(element)) {
                    template.info.F_UI_LAYOUT = stringChars;
                } else if ("F_UI_BORDER".equalsIgnoreCase(element)) {
                    template.info.F_UI_BORDER = stringChars;
                } else if ("F_THUMBNAIL_STEP".equalsIgnoreCase(element)) {
                    template.info.F_THUMBNAIL_STEP = stringChars;
                } else if ("F_stringChars_SIZE_BASE".equalsIgnoreCase(element)) {
                    template.info.F_TEXT_SIZE_BASE = stringChars;
                } else if ("F_PROD_TYPE".equalsIgnoreCase(element)) {
                    template.info.F_PROD_TYPE = stringChars;
                } else if ("F_RES_MIN".equalsIgnoreCase(element)) {
                    template.info.F_RES_MIN = stringChars;
                } else if ("F_RES_DISABLE".equalsIgnoreCase(element)) {
                    template.info.F_RES_DISABLE = stringChars;
                } else if ("F_PAGE_START_NUM".equalsIgnoreCase(element)) {
                    template.info.F_PAGE_START_NUM = stringChars;
                } else if ("F_CENTER_LINE".equalsIgnoreCase(element)) {
                    template.info.F_CENTER_LINE = stringChars;
                } else if ("F_UNITstringChars".equalsIgnoreCase(element)) {
                    template.info.F_UNITTEXT = stringChars;
                } else if ("F_CALENDAR_BONUS_12".equalsIgnoreCase(element)) {
                    template.info.F_CALENDAR_BONUS_12 = stringChars;
                } else if ("F_SPLIT_COVER_MIDSIZE".equalsIgnoreCase(element)) {
                    template.info.F_SPLIT_COVER_MIDSIZE = stringChars;
                } else if ("F_ALLOW_NO_FULL_IMAGE_YORN".equalsIgnoreCase(element)) {
                    template.info.F_ALLOW_NO_FULL_IMAGE_YORN = stringChars;
                } else if ("F_TMPL_CODE".equalsIgnoreCase(element)) {
                    Config.setTMPL_CODE(stringChars);
                    template.info.F_TMPL_CODE = stringChars;
                } else if ("F_TMPL_ID".equalsIgnoreCase(element)) {
                    template.info.F_TMPL_ID = stringChars;
                } else if ("F_TMPL_NAME".equalsIgnoreCase(element)) {
                    template.info.F_TMPL_NAME = stringChars;
                } else if ("F_XML_ID".equalsIgnoreCase(element)) {
                    template.info.F_XML_ID = stringChars;
                } else if ("F_TMPL_DESC".equalsIgnoreCase(element)) {
                    template.info.F_TMPL_DESC = stringChars;
                } else if ("F_REG_DATE".equalsIgnoreCase(element)) {
                    template.info.F_REG_DATE = stringChars;
                } else if ("F_TMPL_TITLE".equalsIgnoreCase(element)) {
                    template.info.F_TMPL_TITLE = stringChars;
                } else if ("F_EDIT_PLATFORM".equalsIgnoreCase(element)) {
                    template.info.F_EDIT_PLATFORM = stringChars;
                } else if ("F_COVER_XML_WIDTH".equalsIgnoreCase(element)) {
                    template.info.F_COVER_XML_WIDTH = stringChars;
                } else if ("F_FRAME_TYPE".equalsIgnoreCase(element)) {
                    template.info.F_FRAME_TYPE = stringChars;
                } else if ("F_COVER_XML_HEIGHT".equalsIgnoreCase(element)) {
                    template.info.F_COVER_XML_HEIGHT = stringChars;
                } else if ("F_FRAME_ID".equalsIgnoreCase(element)) {
                    template.info.F_FRAME_ID = stringChars;
                }
            } else if (state.equalsIgnoreCase(TAG_TMPLPRICE)) {
                if ("F_COMP_CODE".equalsIgnoreCase(element)) {
                    price.F_COMP_CODE = stringChars;
                } else if ("F_PROD_CODE".equalsIgnoreCase(element)) {
                    price.F_PROD_CODE = stringChars;
                } else if ("F_TMPL_CODE".equalsIgnoreCase(element)) {
                    price.F_TMPL_CODE = stringChars;
                } else if ("F_SELL_PRICE".equalsIgnoreCase(element)) {
                    price.F_SELL_PRICE = stringChars;
                } else if ("F_ORG_PRICE".equalsIgnoreCase(element)) {
                    price.F_ORG_PRICE = stringChars;
                } else if ("F_PRICE_NUM".equalsIgnoreCase(element)) {
                    price.F_PRICE_NUM = stringChars;
                } else if ("F_PRNT_BQTY".equalsIgnoreCase(element)) {
                    price.F_PRNT_BQTY = stringChars;
                } else if ("F_PRNT_EQTY".equalsIgnoreCase(element)) {
                    price.F_PRNT_EQTY = stringChars;
                } else if ("F_PAGE_ADD_PRICE".equalsIgnoreCase(element)) {
                    price.F_PAGE_ADD_PRICE = stringChars;
                } else if ("F_ORG_PAGE_ADD_PRICE".equalsIgnoreCase(element)) {
                    price.F_ORG_PAGE_ADD_PRICE = stringChars;
                }
            } else if (state.equalsIgnoreCase(TAG_SPAN)) {
                Dlog.d("textTag() span:" + stringChars);
                textControl.text = stringChars;

            }
        }

        element = null;

    }

    protected String getValue(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue("", name);
        return (value == null) ? "" : value;
    }

    protected String getValue(XmlPullParser parser, String name, String initValue) {
        String value = parser.getAttributeValue("", name);
        return (value == null) ? initValue : value;
    }

    /***
     * 회전시 중심에서 회전이 된것처럼 좌표를 변환해주는 함수..
     *
     * @param control
     * @param angle
     */
    void convertClipRect(SnapsControl control, String angle) {

        try {
            int left = (int) Float.parseFloat(control.x);
            int top = (int) Float.parseFloat(control.y);
            int right = left + (int) Float.parseFloat(control.width);
            int bottom = top + (int) Float.parseFloat(control.height);

            Rect r = new Rect(left, top, right, bottom);
            float fAngle = Float.parseFloat(angle);
            Rect rRect = RotateUtil.convertCenterRotateRect(r, fAngle);

            control.x = String.valueOf(rRect.left);
            control.y = String.valueOf(rRect.top);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void extractTextList() {
        if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
//			textControl.setPageIndex(template.getPages().size());
//			checkKVertical(textControl);
//			page.addControl(textControl);
            extractText();
            return;
        }

        if (htmlText != null && htmlText.trim().length() > 0) {
            if (Config.isCalendar())
                extractTextForCalendar();
            else if (Const_PRODUCT.isSNSBook())
                extractTextForSNSBook();
            else {
                extractText();
            }
        } else {
            textControl.setPageIndex(template.getPages().size());
            checkKVertical(textControl);
            page.addControl(textControl);
        }
    }

    /**
     * FIXME extractCalendar를 일반 htmlText에서 사용할 수 있도록 개조한 거라, 불완전 할 수 있습니다.
     */
    private void extractTextForSNSBook() {
        String[] arParams1 = htmlText.split("<span");
        Dlog.d("extractTextForSNSBook() stringChars:" + htmlText);
        int fontHeight = 0;

        // 하나의 htmltext에서 하나의 span만을 가져온다..
        boolean isCreateTextControl = false;

        String fontFamily = "";
        String label = "";
        for (int i = 0; i < arParams1.length; i++) {

            if (arParams1[i].contains("<TextFlow") || isCreateTextControl) {
                // font family name을 가져온다.
                fontFamily = getParseText("fontFamily=\"", arParams1[i], "");
                fontSize = getParseText("fontSize=\"", arParams1[i], "");
                continue;
            }

            int index2 = 0;
            if (arParams1[i].contains("#")) {
                index2 = arParams1[i].indexOf("\"", arParams1[i].indexOf("\"") + 1);
                fontColor = arParams1[i].substring(arParams1[i].indexOf("#") + 1, index2);
            } else {
                fontColor = "000000";
            }
            Dlog.d("extractTextForSNSBook() font color:" + fontColor);

            fontFamily = getParseText("fontFamily=\"", arParams1[i], fontFamily);
            Dlog.d("extractTextForSNSBook() font family:" + fontFamily);

            String fontWeight = getParseText("fontWeight=\"", arParams1[i], "");
            String fontStyle = getParseText("fontStyle=\"", arParams1[i], "");

            fontSize = getParseText("fontSize=\"", arParams1[i], fontSize);
            Dlog.d("extractTextForSNSBook() font size:" + fontSize);

            index2 = arParams1[i].indexOf(">");
            if (index2 < 0)
                continue;

            String params2 = arParams1[i].substring(index2, arParams1[i].length());
            index2 = params2.indexOf("<");

            if (index2 < 0)
                continue;

            label = params2.substring(1, index2);
            Dlog.d("extractTextForSNSBook() font label:" + label);

            String text = label.replace("&amp;", "&");

            if (textControl == null)
                textControl = new SnapsTextControl();

            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

            textControl.setX(textX);

            int nY = (int) Float.parseFloat(textY) + fontHeight;
            textControl.y = String.format("%d", nY);

            fontHeight += ((int) Float.parseFloat(fontSize) + 2);

            if (!textControl.format.verticalView.equals("true")) {
                textControl.width = textWidth;
                textControl.height = textHeight;
            }

            textControl.rowCount = rowCount;
            textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFamily);
            template.fonts.add(textControl.format.fontFace + fontStyle + fontWeight);
            textControl.format.fontSize = fontSize;
            textControl.format.align = textAlign;
            textControl.type = "calendar";
            textControl.controType = "textlist";
            textControl.format.bold = fontWeight;
            textControl.format.italic = fontStyle;

            template.fonts.add(fontFamily);
            textControl.format.fontColor = fontColor;
            textControl.format.baseFontColor = textControl.format.fontColor;

            textControl.setPageIndex(template.getPages().size());

            checkKVertical(textControl);
            page.addControl(textControl);
            isCreateTextControl = true;
        }
        textList++;

        htmlText = "";
    }

    private void extractText() {//<span color="#000000" fontSize="8" fontFamily="스냅스 윤고딕 230">　</span></p></TextFlow>
        Document document = Jsoup.parse(htmlText);
        if (document.childNodeSize() == 0) return;

        Elements pElements = document.select("p");
        if (pElements == null || pElements.size() < 1) return;

        StringBuilder builder = new StringBuilder();
        for (Element pElement : pElements) {
            if (builder.length() > 0) builder.append("\n");

            String text = pElement.text();
            if (!StringUtil.isEmpty(text)) {
                text = text.replace("&amp;", "&");
                builder.append(text);
            }
        }

        Elements spanElements = pElements.select("span");

        String fontSize = spanElements.attr("fontSize");
        String color = spanElements.attr("color");
        if (color.contains("#")) {
            fontColor = color.substring(color.indexOf("#") + 1);
        } else
            fontColor = "000000";

        String fontWeight = spanElements.attr("fontWeight");

        String fontStyle = spanElements.attr("fontStyle");

        String textDecoration = spanElements.attr("textDecoration");

        String fontFamily = spanElements.attr("fontfamily");
        if (StringUtil.isEmpty(fontFamily))
            fontFamily = spanElements.attr("fontFamily");

        if (textControl == null)
            textControl = new SnapsTextControl();

        textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

        textControl.setX(textX);

        int nY = (int) Float.parseFloat(textY);
        textControl.y = String.format("%d", nY);

        if (!textControl.format.verticalView.equals("true")) {
            textControl.width = textWidth;
            textControl.height = textHeight;
        }

        if (!StringUtil.isEmpty(fontFamily)) {
            textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFamily);
        }

        if (!StringUtil.isEmpty(textControl.format.fontFace)) {
            template.fonts.add(textControl.format.fontFace + fontStyle + fontWeight);
        }

        if (!StringUtil.isEmpty(fontSize)) {
            textControl.format.fontSize = fontSize;
        }

        if (!StringUtil.isEmpty(textAlign)) {
            textControl.format.align = textAlign;
        }

        textControl.controType = "textlist";
        textControl.format.bold = fontWeight;
        textControl.format.italic = fontStyle;
        textControl.format.underline = textDecoration;

        template.fonts.add(fontFamily);
        textControl.format.fontColor = fontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.text = builder.toString();

        textControl.setPageIndex(template.getPages().size());

        checkKVertical(textControl);
        page.addControl(textControl);
        textControl = null;

        textList++;

        htmlText = "";
    }

    String getParseText(String tagName, String plainText, String defaultValue) {
        int start = plainText.indexOf(tagName);

        if (start < 0)
            return defaultValue;

        String parsedText = plainText.substring(start + tagName.length(), plainText.length());
        int end = parsedText.indexOf("\"");

        if (parsedText.length() <= 0)
            return defaultValue;

        String value = parsedText.substring(0, end);

        return value;
    }

    private void extractTextForCalendar() {
        String[] arParams1 = htmlText.split("<span color=\"");
        Dlog.d("extractTextForCalendar() stringChars:" + htmlText);
        int fontHeight = 0;

        if (subType != null && subType.compareTo("schedule_memo") == 0)
            return;
        for (int i = 0; i < arParams1.length; i++) {
            int len = arParams1.length;
            String fontFamily = "";
            String label = "";

            if (arParams1[i].startsWith("#")) {
                int index2 = arParams1[i].indexOf("\"");

                int indexP = arParams1[i].indexOf("<p");

                fontColor = arParams1[i].substring(1, index2);
                Dlog.d("extractTextForCalendar() font color:" + fontColor);

                index2 = arParams1[i].indexOf("fontFamily=\"");

                String params2 = arParams1[i].substring(index2 + 12, arParams1[i].length());

                index2 = params2.indexOf("\"");
                if (index2 < 0)
                    continue;

                fontFamily = params2.substring(0, index2);
                Dlog.d("extractTextForCalendar() font family:" + fontFamily);

                index2 = arParams1[i].indexOf("fontSize=\"");

                if (index2 < 0)
                    continue;

                params2 = arParams1[i].substring(index2 + 10, arParams1[i].length());

                index2 = params2.indexOf("\"");

                fontSize = params2.substring(0, index2);
                Dlog.d("extractTextForCalendar() font size:" + fontSize);

                index2 = arParams1[i].indexOf(">");
                if (index2 < 0)
                    continue;

                params2 = arParams1[i].substring(index2, arParams1[i].length());

                index2 = params2.indexOf("<");

                if (index2 < 0)
                    continue;

                label = params2.substring(1, index2);
                Dlog.d("extractTextForCalendar() font label:" + label);

                String text = label.replace("&amp;", "&");

                String tempValue = textControl.regValue;
                textControl = new SnapsTextControl();
                textControl.regValue = tempValue;

                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = StringUtil.convertEmojiAliasToUniCode(text);
                textControl.setX(textX);

                int nY = (int) Float.parseFloat(textY) + fontHeight;
                textControl.y = String.format("%d", nY);

                fontHeight += ((int) Float.parseFloat(fontSize) + 2);

                textControl.width = textWidth;// getValue(attributes, "width");
                textControl.height = textHeight;// getValue(attributes, "height");
                textControl.rowCount = rowCount;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFamily);
                template.fonts.add(textControl.format.fontFace);
                textControl.format.fontSize = fontSize;
                textControl.format.align = textAlign;
                textControl.type = "calendar";
                textControl.controType = "textlist";

                Const_VALUE.SNAPS_TYPEFACE_NAME2 = fontFamily;

                _textListFont.put("textlist", textControl.format.fontFace);
                textControl.format.fontColor = fontColor;
                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.setPageIndex(template.getPages().size());

                page.addControl(textControl);

            }

        }
        textList++;

        htmlText = "";
    }

    /***
     * 레이아웃의 좌표가 음수인경우 양수로 변환해주는 함수 좌표는 음수가 나올수 없기 때문임..
     *
     * @param pageW
     * @param pageH
     * @param control
     */
    void alignSnapsLayout(int pageW, int pageH, SnapsLayoutControl control) {

    }

    private void assignCalendarFont(String name) {
        titleStyle = name;
        if (titleStyle.compareTo("year") == 0) {
            yearX = titleX;
            yearY = titleY;
            yearWidth = titleWidth;
            yearHeight = titleHeight;

            yearFontFace = fontFace;
            yearFontColor = fontColor;
            yearFontSize = fontSize;
            yearFontAlign = fontAlign;

        } else if (titleStyle.compareTo("month") == 0) {
            monthX = titleX;
            monthY = titleY;
            monthWidth = titleWidth;
            monthHeight = titleHeight;

            monthFontFace = fontFace;
            monthFontColor = fontColor;
            monthFontSize = fontSize;
            monthFontAlign = fontAlign;

        } else if (titleStyle.compareTo("month_title") == 0) {
            monthTitleX = titleX;
            monthTitleY = titleY;
            monthTitleWidth = titleWidth;
            monthTitleHeight = titleHeight;

            monthTitleFontFace = fontFace;
            monthTitleFontColor = fontColor;
            monthTitleFontSize = fontSize;
            monthTitleFontAlign = fontAlign;

        } else if (titleStyle.compareTo("day") == 0) {
            dayFontFace = fontFace;
            dayFontColor = fontColor;
            dayFontSize = fontSize;
            dayFontAlign = fontAlign;

        } else if (titleStyle.compareTo("day_title") == 0) {

            dayTitleFontFace = fontFace;
            dayTitleFontColor = fontColor;
            dayTitleFontSize = fontSize;
            dayTitleFontAlign = fontAlign;

        }
    }

    private void initCalendarControl() {
        if (rowCount.compareTo("0") == 0)
            return;

        if (subType != null && subType.compareTo("schedule_memo") == 0)
            return;

        int nWidth = (int) Float.parseFloat(calWidth);
        int nHeight = (int) Float.parseFloat(calHeight);
        int nRowCount = (int) Float.parseFloat(rowCount);
        int nStartX = (int) Float.parseFloat(calX);
        int nStartY = (int) Float.parseFloat(calY);

        int nDayOffsetX = (int) Float.parseFloat(dayOffsetX);
        int nDayOffsetY = (int) Float.parseFloat(dayOffsetY);

        int nDayTitleOffsetX = (int) Float.parseFloat(dayTitleOffsetX);
        int nDayTitleOffsetY = (int) Float.parseFloat(dayTitleOffsetY);

        int nCellWidth = 0;
        int maxBounday = 0;
        int nMax = GetParsedXml.getMaximumDay(nStartMonth, nStartYear);

        int nType = 0;

        if (nRowCount == 2) {
            if (Config.isCalendarNormalVert(Config.getPROD_CODE()) == false)
                nCellWidth = nWidth / 31;
            else
                nCellWidth = nWidth / 31;

        } else {
            nType = 1;

            nCellWidth = nWidth / 7;
        }

        int nCellHeight = nHeight / nRowCount;

        processYear(nType);

        processMonth(nType);
        if (!monthTitleFontFace.isEmpty())
            processMonthTitle(nType);

        if (nRowCount >= 5) {
            processBackCalendar(nStartX, nStartY, nCellWidth, nCellHeight, nDayOffsetX, nDayOffsetY, nDayTitleOffsetX, nDayTitleOffsetY, nRowCount);
        } else

            processFrontCalendar(nStartX, nStartY, nCellWidth, nCellHeight, nDayOffsetX, nDayOffsetY);

        boolean nextMonth = false;
        if (nRowCount >= 5) {
            nextMonth = true;
        } else if (Config.isWoodBlockCalendar()) {
            nextMonth = true;
        }

        if (nextMonth) {
            nStartMonth++;
            if (nStartMonth == 13) {
                nStartYear++;
                nStartMonth = 1;
            }
        }
        monthTitleFontFace = "";
    }

    private void processMonth(int type) {
        textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

        textControl.text = String.format("%d", nStartMonth);
        if (monthY == null || monthY.isEmpty())
            return;
        int nMonthY = (int) Float.parseFloat(monthY);
        monthY = String.format("%d", nMonthY);

        textControl.setX(monthX);
        textControl.y = monthY;
        textControl.width = monthWidth;
        textControl.height = monthHeight;
        textControl.rowCount = rowCount;
        textControl.format.fontFace = FontUtil.getFontFaceByChannel(monthFontFace);
        textControl.format.fontSize = monthFontSize;
        textControl.format.align = monthFontAlign;

        textControl.format.fontColor = monthFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.type = "calendar";
        textControl.controType = "month";

        if (type == 1) {
            textControl.controType = "month";
            _textListFont.put("month", textControl.format.fontFace);

        } else {
            textControl.controType = "month_front";
            _textListFont.put("month_front", textControl.format.fontFace);

        }

        textControl.setPageIndex(template.getPages().size());
        page.addControl(textControl);
    }

    private void processMonthTitle(int type) {
        textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

        String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        textControl.text = months[nStartMonth - 1];

        if (monthTitleY == null || monthTitleY.isEmpty())
            return;

        int nMonthTitleY = (int) Float.parseFloat(monthTitleY);
        String MonthTitleY = String.format("%d", nMonthTitleY);

        textControl.setX(monthTitleX);
        textControl.y = MonthTitleY;
        textControl.width = monthTitleWidth;
        textControl.height = monthTitleHeight;
        textControl.rowCount = rowCount;
        textControl.format.fontFace = FontUtil.getFontFaceByChannel(monthTitleFontFace);
        textControl.format.fontSize = monthTitleFontSize;
        textControl.format.align = monthTitleFontAlign;

        textControl.format.fontColor = monthTitleFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.type = "calendar";
        textControl.controType = "month_title";

        if (type == 1) {
            textControl.controType = "month_title";
            _textListFont.put("month_title", textControl.format.fontFace);

        } else {
            textControl.controType = "month_title_front";
            _textListFont.put("month_title_front", textControl.format.fontFace);

        }

        textControl.setPageIndex(template.getPages().size());
        page.addControl(textControl);

    }

    private void processYear(int type) {
        textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

        textControl.text = String.format("%d", nStartYear);

        textControl.setX(yearX);
        textControl.y = yearY;
        textControl.width = yearWidth;
        textControl.height = yearHeight;
        textControl.rowCount = rowCount;
        textControl.format.fontFace = FontUtil.getFontFaceByChannel(yearFontFace);
        textControl.format.fontSize = yearFontSize;
        textControl.format.align = yearFontAlign;

        textControl.format.fontColor = yearFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.type = "calendar";

        if (type == 1) {
            textControl.controType = "year";
            _textListFont.put("year", textControl.format.fontFace);

        } else {
            textControl.controType = "year_front";
            _textListFont.put("year_front", textControl.format.fontFace);

        }

        textControl.setPageIndex(template.getPages().size());
        page.addControl(textControl);
    }

    private void processBackCalendar(int nStartX, int nStartY, int nCellWidth, int nCellHeight, int nDayOffsetX, int nDayOffsetY, int nDayTitleOffsetX, int nDayTitleOffsetY, int nRow) {
        int nMax = 0;
        nMax = GetParsedXml.getMaximumDay(nStartMonth, nStartYear);
        int nIndex = GetParsedXml.getStarCalendarIndex(nStartMonth, nStartYear);
        int row = 5;

        if (Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarWide(Config.getPROD_CODE()))
            row = 6;

        row = nRow;
        int cmp = nMax + nIndex - row * 7;

        int nX = 0;
        int nY = 0;
        int day = 0;

        int oldNX = 0;

        _textListFont.put("day", dayFontFace);

        boolean bFlag = false;
        String oldFontAlign = dayFontAlign;
        String oldFontFace = dayFontFace;

        for (int j = 0; j < row; j++) {
            for (int i = 0; i < 7; i++) {
                if (j == 0 && i == 0)
                    i = nIndex;

                nX = nStartX + nCellWidth * i + nDayOffsetX;

                nY = nStartY + nCellHeight * j + nDayOffsetY;
                if (Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarWide(Config.getPROD_CODE())) {
                    nX = nStartX + nCellWidth * i;
                    nY = nStartY + nCellHeight * j + nDayOffsetY;
                }

                String label = "";

                label = String.format("%d", day + 1);

                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);

                String oldFontSize = dayFontSize;

                String oldHeight = dayFontSize;
                String cellWidth = "";

                cellWidth = String.format("%d", nCellWidth / 2);

                if (Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarWide(Config.getPROD_CODE())) {
                    cellWidth = String.format("%d", nCellWidth);

                }
                if (dayFontAlign.compareTo("center") == 0 && (!Config.isCalendarMini(Config.getPROD_CODE()) && !Config.isCalendarWide(Config.getPROD_CODE()))) {
                    cellWidth = String.format("%d", nCellWidth / 2);
                    nX = nStartX + nCellWidth * i + nDayOffsetX;

                    if (cellType.compareTo("1") == 0) {
                        cellWidth = String.format("%d", nCellWidth);
                        nX = nStartX + nCellWidth * i;
                        nY = nStartY + nCellHeight * j + nDayOffsetY;

                        oldHeight = String.format("%d", nCellHeight);

                    } else
                        oldFontAlign = dayFontAlign;
                    if (cmp > 0 && j == (row - 1) && i < cmp) {
                        if (cellType.compareTo("1") == 0) {
                            cellWidth = String.format("%d", nCellWidth / 2);
                            int nFontSize = (int) Float.parseFloat(dayFontSize);
                            nX = nStartX + nCellWidth * i + nDayOffsetX;
                            nY = nStartY + nCellHeight * j;

                            int _nFontSize = (int) Float.parseFloat(dayFontSize);
                            int _nHeight = nCellHeight - nDayOffsetY;
                            oldHeight = String.format("%d", _nHeight);
                            oldFontFace = dayFontFace;
                            if (_nFontSize > 20) {
                                oldFontSize = String.format("%d", (int) (_nFontSize * 0.7));
                                Dlog.d("processBackCalendar() font size:" + oldFontSize);
                            }

                            nY = nStartY + nCellHeight * j + 5;// + nDayOffsetY;
                            bFlag = true;
                        }

                    }

                }
                String strNX = String.format("%d", nX);
                String strNY = String.format("%d", nY);

                textControl = new SnapsTextControl();

                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = label;
                textControl.setX(strNX);
                textControl.y = strNY;
                textControl.width = cellWidth;
                textControl.height = oldHeight;
                textControl.rowCount = rowCount;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(oldFontFace);
                textControl.format.fontSize = oldFontSize;

                if (cellType.compareTo("1") == 0) {
                    if (cmp > 0 && j == (row - 1) && i < cmp)
                        oldFontAlign = "left";
                }

                textControl.format.align = oldFontAlign;

                if (!isHoliday)
                    textControl.format.fontColor = dayFontColor;
                else
                    textControl.format.fontColor = "ff0000";

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.type = "calendar";
                textControl.controType = "day";

                textControl.setPageIndex(template.getPages().size());

                Dlog.d("processBackCalendar() oldFontFace:" + oldFontFace);

                page.addControl(textControl);

                oldFontAlign = dayFontAlign;

                if (!Config.isCalendarMini(Config.getPROD_CODE()) && !Config.isCalendarWide(Config.getPROD_CODE())) {
                    oldFontFace = dayFontFace;

                    processDayTitle(day, i, j, nStartX, nStartY, nCellWidth, nCellHeight, nDayOffsetX, nDayTitleOffsetX, nDayTitleOffsetY);
                }

                if (day == nMax - 1) {
                    oldFontFace = dayFontFace;

                    if (Config.isCalendarWide(Config.getPROD_CODE()) || Config.isCalendarMini(Config.getPROD_CODE()))
                        return;
                    else {
                        oldNX = nStartX + nDayOffsetX;
                        if (cmp > 0)
                            break;
                        else
                            return;
                    }

                }

                day++;

            }

        }

        if (cmp > 0 && row != 6)// cell이 모자란 경우

        {

            for (int _i = 0; _i < cmp; _i++) {
                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);

                textControl = new SnapsTextControl();
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                String label = String.format("%d", day + 1);
                textControl.text = label;

                nX = nStartX + nCellWidth * _i + nDayOffsetX;

                nY = nStartY + nCellHeight * 4 + nCellHeight / 2 + nDayOffsetY;

                String cellWidth = String.format("%d", nCellWidth / 2);
                String oldFontSize = dayFontSize;
                if (dayFontAlign.compareTo("center") == 0) {
                    cellWidth = String.format("%d", nCellWidth / 2);
                    nY = nStartY + nCellHeight * 5 - (int) Float.parseFloat(dayFontSize);

                    if (cellType.compareTo("1") == 0) {
                        if (nDayOffsetX < 0)
                            nDayOffsetX = -nDayOffsetX;

                        if (nDayOffsetY < 0)
                            nDayOffsetY = -nDayOffsetY;

                        cellWidth = String.format("%d", nCellWidth / 2);

                        nX = nStartX + nCellWidth * _i + nCellWidth / 2 - 5;

                        int _nFontSize = (int) Float.parseFloat(dayFontSize);
                        if (_nFontSize > 20)
                            oldFontSize = String.format("%d", (int) (_nFontSize * 0.7));

                        nY = nStartY + nCellHeight * 5 - nCellHeight / 2 + 5;// + nDayOffsetY;

                        textControl.format.align = "right";

                    } else {
                        cellWidth = String.format("%d", nCellWidth / 2);

                        nX = nStartX + nCellWidth * _i + nDayOffsetX;
                        nY = nStartY + nCellHeight * 4 + nCellHeight / 2 + nDayOffsetY;
                        textControl.format.align = dayFontAlign;// "right";

                    }

                } else
                    textControl.format.align = dayFontAlign;

                String strNX = String.format("%d", nX);
                String strNY = String.format("%d", nY);

                textControl.setX(strNX);
                textControl.y = strNY;
                textControl.width = cellWidth;
                textControl.height = oldFontSize;
                textControl.rowCount = rowCount;

                Dlog.d("processBackCalendar() oldFontFace:" + oldFontFace);
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(oldFontFace);
                textControl.format.fontSize = oldFontSize;

                if (!isHoliday)
                    textControl.format.fontColor = dayFontColor;
                else
                    textControl.format.fontColor = "ff0000";

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.type = "calendar";

                textControl.setPageIndex(template.getPages().size());
                page.addControl(textControl);

                day++;

            }

        }
    }

    private void processDayTitle(int day, int i, int j, int nStartX, int nStartY, int nCellWidth, int nCellHeight, int nDayOffsetX, int nDayTitleOffsetX, int nDayTitleOffsetY) {
        // dayTitle2
        if (Config.isCalendarMini(Config.getPROD_CODE()))
            return;

        String dayTitle = GetParsedXml.getDayTitle(nStartMonth, day + 1, nStartYear);
        String dayTitle2 = GetParsedXml.getDayTitle2(nStartMonth, day + 1, nStartYear);

        String test = GetParsedXml.getDayTitle(11, 8, 2014);
        Dlog.d("processDayTitle() day title:" + test);
        boolean isAll = false;
        int nX = 0;
        int nY = 0;
        String strNX;
        String strNY;

//		Const_VALUE.SNAPS_TYPEFACE_DAY_TITLE = dayTitleFontFace;

        _textListFont.put("day_title", dayTitleFontFace);

        if (dayTitle2 != null && dayTitle == null) {
            dayTitle = dayTitle2;
        } else if (dayTitle2 != null && dayTitle != null) {
            isAll = true;
        }

        if (!isAll) {
            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

            textControl.text = dayTitle;

            nX = nStartX + nCellWidth * i + nDayTitleOffsetX + nCellWidth / 3;

            nY = nStartY + nCellHeight * j + nDayTitleOffsetY;

            String width = "";
            String oldHeight = dayTitleFontSize;
            if (dayTitleFontAlign.compareTo("center") != 0)
                width = String.format("%d", 2 * nCellWidth / 3);
            else {
                width = String.format("%d", nCellWidth);
                nX = nStartX + nCellWidth * i;// + nDayTitleOffsetX ;
                if (cellType.compareTo("1") == 0) {
                    int nHeight = (int) Float.parseFloat(dayTitleFontSize);
                    oldHeight = String.format("%d", nCellHeight / 4);
                    nY = nStartY + nCellHeight * j + (3 * nCellHeight) / 4 + nDayTitleOffsetY;
                } else
                    nY = nStartY + nCellHeight * j + nDayTitleOffsetY;

            }
            strNX = String.format("%d", nX);
            strNY = String.format("%d", nY);

            textControl.setX(strNX);
            textControl.y = strNY;
            textControl.width = width;// getValue(attributes, "width");
            textControl.height = dayTitleFontSize;// getValue(attributes, "height");
            textControl.rowCount = rowCount;
            textControl.format.fontFace = FontUtil.getFontFaceByChannel(dayTitleFontFace);
            textControl.format.fontSize = dayTitleFontSize;
            textControl.format.align = dayTitleFontAlign;
            //boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);

            textControl.format.fontColor = dayTitleFontColor;

            textControl.type = "calendar";
            textControl.controType = "day_title";
            textControl.format.fontColor = dayTitleFontColor;

            textControl.format.baseFontColor = textControl.format.fontColor;

            textControl.setPageIndex(template.getPages().size());
            page.addControl(textControl);
        } else {

            if (dayTitleFontAlign.compareTo("center") != 0 || cellType.compareTo("0") == 0) {
                textControl = new SnapsTextControl();
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = dayTitle;

                nX = nStartX + nCellWidth * i + nDayTitleOffsetX + nCellWidth / 3;

                nY = nStartY + nCellHeight * j + nDayTitleOffsetY;

                String width = String.format("%d", 2 * nCellWidth / 3);
                width = String.format("%d", 2 * nCellWidth / 3);
                strNX = String.format("%d", nX);
                strNY = String.format("%d", nY);

                textControl.setX(strNX);
                textControl.y = strNY;

                textControl.width = width;// getValue(attributes, "width");
                textControl.height = dayTitleFontSize;// getValue(attributes, "height");
                textControl.rowCount = rowCount;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(dayTitleFontFace);
                textControl.format.fontSize = dayTitleFontSize;
                textControl.format.align = dayTitleFontAlign;
                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);

                textControl.format.fontColor = dayTitleFontColor;

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.type = "calendar";
                textControl.controType = "day_title";

                textControl.setPageIndex(template.getPages().size());
                page.addControl(textControl);

                // /
                textControl = new SnapsTextControl();
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = dayTitle2;

                int nFontSize = (int) Float.parseFloat(dayTitleFontSize);

                nX = nStartX + nCellWidth * i + nDayTitleOffsetX + nCellWidth / 3;

                nY = nStartY + nCellHeight * j + nDayTitleOffsetY + 2 * nFontSize;

                strNX = String.format("%d", nX);
                strNY = String.format("%d", nY);

                textControl.setX(strNX);
                textControl.y = strNY;
                width = String.format("%d", 2 * nCellWidth / 3);
                strNX = String.format("%d", nX);

                textControl.width = width;// getValue(attributes, "width");
                textControl.height = dayTitleFontSize;// getValue(attributes, "height");
                textControl.rowCount = rowCount;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(dayTitleFontFace);
                textControl.format.fontSize = dayTitleFontSize;
                textControl.format.align = dayTitleFontAlign;

                textControl.format.fontColor = dayTitleFontColor;

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.controType = "day_title";

                textControl.type = "calendar";

                textControl.setPageIndex(template.getPages().size());
                page.addControl(textControl);
            } else {
                textControl = new SnapsTextControl();
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = String.format("%s/%s", dayTitle, dayTitle2);

                String width = String.format("%d", nCellWidth);
                nX = nStartX + nCellWidth * i;// + nDayTitleOffsetX ;
                String oldHeight;
                int nHeight = (int) Float.parseFloat(dayTitleFontSize);
                oldHeight = String.format("%d", nCellHeight / 4);

                nY = nStartY + nCellHeight * j + (3 * nCellHeight) / 4 + nDayTitleOffsetY;

                strNX = String.format("%d", nX);
                strNY = String.format("%d", nY);

                textControl.setX(strNX);
                textControl.y = strNY;
                textControl.width = width;// getValue(attributes, "width");
                textControl.height = dayTitleFontSize;// getValue(attributes, "height");
                textControl.rowCount = rowCount;
                textControl.format.fontColor = dayTitleFontColor;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(dayTitleFontFace);
                textControl.format.fontSize = dayTitleFontSize;
                textControl.format.align = dayTitleFontAlign;
                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);

                textControl.format.fontColor = dayTitleFontColor;

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.type = "calendar";
                textControl.controType = "day_title";

                textControl.setPageIndex(template.getPages().size());
                page.addControl(textControl);

            }

        }

    }

    private void processFrontCalendar(int nStartX, int nStartY, int nCellWidth, int nCellHeight, int nDayOffsetX, int nDayOffsetY) {
        int nMax = 0;
        int nX = 0;
        int nY = 0;

        _textListFont.put("day_front", dayFontFace);

        for (int j = 0; j < 2; j++) {
            nMax = GetParsedXml.getMaximumDay(nStartMonth, nStartYear);

            for (int _i = 0; _i < nMax; _i++) {
                //
                nX = nStartX + nCellWidth * _i + nDayOffsetX;
                nY = nStartY + nCellHeight * j + nDayOffsetY;
                String _label = "";

                if (j == 0) {
                    String tmp[] = {"S", "M", "T", "W", "T", "F", "S"};
                    Calendar cal = Calendar.getInstance();

                    cal.set(nStartYear, nStartMonth - 1, _i + 1);
                    int index = cal.get(Calendar.DAY_OF_WEEK);

                    _label = tmp[index - 1];

                } else
                    _label = String.format("%d", _i + 1);

                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, _i + 1, nStartYear);

                textControl = new SnapsTextControl();
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = _label;

                String strNX = String.format("%d", nX);
                String strNY = String.format("%d", nY);

                String cellWidth = String.format("%d", nCellWidth);
                String cellHeight = String.format("%d", nCellHeight);
                Dlog.d("processFrontCalendar() strNX:" + strNX + ", strNY:" + strNY
                        + ", cellWidth:" + cellWidth + ", cellHeight:" + cellHeight
                        + ", dayFontSize:" + dayFontSize + ". dayFontFace:" + dayFontFace);

                textControl.setX(strNX);
                textControl.y = strNY;
                textControl.width = cellWidth;// getValue(attributes, "width");
                textControl.height = cellHeight;// getValue(attributes, "height");
                textControl.rowCount = rowCount;
                textControl.format.fontFace = FontUtil.getFontFaceByChannel(dayFontFace);
                textControl.format.fontSize = dayFontSize;
                textControl.format.align = "center";

                if (!isHoliday)
                    textControl.format.fontColor = dayFontColor;
                else
                    textControl.format.fontColor = "ff0000";

                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.type = "calendar";

                textControl.setPageIndex(template.getPages().size());
                page.addControl(textControl);

            }
        }
    }

    /***
     * 카카오스토리북 예외처리.
     *
     * @param control
     */
    void checkKVertical(SnapsTextControl control) {
        if (textControl.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL) {
            textControl.setX(String.valueOf(textControl.getIntX() - 12/*9*/));
        }
    }

}
