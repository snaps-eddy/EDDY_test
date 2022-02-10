package com.snaps.common.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

import java.io.Serializable;

public class SnapsFrameInfo implements Parcelable, Serializable {
	private static final String TAG = SnapsFrameInfo.class.getSimpleName();
	private static final long serialVersionUID = 5164686399131387163L;
	String RESOURCECODE = "";
	String F_IMG_URL = "";
	String F_FRAME_IMG_URL = "";
	String F_FRAME_IMG_DETAIL = "";
	String F_FRAME_IMG_DETAIL2 = "";
	String F_FRAME_PRICE = "";
	String F_FRAME_PRICE2 = "";
	String F_FRAME_DESC = "";
	String F_FRAME_THICK = "";
	String F_FRAME_DUG = "";
	String F_FRAME_MAX_LENGTH = "";
	String F_FRAME_MIN_LENGTH = "";
	String F_FRAME_ROUND = "";
	String F_FRAME_WIDTH = "";
	String F_FRAME_HEIGHT = "";

	protected SnapsFrameInfo(Parcel in) {
		RESOURCECODE = in.readString();
		F_IMG_URL = in.readString();
		F_FRAME_IMG_URL = in.readString();
		F_FRAME_IMG_DETAIL = in.readString();
		F_FRAME_IMG_DETAIL2 = in.readString();
		F_FRAME_PRICE = in.readString();
		F_FRAME_PRICE2 = in.readString();
		F_FRAME_DESC = in.readString();
		F_FRAME_THICK = in.readString();
		F_FRAME_DUG = in.readString();
		F_FRAME_MAX_LENGTH = in.readString();
		F_FRAME_MIN_LENGTH = in.readString();
		F_FRAME_ROUND = in.readString();
		F_FRAME_WIDTH = in.readString();
		F_FRAME_HEIGHT = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(RESOURCECODE);
		dest.writeString(F_IMG_URL);
		dest.writeString(F_FRAME_IMG_URL);
		dest.writeString(F_FRAME_IMG_DETAIL);
		dest.writeString(F_FRAME_IMG_DETAIL2);
		dest.writeString(F_FRAME_PRICE);
		dest.writeString(F_FRAME_PRICE2);
		dest.writeString(F_FRAME_DESC);
		dest.writeString(F_FRAME_THICK);
		dest.writeString(F_FRAME_DUG);
		dest.writeString(F_FRAME_MAX_LENGTH);
		dest.writeString(F_FRAME_MIN_LENGTH);
		dest.writeString(F_FRAME_ROUND);
		dest.writeString(F_FRAME_WIDTH);
		dest.writeString(F_FRAME_HEIGHT);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<SnapsFrameInfo> CREATOR = new Creator<SnapsFrameInfo>() {
		@Override
		public SnapsFrameInfo createFromParcel(Parcel in) {
			return new SnapsFrameInfo(in);
		}

		@Override
		public SnapsFrameInfo[] newArray(int size) {
			return new SnapsFrameInfo[size];
		}
	};

	public String getRESOURCECODE() {
		return RESOURCECODE;
	}
	public void setRESOURCECODE(String rESOURCECODE) {
		RESOURCECODE = rESOURCECODE;
	}
	public String getF_IMG_URL() {
		return F_IMG_URL;
	}
	public void setF_IMG_URL(String f_IMG_URL) {
		F_IMG_URL = f_IMG_URL;
	}
	public String getF_FRAME_IMG_URL() {
		return F_FRAME_IMG_URL;
	}

	public String getF_FRAME_IMG_DETAIL() {
		return F_FRAME_IMG_DETAIL;
	}

	public String getF_FRAME_IMG_DETAIL2() {
		return F_FRAME_IMG_DETAIL2;
	}

	public String getF_FRAME_PRICE() {
		return F_FRAME_PRICE;
	}
	public void setF_FRAME_PRICE(String f_FRAME_PRICE) {
		F_FRAME_PRICE = f_FRAME_PRICE;
	}
	public String getF_FRAME_PRICE2() {
		return F_FRAME_PRICE2;
	}

	public String getF_FRAME_DESC() {
		return F_FRAME_DESC;
	}

	public String getF_FRAME_THICK() {
		return F_FRAME_THICK;
	}

	public String getF_FRAME_MAX_LENGTH() {
		return F_FRAME_MAX_LENGTH;
	}

	public String getF_FRAME_MIN_LENGTH() {
		return F_FRAME_MIN_LENGTH;
	}
	public void setF_FRAME_THICK(String f_FRAME_THICK) {
		F_FRAME_THICK = f_FRAME_THICK;
	}
	public String getF_FRAME_DUG() {
		return F_FRAME_DUG;
	}
	public String getF_FRAME_ROUND() {
		return F_FRAME_ROUND;
	}
	public String getF_FRAME_WIDTH() {
		return F_FRAME_WIDTH;
	}
	public String getF_FRAME_HEIGHT() {
		return F_FRAME_HEIGHT;
	}

	public void setF_FRAME_DUG(String f_FRAME_DUG) {
		F_FRAME_DUG = f_FRAME_DUG;
	}
	public void setF_FRAME_MAX_LENGTH(String f_FRAME_MAX_LENGTH) {
		F_FRAME_MAX_LENGTH = f_FRAME_MAX_LENGTH;
	}
	public void setF_FRAME_MIN_LENGTH(String f_FRAME_MIN_LENGTH) {
		F_FRAME_MIN_LENGTH = f_FRAME_MIN_LENGTH;
	}
	public void setF_FRAME_DESC(String f_FRAME_DESC) {
		F_FRAME_DESC = f_FRAME_DESC;
	}
	public void setF_FRAME_PRICE2(String f_FRAME_PRICE2) {
		F_FRAME_PRICE2 = f_FRAME_PRICE2;
	}
	public void setF_FRAME_IMG_DETAIL(String f_FRAME_IMG_DETAIL) {
		F_FRAME_IMG_DETAIL = f_FRAME_IMG_DETAIL;
	}
	public void setF_FRAME_IMG_URL(String f_FRAME_IMG_URL) {
		F_FRAME_IMG_URL = f_FRAME_IMG_URL;
	}
	public void setF_FRAME_IMG_DETAIL2(String f_FRAME_IMG_DETAIL2) {
		F_FRAME_IMG_DETAIL2 = f_FRAME_IMG_DETAIL2;
	}
	public void setF_FRAME_ROUND( String f_FRAME_ROUND ) {
		F_FRAME_ROUND = f_FRAME_ROUND;
	}
	public void setF_FRAME_WIDTH( String f_FRAME_WIDTH ) {
		F_FRAME_WIDTH = f_FRAME_WIDTH;
	}
	public void setF_FRAME_HEIGHT( String f_FRAME_HEIGHT) {
		F_FRAME_HEIGHT = f_FRAME_HEIGHT;
	}

	public SnapsXML getSaveXML(SnapsXML xml) {
		try {
			xml.startTag(null, "FRAMEINFO");
			xml.addTag(null, "RESOURCECODE", RESOURCECODE);
			xml.addTag(null, "F_IMG_URL", F_IMG_URL);
			xml.addTag(null, "F_FRAME_IMG_URL", F_FRAME_IMG_URL);
			xml.addTag(null, "F_FRAME_IMG_DETAIL", F_FRAME_IMG_DETAIL);
			xml.addTag(null, "F_FRAME_IMG_DETAIL2", F_FRAME_IMG_DETAIL2);
			xml.addTag(null, "F_FRAME_PRICE", F_FRAME_PRICE);
			xml.addTag(null, "F_FRAME_PRICE2", F_FRAME_PRICE2);
			xml.addTag(null, "F_FRAME_DESC", F_FRAME_DESC);
			xml.addTag(null, "F_FRAME_DUG", F_FRAME_DUG);
			xml.addTag(null, "F_FRAME_MAX_LENGTH", F_FRAME_MAX_LENGTH);
			xml.addTag(null, "F_FRAME_MIN_LENGTH", F_FRAME_MIN_LENGTH);
			xml.addTag(null, "F_FRAME_ROUND", F_FRAME_ROUND);
			xml.addTag(null, "F_FRAME_WIDTH", F_FRAME_WIDTH);
			xml.addTag(null, "F_FRAME_HEIGHT", F_FRAME_HEIGHT);
			xml.endTag(null, "FRAMEINFO");

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return xml;
	}

	public void setData(String key, String value) {
		if ("RESOURCECODE".equalsIgnoreCase(key)) {
			RESOURCECODE = value;
		} else if ("F_IMG_URL".equalsIgnoreCase(key)) {
			F_IMG_URL = value;
		} else if ("F_FRAME_IMG_URL".equalsIgnoreCase(key)) {
			F_FRAME_IMG_URL = value;
		} else if ("F_FRAME_IMG_DETAIL".equalsIgnoreCase(key)) {
			F_FRAME_IMG_DETAIL = value;
		} else if ("F_FRAME_IMG_DETAIL2".equalsIgnoreCase(key)) {
			F_FRAME_IMG_DETAIL2 = value;
		} else if ("F_FRAME_PRICE".equalsIgnoreCase(key)) {
			F_FRAME_PRICE = value;
		} else if ("F_FRAME_PRICE2".equalsIgnoreCase(key)) {
			F_FRAME_PRICE2 = value;
		} else if ("F_FRAME_DESC".equalsIgnoreCase(key)) {
			F_FRAME_DESC = value;
		} else if ("F_FRAME_THICK".equalsIgnoreCase(key)) {
			F_FRAME_THICK = value;
		} else if ("F_FRAME_DUG".equalsIgnoreCase(key)) {
			F_FRAME_DUG = value;
		} else if ("F_FRAME_MAX_LENGTH".equalsIgnoreCase(key)) {
			F_FRAME_MAX_LENGTH = value;
		} else if ("F_FRAME_MIN_LENGTH".equalsIgnoreCase(key)) {
			F_FRAME_MIN_LENGTH = value;
		} else if ("F_FRAME_ROUND".equalsIgnoreCase(key)) {
			F_FRAME_ROUND = value;
		} else if ("F_FRAME_WIDTH".equalsIgnoreCase(key)) {
			F_FRAME_WIDTH = value;
		} else if ("F_FRAME_HEIGHT".equalsIgnoreCase(key)) {
			F_FRAME_HEIGHT = value;
		}
	}

	public int getFrameWidthByMM() {
		// 기본값을 12로 함..ㅋㅋ
		if (F_FRAME_THICK.equals(""))
			return 12;
		return (int) (Float.parseFloat(F_FRAME_THICK) * 10);
	}

	public int getFrameDugWidth() {
		// 기본값을 12로 함..ㅋㅋ
		if (F_FRAME_DUG.equals(""))
			return 6;
		return (int) (Float.parseFloat(F_FRAME_DUG) * 10);

	}
	
	public SnapsFrameInfo() {
		
	}
	
}
