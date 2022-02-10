package com.snaps.common.utils.net.xml.bean;


import com.snaps.common.utils.log.Dlog;

public class Xml_PostAddress {
	private static final String TAG = Xml_PostAddress.class.getSimpleName();
	public String F_ZIPCD;
	public String F_ADDR;
	public String F_ADDR2;
	public int F_ADD_PRICE;
	public Xml_PostAddress(String f_ZIPCD, String f_ADDR, String f_ADDR2, String f_ADD_PRICE) {
		F_ZIPCD = f_ZIPCD;
		F_ADDR = f_ADDR;
		F_ADDR2 = f_ADDR2;
		try {
			if (f_ADD_PRICE != null && f_ADD_PRICE.length() > 0)
				F_ADD_PRICE = Integer.valueOf(f_ADD_PRICE);
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}
	}
	
	/*
	 *<SCENE ID="MY_ZIP">
<ITEM>
<MY_ZIP_LST>
<F_ZIPCD>157-712</F_ZIPCD>
<F_ADDR>서울 강서구 공항동 대한항공</F_ADDR>
</MY_ZIP_LST>
<MY_ZIP_LST>
<F_ZIPCD>157-811</F_ZIPCD>
<F_ADDR>서울 강서구 공항동 1∼31</F_ADDR>
</MY_ZIP_LST>
<MY_ZIP_LST>
<F_ZIPCD>157-812</F_ZIPCD>
<F_ADDR>서울 강서구 공항동 32∼73</F_ADDR>
</MY_ZIP_LST>
<MY_ZIP_LST>
<F_ZIPCD>157-813</F_ZIPCD>
<F_ADDR>서울 강서구 공항동 150</F_ADDR>
</MY_ZIP_LST>
<MY_ZIP_LST>
<F_ZIPCD>157-814</F_ZIPCD>
<F_ADDR>서울 강서구 공항동 151∼280</F_ADDR>
</MY_ZIP_LST> 

<SCENE ID="MY_ZIP">
<ITEM>
<MY_ZIP_LST>
<F_ZIPCD>9980281</F_ZIPCD>
<F_ADDR>山形県</F_ADDR>
<F_ADDR2>酒田市 飛島</F_ADDR2>
<F_ADD_PRICE>1000</F_ADD_PRICE>
</MY_ZIP_LST>
</ITEM>
</SCENE>
*/
}
