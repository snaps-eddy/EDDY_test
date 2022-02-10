package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_ThemeCategory {

	public List<ThemeCategory> bgList = new ArrayList<Xml_ThemeCategory.ThemeCategory>();

	public static class ThemeCategory {

		public String F_CATEGORY_CODE;
		public String F_CATEGORY_NAME;
		public String F_EIMG_PATH;

		public ThemeCategory(String f_CATEGORY_CODE, String f_CATEGORY_NAME, String f_EIMG_PATH) {

			F_CATEGORY_CODE = f_CATEGORY_CODE;
			F_CATEGORY_NAME = f_CATEGORY_NAME;

			F_EIMG_PATH = f_EIMG_PATH;
			
		}
	}
}
