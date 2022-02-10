package com.snaps.common.structure;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

public class SnapsTemplatePrice implements Parcelable, Serializable{
	private static final String TAG = SnapsTemplatePrice.class.getSimpleName();
	/**
	 * 
	 */
	private static final long serialVersionUID = -5055005531361603225L;
	
	public String F_COMP_CODE = "";
	public String F_PROD_CODE = "";
	public String F_TMPL_CODE = "";
	public String F_SELL_PRICE = "0";
	public String F_ORG_PRICE = "0";
	public String F_PRICE_NUM = "";
	public String F_PRNT_BQTY = "";
	public String F_PRNT_EQTY = "";
	public String F_DISC_RATE = "";
	public String F_PAGE_ADD_PRICE = "0";
	public String F_ORG_PAGE_ADD_PRICE = "0";

	public SnapsXML getSaveXML(SnapsXML xml) {
		try {
			xml.startTag(null, "TmplPrice");

			xml.addTag(null, "F_COMP_CODE", F_COMP_CODE);
			xml.addTag(null, "F_PROD_CODE", F_PROD_CODE);
			xml.addTag(null, "F_TMPL_CODE", F_TMPL_CODE);
			xml.addTag(null, "F_SELL_PRICE", F_SELL_PRICE);
			xml.addTag(null, "F_ORG_PRICE", F_ORG_PRICE);
			xml.addTag(null, "F_PRICE_NUM", F_PRICE_NUM);
			xml.addTag(null, "F_PRNT_BQTY", F_PRNT_BQTY);
			xml.addTag(null, "F_PRNT_EQTY", F_PRNT_EQTY);
			xml.addTag(null, "F_DISC_RATE", F_DISC_RATE);
			xml.addTag(null, "F_PAGE_ADD_PRICE", F_PAGE_ADD_PRICE);
			xml.addTag(null, "F_ORG_PAGE_ADD_PRICE", F_ORG_PAGE_ADD_PRICE);

			xml.endTag(null, "TmplPrice");

		} catch (Exception e) {
			Dlog.e(TAG, e);
		}

		return xml;
	}
	
	public SnapsTemplatePrice() {}
	
	public SnapsTemplatePrice(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(F_COMP_CODE);
		dest.writeString(F_PROD_CODE);
		dest.writeString(F_TMPL_CODE);
		dest.writeString(F_SELL_PRICE);
		dest.writeString(F_ORG_PRICE);
		dest.writeString(F_PRICE_NUM);
		dest.writeString(F_PRNT_BQTY);
		dest.writeString(F_PRNT_EQTY);
		dest.writeString(F_DISC_RATE);
		dest.writeString(F_PAGE_ADD_PRICE);
		dest.writeString(F_ORG_PAGE_ADD_PRICE);
	}
	
	private void readFromParcel(Parcel in) {
		F_COMP_CODE = in.readString();
		F_PROD_CODE = in.readString();
		F_TMPL_CODE = in.readString();
		F_SELL_PRICE = in.readString();
		F_ORG_PRICE = in.readString();
		F_PRICE_NUM = in.readString();
		F_PRNT_BQTY = in.readString();
		F_PRNT_EQTY = in.readString();
		F_DISC_RATE = in.readString();
		F_PAGE_ADD_PRICE = in.readString();
		F_ORG_PAGE_ADD_PRICE = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

		@Override
		public SnapsTemplatePrice createFromParcel(Parcel in) {
			return new SnapsTemplatePrice(in);
		}

		@Override
		public SnapsTemplatePrice[] newArray(int size) {
			return new SnapsTemplatePrice[size];
		}
	};
}
