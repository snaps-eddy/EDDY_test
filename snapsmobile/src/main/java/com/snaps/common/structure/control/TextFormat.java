package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;

public class TextFormat implements Parcelable, Serializable {

    private static final long serialVersionUID = 5917735975946147414L;
    public static final byte TEXT_ORIENTAION_HORIZONTAL = 0;
    public static final byte TEXT_ORIENTAION_VERTICAL = 1;

    public byte orientation = TEXT_ORIENTAION_HORIZONTAL;

    public String fontFace = "나눔고딕";
    public String alterFontFace = "";
    public String fontSize = "11";
    public String auraOrderFontSize = "";
    public String fontColor = "#000000";
    public String baseFontColor = "#000000";
    public String align = "right";
    public String bold = "false";
    public String italic = "false";     //ben: 어떤곳은 "0", 어떤곳은 "false" 인데 실제 들어가는 값은 "italic"... 정체가.....
    public String verticalView = "";
    public String underline = "false";

    public boolean bVerticalViewValueChaneged = false; // 세로 뷰는 가로와 세로를 한번만 바꿔줘야 함.
    private boolean overPrint = false;

    private int offsetFontSize = 0;

    public int getOffsetFontSize() {
        return offsetFontSize;
    }

    public void setOffsetFontSize(int offsetFontSize) {
    }

    public boolean isFontBold() {
        return !StringUtil.isEmpty(bold) && (bold.equalsIgnoreCase("bold") || bold.equalsIgnoreCase("true"));
    }

    public TextFormat() {
    }

    public TextFormat(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(orientation);

        dest.writeString(fontFace);
        dest.writeString(alterFontFace);
        dest.writeString(fontSize);
        dest.writeString(auraOrderFontSize);
        dest.writeString(fontColor);
        dest.writeString(baseFontColor);
        dest.writeString(align);
        dest.writeString(bold);
        dest.writeString(italic);
        dest.writeString(verticalView);
        dest.writeString(underline);

        dest.writeBooleanArray(new boolean[]{bVerticalViewValueChaneged, overPrint});

        dest.writeInt(offsetFontSize);
    }

    private void readFromParcel(Parcel in) {
        orientation = in.readByte();

        fontFace = in.readString();
        alterFontFace = in.readString();
        fontSize = in.readString();
        auraOrderFontSize = in.readString();
        fontColor = in.readString();
        baseFontColor = in.readString();
        align = in.readString();
        bold = in.readString();
        italic = in.readString();
        verticalView = in.readString();
        underline = in.readString();

        boolean[] arrBool = new boolean[2];
        in.readBooleanArray(arrBool);

        bVerticalViewValueChaneged = arrBool[0];
        overPrint = arrBool[1];

        offsetFontSize = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public TextFormat createFromParcel(Parcel in) {
            return new TextFormat(in);
        }

        @Override
        public TextFormat[] newArray(int size) {
            return new TextFormat[size];
        }
    };

    public void setOverPrint(String overPrint) {
        setOverPrint("true".equalsIgnoreCase(overPrint));
    }

    public void setOverPrint(boolean overPrint) {
        this.overPrint = overPrint;
    }

    public boolean isOverPrint() {
        return overPrint;
    }
}
