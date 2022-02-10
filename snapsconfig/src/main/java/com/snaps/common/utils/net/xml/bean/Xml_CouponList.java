package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Xml_CouponList {

	public List<CouponData> couponList = new ArrayList<Xml_CouponList.CouponData>();

	public static class CouponData implements Parcelable {
		/** 쿠폰명 **/
		public String F_ISSUE_CAUSE;
		/** 쿠폰코드 **/
		public String F_DOC_CODE;
		/** 쿠폰발행일 **/
		public String F_ISSUE_DATE;
		/** 쿠폰만기일 **/
		public String F_EXPR_DATE;
		/** 주문금액 **/
		public String F_ORDR_PRICE;

		/** 할인후 금액 **/
		public String F_REMAIN_PRICE;
		/** 할인금액 **/
		public String F_DISC_PRICE;
		
		public boolean isUse = false;
		public boolean F_USE_YORN = false;
		
		public String F_CHNL_CODE;

		public CouponData(String f_ISSUE_CAUSE, String f_DOC_CODE,
				String f_ISSUE_DATE, String f_EXPR_DATE, String f_ORDR_PRICE,
				String f_REMAIN_PRICE, String f_DISC_PRICE, boolean isPossible , String f_CH_CODE) {
			super();
			F_ISSUE_CAUSE = f_ISSUE_CAUSE;
			F_DOC_CODE = f_DOC_CODE;
			F_ISSUE_DATE = f_ISSUE_DATE;
			F_EXPR_DATE = f_EXPR_DATE;
			F_ORDR_PRICE = f_ORDR_PRICE;
			F_REMAIN_PRICE = f_REMAIN_PRICE;
			F_DISC_PRICE = f_DISC_PRICE;
			this.F_USE_YORN = isPossible;
			F_CHNL_CODE = f_CH_CODE;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
			dest.writeString(F_ISSUE_CAUSE);
			dest.writeString(F_DOC_CODE);
			dest.writeString(F_ISSUE_DATE);
			dest.writeString(F_EXPR_DATE);
			dest.writeString(F_ORDR_PRICE);
			dest.writeString(F_REMAIN_PRICE);
			dest.writeString(F_DISC_PRICE);
			dest.writeString(F_CHNL_CODE);
			
		}
		private void readFromParcel(Parcel in){// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
			F_ISSUE_CAUSE = in.readString();
			F_DOC_CODE = in.readString();
			F_ISSUE_DATE = in.readString();
			F_EXPR_DATE = in.readString();
			F_ORDR_PRICE = in.readString();
			F_REMAIN_PRICE = in.readString();
			F_DISC_PRICE = in.readString();
			F_CHNL_CODE = in.readString();
		}
		
		public CouponData(Parcel in) {
			readFromParcel(in);
		}
		    
		@Override
		public int describeContents() {
			return 0;
		}
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	        public CouponData createFromParcel(Parcel in) {
	             return new CouponData(in);
	       }
	       public CouponData[] newArray(int size) {
	            return new CouponData[size];
	       }
	   };

	}
}
