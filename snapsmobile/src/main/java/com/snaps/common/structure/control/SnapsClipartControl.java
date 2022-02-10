package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.storybook.StoryData.StoryLikeData.Emotion;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

public class SnapsClipartControl extends SnapsLayoutControl implements Parcelable, Serializable {
    private static final String TAG = SnapsClipartControl.class.getSimpleName();
    private static final long serialVersionUID = -6898600821747909654L;
    public String clipart_id = "";
    private String overPrint = "false";

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        // <clipart rc="199 280 440.846706867218 482.01313161849976" priority="0" angle="0" readOnly="true" move="true" resize="true" rotate="true"
        // delete="false" copy="false" exclude="false" id="0390012812" alpha="1" resourceURL="/Upload/Data1/Resource/sticker/edit/Est111_na.png"/>
        try {
            xml.startTag(null, "clipart");

            xml.attribute(null, "rc", getRC2());

            xml.attribute(null, "offsetX", getOffsetX() + "");
            xml.attribute(null, "offsetY", getOffsetY() + "");

            xml.attribute(null, "priority", "0");
            xml.attribute(null, "angle", angle);
            xml.attribute(null, "readOnly", "true");
            xml.attribute(null, "resize", "true");
            xml.attribute(null, "rotate", "true");
            xml.attribute(null, "delete", "false");
            xml.attribute(null, "copy", "false");
            xml.attribute(null, "exclude", "false");
            xml.attribute(null, "id", this.clipart_id);
            xml.attribute(null, "alpha", alpha);
            xml.attribute(null, "overPrint", overPrint);

            xml.attribute(null, "resourceURL", this.resourceURL);

            xml.endTag(null, "clipart");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        // <object type="sticker" id="0390012812" alpha="255" angle="0" x="199" y="264" width="241" height="202"/>
        try {

            if (Const_PRODUCT.isCardShapeFolder()) {
                difX = cardFolderFixValueX;
                difY = cardFolderFixValueY;
            }

            xml.startTag(null, "object");

            String[] imageRcClip = this.getRcClip().replace(" ", "|").split("\\|");

            xml.attribute(null, "type", "sticker");
            xml.attribute(null, "id", this.clipart_id);

            xml.attribute(null, "alpha", String.valueOf(Float.parseFloat(alpha) * 255.f));

            xml.attribute(null, "angle", this.angle);

            xml.attribute(null, "x", String.valueOf(Integer.parseInt(imageRcClip[0]) + difX + getOffsetX()));
            xml.attribute(null, "y", String.valueOf(Integer.parseInt(imageRcClip[1]) + difY + getOffsetY()));

            xml.attribute(null, "width", imageRcClip[2]);
            xml.attribute(null, "height", imageRcClip[3]);

            xml.attribute(null, "overPrint", this.overPrint);

            if (this.clipart_id.equals("0390014282")) {
                Dlog.d("getControlAuraOrderXML() clipart_id:0390014282");
            }

            xml.endTag(null, "object");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    /***
     * left top right bottom 형태로 리턴하는 rect
     *
     * @return
     */
    String getRC2() {

        float left = Float.parseFloat(this.x);
        float top = Float.parseFloat(this.y);
        float right = Float.parseFloat(this.width) + left;
        float bottom = Float.parseFloat(this.height) + top;

        return x + " " + y + " " + right + " " + bottom;

    }

    public SnapsClipartControl() {
    }

    public SnapsClipartControl(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(clipart_id);
        dest.writeString(overPrint);
    }

    private void readFromParcel(Parcel in) {
        clipart_id = in.readString();
        overPrint = in.readString();

        boolean[] arrBool = new boolean[1];
        in.readBooleanArray(arrBool);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsClipartControl createFromParcel(Parcel in) {
            return new SnapsClipartControl(in);
        }

        @Override
        public SnapsClipartControl[] newArray(int size) {
            return new SnapsClipartControl[size];
        }
    };

    @Override
    public void parse(XmlPullParser parser) {
        _controlType = SnapsControl.CONTROLTYPE_STICKER;

        String[] temp = getValue(parser, "rc").replace(" ", "|").split("\\|");
        int left = (int) Float.parseFloat(temp[0]);
        int top = (int) Float.parseFloat(temp[1]);
        int right = (int) Float.parseFloat(temp[2]);
        int bottom = (int) Float.parseFloat(temp[3]);

        angle = getValue(parser, "angle", "0");
        alpha = getValue(parser, "alpha");

        setX(String.valueOf(left));
        y = String.valueOf(top);
        width = String.valueOf((int) (right - left));
        height = String.valueOf((int) (bottom - top));
        resourceURL = getValue(parser, "resourceURL");
        if (resourceURL.equals(""))
            resourceURL = getValue(parser, "uploadPath");
        clipart_id = getValue(parser, "id");
        overPrint = getValue(parser, "overPrint");
        // 회전이 있는경우 스티커 중심회전 좌표로 변경을 해야한다..
        if (!angle.equals("0")) {
            convertClipRect(this, angle);
        }
        setSnsproperty(getValue(parser, "snsproperty"));

        stick_target = getValue(parser, "stick_target");

        stick_dirction = getValue(parser, "stick_direction");

        stick_margin = getValue(parser, "stick_margin");
        stick_id = getValue(parser, "id");
        id = getValue(parser, "name");

        Dlog.d("parse() clipart alpha:" + alpha);
    }

    static public SnapsClipartControl getStickerControl(Emotion emoticon) {

        SnapsClipartControl clipart = new SnapsClipartControl();

        switch (emoticon) {
            case LIKE:
                clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est82_gg.png";
                clipart.clipart_id = "0390014261";
                break;
            case COOL:
                clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est78_gg.png";
                clipart.clipart_id = "0390014257";
                break;
            case HAPPY:
                clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est80_gg.png";
                clipart.clipart_id = "0390014259";
                break;
            case SAD:
                clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est79_gg.png";
                clipart.clipart_id = "0390014258";
                break;
            case CHEER_UP:
                clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est81_gg.png";
                clipart.clipart_id = "0390014260";
                break;
            default:
                break;
        }

        clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;
        clipart.angle = "0";
        clipart.alpha = "1";
        clipart.width = "5";
        clipart.height = "3";
        return clipart;
    }

    static public SnapsClipartControl setFeelSticker() {
        return getStickerControl("feel");
    }

    static public SnapsClipartControl setRelySticker() {
        return getStickerControl("rely");
    }

    static public SnapsClipartControl setEtcSticker() {
        return getStickerControl("etc");
    }

    static public SnapsClipartControl setFacebookCommentSticker() {
        return getStickerControl("facebook_comment");
    }

    static public SnapsClipartControl setFacebookForwardSticker() {
        return getStickerControl("facebook_forward");
    }

    static public SnapsClipartControl setFacebookStoryLineSticker() {
        return getStickerControl("facebook_storyline");
    }

    static SnapsClipartControl getStickerControl(String type) {
        SnapsClipartControl clipart = new SnapsClipartControl();
        if (type.equals("feel")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est76_gg.png";
            clipart.clipart_id = "0390014255";
            clipart.width = "6";
            clipart.height = "6";
        } else if (type.equals("rely")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est77_gg.png";
            clipart.clipart_id = "0390014256";
            clipart.width = "6";
            clipart.height = "6";
        } else if (type.equals("etc")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est102_gg.png";
            clipart.clipart_id = "0390014281";
            clipart.width = "5";
            clipart.height = "3";
        } else if (type.equals("facebook_comment")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est199_gg.png";
            clipart.clipart_id = "0390016978";
            clipart.width = "3";
            clipart.height = "3";
        } else if (type.equals("facebook_forward")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est200_gg.png";
            clipart.clipart_id = "0390016979";
            clipart.width = "3";
            clipart.height = "4";
        } else if (type.equals("facebook_storyline")) {
            clipart.resourceURL = "/Upload/Data1/Resource/sticker/edit/Est225_gg.png";
            clipart.clipart_id = "0390017162";
            clipart.width = "128";
            clipart.height = "1";
        }

        clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;
        clipart.angle = "0";
        clipart.alpha = "1";
        return clipart;
    }

    public SnapsClipartControl copyClipartControl() {
        SnapsClipartControl layout = new SnapsClipartControl();
        layout.setX(x);
        layout.y = y;
        layout.type = type;
        layout.width = width;
        layout.height = height;
        layout.angle = angle;
        layout.border = border;
        layout.isClick = isClick;

        layout.stick_dirction = stick_dirction;
        layout.stick_margin = stick_margin;
        layout.stick_target = stick_target;
        layout.resourceURL = resourceURL;

        layout.regName = regName;
        layout.regValue = regValue;
        layout.isSnsBookCover = isSnsBookCover;

        if (imgData != null) {
            layout.imgData = new MyPhotoSelectImageData();
            layout.imgData.set(imgData);
        }

        layout.img_width = img_width;
        layout.img_height = img_height;
        layout.angle = angle;
        layout.angleClip = angleClip;
        layout.freeAngle = freeAngle;
        layout.srcTargetType = srcTargetType;
        layout.srcTarget = srcTarget;
        layout.setSnsproperty(getSnsproperty());

        layout._controlType = _controlType;
        layout.clipart_id = clipart_id;
        layout.alpha = alpha;
        layout.setOverPrint(overPrint);

        return layout;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(", ");
        sb.append("clipart_id:").append(clipart_id);
        sb.append("is Over Print : ").append(overPrint);
        return sb.toString();
    }

    public boolean isOverPrint() {
        return "true".equalsIgnoreCase(overPrint);
    }

    public void setOverPrint(String overPrint) {
        if (overPrint == null || overPrint.trim().length() == 0) {
            this.overPrint = "false";
        }
        this.overPrint = overPrint;
    }

    public void setOverPrint(boolean overPrint) {
        this.overPrint = String.valueOf(overPrint);
    }
}
