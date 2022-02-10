package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

public class SnapsSceneCutControl extends SnapsLayoutControl implements Parcelable, Serializable {
    private static final String TAG = SnapsSceneMaskControl.class.getSimpleName();
    private static final String TYPE_NAME = "sceneCut";
//    private static final long serialVersionUID = 5245814207044452728L;

    private String id;

    public SnapsSceneCutControl() {
        type = TYPE_NAME;
    }

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
            xml.startTag(null, TYPE_NAME);
            xml.attribute(null, "resourceURL", resourceURL);
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "id", id);
            xml.endTag(null, TYPE_NAME);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
            xml.startTag(null, "object");
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "type", TYPE_NAME);
            xml.attribute(null, "id", id);
            xml.endTag(null, "object");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return xml;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SnapsSceneCutControl(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public SnapsSceneCutControl createFromParcel(Parcel in) {
            return new SnapsSceneCutControl(in);
        }

        @Override
        public SnapsSceneCutControl[] newArray(int size) {
            return new SnapsSceneCutControl[size];
        }
    };

    @Override
    public void parse(XmlPullParser parser) {
        _controlType = SnapsControl.CONTROLTYPE_LOCKED;

        x = getValue(parser, "x", "0");
        y = getValue(parser, "y", "0");
        width = getValue(parser, "width", "0");
        height = getValue(parser, "height", "0");
        resourceURL = getValue(parser, "resourceURL", "");
        id = getValue(parser, "id", "");
    }
}
