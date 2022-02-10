package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_PushLevel {
	public List<PushLevel> PushEventList = new ArrayList<Xml_PushLevel.PushLevel>();
	
	public static class PushLevel {
		public String F_BRDCST_CODE; //내부코드
		public String F_IMG_EXIST; //이미지존재 여부
		public String F_CLOSE_YORN; //닫기버튼 노출여부
		public String F_RCV_TYPE; // 푸시타입
		public String F_IMG_PATH; //이미지 경로
		public String F_TARGET_PATH; //클릭시 이동경로
		public String F_STATUS; //결과 처
		
		public PushLevel(String f_BRDCST_CODE, String f_IMG_EXIST, String f_CLOSE_YORN, String f_RCV_TYPE, String f_IMG_PATH, String f_TARGET_PATH, String f_STATUS) {
			F_BRDCST_CODE = f_BRDCST_CODE;
			F_IMG_EXIST = f_IMG_EXIST;
			F_CLOSE_YORN = f_CLOSE_YORN;
			F_RCV_TYPE = f_RCV_TYPE;
			F_IMG_PATH = f_IMG_PATH;
			F_TARGET_PATH = f_TARGET_PATH;
			F_STATUS = f_STATUS;
		}
	}
	
}
