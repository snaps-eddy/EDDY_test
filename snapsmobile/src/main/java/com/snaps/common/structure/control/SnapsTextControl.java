package com.snaps.common.structure.control;

import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageAttribute;
import com.snaps.common.text.SnapsTextToImageUtil;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ColorUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.utils.ui.RotateUtil;

import java.io.Serializable;
import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;

public class SnapsTextControl extends SnapsControl implements iSnapsControlInterface, Parcelable, Serializable {
    private static final String TAG = SnapsTextControl.class.getSimpleName();
    private static final long serialVersionUID = 5893887371269447321L;

    public TextFormat format = new TextFormat();
    public String albumMode = "";
    public String initialText = "";
    public String text = "";
    public String textForDiaryPublish = "";
    public String auraOrderText = "";
    public String htmlText = "";
    public ArrayList<LineText> textList = new ArrayList<LineText>();

    public String type = "";

    public String controType = "";
    public float lineSpcing = 0;
    public String spacing = "";
    public String emptyText = "";
    public String textDrawableWidth = "";
    public String textDrawableHeight = "";
    public String thumbNailTextDrawableWidth = "";
    public String thumbNailTextDrawableHeight = "";

    public boolean isCalendarFrontText = false;
    public boolean isEditedText = false;

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
            xml.startTag(null, "text");
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);
            xml.attribute(null, "textDrawableWidth", textDrawableWidth);
            xml.attribute(null, "textDrawableHeight", textDrawableHeight);
            xml.attribute(null, "isEditable", isClick);
            xml.attribute(null, "offsetX", getOffsetX() + "");
            xml.attribute(null, "offsetY", getOffsetY() + "");
            xml.attribute(null, "angle", angle);

            // style tag
            xml.startTag(null, "style");
            xml.attribute(null, "font_face", format.fontFace);
            xml.attribute(null, "alter_font_face", format.alterFontFace);
            xml.attribute(null, "font_size", format.fontSize);
            xml.attribute(null, "auraOrder_FontSize", format.auraOrderFontSize);
            xml.attribute(null, "font_color", format.fontColor);
            xml.attribute(null, "baseFontColor", format.baseFontColor);
            xml.attribute(null, "align", format.align);
            xml.attribute(null, "bold", format.bold);
            xml.attribute(null, "italic", format.italic);
            xml.attribute(null, "underline", format.underline);
            xml.attribute(null, "vertical_view", format.verticalView);
            xml.attribute(null, "orientation", format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL ? "1" : "0");
            xml.attribute(null, "spacing", spacing);
            xml.attribute(null, "lineSpacing", lineSpcing + "");
            xml.attribute(null, "album_mode", albumMode);
            xml.attribute(null, "overPrint", String.valueOf(format.isOverPrint()));
            xml.endTag(null, "style");

            xml.startTag(null, "regist");
            xml.attribute(null, "name", controType);

            SnapsLogger.appendOrderLog("makeSaveXML TextControl text : " + text);

            if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                if (textList != null && textList.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(textList.get(0).text);
                    if (textList.size() > 1)
                        for (int i = 1; i < textList.size(); ++i)
                            sb.append("\n").append(textList.get(i).text);
                    xml.attribute(null, "value_for_diary_publish", StringUtil.CleanInvalidXmlChars(sb.toString()));

                    SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
                    SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
                    String textForDiaryPublish = writeInfo.getContents();
                    if (textForDiaryPublish == null || textForDiaryPublish.length() < 1)
                        textForDiaryPublish = sb.toString();
                    xml.attribute(null, "value", StringUtil.CleanInvalidXmlChars(textForDiaryPublish));
                }
            } else {
                if (Const_PRODUCT.isNewKakaoBook())
                    xml.attribute(null, "value", StringUtil.CleanInvalidXmlChars(text)); //카카오스토리북에서 이모티콘등을 제거하기 위해 사용
                else if (Const_PRODUCT.isSNSBook()) { // 페이스북 포토북에서 재저장 하면 대댓글 줄바꿈 처리 안되는 부분때문에 수정.
                    if (textList != null && textList.size() > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(textList.get(0).text);
                        if (textList.size() > 1)
                            for (int i = 1; i < textList.size(); ++i)
                                sb.append("\n").append(textList.get(i).text);
                        xml.attribute(null, "value", StringUtil.CleanInvalidXmlChars(sb.toString()));
                    } else xml.attribute(null, "value", StringUtil.CleanInvalidXmlChars(text));
                } else
                    xml.attribute(null, "value", text);

            }

            xml.endTag(null, "regist");

            if (initialText.length() > 0) {
                xml.addTag(null, "initial_text", initialText);
            }

            xml.endTag(null, "text");

        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsLogger.appendOrderLog("getControlSaveXML(TextControl) exception : " + e.toString());
        }

        return xml;

    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        if (!SnapsTextToImageUtil.isSupportEditTextProduct()) {
            return getImmutableTextControlAuraOrderXML(xml, sp, difX, difY);
        }

        try {
            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML point1");

            xml.startTag(null, "object");

            // 세로 텍스트라면 반대로 그리고 회전했기 때문에 aura.xml 만들때는 다시 원래대로 바꿈.
            if ("true".equals(this.format.verticalView)/* || (this.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL) */) {
                xml.attribute(null, "width", this.height);
                xml.attribute(null, "height", this.width);
            } else {
                String textControlWidth = this.width;
                String textControlHeight = this.height;

                if (SnapsTextToImageUtil.isSupportEditTextProduct() && !StringUtil.isEmpty(text) && !Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                    if (!StringUtil.isEmpty(this.textDrawableWidth) && !this.textDrawableWidth.equalsIgnoreCase("0")) {
                        textControlWidth = this.textDrawableWidth;
                    }
                    if (!StringUtil.isEmpty(this.textDrawableHeight) && !this.textDrawableHeight.equalsIgnoreCase("0")) {
                        textControlHeight = this.textDrawableHeight;
                    }
                } else if (Const_PRODUCT.isBabyNameStikerGroupProduct() && !StringUtil.isEmpty(text)) {
                    if (!StringUtil.isEmpty(this.thumbNailTextDrawableWidth) && !this.thumbNailTextDrawableWidth.equalsIgnoreCase("0")) {
                        textControlWidth = this.thumbNailTextDrawableWidth;
                    }
                    if (!StringUtil.isEmpty(this.thumbNailTextDrawableHeight) && !this.thumbNailTextDrawableHeight.equalsIgnoreCase("0")) {
                        textControlHeight = this.thumbNailTextDrawableHeight;
                    }
                }

                xml.attribute(null, "width", textControlWidth);
                xml.attribute(null, "height", textControlHeight);
            }

            xml.attribute(null, "angle", angle);

            if (sp.type.equalsIgnoreCase("cover")) {
                xml.attribute(null, "x", String.valueOf(this.getX() + this.getMaxPageX() + difX + this.getOffsetX()));
                xml.attribute(null, "y", String.valueOf((Float.parseFloat(this.y) + difY + this.getOffsetY())));
            } else {

                float targetX = Float.parseFloat(x) + getOffsetX();
                float targetY = Float.parseFloat(y) + getOffsetY();

                //혹시 모르니 땜방
                if (angle == null || angle.length() == 0) {
                    Dlog.w(TAG, "getControlAuraOrderXML() angle is null or empty");
                    angle = "0";
                }

                if (!"0".equals(angle)) {
                    RectF currentRect = new RectF(targetX, targetY, targetX + Float.parseFloat(width), targetY + Float.parseFloat(height));

                    PointF pivot = new PointF(targetX, targetY); // left top
                    RectF targetRect = RotateUtil.convertCenterRotateRect2(currentRect, Float.parseFloat(angle), pivot);

                    targetX = targetRect.left;
                    targetY = targetRect.top;
                }

                xml.attribute(null, "x", String.valueOf(targetX));
                xml.attribute(null, "y", String.valueOf(targetY));

            }

            xml.attribute(null, "type", "html");

            if (this.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL) {
                xml.attribute(null, "direction", "k_vertical");
            } else if ("true".equals(this.format.verticalView)) {
                xml.attribute(null, "direction", "vertical");
            } else {
                xml.attribute(null, "direction", "horizontal");
            }

            xml.attribute(null, "overPrint", String.valueOf(format.isOverPrint()));

            String htmlText = null;
            try {
                htmlText = SnapsTextToImageUtil.createTextToHtmlWithAttribute(SnapsTextToImageAttribute.createAttribute(this));
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            if (htmlText != null) {
                xml.cData(htmlText);
            }

            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML point3");

            xml.endTag(null, "object");
        } catch (Exception e) {
            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML exception " + e.toString());
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsXML getImmutableTextControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML point1");

            xml.startTag(null, "object");
            xml.attribute(null, "type", "textlist");

            if (this.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL) {
                xml.attribute(null, "direction", "k_vertical");
            } else if ("true".equals(this.format.verticalView)) {
                xml.attribute(null, "direction", "vertical");
            } else
                xml.attribute(null, "direction", "horizontal");

            //폰트를 인식하지 못한다는 이슈로 하드 코딩..
            if (this.format.fontFace != null && this.format.fontFace.equalsIgnoreCase("스냅스 윤고딕 700 Bold")) {
                this.format.fontFace = "스냅스 윤고딕 700";
                this.format.bold = "bold";
            }

            xml.attribute(null, "scaleFont", "0");

            //폰트를 인식하지 못한다는 이슈로 하드 코딩..
            if (this.format.fontFace != null && this.format.fontFace.equalsIgnoreCase("스냅스 윤고딕 700 Bold")) {
                this.format.fontFace = "스냅스 윤고딕 700";
                this.format.bold = "bold";
            }

            xml.attribute(null, "face", this.format.fontFace);
            if (this.format.auraOrderFontSize != null && !this.format.auraOrderFontSize.equals(""))
                xml.attribute(null, "size", Float.parseFloat(this.format.auraOrderFontSize) + "");
            else
                xml.attribute(null, "size", (Float.parseFloat(this.format.fontSize) + this.format.getOffsetFontSize()) + "");

            if (!this.format.fontColor.equalsIgnoreCase("") && !this.format.fontColor.equalsIgnoreCase("0")) {
                xml.attribute(null, "color", String.valueOf(ColorUtil.getParseColor("#" + this.format.fontColor)));
            } else {
                xml.attribute(null, "color", "0");
            }

            xml.attribute(null, "align", this.format.align);
            xml.attribute(null, "bold", this.format.isFontBold() ? "1" : "0");
            xml.attribute(null, "italic", this.format.italic.equals("italic") ? "1" : "0");
            xml.attribute(null, "underline", this.format.underline.equals("underline") ? "true" : "false");

            if (!spacing.equals(""))
                xml.attribute(null, "spacing", "130");

            if (sp.type.equalsIgnoreCase("cover")) {
                xml.attribute(null, "x", String.valueOf(this.getX() + this.getMaxPageX() + difX + this.getOffsetX()));
                xml.attribute(null, "y", String.valueOf((Float.parseFloat(this.y) + difY + this.getOffsetY())));
            } else {
                xml.attribute(null, "x", (this.getIntX() + this.getOffsetX()) + "");
                xml.attribute(null, "y", (this.getIntY() + this.getOffsetY()) + "");
            }

            // 세로 텍스트라면 반대로 그리고 회전했기 때문에 aura.xml 만들때는 다시 원래대로 바꿈.
            if ("true".equals(this.format.verticalView)/* || (this.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL) */) {
                xml.attribute(null, "width", this.height);
                xml.attribute(null, "height", this.width);
            } else {
                xml.attribute(null, "width", this.width);
                xml.attribute(null, "height", this.height);
            }

            xml.attribute(null, "overPrint", String.valueOf(format.isOverPrint()));
            xml.attribute(null, "newTextType", "0");

            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML point2");

            ArrayList<LineText> copiedTextList = (ArrayList<LineText>) textList.clone();

            for (LineText linetext : copiedTextList) {

                xml.startTag(null, "line");
                if (sp.type.equalsIgnoreCase("cover")) {
                    xml.attribute(null, "x", String.valueOf(this.getX() + this.getMaxPageX() + difX + this.getOffsetX()));
                    xml.attribute(null, "y", String.valueOf(Float.parseFloat(this.y) + difY + this.getOffsetY()));
                } else {
                    xml.attribute(null, "x", (linetext.getFloatX() + this.getOffsetX()) + "");
                    xml.attribute(null, "y", (linetext.getFloatY() + this.getOffsetY()) + "");
                }

                // 세로 텍스트라면 반대로 그리고 회전했기 때문에 aura.xml 만들때는 다시 원래대로 바꿈.
                if ("true".equals(this.format.verticalView) /* || this.format.orientation == TextFormat.TEXT_ORIENTAION_VERTICAL */) {
                    if (sp.type.equalsIgnoreCase("cover")) {
                        xml.attribute(null, "width", this.height);
                        xml.attribute(null, "height", this.width);
                    } else {
                        xml.attribute(null, "width", linetext.height);
                        xml.attribute(null, "height", linetext.width);
                    }
                } else {
                    if (sp.type.equalsIgnoreCase("page")) {// 내지 텍스트
                        xml.attribute(null, "width", linetext.width);
                        xml.attribute(null, "height", linetext.height);
                    } else {
                        xml.attribute(null, "width", linetext.width);
                        xml.attribute(null, "height", linetext.height);
                    }
                }

                SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML linetext.text  " + linetext.text);

                if (linetext.text == null) {
                    xml.text("");
                } else {
                    xml.text(StringUtil.convertEmojiUniCodeToAlias(StringUtil.CleanInvalidXmlChars(linetext.text)));
                }

                xml.endTag(null, "line");
            }

            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML point3");

            xml.endTag(null, "object");
        } catch (Exception e) {
            SnapsLogger.appendOrderLog("snapsAuraXml getControlAuraOrderXML exception " + e.toString());
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsTextControl copyControl() {
        SnapsTextControl textControl = new SnapsTextControl();
        textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;
        textControl.format.verticalView = format.verticalView;
        textControl.setX(x);
        textControl.y = y;
        textControl.width = width;
        textControl.height = height;
        textControl.textDrawableWidth = textDrawableWidth;
        textControl.textDrawableHeight = textDrawableHeight;
        textControl.thumbNailTextDrawableWidth = thumbNailTextDrawableWidth;
        textControl.thumbNailTextDrawableHeight = thumbNailTextDrawableHeight;
        textControl.lineSpcing = lineSpcing;
        textControl.format.fontFace = format.fontFace;
        textControl.format.alterFontFace = "";
        textControl.format.fontSize = format.fontSize;
        textControl.format.auraOrderFontSize = format.auraOrderFontSize;
        textControl.format.fontColor = format.fontColor;
        textControl.format.baseFontColor = textControl.format.fontColor;
        textControl.format.align = format.align;
        textControl.format.bold = format.bold;
        textControl.format.italic = format.italic;
        textControl.format.underline = format.underline;
        textControl.text = text;
        textControl.format.auraOrderFontSize = format.auraOrderFontSize;
        textControl.controType = controType;
        textControl.priority = priority;
        textControl.textForDiaryPublish = textForDiaryPublish;

        return textControl;
    }

    public void setAuraTextSize(float ratio) {
        format.auraOrderFontSize = Float.parseFloat(format.fontSize) * ratio + "";
    }

    public SnapsTextControl() {
    }

    public SnapsTextControl(Parcel in) {
        super(in);
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(format, 0);

        dest.writeString(albumMode);
        dest.writeString(initialText);
        dest.writeString(text);
        dest.writeString(textForDiaryPublish);
        dest.writeString(auraOrderText);
        dest.writeString(htmlText);

        if (textList != null && !textList.isEmpty()) {
            dest.writeList(textList);
        }

        dest.writeString(type);
        dest.writeString(controType);

        dest.writeFloat(lineSpcing);
        dest.writeString(spacing);

        dest.writeString(emptyText);

        dest.writeString(textDrawableWidth);
        dest.writeString(textDrawableHeight);

        dest.writeString(thumbNailTextDrawableWidth);
        dest.writeString(thumbNailTextDrawableHeight);

        dest.writeBooleanArray(new boolean[]{isCalendarFrontText, isEditedText});
    }

    private void readFromParcel(Parcel in) {

        format = in.readParcelable(TextFormat.class.getClassLoader());

        albumMode = in.readString();
        initialText = in.readString();
        text = in.readString();
        textForDiaryPublish = in.readString();
        auraOrderText = in.readString();
        htmlText = in.readString();

        in.readList(textList, LineText.class.getClassLoader());

        type = in.readString();
        controType = in.readString();
        lineSpcing = in.readFloat();
        spacing = in.readString();

        emptyText = in.readString();
        textDrawableWidth = in.readString();
        textDrawableHeight = in.readString();

        thumbNailTextDrawableWidth = in.readString();
        thumbNailTextDrawableHeight = in.readString();


        boolean[] arBool = new boolean[2];
        in.readBooleanArray(arBool);
        isCalendarFrontText = arBool[0];
        isEditedText = arBool[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsTextControl createFromParcel(Parcel in) {
            return new SnapsTextControl(in);
        }

        @Override
        public SnapsTextControl[] newArray(int size) {
            return new SnapsTextControl[size];
        }
    };

    public static SnapsTextControl getSnapsText(int width, int height, int fontSize, String fontName) {
        SnapsTextControl control = new SnapsTextControl();
        control.width = width + "";
        control.height = height + "";
        control.format.fontSize = fontSize + "";
        control.format.fontFace = fontName;
        control.format.fontColor = "000000";
        control.format.baseFontColor = control.format.fontColor;
        control.format.align = "left";

        return control;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(", ");
        sb.append("text:").append(text);
        return sb.toString();
    }

    public boolean isVerticalText() {
        return Float.parseFloat(height) > Float.parseFloat(width);
    }

}
