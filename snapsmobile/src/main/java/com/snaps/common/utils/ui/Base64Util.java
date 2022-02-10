package com.snaps.common.utils.ui;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

/**
 *
 * com.snaps.kakao.utils.ui
 * Base64Util.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 28.
 * @Version : 
 */
public class Base64Util {

	/**
	 * @param txt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encode ( String txt ) throws UnsupportedEncodingException {
		byte [] data = txt.getBytes( "UTF-8" );
		return Base64.encodeToString( data , Base64.DEFAULT );
	}
	
	/**
	 * @param txt
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decode ( String txt ) throws UnsupportedEncodingException {
		return new String( Base64.decode( txt, Base64.DEFAULT ) , "UTF-8" ); 
	}
}
