package com.snaps.common.structure.photoprint;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsXML;
import errorhandle.logger.Logg;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;

@SuppressLint("ParcelCreator")
public class SnapsPhotoPrintItem implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3945888066060414933L;
	// 변경이 되지않는 변수들...

	public String mAutoBright = "true";
	public String mRecomment = "true";
	public String mPaperMatch = "true";
	public String mGlossy = "glossy";
	public String mAdjustBright = "true";
	public String mDateApplyAll = "false";
	public String mEditWH = "550";

	// 자체 생성하는 값..
	public String mRegDate = "";

	public String mOrderCount = "";
	public String mProdID = "";
	public String mProdCode = ""; // 상품코드...
	public int mKind;// 이미지 종류 device,facebook
	public String mLocalPath = "";
	public String mOrgPath = "";
	public String mOrgSize = "";
	public int mAngle = 0; // 0,90,180,270
	public int mThumbAngle = 0; // 0,90,180,270
	public String mTrimPos = "";
	public String mEndPos = "";

	public String mWidth = "";
	public String mHeight = "";

	public String mX = "";
	public String mY = "";
	public String mOffsetX = "";
	public String mOffsetY = "";

	public String mScale = "";

	String mSell_price = "";
	String mUnit_price = "";

	// service에서 넣을 변수들...
	public String mImgYear = "";
	public String mImgSeq = "";
	public String mUploadPath = "";
	public String mThumImgPath = "";

	public int mOrientation = 0;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mAutoBright);
		dest.writeString(mRecomment);
		dest.writeString(mPaperMatch);
		dest.writeString(mGlossy);
		dest.writeString(mAdjustBright);
		dest.writeString(mDateApplyAll);
		dest.writeString(mEditWH);
		dest.writeString(mRegDate);
		dest.writeString(mOrderCount);
		dest.writeString(mProdID);
		dest.writeInt(mKind);
		dest.writeString(mLocalPath);
		dest.writeString(mOrgPath);
		dest.writeString(mOrgSize);
		dest.writeInt(mAngle);
		dest.writeInt(mThumbAngle);
		dest.writeString(mTrimPos);
		dest.writeString(mEndPos);
		dest.writeString(mWidth);
		dest.writeString(mHeight);
		dest.writeString(mX);
		dest.writeString(mY);
		dest.writeString(mOffsetX);
		dest.writeString(mOffsetY);
		dest.writeString(mScale);
		dest.writeString(mProdCode);
		dest.writeString(mImgYear);
		dest.writeString(mImgSeq);
		dest.writeString(mUploadPath);
		dest.writeString(mThumImgPath);
		dest.writeString(mSell_price);
		dest.writeString(mUnit_price);
		dest.writeInt(mOrientation);

	}

	private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read
											// 하는 순서와 같아야 함. !!

		mAutoBright = in.readString();
		mRecomment = in.readString();
		mPaperMatch = in.readString();
		mGlossy = in.readString();
		mAdjustBright = in.readString();
		mDateApplyAll = in.readString();
		mEditWH = in.readString();
		mRegDate = in.readString();
		mOrderCount = in.readString();
		mProdID = in.readString();
		mKind = in.readInt();
		mLocalPath = in.readString();
		mOrgPath = in.readString();
		mOrgSize = in.readString();
		mAngle = in.readInt();
		mThumbAngle = in.readInt();
		mTrimPos = in.readString();
		mEndPos = in.readString();
		mWidth = in.readString();
		mHeight = in.readString();
		mX = in.readString();
		mY = in.readString();
		mOffsetX = in.readString();
		mOffsetY = in.readString();
		mScale = in.readString();
		mProdCode = in.readString();
		mImgYear = in.readString();
		mImgSeq = in.readString();
		mUploadPath = in.readString();
		mThumImgPath = in.readString();
		mSell_price = in.readString();
		mUnit_price = in.readString();
		mOrientation = in.readInt();
	}

	public SnapsPhotoPrintItem() {
		// TODO Auto-generated constructor stub

	}

	// !! Parcel 객체에 write 하는 순서는 read
	// 하는 순서와 같아야 함. !!
	public SnapsPhotoPrintItem(Parcel in) {
		readFromParcel(in);

	}

	public static final Parcelable.Creator<SnapsPhotoPrintItem> CREATOR = new Parcelable.Creator<SnapsPhotoPrintItem>() {
		@Override
		public SnapsPhotoPrintItem createFromParcel(Parcel source) {
			return new SnapsPhotoPrintItem(source);
		}

		@Override
		public SnapsPhotoPrintItem[] newArray(int size) {
			return new SnapsPhotoPrintItem[size];
		}
	};

	public void makeItemAuraOrderXML(SnapsXML xml) {
		xml.startTag(null, "item");
		xml.attribute(null, "file", StringUtil.convertEmojiUniCodeToAlias(mUploadPath));
		xml.attribute(null, "prod_code", mProdCode);
		xml.attribute(null, "prod_name", mProdID);
		xml.attribute(null, "prod_real_name", mProdID);
		xml.attribute(null, "prod_nick_name", mProdID);

		xml.attribute(null, "glss_type", mGlossy); // 유광/무광

		xml.attribute(null, "edge_type", "noedge");
		xml.attribute(null, "pool_type", "paper");
		xml.attribute(null, "brht_type", "yes");
		xml.attribute(null, "show_date", "no");
		xml.attribute(null, "rcmm_yorn", "yes");
		xml.attribute(null, "prnt_cnt", mOrderCount);
		xml.attribute(null, "sell_price", mSell_price);
		xml.attribute(null, "imgYear", mImgYear);
		xml.attribute(null, "imgSeq", mImgSeq);
		xml.attribute(null, "x", mX);
		xml.attribute(null, "y", mY);
		xml.attribute(null, "width", mWidth);
		xml.attribute(null, "height", mHeight);
		xml.attribute(null, "uploadType", isFaceBookImage() ? "url" : "file");
		xml.attribute(null, "uploadURL", StringUtil.convertEmojiUniCodeToAlias(mOrgPath));
		xml.attribute(null, "localURL", StringUtil.convertEmojiUniCodeToAlias(mLocalPath));
		xml.startTag(null, "editinfo");
		xml.attribute(null, "orientation", Integer.toString(mOrientation));
		xml.attribute(null, "scale", mScale);
		xml.attribute(null, "editWidth", "550");
		xml.attribute(null, "editHeight", "550");
		xml.endTag(null, "editinfo");
		xml.endTag(null, "item");
	}

	public void makeItemSaveXml(SnapsXML xml) {

		xml.startTag(null, "photo");

		xml.attribute(null, "localPath", StringUtil.convertEmojiUniCodeToAlias(mLocalPath));
		xml.attribute(null, "orgPath", StringUtil.convertEmojiUniCodeToAlias(mOrgPath));
		xml.attribute(null, "orgSize", mOrgSize);

		if (mGlossy.equals("TRUE"))
			xml.attribute(null, "glossy", "glossy");
		else
			xml.attribute(null, "glossy", mGlossy);

		xml.attribute(null, "paperMatch", "TRUE");
		xml.attribute(null, "autoBright", "TRUE");
		xml.attribute(null, "recommend", "TRUE");
		xml.attribute(null, "orderCount", mOrderCount);
		xml.attribute(null, "angle", Integer.toString(mAngle));
		int orientation = (mAngle == 90 || mAngle == 270) ? 1 : 0;
		xml.attribute(null, "thumbAngle", Integer.toString(mThumbAngle));
		xml.attribute(null, "orientation", Integer.toString(orientation));
		xml.attribute(null, "trimPos", mTrimPos);
		xml.attribute(null, "endPos", mEndPos);
		xml.attribute(null, "imgYear", mImgYear);
		xml.attribute(null, "imgSeq", mImgSeq);
		xml.attribute(null, "uploadPath", StringUtil.convertEmojiUniCodeToAlias(mUploadPath));
		xml.attribute(null, "offsetX", mOffsetX);
		xml.attribute(null, "offsetY", mOffsetY);
		xml.attribute(null, "width", mWidth);
		xml.attribute(null, "height", mHeight);
		xml.attribute(null, "thumbImgPath", StringUtil.convertEmojiUniCodeToAlias(mThumImgPath));

		xml.endTag(null, "photo");

	}

	public void makeItemImgOption(SnapsXML xml, String prjCode) {

		xml.startTag(null, "ImageOrderInfo");
		xml.addTag(null, "F_ORDR_CODE", "DDC1003587");
		xml.addTag(null, "F_ALBM_ID", prjCode);
		xml.addTag(null, "F_PROD_CODE", mProdCode);
		xml.addTag(null, "F_IMGX_YEAR", mImgYear);
		xml.addTag(null, "F_IMGX_SQNC", mImgSeq);
		xml.addTag(null, "F_PRNT_CNT", mOrderCount);

		if (mGlossy.equals("glossy") || mGlossy.equals("TRUE"))
			xml.addTag(null, "F_GLSS_TYPE", "G");
		else
			xml.addTag(null, "F_GLSS_TYPE", "M");

		xml.addTag(null, "F_EDGE_TYPE", "X");
		xml.addTag(null, "F_POOL_TYPE", "P");
		xml.addTag(null, "F_BRHT_TYPE", "Y");
		xml.addTag(null, "F_SHOW_DATE", "N");
		xml.addTag(null, "F_RCMM_YORN", "N");
		xml.addTag(null, "F_UNIT_COST", mUnit_price);
		mSell_price = String.valueOf(Integer.parseInt(mUnit_price)
				* Integer.parseInt(mOrderCount));
		xml.addTag(null, "F_SELL_PRICE", mSell_price);
		xml.startTag(null, "F_TRIM_CORD");
		xml.endTag(null, "F_TRIM_CORD");

		xml.endTag(null, "ImageOrderInfo");

	}

	public void setSellPrice(String price) {
		mSell_price = StringUtil.getOnlyNumberString(price);
	}

	public String getSellPrice() {
		return mSell_price;
	}

	public void setUnitPrice(String price) {
		mUnit_price = StringUtil.getOnlyNumberString(price);
	}

	public String getUnitPrice() {
		return mUnit_price;
	}

	public boolean isFaceBookImage() {
		return mOrgPath.startsWith("http");
	}

	/***
	 * 카카오스토리 이미지 인경우에만 처리함..
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	boolean reCalculate(int width, int height) {
		String[] imgSize = mOrgSize.split(" ");

		if (width == Integer.parseInt(imgSize[0])
				&& height == Integer.parseInt(imgSize[1])) {
			return false;
		} else {

			float ratio = width / Float.parseFloat(imgSize[0]);

			mWidth = String.valueOf((int) (ratio * Integer.parseInt(mWidth)));
			mHeight = String.valueOf((int) (ratio * Integer.parseInt(mHeight)));
			mX = String.valueOf((int) (ratio * Integer.parseInt(mX)));
			mY = String.valueOf((int) (ratio * Integer.parseInt(mY)));
			mOrgSize = width + " " + height;
			return true;
		}

	}

}
