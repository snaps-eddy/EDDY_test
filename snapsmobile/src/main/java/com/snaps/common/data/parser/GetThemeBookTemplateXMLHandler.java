package com.snaps.common.data.parser;

import com.snaps.common.utils.constant.Config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GetThemeBookTemplateXMLHandler extends GetTemplateXMLHandler {

	@Override
	public String getValue(Attributes target, String name) {
		// 테마북 A5만 템플릿 수
		if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(Config.getPROD_CODE()) && (name.equals("x") || name.equals("y") || name.equals("width") || name.equals("height"))) {
			String value = target.getValue(name);
			return (value == null) ? "" : (int) (Integer.parseInt(value) * template.a5PerA6) + "";
		} else {

			String value = target.getValue(name);
			return (value == null) ? "" : value;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equalsIgnoreCase(TAG_TMPLINFO)) {
			if (Config.PRODUCT_THEMEBOOK_A5.equalsIgnoreCase(Config.getPROD_CODE())) {
				template.info.F_PAGE_PIXEL_WIDTH = (Float.parseFloat(template.info.F_PAGE_PIXEL_WIDTH) * template.a5PerA6) + "";
				template.info.F_PAGE_PIXEL_HEIGHT = (Float.parseFloat(template.info.F_PAGE_PIXEL_HEIGHT) * template.a5PerA6) + "";

				template.info.F_COVER_VIRTUAL_WIDTH = (Float.parseFloat(template.info.F_COVER_VIRTUAL_WIDTH) * template.a5PerA6) + "";
				template.info.F_COVER_VIRTUAL_HEIGHT = (Float.parseFloat(template.info.F_COVER_VIRTUAL_HEIGHT) * template.a5PerA6) + "";
			}
		}

		super.endElement(uri, localName, qName);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characterText = new StringBuffer();
		super.startElement(uri, localName, qName, attributes);
	}

}
