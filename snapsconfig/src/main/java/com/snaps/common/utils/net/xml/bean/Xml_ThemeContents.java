package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_ThemeContents {

	public List<ThemeContents> bgList = new ArrayList<Xml_ThemeContents.ThemeContents>();

	public static class ThemeContents {

		public String F_RSRC_CODE;
		public String F_RSRC_NAME;

		public String F_DIMG_PATH;
		public String F_EIMG_PATH;
		public String F_REG_DATE;
		public String F_SEARCH_TAGS;
		public String F_CATEGORY_CODE;

		public boolean F_IS_SELECT = false;

		public ThemeContents(String f_RSRC_CODE, String f_RSRC_NAME, String f_DIMG_PATH, String f_EIMG_PATH, String f_REG_DATE, String f_SEARCH_TAGS, String f_CATEGORY_CODE) {

			F_RSRC_CODE = f_RSRC_CODE;
			F_RSRC_NAME = f_RSRC_NAME;

			F_DIMG_PATH = f_DIMG_PATH;
			F_EIMG_PATH = f_EIMG_PATH;
			F_REG_DATE = f_REG_DATE;
			F_SEARCH_TAGS = f_SEARCH_TAGS;
			F_CATEGORY_CODE = f_CATEGORY_CODE;
			
			F_IS_SELECT = false;
		}
	}
}
