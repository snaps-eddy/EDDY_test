package com.snaps.mobile.autosave;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;

public class AutoSaveProjectInfo implements IAutoSaveConstants, Parcelable, Serializable {

	private static final long serialVersionUID = 5479195235343383503L;

	private int productType = 0;

	private String projCode = "";
	private String prodCode = "";
	private String paperCode = "";
	private String frameType = "";
	private String frameId = "";
	private String notePaperCode = "";
	private String glossyType = "";
	private String projName = "";
	private String tmplCode = "";
	private String tmplCover = "";
	private String tmplCoverTitle = "";
	private String userCoverColor = "";
	private String cardQuantity = "";
	private String designId = "";
	private String backType = "";

	//달력용..
	private int nOldYear = 0;
	private int nOldMonth = 0;

	private String summaryTaget = "";
	private String summaryWidth = "";
	private String summaryHeight = "";

	private ArrayList<SnapsLayoutControl> summaryLayer = null;

	private boolean isFromCart = false;

	public AutoSaveProjectInfo() {
	}

	public boolean isValidSaveInfo() {
		return !StringUtil.isEmpty(projCode) && !StringUtil.isEmpty(prodCode);
	}

	protected AutoSaveProjectInfo(Parcel in) {
		productType = in.readInt();
		projCode = in.readString();
		prodCode = in.readString();
		paperCode = in.readString();
		frameType = in.readString();
		frameId = in.readString();
		notePaperCode = in.readString();
		glossyType = in.readString();
		projName = in.readString();
		tmplCode = in.readString();
		tmplCover = in.readString();
		tmplCoverTitle = in.readString();
		userCoverColor = in.readString();
		cardQuantity = in.readString();
		designId = in.readString();
		nOldYear = in.readInt();
		nOldMonth = in.readInt();
		summaryTaget = in.readString();
		summaryWidth = in.readString();
		summaryHeight = in.readString();
		summaryLayer = in.createTypedArrayList(SnapsLayoutControl.CREATOR);
		isFromCart = in.readByte() != 0;
		backType = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(productType);
		dest.writeString(projCode);
		dest.writeString(prodCode);
		dest.writeString(paperCode);
		dest.writeString(frameType);
		dest.writeString(frameId);
		dest.writeString(notePaperCode);
		dest.writeString(glossyType);
		dest.writeString(projName);
		dest.writeString(tmplCode);
		dest.writeString(tmplCover);
		dest.writeString(tmplCoverTitle);
		dest.writeString(userCoverColor);
		dest.writeString(cardQuantity);
		dest.writeString(designId);
		dest.writeInt(nOldYear);
		dest.writeInt(nOldMonth);
		dest.writeString(summaryTaget);
		dest.writeString(summaryWidth);
		dest.writeString(summaryHeight);
		dest.writeTypedList(summaryLayer);
		dest.writeByte((byte) (isFromCart ? 1 : 0));
		dest.writeString(backType);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<AutoSaveProjectInfo> CREATOR = new Creator<AutoSaveProjectInfo>() {
		@Override
		public AutoSaveProjectInfo createFromParcel(Parcel in) {
			return new AutoSaveProjectInfo(in);
		}

		@Override
		public AutoSaveProjectInfo[] newArray(int size) {
			return new AutoSaveProjectInfo[size];
		}
	};

	public void setConfigInfo() {
		setProjName(Config.getPROJ_NAME());
		setProjCode(Config.getPROJ_CODE());
		setProdCode(Config.getPROD_CODE());
		setPaperCode(Config.getPAPER_CODE());
		setFrameType(Config.getFRAME_TYPE());
		setFrameId(Config.getFRAME_ID());
		setNotePaperCode(Config.getNOTE_PAPER_CODE());
		setGlossyType(Config.getGLOSSY_TYPE());
		setTmplCode(Config.getTMPL_CODE());
		setTmplCover(Config.getTMPL_COVER());
		setTmplCoverTitle(Config.getTMPL_COVER_TITLE());
		setUserCoverColor(Config.getUSER_COVER_COLOR());
		setCardQuantity(Config.getCARD_QUANTITY());
		setDESIGN_ID(Config.getDesignId());
		setFromCart(Config.isFromCart());
		setBackType(Config.getBACK_TYPE());
	}

	public String getUserCoverColor() {
		return userCoverColor;
	}

	public void setUserCoverColor(String userCoverColor) {
		this.userCoverColor = userCoverColor;
	}

	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}

	public String getTmplCode() {
		return tmplCode;
	}

	public void setTmplCode(String tmplCode) {
		this.tmplCode = tmplCode;
	}

	public String getTmplCover() {
		return tmplCover;
	}

	public void setTmplCover(String tmplCover) {
		this.tmplCover = tmplCover;
	}

	public String getTmplCoverTitle() {
		return tmplCoverTitle;
	}

	public void setTmplCoverTitle(String tmplCoverTitle) {
		this.tmplCoverTitle = tmplCoverTitle;
	}

	public String getProjCode() {
		return projCode;
	}

	public void setProjCode(String projCode) {
		this.projCode = projCode;
	}

	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	public String getPaperCode() {
		return paperCode;
	}

	public void setPaperCode(String paperCode) {
		this.paperCode = paperCode;
	}

	public String getFrameType() {
		return frameType;
	}

	public void setFrameType(String frameType) {
		this.frameType = frameType;
	}

	public String getFrameId() {
		return frameId;
	}

	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}

	public String getNotePaperCode() {
		return notePaperCode;
	}

	public void setNotePaperCode(String notePaperCode) {
		this.notePaperCode = notePaperCode;
	}

	public String getGlossyType() {
		return glossyType;
	}

	public void setGlossyType(String glossyType) {
		this.glossyType = glossyType;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public int getnOldYear() {
		return nOldYear;
	}

	public void setnOldYear(int nOldYear) {
		this.nOldYear = nOldYear;
	}

	public int getnOldMonth() {
		return nOldMonth;
	}

	public void setnOldMonth(int nOldMonth) {
		this.nOldMonth = nOldMonth;
	}

	public String getSummaryTaget() {
		return summaryTaget;
	}

	public void setSummaryTaget(String summaryTaget) {
		this.summaryTaget = summaryTaget;
	}

	public String getSummaryWidth() {
		return summaryWidth;
	}

	public void setSummaryWidth(String summaryWidth) {
		this.summaryWidth = summaryWidth;
	}

	public String getSummaryHeight() {
		return summaryHeight;
	}

	public void setSummaryHeight(String summaryHeight) {
		this.summaryHeight = summaryHeight;
	}

	public ArrayList<SnapsLayoutControl> getSummaryLayer() {
		return summaryLayer;
	}

	public void setSummaryLayer(ArrayList<SnapsLayoutControl> summaryLayer) {
		this.summaryLayer = summaryLayer;
	}

	public String getCardQuantity() {
		return cardQuantity;
	}

	public void setCardQuantity(String cardQuantity) {
		this.cardQuantity = cardQuantity;
	}

	public void setDESIGN_ID(String design_id) {
		this.designId = design_id;
	}

	public String getDESIGN_ID() {
		return designId;
	}

	public boolean isFromCart() {
		return isFromCart;
	}

	public void setFromCart(boolean fromCart) {
		isFromCart = fromCart;
	}

	public String getBackType() {
		return backType;
	}

	public void setBackType(String backType) {
		this.backType = backType;
	}
}
