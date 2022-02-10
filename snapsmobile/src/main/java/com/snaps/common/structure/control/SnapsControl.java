package com.snaps.common.structure.control;


import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.utils.ui.RotateUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

public class SnapsControl implements iSnapsControlInterface, Parcelable, Serializable {
    private static final String TAG = SnapsControl.class.getSimpleName();
    /**
     *
     */
    private static final long serialVersionUID = -1346197189783361231L;
    public static final int CONTROLTYPE_IMAGE = 0;
    public static final int CONTROLTYPE_TEXT = 1;
    public static final int CONTROLTYPE_BALLOON = 2;        //과거에 있었는데 현재 없음
    public static final int CONTROLTYPE_STICKER = 3;
    public static final int CONTROLTYPE_GRID = 4;
    public static final int CONTROLTYPE_FORMSTYLE = 5;
    public static final int CONTROLTYPE_MOVABLE = 6;
    public static final int CONTROLTYPE_LOCKED = 7;

    /*
     * 아직 미구현 public final int CONTROLTYPE_DESIGN = 4; public final int CONTROLTYPE_DECO = 5; public final int CONTROLTYPE_MYITEM = 6; public final int CONTROLTYPE_FORM = 7; public final int
     * CONTROLTYPE_MAP = 8;
     */

    public final String REGNAME_BACKGROUND = "background";
    public final String REGNAME_USERIMAGE = "user_image";
    public final String REGNAME_USERTEXT = "user_text";

    public final String LAYERNAME_BACKGROUND = "background_layer";
    public final String LAYERNAME_IMAGE = "image_layer";
    public final String LAYERNAME_FORM = "form_layer";

    // control layer 타입...
    public int _controlType;

    public float cardFolderFixValueX = 0;
    public float cardFolderFixValueY = 0;

    public String x = "0";
    public String y = "0";
    public String width = "0";
    public String height = "0";
    public String w = "0";
    public String h = "0";

    public String scaledX = "0";
    public String scaledY = "0";
    public String scaledWidth = "0";
    public String scaledHeight = "0";

    public String img_x = "0";
    public String img_y = "0";
    public String img_width = "0";
    public String img_height = "0";

    public String priority = "";
    public int freeAngle = 0;
    public String angle = "0";

    public String cal_idx = "0";

    public String alpha = "1";

    public String angleClip = "0";
    public String readOnly = "true";
    public String move = "true";
    public String resize = "true";
    public String rotate = "true";
    public String delete = "false";
    public String copy = "true";
    public String exclude = "true";
    /**
     * regist name
     */
    public String regName = "";
    /**
     * regist value
     */
    public String regValue = "1";
    public String layername = "";
    public String analysisImageKey = "";

    public String getVersion = "";

    // 클릭여부
    public String isClick = "false";

    // Calendar
    public String cellType = "0";

    public String rowCount = "0";

    public String day_offest = "0";

    public String dayTitle_offest = "0";

    /**** Max page ****/
    private int maxPageX = 0; // 맥스페이지가 적용이 될때.. 추가될 offset 값설정 태마북은 무조건. 우측으로 컨트롤들을 밀어버린다...

    private int pageIndex = -1;
    private int controlID = -1;

    private int offsetX = 0;
    private int offsetY = 0;

    // 카카스북을 위한 프로퍼티 저장이 되지 않았도 된다.
    String snsproperty = "";
    String format = "";
    String textType = "";

    //레이아웃 구성후 삭제를 위한 키를 저장.
    public String identifier = "";

    public String stick_dirction = "";
    public String stick_target = "";
    public String stick_margin = "";
    // clipart snsproperty 없는 경우를 위해 리소스 아이디값을 삽입.
    public String stick_id = "";

    //컨트롤 아이디 저장 template에서는 name
    public String id = "";

    public int getControlId() {
        return controlID;
    }

    public void setControlId(int control_id) {
        this.controlID = control_id;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getRc() {
        return this.img_x + " " + this.img_y + " " + this.img_width + " " + this.img_height;
    }

    public String getRcClip() {
        return (int) (Float.parseFloat(x) + maxPageX) + " " + this.y + " " + this.width + " " + this.height;
    }

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        return null;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        return null;
    }

    public int getScaledX() {
        int returnX = 0;
        try {
            if (scaledX != null && scaledX.length() > 0)
                returnX = (int) Float.parseFloat(scaledX);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        return returnX;
    }

    public void setScaledX(String scaledX) {
        this.scaledX = scaledX;
    }

    public int getScaledY() {
        int returnY = 0;
        try {
            if (scaledY != null && scaledY.length() > 0)
                returnY = (int) Float.parseFloat(scaledY);
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
        }

        return returnY;
    }

    public void setScaledY(String scaledY) {
        this.scaledY = scaledY;
    }

    public String getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(String scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public String getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(String scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public int getX() {
        // maxpage가 추가가 되면서 리턴시 맥스페이지가 있으면 더해서 넘긴다. 테마북인 경우만 사용하기 때문에 무조건 더해준다.
        return getIntX();
    }

    public int getIntX() {
        return (int) (Float.parseFloat(x));
    }

    public int getIntY() {
        return (int) (Float.parseFloat(y));
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public int getMaxPageX() {
        return maxPageX;
    }

    public void setMaxPageX(int maxPageX) {
        this.maxPageX = maxPageX;
    }

    /***
     * 가로세로의 비율을 반환하는 함수...
     *
     * @return
     */
    public float getRatio() {
        float width = Float.parseFloat(this.width);
        float height = Float.parseFloat(this.height);

        return width / height;
    }

    protected String getAngle() {
        int angle = this.angle.equals("") ? 0 : (int) Float.parseFloat(this.angle);
        int angleClip = this.angleClip.equals("") ? 0 : (int) Float.parseFloat(this.angleClip);

        return String.valueOf(angle + angleClip);
    }

    public int getIntWidth() {
        return (int) (Float.parseFloat(width));
    }

    public int getIntHeight() {
        return (int) (Float.parseFloat(height));
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public String getSnsproperty() {
        return snsproperty;
    }

    public void setSnsproperty(String snsproperty) {
        this.snsproperty = snsproperty;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_controlType);

        dest.writeString(x);
        dest.writeString(y);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(w);
        dest.writeString(h);

        dest.writeString(img_x);
        dest.writeString(img_y);
        dest.writeString(img_width);
        dest.writeString(img_height);

        dest.writeString(priority);
        dest.writeInt(freeAngle);
        dest.writeString(angle);

        dest.writeString(cal_idx);
        dest.writeString(alpha);

        dest.writeString(angleClip);
        dest.writeString(readOnly);
        dest.writeString(move);
        dest.writeString(resize);
        dest.writeString(rotate);
        dest.writeString(delete);
        dest.writeString(copy);
        dest.writeString(exclude);

        dest.writeString(regName);
        dest.writeString(regValue);
        dest.writeString(layername);
        dest.writeString(analysisImageKey);

        dest.writeString(getVersion);

        dest.writeString(isClick);
        dest.writeString(cellType);
        dest.writeString(rowCount);
        dest.writeString(day_offest);
        dest.writeString(dayTitle_offest);

        dest.writeInt(maxPageX);
        dest.writeInt(pageIndex);
        dest.writeInt(controlID);
        dest.writeInt(offsetX);
        dest.writeInt(offsetY);
        dest.writeString(snsproperty);
        dest.writeString(format);
        dest.writeString(textType);
    }

    private void readFromParcel(Parcel in) {
        _controlType = in.readInt();

        x = in.readString();
        y = in.readString();
        width = in.readString();
        height = in.readString();
        w = in.readString();
        h = in.readString();

        img_x = in.readString();
        img_y = in.readString();
        img_width = in.readString();
        img_height = in.readString();

        priority = in.readString();
        freeAngle = in.readInt();
        angle = in.readString();

        cal_idx = in.readString();
        alpha = in.readString();

        angleClip = in.readString();
        readOnly = in.readString();
        move = in.readString();
        resize = in.readString();
        rotate = in.readString();
        delete = in.readString();
        copy = in.readString();
        exclude = in.readString();

        regName = in.readString();
        regValue = in.readString();
        layername = in.readString();
        analysisImageKey = in.readString();

        getVersion = in.readString();

        isClick = in.readString();
        cellType = in.readString();
        rowCount = in.readString();
        day_offest = in.readString();
        dayTitle_offest = in.readString();

        maxPageX = in.readInt();
        pageIndex = in.readInt();
        controlID = in.readInt();
        offsetX = in.readInt();
        offsetY = in.readInt();

        snsproperty = in.readString();
        format = in.readString();
        textType = in.readString();
    }


    protected String getValue(XmlPullParser parser, String name) {
        String value = parser.getAttributeValue("", name);
        return (value == null) ? "" : value;
    }

    protected String getValue(XmlPullParser parser, String name, String initValue) {
        String value = parser.getAttributeValue("", name);
        return (value == null) ? initValue : value;
    }

    public SnapsControl() {
    }

    public SnapsControl(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsControl createFromParcel(Parcel in) {
            return new SnapsControl(in);
        }

        @Override
        public SnapsControl[] newArray(int size) {
            return new SnapsControl[size];
        }
    };

    @Override
    public void parse(XmlPullParser parser) {

    }

    /***
     * 회전시 중심에서 회전이 된것처럼 좌표를 변환해주는 함수..
     *
     * @param control
     * @param angle
     */
    public void convertClipRect(SnapsControl control, String angle) {

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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID:").append(getControlId()).append(", ");
        sb.append("X:").append(getIntX()).append(", ");
        sb.append("Y:").append(getIntY()).append(", ");
        sb.append("W:").append(getIntWidth()).append(", ");
        sb.append("H:").append(getIntHeight());
        return sb.toString();
    }
}
