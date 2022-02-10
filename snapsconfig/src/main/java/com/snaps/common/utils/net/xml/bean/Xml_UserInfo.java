package com.snaps.common.utils.net.xml.bean;

import android.os.Parcel;
import android.os.Parcelable;


public class Xml_UserInfo {
	public static class UserInfoData implements Parcelable {
		public String F_USER_NO;
		public String F_USER_ID;
		public String F_USER_NAME;
		public String F_USER_LVL_NAME;
		public String F_COUPON_DESC;
		public String F_USER_LVL;
		
		public UserInfoData(String F_USER_NO, String F_USER_ID, String F_USER_NAME, String F_USER_LVL_NAME, String F_COUPON_DESC, String F_USER_LVL) {
			this.F_USER_NO = F_USER_NO;
			this.F_USER_ID = F_USER_ID;
			this.F_USER_NAME = F_USER_NAME;
			this.F_USER_LVL_NAME = F_USER_LVL_NAME;
			this.F_COUPON_DESC = F_COUPON_DESC;
			this.F_USER_LVL = F_USER_LVL;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
			dest.writeString(F_USER_NO);
			dest.writeString(F_USER_ID);
			dest.writeString(F_USER_NAME);
			dest.writeString(F_USER_LVL_NAME);
			dest.writeString(F_COUPON_DESC);
			dest.writeString(F_USER_LVL);
		}
		private void readFromParcel(Parcel in){// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
			F_USER_NO = in.readString();
			F_USER_ID = in.readString();
			F_USER_NAME = in.readString();
			F_USER_LVL_NAME = in.readString();
			F_COUPON_DESC = in.readString();
			F_USER_LVL = in.readString();
		}
		
		public UserInfoData(Parcel in) {
			readFromParcel(in);
		}
		    
		@Override
		public int describeContents() {
			return 0;
		}
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	        public UserInfoData createFromParcel(Parcel in) {
	             return new UserInfoData(in);
	       }
	       public UserInfoData[] newArray(int size) {
	            return new UserInfoData[size];
	       }
	   };
	}
}
