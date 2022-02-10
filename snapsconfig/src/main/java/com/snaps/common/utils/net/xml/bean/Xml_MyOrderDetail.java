package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

public class Xml_MyOrderDetail {
	public String F_DLVY_DATE;
	public String F_ORDR_NAME;
	public String F_ORDR_CELL;
	public String F_ORDR_MAIL;
	public String F_RCPT_NAME;
	public String F_RCPT_CELL;
	public String F_RCPT_ZIP;
	public String F_RCPT_ADDR1;
	public String F_RCPT_ADDR2;
	public String F_RCPT_ADDR3;// 일본주소용
	public String F_RCPT_MAIL;
	public String F_PAY_DATE;
	public String F_ORDR_STAT;
	public String F_ORDR_STAT_CODE;// 주문상태코드 추가예정
	public String F_DLVR_NUMB;
	public String F_POINT_AMNT;// point

	// 가격정보
	public String F_ORDR_AMNT;
	public String F_DLVR_AMNT;
	public String F_STTL_AMNT;
	public String F_USE_AMT;
	
	
	public String F_BANK_NAME;
	public String F_ACCOUNT;
	public String F_SEND_EXPR_DATE;
	public String F_DEPOSIT;
	
	
	
	

	public List<MyOrderDetail> myOrderDetailList = new ArrayList<MyOrderDetail>();
//	public List<MyOrderBank> myOrderBankList = new ArrayList<MyOrderBank>();

	public Xml_MyOrderDetail(String f_DLVY_DATE, String f_ORDR_NAME, String f_ORDR_CELL, String f_ORDR_MAIL, String f_RCPT_NAME, String f_RCPT_CELL, String f_RCPT_ZIP, String f_RCPT_ADDR1,
			String f_RCPT_ADDR2, String f_RCPT_ADDR3, String f_RCPT_MAIL, String f_PAY_DATE, String f_ORDR_STAT, String f_ORDR_STAT_CODE, String f_DLVR_NUMB, String f_ORDR_AMNT, String f_DLVR_AMNT,
			String f_STTL_AMNT, String f_USE_AMT , String f_POINT_AMNT, String f_BANK_NAME, String f_ACCOUNT , String f_SEND_EXPR_DATE, String f_DEPOSIT) {
		F_DLVY_DATE = f_DLVY_DATE;
		F_ORDR_NAME = f_ORDR_NAME;
		F_ORDR_CELL = f_ORDR_CELL;
		F_ORDR_MAIL = f_ORDR_MAIL;
		F_RCPT_NAME = f_RCPT_NAME;
		F_RCPT_CELL = f_RCPT_CELL;
		F_RCPT_ZIP = f_RCPT_ZIP;
		F_RCPT_ADDR1 = f_RCPT_ADDR1;
		F_RCPT_ADDR2 = f_RCPT_ADDR2;
		F_RCPT_ADDR3 = f_RCPT_ADDR3;
		F_RCPT_MAIL = f_RCPT_MAIL;
		F_PAY_DATE = f_PAY_DATE;
		F_ORDR_STAT = f_ORDR_STAT;
		F_ORDR_STAT_CODE = f_ORDR_STAT_CODE;
		F_DLVR_NUMB = f_DLVR_NUMB;
		F_ORDR_AMNT = f_ORDR_AMNT;
		F_DLVR_AMNT = f_DLVR_AMNT;
		F_STTL_AMNT = f_STTL_AMNT;
		F_USE_AMT = f_USE_AMT;
		F_POINT_AMNT = f_POINT_AMNT;
		
		F_BANK_NAME = f_BANK_NAME;
		F_ACCOUNT = f_ACCOUNT;
		F_SEND_EXPR_DATE = f_SEND_EXPR_DATE;
		F_DEPOSIT = f_DEPOSIT;
		
	}

	public static class MyOrderDetail {
		public String F_PROJ_CODE;
		public String F_SIMG_PATH;
		public String F_IMG_YEAR;
		public String F_IMG_SQNC;
		public String F_PROJ_NAME;
		public String F_ORDR_PRICE;
		public String F_PROJ_CNT;
		public String F_PROD_CDOE;
		public String F_PROD_NAME;
		public String F_REG_DATE;
		public String F_CHNL_CODE;

		public MyOrderDetail(String f_PROJ_CODE, String f_SIMG_PATH, String f_IMG_YEAR, String f_IMG_SQNC, String f_PROJ_NAME, String f_ORDR_PRICE, String f_PROJ_CNT, String f_PROD_CDOE,
				String f_PROD_NAME, String f_REG_DATE,String f_CHNL_CODE) {
			F_PROJ_CODE = f_PROJ_CODE;
			F_SIMG_PATH = f_SIMG_PATH;
			F_IMG_YEAR = f_IMG_YEAR;
			F_IMG_SQNC = f_IMG_SQNC;
			F_PROJ_NAME = f_PROJ_NAME;
			F_ORDR_PRICE = f_ORDR_PRICE;
			F_PROJ_CNT = f_PROJ_CNT;
			F_PROD_CDOE = f_PROD_CDOE;
			F_PROD_NAME = f_PROD_NAME;
			F_REG_DATE = f_REG_DATE;
			F_CHNL_CODE = f_CHNL_CODE;
		}

		/*
		 * <F_PROJ_CODE>20130604005749</F_PROJ_CODE> <F_SIMG_PATH> /Upload/Cart15/KOR0030/2013/Q2/20130604/20130604005749/tiny/2013060417030335616_0.jpg </F_SIMG_PATH> <F_IMG_YEAR>2013</F_IMG_YEAR>
		 * <F_IMG_SQNC>0062735677</F_IMG_SQNC> <F_PROJ_NAME>2013년 06월 04일에 만든 명함</F_PROJ_NAME> <F_ORDR_PRICE>3000</F_ORDR_PRICE> <F_PROJ_CNT>3</F_PROJ_CNT> <F_PROD_NAME>카카오명함</F_PROD_NAME>
		 * <F_REG_DATE>2013년 6월 04일</F_REG_DATE>
		 */
	}

	/*
	 * <SCENE ID="MY_ODL"> <ITEM> <MY_ODL_DATE> <F_DLVY_DATE>2013-06-07</F_DLVY_DATE> </MY_ODL_DATE> </ITEM> <ITEM> <MY_ODL_DTL> <F_ORDER_CODE>DD60405051</F_ORDER_CODE> <F_ORDR_NAME>정창록</F_ORDR_NAME>
	 * <F_ORDR_CELL>010-8628-6690</F_ORDR_CELL> <F_ORDR_MAIL>jung3642@naver.com</F_ORDR_MAIL> <F_RCPT_NAME>정창록</F_RCPT_NAME> <F_RCPT_CELL>010-8628-6690</F_RCPT_CELL> <F_RCPT_ZIP>440-320</F_RCPT_ZIP>
	 * <F_RCPT_ADDR1>서울시 구로구 구로3동</F_RCPT_ADDR1> <F_RCPT_ADDR2>티타운</F_RCPT_ADDR2> <F_RCPT_MAIL>jung3642@naver.com</F_RCPT_MAIL> <F_PAY_DATE>20130604</F_PAY_DATE>
	 * <F_ORDR_STAT_CODE>014006</F_ORDR_STAT_CODE> <F_ORDR_STAT>주문처리중</F_ORDR_STAT> <F_DLVR_NUMB/> </MY_ODL_DTL> </ITEM> <ITEM> <MY_ODL_PROD> <F_PROJ_CODE>20130604005526</F_PROJ_CODE> <F_SIMG_PATH>
	 * /Upload/Cart15/KOR0030/2013/Q2/20130604/20130604005526/tiny/2013060416403966173_0.jpg </F_SIMG_PATH> <F_IMG_YEAR>2013</F_IMG_YEAR> <F_IMG_SQNC>0062725451</F_IMG_SQNC> <F_PROJ_NAME>2013년 06월
	 * 04일에 만든 명함</F_PROJ_NAME> <F_ORDR_PRICE>1000</F_ORDR_PRICE> <F_PROJ_CNT>1</F_PROJ_CNT> <F_PROD_NAME>카카오명함</F_PROD_NAME> <F_REG_DATE>2013년 6월 04일</F_REG_DATE> </MY_ODL_PROD> <MY_ODL_PROD>
	 * <F_PROJ_CODE>20130604005749</F_PROJ_CODE> <F_SIMG_PATH> /Upload/Cart15/KOR0030/2013/Q2/20130604/20130604005749/tiny/2013060417030335616_0.jpg </F_SIMG_PATH> <F_IMG_YEAR>2013</F_IMG_YEAR>
	 * <F_IMG_SQNC>0062735677</F_IMG_SQNC> <F_PROJ_NAME>2013년 06월 04일에 만든 명함</F_PROJ_NAME> <F_ORDR_PRICE>3000</F_ORDR_PRICE> <F_PROJ_CNT>3</F_PROJ_CNT> <F_PROD_NAME>카카오명함</F_PROD_NAME>
	 * <F_REG_DATE>2013년 6월 04일</F_REG_DATE> </MY_ODL_PROD> </ITEM> </SCENE>
	 */
}
