package com.snaps.mobile.utils.ui;

import com.snaps.common.utils.log.Dlog;

/***
 * 숫자로 된 문자를 계산하는 클래스..
 * 
 * @author hansang-ug
 *
 */
public class CalcUtil {
	private static final String TAG = CalcUtil.class.getSimpleName();

	static public String addIntString(String a, String b) {
		int result = -1;

		try {
			result = Integer.parseInt(a) + Integer.parseInt(b);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return result + "";
	}
	
	static public String subIntString(String a, String b) {
		int result = -1;

		try {
			result = Integer.parseInt(a) - Integer.parseInt(b);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return result + "";
	}

	static public int addInt(int a, String b) {
		int result = -1;

		try {
			result = a + Integer.parseInt(b);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return result;
	}

	static public float addFloat(String a, String b) {
		float result = -1;

		try {
			result = Float.parseFloat(a) + Float.parseFloat(b);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return result;

	}

	static public float addFloat(float a, String b) {
		float result = -1;

		try {
			result = a + Float.parseFloat(b);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return result;

	}

}
