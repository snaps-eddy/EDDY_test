package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

public class SnapsHelperControl extends SnapsLayoutControl implements Parcelable, Serializable {
    private static final String TAG = SnapsHelperControl.class.getSimpleName();
    private static final long serialVersionUID = -1668705372455600719L;


    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
            xml.startTag(null, "helper");

            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);

            xml.attribute(null, "angle", angle);
            xml.attribute(null, "angleClip", angle);

            xml.endTag(null, "helper");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
            xml.startTag(null, "object");
            xml.attribute(null, "type", "helper");
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);

            xml.attribute(null, "angle", angle);
            xml.attribute(null, "angleClip", angle);

            xml.endTag(null, "object");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    public SnapsHelperControl() {
    }

    public SnapsHelperControl(Parcel in) {
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
