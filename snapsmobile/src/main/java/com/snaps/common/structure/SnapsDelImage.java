package com.snaps.common.structure;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;

import java.io.Serializable;

import errorhandle.logger.Logg;

public class SnapsDelImage implements Parcelable, Serializable {
	private static final String TAG = SnapsDelImage.class.getSimpleName();
	private static final long serialVersionUID = 6330733529713222150L;
	// 프로젝트에 저장된 로컬이미지.
	/** 업로드 이미지 Year Key **/
	public String imgYear = "";
	/** 업로드 이미지 Seq Key **/
	public String imgSeq = "";
	/** 업로드 이미지 Path **/
	public String uploadPath = "";
	/** 업로드 이미지 tiny Path **/
	public String tinyPath = "";
	/** 업로드 이미지 org Path **/
	public String oriPath = "";
	/** 업로드 이미지 org Size **/
	public String sizeOrgImg = "";
	/** 업로드 이미지 real name **/
	public String realFileName = "";
	/** 업로드 이미지 shoot date **/
	public String shootDate = "";
	/** 업로드 이미지 tinyPath **/
	public String usedImgCnt = "";
	// 썸내일 url
	public String thumbNailUrl = "";

	public void getSaveXML(SnapsSaveXML xml) {
		try {
			xml.startTag(null, "del_image");
			xml.attribute(null, "imgYear", this.imgYear);
			xml.attribute(null, "imgSeq", this.imgSeq);
			xml.attribute(null, "uploadPath", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(this.uploadPath)));
			xml.attribute(null, "tinyPath", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(this.tinyPath)));
			xml.attribute(null, "oriPath", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(this.oriPath)));
			xml.attribute(null, "sizeOrgImg", this.sizeOrgImg);

			//bug fix : 2020/04/02 ben
            //특이한 파일 이름때문에 서버에서 save.xml 파싱하다가 오류남   --> https://www.google.co.kr/search?hl=ko&ei=_UGFXsLqHcTj-AbN2Iy4Ag&q=%D2%93%E1%B4%80%E1%B4%9B%E1%B4%87%F0%93%8D%AF%E2%81%B7&oq=%D2%93%E1%B4%80%E1%B4%9B%E1%B4%87%F0%93%8D%AF%E2%81%B7&gs_lcp=CgZwc3ktYWIQA1Cz-gFYwJgCYJ6bAmgAcAB4AIABygGIAZMSkgEGNy4xNC4xmAEAoAEBqgEHZ3dzLXdpeg&sclient=psy-ab&ved=0ahUKEwiC3bjRzcjoAhXEMd4KHU0sAycQ4dUDCAo&uact=5
            //서버에서 익셉션이 발생하면 서버 에러 페이지로 이동하는데 해당 에러 페이지가 없어서 404발생.......
			//xml.attribute(null, "realFileName", StringUtil.removeInvalidXMLChar(StringUtil.convertEmojiUniCodeToAlias(this.realFileName)));
			xml.attribute(null, "realFileName", "");

			xml.attribute(null, "shootDate", this.shootDate);
			xml.attribute(null, "usedImgCnt", this.usedImgCnt);
			xml.endTag(null, "del_image");
		} catch (Exception e) {
			Dlog.e(TAG, "DelImage Error : " + realFileName + " -> " + StringUtil.convertEmojiUniCodeToAlias(this.realFileName), e);
		}
	}
	
	public SnapsDelImage() {}
	
	public SnapsDelImage(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(imgYear);
		dest.writeString(imgSeq);
		dest.writeString(uploadPath);
		dest.writeString(tinyPath);
		dest.writeString(oriPath);
		dest.writeString(sizeOrgImg);
		dest.writeString(realFileName);
		dest.writeString(shootDate);
		dest.writeString(usedImgCnt);
		dest.writeString(thumbNailUrl);
	}
	
	private void readFromParcel(Parcel in) {
		imgYear = in.readString();
		imgSeq = in.readString();
		uploadPath = in.readString();
		tinyPath = in.readString();
		oriPath = in.readString();
		sizeOrgImg = in.readString();
		realFileName = in.readString();
		shootDate = in.readString();
		usedImgCnt = in.readString();
		thumbNailUrl = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsDelImage createFromParcel(Parcel in) {
			return new SnapsDelImage(in);
		}

		@Override
		public SnapsDelImage[] newArray(int size) {
			return new SnapsDelImage[size];
		}
	};
}
