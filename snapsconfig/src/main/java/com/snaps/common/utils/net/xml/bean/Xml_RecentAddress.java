package com.snaps.common.utils.net.xml.bean;


import com.snaps.common.utils.log.Dlog;

public class Xml_RecentAddress {
	private static final String TAG = Xml_RecentAddress.class.getSimpleName();
	public String F_RCPT_NAME;
	public String F_RCPT_TELP;
	public String F_RCPT_CELL;
	public String F_RCPT_ZIP;
	public String F_RCPT_ADDR1;
	public String F_RCPT_ADDR2;
	public String F_RCPT_ADDR3;
	public int F_ADD_PRICE;
	public Xml_RecentAddress(String f_RCPT_NAME, String f_RCPT_TELP, String f_RCPT_CELL, String f_RCPT_ZIP, String f_RCPT_ADDR1, String f_RCPT_ADDR2, String f_RCPT_ADDR3, String f_ADD_PRICE) {
		F_RCPT_NAME = f_RCPT_NAME;
		F_RCPT_TELP = f_RCPT_TELP;
		F_RCPT_CELL = f_RCPT_CELL;
		F_RCPT_ZIP = f_RCPT_ZIP;
		F_RCPT_ADDR1 = f_RCPT_ADDR1;
		F_RCPT_ADDR2 = f_RCPT_ADDR2;
		F_RCPT_ADDR3 = f_RCPT_ADDR3;
		try {
			if (f_ADD_PRICE != null && f_ADD_PRICE.length() > 0)
				F_ADD_PRICE = Integer.valueOf(f_ADD_PRICE);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}
	}
	
	public String GetF_RCPT_ADDR3()
	{
		return F_RCPT_ADDR3;
	}
	/*
	 *<SCENE ID="MY_HIS">
<ITEM>
<MY_HIS_LST>
<F_RCPT_NAME>정창록</F_RCPT_NAME>
<F_RCPT_TELP/>
<F_RCPT_CELL>010-8628-6690</F_RCPT_CELL>
<F_RCPT_ZIP>440-320</F_RCPT_ZIP>
<F_RCPT_ADDR1>서울시 구로구 구로3동</F_RCPT_ADDR1>
<F_RCPT_ADDR2>티타운</F_RCPT_ADDR2>
</MY_HIS_LST>
</ITEM>
</SCENE>
*/
}
