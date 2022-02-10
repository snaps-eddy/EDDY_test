package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

public class SnapsHoleControl extends SnapsLayoutControl implements Parcelable, Serializable {
    private static final String TAG = SnapsHoleControl.class.getSimpleName();
    private static final long serialVersionUID = -949480199380406686L;

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
            xml.startTag(null, "hole");
            // 키홀의 원 사이즈는 42px 이고, 편집기에서 보여줄 땐 42px 로 보여준다.
            // xml 에 쓸 경우에는 안쪽 뚫린 원의 크기 (18px) 로 width, height를 써야한다.
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            // 키홀의 원 사이즈는 42px 이고, 편집기에서 보여줄 땐 42px 로 보여준다.
            // xml 에 쓸 경우에는 안쪽 뚫린 원의 크기 (18px) 로 width, height를 써야한다.
            xml.attribute(null, "width", "18");
            xml.attribute(null, "height", "18");

            xml.attribute(null, "angle", angle);
            xml.attribute(null, "angleClip", angle);

            xml.endTag(null, "hole");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
            xml.startTag(null, "object");
            xml.attribute(null, "type", "hole");
            // 키홀의 원 사이즈는 42px 이고, 편집기에서 보여줄 땐 42px 로 보여준다.
            // xml 에 쓸 경우에는 안쪽 뚫린 원의 크기 (18px) 로 width, height를 써야한다.
            xml.attribute(null, "x", String.valueOf(getIntX() + 12));
            xml.attribute(null, "y", String.valueOf(getIntY() + 12));
            // 키홀의 원 사이즈는 42px 이고, 편집기에서 보여줄 땐 42px 로 보여준다.
            // xml 에 쓸 경우에는 안쪽 뚫린 원의 크기 (18px) 로 width, height를 써야한다.
            xml.attribute(null, "width", "18");
            xml.attribute(null, "height", "18");

            xml.attribute(null, "angle", angle);
            xml.attribute(null, "angleClip", angle);

            xml.endTag(null, "object");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsHoleControl() {
    }

    public SnapsHoleControl(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    private void readFromParcel(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsHoleControl createFromParcel(Parcel in) {
            return new SnapsHoleControl(in);
        }

        @Override
        public SnapsHoleControl[] newArray(int size) {
            return new SnapsHoleControl[size];
        }
    };

    @Override
    public void parse(XmlPullParser parser) {
        _controlType = SnapsControl.CONTROLTYPE_MOVABLE;

        x = getValue(parser, "x", "0");
        y = getValue(parser, "y", "0");
        width = getValue(parser, "width", "0");
        height = getValue(parser, "height", "0");

        angle = getValue(parser, "angle", "0");
        angleClip = getValue(parser, "angle", "0");
    }
}
