package com.snaps.common.utils.net.xml.bean;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Xml_ThemePage {

	public ArrayList<ThemePage> bgList = new ArrayList<Xml_ThemePage.ThemePage>();

	public static class ThemePage extends XML_BasePage {

		/**
		 * 리스트 조회 결과.
		 */
		public ThemePage(String f_SSMPL_URL, String f_MMPL_URL, String f_TMPL_ID, String f_TMPL_CODE, String f_XML_PATH, String f_SEARCH_TAGS, String f_DSPL_NUM, String f_MYITEM_YN,
				String f_MYITEM_CODE, String f_MYMAKE_ITEM, String f_NEW_YORN, String f_MASK_CNT) {

			F_SSMPL_URL = f_SSMPL_URL;
			F_MMPL_URL = f_MMPL_URL;

			F_TMPL_ID = f_TMPL_ID;
			F_TMPL_CODE = f_TMPL_CODE;
			F_XML_PATH = f_XML_PATH;
			F_SEARCH_TAGS = f_SEARCH_TAGS;
			F_DSPL_NUM = f_DSPL_NUM;
			F_MYITEM_YN = f_MYITEM_YN;
			F_MYITEM_CODE = f_MYITEM_CODE;
			F_MYMAKE_ITEM = f_MYMAKE_ITEM;
			F_NEW_YORN = f_NEW_YORN;
			F_MASK_CNT = f_MASK_CNT;

			F_IS_SELECT = false;
		}

        public ThemePage(Element element) {
            NodeList list = element.getChildNodes();
            Node node;
            for( int i = 0; i < list.getLength(); ++i ) {
                node = list.item( i );
                if( node instanceof Element ) {
                    switch ( node.getNodeName() ) {
                        case "F_SSMPL_URL": F_SSMPL_URL = getNodeValue( node.getChildNodes().item(0) );
                        case "F_MMPL_URL": F_MMPL_URL = getNodeValue( node.getChildNodes().item(0) );
                        case "F_TMPL_ID": F_TMPL_ID = getNodeValue( node.getChildNodes().item(0) );
                        case "F_TMPL_CODE": F_TMPL_CODE = getNodeValue( node.getChildNodes().item(0) );
                        case "F_XML_PATH": F_XML_PATH = getNodeValue( node.getChildNodes().item(0) );
                        case "F_SEARCH_TAGS": F_SEARCH_TAGS = getNodeValue( node.getChildNodes().item(0) );
                        case "F_DSPL_NUM": F_DSPL_NUM = getNodeValue( node.getChildNodes().item(0) );
                        case "F_MYITEM_YN": F_MYITEM_YN = getNodeValue( node.getChildNodes().item(0) );
                        case "F_MYITEM_CODE": F_MYITEM_CODE = getNodeValue( node.getChildNodes().item(0) );
                        case "F_MYMAKE_ITEM": F_MYMAKE_ITEM = getNodeValue( node.getChildNodes().item(0) );
                        case "F_NEW_YORN": F_NEW_YORN = getNodeValue( node.getChildNodes().item(0) );
                        case "F_MASK_CNT": F_MASK_CNT = getNodeValue( node.getChildNodes().item(0) );
                    }
                }
            }

            F_IS_SELECT = false;
        }

        private String getNodeValue( Node node ) {
            return node == null ? "" : node.getNodeValue();
        }
	}
}
