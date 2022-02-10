package com.snaps.common.utils.ui;

public class ProdNameUtil {

	/***
	 * auraOrder.xml에 prod_name을 만들어 내는 함수..(액자 군에 한해서..)
	 * 
	 * @param mmWidth
	 * @param mmHeight
	 * @return
	 */
	public static String getProdName(String mmWidth, String mmHeight) {

		int mmW = Integer.parseInt(mmWidth);
		int mmH = Integer.parseInt(mmHeight);

		int bigMM = Math.max(mmW, mmH);
		int smallMM = Math.min(mmW, mmH);

		String prod_name = "";

		if (bigMM <= 914) {
			if (smallMM <= 102)
				prod_name = "4R";
			else if (smallMM <= 127)
				prod_name = "5R";
			else if (smallMM <= 203)
				prod_name = "8R";
			else if (smallMM <= 279)
				prod_name = "11R";
			else if (smallMM <= 305)
				prod_name = "12R";
			else if (smallMM <= 508)
				prod_name = "20R";
			else
				prod_name = "24R";
		} else {
			if (smallMM <= 508)
				prod_name = "20R";
			else
				prod_name = "24R";
		}

		return "FRM_" + prod_name;
	}
}
