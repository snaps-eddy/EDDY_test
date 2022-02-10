package com.snaps.common.data.parser;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

import com.snaps.common.structure.SnapsProductOption;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplatePrice;
import com.snaps.common.structure.calendar.ISnapsCalendarConstants;
import com.snaps.common.structure.calendar.SnapsCalendarCell;
import com.snaps.common.structure.calendar.SnapsCalendarTemplateInfo;
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
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageUtil;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.ui.BRect;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.autosave.AutoSaveManager;
import com.snaps.mobile.utils.ui.RotateUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Calendar;

public class GetTemplateXMLHandler extends DefaultHandler {
    private static final String TAG = GetTemplateXMLHandler.class.getSimpleName();
    public StringBuffer characterText = null;

    protected final String TAG_BASKET = "basket";
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
    protected final String TAG_PRODUCT_OPTION = "ProductOption";
    protected final String TAG_FRAMEINFO = "FRAMEINFO";
    protected final String TAG_RECOMMEND_INFO = "RECOMMEND_INFO";

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

    // 추천AI 커버 이미지 정보
    protected final String TAG_ANALYSISINFO = "AnalysisInfo";

    protected final String TAG_HOLE = "hole";   //아크릴 키링 상품의 키홀
    protected final String TAG_HELPER = "helper";    //아크릴 스탠드의 지지대
    protected final String TAG_STICK = "stick";    //아크릴 스탠드의 고정대
    protected final String TAG_SCENE_MASK = "sceneMask";
    protected final String TAG_SCENE_CUT = "sceneCut";

    protected SnapsTemplate template = null;
    protected SnapsTemplatePrice price = null;
    protected SnapsProductOption productOption = null;
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
    protected SnapsSceneMaskControl sceneMask = null;
    protected SnapsSceneCutControl sceneCut = null;
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
    private String fontBold = "";

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
    private String monthFontBold = "";

    private String monthTitleX = "";
    private String monthTitleY = "";
    private String monthTitleWidth = "";
    private String monthTitleHeight = "";
    private String monthTitleFontFace = "";
    private String monthTitleFontColor = "";
    private String monthTitleFontSize = "";
    private String monthTitleFontAlign = "";
    private String monthTitleFontBold = "";

    private boolean isExistMonthRegist = false;
    private boolean isExistYearRegist = false;


    private String yearX = "";
    private String yearY = "";

    private String yearWidth = "";
    private String yearHeight = "";

    private String yearFontFace = "";
    private String yearFontColor = "";
    private String yearFontSize = "";
    private String yearFontAlign = "";
    private String yearFontBold = "";

    private String dayFontFace = "";
    private String dayFontColor = "";
    private String dayFontSize = "";
    private String dayFontAlign = "left";
    private String dayFontBold = "";

    private String dayTitleFontFace = "";
    private String dayTitleFontColor = "";
    private String dayTitleFontSize = "";
    private String dayTitleFontAlign = "";
    private String dayTitleFontBold = "";

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

    public static ArrayList<SnapsLayoutControl> summaryLayer = null;

    public static String summaryTaget = "";

    public static String summaryWidth = "";
    public static String summaryHeight = "";
    private String attrName = "";

    public interface XMLHandler {
        public void loadComplete();
    }

    public static int getNStartYear() {
        return nStartYear;
    }

    public static int getNStartMonth() {
        return nStartMonth;
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

    public static int getExpectEndYear() {
        return getStartMonth() == 1 ? nOldYear : nOldYear + 1;
    }

    public static int getExpectEndMonth() {
        int result = getStartMonth() + 11;
        return result > 12 ? result - 12 : result;
    }

    public static String getPeriod(Context context) {
        if (context == null) return "";
        return String.format(context.getString(R.string.calendar_period_for_save_dialog), getStartYear(), getStartMonth(), getExpectEndYear(), getExpectEndMonth());
    }

    public static ArrayList<SnapsLayoutControl> getSummaryLayer() {
        return summaryLayer;
    }

//	public static HashMap<String, String> getTextListFontName() {
//		return _textListFont;
//	}

    public static void setStartYear(int startYear) {
        nStartYear = startYear;
        nOldYear = startYear;
    }

    public static void setStartMonth(int startMonth) {
        nStartMonth = startMonth;
        nOldMonth = startMonth;
    }

    /**
     * XML Tempate data
     *
     * @return SnapsTemplate
     */
    public SnapsTemplate getTemplate() {
        return template;
    }

    @Override
    public void startDocument() throws SAXException {
        template = new SnapsTemplate();

        FontUtil fontUtil = FontUtil.getInstance();
        fontUtil.initTextListFont();

        boolean isNeedCreateInstance = true;
        AutoSaveManager saveMan = AutoSaveManager.getInstance();
        if (saveMan != null && saveMan.isRecoveryMode()) {
            if (summaryLayer != null && !summaryLayer.isEmpty())
                isNeedCreateInstance = false;
        }

        if (isNeedCreateInstance)
            summaryLayer = new ArrayList<SnapsLayoutControl>();
    }

    @Override
    public void endDocument() throws SAXException {
        if (template.info.F_COVER_TYPE != null && template.info.F_COVER_TYPE.equalsIgnoreCase("soft")) {
            template.info.SOFT_COVER_PXFORMM = Double.parseDouble(template.info.F_COVER_XML_WIDTH) / Double.parseDouble(template.info.F_COVER_MM_WIDTH);
        }
        template.initDynamicTemplate();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        characterText = new StringBuffer();
        element = localName;

        if (localName.equalsIgnoreCase(TAG_TEMPLATE)) {
            template.type = getValue(attributes, "type");
        } else if (localName.equalsIgnoreCase(TAG_SCENE)) {
//			preStr = getValue(attributes, "type");
            attrName = getValue(attributes, "name");
//			if (preStr.compareTo("hidden") == 0) {
            if (attrName.compareTo("root") == 0) {
                if (Config.isCalendar()) {
                    isHidden = true;
                    summaryWidth = getValue(attributes, "width");
                    summaryHeight = getValue(attributes, "height");
                }
            }

            page = new SnapsPage(template.getPages().size(), template.info);

            page.setPageLayoutIDX(page.getPageID());
            page.type = getValue(attributes, "type");
            page.setWidth(getValue(attributes, "width"));
            page.height = getValue(attributes, "height");
            page.border = getValue(attributes, "border");
            page.layout = getValue(attributes, "layout");
            page.background = getValue(attributes, "background");

            page.side = getValue(attributes, "side");
            page.multiformId = getValue(attributes, "multiform");
            page.orgMultiformId = page.multiformId;
            page.templateCode = getValue(attributes, "templateCode");
            page.subType = getValue(attributes, "sub_type");

            if (page.type.equalsIgnoreCase("cover")) {
                cover_width = page.getWidth();
                cover_height = Integer.parseInt(page.height);
            }

        } else if (localName.equalsIgnoreCase(TAG_TEXT)) {
            layoutState = TAG_TEXT;
            state = TAG_TEXT;
            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;

            textControl.setX(getValue(attributes, "x"));
            textControl.y = getValue(attributes, "y");
            textControl.width = getValue(attributes, "width");
            textControl.height = getValue(attributes, "height");
            textControl.textDrawableWidth = getValue(attributes, "textDrawableWidth");
            textControl.textDrawableHeight = getValue(attributes, "textDrawableHeight");
            textControl.isClick = getValue(attributes, "isEditable");
        } else if (localName.equalsIgnoreCase(TAG_IMAGE)) {
            layoutState = TAG_IMAGE;

            if (Config.isCalendar()) {
//				if (preStr.compareTo("hidden") == 0) {
                if (attrName.compareTo("root") == 0) {
                    layout = new SnapsLayoutControl();
                    layout.setX(getValue(attributes, "x"));
                    layout.y = getValue(attributes, "y");
                    layout.width = getValue(attributes, "w");
                    layout.height = getValue(attributes, "h");
                    layout.isImageFull = "true".equalsIgnoreCase(getValue(attributes, "imagefull"));

                    if (layout.width.isEmpty())
                        layout.width = getValue(attributes, "width");
                    if (layout.height.isEmpty())
                        layout.height = getValue(attributes, "height");

                    layout.angle = getValue(attributes, "angle", "0");

                    String indexStr = getValue(attributes, "cal_idx");

                    if (!indexStr.isEmpty()) {
                        int index = Integer.parseInt(indexStr);
                        // cal_idx
                        layout.angleClip = getValue(attributes, "angleClip", "0");
                        summaryLayer.add(layout);
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
                layout.angleClip = getValue(attributes, "angleClip", "0");
                layout.name = getValue(attributes, "name");

                if (!layout.angleClip.equals("0") && !layout.angleClip.equals("")) {
                    convertClipRect(layout, layout.angleClip);
                }

                String szKakaoCover = getValue(attributes, "isNewKakaoCover");
                layout.isSnsBookCover = szKakaoCover != null && szKakaoCover.equalsIgnoreCase("true");

                layout.border = getValue(attributes, "border");
                layout.isClick = getValue(attributes, "isEditable");
                // 사진틀..
                layout.bordersinglecolortype = getValue(attributes, "bordersinglecolortype");
                layout.bordersinglealpha = getValue(attributes, "bordersinglealpha");
                layout.bordersinglethick = getValue(attributes, "bordersinglethick");
                layout.bordersinglecolor = getValue(attributes, "bordersinglecolor");

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
                // BackGround 이미지
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
            } else if (state.equalsIgnoreCase(TAG_FORM_STYLE)) {

                isExistMonthRegist = false;

                isExistYearRegist = false;
            }
        } else if (localName.equalsIgnoreCase(TAG_STYLE)) {

            if (state.equalsIgnoreCase(TAG_FORM_STYLE)) {
                fontFace = getValue(attributes, "font_face");
                Dlog.d("startElement() font_face:" + fontFace);
                template.fonts.add(fontFace);
                Const_VALUE.SNAPS_TYPEFACE_NAME = fontFace;
                fontSize = getValue(attributes, "font_size");

                fontColor = getValue(attributes, "font_color");
                //Ben 땜방
                //"font_color"값이 6자리가 아니고 4자리인 경우가 있다. 이 경우 앞에 00을 붙인다.
                if (fontColor != null && fontColor.startsWith("#") == false && fontColor.length() == 4) {
                    Dlog.w(TAG, "startElement() fontcolor:" + fontColor + " -> " + "00" + fontColor);
                    fontColor = "00" + fontColor;
                }

                fontAlign = getValue(attributes, "align");
                fontBold = getValue(attributes, "bold");
            } else {

                textControl.format.fontFace = getValue(attributes, "font_face");
                Dlog.d("startElement() textControl.format.fontFace:" + textControl.format.fontFace);
                template.fonts.add(textControl.format.fontFace);
                Const_VALUE.SNAPS_TYPEFACE_NAME = textControl.format.fontFace;
                textControl.format.alterFontFace = getValue(attributes, "alter_font_face");
                textControl.format.fontSize = getValue(attributes, "font_size");
                textControl.format.fontColor = getValue(attributes, "font_color");
                textControl.format.baseFontColor = textControl.format.fontColor;
                textControl.format.align = getValue(attributes, "align");
                textControl.format.bold = getValue(attributes, "bold");
                textControl.format.italic = getValue(attributes, "italic");
                textControl.format.underline = getValue(attributes, "underline");
                textControl.spacing = getValue(attributes, "spacing");
                textControl.format.verticalView = getValue(attributes, "vertical_view");
                textControl.albumMode = getValue(attributes, "album_mode");

            }

        } else if (localName.equalsIgnoreCase(TAG_REGIST)) {
            if (state.equalsIgnoreCase(TAG_BACKGROUND_LAYER)) {
                bg.regName = getValue(attributes, "name");
                bg.regValue = getValue(attributes, "value");
            } else if (state.equalsIgnoreCase(TAG_FORM_LAYER)) {
                form.regName = getValue(attributes, "name");
                form.regValue = getValue(attributes, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_TEXT)) {
                textControl.regName = getValue(attributes, "name");
                textControl.regValue = getValue(attributes, "value");
            } else if (layoutState.equalsIgnoreCase(TAG_IMAGE)) {
                layout.regName = getValue(attributes, "name");
                layout.regValue = getValue(attributes, "value");
                layout.analysisImageKey = getValue(attributes, "analysisImageKey");
            } else if (layoutState.equalsIgnoreCase(TAG_FORM_STYLE)) {
                String name = getValue(attributes, "name");
                assignCalendarFont(name);
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
                page.orgBgId = bg.srcTarget;
                bg.resourceURL = getValue(attributes, "resourceURL");
                bg.getVersion = getValue(attributes, "getVersion");

                if (Config.isCalendar()) {
//					if (preStr.compareTo("hidden") == 0) {
                    if (attrName.compareTo("root") == 0) {

                        summaryTaget = getValue(attributes, "target");

                        attrName = "";
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
                Dlog.d("startElement() form.resourceURL:" + form.resourceURL);
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
        } else if (localName.equalsIgnoreCase(TAG_CALENDAR_GRID)) {
            if (page.type.equalsIgnoreCase("cover"))
                return;

            layoutState = TAG_FORM_STYLE;

            String dayOffset = getValue(attributes, "day_offest");
            String dayOffsets[] = dayOffset.split(" ");
            dayOffsetX = dayOffsets[0];
            dayOffsetY = dayOffsets[1];

            String dayTitleOffset = getValue(attributes, "dayTitle_offest");
            String dayTitleOffsets[] = dayTitleOffset.split(" ");
            dayTitleOffsetX = dayTitleOffsets[0];
            dayTitleOffsetY = dayTitleOffsets[1];

            cellType = getValue(attributes, "cellType");
            calWidth = getValue(attributes, "width");
            calHeight = getValue(attributes, "height");
            rowCount = getValue(attributes, "rowCount");

            calX = getValue(attributes, "x");
            calY = getValue(attributes, "y");

        } else if (localName.equalsIgnoreCase(TAG_FORMSTYLE)) {

            layoutState = TAG_FORM_STYLE;

            titleX = getValue(attributes, "x");
            titleY = getValue(attributes, "y");
            titleWidth = getValue(attributes, "width");
            titleHeight = getValue(attributes, "height");

        } else if (localName.equalsIgnoreCase(TAG_TMPLINFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_FRAMEINFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_TMPLPRICE)) {
            state = localName;
            price = new SnapsTemplatePrice();
        } else if (localName.equalsIgnoreCase(TAG_PRODUCT_OPTION)) {
            state = localName;
            productOption = new SnapsProductOption();
        } else if (localName.equalsIgnoreCase(TAG_RECOMMEND_INFO)) {
            state = localName;
        } else if (localName.equalsIgnoreCase(TAG_TEXTLIST)) {
            /*
             * textControl._controlType = SnapsControl.CONTROLTYPE_TEXT; textControl.setX(getValue(attributes, "x")); textControl.y = getValue(attributes, "y"); textControl.width =
             * getValue(attributes, "width"); textControl.height = getValue(attributes, "height");
             *
             * textControl.isClick = getValue(attributes, "isEditable");
             *
             * textControl.format.fontFace = getValue(attributes, "font_face"); textControl.format.alterFontFace = getValue(attributes, "alter_font_face"); textControl.format.fontSize =
             * getValue(attributes, "font_size"); textControl.format.fontColor = getValue(attributes, "font_color"); textControl.format.align = getValue(attributes, "align"); textControl.format.bold =
             * getValue(attributes, "bold"); textControl.format.italic = getValue(attributes, "italic"); textControl.format.verticalView = getValue(attributes, "vertical_view"); textControl.albumMode
             * = getValue(attributes, "album_mode");
             */
            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;
            String[] temp = getValue(attributes, "rc").replace(" ", "|").split("\\|");

            textControl.format.verticalView = getValue(attributes, "vertical");

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

            // 책등인 경우
            if (textControl.format.verticalView.equals("true")) {
                textControl.width = String.valueOf((int) (ww - xx));
                textControl.height = String.valueOf((int) (hh - yy));
            } else {
                textControl.width = String.valueOf(ww);
                textControl.height = String.valueOf(hh);
            }

            textControl.format.fontFace = getValue(attributes, "fontFace");
            template.fonts.add(textControl.format.fontFace);
            textControl.format.alterFontFace = "";
            textControl.format.fontSize = getValue(attributes, "fontSize");
            if (textControl.format.fontSize.equals("0"))
                textControl.format.fontSize = SnapsDiaryDataManager.isAliveSnapsDiaryService() ? "7" : "12";

            String color = getValue(attributes, "color");

            if (color.equals("null") || color.equals(""))
                textControl.format.fontColor = "000000";
            else
                textControl.format.fontColor = color.replace("#", "");

            textControl.format.baseFontColor = textControl.format.fontColor;

            textAlign = getValue(attributes, "align");
            textControl.format.align = textAlign;
            textControl.format.bold = getValue(attributes, "bold");
            textControl.format.italic = getValue(attributes, "italic");
            textControl.format.underline = getValue(attributes, "underline");
            textControl.angle = getValue(attributes, "angle", "0");
            textControl.format.setOverPrint(getValue(attributes, "overPrint"));

        } else if (localName.equalsIgnoreCase(TAG_HTMLTEXT)) {
            state = TAG_HTMLTEXT;

        } else if (localName.equalsIgnoreCase(TAG_TEXTFLOW)) {
            if (textControl != null) {
                textControl.format.fontFace = getValue(attributes, "fontFamily");
                textControl.format.fontSize = getValue(attributes, "fontSize");
            }

        }// 스티커...
        else if (localName.equalsIgnoreCase(TAG_CLIPART)) {
            clipart = new SnapsClipartControl();
            clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;

            String[] temp = getValue(attributes, "rc").replace(" ", "|").split("\\|");
            int left = (int) Float.parseFloat(temp[0]);
            int top = (int) Float.parseFloat(temp[1]);
            int right = (int) Float.parseFloat(temp[2]);
            int bottom = (int) Float.parseFloat(temp[3]);

            clipart.angle = getValue(attributes, "angle", "0");
            clipart.alpha = getValue(attributes, "alpha");

            clipart.setX(String.valueOf(left));
            clipart.y = String.valueOf(top);
            clipart.width = String.valueOf((int) (right - left));
            clipart.height = String.valueOf((int) (bottom - top));
            clipart.resourceURL = getValue(attributes, "resourceURL");
            if (clipart.resourceURL.equals(""))
                clipart.resourceURL = getValue(attributes, "uploadPath");
            clipart.clipart_id = getValue(attributes, "id");
            clipart.setOverPrint(getValue(attributes, "overPrint"));
            // 회전이 있는경우 스티커 중심회전 좌표로 변경을 해야한다..
            if (!clipart.angle.equals("0")) {
                convertClipRect(clipart, clipart.angle);
            }

            Dlog.d("startElement() clipart.alpha:" + clipart.alpha);
            page.addControl(clipart);

        } else if (localName.equalsIgnoreCase(TAG_P)) {

        } else if (localName.equalsIgnoreCase(TAG_SPAN)) {
            state = TAG_SPAN;
            String fSize = getValue(attributes, "fontSize");
            String fFamily = getValue(attributes, "fontFamily");
            String fColor = getValue(attributes, "color");

            textControl.format.fontFace = fFamily;
            textControl.format.fontSize = fSize;

            String color = fColor;

            if (color.equals("null") || color.equals(""))
                textControl.format.fontColor = "000000";
            else
                textControl.format.fontColor = color.replace("#", "");

            textControl.format.baseFontColor = textControl.format.fontColor;
        } else if (localName.equalsIgnoreCase(TAG_ANALYSISINFO)) {
            state = TAG_ANALYSISINFO;
        } else if (localName.equalsIgnoreCase(TAG_HOLE)) {
            state = TAG_HOLE;
            SnapsHoleControl snapsHoleControl = new SnapsHoleControl();
            snapsHoleControl.setX(getValue(attributes, "x"));
            snapsHoleControl.y = getValue(attributes, "y");
            snapsHoleControl.width = getValue(attributes, "width");
            snapsHoleControl.height = getValue(attributes, "height");
            snapsHoleControl.angle = getValue(attributes, "angle", "0");
            snapsHoleControl.angleClip = getValue(attributes, "angleClip", "0");
            snapsHoleControl.setSnsproperty(TAG_HOLE);   //SNS 아닌데...
            snapsHoleControl._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            page.addControl(snapsHoleControl);
        } else if (localName.equalsIgnoreCase(TAG_HELPER)) {
            state = TAG_HELPER;
            SnapsHelperControl snapsHelperControl = new SnapsHelperControl();
            snapsHelperControl.setX(getValue(attributes, "x"));
            snapsHelperControl.y = getValue(attributes, "y");
            snapsHelperControl.width = getValue(attributes, "width");
            snapsHelperControl.height = getValue(attributes, "height");
            snapsHelperControl.angle = getValue(attributes, "angle", "0");
            snapsHelperControl.angleClip = getValue(attributes, "angleClip", "0");
            snapsHelperControl.setSnsproperty(TAG_HELPER);   //SNS 아닌데...
            snapsHelperControl._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            page.addControl(snapsHelperControl);
        } else if (localName.equalsIgnoreCase(TAG_STICK)) {
            state = TAG_STICK;
            SnapsStickControl snapsStickControl = new SnapsStickControl();
            snapsStickControl.setX(getValue(attributes, "x"));
            snapsStickControl.y = getValue(attributes, "y");
            snapsStickControl.width = getValue(attributes, "width");
            snapsStickControl.height = getValue(attributes, "height");
            snapsStickControl.angle = getValue(attributes, "angle", "0");
            snapsStickControl.angleClip = getValue(attributes, "angleClip", "0");
            snapsStickControl.setSnsproperty(TAG_STICK);   //SNS 아닌데...
            snapsStickControl._controlType = SnapsControl.CONTROLTYPE_MOVABLE;
            page.addControl(snapsStickControl);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_MASK)) {
            state = TAG_SCENE_MASK;
            SnapsSceneMaskControl control = new SnapsSceneMaskControl();
            control.setX(getValue(attributes, "x"));
            control.y = getValue(attributes, "y");
            control.width = getValue(attributes, "width");
            control.height = getValue(attributes, "height");
            control.angle = getValue(attributes, "angle", "0");
            control.alpha = getValue(attributes, "alpha");
            control.resourceURL = getValue(attributes, "resourceURL", "");
            control.setId(getValue(attributes, "id"));
            control.setSnsproperty(TAG_SCENE_MASK);
            control._controlType = SnapsControl.CONTROLTYPE_LOCKED;
            page.addControl(control);
        } else if (localName.equalsIgnoreCase(TAG_SCENE_CUT)) {
            state = TAG_SCENE_CUT;
            SnapsSceneCutControl control = new SnapsSceneCutControl();
            control.setX(getValue(attributes, "x"));
            control.y = getValue(attributes, "y");
            control.width = getValue(attributes, "width");
            control.height = getValue(attributes, "height");
            control.resourceURL = getValue(attributes, "resourceURL", "");
            control.setId(getValue(attributes, "id"));
            control.setSnsproperty(TAG_SCENE_CUT);
            control._controlType = SnapsControl.CONTROLTYPE_LOCKED;
            page.addControl(control);
        }
    }

    public String getValue(Attributes target, String name) {
        String value = target.getValue(name);
        return (value == null) ? "" : value;
    }

    public int getValueInt(Attributes target, String name) {
        String value = target.getValue(name);
        return (value == null) ? 0 : Integer.parseInt(value);
    }

    public float getValueFloat(Attributes target, String name) {
        String value = target.getValue(name);
        return (value == null) ? 0f : Float.parseFloat(value);
    }

    public String getValue(Attributes target, String name, String initValue) {
        String value = target.getValue(name);
        return (value == null || value.length() == 0) ? initValue : value;
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

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (localName.equalsIgnoreCase(TAG_SCENE)) {
            if (Config.isCalendar()) {
                if (!isHidden && page.subType.compareTo("schedule_memo") != 0)
                    template.getOriginalPages().add(page);

            } else
                template.getOriginalPages().add(page);

            // 카드 제품 추가하다 보니..텍스트가 계속 들어가서 추가 함.
            textControl = null;

        } else if (localName.equalsIgnoreCase(TAG_TEXT)) {
            // 페이지 번호도 저장한다 커버는 0번이 되겠다.
            textControl.setPageIndex(template.getPages().size());
            addTextControl(page, textControl);
        } else if (localName.equalsIgnoreCase(TAG_TEXTLIST)) {
            if (Config.isCalendar()) {
                extractTextListForCalendarProduct();
            } else {
                if (SnapsTextToImageUtil.isSupportEditTextProduct()) {
                    if (Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct() ||
                            Const_PRODUCT.isNewPolaroidPackProduct() || Const_PRODUCT.isSealStickerProduct()) {
                        //ben : 전체 상품에 적용하면 문제가 될수 있으므로 일단 새로운 폰케이스에만 적용
                        //2021년 9월 27일 : 씰 스티커 상품도 추가.. 이거 아트 클라우드에서 만든 상품은 다 이런거 같음
                        extractTextList2();
                    } else {
                        extractTextList();
                    }
                } else if (Config.isPhotobooks()) {
                    String fontFace = textControl.format.fontFace;
                    textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFace);
                    textControl.setPageIndex(template.getPages().size());
                    addTextControl(page, textControl);
                    textControl = null;
                } else {
                    textControl.setPageIndex(template.getPages().size());
                    addTextControl(page, textControl);
                    textControl = null;
                }

            }
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
                if (Config.isCalendar()) {
                    initCalendarControl();
                } else {
                    if (textControl != null) {
                        addTextControl(page, textControl);
                    }
                }
            } else {
                // browse 이미지.
            }
        } else if (localName.equalsIgnoreCase(TAG_TMPLPRICE)) {
            template.priceList.add(price);
        } else if (state != null && state.equalsIgnoreCase(TAG_RECOMMEND_INFO)) {
            if ("F_BOOK_TITLE".equalsIgnoreCase(localName)) {
                String recommendTitle = characterText.toString();
                if (StringUtil.isEmptyAfterTrim(Config.getPROJ_NAME())
                        && !StringUtil.isEmptyAfterTrim(recommendTitle)) {
                    Config.setPROJ_NAME(recommendTitle);
                }
            }
        } else if (state != null && state.equalsIgnoreCase(TAG_TMPLINFO)) {
            if ("F_PROD_CODE".equalsIgnoreCase(localName)) {
                template.info.F_PROD_CODE = characterText.toString().trim();
            } else if ("F_PROD_NAME".equalsIgnoreCase(localName)) {
                template.info.F_PROD_NAME = characterText.toString().trim();
            } else if ("F_PROD_SIZE".equalsIgnoreCase(localName)) {
                template.info.F_PROD_SIZE = characterText.toString().trim();
            } else if ("F_PROD_NICK_NAME".equalsIgnoreCase(localName)) {
                template.info.F_PROD_NICK_NAME = characterText.toString().trim();
            } else if ("F_GLOSSY_TYPE".equalsIgnoreCase(localName)) {
                template.info.F_GLOSSY_TYPE = characterText.toString().trim();
            } else if ("F_USE_ANALECTA".equalsIgnoreCase(localName)) {
                template.info.F_USE_ANALECTA = characterText.toString().trim();
            } else if ("F_ENLARGE_IMG".equalsIgnoreCase(localName)) {
                template.info.F_ENLARGE_IMG = characterText.toString().trim();
            } else if ("FEP".equalsIgnoreCase(localName)) {
                template.info.FEP = characterText.toString().trim();
            } else if ("F_USE_WATERMARK".equalsIgnoreCase(localName)) {
                template.info.F_USE_WATERMARK = characterText.toString().trim();
            } else if ("F_COVER_VIRTUAL_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER_VIRTUAL_WIDTH = characterText.toString().trim();
            } else if ("F_COVER_VIRTUAL_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_COVER_VIRTUAL_HEIGHT = characterText.toString().trim();
            } else if ("F_COVER_EDGE_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER_EDGE_WIDTH = characterText.toString().trim();
            } else if ("F_COVER_EDGE_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_COVER_EDGE_HEIGHT = characterText.toString().trim();
            } else if ("F_BASE_QUANTITY".equalsIgnoreCase(localName)) {
                template.info.F_BASE_QUANTITY = characterText.toString().trim();
            } else if ("F_MAX_QUANTITY".equalsIgnoreCase(localName)) {
//				401p 적용 초기화할때. 값을 초기화 한다
                template.info.F_MAX_QUANTITY = characterText.toString().trim();
            } else if ("F_PRNT_TYPE".equalsIgnoreCase(localName)) {
                template.info.F_PRNT_TYPE = characterText.toString().trim();
            } else if ("F_EDIT_COVER".equalsIgnoreCase(localName)) {
                template.info.F_EDIT_COVER = characterText.toString().trim();
            } else if ("F_COVER_TYPE".equalsIgnoreCase(localName)) {
                template.info.F_COVER_TYPE = characterText.toString().trim();
            } else if ("F_COVER_MID_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER_MID_WIDTH = characterText.toString().trim();
            } else if ("F_COVER2_MID_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER2_MID_WIDTH = characterText.toString().trim();
            } else if ("F_COVER_MM_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER_MM_WIDTH = characterText.toString().trim();
            } else if ("F_COVER_MM_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_COVER_MM_HEIGHT = characterText.toString().trim();
            } else if ("F_COVER2_MM_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER2_MM_WIDTH = characterText.toString().trim();
            } else if ("F_COVER2_MM_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_COVER2_MM_HEIGHT = characterText.toString().trim();
            } else if ("F_COVER_CHANGE_QUANTITY".equalsIgnoreCase(localName)) {
                template.info.F_COVER_CHANGE_QUANTITY = characterText.toString().trim();
            } else if ("F_TITLE_MM_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_TITLE_MM_WIDTH = characterText.toString().trim();
            } else if ("F_TITLE_MM_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_TITLE_MM_HEIGHT = characterText.toString().trim();
            } else if ("F_PAGE_MM_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_PAGE_MM_WIDTH = characterText.toString().trim();
            } else if ("F_PAGE_MM_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_PAGE_MM_HEIGHT = characterText.toString().trim();
            } else if ("F_PAGE_PIXEL_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_PAGE_PIXEL_WIDTH = characterText.toString().trim();
            } else if ("F_PAGE_PIXEL_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_PAGE_PIXEL_HEIGHT = characterText.toString().trim();
            } else if ("F_CP_CODE".equalsIgnoreCase(localName)) {
                template.info.F_CP_CODE = characterText.toString().trim();
            } else if ("F_UI_COVER".equalsIgnoreCase(localName)) {
                template.info.F_UI_COVER = characterText.toString().trim();
            } else if ("F_UI_BACKGROUND".equalsIgnoreCase(localName)) {
                template.info.F_UI_BACKGROUND = characterText.toString().trim();
            } else if ("F_UI_LAYOUT".equalsIgnoreCase(localName)) {
                template.info.F_UI_LAYOUT = characterText.toString().trim();
            } else if ("F_UI_BORDER".equalsIgnoreCase(localName)) {
                template.info.F_UI_BORDER = characterText.toString().trim();
            } else if ("F_THUMBNAIL_STEP".equalsIgnoreCase(localName)) {
                template.info.F_THUMBNAIL_STEP = characterText.toString().trim();
            } else if ("F_stringChars_SIZE_BASE".equalsIgnoreCase(localName)) {
                template.info.F_TEXT_SIZE_BASE = characterText.toString().trim();
            } else if ("F_PROD_TYPE".equalsIgnoreCase(localName)) {
                template.info.F_PROD_TYPE = characterText.toString().trim();
            } else if ("F_RES_MIN".equalsIgnoreCase(localName)) {
                template.info.F_RES_MIN = characterText.toString().trim();
            } else if ("F_RES_DISABLE".equalsIgnoreCase(localName)) {
                template.info.F_RES_DISABLE = characterText.toString().trim();
            } else if ("F_PAGE_START_NUM".equalsIgnoreCase(localName)) {
                template.info.F_PAGE_START_NUM = characterText.toString().trim();
            } else if ("F_CENTER_LINE".equalsIgnoreCase(localName)) {
                template.info.F_CENTER_LINE = characterText.toString().trim();
            } else if ("F_UNITstringChars".equalsIgnoreCase(localName)) {
                template.info.F_UNITTEXT = characterText.toString().trim();
            } else if ("F_CALENDAR_BONUS_12".equalsIgnoreCase(localName)) {
                template.info.F_CALENDAR_BONUS_12 = characterText.toString().trim();
            } else if ("F_SPLIT_COVER_MIDSIZE".equalsIgnoreCase(localName)) {
                template.info.F_SPLIT_COVER_MIDSIZE = characterText.toString().trim();
            } else if ("F_ALLOW_NO_FULL_IMAGE_YORN".equalsIgnoreCase(localName)) {
                template.info.F_ALLOW_NO_FULL_IMAGE_YORN = characterText.toString().trim();
            } else if ("F_TMPL_CODE".equalsIgnoreCase(localName)) {
                template.info.F_TMPL_CODE = characterText.toString().trim();
            } else if ("F_TMPL_ID".equalsIgnoreCase(localName)) {
                template.info.F_TMPL_ID = characterText.toString().trim();
            } else if ("F_TMPL_NAME".equalsIgnoreCase(localName)) {
                template.info.F_TMPL_NAME = characterText.toString().trim();
            } else if ("F_XML_ID".equalsIgnoreCase(localName)) {
                template.info.F_XML_ID = characterText.toString().trim();
            } else if ("F_TMPL_DESC".equalsIgnoreCase(localName)) {
                template.info.F_TMPL_DESC = characterText.toString().trim();
            } else if ("F_REG_DATE".equalsIgnoreCase(localName)) {
                template.info.F_REG_DATE = characterText.toString().trim();
            } else if ("F_TMPL_TITLE".equalsIgnoreCase(localName)) {
                template.info.F_TMPL_TITLE = characterText.toString().trim();
            } else if ("F_EDIT_PLATFORM".equalsIgnoreCase(localName)) {
                template.info.F_EDIT_PLATFORM = characterText.toString().trim();
            } else if ("F_COVER_XML_WIDTH".equalsIgnoreCase(localName)) {
                template.info.F_COVER_XML_WIDTH = characterText.toString().trim();
            } else if ("F_FRAME_TYPE".equalsIgnoreCase(element)) {
                template.info.F_FRAME_TYPE = characterText.toString().trim();
            } else if ("F_PAPER_CODE".equalsIgnoreCase(element)) {
                template.info.F_PAPER_CODE = characterText.toString().trim();
            } else if ("F_COVER_XML_HEIGHT".equalsIgnoreCase(localName)) {
                template.info.F_COVER_XML_HEIGHT = characterText.toString().trim();
            } else if ("F_FRAME_ID".equalsIgnoreCase(element)) {
                template.info.F_FRAME_ID = characterText.toString().trim();
            } else if ("F_STORY_BOOK_INFO_USER_NAME".equalsIgnoreCase(element)) {
                template.info.F_SNS_BOOK_INFO_USER_NAME = characterText.toString().trim();
            } else if ("F_STORY_BOOK_INFO_PERIOD".equalsIgnoreCase(element)) {
                template.info.F_SNS_BOOK_INFO_PERIOD = characterText.toString().trim();
            } else if ("F_STORY_BOOK_INFO_THUMBNAIL".equalsIgnoreCase(element)) {
                template.info.F_SNS_BOOK_INFO_THUMBNAIL = characterText.toString().trim();
            } else if ("F_CARD_OPTION_QUANTITY".equalsIgnoreCase(element)) {
            } else if ("F_DESIGNER_ID".equalsIgnoreCase(element)) {
                Config.setDesignId(characterText.toString().trim());
            }
        } else if (state != null && state.equalsIgnoreCase(TAG_PRODUCT_OPTION)) {
            String value = characterText.toString().trim();
            if (value.length() > 0) {
                //TAG_TMPLPRICE, TAG_TMPLINFO 등과 다르게 ProductOption은 Key값들(localName)이 정의되어 있지 않다.
                //문제가 되는 상황이 localName이 "ProductOption", "Basket"와 같이 전달되고 있는데 value는 ""이다.
                //아무튼 그래서 value가 있는 경우만 처리
                template.getProductOption().set(localName, value);
                // @Marko
                // 이 characterText 값이 초기화 되지 않아서 productoption 이 중복해서 써지는 버그 수정을 위해 characterText 초기화 하는 코드 추가.
                characterText = new StringBuffer();
            }
        } else if (state != null && state.equalsIgnoreCase(TAG_TMPLPRICE)) {
            if ("F_COMP_CODE".equalsIgnoreCase(localName)) {
                price.F_COMP_CODE = characterText.toString().trim();
            } else if ("F_PROD_CODE".equalsIgnoreCase(localName)) {
                price.F_PROD_CODE = characterText.toString().trim();
            } else if ("F_TMPL_CODE".equalsIgnoreCase(localName)) {
                price.F_TMPL_CODE = characterText.toString().trim();
            } else if ("F_SELL_PRICE".equalsIgnoreCase(localName)) {
                price.F_SELL_PRICE = characterText.toString().trim();
            } else if ("F_ORG_PRICE".equalsIgnoreCase(localName)) {
                price.F_ORG_PRICE = characterText.toString().trim();
            } else if ("F_PRICE_NUM".equalsIgnoreCase(localName)) {
                price.F_PRICE_NUM = characterText.toString().trim();
            } else if ("F_PRNT_BQTY".equalsIgnoreCase(localName)) {
                price.F_PRNT_BQTY = characterText.toString().trim();
            } else if ("F_PRNT_EQTY".equalsIgnoreCase(localName)) {
                price.F_PRNT_EQTY = characterText.toString().trim();
            } else if ("F_PAGE_ADD_PRICE".equalsIgnoreCase(localName)) {
                price.F_PAGE_ADD_PRICE = characterText.toString().trim();
            } else if ("F_ORG_PAGE_ADD_PRICE".equalsIgnoreCase(localName)) {
                price.F_ORG_PAGE_ADD_PRICE = characterText.toString().trim();
            } else if ("F_DISC_RATE".equalsIgnoreCase(localName)) {
                price.F_DISC_RATE = characterText.toString().trim();
            }
        } else if (state != null && state.equalsIgnoreCase(TAG_TEXT)) {
            if ("initial_text".equalsIgnoreCase(localName)) {
                textControl.initialText = characterText.toString().trim();
            }
        } else if (localName != null && localName.equalsIgnoreCase(TAG_SPAN)) {
            Dlog.d("endElement() span:" + characterText.toString().trim());
        } else if (state != null && state.equalsIgnoreCase(TAG_FRAMEINFO)) {
            template.info.frameInfo.setData(localName, characterText.toString().trim());
        } else if (state != null && state.equalsIgnoreCase(TAG_ANALYSISINFO)) {
            if ("F_COVER_IMAGE_KEY".equalsIgnoreCase(localName)) {
                template.info.F_COVER_IMAGE_KEY = characterText.toString().trim();
            }
        }
    }

    private void extractTextListForCalendarProduct() {
        if (Config.isCalenderWall(Config.getPROD_CODE()) || Config.isCalenderSchedule(Config.getPROD_CODE()))
            extractTextListForCalendarVerticalType();
        else
            extractTextListForGeneralCalendar();
    }

    // 스케즐러와 벽걸이 달력에서만 사용을 하도록 만듬 사이드 때문
    private void extractTextListForCalendarVerticalType() {
        GetXMLDomParser domParser = new GetXMLDomParser(htmlText);
        int fontHeight = 0;
        for (SnapsTextControl textControl : domParser.getTextControl()) {
            textControl._controlType = SnapsControl.CONTROLTYPE_GRID;
            textControl.setX(textX);

            int nY = (int) Float.parseFloat(textY) + fontHeight;
            textControl.y = String.format("%d", nY);

            fontHeight += ((int) Float.parseFloat(textControl.format.fontSize) + 2);

            textControl.width = textWidth;
            textControl.height = textHeight;
            textControl.rowCount = rowCount;
            textControl.type = "calendar";
            textControl.controType = "textlist";

            Const_VALUE.SNAPS_TYPEFACE_NAME2 = textControl.format.fontFace;
            FontUtil.putUITextListFont(textControl, "textlist", textControl.format.fontFace);

            textControl.setPageIndex(template.getPages().size());
            addTextControl(page, textControl);
        }

        textList++;
        htmlText = "";
    }

    private String getElementAtt(Element ele, String attName) {
        if (ele == null) {
            return "";
        }

        if (ele.hasAttr(attName)) {
            return ele.attr(attName);
        }
        return "";
    }

    //html 구조상 자식의 속성이 먼저 적용된다.
    private String getElementAtt(Element parentEle, Element childEle, String attName) {
        String att = getElementAtt(childEle, attName);
        if (att.length() > 0) {
            return att;
        }
        return getElementAtt(parentEle, attName);
    }

    /**
     * 아래와 같이 span에 속성이 정의되지 않은 경우 TextFlow의 속성을 읽어서 설정하게 수정
     * 속성 적용의 우선 순위는 span > TextFlow
     * <htmlText>
     * <![CDATA[ <TextFlow color="#00000b" fontFamily="snaps YGO 230" fontSize="12" kerning="auto" leadingModel="auto" ligatureLevel="minimum" lineBreak="toFit" textAlign="center" trackingLeft="0" trackingRight="0" whiteSpaceCollapse="preserve" version="2.0.0" xmlns="http://ns.adobe.com/textLayout/2008"><p textAlign="center"><span color="#ffffff" fontFamily="타이포_쌍문동 B" fontLookup="embeddedCFF" fontSize="200" renderingMode="cff">MINJU</span></p></TextFlow> ]]>
     * </htmlText>
     */
    private void extractTextList2() {
        Document document = Jsoup.parse(htmlText);
        if (document.childNodeSize() == 0) return;

        Elements pElements = document.select("p");
        if (pElements == null || pElements.size() < 1) return;

        StringBuilder builder = new StringBuilder();
        for (Element pElement : pElements) {
            if (builder.length() > 0) builder.append("\n");

            Elements spanElements = pElement.select("span");
            Element spanElement = spanElements.size() > 0 ? spanElements.first() : null;
            if (spanElement == null) continue;
            int size = spanElement.childNodeSize();
            for(int i = 0; i < size; i++) {
                Node childNode = spanElement.childNode(i);
                if (childNode instanceof TextNode) {
                    TextNode textNode = (TextNode)childNode;
                    String text = textNode.text();
                    text = text.replaceAll("&lt;", "<");
                    text = text.replaceAll("&gt;", ">");
                    builder.append(text);
                } else if (childNode instanceof Element) {
                    Element element = (Element)childNode;
                    if (element.tagName().toLowerCase().equals("br")) {
                        builder.append("\n");
                    }
                }
            }
        }

        Elements textFlowElements = document.select("TextFlow");
        Element textFlowElement = textFlowElements.size() > 0 ? textFlowElements.first() : null;
        Elements spanElements = pElements.select("span");
        Element spanElement = spanElements.size() > 0 ? spanElements.first() : null;

        String fontSize = getElementAtt(textFlowElement, spanElement, "fontSize");
        String color = getElementAtt(textFlowElement, spanElement, "color");
        if (color.contains("#")) {
            fontColor = color.substring(color.indexOf("#") + 1);
        } else {
            fontColor = "000000";
        }
        String fontWeight = getElementAtt(textFlowElement, spanElement, "fontWeight");
        String fontStyle = getElementAtt(textFlowElement, spanElement, "fontStyle");
        String fontFamily = getElementAtt(textFlowElement, spanElement, "fontfamily");
        if (StringUtil.isEmpty(fontFamily)) {
            fontFamily = getElementAtt(textFlowElement, spanElement, "fontFamily"); // 대소문자 오류 땜방?
        }
        String tmpTextAlign = getElementAtt(textFlowElement, spanElement, "textAlign");
        if (tmpTextAlign.length() > 0) {
            textAlign = tmpTextAlign;
        }

        String textDecoration = getElementAtt(textFlowElement, spanElement, "textDecoration");

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

        textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFamily);
        template.fonts.add(textControl.format.fontFace + fontStyle + fontWeight);
        textControl.format.fontSize = fontSize;
        textControl.format.align = textAlign;
//		textControl.type = "calendar";
        textControl.controType = "textlist";
        textControl.format.bold = fontWeight;
        textControl.format.italic = fontStyle;
        textControl.format.underline = textDecoration;

        template.fonts.add(fontFamily);
        textControl.format.fontColor = fontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.text = builder.toString();

        textControl.setPageIndex(template.getPages().size());
        addTextControl(page, textControl);
        textControl = null;

        textList++;

        htmlText = "";
    }

    private void extractTextList() {
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

        String fontFamily = spanElements.attr("fontfamily");
        if (StringUtil.isEmpty(fontFamily)) {
            fontFamily = spanElements.attr("fontFamily");
        }

        String textDecoration = spanElements.attr("textDecoration");

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

        textControl.format.fontFace = FontUtil.getFontFaceByChannel(fontFamily);
        template.fonts.add(textControl.format.fontFace + fontStyle + fontWeight);
        textControl.format.fontSize = fontSize;
        textControl.format.align = textAlign;
//		textControl.type = "calendar";
        textControl.controType = "textlist";
        textControl.format.bold = fontWeight;
        textControl.format.italic = fontStyle;
        textControl.format.underline = textDecoration;

        template.fonts.add(fontFamily);
        textControl.format.fontColor = fontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;

        textControl.text = builder.toString();

        textControl.setPageIndex(template.getPages().size());
        addTextControl(page, textControl);
        textControl = null;

        textList++;

        htmlText = "";
    }

    private void extractTextListForGeneralCalendar() {
        String[] arParams1 = htmlText.split("<span color=\"");
        Dlog.d("extractTextListForGeneralCalendar() stringChars:" + htmlText);
        int fontHeight = 0;

        if (page.subType != null && page.subType.compareTo("schedule_memo") == 0)
            return;
        for (int i = 0; i < arParams1.length; i++) {
            int len = arParams1.length;
            String fontFamily = "";
            String label = "";

            if (arParams1[i].startsWith("#")) {
                int index2 = arParams1[i].indexOf("\"");

                int indexP = arParams1[i].indexOf("<p");

                fontColor = arParams1[i].substring(1, index2);
                Dlog.d("extractTextListForGeneralCalendar() font color:" + fontColor);

                index2 = arParams1[i].indexOf("fontFamily=\"");

                String params2 = arParams1[i].substring(index2 + 12, arParams1[i].length());

                index2 = params2.indexOf("\"");
                if (index2 < 0)
                    continue;

                fontFamily = params2.substring(0, index2);
                Dlog.d("extractTextListForGeneralCalendar() font fontFamily:" + fontFamily);

                index2 = arParams1[i].indexOf("fontSize=\"");

                if (index2 < 0)
                    continue;

                params2 = arParams1[i].substring(index2 + 10, arParams1[i].length());

                index2 = params2.indexOf("\"");

                fontSize = params2.substring(0, index2);
                Dlog.d("extractTextListForGeneralCalendar() font size:" + fontSize);

                index2 = arParams1[i].indexOf(">");
                if (index2 < 0)
                    continue;

                params2 = arParams1[i].substring(index2, arParams1[i].length());

                index2 = params2.indexOf("<");

                if (index2 < 0)
                    continue;

                label = params2.substring(1, index2);
                Dlog.d("extractTextListForGeneralCalendar() font label:" + label);

                String text = label.replace("&amp;", "&");

                textControl = new SnapsTextControl();

                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = text;
                textControl.setX(textX);

                int nY = (int) Float.parseFloat(textY) + fontHeight;
                textControl.y = String.format("%d", nY);

                fontHeight += ((int) Float.parseFloat(fontSize) + 2);

                textControl.width = textWidth;// getValue(attributes, "width");
                textControl.height = textHeight;// getValue(attributes, "height");
                textControl.rowCount = rowCount;

                textControl.format.fontFace = fontFamily;
                textControl.format.fontSize = fontSize;
                textControl.format.align = textAlign;

                textControl.type = "calendar";
                textControl.controType = "textlist";

                Const_VALUE.SNAPS_TYPEFACE_NAME2 = fontFamily;

                FontUtil.putUITextListFont(textControl, "textlist", textControl.format.fontFace);

                textControl.format.fontColor = fontColor;
                textControl.format.baseFontColor = textControl.format.fontColor;

                textControl.setPageIndex(template.getPages().size());

                addTextControl(page, textControl);

            }

        }
        textList++;

        htmlText = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String stringChars_o = new String(ch, start, length);
        if (stringChars_o.hashCode() != 985088 && stringChars_o.hashCode() != 10 && stringChars_o.hashCode() != 1024) {

            String stringChars = stringChars_o;
            if (stringChars.equalsIgnoreCase(""))
                return;

            if (state != null) {
                if (state.equalsIgnoreCase(TAG_HTMLTEXT)) {
                    if (stringChars.length() > 0) {
                        htmlText += StringUtil.convertEmojiAliasToUniCode(stringChars);
                        return;
                    }
                }
            }

            if (characterText != null)
                characterText.append(ch, start, length);

            // 하나의 Tag에 Text값을 한번만 읽기위해서 초기화.....
        }
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
            yearFontBold = fontBold;

            isExistYearRegist = true;

        } else if (titleStyle.compareTo("month") == 0) {
            monthX = titleX;
            monthY = titleY;
            monthWidth = titleWidth;
            monthHeight = titleHeight;

            monthFontFace = fontFace;
            monthFontColor = fontColor;
            monthFontSize = fontSize;
            monthFontAlign = fontAlign;
            monthFontBold = fontBold;

            isExistMonthRegist = true;

            isExistMonthRegist = true;

        } else if (titleStyle.compareTo("month_title") == 0) {
            monthTitleX = titleX;
            monthTitleY = titleY;
            monthTitleWidth = titleWidth;
            monthTitleHeight = titleHeight;

            monthTitleFontFace = fontFace;
            monthTitleFontColor = fontColor;
            monthTitleFontSize = fontSize;
            monthTitleFontAlign = fontAlign;
            monthTitleFontBold = fontBold;

        } else if (titleStyle.compareTo("day") == 0) {
            dayFontFace = fontFace;
            dayFontColor = fontColor;
            dayFontSize = fontSize;
            dayFontAlign = fontAlign;
            dayFontBold = fontBold;

        } else if (titleStyle.compareTo("day_title") == 0) {
            dayTitleFontFace = fontFace;
            dayTitleFontColor = fontColor;
            dayTitleFontSize = fontSize;
            dayTitleFontAlign = fontAlign;
            dayTitleFontBold = fontBold;
        }
    }

    private void initCalendarControl() {
        if (rowCount.compareTo("0") == 0)
            return;

        if (page.subType != null && page.subType.compareTo("schedule_memo") == 0)
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

        float nCellWidth = 0.f;
        boolean isFrontType = false;

        if (nRowCount == 2) {
            if (cellType != null && cellType.equalsIgnoreCase("6")) { //우드블럭 달력의 경우, 예외 처리...
                nCellWidth = nWidth / (float) getMaxDayByCurrentYearMonth();
            } else {
                nCellWidth = nWidth / 31;
            }
            isFrontType = true;
        } else {
            nCellWidth = nWidth / 7;
        }

        int nCellHeight = nHeight / nRowCount;

        // scheduler인경우 front에 달력이 나오지 못하게 수정..
        if (Config.isCalenderSchedule(Config.getPROD_CODE()) && page.side.equals("front"))
            return;

        SnapsCalendarTemplateInfo calendarTemplateInfo = new SnapsCalendarTemplateInfo();
        calendarTemplateInfo.setGridWidth(nWidth);
        calendarTemplateInfo.setGridHeight(nHeight);
        calendarTemplateInfo.setStartOffset(new Point(nStartX, nStartY));
        calendarTemplateInfo.setDayOffset(new Point(nDayOffsetX, nDayOffsetY));
        calendarTemplateInfo.setDayTitleOffset(new Point(nDayTitleOffsetX, nDayTitleOffsetY));
        calendarTemplateInfo.setCellWidth(nCellWidth);
        calendarTemplateInfo.setCellHeight(nCellHeight);
        calendarTemplateInfo.setFrontType(isFrontType);
        calendarTemplateInfo.setRowCount(nRowCount);
        calendarTemplateInfo.setCellType(cellType);
        calendarTemplateInfo.setDayFontDiffY(dayFontSize);
        calendarTemplateInfo.setDayTitleFontDiffY(dayTitleFontSize);

        processYear(isFrontType);

        processMonth(isFrontType);
        if (!monthTitleFontFace.isEmpty())
            processMonthTitle(isFrontType);

        if (isFrontType)
            processFrontCalendar(calendarTemplateInfo);
        else
            processBackCalendar(calendarTemplateInfo);

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
        calendarTemplateInfo = null;
    }

    private int getMaxDayByCurrentYearMonth() {
        return GetParsedXml.getMaximumDay(nStartMonth, nStartYear);
    }

    private void processMonth(boolean isFrontType) {

        if (!isExistMonthRegist) return;

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
        textControl.format.fontFace = monthFontFace;
        textControl.format.fontSize = monthFontSize;
        textControl.format.align = monthFontAlign;
        textControl.format.bold = StringUtil.isEmpty(monthFontBold) ? "false" : monthFontBold;

        textControl.format.fontColor = monthFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;
        textControl.type = "calendar";
        textControl.controType = "month";

        if (isFrontType) {
            textControl.controType = "month_front";
            FontUtil.putUITextListFont(textControl, "month_front", textControl.format.fontFace);
        } else {
            textControl.controType = "month";
            FontUtil.putUITextListFont(textControl, "month", textControl.format.fontFace);
        }

        textControl.setPageIndex(template.getPages().size());
        addTextControl(page, textControl);
    }

    private void processMonthTitle(boolean isFrontType) {
        textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

        String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        textControl.text = months[nStartMonth - 1];// String.format("%d",nStartMonth);

        if (monthTitleY == null || monthTitleY.isEmpty())
            return;

        int nMonthTitleY = (int) Float.parseFloat(monthTitleY);
        String MonthTitleY = String.format("%d", nMonthTitleY);

        textControl.setX(monthTitleX);
        textControl.y = MonthTitleY;
        textControl.width = monthTitleWidth;
        textControl.height = monthTitleHeight;
        textControl.rowCount = rowCount;
        textControl.format.fontFace = monthTitleFontFace;
        textControl.format.fontSize = monthTitleFontSize;
        textControl.format.align = monthTitleFontAlign;
        textControl.format.bold = StringUtil.isEmpty(monthTitleFontBold) ? "false" : monthTitleFontBold;

        textControl.format.fontColor = monthTitleFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;
        textControl.type = "calendar";
        textControl.controType = "month_title";

        if (isFrontType) {
            textControl.controType = "monthtitle_front";
            FontUtil.putUITextListFont(textControl, "monthtitle_front", textControl.format.fontFace);
        } else {
            textControl.controType = "month_title";
            FontUtil.putUITextListFont(textControl, "month_title", textControl.format.fontFace);
        }

        textControl.setPageIndex(template.getPages().size());
        addTextControl(page, textControl);

    }

    private void processYear(boolean isFrontType) {
        if (!isExistYearRegist)
            return;

        textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

        textControl.text = String.format("%d", nStartYear);

        textControl.setX(yearX);
        textControl.y = yearY;
        textControl.width = yearWidth;
        textControl.height = yearHeight;
        textControl.rowCount = rowCount;
        textControl.format.fontFace = yearFontFace;
        textControl.format.fontSize = yearFontSize;
        textControl.format.align = yearFontAlign;
        textControl.format.bold = StringUtil.isEmpty(yearFontBold) ? "false" : yearFontBold;

        textControl.format.fontColor = yearFontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;
        textControl.type = "calendar";

        if (isFrontType) {
            textControl.controType = "year_front";
            FontUtil.putUITextListFont(textControl, "year_front", textControl.format.fontFace);
        } else {
            textControl.controType = "year";
            FontUtil.putUITextListFont(textControl, "year", textControl.format.fontFace);
        }

        textControl.setPageIndex(template.getPages().size());
        addTextControl(page, textControl);
    }

    private void processBackCalendar(SnapsCalendarTemplateInfo calendarTemplateInfo) {
        if (calendarTemplateInfo == null) return;

        final int COUNT_OF_WEEK = 7; //월화수목금토일..갯수
        final int MAX_DAYS = getMaxDayByCurrentYearMonth(); //몇 일까지 있는지
        final int START_DAY_OF_WEEK = GetParsedXml.getStarCalendarIndex(nStartMonth, nStartYear); //1일이 무슨 요일인지
        final int OVER_COUNT = MAX_DAYS + START_DAY_OF_WEEK - calendarTemplateInfo.getRowCount() * 7; //칸이 모자라서 그리지 못하는 day의 count

        SnapsCalendarCell dayCell = new SnapsCalendarCell();
        //dayTitle과 dayCell 기본 위치와 크기 설정
        if (calendarTemplateInfo.isTitleVerticalType()) { //title은 가로 형태, 세로 형태가 있다.
            dayCell.setWidth(calendarTemplateInfo.getCellWidth());
            dayCell.setHeight(calendarTemplateInfo.getCellHeight());
        } else {
            dayCell.setWidth(calendarTemplateInfo.getCellWidth() / 2);
            dayCell.setHeight(calendarTemplateInfo.getCellHeight() / 2);
        }

        int day = 0;
        int addRowCount = (calendarTemplateInfo.getRowCount() == 5 && OVER_COUNT > 0) ? 1 : 0; //row가 5개인데 넘어간다면 1줄 더 그린다.
        final int MAX_ROW_COUNT = calendarTemplateInfo.getRowCount() + addRowCount;

        for (int row = 0; row < MAX_ROW_COUNT; row++) {
            if (day >= MAX_DAYS) break;
            for (int column = 0; column < COUNT_OF_WEEK; column++) {
                if (row == 0 && column == 0) //1일
                    column = START_DAY_OF_WEEK;

                String label = String.valueOf(day + 1);
                String szfontSize = dayFontSize;

                int overDateX = 0;
                int overDateY = 0;
                ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE eOverRowType = ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.NONE;

                dayCell.setAlign(dayFontAlign);

                if (OVER_COUNT > 0 && row >= 4 && calendarTemplateInfo.getRowCount() == 5) { //칸이 모잘라서 추가로 그려야 하는 경우
                    if (OVER_COUNT - 1 >= column) {
                        if (calendarTemplateInfo.isTitleVerticalType()) {
                            if (row == 4) {
                                eOverRowType = ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.LEFT;
                                dayCell.setAlign(ISnapsCalendarConstants.TEXT_ALIGN_LEFT);
                            } else {
                                eOverRowType = ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.RIGHT;
                                dayCell.setAlign(ISnapsCalendarConstants.TEXT_ALIGN_RIGHT);
                            }
                        } else {
                            eOverRowType = row == 4 ? ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.UPPER : ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.LOWER;
                        }

                        int iFontSize = (int) Float.parseFloat(dayFontSize);
                        if (iFontSize > 20) {
                            szfontSize = String.valueOf((iFontSize * 0.7f));
                        }
                    }

                    switch (eOverRowType) {
                        case LOWER:
                            overDateY = -(calendarTemplateInfo.getCellHeight() / 2);
                            break;
                        case LEFT:
                            overDateX = 5;
                            overDateY = -5;
                            break;
                        case RIGHT:
                            overDateX = -5;
                            overDateY = -(calendarTemplateInfo.getCellHeight() / 2) - 5;
                            break;
                        case UPPER:
                            break;
                    }
                }

                //날짜가 그려지는 좌표
                Point ptDay = new Point((int) (calendarTemplateInfo.getStartOffset().x + (calendarTemplateInfo.getCellWidth() * column)),
                        calendarTemplateInfo.getStartOffset().y + (calendarTemplateInfo.getCellHeight() * row));

                //폰트의 상하 스페이싱 고려..
                calendarTemplateInfo.setDayFontDiffY(szfontSize);

                //template에 보정치와 겹치는 날짜의 보정치를 더해 줌.
                int fixOffsetX = calendarTemplateInfo.getDayOffset().x + overDateX;
                int fixOffsetY = (int) (calendarTemplateInfo.getDayOffset().y + overDateY - calendarTemplateInfo.getDayFontDiffY()); //diffY는 font의 보정치

                dayCell.setX(ptDay.x + fixOffsetX);
                dayCell.setY(ptDay.y + fixOffsetY);


                textControl = new SnapsTextControl();

                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;

                textControl.text = label;
                textControl.setX(String.valueOf(dayCell.getX()));
                textControl.y = String.valueOf(dayCell.getY());
                textControl.width = String.valueOf(dayCell.getWidth());//cellWidth;
                textControl.height = String.valueOf(dayCell.getHeight());//oldHeight;
                textControl.rowCount = String.valueOf(calendarTemplateInfo.getRowCount());
                textControl.format.fontFace = dayFontFace;
                textControl.format.fontSize = szfontSize;
                textControl.format.align = dayCell.getAlignStr();

                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, day + 1, nStartYear);
                textControl.format.fontColor = isHoliday ? "ff0000" : dayFontColor;
                textControl.format.baseFontColor = textControl.format.fontColor;
                textControl.type = "calendar";
                textControl.controType = "day";

                textControl.setPageIndex(template.getPages().size());

                addTextControl(page, textControl);

                FontUtil.putUITextListFont(textControl, "day", dayFontFace);

                if (eOverRowType == ISnapsCalendarConstants.CALENDAR_OVER_ROW_TYPE.NONE
                        && calendarTemplateInfo.getCellType() < 2) {
                    processDayTitle(day, ptDay, calendarTemplateInfo);
                }

                if (++day >= MAX_DAYS)
                    break;
            }
        }
    }

    private void processDayTitle(int day, Point ptDay, SnapsCalendarTemplateInfo templateInfo) {
        if (!Config.useKorean() || Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarWide(Config.getPROD_CODE()) || templateInfo == null || ptDay == null)
            return;//미니, 스몰가로, 스몰세로는 DAYTITLE이 빠진다.

        boolean isTwoLine = false; //기념일과 음력이 모두 표기되는 형태
        SnapsCalendarCell dayTitleCell = new SnapsCalendarCell();
        SnapsCalendarCell dayTitleCellSecond = null;

        //dayTitle 정보가 없으면 pass
        String dayTitle = GetParsedXml.getDayTitle(nStartMonth, day + 1, nStartYear);
        String dayTitleSecond = GetParsedXml.getDayTitle2(nStartMonth, day + 1, nStartYear);

        if (dayTitleSecond != null && dayTitle == null) {
            dayTitle = dayTitleSecond;
        } else if (dayTitleSecond != null && dayTitleSecond.length() > 0
                && dayTitle != null && dayTitle.length() > 0) {
            if (templateInfo.isTitleVerticalType()) {
                dayTitle = String.format("%s/%s", dayTitle, dayTitleSecond);
            } else {
                isTwoLine = true;
                dayTitleCellSecond = new SnapsCalendarCell();
            }
        } else if (dayTitle == null && dayTitleSecond == null) {
            return;
        }

        //dayTitle과 dayCell 기본 위치와 크기 설정
        if (templateInfo.isTitleVerticalType()) { //title은 가로 형태, 세로 형태가 있다.
            dayTitleCell.setX(0);
            dayTitleCell.setY(templateInfo.getCellHeight() / 4 * 3);
            dayTitleCell.setWidth(templateInfo.getCellWidth());
            dayTitleCell.setHeight(templateInfo.getCellHeight() / 4);

            if (dayTitleCellSecond != null) {
                dayTitleCellSecond.setX(0);
                dayTitleCellSecond.setY(0);
                dayTitleCellSecond.setWidth(templateInfo.getCellWidth());
                dayTitleCellSecond.setHeight(templateInfo.getCellHeight() / 4);
            }
        } else {
            dayTitleCell.setX((int) (templateInfo.getCellWidth() / 3));
            dayTitleCell.setY(0);
            dayTitleCell.setWidth(templateInfo.getCellWidth() * 2 / 3);
            dayTitleCell.setHeight(templateInfo.getCellHeight() / 4);

            if (dayTitleCellSecond != null) {
                dayTitleCellSecond.setX((int) (templateInfo.getCellWidth() / 3));
                dayTitleCellSecond.setY(templateInfo.getCellHeight() / 4);
                dayTitleCellSecond.setWidth(templateInfo.getCellWidth() * 2 / 3);
                dayTitleCellSecond.setHeight(templateInfo.getCellHeight() / 3);
            }
        }

        int lineCount = isTwoLine ? 2 : 1;
        for (int ii = 0; ii < lineCount; ii++) {
            String titleOffsetX = "", titleOffsetY = "", titleWidth = "", titleHeight = "", titleText = "";
            if (ii == 0) {
                titleOffsetX = String.valueOf(ptDay.x + templateInfo.getDayTitleOffset().x + dayTitleCell.getX());
                titleOffsetY = String.valueOf(ptDay.y + templateInfo.getDayTitleOffset().y + dayTitleCell.getY() - templateInfo.getDayTitleFontDiffY());
                titleWidth = String.valueOf(dayTitleCell.getWidth());
                titleHeight = String.valueOf(dayTitleCell.getHeight());
                titleText = dayTitle;
            } else {
                if (dayTitleCellSecond != null) {
                    titleOffsetX = String.valueOf(ptDay.x + templateInfo.getDayTitleOffset().x + dayTitleCellSecond.getX());
                    titleOffsetY = String.valueOf(ptDay.y + templateInfo.getDayTitleOffset().y + dayTitleCellSecond.getY() - templateInfo.getDayTitleFontDiffY());
                    titleWidth = String.valueOf(dayTitleCellSecond.getWidth());
                    titleHeight = String.valueOf(dayTitleCellSecond.getHeight());
                    titleText = dayTitleSecond;
                }
            }

            textControl = new SnapsTextControl();
            textControl._controlType = SnapsControl.CONTROLTYPE_GRID;
            textControl.text = titleText;
            textControl.setX(titleOffsetX);
            textControl.y = titleOffsetY;
            textControl.width = titleWidth;
            textControl.height = titleHeight;
            textControl.rowCount = String.valueOf(templateInfo.getRowCount());
            textControl.format.fontFace = dayTitleFontFace;
            textControl.format.fontSize = dayTitleFontSize;
            textControl.format.align = templateInfo.isTitleVerticalType() ? ISnapsCalendarConstants.TEXT_ALIGN_CENTER : dayTitleFontAlign;
            textControl.format.fontColor = dayTitleFontColor;
            textControl.format.bold = StringUtil.isEmpty(dayTitleFontBold) ? "false" : dayTitleFontBold;
            textControl.format.baseFontColor = textControl.format.fontColor;

            //SnapsCalendarTextView.java의 getControlTypeFace 메소드에서 fontFace를 다시 구하거 있네... 여기서 설정하는데
            //아래 2줄은 내가 추가함.. 위에서 설정 한것 아무 소용 없음 아래 2줄이 없으면 ㅡㅡ;;
            //그리고 다른 곳에서는 다 설정 하는데 여기서만 안하네.. 최초 상품 진입시.. 저장중인 상품 재편집할때는 하면서...
            textControl.type = "calendar";
            textControl.controType = "day_title";

//			Const_VALUE.SNAPS_TYPEFACE_DAY_TITLE = dayTitleFontFace; //FIXME 구조 좀 바꾸자...
            FontUtil.putUITextListFont(textControl, "day_title", textControl.format.fontFace);

            addTextControl(page, textControl);
        }
    }

    private void processFrontCalendar(SnapsCalendarTemplateInfo calendarTemplateInfo) {
        if (calendarTemplateInfo == null) return;

        final int MAX_DAYS = getMaxDayByCurrentYearMonth(); //몇 일까지 있는지

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < MAX_DAYS; column++) {

                //날짜가 그려지는 좌표
                Point ptDay = new Point(calendarTemplateInfo.getStartOffset().x, calendarTemplateInfo.getStartOffset().y);

                float fixOffsetX = calendarTemplateInfo.getDayOffset().x + (calendarTemplateInfo.getCellWidth() * column);
                int fixOffsetY = (int) (calendarTemplateInfo.getDayOffset().y - calendarTemplateInfo.getDayFontDiffY()); //diffY는 font의 보정치

                if (row == 1)
                    fixOffsetY += calendarTemplateInfo.getCellHeight();

                String dayOffsetX = String.valueOf(ptDay.x + fixOffsetX);
                String dayOffsetY = String.valueOf(ptDay.y + fixOffsetY);

                String label = "";
                if (row == 0) {
                    String tmp[] = {"S", "M", "T", "W", "T", "F", "S"};
                    Calendar cal = Calendar.getInstance();

                    cal.set(nStartYear, nStartMonth - 1, column + 1);
                    int index = cal.get(Calendar.DAY_OF_WEEK);

                    label = tmp[index - 1];
                } else
                    label = String.valueOf(column + 1);

                boolean isHoliday = GetParsedXml.isHolliday(nStartMonth, column + 1, nStartYear);

                textControl = new SnapsTextControl();
                textControl.isCalendarFrontText = true;
                textControl._controlType = SnapsControl.CONTROLTYPE_GRID;
                textControl.text = label;

                textControl.setX(dayOffsetX);
                textControl.y = dayOffsetY;
                textControl.width = String.valueOf(calendarTemplateInfo.getCellWidth());
                textControl.height = String.valueOf(calendarTemplateInfo.getCellHeight());
                textControl.rowCount = String.valueOf(calendarTemplateInfo.getRowCount());
                textControl.format.fontFace = dayFontFace;
                textControl.format.fontSize = dayFontSize;
                textControl.format.align = dayFontAlign;//"center";
                textControl.format.fontColor = isHoliday ? "ff0000" : dayFontColor;
                textControl.format.bold = StringUtil.isEmpty(dayFontBold) ? "false" : dayFontBold;
                textControl.format.baseFontColor = textControl.format.fontColor;
                textControl.controType = "day_front";
                textControl.type = "calendar";
                textControl.setPageIndex(template.getPages().size());

                FontUtil.putUITextListFont(textControl, "day_front", textControl.format.fontFace);

                addTextControl(page, textControl);
            }
        }
    }

    void addTextControl(SnapsPage page, SnapsTextControl textControl) {
        if (textControl != null) {
            page.addControl(textControl);
            textControl = null;
        }
    }
}
