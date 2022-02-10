package com.snaps.common.utils.net.xml.bean;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Xml_Delivery {
	public List<Xml_Delivery.DeliverInfo> mDeliveryList = new ArrayList<Xml_Delivery.DeliverInfo>();

	public static class DeliverInfo implements Parcelable {
		public String F_DLVR_CODE = "";
		public String F_POST_PRICE = "";

		public DeliverInfo(String F_DLVR_CODE, String F_POST_PRICE) {
			super();
			this.F_DLVR_CODE = F_DLVR_CODE;
			this.F_POST_PRICE = F_POST_PRICE;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {// !! Parcel 객체에
															// write 하는 순서는 read
															// 하는 순서와 같아야 함. !!
			dest.writeString(F_DLVR_CODE);
			dest.writeString(F_POST_PRICE);

		}
		private void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는
												// read 하는 순서와 같아야 함. !!
			F_DLVR_CODE = in.readString();
			F_POST_PRICE = in.readString();

		}

		public DeliverInfo(Parcel in) {
			readFromParcel(in);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
			public DeliverInfo createFromParcel(Parcel in) {
				return new DeliverInfo(in);
			}
			public DeliverInfo[] newArray(int size) {
				return new DeliverInfo[size];
			}
		};

	}

	/***
	 * 택배 배송 금액 가져오기...
	 * 
	 * @return
	 */
	public int getPacelServicePrice() {
		for (DeliverInfo info : mDeliveryList) {
			if (info.F_DLVR_CODE.equals("011005"))
				return Integer.parseInt(info.F_POST_PRICE);
		}

		return 0;
	}

	/***
	 * 일반 우편 배송 금액 가져오기..
	 * 
	 * @return
	 */
	public int getRegularMailPrice() {
		for (DeliverInfo info : mDeliveryList) {
			if (info.F_DLVR_CODE.equals("011001"))
				return Integer.parseInt(info.F_POST_PRICE);
		}
		return 0;
	}

	/***
	 * 택배와 일반우편 금액을 비교하여 작은 금액을 가져온다.
	 * 
	 * @return
	 */
	public int getDeliveryPrice() {
		int a = getPacelServicePrice();
		int b = getRegularMailPrice();

		if (b > 10 && a > b) {
			return b;
		} else {
			return a;
		}
	}

}
