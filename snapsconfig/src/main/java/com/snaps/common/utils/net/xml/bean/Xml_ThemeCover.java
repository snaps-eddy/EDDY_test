package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;

public class Xml_ThemeCover {

	public ArrayList<ThemeCover> bgList = new ArrayList<Xml_ThemeCover.ThemeCover>();

	public static class ThemeCover extends XML_BasePage {
		/**
		 * 배경 리스트 조회 결과.
		 */
		public ThemeCover(String f_SSMPL_URL, String f_MMPL_URL, String f_TMPL_ID, String f_TMPL_CODE, String f_XML_PATH, String f_SEARCH_TAGS, String f_DSPL_NUM, String f_MYITEM_YN,
				String f_MYITEM_CODE, String f_MYMAKE_ITEM, String f_NEW_YORN, String f_RESIZE_320_url) {

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
			F_RESIZE_320_URL = f_RESIZE_320_url;

			F_IS_SELECT = false;
		}
	}
}
