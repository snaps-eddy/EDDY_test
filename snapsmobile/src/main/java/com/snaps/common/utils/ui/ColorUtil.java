package com.snaps.common.utils.ui;

import android.graphics.Color;

public class ColorUtil {

	public static int getParseColor(String code) {
		int color = Color.parseColor(code);
		return color + 16777216;
	}

	public static String covertRGBToBGR(String colorCode) {

		if (colorCode.length() == 8) {
			String a = colorCode.substring(0, 2);
			String r = colorCode.substring(2, 4);
			String g = colorCode.substring(4, 6);
			String b = colorCode.substring(6, 8);
			return a + b + g + r;
		} else if (colorCode.length() == 6) {
			String r = colorCode.substring(0, 2);
			String g = colorCode.substring(2, 4);
			String b = colorCode.substring(4, 6);

			return b + g + r;
		}

		return colorCode;

	}

}
