package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_BgCover {

	public List<BgCoverData> bgList = new ArrayList<Xml_BgCover.BgCoverData>();
	
	public static class BgCoverData {
		/** 템플릿 id **/
		public String F_TMPL_ID;
		/** 템플릿 Code **/
		public String F_TMPL_CODE;
		/** 템플릿 sam 이미지 **/
		public String F_SMPL_URL;
		/** 템플릿 thum 이미지 **/
		public String F_MMPL_URL;
		/** 템플릿 xml Path **/
		public String F_XML_PATH;
		
		/**
		 * 
		 * 배경 리스트 조회 결과.
		 * @param tmpl_id	템플릿 ID
		 * @param tmpl_code 템플릿 Code
		 * @param smpl_url 	sam 이미지
		 * @param mmpl_url	thum 이미지
		 * @param xml_path	xml Path
		 */
		public BgCoverData( String tmpl_id , String tmpl_code , String  smpl_url , String mmpl_url , String xml_path ) {
			
			F_TMPL_ID = tmpl_id;
			F_TMPL_CODE = tmpl_code;
			F_SMPL_URL = smpl_url;
			F_MMPL_URL = mmpl_url;
			F_XML_PATH = xml_path;
		}
	}
}
