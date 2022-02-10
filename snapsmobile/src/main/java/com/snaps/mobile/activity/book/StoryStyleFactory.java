package com.snaps.mobile.activity.book;

import android.content.Context;

import com.snaps.common.storybook.StoryDataType;

public class StoryStyleFactory {

	public static enum eStoryDataStyle {
		NONE, ONE_STYLE, TWO_STYLE, THREE_STYLE, FOUR_STYLE, A_STYLE, B_STYLE, C_STYLE, D_STYLE
	}

	public static final String[] ARR_TMP_ID_LIST_STYLE_A = {"045021004838", "045021004837", "045021004836", "045021004834", "045021004822", "045021004821", "045021004820", "045021004818"};

	public static final String[] ARR_TMP_ID_LIST_STYLE_AA = {"045021005059", "045021005051", "045021005052", "045021005059", "045021005060", "045021005063", "045021005064", "045021005067",
			"045021005068"};

	public static final String[] ARR_TMP_ID_LIST_STYLE_B = {"045021004841", "045021004840", "045021004839", "045021004835", "045021004825", "045021004824", "045021004823", "045021004819",/**/
	"045021005055", "045021005056", "045021005071", "045021005072", "045021005075", "045021005076", "045021005079", "045021005080"};

	public static final String[] ARR_TMP_ID_LIST_STYLE_C = {"045021004846", "045021004845", "045021004844", "045021004842", "045021004830", "045021004829", "045021004828", "045021004826"};

	public static final String[] ARR_TMP_ID_LIST_STYLE_CC = {"045021005053", "045021005054", "045021005061", "045021005062", "045021005065", "045021005066", "045021005069", "045021005070"};

	public static final String[] ARR_TMP_ID_LIST_STYLE_D = {"045021004849", "045021004848", "045021004847", "045021004843", "045021004833", "045021004832", "045021004831", "045021004827",/**/
	"045021005057", "045021005058", "045021005073", "045021005074", "045021005077", "045021005078", "045021005081", "045021005082"};

	public static StoryBookDataManager createStoryData(eStoryDataStyle style, Context con, StoryDataType storyDataType) {

		StoryBookDataManager data = null;

		switch (style) {
			default : //임시코드 7/29
				data = new StoryBookMakerStyleNew(con, storyDataType);
				break;

		}

		return data;
	}

	public static eStoryDataStyle getStyleByTmpId(String code) {
		if (code == null)
			return eStoryDataStyle.NONE;

		for (String str : ARR_TMP_ID_LIST_STYLE_A) {
			if (str.equals(code))
				return eStoryDataStyle.ONE_STYLE;
		}

		for (String str : ARR_TMP_ID_LIST_STYLE_AA) {
			if (str.equals(code))
				return eStoryDataStyle.A_STYLE;
		}

		for (String str : ARR_TMP_ID_LIST_STYLE_B) {
			if (str.equals(code))
				return eStoryDataStyle.TWO_STYLE;
		}

		for (String str : ARR_TMP_ID_LIST_STYLE_C) {
			if (str.equals(code))
				return eStoryDataStyle.THREE_STYLE;
		}

		for (String str : ARR_TMP_ID_LIST_STYLE_CC) {
			if (str.equals(code))
				return eStoryDataStyle.C_STYLE;

		}

		for (String str : ARR_TMP_ID_LIST_STYLE_D) {
			if (str.equals(code))
				return eStoryDataStyle.FOUR_STYLE;
		}

		return eStoryDataStyle.NONE;
	}

	static final String[] ARR_TMP_ID_LIST_WHITE = {"045021004836", "045021004838", "045021004835", "045021004839", "045021004840", "045021004841", "045021004844", "045021004847", "045021004849",/**/
	"045021005059", "045021005061", "045021005062", "045021005067", "045021005068", "045021005073", "045021005074", "045021005075", "045021005076", "045021005081", "045021005082"};
	static final String[] ARR_TMP_ID_LIST_BLACK = {"045021004834", "045021004848", "045021004845", "045021004846", "045021004843", "045021004842",/**/"045021005053", "045021005054", "045021005051",
			"045021005052", "045021005055", "045021005056", "045021005057", "045021005058", "045021005063", "045021005064", "045021005065", "045021005066", "045021005069", "045021005070",
			"045021005071", "045021005072", "045021005077", "045021005078", "045021005079", "045021005080"};
	static final String[] ARR_TMP_ID_LIST_GRAY = {"045021004837"};

	/**
	 * 책등 텍스트 컬러를 가져오는 함수..
	 * 
	 * @param code
	 * @return
	 */
	public static String getBookThick(String code) {
		if (code == null)
			return "000000";

		for (String str : ARR_TMP_ID_LIST_WHITE) {
			if (str.equals(code))
				return "ffffff";
		}

		for (String str : ARR_TMP_ID_LIST_BLACK) {
			if (str.equals(code))
				return "000000";
		}

		for (String str : ARR_TMP_ID_LIST_GRAY) {
			if (str.equals(code))
				return "3e3a39";
		}

		return "000000";
	}
}
