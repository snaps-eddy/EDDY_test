package com.snaps.common.structure.control;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.log.Dlog;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;

public class SnapsSceneMaskControl extends SnapsLayoutControl implements Parcelable, Serializable {

    private static final String TAG = SnapsSceneMaskControl.class.getSimpleName();
    private static final long serialVersionUID = 5245814207044452729L;

    private String id;

    public SnapsSceneMaskControl() {
        type = "sceneMask";
    }

    @Override
    public SnapsXML getControlSaveXML(SnapsXML xml) {
        try {
//            <objects resourceURL="/Upload/Data1/Resource/scene_mask/keyring-heart.svg" width="192" height="162" status="ready" angle="0" x="54" y="54" reactId="0_2" type="sceneMask" id="sm_keyring_heart" alpha="100"/>
            xml.startTag(null, "sceneMask");
            xml.attribute(null, "resourceURL", resourceURL);
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);
            xml.attribute(null, "angle", angle);
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "type", "sceneMask");
            xml.attribute(null, "id", id);
            xml.attribute(null, "alpha", alpha);

            xml.endTag(null, "sceneMask");
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return xml;
    }

    @Override
    public SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY) {
        try {
//            <object width="192" height="162" angle="0" x="54" y="54" type="sceneMask" id="sm_keyring_heart" alpha="1"/>
            xml.startTag(null, "object");
            xml.attribute(null, "width", width);
            xml.attribute(null, "height", height);
            xml.attribute(null, "angle", angle);
            xml.attribute(null, "x", x);
            xml.attribute(null, "y", y);
            xml.attribute(null, "type", "sceneMask");
            xml.attribute(null, "id", id);
            xml.attribute(null, "alpha", alpha);

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

    /**
     * Parceable implements
     *
     * @param in
     */

    public SnapsSceneMaskControl(Parcel in) {
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
        public SnapsSceneMaskControl createFromParcel(Parcel in) {
            return new SnapsSceneMaskControl(in);
        }

        @Override
        public SnapsSceneMaskControl[] newArray(int size) {
            return new SnapsSceneMaskControl[size];
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
        angle = getValue(parser, "angle", "0");
        angleClip = getValue(parser, "angle", "0");
        id = getValue(parser, "id", "");

//        xml.startTag(null, "");
//        xml.attribute(null, "resourceURL", resourceURL);
//        xml.attribute(null, "width", width);
//        xml.attribute(null, "height", height);
//        xml.attribute(null, "angle", angle);
//        xml.attribute(null, "x", x);
//        xml.attribute(null, "y", y);
//        xml.attribute(null, "type", "sceneMask");
//        xml.attribute(null, "id", id);
//        xml.attribute(null, "alpha", alpha);
    }
}
