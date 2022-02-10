package com.snaps.common.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.card.SnapsTextOptions;

import java.io.Serializable;

import errorhandle.logger.Logg;

public class SnapsTemplateInfo implements Parcelable, Serializable {
	private static final String TAG = SnapsTemplateInfo.class.getSimpleName();
	private static final long serialVersionUID = -4477263547143902064L;

	public enum COVER_TYPE {
		HARD_COVER, SOFT_COVER, NONE_COVER
	};

	public static float HARDCOVER_SPINE_WIDTH = 8.f;
	float SOFTCOVER_SPINE_WIDTH = 12.f; // 소프트 커버 기본 넓이.

	// 썬샤인 160한장의 두께
	SnapsMaxPageInfo maxPageInfo = null;
	public SnapsFrameInfo frameInfo = new SnapsFrameInfo();

	public float mmMidWidth = 0.0f;

	public String F_PROD_CODE = "";
	public String F_PROD_NAME = "";
	public String F_PROD_SIZE = "";
	public String F_PROD_NICK_NAME = "";
	public String F_GLOSSY_TYPE = "";
	public String F_USE_ANALECTA = "";
	public String F_ENLARGE_IMG = "";
	public String FEP = "";
	public String F_USE_WATERMARK = "";
	public String F_COVER_VIRTUAL_WIDTH = "";
	public String F_COVER_VIRTUAL_HEIGHT = "";
	public String F_COVER_EDGE_WIDTH = "";
	public String F_COVER_EDGE_HEIGHT = "";
	public String F_BASE_QUANTITY = "";
	public String F_MAX_QUANTITY = "";
	public String F_PRNT_TYPE = "";
	public String F_EDIT_COVER = "";
	public String F_COVER_TYPE = "";
	public String F_COVER_MID_WIDTH = "";
	public String F_COVER2_MID_WIDTH = "";
	public String F_COVER_MM_WIDTH = "";
	public String F_COVER_MM_HEIGHT = "";
	public String F_COVER2_MM_WIDTH = "";
	public String F_COVER2_MM_HEIGHT = "";
	public String F_COVER_CHANGE_QUANTITY = "";
	public String F_TITLE_MM_WIDTH = "";
	public String F_TITLE_MM_HEIGHT = "";
	public String F_PAGE_MM_WIDTH = "";
	public String F_PAGE_MM_HEIGHT = "";
	public String F_PAGE_PIXEL_WIDTH = "";
	public String F_PAGE_PIXEL_HEIGHT = "";
	public String F_THUMBNAIL_PAGE_PIXEL_WIDTH = "";
	public String F_THUMBNAIL_PAGE_PIXEL_HEIGHT = "";
	public String F_CP_CODE = "";
	public String F_UI_COVER = "";
	public String F_UI_BACKGROUND = "";
	public String F_UI_LAYOUT = "";
	public String F_UI_BORDER = "";

	public String F_THUMBNAIL_STEP = "";
	public String F_TEXT_SIZE_BASE = "";
	public String F_PROD_TYPE = "";
	public String F_RES_MIN = "";
	public String F_RES_DISABLE = "";
	public String F_PAGE_START_NUM = "";
	public String F_CENTER_LINE = "";
	public String F_UNITTEXT = "";
	public String F_CALENDAR_BONUS_12 = "";
	public String F_SPLIT_COVER_MIDSIZE = "";
	public String F_ALLOW_NO_FULL_IMAGE_YORN = "";
	public String F_TMPL_CODE = "";
	public String F_TMPL_SUB = "";
	public String F_TMPL_ID = "";
	public String F_TMPL_NAME = "";
	public String F_XML_ID = "";
	public String F_TMPL_DESC = "";
	public String F_REG_DATE = "";
	public String F_TMPL_TITLE = "";
	public String F_EDIT_PLATFORM = "";
	public String F_COVER_XML_WIDTH = "";
	public String F_COVER_XML_HEIGHT = "";
	public String F_MIN_QTY = "";
	public String F_SELL_UNIT = "";
	public String F_USE_FORMBOARD = "";
	public String F_DLVR_PRICE = "";

	public String F_PAPER_CODE = "";

	public double SOFT_COVER_PXFORMM;
	public String F_FRAME_TYPE = "";
	public String F_FRAME_ID = ""; // 원목액자 검정 네츄럴 구분용

	// max page 추가용
	public String F_COVEREDGE_TYPE = "";

	// SNS 정보
	public String F_SNS_BOOK_INFO_THUMBNAIL = "";
	public String F_SNS_BOOK_INFO_USER_NAME = "";
	public String F_SNS_BOOK_INFO_PERIOD = "";

	// 카드 정보
	public SnapsTextOptions snapsTextOption = new SnapsTextOptions();

	public String F_ACTIVITY = "";

	// 추천AI 커버 이미지 정보
	public String F_COVER_IMAGE_KEY = "";

	protected SnapsTemplateInfo(Parcel in) {
		SOFTCOVER_SPINE_WIDTH = in.readFloat();
		maxPageInfo = in.readParcelable(SnapsMaxPageInfo.class.getClassLoader());
		frameInfo = in.readParcelable(SnapsFrameInfo.class.getClassLoader());
		mmMidWidth = in.readFloat();
		F_PROD_CODE = in.readString();
		F_PROD_NAME = in.readString();
		F_PROD_SIZE = in.readString();
		F_PROD_NICK_NAME = in.readString();
		F_GLOSSY_TYPE = in.readString();
		F_USE_ANALECTA = in.readString();
		F_ENLARGE_IMG = in.readString();
		FEP = in.readString();
		F_USE_WATERMARK = in.readString();
		F_COVER_VIRTUAL_WIDTH = in.readString();
		F_COVER_VIRTUAL_HEIGHT = in.readString();
		F_COVER_EDGE_WIDTH = in.readString();
		F_COVER_EDGE_HEIGHT = in.readString();
		F_BASE_QUANTITY = in.readString();
		F_MAX_QUANTITY = in.readString();
		F_PRNT_TYPE = in.readString();
		F_EDIT_COVER = in.readString();
		F_COVER_TYPE = in.readString();
		F_COVER_MID_WIDTH = in.readString();
		F_COVER2_MID_WIDTH = in.readString();
		F_COVER_MM_WIDTH = in.readString();
		F_COVER_MM_HEIGHT = in.readString();
		F_COVER2_MM_WIDTH = in.readString();
		F_COVER2_MM_HEIGHT = in.readString();
		F_COVER_CHANGE_QUANTITY = in.readString();
		F_TITLE_MM_WIDTH = in.readString();
		F_TITLE_MM_HEIGHT = in.readString();
		F_PAGE_MM_WIDTH = in.readString();
		F_PAGE_MM_HEIGHT = in.readString();
		F_PAGE_PIXEL_WIDTH = in.readString();
		F_PAGE_PIXEL_HEIGHT = in.readString();
		F_THUMBNAIL_PAGE_PIXEL_WIDTH = in.readString();
		F_THUMBNAIL_PAGE_PIXEL_HEIGHT = in.readString();
		F_CP_CODE = in.readString();
		F_UI_COVER = in.readString();
		F_UI_BACKGROUND = in.readString();
		F_UI_LAYOUT = in.readString();
		F_UI_BORDER = in.readString();
		F_THUMBNAIL_STEP = in.readString();
		F_TEXT_SIZE_BASE = in.readString();
		F_PROD_TYPE = in.readString();
		F_RES_MIN = in.readString();
		F_RES_DISABLE = in.readString();
		F_PAGE_START_NUM = in.readString();
		F_CENTER_LINE = in.readString();
		F_UNITTEXT = in.readString();
		F_CALENDAR_BONUS_12 = in.readString();
		F_SPLIT_COVER_MIDSIZE = in.readString();
		F_ALLOW_NO_FULL_IMAGE_YORN = in.readString();
		F_TMPL_CODE = in.readString();
		F_TMPL_SUB = in.readString();
		F_TMPL_ID = in.readString();
		F_TMPL_NAME = in.readString();
		F_XML_ID = in.readString();
		F_TMPL_DESC = in.readString();
		F_REG_DATE = in.readString();
		F_TMPL_TITLE = in.readString();
		F_EDIT_PLATFORM = in.readString();
		F_COVER_XML_WIDTH = in.readString();
		F_COVER_XML_HEIGHT = in.readString();
		F_MIN_QTY = in.readString();
		F_SELL_UNIT = in.readString();
		F_USE_FORMBOARD = in.readString();
		F_DLVR_PRICE = in.readString();
		F_PAPER_CODE = in.readString();
		SOFT_COVER_PXFORMM = in.readDouble();
		F_FRAME_TYPE = in.readString();
		F_FRAME_ID = in.readString();
		F_COVEREDGE_TYPE = in.readString();
		F_SNS_BOOK_INFO_THUMBNAIL = in.readString();
		F_SNS_BOOK_INFO_USER_NAME = in.readString();
		F_SNS_BOOK_INFO_PERIOD = in.readString();
		snapsTextOption = in.readParcelable(SnapsTextOptions.class.getClassLoader());
		F_ACTIVITY = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(SOFTCOVER_SPINE_WIDTH);
		dest.writeParcelable(maxPageInfo, flags);
		dest.writeParcelable(frameInfo, flags);
		dest.writeFloat(mmMidWidth);
		dest.writeString(F_PROD_CODE);
		dest.writeString(F_PROD_NAME);
		dest.writeString(F_PROD_SIZE);
		dest.writeString(F_PROD_NICK_NAME);
		dest.writeString(F_GLOSSY_TYPE);
		dest.writeString(F_USE_ANALECTA);
		dest.writeString(F_ENLARGE_IMG);
		dest.writeString(FEP);
		dest.writeString(F_USE_WATERMARK);
		dest.writeString(F_COVER_VIRTUAL_WIDTH);
		dest.writeString(F_COVER_VIRTUAL_HEIGHT);
		dest.writeString(F_COVER_EDGE_WIDTH);
		dest.writeString(F_COVER_EDGE_HEIGHT);
		dest.writeString(F_BASE_QUANTITY);
		dest.writeString(F_MAX_QUANTITY);
		dest.writeString(F_PRNT_TYPE);
		dest.writeString(F_EDIT_COVER);
		dest.writeString(F_COVER_TYPE);
		dest.writeString(F_COVER_MID_WIDTH);
		dest.writeString(F_COVER2_MID_WIDTH);
		dest.writeString(F_COVER_MM_WIDTH);
		dest.writeString(F_COVER_MM_HEIGHT);
		dest.writeString(F_COVER2_MM_WIDTH);
		dest.writeString(F_COVER2_MM_HEIGHT);
		dest.writeString(F_COVER_CHANGE_QUANTITY);
		dest.writeString(F_TITLE_MM_WIDTH);
		dest.writeString(F_TITLE_MM_HEIGHT);
		dest.writeString(F_PAGE_MM_WIDTH);
		dest.writeString(F_PAGE_MM_HEIGHT);
		dest.writeString(F_PAGE_PIXEL_WIDTH);
		dest.writeString(F_PAGE_PIXEL_HEIGHT);
		dest.writeString(F_THUMBNAIL_PAGE_PIXEL_WIDTH);
		dest.writeString(F_THUMBNAIL_PAGE_PIXEL_HEIGHT);
		dest.writeString(F_CP_CODE);
		dest.writeString(F_UI_COVER);
		dest.writeString(F_UI_BACKGROUND);
		dest.writeString(F_UI_LAYOUT);
		dest.writeString(F_UI_BORDER);
		dest.writeString(F_THUMBNAIL_STEP);
		dest.writeString(F_TEXT_SIZE_BASE);
		dest.writeString(F_PROD_TYPE);
		dest.writeString(F_RES_MIN);
		dest.writeString(F_RES_DISABLE);
		dest.writeString(F_PAGE_START_NUM);
		dest.writeString(F_CENTER_LINE);
		dest.writeString(F_UNITTEXT);
		dest.writeString(F_CALENDAR_BONUS_12);
		dest.writeString(F_SPLIT_COVER_MIDSIZE);
		dest.writeString(F_ALLOW_NO_FULL_IMAGE_YORN);
		dest.writeString(F_TMPL_CODE);
		dest.writeString(F_TMPL_SUB);
		dest.writeString(F_TMPL_ID);
		dest.writeString(F_TMPL_NAME);
		dest.writeString(F_XML_ID);
		dest.writeString(F_TMPL_DESC);
		dest.writeString(F_REG_DATE);
		dest.writeString(F_TMPL_TITLE);
		dest.writeString(F_EDIT_PLATFORM);
		dest.writeString(F_COVER_XML_WIDTH);
		dest.writeString(F_COVER_XML_HEIGHT);
		dest.writeString(F_MIN_QTY);
		dest.writeString(F_SELL_UNIT);
		dest.writeString(F_USE_FORMBOARD);
		dest.writeString(F_DLVR_PRICE);
		dest.writeString(F_PAPER_CODE);
		dest.writeDouble(SOFT_COVER_PXFORMM);
		dest.writeString(F_FRAME_TYPE);
		dest.writeString(F_FRAME_ID);
		dest.writeString(F_COVEREDGE_TYPE);
		dest.writeString(F_SNS_BOOK_INFO_THUMBNAIL);
		dest.writeString(F_SNS_BOOK_INFO_USER_NAME);
		dest.writeString(F_SNS_BOOK_INFO_PERIOD);
		dest.writeParcelable(snapsTextOption, flags);
		dest.writeString(F_ACTIVITY);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SnapsTemplateInfo> CREATOR = new Creator<SnapsTemplateInfo>() {
		@Override
		public SnapsTemplateInfo createFromParcel(Parcel in) {
			return new SnapsTemplateInfo(in);
		}

		@Override
		public SnapsTemplateInfo[] newArray(int size) {
			return new SnapsTemplateInfo[size];
		}
	};

	public SnapsXML getSaveXML(SnapsXML xml) {
		try {
			xml.startTag(null, "TmplInfo");

			xml.addTag(null, "F_PROD_CODE", F_PROD_CODE);
			xml.addTag(null, "F_PROD_NAME", F_PROD_NAME);
			xml.addTag(null, "F_PROD_SIZE", F_PROD_SIZE);
			xml.addTag(null, "F_PROD_NICK_NAME", F_PROD_NICK_NAME);
			xml.addTag(null, "F_GLOSSY_TYPE", F_GLOSSY_TYPE);
			xml.addTag(null, "F_USE_ANALECTA", F_USE_ANALECTA);
			xml.addTag(null, "F_ENLARGE_IMG", F_ENLARGE_IMG);
			xml.addTag(null, "FEP", FEP);
			xml.addTag(null, "F_USE_WATERMARK", F_USE_WATERMARK);
			xml.addTag(null, "F_COVER_VIRTUAL_WIDTH", F_COVER_VIRTUAL_WIDTH);
			xml.addTag(null, "F_COVER_VIRTUAL_HEIGHT", F_COVER_VIRTUAL_HEIGHT);
			xml.addTag(null, "F_COVER_EDGE_WIDTH", F_COVER_EDGE_WIDTH);
			xml.addTag(null, "F_COVER_EDGE_HEIGHT", F_COVER_EDGE_HEIGHT);
			xml.addTag(null, "F_BASE_QUANTITY", F_BASE_QUANTITY);
			xml.addTag(null, "F_MAX_QUANTITY", F_MAX_QUANTITY);
			xml.addTag(null, "F_PRNT_TYPE", F_PRNT_TYPE);
			xml.addTag(null, "F_EDIT_COVER", F_EDIT_COVER);
			xml.addTag(null, "F_COVER_TYPE", F_COVER_TYPE);
			xml.addTag(null, "F_COVER_MID_WIDTH", F_COVER_MID_WIDTH);
			xml.addTag(null, "F_COVER2_MID_WIDTH", F_COVER2_MID_WIDTH);
			xml.addTag(null, "F_COVER_MM_WIDTH", F_COVER_MM_WIDTH);
			xml.addTag(null, "F_COVER_MM_HEIGHT", F_COVER_MM_HEIGHT);
			xml.addTag(null, "F_COVER2_MM_WIDTH", F_COVER2_MM_WIDTH);
			xml.addTag(null, "F_COVER2_MM_HEIGHT", F_COVER2_MM_HEIGHT);
			xml.addTag(null, "F_COVER_CHANGE_QUANTITY", F_COVER_CHANGE_QUANTITY);
			xml.addTag(null, "F_TITLE_MM_WIDTH", F_TITLE_MM_WIDTH);
			xml.addTag(null, "F_TITLE_MM_HEIGHT", F_TITLE_MM_HEIGHT);
			xml.addTag(null, "F_PAGE_MM_WIDTH", F_PAGE_MM_WIDTH);
			xml.addTag(null, "F_PAGE_MM_HEIGHT", F_PAGE_MM_HEIGHT);
			xml.addTag(null, "F_PAGE_PIXEL_WIDTH", F_PAGE_PIXEL_WIDTH);
			xml.addTag(null, "F_PAGE_PIXEL_HEIGHT", F_PAGE_PIXEL_HEIGHT);
			xml.addTag(null, "F_CP_CODE", F_CP_CODE);
			xml.addTag(null, "F_UI_COVER", F_UI_COVER);
			xml.addTag(null, "F_UI_BACKGROUND", F_UI_BACKGROUND);
			xml.addTag(null, "F_UI_LAYOUT", F_UI_LAYOUT);
			xml.addTag(null, "F_UI_BORDER", F_UI_BORDER);

			xml.addTag(null, "F_THUMBNAIL_STEP", F_THUMBNAIL_STEP);
			xml.addTag(null, "F_TEXT_SIZE_BASE", F_TEXT_SIZE_BASE);
			xml.addTag(null, "F_PROD_TYPE", F_PROD_TYPE);
			xml.addTag(null, "F_RES_MIN", F_RES_MIN);
			xml.addTag(null, "F_RES_DISABLE", F_RES_DISABLE);
			xml.addTag(null, "F_PAGE_START_NUM", F_PAGE_START_NUM);
			xml.addTag(null, "F_CENTER_LINE", F_CENTER_LINE);
			xml.addTag(null, "F_UNITTEXT", F_UNITTEXT);
			xml.addTag(null, "F_CALENDAR_BONUS_12", F_CALENDAR_BONUS_12);
			xml.addTag(null, "F_SPLIT_COVER_MIDSIZE", F_SPLIT_COVER_MIDSIZE);
			xml.addTag(null, "F_ALLOW_NO_FULL_IMAGE_YORN", F_ALLOW_NO_FULL_IMAGE_YORN);
			xml.addTag(null, "F_TMPL_CODE", F_TMPL_CODE);
			xml.addTag(null, "F_TMPL_SUB", F_TMPL_SUB);
			xml.addTag(null, "F_TMPL_ID", F_TMPL_ID);
			xml.addTag(null, "F_TMPL_NAME", F_TMPL_NAME);
			xml.addTag(null, "F_XML_ID", F_XML_ID);
			xml.addTag(null, "F_TMPL_DESC", F_TMPL_DESC);
			xml.addTag(null, "F_REG_DATE", F_REG_DATE);
			xml.addTag(null, "F_TMPL_TITLE", F_TMPL_TITLE);
			xml.addTag(null, "F_EDIT_PLATFORM", F_EDIT_PLATFORM);
			xml.addTag(null, "F_COVER_XML_WIDTH", F_COVER_XML_WIDTH);
			xml.addTag(null, "F_COVER_XML_HEIGHT", F_COVER_XML_HEIGHT);
			xml.addTag(null, "F_MIN_QTY", F_MIN_QTY);
			xml.addTag(null, "F_SELL_UNIT", F_SELL_UNIT);
			xml.addTag(null, "F_USE_FORMBOARD", F_USE_FORMBOARD);
			xml.addTag(null, "F_DLVR_PRICE", F_DLVR_PRICE);
			xml.addTag(null, "F_PAPER_CODE", F_PAPER_CODE);
			xml.addTag(null, "F_FRAME_TYPE", F_FRAME_TYPE);
			xml.addTag(null, "F_FRAME_ID", F_FRAME_ID);

			xml.addTag(null, "F_STORY_BOOK_INFO_THUMBNAIL", F_SNS_BOOK_INFO_THUMBNAIL);
			xml.addTag(null, "F_STORY_BOOK_INFO_USER_NAME", F_SNS_BOOK_INFO_USER_NAME);
			xml.addTag(null, "F_STORY_BOOK_INFO_PERIOD", F_SNS_BOOK_INFO_PERIOD);

			//프리미엄 디자인으로 인해 추가.
			String designID = Config.getDesignId();
			if(designID != null && designID.length()>0)
				xml.addTag(null, "F_DESIGNER_ID", Config.getDesignId());

			xml.addTag(null, "F_ACTIVITY", F_ACTIVITY);
			xml.endTag(null, "TmplInfo");

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return xml;
	}


	// 용지에 따라 한장 두께를 구하는 함수
	public float getPaperThick() {
		return getPaperThick2();
	}

	public float getPaperThick2() {
		return maxPageInfo.getSoftCoverPageThickMMSize(F_PAPER_CODE);
	}

	/***
	 * 커버타입이 하드인지 소프트인지 확인하는 함수..
	 * 
	 * @return
	 */
	public COVER_TYPE getCoverType() {

		if (F_COVER_TYPE.equals("hard") || F_COVER_TYPE.equals("padding") || F_COVER_TYPE.equals("leather"))
			return COVER_TYPE.HARD_COVER;
		else if (F_COVER_TYPE.equals("soft"))
			return COVER_TYPE.SOFT_COVER;
		else
			return COVER_TYPE.NONE_COVER;

	}

	/***
	 * 테마북 심플포토북 소프트커버인경우 책등이 생기는 페이지를 구하는 함수...
	 * 
	 * @return
	 */
	public int getSoftCoverAddSpineText() {
		//KT 북
		if (Config.isKTBook()) {
			return 10;
		}

		// 테마북은 37(75p에서 추가)
		if (Config.isThemeBook(F_PROD_CODE) || Config.isSimplePhotoBook(F_PROD_CODE) || Const_PRODUCT.isSNSBook(F_PROD_CODE) || Config.isSimpleMakingBook(F_PROD_CODE)) { // TODO
			// 스토리북도
			// 테마북과
			// 동일하게?
			return 38;// 37
		}

		return -1;
	}

	float getSoftSpineBasemmWidth() {
		return SOFTCOVER_SPINE_WIDTH;
	}

	public float getPXMM() {
		return Float.parseFloat(F_COVER_XML_WIDTH) / Float.parseFloat(F_COVER_EDGE_WIDTH);
	}

	public float getMMPX() {
		return Float.parseFloat(F_COVER_EDGE_WIDTH) / Float.parseFloat(F_COVER_XML_WIDTH);
	}

	public SnapsTemplateInfo() {

	}
}
