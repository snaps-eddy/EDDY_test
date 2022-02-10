package com.snaps.common.structure.photoprint;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.SnapsDelImage;
import com.snaps.common.structure.SnapsXML;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;
import com.snaps.mobile.order.order_v2.exceptions.SnapsIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;

public class SnapsPhotoPrintProject implements ImpUploadProject, Parcelable, Serializable {
	private static final String TAG = SnapsPhotoPrintProject.class.getSimpleName();
	/**
	 *
	 */
	private static final long serialVersionUID = 8624165714050829948L;
	// OrderAura xml header
	String mAorderCode = "DDC0903259"; // 생략 가능
	String mAapplicationName = "android_mobile";
	public String mApplicationVersion = "";

	// save xml header
	public String mItemID = "photo";
	public String mMaker = "snaps";
	public String mType = "photo";
	public String mCheck = "false";
	String mProjectCode = "";
	public String mSBasketVersion = "2.5.0.0";
	public String mEditDate = "";
	public String mViewRatio = "";

	// option.xml header
	public String mOstring = "";
	// 대표썸네일 Path
	public String mCartBitmapPath = "";

	public ArrayList<SnapsPhotoPrintItem> mData = new ArrayList<SnapsPhotoPrintItem>();

	// 진행 사항 설정.. 디바이스에서만 사용한 데이터들...
	int mStep = 0; // 단계설정 프로젝트 코드,작품썸네일,작품페이지,원본이미지,xml올리기
	int mSubStep = 0; // 서브단계설정 -1이면 완료단계..
	int mRetryCount = 0; // 재시도 횟수
	int mCancel = 0; // 취소여부.. 0:취소아님 1:취소
	// 추가..
	int mCompleteProgress = 0;

	public String mGlossy = "glossy";

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mAorderCode);
		dest.writeString(mAapplicationName);
		dest.writeString(mApplicationVersion);
		dest.writeString(mItemID);
		dest.writeString(mMaker);
		dest.writeString(mType);
		dest.writeString(mCheck);
		dest.writeString(mProjectCode);
		dest.writeString(mSBasketVersion);
		dest.writeString(mEditDate);
		dest.writeString(mViewRatio);

		dest.writeString(mOstring);

		dest.writeInt(mStep);
		dest.writeInt(mSubStep);
		dest.writeInt(mRetryCount);
		dest.writeInt(mCancel);
		dest.writeInt(mCompleteProgress);
		dest.writeString(mCartBitmapPath);

		dest.writeTypedList(mData);

	}

	public SnapsPhotoPrintProject() {

	}

	// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
	public SnapsPhotoPrintProject(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		mAorderCode = in.readString();
		mAapplicationName = in.readString();
		mApplicationVersion = in.readString();
		mItemID = in.readString();
		mMaker = in.readString();
		mType = in.readString();
		mCheck = in.readString();
		mProjectCode = in.readString();
		mSBasketVersion = in.readString();
		mEditDate = in.readString();
		mViewRatio = in.readString();
		mOstring = in.readString();

		mStep = in.readInt();
		mSubStep = in.readInt();
		mRetryCount = in.readInt();
		mCancel = in.readInt();
		mCompleteProgress = in.readInt();
		mCartBitmapPath = in.readString();

		mData = new ArrayList<SnapsPhotoPrintItem>();
		in.readTypedList(mData, SnapsPhotoPrintItem.CREATOR);
	}

	public static final Parcelable.Creator<SnapsPhotoPrintProject> CREATOR = new Parcelable.Creator<SnapsPhotoPrintProject>() {
		@Override
		public SnapsPhotoPrintProject createFromParcel(Parcel source) {
			return new SnapsPhotoPrintProject(source);
		}

		@Override
		public SnapsPhotoPrintProject[] newArray(int size) {
			return new SnapsPhotoPrintProject[size];
		}
	};

	@Override
	public void setProjectCode(String prjCode) {
		this.mProjectCode = prjCode;

	}

	@Override
	public String getProjectCode() {
		return mProjectCode;
	}

	@Override
	public void setItemImgSeqWithImageId(int idx, SnapsDelImage data) {
//		if (mData.size() > idx) {
//			SnapsPhotoPrintItem item = mData.get(idx);
//			item.mImgSeq = data.imgSeq;
//			item.mImgYear = data.imgYear;
//			// 로컬 이미지 인경우만 설
//			if (!item.isFaceBookImage()) {
//				item.mUploadPath = data.uploadPath;
//				item.mOrgSize = data.sizeOrgImg;
//				item.mThumImgPath = data.tinyPath;
//				item.mOrgPath = data.oriPath;
//			}
//		}
	}

	@Override
	public void setApplicationVersion(String version) {
		mApplicationVersion = version;
	}

	@Override
	public String getCartThumbnail() {
		return mCartBitmapPath;
	}

	@Override
	public ArrayList<String> getWorkThumbnails() {
		ArrayList<String> ret = new ArrayList<String>();
		for (SnapsPhotoPrintItem item : mData) {
			ret.add(item.mThumImgPath);
		}

		return ret;
	}

	@Override
	public String getOriginalPathWithIndex(int index) {
		return mData.get(index).mOrgPath;
	}

	@Override
	public SnapsXmlMakeResult makeSaveXML(String filePath) {
		SnapsLogger.appendOrderLog("start photoPrint makeSaveXML");

		File saveFile = null;
		if (filePath == null) {
			try {
				saveFile = Config.getPROJECT_FILE(Config.SAVE_XML_FILE_NAME);
				if (saveFile == null) throw new SnapsIOException("failed make file");
			} catch (Exception e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("Exception photoprint makeSaveXML createFile " + e.toString());
				return null;
			}
		} else {
			saveFile = new File(filePath);
		}

		if (!saveFile.exists()) {
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("Exception photoprint makeSaveXML IOException " + e.toString());
				return null;
			}
		}

		FileOutputStream fileStream = null;

		try {
			fileStream = new FileOutputStream(saveFile);
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("Exception photoprint makeSaveXML FileNotFound " + e.toString());
			return null;
		}

		try {

			String currentDate = "";
			if (mEditDate.equals(""))
				currentDate = StringUtil.getCurrentDate();

			SnapsXML xml = new SnapsXML(fileStream);
			xml.startTag(null, "basket");
			xml.attribute(null, "version", mSBasketVersion);
			xml.attribute(null, "projectCode", mProjectCode);

			xml.startTag(null, "item");
			xml.attribute(null, "id", "photo");
			xml.attribute(null, "maker", "snaps");
			xml.attribute(null, "editdate", currentDate);
			xml.attribute(null, "type", "photo");
			xml.attribute(null, "check", "FALSE");
			xml.attribute(null, "regDate", currentDate);

			xml.startTag(null, "photoOption");
			xml.attribute(null, "paperMatch", "TRUE");
			String printOption = "TRUE";
			if (mData != null && mData.size() > 0) {
				printOption = ((SnapsPhotoPrintItem) mData.get(0)).mGlossy;
				if (printOption.equals("matt"))
					printOption = "FALSE";

			}

			xml.attribute(null, "glossy", printOption);
			xml.attribute(null, "adjustbright", "TRUE");
			xml.attribute(null, "orderCount", "1");
			xml.attribute(null, "DateApplyAll", "FALSE");
			xml.endTag(null, "photoOption");

			xml.startTag(null, "scene");
			xml.attribute(null, "id", mData.get(0).mProdID);
			xml.attribute(null, "productCode", mData.get(0).mProdCode);
			xml.attribute(null, "unitCost", mData.get(0).mUnit_price);
			xml.attribute(null, "viewportRatio", mViewRatio);

			for (SnapsPhotoPrintItem item : mData) {
				item.makeItemSaveXml(xml);
			}

			xml.endTag(null, "scene");
			xml.endTag(null, "item");
			xml.endTag(null, "basket");
			xml.endDocument();

			fileStream.close();

		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("Exception photoprint makeSaveXML writeSaveXML " + e.toString());
			return null;
		}

		return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(saveFile).create();
	}

	@Override
	public SnapsXmlMakeResult makeAuraOrderXML(String filePath) {
		SnapsLogger.appendOrderLog("start photoPrint makeAuraOrderXML");
		File auraOrderFile = null;

		if (filePath == null) {
			try {
				auraOrderFile = Config.getPROJECT_FILE(Config.AURA_ORDER_XML_FILE_NAME);
				if (auraOrderFile == null) throw new SnapsIOException("failed make xml File");
			} catch (Exception e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("error occurred while createNewFile AuraOrderXmlFile : " + e.toString());
				return null;
			}
		} else {
			auraOrderFile = new File(filePath);
		}

		if (!auraOrderFile.exists()) {
			try {
				if (!auraOrderFile.createNewFile()) {
					SnapsLogger.appendOrderLog("failed create xml file no.1");
					return null;
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("exception SnapsPhotoPriintProject IOException. " + e.toString());
				return null;
			}
		}

		FileOutputStream fileStream = null;

		try {
			fileStream = new FileOutputStream(auraOrderFile);
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("exception SnapsPhotoPriintProject fileNotFountException. " + e.toString());
			return null;
		}

		try {
			SnapsXML xml = new SnapsXML(fileStream);
			xml.startTag(null, "Order");
			xml.attribute(null, "code", mAorderCode);
			xml.startTag(null, "Application");
			xml.attribute(null, "name", mAapplicationName);
			xml.attribute(null, "version", mApplicationVersion);
			xml.endTag(null, "Application");

			for (SnapsPhotoPrintItem item : mData) {
				item.makeItemAuraOrderXML(xml);
			}

			xml.endTag(null, "Order");
			xml.endDocument();

			fileStream.close();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("exception SnapsPhotoPriintProject while make xml. " + e.toString());
			return null;
		}

		return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(auraOrderFile).create();
	}

	@Override
	public SnapsXmlMakeResult makeOptionXML(String filePath) {
		SnapsLogger.appendOrderLog("start photoPrint makeOptionXML");
		File saveFile = null;

		if (filePath == null) {
			try {
				saveFile = Config.getPROJECT_FILE(Config.OPTION_XML_FILE_NAME);
				if (saveFile == null) throw new SnapsIOException("failed make xml");
			} catch (Exception e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("exception photoPrint makeOptionXML create File " + e.toString() );
				return null;
			}
		} else {
			saveFile = new File(filePath);
		}

		if (!saveFile.exists()) {
			try {
				if (!saveFile.createNewFile()) {
					return null;
				}
			} catch (IOException e) {
				Dlog.e(TAG, e);
				SnapsLogger.appendOrderLog("exception photoPrint makeOptionXML IOException " + e.toString() );
				return null;
			}
		}

		FileOutputStream fileStream = null;

		try {
			fileStream = new FileOutputStream(saveFile);
		} catch (FileNotFoundException e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("exception photoPrint makeOptionXML FileNotFound " + e.toString() );
			return null;
		}

		try {
			SnapsXML xml = new SnapsXML(fileStream);
			xml.startTag(null, "string");

			for (SnapsPhotoPrintItem item : mData) {
				item.makeItemImgOption(xml, mProjectCode);
			}

			xml.endTag(null, "string");
			xml.endDocument();

			fileStream.close();

		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendOrderLog("exception photoPrint makeOptionXML write xml " + e.toString() );
			return null;
		}

		return new SnapsXmlMakeResult.Builder().setSuccess(true).setXmlFile(saveFile).create();
	}

	@Override
	public int getItemCount() {

		return mData.size();
	}

	@Override
	public String getOrderCode() {

		return "146000";
	}

	/********************
	 * 진행 상태 저장 메소드 * 완료된 사항만 저장한다 *
	 ********************/

	@Override
	public void setProcessStep(int step, int subStep) {
		mStep = step;
		mSubStep = subStep;
		// 원본사진을 올리는 경우 완료 카운트 증가.
		if (step == 3 && subStep > 0) {
			mCompleteProgress = subStep;
		}
	}

	@Override
	public int getRetryCount() {
		return mRetryCount;
	}

	@Override
	public void setRetryCount(int count) {
		if (count == -1) {
			mRetryCount = 0;
			return;
		}

		mRetryCount += count;

	}

	@Override
	public boolean isFacebookImage(int index) {
		return ((SnapsPhotoPrintItem) mData.get(index)).isFaceBookImage();

	}

	@Override
	public int getProcessStep() {

		return mStep;
	}

	@Override
	public int getProcessSubStep() {
		// TODO Auto-generated method stub
		return mSubStep;
	}

	@Override
	public int getCancel() {
		// TODO Auto-generated method stub
		return mCancel;
	}

	@Override
	public int getUploadComleteCount() {

		return mCompleteProgress;
	}

	/***
	 * 사진인화 유광 무광 설정
	 *
	 * @param type
	 */
	public void setGlssType(String type) {
		mGlossy = type;
		for (SnapsPhotoPrintItem item : mData) {
			item.mGlossy = type;
		}
	}

	@Override
	public int getImageKindWithIndex(int index) {
		return ((SnapsPhotoPrintItem) mData.get(index)).mKind;
	}

	@Override
	public boolean chagneImageSize(int index, int width, int height) {
		SnapsPhotoPrintItem item = (SnapsPhotoPrintItem) mData.get(index);
		return item.reCalculate(width, height);
	}

	@Override
	public boolean removeImageDataWithImageId(int index) throws Exception {
		return false;
	}

	@Override
	public PhotoPrintData getPhotoPrintDataWithImageId(int index) {
		return null;
	}

	@Override
	public PhotoPrintData getPhotoPrintDataWithIndex(int index) {
		return null;
	}
}
