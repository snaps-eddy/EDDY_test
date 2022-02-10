package com.snaps.common.data.parser;

import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.utils.log.Dlog;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import errorhandle.logger.Logg;

public class GetXMLDomParser {
	private static final String TAG = GetXMLDomParser.class.getSimpleName();

	Element root = null;

	public GetXMLDomParser(String xmlString) {
		parsingWithString(xmlString);
	}

	void parsingWithString(String xmlString) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream istream = new ByteArrayInputStream(xmlString.getBytes("utf-8"));
			Document doc = builder.parse(istream);
			root = doc.getDocumentElement();
			Dlog.d("parsingWithString() root:" + root.toString());
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public ArrayList<SnapsTextControl> getTextControl() {
		// textFlow 값을 구한다.
		String[] textflowAttr = { "fontFamily", "fontSize", "textAlign", "color" };
		String[] textflowValues = new String[textflowAttr.length];
		NamedNodeMap map = root.getAttributes();
		int index = 0;
		for (String a : textflowAttr) {
			Node n = map.getNamedItem(a);
			if (n != null)
			textflowValues[index] = n.getNodeValue();
			index++;
		}

		// p 값을 구한다.
		// span 값을 구한다.
		String[] spanAttr = { "fontFamily", "fontSize", "color" };
		String[] spanValues = new String[spanAttr.length];
		NodeList spanNodeList = root.getElementsByTagName("span");

		ArrayList<SnapsTextControl> ret = new ArrayList<SnapsTextControl>();

		for (int i = 0; i < spanNodeList.getLength(); i++) {
			SnapsTextControl textControl = new SnapsTextControl();
			Node spanNode = spanNodeList.item(i);
			NamedNodeMap attr = spanNode.getAttributes();
			index = 0;
			for (String a : spanAttr) {
				Node n = attr.getNamedItem(a);
				if (n != null)
					spanValues[index] = n.getNodeValue();
				index++;
			}
			String text = spanNode.getTextContent();
			Node pNode = spanNode.getParentNode();
			Node pAlign = pNode.getAttributes().getNamedItem("textAlign");
			String textAlign = "";
			if(pAlign != null) {
				textAlign = pAlign.getNodeValue();
			}

			String color = (spanValues[2] != null) ? spanValues[2] : textflowValues[3];
			textControl.format.fontColor = color.replace("#", "");
			textControl.format.baseFontColor = textControl.format.fontColor;
			textControl.format.fontFace = (spanValues[0] != null) ? spanValues[0] : textflowValues[0];
			textControl.format.fontSize = (spanValues[1] != null) ? spanValues[1] : textflowValues[1];
			textControl.format.align = textAlign;
			textControl.text = text;
			ret.add(textControl);
		}

		return ret;
	}
}
