package com.snaps.common.data.product;

import android.os.Parcel;
import android.os.Parcelable;

public class KakaoBookData implements Parcelable {
	/** 커버 제목 */
	public String kakaobookCoverTitle;
	/** 커버색상 Index */
	public int kakaobookCoverColorIdx = 0;
	/** 커버색상 Target */
	public String kakaobookCoverTarget = "";
	/** 커버색상 컬러값 */
	public String kakaobookCoverColor = "";
	/** 커버 배경 이미지 Url(템플릿이미지) */
	public String kakaobookCoverBgImgUrl = "";
	/** 커버 메인 이미지 Url(카스배경이미지) */
	public String kakaobookCoverMainImgUrl = "";
	
	/** 속지 이름(카스이름) */
	public String kakaobookTitleName = "";
	/** 속지 프로필 이미지 Url(카스프로필이미지) */
	public String kakaobookTitleProfileImgUrl = "";
	
	/** 폰트 컬러값 */
	public String kakaobookFontColor = "";
	
	public KakaoBookData() {}
	public KakaoBookData(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(kakaobookCoverTitle);
		dest.writeInt(kakaobookCoverColorIdx);
		dest.writeString(kakaobookCoverTarget);
		dest.writeString(kakaobookCoverColor);
		dest.writeString(kakaobookCoverBgImgUrl);
		dest.writeString(kakaobookCoverMainImgUrl);
		dest.writeString(kakaobookTitleName);	
		dest.writeString(kakaobookTitleProfileImgUrl);	
		dest.writeString(kakaobookFontColor);
	}
	
	private void readFromParcel(Parcel in){// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
		kakaobookCoverTitle = in.readString();
		kakaobookCoverColorIdx = in.readInt();
		kakaobookCoverTarget = in.readString();
		kakaobookCoverColor = in.readString();
		kakaobookCoverBgImgUrl = in.readString();
		kakaobookCoverMainImgUrl = in.readString();
		kakaobookTitleName = in.readString();
		kakaobookTitleProfileImgUrl = in.readString();
		kakaobookFontColor = in.readString();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public KakaoBookData createFromParcel(Parcel in) {
             return new KakaoBookData(in);
       }
       public KakaoBookData[] newArray(int size) {
            return new KakaoBookData[size];
       }
   };
}
