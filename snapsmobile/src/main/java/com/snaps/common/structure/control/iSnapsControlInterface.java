package com.snaps.common.structure.control;

import com.snaps.common.structure.SnapsXML;
import com.snaps.common.structure.page.SnapsPage;

import org.xmlpull.v1.XmlPullParser;

public interface iSnapsControlInterface {
	SnapsXML getControlSaveXML(SnapsXML xml);
	SnapsXML getControlAuraOrderXML(SnapsXML xml, SnapsPage sp, float difX, float difY);
	void parse(XmlPullParser parser);
}
